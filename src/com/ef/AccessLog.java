package com.ef;

import java.util.Date;

public class AccessLog {

    private Long id;

    private String ipAddress;

    private Date date;

    public AccessLog(String ipAddress, Date date) {
        this.ipAddress = ipAddress;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
