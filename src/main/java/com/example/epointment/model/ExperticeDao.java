package com.example.epointment.model;

import com.example.epointment.common.Expertice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ExperticeDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Expertice expertice){
        entityManager.persist(expertice);
    }

    @Transactional
    public List<Expertice> findAll(){
        return entityManager.createQuery("from Expertice").getResultList();
    }

    @Transactional
    public List<Expertice> getLikeName(String name){
        String filter = name+"%";
        return entityManager.createQuery("select e from Expertice e where e.name like :name")
                .setParameter("name", filter).getResultList();
    }

    @Transactional
    public List<Expertice> getTenTopExpertices(){
        return entityManager.createQuery("from Expertice").setMaxResults(50).getResultList();
    }

    @Transactional
    public Expertice findExperticeById(Long id){
        List<Expertice> expertices =  entityManager.createQuery("select e from Expertice e where e.id=:id")
                .setParameter("id", id).getResultList();

        if((expertices != null) && expertices.size() > 0){
            return expertices.get(0);
        }

        return null;
    }
}
