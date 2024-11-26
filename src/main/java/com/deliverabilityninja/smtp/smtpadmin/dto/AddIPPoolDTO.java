package com.deliverabilityninja.smtp.smtpadmin.dto;

public class AddIPPoolDTO {
    private Long domainId;
    private Long smtpProviderId;
    private String ipPool;

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getSmtpProviderId() {
        return smtpProviderId;
    }

    public void setSmtpProviderId(Long smtpProviderId) {
        this.smtpProviderId = smtpProviderId;
    }

    public String getIpPool() {
        return ipPool;
    }

    public void setIpPool(String ipPool) {
        this.ipPool = ipPool;
    }
}
