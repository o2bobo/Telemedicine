package com.chinabsc.telemedicine.expert.entity.educationvideo;

public class EducationLiveMsgEntity {
    /**
     * data : {"commondepartmentcode":"消化内科专业","courseclass":"基础理论讲座","coursedesc":"11","courseduration":"1.0","coursemajor":"药学","createtime":"2017-10-27 09:46:15","createuser":"a1bba00044b2416c9c94bff69b5cdd62","departid":"bab256d89ddb488b90417eb5283a6263","departname":"消化","endtime":"2018-12-29 09:40:00","expertid":"90a49502db5446408c38b6c76bf269a7","expertname":"王医生","isdvr":"0","jobtitlecode":"","liveid":"243f608f16464425bae51a6aceecffba","liveurl":"rtmp://www.bsc1.cn:1935/live/00003","rcvsiteid":"专家医院测试","rsiteid":"","score":"1","scoretype":"Ⅱ类学分","starttime":"2017-10-27 09:40:00","statue":"playing","updatetime":"2018-11-03 09:45:44","updateuser":"a1bba00044b2416c9c94bff69b5cdd62","warename":"测试科室"}
     * resultCode : 200
     * resultMsg : 请求成功
     */

    private DataBean data;
    private String resultCode;
    private String resultMsg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public static class DataBean {
        /**
         * commondepartmentcode : 消化内科专业
         * courseclass : 基础理论讲座
         * coursedesc : 11
         * courseduration : 1.0
         * coursemajor : 药学
         * createtime : 2017-10-27 09:46:15
         * createuser : a1bba00044b2416c9c94bff69b5cdd62
         * departid : bab256d89ddb488b90417eb5283a6263
         * departname : 消化
         * endtime : 2018-12-29 09:40:00
         * expertid : 90a49502db5446408c38b6c76bf269a7
         * expertname : 王医生
         * isdvr : 0
         * jobtitlecode :
         * liveid : 243f608f16464425bae51a6aceecffba
         * liveurl : rtmp://www.bsc1.cn:1935/live/00003
         * rcvsiteid : 专家医院测试
         * rsiteid :
         * score : 1
         * scoretype : Ⅱ类学分
         * starttime : 2017-10-27 09:40:00
         * statue : playing
         * updatetime : 2018-11-03 09:45:44
         * updateuser : a1bba00044b2416c9c94bff69b5cdd62
         * warename : 测试科室
         */

        private String commondepartmentcode;
        private String courseclass;
        private String coursedesc;
        private String courseduration;
        private String coursemajor;
        private String createtime;
        private String createuser;
        private String departid;
        private String departname;
        private String endtime;
        private String expertid;
        private String expertname;
        private String isdvr;
        private String jobtitlecode;
        private String liveid;
        private String liveurl;
        private String rcvsiteid;
        private String rsiteid;
        private String score;
        private String scoretype;
        private String starttime;
        private String statue;
        private String updatetime;
        private String updateuser;
        private String warename;

        public String getCommondepartmentcode() {
            return commondepartmentcode;
        }

        public void setCommondepartmentcode(String commondepartmentcode) {
            this.commondepartmentcode = commondepartmentcode;
        }

        public String getCourseclass() {
            return courseclass;
        }

        public void setCourseclass(String courseclass) {
            this.courseclass = courseclass;
        }

        public String getCoursedesc() {
            return coursedesc;
        }

        public void setCoursedesc(String coursedesc) {
            this.coursedesc = coursedesc;
        }

        public String getCourseduration() {
            return courseduration;
        }

        public void setCourseduration(String courseduration) {
            this.courseduration = courseduration;
        }

        public String getCoursemajor() {
            return coursemajor;
        }

        public void setCoursemajor(String coursemajor) {
            this.coursemajor = coursemajor;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getCreateuser() {
            return createuser;
        }

        public void setCreateuser(String createuser) {
            this.createuser = createuser;
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

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
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

        public String getIsdvr() {
            return isdvr;
        }

        public void setIsdvr(String isdvr) {
            this.isdvr = isdvr;
        }

        public String getJobtitlecode() {
            return jobtitlecode;
        }

        public void setJobtitlecode(String jobtitlecode) {
            this.jobtitlecode = jobtitlecode;
        }

        public String getLiveid() {
            return liveid;
        }

        public void setLiveid(String liveid) {
            this.liveid = liveid;
        }

        public String getLiveurl() {
            return liveurl;
        }

        public void setLiveurl(String liveurl) {
            this.liveurl = liveurl;
        }

        public String getRcvsiteid() {
            return rcvsiteid;
        }

        public void setRcvsiteid(String rcvsiteid) {
            this.rcvsiteid = rcvsiteid;
        }

        public String getRsiteid() {
            return rsiteid;
        }

        public void setRsiteid(String rsiteid) {
            this.rsiteid = rsiteid;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getScoretype() {
            return scoretype;
        }

        public void setScoretype(String scoretype) {
            this.scoretype = scoretype;
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getStatue() {
            return statue;
        }

        public void setStatue(String statue) {
            this.statue = statue;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public String getUpdateuser() {
            return updateuser;
        }

        public void setUpdateuser(String updateuser) {
            this.updateuser = updateuser;
        }

        public String getWarename() {
            return warename;
        }

        public void setWarename(String warename) {
            this.warename = warename;
        }
    }
}
