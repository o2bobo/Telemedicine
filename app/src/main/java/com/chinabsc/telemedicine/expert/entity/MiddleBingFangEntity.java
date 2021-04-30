package com.chinabsc.telemedicine.expert.entity;

import java.util.List;

public class MiddleBingFangEntity {
    /**
     * code : 001
     * data : [{"wardId":"2901","wardName":"泌尿外科病区"}]
     * message : 返回正确！
     */

    private String code;
    private String message;
    private List<DataBean> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * wardId : 2901
         * wardName : 泌尿外科病区
         */

        private String wardId;
        private String wardName;

        public String getWardId() {
            return wardId;
        }

        public void setWardId(String wardId) {
            this.wardId = wardId;
        }

        public String getWardName() {
            return wardName;
        }

        public void setWardName(String wardName) {
            this.wardName = wardName;
        }
    }
}
