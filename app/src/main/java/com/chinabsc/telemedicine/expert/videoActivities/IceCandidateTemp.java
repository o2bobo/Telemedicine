package com.chinabsc.telemedicine.expert.videoActivities;

/**
 * Created by zzw on 2017/3/5.
 */

 public  class IceCandidateTemp {
     public  String sdpMid;//sdpMid
     public  int sdpMLineIndex;//sdpMLineIndex
     public  String candidate;//candidate
    public IceCandidateTemp(){}
    public IceCandidateTemp(String candidate, int sdpMLineIndex, String sdpMid) {
        this.sdpMid = sdpMid;
        this.sdpMLineIndex = sdpMLineIndex;
        this.candidate = candidate;
    }

    public int getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(int sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }
}
