package com.example.epointment.controller;


import com.example.epointment.common.*;
import com.example.epointment.config.DataConfiguration;
import com.example.epointment.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BaseJsonNode;

import com.ibm.icu.util.ULocale;
import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;
import com.kavenegar.sdk.models.SendResult;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.print.Doc;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ibm.icu.util.Calendar;


@RestController
@CrossOrigin
@RequestMapping(value = "/api/doctor")
@Secured("ROLE_DOCTOR")
public class DoctorController {

    @Autowired
    DoctorDao doctorDao;
    @Autowired
    OfficeDao officeDao;
    @Autowired
    ClinicOfficeDao clinicOfficeDao;
    @Autowired
    ExperticeDao experticeDao;
    @Autowired
    EducationDao educationDao;
    @Autowired
    PatientDao patientDao;
    @Autowired
    ServiceDao serviceDao;
    @Autowired
    DoctorServiceDao doctorServiceDao;
    @Autowired
    InsuranceDao insuranceDao;
    @Autowired
    DoctorInsuranceDao doctorInsuranceDao;
    @Autowired
    TurnDao turnDao;
    @Autowired
    TurnServiceDao turnServiceDao;

    @GetMapping(value = "/")
    public void doctor() {
    }

    @PostMapping(value = "/getFullInfo")
    public HashMap<String, Object> getFullInfo(@RequestBody BaseJsonNode json) {
        Doctor doctor = doctorDao.findByMelliCode(json.get("melliCode").asText());
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("name", doctor.getName());
        hm.put("lastname", doctor.getLastname());
        hm.put("melliCode", doctor.getMelliCode());
//        hm.put("state", doctor.getProvince().getName());
        hm.put("phone", doctor.getPhone());
        hm.put("city", doctor.getCity());
        hm.put("doctorCode", doctor.getDoctorCode());

        List<HashMap> clinics = new ArrayList<>();
        for (ClinicOffice co : doctor.getClinics()) {
            if(co.getEnable()){
                HashMap<String, Object> clinic = new HashMap<>();
                clinic.put("id", co.getId());
                clinic.put("address", co.getClinic().getName() + " : " + co.getClinic().getAddress());
                clinics.add(clinic);
            }

        }
        hm.put("clinic", clinics);
        hm.put("gender", doctor.getGender());
        hm.put("bio", doctor.getBio());

        List<HashMap> addressPhone = new ArrayList<>();
        for (Office o : doctor.getOfficeList()) {
            HashMap<String, Object> office = new HashMap<>();
            office.put("id", o.getId());
            office.put("number", o.getNumber());
            office.put("address", o.getAddress());

            addressPhone.add(office);
        }
        hm.put("addressPhone", addressPhone);
        hm.put("image", doctor.getImage());
        hm.put("expertice", doctor.getExpertice());
        hm.put("education", doctor.getEducation());

        return hm;
    }

    @PostMapping(value = "/getInfo")
    public HashMap<String, Object> getInfo(@RequestBody BaseJsonNode json) {
//        return doctorDao.getInfo();
        Doctor doctor = doctorDao.findByMelliCode(json.get("melliCode").asText());
        HashMap<String, Object> info = new HashMap<>();
        info.put("melliCode", doctor.getMelliCode());
        info.put("name", doctor.getName());
        info.put("lastname", doctor.getLastname());
        info.put("expertice", doctor.getExpertice().getName());
        info.put("education", doctor.getEducation().getName());
        info.put("image", doctor.getImage());
        return info;
    }

    @PostMapping(value = "/updateProfile")
    public void updateProfile(@RequestBody BaseJsonNode json) {
        String melliCode = json.get("melliCode").asText();
        Doctor doctor = doctorDao.findByMelliCode(melliCode);
        if (doctor != null) {
            doctor.setName(json.get("name").asText());
            doctor.setLastname(json.get("lastname").asText());
            doctor.setCity(new City().setId(json.get("city").get("id").asLong()));
            doctor.setBio(json.get("bio").asText());

            List<Office> offices = new ArrayList<>();
            for (Iterator<JsonNode> i = json.get("addressPhone").iterator(); i.hasNext(); ) {
                JsonNode item = i.next();
                Office office = null;
                if (item.get(2).asLong() != -1)
                    office = new Office(item.get(2).asLong(), item.get(0).asText(), item.get(1).asText(), doctor);
                else
                    office = new Office(item.get(0).asText(), item.get(1).asText(), doctor);
                officeDao.save(office);
                offices.add(office);
            }

            doctor.setOfficeList(offices);
            doctor.setExpertice(new Expertice().setId(json.get("expertice").get("id").asLong()));
            doctor.setEducation(new Education().setId(json.get("education").get("id").asLong()));
            doctor.setCity(new City().setId(json.get("city").get("id").asLong()));
            doctor.setGender(json.get("gender").asBoolean());
            doctorDao.save(doctor);

            //---- delete doctor offices
            for (Iterator<JsonNode> i = json.get("deleteAddress").iterator(); i.hasNext(); ) {
                long id = i.next().asLong();
                officeDao.deleteById(id);
            }
            //---- delete doctoor clinics
            for (Iterator<JsonNode> i = json.get("deleteClinic").iterator(); i.hasNext(); ) {
                long id = i.next().asLong();
                clinicOfficeDao.deleteById(id);
            }


        }
    }

    @PostMapping(value = "/updateDoctorPhone")
    public void updateDoctorPhone(@RequestBody BaseJsonNode json) {
        String melliCode = json.get("melliCode").asText();
        String phone = json.get("phone").asText();
        Doctor doctor = doctorDao.findByMelliCode(melliCode);
        doctor.setPhone(phone);
        doctorDao.save(doctor);
    }

    @PostMapping(value = "/addPatient")
    public void addPatient(@RequestBody BaseJsonNode json) {
        String dMelliCode = json.get("username").asText();
        Doctor doctor = doctorDao.findByMelliCode(dMelliCode);

        String name = json.get("name").asText();
        String lastname = json.get("lastname").asText();
        String codeMelli = json.get("melliCode").asText();
        String phone = json.get("phone").asText();
        Boolean gender = json.get("gender").asBoolean();

        Patient patient = new Patient();
        patient.setGender(gender);
        patient.setName(name);
        patient.setLastname(lastname);
        patient.setMelliCode(codeMelli);
        patient.setPhone(phone);
        patient.setDoctor(doctor);

        patientDao.save(patient);
    }

    @PostMapping(value = "/patientMelliCodeIsExists")
    public boolean patientMelliCodeIsExists(@RequestBody BaseJsonNode json) {
        String patientMelliCode = json.get("melliCode").asText();
        Patient patient = patientDao.findByMelliCode(patientMelliCode);
        if (patient != null)
            return true;
        else
            return false;

    }

    @PostMapping(value = "/patientPhoneIsExists")
    public boolean patientPhoneIsExists(@RequestBody BaseJsonNode json) {
        String phone = json.get("phone").asText();
        Patient patient = patientDao.findByPhone(phone);
        if (patient != null)
            return true;
        else
            return false;
    }

    //---------- our service
    @GetMapping(value = "/get50TopServices")
    public List<Service> get50TopServices() {
        return serviceDao.find50TopService();
    }

    @PostMapping(value = "/getServiceLikeName")
    public List<Service> getServiceLikeName(@RequestBody BaseJsonNode json) {
        return serviceDao.findAllServiceLike(json.get("name").asText());
    }

    @PostMapping(value = "/addService")
    public Service addService(@RequestBody BaseJsonNode json) {

        if (json.get("service").asText() == "" || json.get("service").asText() == null)
            return null;
//        List<Service> services = new
        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());
        DoctorService doctorService = new DoctorService();
        doctorService.setDoctor(doctor);

        Service service;
        if (json.get("service").isNumber()) {
            service = serviceDao.findById(json.get("service").asLong());
            if (doctorServiceDao.doctorHasService(doctor, service)) {
                return null;
            }
        } else {
            service = serviceDao.findByName(json.get("service").asText());
            if (service == null)
                service = serviceDao.save(new Service().setName(json.get("service").asText()));
        }
        DoctorService ds = new DoctorService();
        ds.setService(service);
        ds.setDoctor(doctor);
        doctorServiceDao.save(ds);

        return service;
    }

    @PostMapping(value = "/removeService")
    public void removeService(@RequestBody BaseJsonNode json) {
        Service service = serviceDao.findById(json.get("serviceId").asLong());
        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());

        DoctorService ds = doctorServiceDao.findByDoctorAndService(doctor, service);
        doctorServiceDao.remove(ds);
    }

    @PostMapping(value = "/getDoctorServices")
    public List<Service> getDoctorServices(@RequestBody BaseJsonNode json) {
        String melliCode = json.get("username").asText();
        Doctor doctor = doctorDao.findByMelliCode(melliCode);
        return doctorServiceDao.findDoctorServices(doctor);
    }

    @GetMapping(value = "/get50TopInsurances")
    public List<Insurance> get50TopInsurance() {
        return insuranceDao.find50Top();
    }

    @PostMapping(value = "/getInsuranceLikeName")
    public List<Insurance> getInsuranceLikeName(@RequestBody BaseJsonNode json) {
        return insuranceDao.findAllInsuranceLike(json.get("name").asText());
    }

    @PostMapping(value = "/getDoctorInsurance")
    public List<Insurance> getDoctorInsurance(@RequestBody BaseJsonNode json) {
        String melliCode = json.get("username").asText();
        Doctor doctor = doctorDao.findByMelliCode(melliCode);
        return doctorInsuranceDao.findDoctorInsurances(doctor);
    }

    @PostMapping(value = "/addInsurance")
    public Insurance addInsurance(@RequestBody BaseJsonNode json) {

        if (json.get("insurance").asText() == "" || json.get("insurance").asText() == null)
            return null;

        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());
        DoctorInsurance doctorInsurance = new DoctorInsurance();
        doctorInsurance.setDoctor(doctor);

        Insurance insurance;
        if (json.get("insurance").isNumber()) {
            insurance = insuranceDao.findById(json.get("insurance").asLong());
            if (doctorInsuranceDao.doctorHasInsurance(doctor, insurance)) {
                return null;
            }
        } else {
            insurance = insuranceDao.findByName(json.get("insurance").asText());
            if (insurance == null)
                insurance = insuranceDao.save(new Insurance().setName(json.get("insurance").asText()));
        }
        DoctorInsurance ds = new DoctorInsurance();
        ds.setInsurance(insurance);
        ds.setDoctor(doctor);
        doctorInsuranceDao.save(ds);

        return insurance;
    }

    @PostMapping(value = "/removeInsurance")
    public void removeInsurance(@RequestBody BaseJsonNode json) {
        Insurance insurance = insuranceDao.findById(json.get("insuranceId").asLong());
        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());

        DoctorInsurance di = doctorInsuranceDao.findByDoctorAndInsurance(doctor, insurance);
        doctorInsuranceDao.remove(di);
    }

    //--------------------- turns
    //---- new turn
    @PostMapping(value = "/findPatientLikeLastname")
    public List findPatientLikeLastname(@RequestBody BaseJsonNode json) {
        String lname = json.get("lastname").asText();
        List list = new ArrayList();

        if (lname == "" || lname == null)
            return list;
        List<Patient> patients = patientDao.findLikeLastname(lname);
        for (Patient p : patients) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("id", p.getId());
            hm.put("name", p.getName());
            hm.put("lastname", p.getLastname());
            hm.put("melliCode", p.getMelliCode());
            hm.put("phone", p.getPhone());

            list.add(hm);
        }
        return list;
    }

    @PostMapping(value = "/findPatientsLikeMelliCode")
    public List findPatientLikeMelliCode(@RequestBody BaseJsonNode json) {
        String melliCode = json.get("melliCode").asText();
        List list = new ArrayList();

        if (melliCode == "" || melliCode == null)
            return list;
        List<Patient> patients = patientDao.findLikeMelliCode(melliCode);
        for (Patient p : patients) {
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("id", p.getId());
            hm.put("name", p.getName());
            hm.put("lastname", p.getLastname());
            hm.put("melliCode", p.getMelliCode());
            hm.put("phone", p.getPhone());

            list.add(hm);
        }
        return list;
    }

    //--------- turn
    @PostMapping(value = "/dateTurnConflict")
    public boolean dateTurnConflict(@RequestBody BaseJsonNode json) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        String sDate = json.get("date").asText();
        String sSTime = json.get("sTime").asText();
        String sETime = json.get("eTime").asText();
        String mc;
        mc = json.get("username").asText();
        Doctor doctor = doctorDao.findByMelliCode(mc);
        Long turnId = null;
        if(json.get("id") != null)
            turnId = json.get("id").asLong();

        try {
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            java.sql.Time timeValue = new java.sql.Time(formatter.parse(sSTime).getTime());
            Date date = format.parse(sDate);
            java.sql.Time sTime = new java.sql.Time(formatter.parse(sSTime).getTime());
            java.sql.Time eTime = new java.sql.Time(formatter.parse(sETime).getTime());

            List<Turn> turns = turnDao.findByDateAndBetweenHours(doctor.getId(), date, sTime, eTime);
            if (turns.size() > 0 )
            {
                for(Turn turn:turns) {
                    if (turn.getStatus() == 0){
                        if(turnId == null) return true;
                        if (turn.getId() != turnId) {
                            return true;
                        }
                    }
                }
                return false;
            }
            else
                return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        turnDao.findByDateAndBetweenHours()
//        date.getCalendarDate().getYear()
        return false;
    }

    @PostMapping(value = "/createTurn")
    public Long createTurn(@RequestBody BaseJsonNode json) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());
        Turn turn = new Turn();
        Long turnId = null;
        try {
            Date date = format.parse(json.get("date").asText());
            Time sTiem = new Time(formatter.parse(json.get("sTime").asText()).getTime());
            Time eTime = new Time(formatter.parse(json.get("eTime").asText()).getTime());
            Long patientId = json.get("patientId").asLong();

            String comment = json.get("comment").asText();
            Double cost = json.get("cost").asDouble();
            Long insuranceId = json.get("insurance").get("id").asLong();


            Patient patient = new Patient();
            patient.setId(patientId);

            Insurance insurance = new Insurance();
            insurance.setId(insuranceId);

            turn.setStartTime(sTiem).setEndTime(eTime).setPatient(patient)
                    .setInsurance(insurance).setComment(comment).setCost(cost)
                    .setDate(date).setStatus(0).setDoctor(doctor);


            JsonNode jOffice = json.get("offices");
            if (jOffice != null) {
                if (jOffice.get("type").asText().equals("clinic")) {
                    ClinicOffice co = clinicOfficeDao.findById(jOffice.get("id").asLong());
                    turn.setClinicOffice(co);
                } else if (jOffice.get("type").asText().equals("office")) {
                    Office office = officeDao.findById(jOffice.get("id").asLong());
                    turn.setOffice(office);
                }
            }

            Turn tempTurn = turnDao.save(turn);
            turnId = tempTurn.getId();

            TurnService ts = new TurnService();
            Iterator<JsonNode> iterator = json.get("services").iterator();
            for (; iterator.hasNext(); ) {
                JsonNode service = iterator.next();
                ts.setTurn(tempTurn);
                Service s = new Service();
                s.setId(service.get("id").asLong());
                ts.setService(s);
                turnServiceDao.save(ts);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Patient p = patientDao.findById(json.get("patientId").asLong());
        String address = "";
        JsonNode jOffice = json.get("offices");
        if (jOffice != null) {
            if (jOffice.get("type").asText().equals("clinic")) {
                ClinicOffice co = clinicOfficeDao.findById(jOffice.get("id").asLong());
                address = co.getClinic().getAddress();
            } else if (jOffice.get("type").asText().equals("office")) {
                Office office = officeDao.findById(jOffice.get("id").asLong());
                address = office.getAddress();
            }
        }



        this.sendTurnInfo(p.getPhone(), doctor.getName()+" "+doctor.getLastname() , address, turn.getDate(), turn.getStartTime());

        return turnId;
    }

    @PostMapping(value = "/getDoctorOfficesAndCLinics")
    public List getDoctorOfficesAndCLinics(@RequestBody BaseJsonNode json) {
        List list = new ArrayList();
        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());

        for (ClinicOffice co : doctor.getClinics()) {
            if(co.getEnable()){
                HashMap<String, Object> clinic = new HashMap<>();
                clinic.put("id", co.getId());
                clinic.put("address", co.getClinic().getName() + " : " + co.getClinic().getAddress());
                clinic.put("type", "clinic");
                list.add(clinic);
            }
        }

        for (Office o : doctor.getOfficeList()) {
            HashMap<String, Object> office = new HashMap<>();
            office.put("id", o.getId());
            office.put("address", o.getAddress());
            office.put("type", "office");
            list.add(office);
        }

        return list;
    }

    @PostMapping(value = "/getAllTurns")
    public List getAllTurns(@RequestBody BaseJsonNode json) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String melliCode = json.get("username").asText();
        Doctor doctor = doctorDao.findByMelliCode(melliCode);
        List<Turn> turns = turnDao.findByDoctor(doctor);

        List response = new ArrayList();
        for (Turn turn : turns) {
            HashMap<String, Object> hm = new HashMap<>();
            String sDate = dateFormat.format(turn.getDate());
            String startTime = turn.getStartTime().toString();
            String endTime = turn.getEndTime().toString();

            hm.put("id", turn.getId());
            hm.put("title", turn.getPatient().getName() + " " + turn.getPatient().getLastname());
            hm.put("start", sDate + " " + startTime);
            hm.put("end", sDate + " " + endTime);

            hm.put("textColor", "#fff");
            switch (turn.getStatus()){
                case 1:
                    hm.put("backgroundColor", "#04B431");
                    break;
                case 2:
                    hm.put("backgroundColor", "#FF0000");
                    break;
            }


            response.add(hm);
        }
        return response;
    }

    @PostMapping(value = "/getTurnById")
    public HashMap getTurnById(@RequestBody BaseJsonNode json) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Long TurnId = json.get("turnId").asLong();
        Turn turn = turnDao.findById(TurnId);
        HashMap<String, Object> hm = new HashMap<>();

        String sDate = dateFormat.format(turn.getDate());
        String startTime = turn.getStartTime().toString();
        String endTime = turn.getEndTime().toString();

        HashMap hClinic = new HashMap();
        if (turn.getClinicOffice() != null) {
            Clinic clinic = turn.getClinicOffice().getClinic();
            hClinic.put("id", clinic.getId());
            hClinic.put("address", clinic.getName() + " : " + clinic.getAddress());
            hClinic.put("type", "clinic");
        } else {
            hClinic = null;
        }

        HashMap hOffice = new HashMap();
        if (turn.getOffice() != null) {
            Office office = turn.getOffice();
            hOffice.put("id", office.getId());
            hOffice.put("address", office.getAddress());
            hOffice.put("type", "office");
        } else {
            hOffice = null;
        }

        hm.put("id", turn.getId());
        hm.put("name", turn.getPatient().getName());
        hm.put("lastname", turn.getPatient().getLastname());
        hm.put("melliCode", turn.getPatient().getMelliCode());
        hm.put("phone", turn.getPatient().getPhone());
        hm.put("services", turnServiceDao.findServicesByTurn(turn));
        hm.put("insurance", turn.getInsurance());
        hm.put("clinic", hClinic);
        hm.put("office", hOffice);
        hm.put("comment", turn.getComment());
        hm.put("cost", turn.getCost());
        hm.put("date", sDate);
        hm.put("startTime", startTime);
        hm.put("endTime", endTime);

        return hm;
    }

    @PostMapping(value = "/updateTurn")
    public Long updateTurn(@RequestBody BaseJsonNode json) {
        boolean sendSMS = false;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
//        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());
        Turn turn = turnDao.findById(json.get("id").asLong());

        Long turnId = turn.getId();
        try {

            if(!format.parse(format.format(turn.getDate())).toString().equals(format.parse(json.get("date").asText()).toString()) || turn.getStartTime() != new Time(formatter.parse(json.get("sTime").asText()).getTime()) )
                sendSMS = true;

            Date date = format.parse(json.get("date").asText());
            turn.setDate(date);
            turn.setStartTime(new Time(formatter.parse(json.get("sTime").asText()).getTime()));
            turn.setEndTime(new Time(formatter.parse(json.get("eTime").asText()).getTime()));
            turn.setComment(json.get("comment").asText());
            turn.setCost(json.get("cost").asDouble());
            Long insuranceId = json.get("insurance").get("id").asLong();

            Insurance insurance = new Insurance();
            insurance.setId(insuranceId);

            turn.setInsurance(insurance);

            JsonNode jOffice = json.get("offices");
            if (jOffice != null) {
                if (jOffice.get("type").asText().equals("clinic")) {
                    ClinicOffice co = clinicOfficeDao.findById(jOffice.get("id").asLong());
                    turn.setClinicOffice(co);
                } else if (jOffice.get("type").asText().equals("office")) {
                    Office office = officeDao.findById(jOffice.get("id").asLong());
                    turn.setOffice(office);
                }
            }

            Turn tempTurn = turnDao.save(turn);
            turnId = tempTurn.getId();
            turnServiceDao.removeByTurnId(tempTurn);
            TurnService ts = new TurnService();
            Iterator<JsonNode> iterator = json.get("services").iterator();
            for (; iterator.hasNext(); ) {
                JsonNode service = iterator.next();
                ts.setTurn(tempTurn);
                Service s = new Service();
                s.setId(service.get("id").asLong());
                ts.setService(s);
                turnServiceDao.save(ts);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(sendSMS){
            ULocale locale = new ULocale("fa_IR@calendar=persian");
            Calendar calendar = Calendar.getInstance(locale);
            calendar.setTime(turn.getDate());
            com.ibm.icu.text.DateFormat df = com.ibm.icu.text.DateFormat.getDateInstance(com.ibm.icu.text.DateFormat.FULL, locale);

            String date = df.format(calendar);
            String message = "";
            message = "نوبت شما به "+date+" ساعت:"+turn.getStartTime()+" انتقال یافت"+" _ دکتر "+turn.getDoctor().getLastname();
            this.sendSMS(json.get("phone").asText(),message);
        }


        return turnId;
    }

    @PostMapping(value = "getTurnFiles")
    public void getTurnFiles(@RequestBody BaseJsonNode json) {
        Long turnId = json.get("turnId").asLong();
        File initialFile = new File("C:\\tabib\\" + turnId + "\1.dll");
        InputStream targetStream = null;
        try {
            targetStream =
                    new DataInputStream(new FileInputStream(initialFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @PostMapping(value = "/visitTurn")
    public void visitTurn(@RequestBody BaseJsonNode json){
        Long turnId = json.get("turnId").asLong();
        Turn turn = turnDao.findById(turnId);
        turn.setStatus(1);
        turnDao.save(turn);
    }

    @PostMapping(value = "/cancelTurn")
    public void cancelTurn(@RequestBody BaseJsonNode json){
        Long turnId = json.get("turnId").asLong();
        String phone = json.get("phone").asText();
        String pname = json.get("name").asText();
        String plastName = json.get("lastname").asText();


        Turn turn = turnDao.findById(turnId);
        turn.setStatus(2);
        String officePhone = "";
        if(turn.getOffice() != null){
            officePhone = turn.getOffice().getNumber();
        }else if(turn.getClinicOffice() != null){
            officePhone = turn.getClinicOffice().getClinic().getPhone();
        }

        String name = turn.getDoctor().getName();
        String lastName = turn.getDoctor().getLastname();

        turnDao.save(turn);

        ULocale locale = new ULocale("fa_IR@calendar=persian");
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(turn.getDate());
        com.ibm.icu.text.DateFormat df = com.ibm.icu.text.DateFormat.getDateInstance(com.ibm.icu.text.DateFormat.FULL, locale);

        String date = df.format(calendar);

        String message = pname+" "+plastName+" "+"نوبت شما به تاریخ "+date+" ساعت "+turn.getStartTime()+" توسط دکتر "+name+" "+lastName+" لغو گرددید لطفا برای گرفتن نوبت جدید با مطب پزشک تماس بگیرید تلفن تماس: "+officePhone;
        this.sendSMS(phone, message);

    }

    @PostMapping(value = "/doCancelTurn")
    public void doCancelTurn(@RequestBody BaseJsonNode json){
        Long turnId = json.get("turnId").asLong();
//        String phone = json.get("phone").asText();
//        String pname = json.get("name").asText();
//        String plastName = json.get("lastname").asText();


        Turn turn = turnDao.findById(turnId);
        String phone = turn.getPatient().getPhone();
        String pname = turn.getPatient().getName();
        String plastName = turn.getPatient().getLastname();

        turn.setStatus(2);
        String officePhone = "";
        if(turn.getOffice() != null){
            officePhone = turn.getOffice().getNumber();
        }else if(turn.getClinicOffice() != null){
            officePhone = turn.getClinicOffice().getClinic().getPhone();
        }

        String name = turn.getDoctor().getName();
        String lastName = turn.getDoctor().getLastname();

        turnDao.save(turn);

        ULocale locale = new ULocale("fa_IR@calendar=persian");
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(turn.getDate());
        com.ibm.icu.text.DateFormat df = com.ibm.icu.text.DateFormat.getDateInstance(com.ibm.icu.text.DateFormat.FULL, locale);

        String date = df.format(calendar);

        String message = pname+" "+plastName+" "+"نوبت شما به تاریخ "+date+" ساعت "+turn.getStartTime()+" توسط دکتر "+name+" "+lastName+" لغو گرددید لطفا برای گرفتن نوبت جدید با مطب پزشک تماس بگیرید تلفن تماس: "+officePhone;
        this.sendSMS(phone, message);

    }

    @PostMapping(value = "/getMyPatient")
    public List<HashMap> getMyPatient(@RequestBody BaseJsonNode json){
        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Patient> patients = turnDao.findPatientsByDoctor(doctor);
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
        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());
        Patient patient = new Patient();
        patient.setLastname(json.get("name").asText());
        patient.setMelliCode(json.get("melliCode").asText());
        patient.setGender(json.get("gender").asBoolean());
        Long serviceId = json.get("service").asLong();
        List<Turn> turns = turnServiceDao.findTurnByServiceId(serviceId);

        List<Patient> patients = null;
        if(serviceId == -1)
            patients = turnDao.findPatientsByDoctorAndPatient(doctor, patient);
        else
            patients = turnDao.findPatientsByDoctorAndPatientAndTurns(doctor, patient, turns);

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
    @Secured({"ROLE_CLINIC", "ROLE_DOCTOR"})
    @PostMapping(value ="/getPatientInfoById")
    public HashMap<String, Object> getPatientInfoById(@RequestBody BaseJsonNode json){
        Long patientId = json.get("patientId").asLong();
        Patient patient = patientDao.findById(patientId);
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("name", patient.getName()+" "+patient.getLastname());
        hm.put("melliCode", patient.getMelliCode());
        hm.put("phone", patient.getPhone());
        hm.put("image", patient.getImage());

        List<Long> turnsId = new ArrayList<>();
        List<Turn> turns = turnDao.findByPatient(patient);
        for(Turn turn:turns){
            turnsId.add(turn.getId());
        }
        hm.put("turns", turnsId);

        return hm;
    }

    @PostMapping(value ="/getPatientIdOfTurnId")
    public Long getPatientIdOfTurnId(@RequestBody BaseJsonNode json){
        Long turnId = json.get("turnId").asLong();
        Turn turn = turnDao.findById(turnId);
        return turn.getPatient().getId();
    }

    @Secured({"ROLE_CLINIC", "ROLE_DOCTOR"})
    @PostMapping(value = "/getTurnInfoById")
    public HashMap<String, Object> getTurnInfoById(@RequestBody BaseJsonNode json){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Long turnId = json.get("turnId").asLong();
        Turn turn = turnDao.findById(turnId);
        HashMap<String, Object> h = new HashMap<>();

        String phone = "";
        if(turn.getClinicOffice() != null){
            phone = turn.getClinicOffice().getClinic().getPhone();
        }else if(turn.getOffice() != null){
            phone = turn.getOffice().getNumber();
        }

        List<String> sNames = new ArrayList<>();
        List<Service> services = turnServiceDao.findServicesByTurn(turn);
        for(Service s:services){
            sNames.add(s.getName());
        }

        h.put("status", turn.getStatus());
        h.put("date",  dateFormat.format(turn.getDate()));
        h.put("STime", turn.getStartTime());
        h.put("ETime", turn.getEndTime());
        h.put("treatType", sNames);
        h.put("phone",phone);
        h.put("comment", turn.getComment());
        h.put("cost", turn.getCost());
        h.put("insurance", turn.getInsurance().getName());
        h.put("doctor", turn.getDoctor().getName()+" "+turn.getDoctor().getLastname());

        return h;
    }

    @PostMapping(value = "/getTodayTurns")
    public List<HashMap> getTodayTurns(@RequestBody BaseJsonNode json){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Doctor doctor = doctorDao.findByMelliCode(json.get("username").asText());
        List<HashMap> list = new ArrayList<>();
        Date date = new Date();
        for(Turn t:turnDao.findByDoctorAndDate(doctor, date)){
            if(t.getStatus() == 0){
                HashMap h = new HashMap();
                h.put("id", t.getId());
                h.put("date", dateFormat.format(t.getDate()));
                list.add(h);
            }

        }
        return list;
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
        Doctor doctor = doctorDao.findByMelliCode(melliCode);
        List<Turn> turns = turnDao.findByDoctorIdAndBetweenDates(doctor.getId(), startDate, endDate);
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

            list.add(h);
        }
        return list;
    }

    @PostMapping(value = "/getPatientsNumBetweenDates")
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
        Doctor doctor = doctorDao.findByMelliCode(melliCode);
        return turnDao.countPatientByDoctorIdAndBetweenDates(doctor.getId(), s, e);
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
        Doctor doctor = doctorDao.findByMelliCode(melliCode);
        return String.valueOf(turnDao.findIncomeByDoctorIdAndBetweenDates(doctor.getId(), s, e));
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

        Doctor doctor = doctorDao.findByMelliCode(melliCode);

        List<HashMap> list = new ArrayList<>();
        for(int i=0; i<=daysBetween(s, e);i++){
            HashMap<String, Object> h = new HashMap<>();

            Date date = s;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, i);
//            date = c.getTime();
            Long pateintNum = turnDao.findPatientsCountByDoctorIdAndDate(doctor.getId(), calendar.getTime());

            String persianDate = JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
                    calendar.get(Calendar.DAY_OF_MONTH))).toString();

            h.put("name",persianDate);
            h.put("نوبت", pateintNum);
            list.add(h);
        }

        HashMap<String, Object> hashMap = new HashMap();
        hashMap.put("data", list);
        hashMap.put("datakey", "نوبت");

        return hashMap;
    }


    @PostMapping(value = "/get3endTurns")
    public List<HashMap> get3endTurns(@RequestBody BaseJsonNode json){
        Doctor doctor = doctorDao.findByMelliCode(json.get("melliCode").asText());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<HashMap> list = new ArrayList<>();
        for(Turn t:turnDao.last3TurnsByDoctorId(doctor.getId()) ){
            if(t.getStatus() == 0){
                HashMap<String, Object> h = new HashMap<>();
                h.put("id", t.getId());
                h.put("date", dateFormat.format(t.getDate()));
                list.add(h);
            }
        }
        return list;
    }

    @PostMapping(value = "/getLastSubmitedTurns")
    public List<HashMap> getLastSubmitedTurns(@RequestBody BaseJsonNode json){
        Doctor doctor = doctorDao.findByMelliCode(json.get("melliCode").asText());
        List<Turn> turns = turnDao.findLastSubmitedByDoctorId(doctor.getId());
        List<HashMap> list = new ArrayList<>();
        for(Turn t:turns){
            HashMap<String, Object> h = new HashMap<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(t.getDate());
            String d = JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
                    calendar.get(Calendar.DAY_OF_MONTH))).toString();

            String services = "";
            List<Service> serviceList = turnServiceDao.findServicesByTurn(t);
            for(int i=0;i<serviceList.size();i++){
                if(i == serviceList.size()-1)
                    services += serviceList.get(i).getName();
                else
                    services += serviceList.get(i).getName()+ ", ";
            }

            h.put("id", t.getId());
            h.put("name", t.getPatient().getName()+" "+t.getPatient().getLastname());
            h.put("services", services);
            h.put("date", d);
            h.put("time", t.getStartTime());
            if(t.getStatus() == 0)
                h.put("status", "درحال انتظار");
            else if(t.getStatus() == 1)
                h.put("status","ویزیت شده");
            else
                h.put("status", "لغو شده");

            list.add(h);
        }

        return list;
    }

    @PostMapping(value = "/getStatisticInfo")
    public HashMap<String, Object> getStatisticInfo(@RequestBody BaseJsonNode json){
        Doctor doctor = doctorDao.findByMelliCode(json.get("melliCode").asText());
        /*
        patientsCount: '',
        todayTurnCount: '',
        turnsCount: '',
        income: '',
         */
        HashMap<String, Object> map = new HashMap<>();
        map.put("todayTurnCount", turnDao.countTodayTurnByDoctorId(doctor.getId()));
        map.put("income", turnDao.findIncomeByDoctorId(doctor.getId()));
        map.put("turnsCount", turnDao.countTurnsByDoctorId(doctor.getId()));
        map.put("patientsCount", turnDao.findPatientsByDoctor(doctor).size());

        return map;
    }
    //----------------------------------------------------- SMS
    public void sendTurnInfo(String phone, String doctorName, String address,Date date, Time startTime) {

        ULocale locale = new ULocale("fa_IR@calendar=persian");
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
        com.ibm.icu.text.DateFormat df = com.ibm.icu.text.DateFormat.getDateInstance(com.ibm.icu.text.DateFormat.FULL, locale);

        System.out.println(df.format(calendar));

        String msg = "نوبت شما در تاریخ " +df.format(calendar)+" برای ساعت"+ startTime + " توسط دکتر " + doctorName + " ثبت شد ادرس : " + address;
        try {
            KavenegarApi api = new KavenegarApi(DataConfiguration.smsApiKey);
            SendResult Result = api.send(null, phone, msg);
        } catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("HttpException  : " + ex.getMessage());
        } catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("ApiException : " + ex.getMessage());
        }
    }

    public void sendSMS(String phone, String msg){
        try {
            KavenegarApi api = new KavenegarApi(DataConfiguration.smsApiKey);
            SendResult Result = api.send(null, phone, msg);
        } catch (HttpException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("HttpException  : " + ex.getMessage());
        } catch (Exception ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("ApiException : " + ex.getMessage());
        }
    }

    //------------------------------------ ohters
    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }


}