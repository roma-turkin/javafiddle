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

public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int status_id;
    private String status_name;

    public Status(String status_name) {
        this.status_name = status_name;
    }

    public Status(){
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    @Override
    public String toString() {
        return "Status{" +
                "status_id=" + status_id +
                ", status_name='" + status_name +
                '}';
    }
}
