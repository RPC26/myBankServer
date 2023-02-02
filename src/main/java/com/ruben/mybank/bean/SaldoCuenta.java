package com.ruben.mybank.bean;


public class SaldoCuenta {

    private Long idCuenta;
    private Double saldoReal;
    private Double saldoBeneficio;


    public Double getSaldoBeneficio() {
        return this.saldoBeneficio;
    }

    public void setSaldoBeneficio(Double saldoBenificio) {
        this.saldoBeneficio = saldoBenificio;
    }


    public Long getIdCuenta() {
        return this.idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }

    public Double getSaldoReal() {
        return this.saldoReal;
    }

    public void setSaldoReal(Double saldo) {
        this.saldoReal = saldo;
    }

}
