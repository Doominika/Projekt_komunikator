package com.example.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User
{
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "haslo", nullable = false)
    private String haslo;

    public User(Integer id, String login, String haslo)
    {
        this.id = id;
        this.login = login;
        this.haslo = haslo;
    }

    public User()
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

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getHaslo()
    {
        return haslo;
    }

    public void setHaslo(String haslo)
    {
        this.haslo = haslo;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", haslo='" + haslo + '\'' +
                '}';
    }
}