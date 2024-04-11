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
public class MyContributersDTO {

    String contributor_first_name;
    String contributor_last_name;
    String contributor_email;
    double contribution_amount;

    public MyContributersDTO(String contributor_first_name, String contributor_last_name, String contributor_email, double contribution_amount) {
        this.contributor_first_name = contributor_first_name;
        this.contributor_last_name = contributor_last_name;
        this.contributor_email = contributor_email;
        this.contribution_amount = contribution_amount;
    }

    public MyContributersDTO() {
    }

    public String getContributor_first_name() {
        return contributor_first_name;
    }

    public void setContributor_first_name(String contributor_first_name) {
        this.contributor_first_name = contributor_first_name;
    }

    public String getContributor_last_name() {
        return contributor_last_name;
    }

    public void setContributor_last_name(String contributor_last_name) {
        this.contributor_last_name = contributor_last_name;
    }

    public String getContributor_email() {
        return contributor_email;
    }

    public void setContributor_email(String contributor_email) {
        this.contributor_email = contributor_email;
    }

    public double getContribution_amount() {
        return contribution_amount;
    }

    public void setContribution_amount(double contribution_amount) {
        this.contribution_amount = contribution_amount;
    }

    

}
