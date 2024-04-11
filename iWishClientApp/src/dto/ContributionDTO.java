/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;
import java.math.BigDecimal;
import java.sql.Date;
/**
 *
 * @author ahmed
 */


public class ContributionDTO {
    private String userEmail;
    private int itemId;
    private BigDecimal contributionAmount;
    private java.sql.Date contributionDate; // Assuming contribution date is stored as a SQL date


    public ContributionDTO(String userEmail, int itemId, BigDecimal contributionAmount, java.sql.Date contributionDate) {
        this.userEmail = userEmail;
        this.itemId = itemId;
        this.contributionAmount = contributionAmount;
        this.contributionDate = contributionDate;
    }

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

    public BigDecimal getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(BigDecimal contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public Date getContributionDate() {
        return contributionDate;
    }

    public void setContributionDate(Date contributionDate) {
        this.contributionDate = contributionDate;
    }

}
