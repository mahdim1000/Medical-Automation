package com.example.epointment.controller;


import com.example.epointment.common.Doctor;
import com.example.epointment.common.Patient;
import com.example.epointment.common.Turn;
import com.example.epointment.common.TurnFiles;
import com.example.epointment.config.DataConfiguration;
import com.example.epointment.model.DoctorDao;
import com.example.epointment.model.PatientDao;
import com.example.epointment.model.TurnDao;
import com.example.epointment.model.TurnFilesDao;
import com.fasterxml.jackson.databind.node.BaseJsonNode;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/upload")
@Secured({"ROLE_DOCTOR", "ROLE_PATIENT", "ROLE_CLINIC"})
public class RestUploadController implements Serializable{
    private final Logger logger = LoggerFactory.getLogger(RestUploadController.class);
    @Autowired
    TurnFilesDao turnFilesDao;
    @Autowired
    TurnDao turnDao;
    @Autowired
    ServletContext context;
    @Autowired
    DoctorDao doctorDao;
    @Autowired
    PatientDao patientDao;
    // 3.1.2 Multiple file upload
    @PostMapping("/multi")
    public ResponseEntity  handleFileUpload(@RequestParam("file") MultipartFile file , @RequestParam Long turnId) {

        if(file != null){
//            String directoryName = DataConfiguration.uploads_path+turnId+"\\";
            String directoryName = DataConfiguration.getUploads_path()+turnId+"\\";
//        String directoryName = "C:\\tabib\\";
//        String fileName = id + getTimeStamp() + ".txt";

            File directory = new File(directoryName);
            if (! directory.exists()){
                directory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }

            try {
                System.out.printf("File name=%s, size=%s\n", file.getOriginalFilename(),file.getSize());
                TurnFiles tf = new TurnFiles();
                tf.setName(file.getOriginalFilename());
                tf.setTurn(turnDao.findById(turnId));
                //creating a new file in some local directory
                File fileToSave = new File(directoryName + file.getOriginalFilename());
                //copy file content from received file to new local file
                file.transferTo(fileToSave);
                turnFilesDao.save(tf);
            } catch (IOException ioe) {
                //if something went bad, we need to inform client about it
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            //everything was OK, return HTTP OK status (200) to the client
            return ResponseEntity.ok().build();
        }
        return null;
    }

    @PostMapping(value = "/getAllFilesName")
    public List<HashMap<String, Object>> getAllFilesName(@RequestBody BaseJsonNode json){
        Long turnId = json.get("turnId").asLong();
        Turn turn = turnDao.findById(turnId);
        List<TurnFiles> tfs = turnFilesDao.findAllByTurn(turn);
        List<HashMap<String, Object>> list = new ArrayList<>();
        for(TurnFiles tf:tfs){
            HashMap<String, Object> h = new HashMap<>();
            h.put("id", tf.getId());
            h.put("name", tf.getName());
            list.add(h);
        }
        return list;
    }


    @PostMapping(value = "/getTurnFile")
    public String  getFilesByTurn(@RequestBody BaseJsonNode json){
        Long turnId = json.get("turnId").asLong();
        String fileName = json.get("fileName").asText();

        String directoryName = "";
        boolean check = true;
        String rand="";
        while (check){
            rand = "tabib";
            for(int i=0; i < 20; i++){
                int random = new Random().nextInt(9);
                rand += String.valueOf(random);
            }

            directoryName = DataConfiguration.getStatic_path()+"temp\\"+rand+"\\";
            File directory = new File(directoryName);
            if ( directory.exists()){
                continue;
            }

            directory.mkdirs();
            check = false;
        }

        Path sourceDirectory = Paths.get(DataConfiguration.getUploads_path()+turnId+"\\"+fileName);
        Path targetDirectory = Paths.get(directoryName+fileName);

        //copy source to target using Files Class
        try {
            Files.copy(sourceDirectory, targetDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer();
        String finalDirectoryName = directoryName;
        TimerTask ts = new TimerTask() {
            @Override
            public void run() {
                System.out.println("file removed...");
                File f = new File(finalDirectoryName);
                if(f.exists()){
                    String[]entries = f.list();
                    for(String s: entries){
                        File currentFile = new File(f.getPath(),s);
                        currentFile.delete();
                    }
                    f.delete();
                }
                timer.cancel();
            }
        };
        timer.schedule(ts, 1800000, 1);//30 minues


        return DataConfiguration.domain+"temp/"+rand+"/"+fileName;
    }

    @PostMapping(value = "/removeFile")
    public void removeFile(@RequestBody BaseJsonNode json){
        Long turnId = json.get("turnId").asLong();
        Long fileId = json.get("fileId").asLong();
        String fileName = json.get("fileName").asText();

//        String uploadDir = DataConfiguration.uploads_path+turnId+"\\"+fileName;
        String uploadDir = DataConfiguration.getUploads_path()+turnId+"\\"+fileName;
//        String tempDir = DataConfiguration.static_path+"temp\\"+turnId+"\\"+fileName;
        String tempDir = DataConfiguration.getStatic_path()+"temp\\"+turnId+"\\"+fileName;
        File uploadFile = new File(uploadDir);
        File tempFile = new File(tempDir);
        if(uploadFile.exists()){
            uploadFile.delete();
        }
        if(tempFile.exists()){
            tempFile.delete();
        }


//        TurnFiles tf = turnFilesDao.findById(fileId);
        turnFilesDao.remove(fileId);
    }

    @PostMapping(value = "/changeDoctorPicture")
    public void changeDoctorPicture(@RequestParam("file") MultipartFile file , @RequestParam String doctorMelliCode){

        Doctor doctor = doctorDao.findByMelliCode(doctorMelliCode);
        byte[] bFile = null;

        try {
            bFile = file.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        doctor.setImage(bFile);
        doctorDao.save(doctor);
    }

    @PostMapping(value = "/changePatientPicture")
    public void changePatientPicture(@RequestParam("file") MultipartFile file , @RequestParam String MelliCode){

        Patient patient = patientDao.findByMelliCode(MelliCode);
        byte[] bFile = null;

        try {
            bFile = file.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }

        patient.setImage(bFile);
        patientDao.save(patient);
    }

}
