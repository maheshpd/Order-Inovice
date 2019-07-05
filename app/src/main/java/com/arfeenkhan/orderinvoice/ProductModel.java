package com.arfeenkhan.orderinvoice;

public class ProductModel {
    String id,name;

    public ProductModel(String name) {
        this.name = name;
    }

    public ProductModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
