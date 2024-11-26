package com.deliverabilityninja.smtp.smtpadmin.dto;

public class MailServerMailDomainDTO {

    private Long maildomainId;
    private String domainTld;
    private String mailServerCnameDomain;
    private String mailDomainAPIKey;

    public Long getMaildomainId() {
        return maildomainId;
    }

    public void setMaildomainId(Long maildomainId) {
        this.maildomainId = maildomainId;
    }

    public String getDomainTld() {
        return domainTld;
    }

    public void setDomainTld(String domainTld) {
        this.domainTld = domainTld;
    }

    public String getMailServerCnameDomain() {
        return mailServerCnameDomain;
    }

    public void setMailServerCnameDomain(String mailServerCnameDomain) {
        this.mailServerCnameDomain = mailServerCnameDomain;
    }

    public String getMailDomainAPIKey() {
        return mailDomainAPIKey;
    }

    public void setMailDomainAPIKey(String mailDomainAPIKey) {
        this.mailDomainAPIKey = mailDomainAPIKey;
    }

}
