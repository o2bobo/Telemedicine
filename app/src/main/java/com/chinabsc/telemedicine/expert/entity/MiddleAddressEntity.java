package com.chinabsc.telemedicine.expert.entity;

public class MiddleAddressEntity {

    /**
     * resultCode : 200
     * resultMsg :
     * data : {"id":"c01c6d0d6911413ca37c92ba16ebc6ab","siteId":"54c6d8826d744fd49a8109629c76fc37","siteName":"专家医院","type":"inner","ip":"192.168.0.215","port":"1007","description":null,"enabled":"yes","province":null}
     */

    private int resultCode;
    private String resultMsg;
    private DataBean data;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : c01c6d0d6911413ca37c92ba16ebc6ab
         * siteId : 54c6d8826d744fd49a8109629c76fc37
         * siteName : 专家医院
         * type : inner
         * ip : 192.168.0.215
         * port : 1007
         * description : null
         * enabled : yes
         * province : null
         */

        private String id;
        private String siteId;
        private String siteName;
        private String type;
        private String ip;
        private String port;
        private Object description;
        private String enabled;
        private Object province;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSiteId() {
            return siteId;
        }

        public void setSiteId(String siteId) {
            this.siteId = siteId;
        }

        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public Object getDescription() {
            return description;
        }

        public void setDescription(Object description) {
            this.description = description;
        }

        public String getEnabled() {
            return enabled;
        }

        public void setEnabled(String enabled) {
            this.enabled = enabled;
        }

        public Object getProvince() {
            return province;
        }

        public void setProvince(Object province) {
            this.province = province;
        }
    }
}
