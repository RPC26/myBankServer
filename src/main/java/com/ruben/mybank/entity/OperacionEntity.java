package com.ruben.mybank.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "operacion")
@JsonIgnoreProperties({ "hibernateLazyInitialize", "handler" })
public class OperacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cantidad;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime fechahoraoperacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipooperacion")
    private TipooperacionEntity tipooperacion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cuentaemisor")
    private CuentaEntity emisorCuentaEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cuentareceptor")
    private CuentaEntity receptorCuentaEntity;


    public OperacionEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public double getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechahoraoperacion() {
        return this.fechahoraoperacion;
    }

    public void setFechahoraoperacion(LocalDateTime fechaoperacion) {
        this.fechahoraoperacion = fechaoperacion;
    }

    public TipooperacionEntity getTipooperacion() {
        return this.tipooperacion;
    }

    public void setTipooperacion(TipooperacionEntity tipooperacion) {
        this.tipooperacion = tipooperacion;
    }

    public CuentaEntity getEmisorCuentaEntity() {
        return this.emisorCuentaEntity;
    }

    public void setEmisorCuentaEntity(CuentaEntity emisorCuentaEntity) {
        this.emisorCuentaEntity = emisorCuentaEntity;
    }

    public CuentaEntity getReceptorCuentaEntity() {
        return this.receptorCuentaEntity;
    }

    public void setReceptorCuentaEntity(CuentaEntity receptorCuentaEntity) {
        this.receptorCuentaEntity = receptorCuentaEntity;
    }



}
