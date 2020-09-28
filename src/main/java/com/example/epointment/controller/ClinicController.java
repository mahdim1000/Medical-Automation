package com.example.epointment.controller;

import com.example.epointment.common.*;
import com.example.epointment.model.*;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/clinic")
@Secured("ROLE_CLINIC")
public class ClinicController {

    @Autowired
    ClinicDao clinicDao;
    @Autowired
    CityDao cityDao;
    @Autowired
    TurnDao turnDao;
    @Autowired
    ClinicOfficeDao clinicOfficeDao;
    @Autowired
    DoctorDao doctorDao;
    @Autowired
    DoctorServiceDao doctorServiceDao;
    @Autowired
    TurnServiceDao turnServiceDao;

    @PostMapping(value = "/getInfo")
    public HashMap<String, Object> getInfo(@RequestBody BaseJsonNode json) {
//        return doctorDao.getInfo();
        Clinic clinic = clinicDao.findByMelliCode(json.get("melliCode").asText());
        HashMap<String, Object> info = new HashMap<>();
        info.put("bio", clinic.getBio());
        info.put("name", clinic.getName());
        info.put("address", clinic.getAddress());
        info.put("city", clinic.getCity());
        info.put("clinicNumber", clinic.getClinicNumber());
        info.put("image", clinic.getImage());
        info.put("phone", clinic.getPhone());
        return info;
    }

    @PostMapping(value = "/getFullInfo")
    public HashMap<String, Object> getFullInfo(@RequestBody BaseJsonNode json){
        String melliCode = json.get("melliCode").asText();
        Clinic clinic = clinicDao.findByMelliCode(melliCode);

        HashMap<String, Object> h = new HashMap<>();
        h.put("name", clinic.getName());
        h.put("melliCode", clinic.getMelliCode());
        h.put("mobile", clinic.getPhone());
        h.put("city", clinic.getCity());
        h.put("address", clinic.getAddress());
        h.put("phone", clinic.getClinicPhone());
        h.put("image", clinic.getImage());
        h.put("bio", clinic.getBio());
        h.put("clinicNumber", clinic.getClinicNumber());

        return h;
    }

    @PostMapping(value="/updateProfile")
    public void updateProfile(@RequestBody BaseJsonNode json){
        Clinic clinic = clinicDao.findByMelliCode(json.get("melliCode").asText());
        clinic.setName(json.get("name").asText());
        clinic.setCity(cityDao.findById(json.get("city").get("id").asLong()));
        clinic.setAddress(json.get("address").asText());
        clinic.setClinicPhone(json.get("phone").asText());
        clinic.setBio(json.get("bio").asText());
        clinic.setClinicNumber(json.get("clinicNumber").asText());

        clinicDao.save(clinic);
    }

    @PostMapping(value="/updatePhone")
    public void updatePhone(@RequestBody BaseJsonNode json){
        String mc = json.get("melliCode").asText();
        String phone = json.get("phone").asText();
        Clinic c = clinicDao.findByMelliCode(mc);
        c.setPhone(phone);

        clinicDao.save(c);
    }

    @PostMapping(value="/getClinicTurns")
    public List<HashMap> getClinicTurns(@RequestBody BaseJsonNode json){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String mc = json.get("melliCode").asText();
        Clinic clinic = clinicDao.findByMelliCode(mc);
        Long cid = clinic.getId();
        List<Turn> turns = turnDao.findByClinicId(cid);
        List<HashMap> list = new ArrayList<>();
        for(Turn turn:turns){
            HashMap<String, Object> hm = new HashMap<>();
            String sDate = dateFormat.format(turn.getDate());
            String startTime = turn.getStartTime().toString();
            String endTime = turn.getEndTime().toString();

            hm.put("id", turn.getId());
            if(turn.getPatient() != null)
                hm.put("title", turn.getPatient().getName() + " " + turn.getPatient().getLastname());
            hm.put("start", sDate + " " + startTime);
            hm.put("end", sDate + " " + endTime);

            hm.put("textColor", "#fff");
            if(turn.getStatus() == 1){
                hm.put("backgroundColor", "#04B431");
            }else if(turn.getStatus() == 2){
                hm.put("backgroundColor", "#FF0000");
            }

            list.add(hm);
        }
        return list;
    }

    @PostMapping(value = "/getMyDoctors")
    public List<HashMap> getMyDoctors(@RequestBody BaseJsonNode json){
        String mc = json.get("melliCode").asText();
        Clinic clinic = clinicDao.findByMelliCode(mc);
        List<Doctor> doctors = clinicOfficeDao.findDoctorsByClinicId(clinic.getId());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -30);
        Date today30 = cal.getTime();


        List<HashMap> list = new ArrayList<>();
        for(Doctor d:doctors){
            List<String> services = new ArrayList<>();
            for(Service s:doctorServiceDao.findDoctorServices(d)){
                services.add(s.getName());
            }

            HashMap<String, Object> h = new HashMap<>();
            h.put("id", d.getId());
            h.put("name", d.getName()+" "+d.getLastname());
            h.put("skill", d.getEducation().getName()+" "+d.getExpertice().getName());
            h.put("melliCode", d.getMelliCode());
            h.put("image", d.getImage());
            h.put("services",services);
            h.put("phone", d.getPhone());
            h.put("doctorNumber", d.getDoctorCode());
            h.put("turnNum", turnDao.countDoctorOfClinicTurnBetweenDates(clinic.getId(), d.getId(), today,today30));
            h.put("income30", turnDao.doctorOfClinicLast30IncomeById(clinic.getId(), d.getId(), today, today30));
            h.put("bio", d.getBio());
            list.add(h);
        }
        return list;
    }

//    @PostMapping(value = "/getMyDoctorInfoById")
//    public HashMap<String, Object> getMyDoctorInfoById(@RequestBody BaseJsonNode json){
//        Long doctorId = json.get("id").asLong();
//        Doctor doctor = doctorDao.findById(doctorId);
//
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        Date today = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(today);
//        cal.add(Calendar.DATE, -30);
//        Date today30 = cal.getTime();
//
//        List<String> services = new ArrayList<>();
//        for(Service s:doctorServiceDao.findDoctorServices(doctor)){
//            services.add(s.getName());
//        }
//
//        HashMap<String, Object> h = new HashMap<>();
//        h.put("name", doctor.getName()+" "+doctor.getLastname());
//        h.put("skill", doctor.getEducation().getName()+" "+doctor.getExpertice().getName());
//        h.put("melliCode", doctor.getMelliCode());
//        h.put("image", doctor.getImage());
//        h.put("services",services);
//        h.put("phone", doctor.getPhone());
//        h.put("doctorNumber", doctor.getDoctorCode());
//        h.put("turnNum", turnDao.countDoctorTurnBetweenDates(doctor.getId(), today,today30));
//        h.put("income30", turnDao.doctorLast30IncomeById(doctor.getId(), today, today30));
//        h.put("bio", doctor.getBio());
//
//
//        return h;
//    }

    @PostMapping(value = "/removeDoctorFromClinic")
    public void removeDoctorFromClinic(@RequestBody BaseJsonNode json){
        Clinic clinic = clinicDao.findByMelliCode(json.get("clinicMc").asText());
        Long doctorId = json.get("doctorId").asLong();
        ClinicOffice co = clinicOfficeDao.findByClinicIdAndDoctorId(clinic.getId(), doctorId);
//        clinicOfficeDao.removeByClinicIdAndDoctorId(clinic.getId(), doctorId);
        co.setEnable(false);
        clinicOfficeDao.save(co);
    }

    @PostMapping(value = "/getMyDoctorsBySearch")
    public List<HashMap> getMyDoctorsBySearch(@RequestBody BaseJsonNode json){
        Clinic clinic = clinicDao.findByMelliCode(json.get("username").asText());
        Doctor doctor = new Doctor();
        doctor.setLastname(json.get("name").asText());
        doctor.setMelliCode(json.get("melliCode").asText());
        if(json.get("gender").isNumber()){
            doctor.setGender(null);
        }else
            doctor.setGender(json.get("gender").asBoolean());
        doctor.setDoctorCode(json.get("doctorCode").asText());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -30);
        Date today30 = cal.getTime();

        List<Doctor> doctors = null;
        if(doctor.getGender() == null)
            doctors = clinicOfficeDao.findDoctorsByClinicAndDoctorWithoutGender(clinic, doctor);
        else
            doctors = clinicOfficeDao.findDoctorsByClinicAndDoctor(clinic, doctor);

        List<HashMap> list =  new ArrayList<>();
        for(Doctor d:doctors){
            List<String> services = new ArrayList<>();
            for(Service s:doctorServiceDao.findDoctorServices(d)){
                services.add(s.getName());
            }

            HashMap<String, Object> h = new HashMap<>();
            h.put("id", d.getId());
            h.put("name", d.getName()+" "+d.getLastname());
            h.put("skill", d.getEducation().getName()+" "+d.getExpertice().getName());
            h.put("melliCode", d.getMelliCode());
            h.put("image", d.getImage());
            h.put("services",services);
            h.put("phone", d.getPhone());
            h.put("doctorNumber", d.getDoctorCode());
            h.put("turnNum", turnDao.countDoctorOfClinicTurnBetweenDates(clinic.getId(), d.getId(), today,today30));
            h.put("income30", turnDao.doctorOfClinicLast30IncomeById(clinic.getId(), d.getId(), today, today30));
            h.put("bio", d.getBio());
            list.add(h);
        }
        return list;
    }

    @PostMapping("/findDoctorsLikeMelliCode")
    public List<String> findDoctorsLikeMelliCode(@RequestBody BaseJsonNode json){
        String mc = json.get("melliCode").asText();
        List<Doctor> doctors = doctorDao.findLikeMelliCode(mc);
        List<String> list = new ArrayList<>();
        for(Doctor d:doctors){
            list.add(d.getMelliCode());
        }
        return list;
    }

    @PostMapping("/getDoctorByMelliCode")
    public HashMap<String, Object> getDoctorByMelliCode(@RequestBody BaseJsonNode json){
      HashMap<String, Object> h = new HashMap<>();
      String mc = json.get("melliCode").asText();
      Doctor doctor = doctorDao.findByMelliCode(mc);
      h.put("name", doctor.getName()+" "+doctor.getLastname());
      h.put("melliCode", doctor.getMelliCode());
      h.put("phone", doctor.getPhone());

      return h;
    }

    @PostMapping("/addDoctorToClinic")
    public void addDoctorToClinic(@RequestBody BaseJsonNode json){
        String dMc = json.get("doctorMelliCode").asText();
        String cPhone = json.get("clinicPhone").asText();
        String cMc= json.get("clinicMelliCode").asText();
        Doctor d = doctorDao.findByMelliCode(dMc);
        Clinic c = clinicDao.findByMelliCode(cMc);

        ClinicOffice clinicOffice = clinicOfficeDao.findExactByDoctorId(c, d);
        if(clinicOffice != null && clinicOffice.getEnable()== false){
            clinicOffice.setEnable(true);
            clinicOfficeDao.save(clinicOffice);
        }else {
            ClinicOffice co = new ClinicOffice();
            co.setClinic(c);
            co.setDoctor(d);
            co.setEnable(true);
            co.setPhone(cPhone);
            clinicOfficeDao.save(co);
        }
    }

    @PostMapping(value="/dailyReport")
    public List<HashMap> dailyReport(@RequestBody BaseJsonNode json){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String melliCode = json.get("melliCode").asText();
        String sDate = json.get("sDate").asText();
        String eDate = json.get("eDate").asText();
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Clinic clinic = clinicDao.findByMelliCode(melliCode);
        List<Turn> turns = turnDao.findByClinicIdAndBetweenDates(clinic.getId(), startDate, endDate);
        /*
        {date: '1398/1/1', name: 'مهدی موحدی', gender: 'مرد', treat: 'عصب کشی, پرکردن', cost: '300,000'
         */

        List<HashMap> list = new ArrayList<>();
        for(Turn t:turns){
            HashMap<String, Object> h = new HashMap<>();
            h.put("date", dateFormat.format(t.getDate()));
            h.put("name", t.getPatient().getName()+" "+t.getPatient().getLastname());
            if(t.getPatient().getGender() == false)
                h.put("gender", "مرد");
            else
                h.put("gender", "زن");
            String services = "";
            List<Service> serviceList = turnServiceDao.findServicesByTurn(t);
            for(int i=0;i<serviceList.size();i++){
                if(i == serviceList.size()-1)
                    services += serviceList.get(i).getName();
                else
                    services += serviceList.get(i).getName()+ ", ";
            }

            h.put("treat", services);
            h.put("cost", t.getCost());
            String status = "";
            if(t.getStatus() == 0)
                status = "درحال انتظار";
            else if(t.getStatus() == 1)
                status = "ویزیت شده";
            else if(t.getStatus() == 2)
                status = "لغو شده";
            h.put("status", status);
            h.put("dname", t.getDoctor().getName()+" "+t.getDoctor().getLastname());
            h.put("dskill", t.getDoctor().getEducation().getName()+" "+t.getDoctor().getExpertice().getName());
            list.add(h);
        }
        return list;
    }

    @PostMapping(value = "/getTurnNumByClinicIdBetweenDates")
    public Long getPatientsNumBetweenDates(@RequestBody BaseJsonNode json){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String melliCode = json.get("melliCode").asText();
        String sDate = json.get("sDate").asText();
        String eDate = json.get("eDate").asText();
        Date s = null;
        Date e = null;
        try {
            s = dateFormat.parse(sDate);
            e = dateFormat.parse(eDate);
        } catch (ParseException er) {
            er.printStackTrace();
        }
        Clinic clinic = clinicDao.findByMelliCode(melliCode);
        return turnDao.countTurnByClinicIdAndBetweenDates(clinic.getId(), s, e);
    }

    @PostMapping(value = "/getIncomeBetweenDates")
    public String getIncomeBetweenDates(@RequestBody BaseJsonNode json){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String melliCode = json.get("melliCode").asText();
        String sDate = json.get("sDate").asText();
        String eDate = json.get("eDate").asText();
        Date s = null;
        Date e = null;
        try {
            s = dateFormat.parse(sDate);
            e = dateFormat.parse(eDate);
        } catch (ParseException er) {
            er.printStackTrace();
        }
        Clinic clinic = clinicDao.findByMelliCode(melliCode);
        return String.valueOf(turnDao.findIncomeByClinicIdAndBetweenDates(clinic.getId(), s, e));
    }

    @PostMapping(value="/getChartInfo")
    public HashMap<String, Object> getChartInfo(@RequestBody BaseJsonNode json){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String melliCode = json.get("melliCode").asText();
        String sDate = json.get("sDate").asText();
        String eDate = json.get("eDate").asText();

        Date s = null;
        Date e = null;
        try {
            s = dateFormat.parse(sDate);
            e = dateFormat.parse(eDate);
        } catch (ParseException er) {
            er.printStackTrace();
        }

        Clinic clinic = clinicDao.findByMelliCode(melliCode);

        List<HashMap> list = new ArrayList<>();
        for(int i=0; i<=daysBetween(s, e);i++){
            HashMap<String, Object> h = new HashMap<>();

            Date date = s;
            com.ibm.icu.util.Calendar calendar = com.ibm.icu.util.Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(com.ibm.icu.util.Calendar.DATE, i);
//            date = c.getTime();
            Long pateintNum = turnDao.findTurnCountByClinicIdAndDate(clinic.getId(), calendar.getTime());

            String persianDate = JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(calendar.get(com.ibm.icu.util.Calendar.YEAR), calendar.get(com.ibm.icu.util.Calendar.MONTH)+1,
                    calendar.get(com.ibm.icu.util.Calendar.DAY_OF_MONTH))).toString();

            h.put("name",persianDate);
            h.put("نوبت", pateintNum);
            list.add(h);
        }

        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("data", list);
        hashMap.put("datakey", "نوبت");

        return hashMap;
    }

    @PostMapping(value = "/getClinicServices")
    public List getClinicServices(@RequestBody BaseJsonNode json) {
        String melliCode = json.get("username").asText();
        Clinic clinic = clinicDao.findByMelliCode(melliCode);

        List<List> list = new ArrayList<>();

        List<Service> services = doctorServiceDao.findClinicServices(clinic.getId());
        for(Service s:services){
            List l = new ArrayList();
            l.add(s.getId());
            l.add(s.getName());
            list.add(l);
        }

        return list;
    }

    @PostMapping(value = "/getMyPatient")
    public List<HashMap> getMyPatient(@RequestBody BaseJsonNode json){
        Clinic clinic = clinicDao.findByMelliCode(json.get("username").asText());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Patient> patients = turnDao.findPatientsByClinic(clinic);
        List<HashMap> list =  new ArrayList<>();
        for(Patient p:patients){
            HashMap<String, Object> h = new HashMap<>();
            h.put("id", p.getId());
            h.put("name", p.getName()+" "+p.getLastname());
            h.put("phone", p.getPhone());
            h.put("melliCode", p.getMelliCode());
            h.put("image", p.getImage());

            Turn t = turnDao.findLastTurnByPatient(p);
            h.put("lastVisitTime", t.getStartTime());
            h.put("lastVisitDate", dateFormat.format(t.getDate()));

            list.add(h);
        }
        return list;
    }

    @PostMapping(value = "/getMyPatientBySearch")
    public List<HashMap> getMyPatientByName(@RequestBody BaseJsonNode json){
        Clinic clinic = clinicDao.findByMelliCode(json.get("username").asText());
        Patient patient = new Patient();
        patient.setLastname(json.get("name").asText());
        patient.setMelliCode(json.get("melliCode").asText());
        patient.setGender(json.get("gender").asBoolean());
        Long serviceId = json.get("service").asLong();
        List<Turn> turns = turnServiceDao.findTurnByServiceId(serviceId);

        List<Patient> patients = null;
        if(serviceId == -1)
            patients = turnDao.findPatientsByClinicAndPatient(clinic, patient);
        else
            patients = turnDao.findPatientsByClinicAndPatientAndTurns(clinic, patient, turns);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<HashMap> list =  new ArrayList<>();
        for(Patient p:patients){
            HashMap<String, Object> h = new HashMap<>();
            h.put("id", p.getId());
            h.put("name", p.getName()+" "+p.getLastname());
            h.put("phone", p.getPhone());
            h.put("melliCode", p.getMelliCode());
            h.put("image", p.getImage());

            Turn t = turnDao.findLastTurnByPatient(p);
            h.put("lastVisitTime", t.getStartTime());
            h.put("lastVisitDate", dateFormat.format(t.getDate()));

            list.add(h);
        }
        return list;
    }

    @PostMapping(value = "/getBestDoctorsInfoOfClinic")
    public List<HashMap> getBestDoctorsInfo(@RequestBody BaseJsonNode json){
        /* name education expertice city */
        Clinic clinic = clinicDao.findByMelliCode(json.get("melliCode").asText());
        List<Long> bestDoctorsId = turnDao.findIdOfBestDoctorsByClinicId(clinic.getId());
        List<HashMap> list = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -30);
        Date today30 = cal.getTime();

        for(Long id:bestDoctorsId){
            HashMap<String, Object> h = new HashMap<>();
            Doctor d = doctorDao.findById(id);

            ClinicOffice co = clinicOfficeDao.findByClinicIdAndDoctorId(clinic.getId(), d.getId());
            if(co == null || co.getEnable() == false)
                continue;
            List<String> services = new ArrayList<>();
            for(Service s:doctorServiceDao.findDoctorServices(d)){
                services.add(s.getName());
            }

            h.put("id", d.getId());
            h.put("name", d.getName()+" "+d.getLastname());
            h.put("skill", d.getEducation().getName()+" "+d.getExpertice().getName());
            h.put("melliCode", d.getMelliCode());
            h.put("image", d.getImage());
            h.put("services",services);
            h.put("phone", d.getPhone());
            h.put("doctorNumber", d.getDoctorCode());
            h.put("turnNum", turnDao.countDoctorOfClinicTurnBetweenDates(clinic.getId(), d.getId(), today,today30));
            h.put("income30", turnDao.doctorOfClinicLast30IncomeById(clinic.getId(), d.getId(), today, today30));
            h.put("bio", d.getBio());

            list.add(h);
        }
        return list;
    }

    @PostMapping(value = "/getStatisticsInfo")
    public HashMap<String, Object> getStatisticsInfo(@RequestBody BaseJsonNode json){
        Clinic clinic = clinicDao.findByMelliCode(json.get("melliCode").asText());
        /*
           countTodyTurn: '',
        income: '',
        countDoctors: '',
        countPatients: ''
         */
        HashMap<String, Object> map = new HashMap<>();
        map.put("countTodyTurn", turnDao.countTodayTurnByClinicId(clinic.getId()));
        map.put("income", turnDao.findIncomeByClinicId(clinic.getId()));
        map.put("countDoctors", clinicOfficeDao.findDoctorsByClinicId(clinic.getId()).size());
        map.put("countPatients", turnDao.findPatientsByClinic(clinic).size());

        return map;
    }
    //------------------------------------ ohters
    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
