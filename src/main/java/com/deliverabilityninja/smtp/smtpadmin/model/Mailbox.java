package com.deliverabilityninja.smtp.smtpadmin.model;

import java.sql.Timestamp;

public class Mailbox {
    public static enum Status {
        ACTIVE, INACTIVE, LOCKED
    }

    public static enum AuthResult {
        SUCCESS () {
            @Override
            public String toString() {
                return "Authentication successful";
            }
        }, 
        FAILURE () {
            @Override
            public String toString() {
                return "Authentication failed";
            }
        }, 
        INACTIVE () {
            @Override
            public String toString() {
                return "Account is inactive";
            }
        }, 
        LOCKED () {
            @Override
            public String toString() {
                return "Account is locked";
            }
        }, 
        NOTFOUND() {
            @Override
            public String toString() {
                return "Account not found";
            }
        }
    }

    private long id;
    private String email;
    private String password;
    private long domainId;
    private int status;
    Timestamp created;
    Timestamp modified;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getDomainId() {
        return domainId;
    }

    public void setDomainId(long domainId) {
        this.domainId = domainId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getModified() {
        return this.modified;
    }

    public void setModified(Timestamp updated) {
        this.modified = updated;
    }

    @Override
    public String toString() {
        return "Mailbox [id=" + id + ", email=" + email + ", password=" + password + ", domainId=" + domainId
                + ", status=" + status + ", created=" + created + ", modified=" + modified + "]";
    }
    

}
