package com.example.epointment.model;

import com.example.epointment.repository.InsuranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.epointment.common.Insurance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class InsuranceDao {
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    InsuranceRepository repository;

    @Transactional
    public Insurance save(Insurance ins){
         return entityManager.merge(ins);
    }

    @Transactional
    public List<Insurance> find50Top(){
        return entityManager.createQuery("from Insurance").setMaxResults(50).getResultList();
    }

    @Transactional
    public List<Insurance> findAllInsuranceLike(String nme){
        String insuranc = "%"+nme+"%";
        return entityManager.createQuery("from Insurance s where s.name like :insuranc")
                .setParameter("insuranc", insuranc).getResultList();
    }

    @Transactional
    public Insurance findByName(String name){
        List<Insurance>insurances = entityManager.createQuery("from Insurance s where s.name = :name")
                .setParameter("name", name).getResultList();

        if(insurances!=null && insurances.size() >0)
            return insurances.get(0);
        else
            return null;
    }

    @Transactional
    public Insurance findById(Long id){
        List<Insurance>insurances = entityManager.createQuery("from Insurance i where i.id = :id")
                .setParameter("id", id).getResultList();

        if(insurances!=null && insurances.size() >0)
            return insurances.get(0);
        else
            return null;
    }

}
