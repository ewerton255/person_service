package br.com.edcor.PersonApi.db;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "person")
public class PersonTO {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @CreationTimestamp
    @Column(name = "dt_creation", updatable = false)
    private OffsetDateTime dtCreation;

    @Column(name = "dt_last_update")
    private OffsetDateTime dtLastUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OffsetDateTime getDtCreation() {
        return dtCreation;
    }

    public void setDtCreation(OffsetDateTime dtCreation) {
        this.dtCreation = dtCreation;
    }

    public OffsetDateTime getDtLastUpdate() {
        return dtLastUpdate;
    }

    public void setDtLastUpdate(OffsetDateTime dtLastUpdate) {
        this.dtLastUpdate = dtLastUpdate;
    }
}
