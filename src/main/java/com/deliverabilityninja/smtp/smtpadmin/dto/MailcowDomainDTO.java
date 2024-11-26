package com.deliverabilityninja.smtp.smtpadmin.dto;

public class MailcowDomainDTO {
    private String domain;
    private long server_id;
    private long client_id;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getServer_id() {
        return server_id;
    }

    public void setServer_id(long server_id) {
        this.server_id = server_id;
    }

    public long getClient_id() {
        return client_id;
    }

    public void setClient_id(long client_id) {
        this.client_id = client_id;
    }

    @Override
    public String toString() {
        return "MailcowDomainDTO [domain=" + domain + ", server_id=" + server_id + ", client_id=" + client_id + "]";
    }
}

