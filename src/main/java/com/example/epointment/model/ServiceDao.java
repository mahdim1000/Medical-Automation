package com.example.epointment.model;

import com.example.epointment.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import com.example.epointment.common.Service;

@org.springframework.stereotype.Service
public class ServiceDao {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ServiceRepository repository;

    @Transactional
    public Service save(com.example.epointment.common.Service s){
        return entityManager.merge(s);
    }

    @Transactional
    public List<Service> find50TopService(){
        return entityManager.createQuery("from Service").setMaxResults(50).getResultList();
    }

    @Transactional
    public List<Service> findAllServiceLike(String snme){
        String service = "%"+snme+"%";
        return entityManager.createQuery("from Service s where s.name like :sname")
                .setParameter("sname", service).getResultList();
    }

    @Transactional
    public Service findByName(String name){
        List<Service>services = entityManager.createQuery("from Service s where s.name = :name")
                .setParameter("name", name).getResultList();

        if(services!=null && services.size() >0)
            return services.get(0);
        else
            return null;
    }

    @Transactional
    public Service findById(Long id){
        List<Service>services = entityManager.createQuery("from Service s where s.id = :id")
                .setParameter("id", id).getResultList();

        if(services!=null && services.size() >0)
            return services.get(0);
        else
            return null;
    }
}
