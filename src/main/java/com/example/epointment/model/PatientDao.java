package com.example.epointment.model;

import com.example.epointment.common.Patient;
import com.example.epointment.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class PatientDao {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    PatientRepository patientRepository;

    @Transactional
    public Patient save(Patient patient){
        return entityManager.merge(patient);
    }

    @Transactional
    public Patient findByMelliCode(String mc){
//        rPeturn patientRepository.findByMelliCode(mc);
        List<Patient> patients =  entityManager.createQuery("from Patient where melliCode = :mc").setParameter("mc", mc).getResultList();
        if(patients != null && patients.size() > 0){
            return patients.get(0);
        }
        else
            return null;
    }

    public Patient findById(Long id){
//        rPeturn patientRepository.findByMelliCode(mc);
        List<Patient> patients =  entityManager.createQuery("from Patient where id = :id").setParameter("id", id).getResultList();
        if(patients != null && patients.size() > 0){
            return patients.get(0);
        }
        else
            return null;
    }

    @Transactional
    public Patient findByPhone(String p){
        List<Patient> patients = entityManager.createQuery("from Patient where phone=:phone")
                .setParameter("phone", p).getResultList();
        if(patients != null &&  patients.size()> 0)
            return patients.get(0);
        else
            return null;
    }

    @Transactional
    public List<Patient> findLikeLastname(String lname){
        String lastname = lname+"%";
        return entityManager.createQuery("from Patient p where p.lastname like :lname")
                .setParameter("lname", lastname).getResultList();
    }

    @Transactional
    public List<Patient> findLikeMelliCode(String mc){
        String melliCode = mc+"%";
        return entityManager.createQuery("from Patient p where p.melliCode like :m")
                .setParameter("m", melliCode).getResultList();
    }

    //--- test
    @Transactional
    public void setImageToAll(byte[] image){
        entityManager.createQuery("update Patient p set p.image=:img")
                .setParameter("img", image).executeUpdate();
    }
}
