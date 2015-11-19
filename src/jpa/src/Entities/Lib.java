package Entities;

/**
 * Created by Fedor on 18.11.2015.
 */
import javax.persistence.*;

@Entity
@Table

public class Lib {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int lib_id;
    private String lib_name;
    @Lob
    private byte[] lib;

    public Lib(String lib_name, byte[] lib) {
        this.lib_name = lib_name;
        this.lib = lib;
    }

    public Lib() {
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
        return "Lib{" +
                "lib_id=" + lib_id +
                ", lib_name='" + lib_name +
                '}';
    }
}
