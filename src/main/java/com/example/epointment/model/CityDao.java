package com.example.epointment.model;

import com.example.epointment.common.City;
//import com.example.epointment.common.Province;
import com.example.epointment.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Persistent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//@Persistent
@Service
public class CityDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CityRepository cityRepository;

    @Transactional
    public List<City> findAll() {

//        return (List<City>) cityRepository.findAll();
        return entityManager.createQuery("from City").getResultList();
    }

//    @Transactional
//    public List<City> findByProvince(Province province){
//        return cityRepository.findByProvince(province);
//    }

    @Transactional
    public void save(City city){
         entityManager.persist(city);
    }

    @Transactional
    public List<City> getTenTopCity(){
        return entityManager.createQuery("from City").setMaxResults(50).getResultList();
    }

    @Transactional
    public List<City> getLikeName(String name){
        String filter = name+"%";
        return entityManager.createQuery("select e from City e where e.name like :name")
                .setParameter("name", filter).getResultList();
    }

    @Transactional
    public City findById(Long id){
        List<City> cities = entityManager.createQuery("from  City where id=:id")
                .setParameter("id", id).getResultList();
        if(cities != null && cities.size() > 0){
            return cities.get(0);
        }
        return null;
    }
}
