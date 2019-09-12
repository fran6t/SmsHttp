package com.myouaibe.passion.smshttp;

public class Article {

    private int idArticle;
    private String description;
    private String brand;
    private double price;

    public Article() {
        this(0, "unknow", "unknow", 0);
    }

    public Article(int idArticle, String description, String brand, double price){
        this.setIdArticle( idArticle);
        this.setDescription( description);
        this.setBrand( brand);
        this.setPrice( price );

    }

    public int getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(int idArticle) {
        this.idArticle = idArticle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}