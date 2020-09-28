package com.example.epointment.model;

import com.example.epointment.common.Clinic;
import com.example.epointment.common.ClinicOffice;
import com.example.epointment.common.Doctor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ClinicOfficeDao {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public ClinicOffice save(ClinicOffice co){
        return entityManager.merge(co);
    }

    @Transactional
    public void delete(ClinicOffice co){
        entityManager.remove(co);
    }

    @Transactional
    public void deleteById(Long id){
//        entityManager.createQuery("delete from ClinicOffice c where c.id=:id")
//                .setParameter("id", id).executeUpdate();
        entityManager.createQuery("update ClinicOffice set isEnable=false where id=:id")
                .setParameter("id", id).executeUpdate();
    }

    @Transactional
    public ClinicOffice findById(Long id){
        List<ClinicOffice> co = entityManager.createQuery("from ClinicOffice c where c.id=:id and c.isEnable=true")
                .setParameter("id", id).getResultList();
        if(co.size()>0)
            return co.get(0);
        else
            return null;
    }

    @Transactional
    public List<Doctor> findDoctorsByClinicId(Long id){
        return entityManager.createQuery("select distinct t.doctor from ClinicOffice t where t.clinic.id=:cId and t.isEnable=true")
                .setParameter("cId", id).getResultList();
    }

    @Transactional
    public ClinicOffice findByClinicIdAndDoctorId(Long clinicId, Long doctorId){
        List<ClinicOffice> cos = entityManager.createQuery("from ClinicOffice co where co.clinic.id=:cId and co.doctor.id=:dId and co.isEnable=true")
                .setParameter("cId", clinicId).setParameter("dId", doctorId).getResultList();
        if(cos.size()>0) return cos.get(0);
        else return null;
    }

    @Transactional
    public List<Doctor> findDoctorsByClinicAndDoctor(Clinic c, Doctor d){
        return entityManager.createQuery("select distinct t.doctor from ClinicOffice t where t.isEnable=true and t.clinic=:clinic and t.doctor.lastname like :ln and t.doctor.melliCode like :mc and t.doctor.gender=:gender and t.doctor.doctorCode like :dc")
                .setParameter("clinic", c).setParameter("ln","%"+d.getLastname()+"%").setParameter("mc", d.getMelliCode()+"%").setParameter("gender", d.getGender()).setParameter("dc", d.getDoctorCode()+"%")
                .getResultList();
    }
    @Transactional
    public List<Doctor> findDoctorsByClinicAndDoctorWithoutGender(Clinic c, Doctor d){
        return entityManager.createQuery("select distinct t.doctor from ClinicOffice t where t.isEnable=true and t.clinic=:clinic and t.doctor.lastname like :ln and t.doctor.melliCode like :mc and t.doctor.doctorCode like :dc")
                .setParameter("clinic", c).setParameter("ln","%"+d.getLastname()+"%").setParameter("mc", d.getMelliCode()+"%").setParameter("dc", d.getDoctorCode()+"%")
                .getResultList();
    }
    @Transactional
    public ClinicOffice findExactByDoctorId(Clinic c, Doctor d){
        List<ClinicOffice> cos = entityManager.createQuery("from ClinicOffice co where co.doctor=:d and co.clinic=:c")
                .setParameter("d", d).setParameter("c", c).getResultList();

        if(cos.size()>0) return cos.get(0);
        else return null;
    }

    @Transactional
    public List<ClinicOffice> findByDoctorId(Long doctorId){
        return entityManager.createQuery("from ClinicOffice where doctor.id=:id")
                .setParameter("id", doctorId).getResultList();
    }

}
