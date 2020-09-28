package com.example.epointment.model;

import com.example.epointment.common.Clinic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ClinicDao {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void save(Clinic clinic){
        entityManager.persist(clinic);
    }

    @Transactional
    public List<Clinic> findAll(){
        return entityManager.createQuery("from Clinic").getResultList();
    }

    @Transactional
    public List<Clinic> getTenTopClinic(){
        return entityManager.createQuery("from Clinic").setMaxResults(50).getResultList();
    }

    @Transactional
    public List<Clinic> getLikeName(String name){
        String filter = name+"%";
        return entityManager.createQuery("select e from Clinic e where e.name like :name")
                .setParameter("name", filter).getResultList();
    }

    @Transactional
    public Clinic findByMelliCode(String mc){
        List<Clinic> cs = entityManager.createQuery("from Clinic c where c.melliCode=:mc")
                .setParameter("mc", mc).getResultList();
        if(cs.size()>0)
            return cs.get(0);
        else
            return null;
    }

    @Transactional
    public Clinic findByPhone(String p){
        List<Clinic> clinics = entityManager.createQuery("from Clinic c where c.phone=:p")
                .setParameter("p", p).getResultList();
        if(clinics.size()>0)
            return clinics.get(0);
        else
            return null;
    }

    @Transactional
    public Clinic findByClinicNumber(String cn){
        List<Clinic> clinics = entityManager.createQuery("from Clinic c where c.clinicNumber=:cn")
                .setParameter("cn", cn).getResultList();
        if(clinics.size()>0)
            return clinics.get(0);
        else
            return null;
    }
}
