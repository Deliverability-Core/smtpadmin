package com.deliverabilityninja.smtp.smtpadmin.dto;

public class MailcowDomainCreationPayloadDTO {
    private String domain;
    private String description = "Created via admin interface";
    private String aliases = "100";
    private String mailboxes = "10";
    private String defquota = "128";
    private String maxquota = "256";
    private String quota = "10240";
    private String active = "1";
    private String rl_value = "10";
    private String rl_frame = "s";
    private String backupmx = "0";
    private String relay_all_recipients = "0";
    private String restart_sogo = "0";

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAliases() {
        return aliases;
    }

    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public String getMailboxes() {
        return mailboxes;
    }

    public void setMailboxes(String mailboxes) {
        this.mailboxes = mailboxes;
    }

    public String getDefquota() {
        return defquota;
    }

    public void setDefquota(String defquota) {
        this.defquota = defquota;
    }

    public String getMaxquota() {
        return maxquota;
    }

    public void setMaxquota(String maxquota) {
        this.maxquota = maxquota;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getRl_value() {
        return rl_value;
    }

    public void setRl_value(String rl_value) {
        this.rl_value = rl_value;
    }

    public String getRl_frame() {
        return rl_frame;
    }

    public void setRl_frame(String rl_frame) {
        this.rl_frame = rl_frame;
    }

    public String getBackupmx() {
        return backupmx;
    }

    public void setBackupmx(String backupmx) {
        this.backupmx = backupmx;
    }

    public String getRelay_all_recipients() {
        return relay_all_recipients;
    }

    public void setRelay_all_recipients(String relay_all_recipients) {
        this.relay_all_recipients = relay_all_recipients;
    }

    public String getRestart_sogo() {
        return restart_sogo;
    }

    public void setRestart_sogo(String restart_sogo) {
        this.restart_sogo = restart_sogo;
    }
}
