package com.example.database;

import com.example.entities.User;


import javax.persistence.*;
import java.util.List;

public class UserRep
{
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("MyUnit");
    EntityManager entityManager = emf.createEntityManager();

    public List<User> findAll()
    {
        Query query = entityManager.createQuery("select u from User u ORDER BY id ASC");
        return query.getResultList();
    }

    public void addUser (User u)
    {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(u);
        transaction.commit();
    }

    public int findLastId()
    {

        int x = 1;
        Query query = entityManager.createQuery("SELECT MAX(id) FROM User");

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


    public User findByName(String name)
    {
        Query query = entityManager.createQuery("select u from User u where u.login like '"+ name +"'");
        return (User) query.getSingleResult();
    }
    public User findById(Integer id){
        Query query = entityManager.createQuery("select u from User u where u.id = '"+ id +"'");
        return (User) query.getSingleResult();
    }

}
