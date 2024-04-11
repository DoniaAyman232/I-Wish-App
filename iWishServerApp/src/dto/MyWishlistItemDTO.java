/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

/**
 *
 * @author ahmed
 */
public class MyWishlistItemDTO {

    String id;
    String item_name;
    String item_price;
    double total_contribution;

    public MyWishlistItemDTO(String id, String item_name, String item_price, double total_contribution) {
        this.id = id;
        this.item_name = item_name;
        this.item_price = item_price;
        this.total_contribution = total_contribution;
    }

    public MyWishlistItemDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_price() {
        return item_price;
    }

    public void setItem_price(String item_price) {
        this.item_price = item_price;
    }

    public double getTotal_contribution() {
        return total_contribution;
    }

    public void setTotal_contribution(double total_contribution) {
        this.total_contribution = total_contribution;
    }


}
