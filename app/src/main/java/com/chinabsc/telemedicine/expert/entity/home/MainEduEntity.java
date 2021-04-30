package com.chinabsc.telemedicine.expert.entity.home;

import java.util.List;

public class MainEduEntity {

    /**
     * resultCode : 200
     * resultMsg :
     * data : [{"liveid":"2bd9361943854bc7b4d7d5427e233635","rcvsiteid":"专家医院","departid":"ff2071c6b1d84c228e9bce1084e0201a","departname":"儿科","expertid":"76d06604b68d42a6b2db5f8f33f875e1","expertname":"韩磊","commondepartmentcode":"儿科","starttime":1517362200000,"endtime":1517329800000,"courseduration":null,"coursemajor":"儿科学","scoretype":"01","score":2,"warename":"藏医馆测试","coursedesc":"","liveurl":"3c6a6259149244d3a2c7ac7f6f97260b","courseclass":"临床病例讨论","createtime":1516892700000,"createuser":"a1bba00044b2416c9c94bff69b5cdd62","updatetime":null,"updateuser":null,"statue":"playing","rsiteid":"54c6d8826d744fd49a8109629c76fc37","jobtitlecode":null,"isdvr":0},{"liveid":"a4dadb79963d412ebfb5b9d1e7866794","rcvsiteid":"专家医院","departid":"ff2071c6b1d84c228e9bce1084e0201a","departname":"儿科","expertid":"76d06604b68d42a6b2db5f8f33f875e1","expertname":"韩磊","commondepartmentcode":"儿科","starttime":1515551400000,"endtime":1516258200000,"courseduration":null,"coursemajor":"儿科学","scoretype":"02","score":2,"warename":"儿科","coursedesc":"","liveurl":"edbcffd4aa4f475f85259da066b59cec","courseclass":"临床病例讨论","createtime":1516892253000,"createuser":"a1bba00044b2416c9c94bff69b5cdd62","updatetime":1541036081000,"updateuser":"a1bba00044b2416c9c94bff69b5cdd62","statue":"playing","rsiteid":"54c6d8826d744fd49a8109629c76fc37","jobtitlecode":null,"isdvr":0},{"liveid":"243f608f16464425bae51a6aceecffba","rcvsiteid":"专家医院测试","departid":"bab256d89ddb488b90417eb5283a6263","departname":"消化","expertid":"90a49502db5446408c38b6c76bf269a7","expertname":"王医生","commondepartmentcode":"消化内科专业","starttime":1509068400000,"endtime":1546047600000,"courseduration":1,"coursemajor":"药学","scoretype":"02","score":1,"warename":"测试科室","coursedesc":"11","liveurl":"b0665c75aece4f43a2b94d22283817a9","courseclass":"基础理论讲座","createtime":1509068775000,"createuser":"a1bba00044b2416c9c94bff69b5cdd62","updatetime":1541209544000,"updateuser":"a1bba00044b2416c9c94bff69b5cdd62","statue":"playing","rsiteid":"43ffcb4a39464418bba95aa8bf3164cd","jobtitlecode":null,"isdvr":0}]
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
         * liveid : 2bd9361943854bc7b4d7d5427e233635
         * rcvsiteid : 专家医院
         * departid : ff2071c6b1d84c228e9bce1084e0201a
         * departname : 儿科
         * expertid : 76d06604b68d42a6b2db5f8f33f875e1
         * expertname : 韩磊
         * commondepartmentcode : 儿科
         * starttime : 1517362200000
         * endtime : 1517329800000
         * courseduration : null
         * coursemajor : 儿科学
         * scoretype : 01
         * score : 2
         * warename : 藏医馆测试
         * coursedesc :
         * liveurl : 3c6a6259149244d3a2c7ac7f6f97260b
         * courseclass : 临床病例讨论
         * createtime : 1516892700000
         * createuser : a1bba00044b2416c9c94bff69b5cdd62
         * updatetime : null
         * updateuser : null
         * statue : playing
         * rsiteid : 54c6d8826d744fd49a8109629c76fc37
         * jobtitlecode : null
         * isdvr : 0
         */

        private String liveid;
        private String rcvsiteid;
        private String departid;
        private String departname;
        private String expertid;
        private String expertname;
        private String commondepartmentcode;
        private String starttime;
        private String endtime;
        private Object courseduration;
        private String coursemajor;
        private String scoretype;
        private int score;
        private String warename;
        private String coursedesc;
        private String liveurl;
        private String courseclass;
        private String createtime;
        private String createuser;
        private String updatetime;
        private String updateuser;
        private String statue;
        private String rsiteid;
        private Object jobtitlecode;
        private int isdvr;

        public String getLiveid() {
            return liveid;
        }

        public void setLiveid(String liveid) {
            this.liveid = liveid;
        }

        public String getRcvsiteid() {
            return rcvsiteid;
        }

        public void setRcvsiteid(String rcvsiteid) {
            this.rcvsiteid = rcvsiteid;
        }

        public String getDepartid() {
            return departid;
        }

        public void setDepartid(String departid) {
            this.departid = departid;
        }

        public String getDepartname() {
            return departname;
        }

        public void setDepartname(String departname) {
            this.departname = departname;
        }

        public String getExpertid() {
            return expertid;
        }

        public void setExpertid(String expertid) {
            this.expertid = expertid;
        }

        public String getExpertname() {
            return expertname;
        }

        public void setExpertname(String expertname) {
            this.expertname = expertname;
        }

        public String getCommondepartmentcode() {
            return commondepartmentcode;
        }

        public void setCommondepartmentcode(String commondepartmentcode) {
            this.commondepartmentcode = commondepartmentcode;
        }


        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public void setUpdateuser(String updateuser) {
            this.updateuser = updateuser;
        }

        public Object getCourseduration() {
            return courseduration;
        }

        public void setCourseduration(Object courseduration) {
            this.courseduration = courseduration;
        }

        public String getCoursemajor() {
            return coursemajor;
        }

        public void setCoursemajor(String coursemajor) {
            this.coursemajor = coursemajor;
        }

        public String getScoretype() {
            return scoretype;
        }

        public void setScoretype(String scoretype) {
            this.scoretype = scoretype;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getWarename() {
            return warename;
        }

        public void setWarename(String warename) {
            this.warename = warename;
        }

        public String getCoursedesc() {
            return coursedesc;
        }

        public void setCoursedesc(String coursedesc) {
            this.coursedesc = coursedesc;
        }

        public String getLiveurl() {
            return liveurl;
        }

        public void setLiveurl(String liveurl) {
            this.liveurl = liveurl;
        }

        public String getCourseclass() {
            return courseclass;
        }

        public void setCourseclass(String courseclass) {
            this.courseclass = courseclass;
        }


        public String getCreateuser() {
            return createuser;
        }

        public void setCreateuser(String createuser) {
            this.createuser = createuser;
        }

        public Object getUpdatetime() {
            return updatetime;
        }


        public Object getUpdateuser() {
            return updateuser;
        }


        public String getStatue() {
            return statue;
        }

        public void setStatue(String statue) {
            this.statue = statue;
        }

        public String getRsiteid() {
            return rsiteid;
        }

        public void setRsiteid(String rsiteid) {
            this.rsiteid = rsiteid;
        }

        public Object getJobtitlecode() {
            return jobtitlecode;
        }

        public void setJobtitlecode(Object jobtitlecode) {
            this.jobtitlecode = jobtitlecode;
        }

        public int getIsdvr() {
            return isdvr;
        }

        public void setIsdvr(int isdvr) {
            this.isdvr = isdvr;
        }
    }
}
