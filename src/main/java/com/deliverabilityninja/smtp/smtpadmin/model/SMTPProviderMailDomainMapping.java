package com.deliverabilityninja.smtp.smtpadmin.model;

public class SMTPProviderMailDomainMapping {
    private long id;
    private long maildomainId;
    private long smtpProviderId;
    private String sendingDomain;
    private int status;

    public static enum Status {
        ACTIVE(0) {
            @Override
            public String toString() {
                return "Active";
            }
        },
        INACTIVE(1) {
            @Override
            public String toString() {
                return "Inactive";
            }
        };

        private int status;

        Status(int status) {
            this.status = status;
        }

        public static Status getStatus(int status) {
            switch (status) {
                case 0:
                    return INACTIVE;
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

    public long getMaildomainId() {
        return maildomainId;
    }

    public void setMaildomainId(long maildomainId) {
        this.maildomainId = maildomainId;
    }

    public long getSmtpProviderId() {
        return smtpProviderId;
    }

    public void setSmtpProviderId(long smtpProviderId) {
        this.smtpProviderId = smtpProviderId;
    }

    public String getSendingDomain() {
        return sendingDomain;
    }

    public void setSendingDomain(String sendingDomain) {
        this.sendingDomain = sendingDomain;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SMTPProviderMailDomainMapping [id=" + id + ", maildomainId=" + maildomainId + ", smtpProviderId="
                + smtpProviderId + ", sendingDomain=" + sendingDomain + ", status=" + status + "]";
    }

}
