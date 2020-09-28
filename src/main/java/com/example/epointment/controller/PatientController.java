package com.example.epointment.controller;

import com.example.epointment.common.*;
import com.example.epointment.config.DataConfiguration;
import com.example.epointment.model.PatientDao;
import com.example.epointment.model.TurnDao;
import com.example.epointment.model.TurnServiceDao;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.HttpException;
import com.kavenegar.sdk.models.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/patient")
@Secured({"ROLE_PATIENT", "ROLE_DOCTOR"})
public class PatientController {
    @Autowired
    PatientDao patientDao;
    @Autowired
    TurnDao turnDao;
    @Autowired
    TurnServiceDao turnServiceDao;

    @PostMapping(value = "/getInfo")
    public HashMap<String, Object> getInfo(@RequestBody BaseJsonNode json){
      String melliCode = json.get("melliCode").asText();
      Patient patient = patientDao.findByMelliCode(melliCode);
      HashMap<String, Object> h = new HashMap<>();
      h.put("name", patient.getName());
      h.put("lastname", patient.getLastname());
      h.put("image", patient.getImage());
      h.put("phone", patient.getPhone());
      h.put("id", patient.getId());
      h.put("gender", patient.getGender());
      if(patient.getDoctor() != null)
        h.put("doctorId", patient.getDoctor().getId());
      else
          h.put("doctorId", null);

      return h;
    }

    @PostMapping(value = "/getTurns")
    public List<Long> getTurns(@RequestBody BaseJsonNode json){
        String melliCode = json.get("melliCode").asText();
        List<Long> list = new ArrayList<>();
        Patient p = patientDao.findByMelliCode(melliCode);
        List<Turn> turns = turnDao.findByPatient(p);
        for(Turn t:turns){
            list.add(t.getId());
        }
        return list;
    }

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

    @PostMapping(value = "/cancelTurn")
    public void cancelTurn(@RequestBody BaseJsonNode json){
        Long turnId = json.get("turnId").asLong();


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

    @PostMapping(value = "/changePhone")
    public void changePhone(@RequestBody BaseJsonNode json){
        String melliCode = json.get("melliCode").asText();
        String phone = json.get("phone").asText();

        Patient p = patientDao.findByMelliCode(melliCode);
        p.setPhone(phone);
        patientDao.save(p);
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


}
