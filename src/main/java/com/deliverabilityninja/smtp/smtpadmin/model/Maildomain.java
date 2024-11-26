package com.deliverabilityninja.smtp.smtpadmin.model;

public class Maildomain {
    private long id;
    private String domainTld;
    private String mailDomain;
    private long mailserverId;
    private int status;
    private long clientId;

    public static enum Status {
        ACTIVE(0){
            @Override
            public String toString() {
                return "Active";
            }
        },   
        INACTIVE(1){
            @Override
            public String toString() {
                return "Inactive";
            }
        },
        LOCKED(2){
            @Override
            public String toString() {
                return "Locked";
            }
        };

        private int status;
        
        Status(int status){
            this.status = status;
        }

        public static Status getStatus(int status) {
            switch(status){
                case 1:
                    return INACTIVE;
                case 2:
                    return LOCKED;
                default:
                    return ACTIVE;
            }
        }

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getMailserverId() {
        return mailserverId;
    }

    public void setMailserverId(long mailserverId) {
        this.mailserverId = mailserverId;
    }

    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MailDomain [id=" + id + ", domainTld=" + domainTld + ", mailDomain=" + mailDomain + ", mailserverId="
                + mailserverId + ", status=" + status + "]";
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    

}
