package com.deliverabilityninja.smtp.smtpadmin.dto;

public class DomainListDTO {
    private String clientName;
    private String domainTld;
    private String mailDomain;
    private int domainStatus;
    private String providerName;
    private int providerStatus;


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getDomainTld() {
        return domainTld;
    }

    public void setDomainTld(String domainTld) {
        this.domainTld = domainTld;
    }

    public String getMailDomain() {
        return mailDomain;
    }

    public void setMailDomain(String mailDomain) {
        this.mailDomain = mailDomain;
    }

    public int getDomainStatus() {
        return domainStatus;
    }

    public void setDomainStatus(int domainStatus) {
        this.domainStatus = domainStatus;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public int getProviderStatus() {
        return providerStatus;
    }

    public void setProviderStatus(int providerStatus) {
        this.providerStatus = providerStatus;
    }
}
