package com.ruben.mybank.bean;

import java.util.List;

public class OperacionesDashboard {
    private String label;
    private List<Integer> data;

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Integer> getData() {
        return this.data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }

}
