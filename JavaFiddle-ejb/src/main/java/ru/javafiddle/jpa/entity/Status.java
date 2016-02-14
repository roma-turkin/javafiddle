package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 * A status of user's account (registered, unconfirmed etc.)
 */
@Entity
//@Table(name = "\"Status\"")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(name = "\"statusId\"")
    private int statusId;
//    @Column(name = "\"statusName\"")
    private String statusName;

    public Status(String statusName) {
        this.statusName = statusName;
    }

    public Status(){
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "Status{" +
                "statusId=" + statusId +
                ", statusName='" + statusName + '\'' +
                '}';
    }
}
