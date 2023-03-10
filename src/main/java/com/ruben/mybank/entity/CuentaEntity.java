package com.ruben.mybank.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "cuenta")
@JsonIgnoreProperties({ "hibernateLazyInitialize", "handler" })
public class CuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime fechacreacion;

    private boolean activa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipocuenta")
    private TipocuentaEntity tipocuenta;

    @OneToMany(mappedBy = "emisorCuentaEntity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<OperacionEntity> operacionesEmisor;

    @OneToMany(mappedBy = "receptorCuentaEntity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<OperacionEntity> operacionesReceptor;

    private String iban;

    public CuentaEntity() {
        this.operacionesEmisor = new ArrayList<>(); 
        this.operacionesReceptor = new ArrayList<>();
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public int getOperacionesEmisor() {
        return this.operacionesEmisor.size();
    }

    public int getOperacionesReceptor() {
        return this.operacionesReceptor.size();
    }

    public Long getId() {
        return this.id;
    }

    public String getIban() {
        return this.iban;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechacreacion() {
        return this.fechacreacion;
    }

    public void setFechacreacion(LocalDateTime fechaCreacion) {
        this.fechacreacion = fechaCreacion;
    }

    public UsuarioEntity getUsuario() {
        return this.usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public TipocuentaEntity getTipocuenta() {
        return this.tipocuenta;
    }

    public void setTipocuenta(TipocuentaEntity tipocuenta) {
        this.tipocuenta = tipocuenta;
    }

}
