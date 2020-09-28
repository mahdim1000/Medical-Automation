package com.example.epointment.model;

import com.example.epointment.common.ClinicOffice;
import com.example.epointment.common.Office;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class OfficeDao {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Office save(Office office){
        return entityManager.merge(office);
    }

    @Transactional
    public void delete(Office o){
        entityManager.remove(o);
    }

    @Transactional
    public void deleteById(Long id){
        entityManager.createQuery("delete from Office o where o.id=:id")
                .setParameter("id", id).executeUpdate();
    }

    @Transactional
    public Office findById(Long id){
        List<Office> co = entityManager.createQuery("from Office c where c.id=:id")
                .setParameter("id", id).getResultList();
        if(co.size()>0)
            return co.get(0);
        else
            return null;
    }

    @Transactional
    public List<Office> findByDoctorId(Long id){
        return entityManager.createQuery("from Office where doctor.id=:id")
                .setParameter("id", id).getResultList();
    }
}
