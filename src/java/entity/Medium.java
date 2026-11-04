/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "medium")
public class Medium implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medium_id")
    private int mediumid;

    @Column(name = "medium_name", length = 45)
    private String mediumName;

    public Medium() {

    }

    /**
     * @return the mediumid
     */
    public int getMediumid() {
        return mediumid;
    }

    /**
     * @param mediumid the mediumid to set
     */
    public void setMediumid(int mediumid) {
        this.mediumid = mediumid;
    }

    /**
     * @return the mediumName
     */
    public String getMediumName() {
        return mediumName;
    }

    /**
     * @param mediumName the mediumName to set
     */
    public void setMediumName(String mediumName) {
        this.mediumName = mediumName;
    }
}
