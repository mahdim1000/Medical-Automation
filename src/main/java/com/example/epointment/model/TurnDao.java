package com.example.epointment.model;

import com.example.epointment.common.Clinic;
import com.example.epointment.common.Doctor;
import com.example.epointment.common.Patient;
import com.example.epointment.common.Turn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class TurnDao {
    @PersistenceContext
    EntityManager entityManager;

//    @Transactional
//    public List<Turn> findAll(){
//        return entityManager.createQuery("from Turn").getResultList();
//    }

    @Transactional
    public Turn save(Turn turn){
        return entityManager.merge(turn);
    }

    @Transactional
    public Long countTurns(){
        return (Long) entityManager.createQuery("select count(t) from Turn as t").getSingleResult();
    }

    @Transactional
    public List<Turn> findByDateAndBetweenHours(Long dId, Date date, Time sTime, Time eTime){
       return entityManager.createQuery("from Turn t where t.doctor.id=:dId and t.date=:date and ((:sTime between t.startTime and t.endTime) or (:eTime between t.startTime and t.endTime))")
                .setParameter("dId", dId).setParameter("sTime", sTime).setParameter("eTime", eTime).setParameter("date",date).getResultList();
    }

    @Transactional
    public List<Turn> findByDoctor(Doctor doctor){
        return entityManager.createQuery("from Turn t where t.doctor=:d order by t.date desc")
                .setParameter("d", doctor).getResultList();
    }

    @Transactional
    public List<Turn> findByDoctorAndDate(Doctor doctor, Date date){
        return entityManager.createQuery("from Turn t where t.doctor=:d and t.date=:date order by t.startTime asc")
                .setParameter("d", doctor).setParameter("date", date).getResultList();
    }

    @Transactional
    public List<Patient> findPatientsByDoctor(Doctor doctor){
        return entityManager.createQuery("select distinct t.patient from Turn t where t.doctor=:d")
                .setParameter("d", doctor).getResultList();
    }

    @Transactional
    public List<Patient> findPatientsByClinic(Clinic clinic){
        return entityManager.createQuery("select distinct t.patient from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic=:c")
                .setParameter("c", clinic).getResultList();
    }

    @Transactional
    public List<Patient> findPatientsByDoctorAndPatient(Doctor doctor, Patient patient){
        return entityManager.createQuery("select distinct t.patient from Turn t where t.doctor=:d and t.patient.lastname like :name and t.patient.melliCode like :mc and t.patient.gender=:g")
                .setParameter("d", doctor).setParameter("name", "%"+patient.getLastname()+"%").setParameter("mc", patient.getMelliCode()+"%").setParameter("g", patient.getGender())
                .getResultList();
    }

    @Transactional
    public List<Patient> findPatientsByClinicAndPatient(Clinic clinic, Patient patient){
        return entityManager.createQuery("select distinct t.patient from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic=:c and t.patient.lastname like :name and t.patient.melliCode like :mc and t.patient.gender=:g")
                .setParameter("c", clinic).setParameter("name", "%"+patient.getLastname()+"%").setParameter("mc", patient.getMelliCode()+"%").setParameter("g", patient.getGender())
                .getResultList();
    }

    @Transactional
    public List<Patient> findPatientsByDoctorAndPatientAndTurns(Doctor doctor, Patient patient, List<Turn> turns){
        return entityManager.createQuery("select distinct t.patient from Turn t where t.doctor=:d and t.patient.lastname like :name and t.patient.melliCode like :mc and t.patient.gender=:g and t in (:tList)")
                .setParameter("d", doctor).setParameter("name", "%"+patient.getLastname()+"%").setParameter("mc", patient.getMelliCode()+"%").setParameter("g", patient.getGender())
                .setParameter("tList", turns).getResultList();
    }

    @Transactional
    public List<Patient> findPatientsByClinicAndPatientAndTurns(Clinic clinic, Patient patient, List<Turn> turns){
        return entityManager.createQuery("select distinct t.patient from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic=:c and t.patient.lastname like :name and t.patient.melliCode like :mc and t.patient.gender=:g and t in (:tList)")
                .setParameter("c", clinic).setParameter("name", "%"+patient.getLastname()+"%").setParameter("mc", patient.getMelliCode()+"%").setParameter("g", patient.getGender())
                .setParameter("tList", turns).getResultList();
    }

//    @Transactional
//    public List<Patient> findPatientsByDoctorAndPatientService(Doctor doctor, Patient patient){
//        return entityManager.createQuery("select distinct t.patient from Turn t where t.doctor=:d and t.patient.melliCode like :p")
//                .setParameter("d", doctor).setParameter("p", "%"+patient.getMelliCode()+"%").getResultList();
//    }

    @Transactional
    public Turn findById(Long Id){
        List<Turn> turns = entityManager.createQuery("from Turn t where t.id=:id")
                .setParameter("id", Id).getResultList();

        if(turns.size()>0)
            return turns.get(0);
        else
            return null;
    }

    @Transactional
    public Turn findLastTurnByPatient(Patient patient){
        List<Turn> turns = entityManager.createQuery("from Turn t where t.patient=:p order by t.date desc, t.startTime desc")
                .setParameter("p", patient).setMaxResults(1).getResultList();
        if(turns.size()>0)
            return turns.get(0);
        else
            return null;
    }

    @Transactional
    public List<Turn> findByPatient(Patient p){
        return entityManager.createQuery("from Turn t where t.patient=:p order by t.date desc")
                .setParameter("p",p).getResultList();
    }

    @Transactional
    public List<Turn> findByClinicId(Long clinicId){
        return entityManager.createQuery("select t from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic.id=:cId ")
            .setParameter("cId", clinicId).getResultList();

//        return entityManager.createQuery("select t from Turn t").getResultList();
}

    @Transactional
    public Long countDoctorTurnBetweenDates(Long dId, Date date1, Date date2){

            return (Long) entityManager.createQuery("select count(t) from Turn t where t.doctor.id=:id and (t.date between :lastDate and :today) and t.status in ('0','1')")
                .setParameter("id",dId).setParameter("today", date1).setParameter("lastDate", date2).getSingleResult();
    }

    @Transactional
    public Long countDoctorOfClinicTurnBetweenDates(Long cId, Long dId, Date date1, Date date2){

        return (Long) entityManager.createQuery("select count(t) from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic.id=:cId and t.doctor.id=:id and (t.date between :lastDate and :today) and t.status in ('0','1')")
                .setParameter("id",dId).setParameter("today", date1).setParameter("lastDate", date2).setParameter("cId", cId).getSingleResult();
    }

    @Transactional
    public Double doctorOfClinicLast30IncomeById(Long cId, Long dId, Date date1, Date date2){

        return (Double) entityManager.createQuery("select sum(t.cost) from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic.id=:cId and t.cost is not null and t.doctor.id=:id and (t.date between :lastDate and :today)")
                .setParameter("id", dId).setParameter("today", date1).setParameter("lastDate", date2).setParameter("cId", cId).getSingleResult();
    }

    @Transactional
    public Double doctorLast30IncomeById(Long dId, Date date1, Date date2){

        return (Double) entityManager.createQuery("select sum(t.cost) from Turn t where t.cost is not null and t.doctor.id=:id and (t.date between :lastDate and :today)")
                .setParameter("id", dId).setParameter("today", date1).setParameter("lastDate", date2).getSingleResult();
    }

    @Transactional
    public List<Turn> findByDoctorIdAndBetweenDates(Long dId, Date s, Date e){
        return entityManager.createQuery("from Turn where doctor.id=:id and date between :s and :e order by date desc, startTime desc")
                .setParameter("id",dId).setParameter("s", s).setParameter("e",e).getResultList();
    }

    public List<Turn> findByClinicIdAndBetweenDates(Long cId, Date s, Date e){
        return entityManager.createQuery("from Turn where clinicOffice is not null and clinicOffice.clinic.id=:id and date between :s and :e order by date desc, startTime desc")
                .setParameter("id",cId).setParameter("s", s).setParameter("e",e).getResultList();
    }

    @Transactional
    public Long countPatientByDoctorIdAndBetweenDates(Long id, Date s, Date e){
        return (Long) entityManager.createQuery("select count(t) from Turn t where t.doctor.id=:id and t.date between :s and :e")
                .setParameter("s", s).setParameter("e", e).setParameter("id", id).getSingleResult();
    }

    @Transactional
    public Long countTurnByClinicIdAndBetweenDates(Long id, Date s, Date e){
        return (Long) entityManager.createQuery("select count(t) from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic.id=:id and t.date between :s and :e")
                .setParameter("s", s).setParameter("e", e).setParameter("id", id).getSingleResult();
    }

    @Transactional
    public Double findIncomeByDoctorIdAndBetweenDates(Long id, Date s, Date e){
        return (Double) entityManager.createQuery("select sum(t.cost) from Turn t where t.doctor.id=:id and t.date between :s and :e")
                .setParameter("id", id).setParameter("s", s).setParameter("e", e).getSingleResult();
    }

    @Transactional
    public Double findIncomeByClinicIdAndBetweenDates(Long id, Date s, Date e){
        return (Double) entityManager.createQuery("select sum(t.cost) from Turn t where t.clinicOffice.clinic is not null and t.clinicOffice.clinic.id=:id and t.date between :s and :e")
                .setParameter("id", id).setParameter("s", s).setParameter("e", e).getSingleResult();
    }

    @Transactional
    public Long findPatientsCountByDoctorIdAndDate(Long doctorId, Date date){
        return (Long) entityManager.createQuery("select count(t) from Turn t where t.doctor.id=:id and t.date=:date")
                .setParameter("id", doctorId).setParameter("date", date).getSingleResult();
    }

    @Transactional
    public Long findTurnCountByClinicIdAndDate(Long clinicId, Date date){
        return (Long) entityManager.createQuery("select count(t) from Turn t where t.clinicOffice.clinic is not null and t.clinicOffice.clinic.id=:id and t.date=:date")
                .setParameter("id", clinicId).setParameter("date", date).getSingleResult();
    }

    @Transactional
    public List<Turn> last3TurnsByDoctorId(Long dId){
        Date date = new Date();
        return entityManager.createQuery("from Turn where doctor.id=:id and date=:date order by startTime asc")
                .setParameter("id", dId).setParameter("date", date).setMaxResults(3).getResultList();
    }

    @Transactional
    public List<Turn> findLastSubmitedByDoctorId(Long doctorId){
        return entityManager.createQuery("from Turn where doctor.id=:id order by date desc, startTime desc")
                .setParameter("id", doctorId).setMaxResults(5).getResultList();
    }

    @Transactional
    public List<Long> findIdOfBestDoctors(){
       return entityManager.createQuery("select t.doctor.id from Turn t where t.doctor.id is not null group by t.doctor order by count(t) desc").setMaxResults(4).getResultList();
    }

    @Transactional
    public List<Long> findIdOfBestDoctorsByClinicId(Long id){
        return entityManager.createQuery("select t.doctor.id from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic.id=:cId and t.doctor.id is not null and t.clinicOffice.isEnable=true group by t.doctor order by count(t) desc")
                .setParameter("cId", id).setMaxResults(4).getResultList();
    }

    @Transactional
    public Long countTodayTurnByClinicId(Long cId){
       return (Long) entityManager.createQuery("select count(t) from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic is not null and t.clinicOffice.clinic.id=:id and t.date=:date")
                .setParameter("id", cId).setParameter("date", new Date()).getSingleResult();
    }

    @Transactional
    public Double findIncomeByClinicId(Long cId){
        return (Double) entityManager.createQuery("select sum(t.cost) from Turn t where t.clinicOffice is not null and t.clinicOffice.clinic is not null and t.clinicOffice.clinic.id=:id")
                .setParameter("id", cId).getSingleResult();
    }

    @Transactional
    public Long countTodayTurnByDoctorId(Long dId){
        return (Long) entityManager.createQuery("select count(t) from Turn t where t.doctor.id=:id and t.date=:date")
                .setParameter("id", dId).setParameter("date", new Date()).getSingleResult();
    }

    @Transactional
    public Double findIncomeByDoctorId(Long dId){
        return (Double) entityManager.createQuery("select sum(t.cost) from Turn t where t.doctor.id=:id")
                .setParameter("id", dId).getSingleResult();
    }

    @Transactional
    public Long countTurnsByDoctorId(Long dId){
        return (Long) entityManager.createQuery("select count(t) from Turn t where t.doctor.id=:id")
                .setParameter("id", dId).getSingleResult();
    }
}
