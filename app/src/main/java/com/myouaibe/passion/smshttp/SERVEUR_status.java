package com.myouaibe.passion.smshttp;

public class SERVEUR_status {
    private String quelstatus;

    public SERVEUR_status() {
        this( "unknow" );
    }

    public SERVEUR_status( String quelstatus ) {
        this.setQuelstatus(quelstatus);
    }

    public String getQuelstatus() {
        return quelstatus;
    }

    public void setQuelstatus(String quelstatus) {
        this.quelstatus = quelstatus;
    }

}
