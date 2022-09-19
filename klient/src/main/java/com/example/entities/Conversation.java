package com.example.entities;

import javax.persistence.*;

@Entity
@Table(name = "conversations")
public class Conversation
{
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user1", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user2", nullable = false)
    private User user2;

    public Conversation(Integer id, User user1, User user2)
    {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
    }

    public Conversation()
    {
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public User getUser1()
    {
        return user1;
    }

    public void setUser1(User user1)
    {
        this.user1 = user1;
    }

    public User getUser2()
    {
        return user2;
    }

    public void setUser2(User user2)
    {
        this.user2 = user2;
    }

    @Override
    public String toString()
    {
        return "Conversation{" +
                "id=" + id +
                ", user1=" + user1 +
                ", user2=" + user2 +
                '}';
    }
}