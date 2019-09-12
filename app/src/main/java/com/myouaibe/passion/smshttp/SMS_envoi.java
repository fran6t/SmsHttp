package com.myouaibe.passion.smshttp;

public class SMS_envoi {

    private int idSms;
    private String quelnumero;
    private String quelmessage;

    public SMS_envoi() {
        this( 0 ,"unknow", "unknow");
    }

    public SMS_envoi( int idSms, String quelnumero, String quelmessage ) {
        this.setIdSms(idSms);
        this.setQuelnumero(quelnumero);
        this.setQuelmessage(quelmessage);
    }

    public int getIdSms() {
        return idSms;
    }

    public void setIdSms(int idSms) {
        this.idSms = idSms;
    }

    public String getQuelnumero() {
        return quelnumero;
    }

    public void setQuelnumero(String quelnumero) {
        this.quelnumero = quelnumero;
    }

    public String getQuelmessage() {
        return quelmessage;
    }

    public void setQuelmessage(String quelmessage) {
        this.quelmessage = quelmessage;
    }
}