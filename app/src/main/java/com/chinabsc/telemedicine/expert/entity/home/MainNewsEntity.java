package com.chinabsc.telemedicine.expert.entity.home;

import java.util.List;

public class MainNewsEntity {

    /**
     * resultCode : 200
     * resultMsg :
     * data : [{"colid":"04617A47D9B649EE930F159C70346FD7","colname":"通知公告"},{"colid":"132B42AAE09C43EF9093647831B8E5C3","colname":"网络动态"},{"colid":"A54B2ADB3E61403E9FD8D98DAFEC242E","colname":"医院新闻"},{"colid":"CCE08408038E46A2A3C52A16140FA7DA","colname":"行业资讯"},{"colid":"941E9D58D6F54C4AA3965E327FA37405","colname":"政策法规"},{"colid":"07e4f4db90ec40db999c38af2fe08fd1","colname":"典型病例"}]
     */

    private int resultCode;
    private String resultMsg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * colid : 04617A47D9B649EE930F159C70346FD7
         * colname : 通知公告
         */

        private String colid;
        private String colname;

        public String getColid() {
            return colid;
        }

        public void setColid(String colid) {
            this.colid = colid;
        }

        public String getColname() {
            return colname;
        }

        public void setColname(String colname) {
            this.colname = colname;
        }
    }
}
