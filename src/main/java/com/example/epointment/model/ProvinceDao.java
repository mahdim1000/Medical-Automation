//package com.example.epointment.model;
//
//import com.example.epointment.common.City;
//import com.example.epointment.common.Education;
//import com.example.epointment.common.Province;
//import com.example.epointment.repository.ProvinceRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import java.util.List;
//
//@Service
//public class ProvinceDao {
//    @PersistenceContext
//    EntityManager entityManager;
//
//    @Autowired
//    ProvinceRepository provinceRepository;
//
//
//    @Transactional
//    public void save(Province province){
//        entityManager.persist(province);
//    }
//
//    @Transactional
//    public List<Province> findAll(){
//        return entityManager.createQuery("from Province").getResultList();
//    }
//
//    @Transactional
//    public List<Province> getTenTopProvince(){
//        return entityManager.createQuery("from Province").setMaxResults(50).getResultList();
//    }
//
//    @Transactional
//    public List<Province> getLikeName(String name){
//        String filter = name+"%";
//        return entityManager.createQuery("select e from Province e where e.name like :name")
//                .setParameter("name", filter).getResultList();
//    }
//}
