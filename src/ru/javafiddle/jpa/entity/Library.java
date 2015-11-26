package ru.javafiddle.jpa.entity;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Lob;

@Entity
@Table

public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int lib_id;
    private String lib_name;
    @Lob
    private byte[] lib;

    public Library(String lib_name, byte[] lib) {
        this.lib_name = lib_name;
        this.lib = lib;
    }

    public Library() {
    }

    public int getLib_id() {
        return lib_id;
    }

    public void setLib_id(int lib_id) {
        this.lib_id = lib_id;
    }

    public String getLib_name() {
        return lib_name;
    }

    public void setLib_name(String lib_name) {
        this.lib_name = lib_name;
    }

    public byte[] getLib() {
        return lib;
    }

    public void setLib(byte[] lib) {
        this.lib = lib;
    }

    @Override
    public String toString() {
        return "Library{" +
                "lib_id=" + lib_id +
                ", lib_name='" + lib_name +
                '}';
    }
}
