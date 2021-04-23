package com.example.ip10assistant.model;

import com.google.gson.annotations.SerializedName;

public class IpData {
    @SerializedName("ip")
    String ip;

    public IpData(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
