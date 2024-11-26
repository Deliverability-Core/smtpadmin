package com.deliverabilityninja.smtp.smtpadmin.dto;

public class MailcowMailboxDTO {
    
    private String local_part;
    private String domain;
    private String password;
    private String password2;
    private int quota = 256;
    private int active = 1;

    public String getLocal_part() {
        return local_part;
    }

    public void setLocal_part(String local_part) {
        this.local_part = local_part;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
