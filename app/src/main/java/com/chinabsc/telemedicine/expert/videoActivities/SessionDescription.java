package com.chinabsc.telemedicine.expert.videoActivities;

/**
 * Created by zzw on 2017/3/5.
 */

public  class SessionDescription {

    public final org.webrtc.SessionDescription.Type type ;
    public final String sdp;
    public SessionDescription(org.webrtc.SessionDescription.Type type, String description) {
        this.type = type;
        this.sdp = description;
    }

    public static enum Type {
        OFFER,
        PRANSWER,
        ANSWER;

        private Type() {
        }
      
        public String canonicalForm() {
            return this.name().toLowerCase();
        }

        public static org.webrtc.SessionDescription.Type fromCanonicalForm(String canonical) {
            return (org.webrtc.SessionDescription.Type)valueOf(org.webrtc.SessionDescription.Type.class, canonical.toUpperCase());
        }
    }
}
