package com.deliverabilityninja.smtp.smtpadmin.dto;

import java.util.List;

public class MailboxDTO {
    private long clientId;
    private List<Long> domain;
    private int mailboxCount;

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }   

    public List<Long> getDomain() {
        return domain;
    }

    public void setDomain(List<Long> domain) {
        this.domain = domain;
    }

    public int getMailboxCount() {
        return mailboxCount;
    }

    public void setMailboxCount(int mailboxCount) {
        this.mailboxCount = mailboxCount;
    }

    @Override
    public String toString() {
        return "MailboxDTO [clientId=" + clientId + ", domain=" + domain + ", mailboxCount=" + mailboxCount + "]";
    }
}
