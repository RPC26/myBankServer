package com.ruben.mybank.bean;

import java.util.List;


public class SaldoUsuario {
    private List<SaldoCuenta> cuentasUsuario;

    public List<SaldoCuenta> getCuentasUsuario() {
        return this.cuentasUsuario;
    }

    public void setCuentasUsuario(List<SaldoCuenta> cuentasUsuario) {
        this.cuentasUsuario = cuentasUsuario;
    }
}
