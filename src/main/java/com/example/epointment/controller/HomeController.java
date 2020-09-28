package com.example.epointment.controller;

import com.example.epointment.common.*;
import com.example.epointment.config.DataConfiguration;
import com.example.epointment.model.*;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
//import com.google.gson.JsonObject;
import com.kavenegar.sdk.KavenegarApi;
import com.kavenegar.sdk.excepctions.ApiException;
import com.kavenegar.sdk.excepctions.HttpException;

import com.kavenegar.sdk.models.SendResult;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


import javax.net.ssl.HttpsURLConnection;
import javax.persistence.Column;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.util.*;

import static org.springframework.http.HttpHeaders.USER_AGENT;

@RestController
@CrossOrigin
@RequestMapping(value = "/api")
public class HomeController  {
    @Autowired
    ServletContext servletContext;
    @Autowired
    CityDao cityDao;
    @Autowired
    ClinicDao clinicDao;
    @Autowired
    DoctorDao doctorDao;
    @Autowired
    DoctorInsuranceDao doctorInsuranceDao;
    @Autowired
    DoctorServiceDao doctorServiceDao;
    @Autowired
    EducationDao educationDao;
    @Autowired
    ExperticeDao experticeDao;
    @Autowired
    InsuranceDao insuranceDao;
    @Autowired
    OfficeDao officeDao;
    @Autowired
    PatientDao patientDao;
    @Autowired
    ServiceDao serviceDao;
    @Autowired
    TurnDao turnDao;
    @Autowired
    TurnServiceDao turnServiceDao;
    @Autowired
    ClinicOfficeDao clinicOfficeDao;
    @Autowired
    UserDao userDao;

    @PostMapping(value = "/verfiyCaptcha")
    public Boolean verfiyCaptcha(@RequestBody BaseJsonNode json){
        String token = json.get("recaptcha_token").asText();
        JSONObject respose = null;
        try {
           respose = sendPost(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(respose != null)
            return (Boolean) respose.get("success");
        else
            return false;
    }

    @Secured({"ROLE_PATIENT", "ROLE_DOCTOR", "ROLE_CLINIC"})
    @PostMapping(value="/changePassword")
    public void changePassword(@RequestBody BaseJsonNode json){
        String mc = json.get("melliCode").asText();
        String pass = json.get("pass").asText();
        if(!pass.equals("")){
            Users user = userDao.findByMelliCode(mc);
            user.setPassword(DataConfiguration.encode_pass(pass));
            userDao.save(user);
        }
    }

    @PostMapping(value="/forgotPassword")
    public void forgotPassword(@RequestBody BaseJsonNode json){
        String mc = json.get("melliCode").asText();
        String pass = json.get("pass").asText();
        if(!pass.equals("")){
            Users user = userDao.findByMelliCode(mc);
            if(user != null) {
                user.setPassword(DataConfiguration.encode_pass(pass));
                userDao.save(user);
            }
        }
    }

    @PostMapping(value = "/getAllDoctorsSearch")
    public List<HashMap> getAllDoctorsSearch(@RequestBody BaseJsonNode json){
        String lastname = "";
        if( !json.get("lastname").asText().equals("all")){
            lastname = json.get("lastname").asText();
        }
        String city = "";
        if(!json.get("city").asText().equals("all")){
            city = cityDao.findById(json.get("city").asLong()).getName();
        }
        String expertice = "";
        if(!json.get("expertice").asText().equals("all")){
            expertice = experticeDao.findExperticeById(json.get("expertice").asLong()).getName();
        }

        List<HashMap> list = new ArrayList<>();
        for(Doctor d:doctorDao.findLikeLastNameAndExperticeNameAndCity(lastname, expertice, city)){
            HashMap<String, Object> map = new HashMap();
            map.put("bio", d.getBio());
            map.put("image", d.getImage());
            map.put("id", d.getId());
            map.put("name", d.getName()+" "+d.getLastname());
            map.put("expertice", d.getExpertice().getName());
            map.put("education", d.getEducation().getName());
            map.put("turnNum", turnDao.countTurnsByDoctorId(d.getId()));
            List<Object> offices = new ArrayList<>();
            for(Office o:officeDao.findByDoctorId(d.getId())){
                HashMap<String, String> h = new HashMap();
                h.put("type","مطب");
                h.put("city", d.getCity().getName());
                h.put("address", o.getAddress());
                h.put("phone", o.getNumber());
                offices.add(h);
            }
            for(ClinicOffice co:clinicOfficeDao.findByDoctorId(d.getId())){
                HashMap<String, String> h = new HashMap();
                h.put("type","کلینیک");
                h.put("city", d.getCity().getName());
                if(co.getClinic() != null)
                    h.put("address", co.getClinic().getAddress());
                else
                    h.put("address", "ادرسی وجود ندارد");
                h.put("phone", co.getPhone());
                offices.add(h);
            }
            map.put("offices", offices);
            list.add(map);
        }
        return list;
    }

    @PostMapping(value = "/getAllDoctorsSearchBySearch")
    public List<HashMap> getAllDoctorsSearchBySearch(@RequestBody BaseJsonNode json){

        String name = json.get("name").asText();
        Boolean gender = null;
        if(json.get("gender").asLong() != -1)
            gender = json.get("gender").asBoolean();
        String expertice = "";
        if(json.get("expertice").asLong() != -1)
            expertice = experticeDao.findExperticeById(json.get("expertice").asLong()).getName();

        String education = "";
        if(json.get("education").asLong() != -1)
            education = educationDao.findEducationById(json.get("education").asLong()).getName();
        List<Doctor> doctors = null;
        List<HashMap> list = new ArrayList<>();
        if(gender != null)
            doctors = doctorDao.findLikeLastNameAndByExperticeIdAndEducationIdAndGender(name, expertice, education, gender);
        else
            doctors = doctorDao.findLikeLastNameAndByExperticeIdAndEducationId(name, expertice, education);

        for(Doctor d:doctors){
            HashMap<String, Object> map = new HashMap();
            map.put("bio", d.getBio());
            map.put("image", d.getImage());
            map.put("id", d.getId());
            map.put("name", d.getName()+" "+d.getLastname());
            map.put("expertice", d.getExpertice().getName());
            map.put("education", d.getEducation().getName());
            map.put("turnNum", turnDao.countTurnsByDoctorId(d.getId()));
            List<Object> offices = new ArrayList<>();
            for(Office o:officeDao.findByDoctorId(d.getId())){
                HashMap<String, String> h = new HashMap();
                h.put("type","مطب");
                h.put("city", d.getCity().getName());
                h.put("address", o.getAddress());
                h.put("phone", o.getNumber());
                offices.add(h);
            }
            for(ClinicOffice co:clinicOfficeDao.findByDoctorId(d.getId())){
                HashMap<String, String> h = new HashMap();
                h.put("type","کلینیک");
                h.put("city", d.getCity().getName());
                if(co.getClinic() != null)
                    h.put("address", co.getClinic().getAddress());
                else
                    h.put("address", "ادرسی وجود ندارد");
                h.put("phone", co.getPhone());
                offices.add(h);
            }
            map.put("offices", offices);
            list.add(map);
        }
        return list;
    }

    //-------------------------------------------- doctors
    @GetMapping(value = "/get-doctors")
    public List<Doctor> getDoctors(){
        List<Doctor> ds = new ArrayList<>();
        for(Doctor d:doctorDao.findAll()){
            Doctor doctor = new Doctor();
            doctor.setId(d.getId());
            doctor.setPhone(d.getPhone());
            doctor.setMelliCode(d.getMelliCode());
            doctor.setCity(d.getCity());
            doctor.setBio(d.getBio());
            doctor.setEducation(d.getEducation());
            doctor.setExpertice(d.getExpertice());
            doctor.setName(d.getName());
            doctor.setLastname(d.getLastname());
            doctor.setGender(d.getGender());
            doctor.setDoctorCode(d.getDoctorCode());
            ds.add(doctor);
        }

        return ds;
//        return doctorDao.findAll();
    }

    @PostMapping(value = "/registerPatient")
    public void registerPatient(@RequestBody BaseJsonNode json){

        String name = json.get("name").asText();
        String lastname = json.get("lastname").asText();
        String melliCode = json.get("nationalNumber").asText();
        String phone = json.get("phone").asText();
        Boolean gender = json.get("gender").asBoolean();
        String password = json.get("password").asText();
//        Long cityId = json.get("city").asLong();

//        String directory = DataConfiguration.resources_path+"images\\patient.png";
        String directory = DataConfiguration.getResources_path()+"images\\patient.png";
        File file = new File(directory);
        byte[] bFile = new byte[(int) file.length()];

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            //convert file into array of bytes
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Patient patient = new Patient();
        patient.setGender(gender);
        patient.setName(name);
        patient.setLastname(lastname);
        patient.setMelliCode(melliCode);
        patient.setPhone(phone);
//        patient.setDoctor(null);  recent change
        patient.setImage(bFile);

        String pass = DataConfiguration.encode_pass(password);
        patient.setRole("ROLE_PATIENT");
        patient.setPassword(pass);

        patientDao.save(patient);
    }

    @PostMapping(value = "/registerDoctor")
    public Boolean registerDoctor(@RequestBody BaseJsonNode json){
        Clinic clinic = new Clinic();



        Doctor doctor = new Doctor();
        doctor.setName(json.get("name").asText());
        doctor.setLastname(json.get("lastname").asText());
        doctor.setMelliCode(json.get("melliCode").asText());
        String pass = DataConfiguration.encode_pass(json.get("Password").asText());
        doctor.setPassword(pass);
        doctor.setPhone(json.get("phone").asText());
        doctor.setDoctorCode(json.get("doctorCode").asText());
        doctor.setEducation(new Education().setId(json.get("education").asLong()));
        doctor.setExpertice(new Expertice().setId(json.get("expertice").asLong()));
        doctor.setCity(new City().setId(json.get("city").asLong()));
        String directory = DataConfiguration.getResources_path()+"images\\male-avatar.png";
        File file = new File(directory);
        byte[] bFile = new byte[(int) file.length()];

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        doctor.setImage(bFile);
        doctor.setRole("ROLE_DOCTOR");
        doctor.setGender(json.get("gender").asBoolean());
        Doctor savedDoctor = doctorDao.save(doctor);

        String address = json.get("address").asText();
        if(!(address.equals(""))){
            Office office = new Office();
            office.setDoctor(savedDoctor);
            office.setAddress(address);
            officeDao.save(office);
        }

        if(json.get("clinicNumber").asText().equals(""))
            return true;
        else{
            clinic.setId(json.get("clinicNumber").asLong());
            ClinicOffice clinicOffice = new ClinicOffice();
            clinicOffice.setClinic(clinic);
            clinicOffice.setDoctor(savedDoctor);
            clinicOfficeDao.save(clinicOffice);
        }
        return true;

    }

    @GetMapping(value = "/getBestDoctorsInfo")
    public List<HashMap> getBestDoctorsInfo(){
        /* name education expertice city */
        List<Long> bestDoctorsId = turnDao.findIdOfBestDoctors();
        List<HashMap> list = new ArrayList<>();
        for(Long id:bestDoctorsId){
            HashMap<String, Object> map = new HashMap<>();
            Doctor d = doctorDao.findById(id);
            map.put("image", d.getImage());
            map.put("name", d.getName()+" "+d.getLastname());
            map.put("education", d.getEducation().getName());
            map.put("expertice", d.getExpertice().getName());
            map.put("city", d.getCity().getName());

            list.add(map);
        }
        return list;
    }


    @PostMapping(value = "/is-registered")
    public Boolean isMelliCodeRegistered(@RequestBody Patient patient){
        Users patients = userDao.findByMelliCode(patient.getMelliCode());

        if(patients != null)
            return true;
        else
            return false;
    }

    @PostMapping(value = "/check-doctor-register")
    public Boolean checkDoctorIsRegistered(@RequestBody BaseJsonNode json){
        int x = 1;
        String mc = json.get("melliCode").asText();
        Doctor ds =  doctorDao.findByMelliCode(mc);
        if(ds != null){
            return true;
        }else
            return false;
    }

    @PostMapping(value = "/doctorCodeExist")
    public Boolean doctorCodeIsExist(@RequestBody BaseJsonNode json){
        String dc = json.asText();
        List<Doctor> ds = doctorDao.findByDoctorCode(dc);
        if(ds.size() > 0)
            return true;
        else
            return false;
    }

    @PostMapping(value = "/doctorMelliCodeIsExist")
    public Boolean doctorMelliCodeIsExist(@RequestBody BaseJsonNode json){
        String mc = json.asText();
        Users ds =  userDao.findByMelliCode(mc);
        if(ds != null)
            return true;
        else
            return false;
    }

    @PostMapping(value = "/doctorPhoneIsExist")
    public Boolean doctorPhoneIsExist(@RequestBody BaseJsonNode json){
        String p = json.asText();
        List<Doctor> ds = doctorDao.findByPhone(p);
        if(ds.size() > 0)
            return true;
        else
            return false;
    }

    @GetMapping(value = "/countDoctors")
    public Long countDoctors(){
        return doctorDao.countDoctors();
    }

    //------------------------------------------------- turns

    @GetMapping(value = "/countTurns")
    public Long countTurns(){
        return turnDao.countTurns();
    }

    //-------------------------------------------------- common
    @PostMapping(value = "/sendRegisterCodeSMS")
    public String sendRegisterCodeSMS(@RequestBody BaseJsonNode json, ServletRequest request) throws UnirestException {
//        String pId = json.get("id").asText();
        String phone = json.get("phone").asText();
        SendResult msg = null;
        String id = "";
        String rand = "";
        for(int i=0; i < 6; i++){
            int random = new Random().nextInt(9);
            rand += String.valueOf(random);
            int randomId = new Random().nextInt(9);
            id += String.valueOf(randomId);
        }
        try {
            KavenegarApi api= new KavenegarApi(DataConfiguration.smsApiKey);
            SendResult Result = api.send(null, phone, "کد تایید در طبیب یار: "+rand);
            msg = Result;
        }
        catch (HttpException ex)
        { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("HttpException  : " + ex.getMessage());
        }
        catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("ApiException : " + ex.getMessage());
        }
        Timer timer = new Timer();
        String finalId = id;
        TimerTask ts = new TimerTask() {
            @Override
            public void run() {
                servletContext.removeAttribute(finalId);
                System.out.println(finalId+" : remove ed ******");
                timer.cancel();
            }
        };

        timer.schedule(ts, 70000, 1);


            servletContext.setAttribute(id, rand);
//            servletContext.addListener();
            System.out.println("id: "+id+" , code: "+rand);
            return id;
//        }
//
//        else
//            return null;
    }

    @PostMapping(value = "/sendRegisterCodeSMSByMelliCode")
    public String sendRegisterCodeSMSByMelliCode(@RequestBody BaseJsonNode json, ServletRequest request) throws UnirestException {
//        String pId = json.get("id").asText();
        String phone = userDao.findByMelliCode(json.get("melliCode").asText()).getPhone();
        SendResult msg = null;
        String id = "";
        String rand = "";
        for(int i=0; i < 6; i++){
            int random = new Random().nextInt(9);
            rand += String.valueOf(random);
            int randomId = new Random().nextInt(9);
            id += String.valueOf(randomId);
        }
        try {
            KavenegarApi api= new KavenegarApi(DataConfiguration.smsApiKey);
            SendResult Result = api.send(null, phone, "کد تایید در طبیب یار: "+rand);
            msg = Result;
        }
        catch (HttpException ex)
        { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("HttpException  : " + ex.getMessage());
        }
        catch (ApiException ex) { // در صورتی که خروجی وب سرویس 200 نباشد این خطارخ می دهد.
            System.out.print("ApiException : " + ex.getMessage());
        }
        Timer timer = new Timer();
        String finalId = id;
        TimerTask ts = new TimerTask() {
            @Override
            public void run() {
                servletContext.removeAttribute(finalId);
                System.out.println(finalId+" : remove ed ******");
                timer.cancel();
            }
        };

        timer.schedule(ts, 70000, 1);


        servletContext.setAttribute(id, rand);
//            servletContext.addListener();
        System.out.println("id: "+id+" , code: "+rand);
        return id;
//        }
//
//        else
//            return null;
    }

    @PostMapping("/checkRegisterCodeSMS")
    public boolean checkRegisterCodeSMS(@RequestBody BaseJsonNode json){
        String code = json.get("code").asText();
        String id = json.get("id").asText();

        String trueCode = (String) servletContext.getAttribute(id);
        System.out.println("id: "+id+" , code: "+trueCode);
        if(id != null && trueCode != null && trueCode.equals(code)){
            return true;
        }else
            return false;
    }

    //----------------------------------------------------------------- expertice
    @PostMapping(value = "/getExperticeLikeName")
    public List<Expertice> getExperticeLikeName(@RequestBody BaseJsonNode json){
        String name = json.get("name").asText();
        if(name.equals("") || name.equals(" "))
            return experticeDao.getTenTopExpertices();

        List<Expertice> expertices =  experticeDao.getLikeName(name);
        for(Expertice e:expertices){
            System.out.println(e.getName());
        }
        return expertices;
    }

    @GetMapping(value = "/getAllExpertice")
    public List<Expertice> getAllExpertice(){
        return experticeDao.findAll();
    }

    @GetMapping(value = "/getAllExpertices")
    public List<Expertice> getAllExpertices(){
        return experticeDao.findAll();
    }

    @GetMapping(value = "/getTenTopExpertices")
    public List<Expertice> getTenTopExpertices(){
        return experticeDao.getTenTopExpertices();
    }

    @PostMapping(value = "/findExperticeById")
    public Expertice findExperticeById(@RequestBody BaseJsonNode json){
        long id = json.get("id").asLong();
        return experticeDao.findExperticeById(id);
    }

    //---------------------------------------------------------------- Education
    @PostMapping(value = "/getEducationLikeName")
    public List<Education> getEducationLikeName(@RequestBody BaseJsonNode json){
        String name = json.get("name").asText();
        if(name.equals("") || name.equals(" "))
            return educationDao.getTenTopEducation();

        List<Education> educations =  educationDao.getLikeName(name);
        for(Education e:educations){
            System.out.println(e.getName());
        }
        return educations;
    }

    @GetMapping(value = "/getAllEducations")
    public List<Education> getAllEducations(){
        return educationDao.findAll();
    }

    @GetMapping(value = "/getTenTopEducations")
    public List<Education> getTenTopEducations(){
        return educationDao.getTenTopEducation();
    }

    @PostMapping(value = "/findEducationById")
    public Education findEducationById(@RequestBody BaseJsonNode json){
        long id = json.get("id").asLong();
        return educationDao.findEducationById(id);
    }


    //----------------------------------------------------------------- city

    @PostMapping(value = "/getCityLikeName")
    public List<City> getCityLikeName(@RequestBody BaseJsonNode json){
        String name = json.get("name").asText();
        if(name.equals("") || name.equals(" "))
            return cityDao.getTenTopCity();

        List<City> cities =  cityDao.getLikeName(name);
        for(City e:cities){
            System.out.println(e.getName());
        }
        return cities;
    }

    @GetMapping(value = "/getAllCitys")
    public List<City> getAllCitys(){
        return cityDao.findAll();
    }

    @GetMapping(value = "/getTenTopCitys")
    public List<City> getTenTopCity(){
        return cityDao.getTenTopCity();
    }

    @PostMapping(value = "/findCityById")
    public City findCityById(@RequestBody BaseJsonNode json){
        long id = json.get("id").asLong();
        return cityDao.findById(id);
    }

    //---------------------------------------------------- Clinic

    @PostMapping(value = "/getClinicLikeName")
    public List<Clinic> getClinicLikeName(@RequestBody BaseJsonNode json){
        String name = json.get("name").asText();
        if(name.equals("") || name.equals(" "))
            return clinicDao.getTenTopClinic();

        List<Clinic> clinics =  clinicDao.getLikeName(name);
        for(Clinic e:clinics){
            System.out.println(e.getName());
        }
        return clinics;
    }

    @GetMapping(value = "/getAllClinics")
    public List<Clinic> getAllClinics(){
        return clinicDao.findAll();
    }

    @GetMapping(value = "/getTenTopClinics")
    public List<Clinic> getTenTopClinic(){
        return clinicDao.getTenTopClinic();
    }

    @PostMapping(value = "/clinicPhoneIsExists")
    public boolean patientPhoneIsExists(@RequestBody BaseJsonNode json) {
        String phone = json.get("phone").asText();
        Clinic clinic = clinicDao.findByPhone(phone);
        if (clinic != null)
            return true;
        else
            return false;
    }

    @PostMapping(value = "/clinicMelliCodeIsExists")
    public boolean clinicMelliCodeIsExists(@RequestBody BaseJsonNode json) {
        String mc = json.get("melliCode").asText();
        Users user = userDao.findByMelliCode(mc);
        if (user != null)
            return true;
        else
            return false;
    }

    @PostMapping(value = "/clinicNumberIsExists")
    public boolean clinicNumberIsExists(@RequestBody BaseJsonNode json) {
        String cn = json.get("clinicNumber").asText();
        Clinic clinic = clinicDao.findByClinicNumber(cn);
        if (clinic != null)
            return true;
        else
            return false;
    }

    @PostMapping(value = "/registerClinic")
    public void registerClinic(@RequestBody BaseJsonNode json){

        Clinic clinic = new Clinic();
        clinic.setName(json.get("name").asText());
        clinic.setPhone(json.get("mobile").asText());
        clinic.setClinicPhone(json.get("phone").asText());
        clinic.setAddress(json.get("address").asText());
        clinic.setMelliCode(json.get("melliCode").asText());
        clinic.setClinicNumber(json.get("clinicNumber").asText());
        clinic.setCity(cityDao.findById(json.get("city").asLong()));
        String pass = DataConfiguration.encode_pass(json.get("password").asText());
        clinic.setPassword(pass);
        clinic.setRole("ROLE_CLINIC");

        clinicDao.save(clinic);
    }
//    @PostMapping(value = "/GenerateCode")
//    public String generate(@RequestBody BaseJsonNode json) {
//        String code = json.get("code").asText();
//        String melliCode = json.get("melliCde").asText();

//        String phone = json.get("phone").asText();

//        HashMap<String, String> map = new HashMap<>();
//        SendResult msg = null;
//        String id = "";
//        String rand = "";
//        for (int i = 0; i < 6; i++) {
//            int random = new Random().nextInt(9);
//            rand += String.valueOf(random);
//            int randomId = new Random().nextInt(9);
//            id += String.valueOf(randomId);
//        }
//        servletContext.setAttribute("1", rand);
//        map.put("id", id);
//        map.put("code", rand);

//        Timer timer = new Timer();
//        String finalId = id;
//        TimerTask ts = new TimerTask() {
//            @Override
//            public void run() {
//                servletContext.removeAttribute(finalId);
//                System.out.println(finalId + " : remove ed ******");
//                timer.cancel();
//            }

//    return rand;
//    }

    private JSONObject sendPost(String token) throws Exception {

        String url = "https://www.google.com/recaptcha/api/siteverify";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "secret=6LdRrLQUAAAAAAc02euAN394B2CPhJo4Gng01chK=&response="+token;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

        JSONObject json = new JSONObject(response.toString());

        return json;

    }



}
