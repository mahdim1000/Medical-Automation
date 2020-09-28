package com.example.epointment.model;

import com.example.epointment.common.Users;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class UserDao {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Users save(Users user){
        return entityManager.merge(user);
    }
    @Transactional
    public Users findByMelliCode(String m){
        List<Users> usersList =  entityManager.createQuery("from Users u where u.melliCode = :m")
                .setParameter("m", m).getResultList();

        if(usersList != null && usersList.size() > 0){
            return usersList.get(0);
        }else
            return null;
    }
}
