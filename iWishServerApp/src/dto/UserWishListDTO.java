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
public class UserWishListDTO {
    private String userEmail;
    private int itemId;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }


    // Example constructors (you may need to create more)
    public UserWishListDTO(String userEmail, int itemId) {
        this.userEmail = userEmail;
        this.itemId = itemId;
    }

    // Getters and setters for all fields
}
