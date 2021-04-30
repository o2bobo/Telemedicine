package com.chinabsc.telemedicine.expert.entity;

import java.util.List;

public class MiddlewareMedicalImgEntity {

    /**
     * code : 001
     * data : [{"accessionNumber":"ZCT201811140361","checkPoint":"头颅","hisId":"714423","patientId":"590432","patientName":"田德风","reportAddress":"RepNo=488017&RepTypeNo=RKCT&pubTime=2018-11-15 08:39:34","reportName":"CT报告","reportNo":"488017","reportTime":"2018-11-15 08:39:34","reportType":"RKCT"},{"accessionNumber":"ZCT201811140361","checkPoint":"肾上腺","hisId":"714423","patientId":"590432","patientName":"田德风","reportAddress":"RepNo=488962&RepTypeNo=RKCT&pubTime=2018-11-16 14:23:37","reportName":"CT报告","reportNo":"488962","reportTime":"2018-11-16 14:23:37","reportType":"RKCT"},{"accessionNumber":"ZCT201811140361","checkPoint":"头部, 头部, 通用","hisId":"714423","patientId":"590432","patientName":"田德风","reportAddress":"RepNo=496606&RepTypeNo=RKMR&pubTime=2018-11-25 09:21:40","reportName":"MRI报告","reportNo":"496606","reportTime":"2018-11-25 09:21:40","reportType":"RKMR"}]
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
         * accessionNumber : ZCT201811140361
         * checkPoint : 头颅
         * hisId : 714423
         * patientId : 590432
         * patientName : 田德风
         * reportAddress : RepNo=488017&RepTypeNo=RKCT&pubTime=2018-11-15 08:39:34
         * reportName : CT报告
         * reportNo : 488017
         * reportTime : 2018-11-15 08:39:34
         * reportType : RKCT
         */

        private String accessionNumber;
        private String checkPoint;
        private String hisId;
        private String patientId;
        private String patientName;
        private String reportAddress;
        private String reportName;
        private String reportNo;
        private String reportTime;
        private String reportType;

        public String getAccessionNumber() {
            return accessionNumber;
        }

        public void setAccessionNumber(String accessionNumber) {
            this.accessionNumber = accessionNumber;
        }

        public String getCheckPoint() {
            return checkPoint;
        }

        public void setCheckPoint(String checkPoint) {
            this.checkPoint = checkPoint;
        }

        public String getHisId() {
            return hisId;
        }

        public void setHisId(String hisId) {
            this.hisId = hisId;
        }

        public String getPatientId() {
            return patientId;
        }

        public void setPatientId(String patientId) {
            this.patientId = patientId;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getReportAddress() {
            return reportAddress;
        }

        public void setReportAddress(String reportAddress) {
            this.reportAddress = reportAddress;
        }

        public String getReportName() {
            return reportName;
        }

        public void setReportName(String reportName) {
            this.reportName = reportName;
        }

        public String getReportNo() {
            return reportNo;
        }

        public void setReportNo(String reportNo) {
            this.reportNo = reportNo;
        }

        public String getReportTime() {
            return reportTime;
        }

        public void setReportTime(String reportTime) {
            this.reportTime = reportTime;
        }

        public String getReportType() {
            return reportType;
        }

        public void setReportType(String reportType) {
            this.reportType = reportType;
        }
    }
}
