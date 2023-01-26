package com.ruben.mybank.bean;

import java.util.List;


public class CuentaSaldoBean {
    private List<SaldoBean> cuentasUsuario;

    public List<SaldoBean> getCuentasUsuario() {
        return this.cuentasUsuario;
    }

    public void setCuentasUsuario(List<SaldoBean> cuentasUsuario) {
        this.cuentasUsuario = cuentasUsuario;
    }
}
