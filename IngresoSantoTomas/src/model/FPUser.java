package model;

import com.digitalpersona.onetouch.DPFPTemplate;

public class FPUser {

    private DPFPTemplate template;
    private long userId;
    private double FAR;

    public FPUser(DPFPTemplate template, long userId, double FAR) {
        this.template = template;
        this.userId = userId;
        this.FAR = FAR;
    }

    public FPUser() {
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

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getFAR() {
        return FAR;
    }

    public void setFAR(double FAR) {
        this.FAR = FAR;
    }
}
