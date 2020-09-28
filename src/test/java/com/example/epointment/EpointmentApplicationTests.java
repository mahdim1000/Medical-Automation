package com.example.epointment;

import com.example.epointment.common.Doctor;
import com.example.epointment.common.JalaliCalendar;
import com.example.epointment.config.DataConfiguration;
import com.example.epointment.model.DoctorDao;
import com.example.epointment.model.DoctorServiceDao;
import com.example.epointment.model.PatientDao;
import com.example.epointment.model.TurnDao;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EpointmentApplicationTests {

    @Autowired
    TurnDao turnDao;
    @Autowired
    DoctorServiceDao doctorServiceDao;
    @Autowired
    DoctorDao doctorDao;
    @Autowired
    ServletContext context;
    @Autowired
    PatientDao patientDao;

    @Test
    public void contextLoads() {
    }

    @Test
    public void garigoranTime(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH))).toString();

    }

    @Test
    public void findBestDoctors(){
        doctorServiceDao.findClinicServices(21L);
    }

    @Test
    public void imgageTest(){
        Doctor doctor = doctorDao.findById(72L);

//        String directory = DataConfiguration.resources_path+"images\\male-avatar.png";
//        File file = new File(directory);
//        byte[] bFile = new byte[(int) file.length()];
//
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            //convert file into array of bytes
//            fileInputStream.read(bFile);
//            fileInputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        doctor.setImage(bFile);
//        doctorDao.save(doctor);
        byte[] bAvatar = doctor.getImage();
        int x=0;
        //        doctorDao.setImageToAll(bAvatar);
//        try{
//            FileOutputStream fos = new FileOutputStream("C:\\tabib\\test.png");
//            fos.write(bAvatar);
//            fos.close();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
    }

    @Test
    public void setImageToPatients(){
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
        patientDao.setImageToAll(bFile);
    }

}
