package com.ruben.mybank.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name= "tipooperacion")
@JsonIgnoreProperties({ "hibernateLazyInitialize", "handler" })
public class TipooperacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(mappedBy = "tipooperacion", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private final List<OperacionEntity> operacion;


    public TipooperacionEntity() {
        this.operacion = new ArrayList<>();
    }


    public TipooperacionEntity(Long id, String nombre, List<OperacionEntity> operacion) {
        this.operacion = new ArrayList<>();
        this.id = id;
        this.nombre = nombre;
    }


    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getOperacion() {
        return operacion.size();
    }
    
    @PreRemove
    public void nullify() {
        this.operacion.forEach(c -> c.setTipooperacion(null));
    }

    
}
