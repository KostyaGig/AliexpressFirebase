package com.kostya_zinoviev.aliexpress.Model;

public class Products {
    private String nameProduct;
    private String descProduct;
    private String priceProduct;
    private String imageProduct;
    private String categoryProduct;
    private String idProduct;
    private String dateProduct;
    private String timeProduct;

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    public String getDescProduct() {
        return descProduct;
    }

    public void setDescProduct(String descProduct) {
        this.descProduct = descProduct;
    }

    public String getPriceProduct() {
        return priceProduct;
    }

    public void setPriceProduct(String priceProduct) {
        this.priceProduct = priceProduct;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public void setImageProduct(String imageProduct) {
        this.imageProduct = imageProduct;
    }

    public String getCategoryProduct() {
        return categoryProduct;
    }

    public void setCategoryProduct(String categoryProduct) {
        this.categoryProduct = categoryProduct;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getDateProduct() {
        return dateProduct;
    }

    public void setDateProduct(String dateProduct) {
        this.dateProduct = dateProduct;
    }

    public String getTimeProduct() {
        return timeProduct;
    }

    public void setTimeProduct(String timeProduct) {
        this.timeProduct = timeProduct;
    }

    public Products(String nameProduct, String descProduct, String priceProduct, String imageProduct, String categoryProduct, String idProduct, String dateProduct, String timeProduct) {
        this.nameProduct = nameProduct;
        this.descProduct = descProduct;
        this.priceProduct = priceProduct;
        this.imageProduct = imageProduct;
        this.categoryProduct = categoryProduct;
        this.idProduct = idProduct;
        this.dateProduct = dateProduct;
        this.timeProduct = timeProduct;
    }

    public Products(){

    }


}
