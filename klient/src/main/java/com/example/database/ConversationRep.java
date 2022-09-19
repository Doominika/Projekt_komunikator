package com.example.database;

import com.example.entities.Conversation;
import com.example.entities.User;

import javax.persistence.*;
import java.util.List;

public class ConversationRep
{
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyUnit");
    EntityManager entityManager = emf.createEntityManager();

    public List<Conversation> findAll()
    {
        Query query = entityManager.createQuery("select c from Conversation c ORDER BY id ASC");
        return query.getResultList();
    }

    public int ifConversationExists(int minor, int major)
    {
        Query query = entityManager.createQuery("SELECT id FROM Conversation c WHERE c.user1 = "+ minor +"AND c.user2 = "+ major);
        try
        {
            return (int)query.getSingleResult();
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    public void addConversation (Conversation c)
    {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(c);
        transaction.commit();
    }

    public int findLastId()
    {

        int x = 1;
        Query query = entityManager.createQuery("SELECT MAX(id) FROM Conversation");

        try
        {
            x = (int) query.getSingleResult();
            x += 1;

        }
        catch (Exception e)
        {
            x = 1;
        }

        return x;
    }

    public List<Conversation> findFriends(Integer id)
    {
        Query query = entityManager.createQuery("SELECT c FROM Conversation c WHERE user2= "+ id +" OR user1="+ id);
        return query.getResultList();
    }
}
