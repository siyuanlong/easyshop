package com;

public class ResultMsg {
    private Boolean aBoolean;
    private String res;

    public ResultMsg(Boolean aBoolean, String res) {
        this.aBoolean = aBoolean;
        this.res = res;
    }

    public Boolean getaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(Boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}
