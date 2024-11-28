package com.deliverabilityninja.smtp.smtpadmin.dto.godaddy;

public class DnsRecord {
    private String type;
    private String name;
    private String data;
    private int ttl;

    public DnsRecord(String type, String name, String data, int ttl) {
        this.type = type;
        this.name = name;
        this.data = data;
        this.ttl = ttl;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public int getTtl() {
        return ttl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "DnsRecord{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", ttl=" + ttl +
                '}';
    }
}
