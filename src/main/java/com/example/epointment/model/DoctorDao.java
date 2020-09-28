package com.example.epointment.model;

import com.example.epointment.common.Doctor;
import com.example.epointment.repository.DoctorRepository;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.Oneway;
import javax.persistence.Basic;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.PersistenceContext;
import javax.print.Doc;
import java.util.List;

@Service
public class DoctorDao {

    @Autowired
    DoctorRepository doctorRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional()
    public List<Doctor> findAll(){
        List<Doctor> doctors =  entityManager.createQuery("select d FROM Doctor d left outer join d.turnList t group by d order by count(t) desc").getResultList();
        return doctors;

    }

    @Transactional
    public Long countDoctors(){
        return (Long) entityManager.createQuery("select count(d) from Doctor as d").getSingleResult();
    }

    @Transactional
    public Doctor findById(Long id){
        List<Doctor> doctors = entityManager.createQuery("from Doctor where id=:id")
                .setParameter("id", id).getResultList();
        if(doctors.size()>0)
            return doctors.get(0);
        else return null;
    }

    @Transactional
    public Doctor save(Doctor doctor){
        return entityManager.merge(doctor);
    }

    @Transactional
    public Doctor findByMelliCode(String mc){
//        return doctorRepository.findByMelliCode(mc);
        List<Doctor> ds =  entityManager.createQuery("from Doctor where melliCode=:mc")
                .setParameter("mc", mc).getResultList();

        if(ds.size()>0)
            return ds.get(0);
        else return null;
    }

    @Transactional
    public List<Doctor> findByDoctorCode(String dc){
//        return doctorRepository.findByDoctorCode(dc);
        return entityManager.createQuery("from Doctor where doctorCode=:dc")
                .setParameter("dc", dc).getResultList();
    }

    @Transactional
    public List<Doctor> findByPhone(String phone){
        return entityManager.createQuery("from Doctor as d where phone = :phone")
                .setParameter("phone", phone).getResultList();
    }

    @Transactional
    public Doctor findByMelliCodeAndPassword(String m, String p){
        List<Doctor> doctors =  entityManager.createQuery("from Doctor d where d.melliCode = :m and d.password = :p")
                .setParameter("m", m).setParameter("p", p).getResultList();
        if(doctors.size() > 0)
            return doctors.get(0);
        else
            return null;
    }

    @Transactional
    public List<Doctor> findLikeMelliCode(String mc){
        return entityManager.createQuery("from Doctor where melliCode like :mc")
                .setParameter("mc", mc+"%").getResultList();
    }

    @Transactional
    public List<Doctor> findLikeLastNameAndExperticeNameAndCity(String lastname, String expertice, String city){
        return entityManager.createQuery("select d from Doctor d left outer join d.turnList t where d.lastname like :ln and d.expertice.name like :ex and d.city.name like :city group by d order by count(t) desc")
                .setParameter("ln", "%"+lastname+"%").setParameter("ex", "%"+expertice+"%").setParameter("city", "%"+city+"%").getResultList();

    }

    @Transactional
    public List<Doctor> findLikeLastNameAndByExperticeIdAndEducationIdAndGender(String lastname, String expertice, String education, Boolean gender){
        return entityManager.createQuery("select t from Doctor t left outer join t.turnList turn where t.lastname like :ln and t.expertice.name like :expertice and t.education.name like :education and t.gender=:gender group by t order by count(turn) desc")
                .setParameter("ln","%"+lastname+"%").setParameter("expertice", "%"+expertice+"%").setParameter("education", "%"+education+"%").setParameter("gender", gender)
                .getResultList();
    }

    @Transactional
    public List<Doctor> findLikeLastNameAndByExperticeIdAndEducationId(String lastname, String expertice, String education){
        return entityManager.createQuery("select t from Doctor t left outer join t.turnList turn where t.lastname like :ln and t.expertice.name like :expertice and t.education.name like :education group by t order by count(turn) desc")
                .setParameter("ln","%"+lastname+"%").setParameter("expertice", "%"+expertice+"%").setParameter("education", "%"+education+"%")
                .getResultList();
    }

    //teset
    @Transactional
    public void setImageToAll(byte[] image){
        entityManager.createQuery("update Doctor d set d.image=:image").setParameter("image", image).executeUpdate();
    }
}
