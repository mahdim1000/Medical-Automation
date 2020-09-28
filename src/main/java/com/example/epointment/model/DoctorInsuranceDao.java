package com.example.epointment.model;

import com.example.epointment.common.Doctor;
import com.example.epointment.common.DoctorInsurance;
import com.example.epointment.common.Insurance;
import com.example.epointment.common.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class DoctorInsuranceDao {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public DoctorInsurance save(DoctorInsurance di){
        return entityManager.merge(di);
    }

    @Transactional
    public boolean doctorHasInsurance(Doctor doctor, Insurance insurance){
        List<DoctorInsurance> doctorInsurances = entityManager.createQuery("from DoctorInsurance di where di.doctor=:d and di.insurance=:i")
                .setParameter("d", doctor).setParameter("i", insurance).getResultList();


        if(doctorInsurances != null && doctorInsurances.size() > 0)
            return true;
        else
            return false;
    }

    @Transactional
    public void remove(DoctorInsurance ds){
        entityManager.remove(ds);
    }

    @Transactional
    public DoctorInsurance findByDoctorAndInsurance(Doctor doctor, Insurance insurance){

        List<DoctorInsurance> doctorInsurances = entityManager.createQuery("from DoctorInsurance ds where ds.doctor=:d and ds.insurance=:s")
                .setParameter("d", doctor).setParameter("s", insurance).getResultList();

        if(doctorInsurances != null && doctorInsurances.size() > 0)
            return doctorInsurances.get(0);
        else
            return null;
    }

    @Transactional
    public List<Insurance> findDoctorInsurances(Doctor doctor){
        return entityManager.createQuery("select ds.insurance from DoctorInsurance ds where ds.doctor=:d").setParameter("d",doctor).getResultList();
    }



}
