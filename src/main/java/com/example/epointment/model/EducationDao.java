package com.example.epointment.model;

import com.example.epointment.common.Education;
import com.example.epointment.common.Expertice;
import com.example.epointment.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class EducationDao {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    EducationRepository repository;

    @Transactional
    public void save(Education education){
        entityManager.persist(education);
    }

    @Transactional
    public List<Education> findAll(){
        return entityManager.createQuery("from Education").getResultList();
    }

    @Transactional
    public List<Education> getTenTopEducation(){
        return entityManager.createQuery("from Education").setMaxResults(50).getResultList();
    }

    @Transactional
    public List<Education> getLikeName(String name){
        String filter = name+"%";
        return entityManager.createQuery("select e from Education e where e.name like :name")
                .setParameter("name", filter).getResultList();
    }

    @Transactional
    public Education findEducationById(Long id){
        List<Education> educations =  entityManager.createQuery("select e from Education e where e.id=:id")
                .setParameter("id", id).getResultList();

        if((educations != null) && educations.size() > 0){
            return educations.get(0);
        }

        return null;
    }

}
