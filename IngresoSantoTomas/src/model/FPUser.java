package model;

import com.digitalpersona.onetouch.DPFPTemplate;

public class FPUser {

    private DPFPTemplate template;
    private int userId;
    private double FAR;

    public FPUser(DPFPTemplate template, int userId, double FAR) {
        this.template = template;
        this.userId = userId;
        this.FAR = FAR;
    }

    public FPUser() {
    }

    public FPUser(DPFPTemplate template, int userId) {
        this.template = template;
        this.userId = userId;
    }

    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
        this.template = template;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getFAR() {
        return FAR;
    }

    public void setFAR(double FAR) {
        this.FAR = FAR;
    }

    @Override
    public String toString() {
        return "FPUser{" + "template=" + template + ", userId=" + userId + ", FAR=" + FAR + '}';
    }

}
