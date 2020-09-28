package com.example.epointment.repository;

import com.example.epointment.common.City;
//import com.example.epointment.common.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
//    public List<City> findByProvince(Province province);
}
