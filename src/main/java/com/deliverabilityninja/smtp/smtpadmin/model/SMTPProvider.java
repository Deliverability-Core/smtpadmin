package com.deliverabilityninja.smtp.smtpadmin.model;

public class SMTPProvider {
    private long id;
    private String name;
    private int keyType;
    private String keyId;
    private String keyValue;
    private long clientId;

    public static enum KeyType {
        API_KEY, USERNAME_PASSWORD
    }

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

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    @Override
    public String toString() {
        return "SMTPProvider [id=" + id + ", name=" + name + ", keyType=" + keyType + ", keyId=" + keyId + ", keyValue="
                + keyValue + "]";
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    

}
