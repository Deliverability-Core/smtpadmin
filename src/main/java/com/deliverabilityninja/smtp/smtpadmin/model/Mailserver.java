/**
 * 
 */
package com.deliverabilityninja.smtp.smtpadmin.model;

/**
 * 
 */
public class Mailserver
{
    private long id;
    private String name;
    private String cnameDomain;
    private String ipAddress;
    private int status;
    private int smtpPort;
    private String apiKey;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCnameDomain()
    {
        return cnameDomain;
    }

    public void setCnameDomain(String cnameDomain)
    {
        this.cnameDomain = cnameDomain;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String inet)
    {
        this.ipAddress = inet;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        return "Mailserver [id=" + id + ", name=" + name + ", cnameDomain=" + cnameDomain + ", ipAddress=" + ipAddress
                + ", status=" + status + ", smtpPort=" + smtpPort + ", apiKey=" + apiKey + "]";
    }

    
}
