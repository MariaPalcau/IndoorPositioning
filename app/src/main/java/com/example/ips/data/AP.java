package com.example.ips.data;

import org.json.JSONObject;

public class AP {
    private String bssid;
    private int sumSamples;
    private int nSamples;
    public AP() {}

    public AP(String bssid, int rssi) {
        this.bssid = bssid;
        this.sumSamples = rssi;
        this.nSamples = 1;
    }

    public float addSample(int rssi) {
        sumSamples += rssi;
        nSamples++;
        return getMean();
    }

    public float getMean() {
        if (nSamples == 0) {
            return 0;
        }
        return (float) sumSamples / nSamples;
    }
    public String getBssid() {
        return bssid;
    }
    public int getSumSamples() { return sumSamples; }
    public void setSumSamples(int sumSamples) { this.sumSamples = sumSamples; }
    public void setNSamples(int nSamples) { this.nSamples = nSamples; }
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public JSONObject toJSON() {
        JSONObject ap = new JSONObject();
        try {
            ap.put("bssid", bssid);
            ap.put("sumSamples", sumSamples);
            ap.put("nSamples", nSamples);
            ap.put("mean", getMean());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ap;
    }
}