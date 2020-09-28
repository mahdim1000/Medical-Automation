package com.example.epointment.model;

import com.example.epointment.common.Service;
import com.example.epointment.common.Turn;
import com.example.epointment.common.TurnService;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@org.springframework.stereotype.Service
public class TurnServiceDao {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public TurnService save(TurnService ts){
        return entityManager.merge(ts);
    }

    @Transactional
    public List<Service> findServicesByTurn(Turn turn){
        return entityManager.createQuery("select ts.service from TurnService ts where ts.turn=:turn")
                .setParameter("turn", turn).getResultList();
    }

    @Transactional
    public void removeByTurnId(Turn turn){
        entityManager.createQuery("delete from TurnService where turn=:turn")
        .setParameter("turn", turn).executeUpdate();
    }

    @Transactional
    public List<Turn> findTurnByServiceId(Long id){
        return entityManager.createQuery("select ts.turn from TurnService ts where ts.service.id=:id")
                .setParameter("id", id).getResultList();
    }
}
