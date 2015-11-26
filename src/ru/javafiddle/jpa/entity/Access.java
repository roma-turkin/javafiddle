package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table

public class Access {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int access_id;
    private String access_name;

    public Access(int access_id, String access_name) {
        this.access_id = access_id;
        this.access_name = access_name;
    }

    public Access() {
    }

    public int getAccess_id() {
        return access_id;
    }

    public void setAccess_id(int access_id) {
        this.access_id = access_id;
    }

    public String getAccess_name() {
        return access_name;
    }

    public void setAccess_name(String access_name) {
        this.access_name = access_name;
    }

    @Override
    public String toString() {
        return "Access{" +
                "access_id=" + access_id +
                ", access_name='" + access_name +
                '}';
    }
}
