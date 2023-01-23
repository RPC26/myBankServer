package com.ruben.mybank.entity;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "tipocuenta")
@JsonIgnoreProperties({ "hibernateLazyInitialize", "handler" })
public class TipocuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private double porcentajebeneficio;

    private double maxnegativo;

    @OneToMany(mappedBy = "tipocuenta", fetch = FetchType.LAZY)
    private final List<CuentaEntity> cuenta;

    public TipocuentaEntity() {
        this.cuenta = new ArrayList<>();
    }

    public TipocuentaEntity(Long id, String nombre, double porcentajebeneficio, double maxnegativo) {
        this.cuenta = new ArrayList<>();
        this.id = id;
        this.nombre = nombre;
        this.porcentajebeneficio = porcentajebeneficio;
        this.maxnegativo = maxnegativo;
    }

    public Long getId() {
        return this.id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public double getPorcentajebeneficio() {
        return this.porcentajebeneficio;
    }

    public void setPorcentajebeneficio(double porcentajebeneficio) {
        this.porcentajebeneficio = porcentajebeneficio;
    }

    public double getMaxnegativo() {
        return this.maxnegativo;
    }

    public void setMaxnegativo(double maxnegativo) {
        this.maxnegativo = maxnegativo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCuenta() {
        return cuenta.size();
    }

    @PreRemove
    public void nullify() {
        this.cuenta.forEach(c -> c.setTipocuenta(null));
    }

}
