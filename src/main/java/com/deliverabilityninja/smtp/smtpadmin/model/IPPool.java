package com.deliverabilityninja.smtp.smtpadmin.model;

public class IPPool {
    private long id;
    private String name;
    private long domainId;
    private long smtpId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDomainId() {
        return domainId;
    }

    public void setDomainId(long domainId) {
        this.domainId = domainId;
    }

    public long getSmtpId() {
        return smtpId;
    }

    public void setSmtpId(long smtpId) {
        this.smtpId = smtpId;
    }

}
