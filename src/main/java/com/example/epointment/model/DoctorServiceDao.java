package com.example.epointment.model;

import com.example.epointment.common.Doctor;
import com.example.epointment.common.DoctorService;
import com.example.epointment.common.Service;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.Doc;
import java.util.List;

@org.springframework.stereotype.Service
public class DoctorServiceDao {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void save(DoctorService ds){
        entityManager.persist(ds);
    }

    @Transactional
    public List<Service> findServicesByDoctorId(Long dId){
        return entityManager.createQuery("ds.service from DoctorService ds where s.id=:id")
                .setParameter("id",dId).getResultList();
    }

    @Transactional
    public boolean doctorHasService(Doctor doctor, Service service){
        List<DoctorService> doctorServices = entityManager.createQuery("from DoctorService ds where ds.doctor=:d and ds.service=:s")
                .setParameter("d", doctor).setParameter("s", service).getResultList();


        if(doctorServices != null && doctorServices.size() > 0)
            return true;
        else
            return false;
    }

    @Transactional
    public void remove(DoctorService ds){
        entityManager.remove(ds);
    }

    @Transactional
    public DoctorService findByDoctorAndService(Doctor doctor, Service service){

        List<DoctorService> doctorServices = entityManager.createQuery("from DoctorService ds where ds.doctor=:d and ds.service=:s")
                .setParameter("d", doctor).setParameter("s", service).getResultList();

        if(doctorServices != null && doctorServices.size() > 0)
            return doctorServices.get(0);
        else
            return null;
    }

    @Transactional
    public List<Service> findDoctorServices(Doctor doctor){
        return entityManager.createQuery("select ds.service from DoctorService ds where ds.doctor=:d").setParameter("d",doctor).getResultList();
    }

    @Transactional
    public List<Service> findClinicServices(Long clinicId){
        return entityManager.createQuery("select distinct ds.service from DoctorService ds inner join ds.doctor.clinics as co where co.clinic.id=:cId")
                .setParameter("cId", clinicId).getResultList();
    }
}
