package com.example.epointment.model;

import com.example.epointment.common.Turn;
import com.example.epointment.common.TurnFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class TurnFilesDao {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public TurnFiles save(TurnFiles tf){
        return entityManager.merge(tf);
    }

//    @Transactional
//    public TurnFiles findById(Long id){
//        List<TurnFiles> tfs = entityManager.createQuery("from turn_files tf where tf.id=:id").setParameter("id", id).getResultList();
//        if(tfs.size()>0)
//            return tfs.get(0);
//        else
//            return null;
//    }

    @Transactional
    public List<TurnFiles> findAllByTurn(Turn t){
        return entityManager.createQuery("from turn_files tf where tf.turn=:turn")
                .setParameter("turn", t).getResultList();
    }

    @Transactional
    public void remove(Long id){
        entityManager.createQuery("delete from turn_files where id=:id").setParameter("id", id).executeUpdate();
    }
}
