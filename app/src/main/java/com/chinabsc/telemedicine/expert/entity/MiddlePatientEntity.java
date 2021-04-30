package com.chinabsc.telemedicine.expert.entity;

import java.util.List;

public class MiddlePatientEntity {


    /**
     * code : 001
     * data : [{"admissionId":"1338731","birthDate":"1933-06-05","cardId":"420400193306051016","gender":"男  ","hisId":"710073","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"朱心康","sickbedNumber":"21","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1339524","birthDate":"1980-01-01","cardId":"421022198019141859","gender":"男  ","hisId":"710838","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"刘勇","sickbedNumber":"46","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1339526","birthDate":"1962-03-14","cardId":"42242219620314001x","gender":"男  ","hisId":"710840","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"许宏平","sickbedNumber":"20","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1341649","birthDate":"1988-01-01","cardId":"420420198801010101","gender":"男  ","hisId":"712887","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"梅启华","sickbedNumber":"2","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1341743","birthDate":"1950-01-18","cardId":"422422195001182110","gender":"男  ","hisId":"712981","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"李克勤","sickbedNumber":"40","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1341919","birthDate":"1944-03-10","cardId":"420400194403101016","gender":"男  ","hisId":"713151","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"王典经","sickbedNumber":"32","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1342652","birthDate":"1946-12-24","cardId":"422432194612246564","gender":"女  ","hisId":"713861","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"苏小兰","sickbedNumber":"24","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1343011","birthDate":"1953-09-21","cardId":"421087195309215017","gender":"男  ","hisId":"714193","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"王兴福","sickbedNumber":"42","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1343105","birthDate":"1962-09-22","cardId":"430523196209226617","gender":"男  ","hisId":"714284","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"赵更祥","sickbedNumber":"43","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1343357","birthDate":"1961-01-01","cardId":"4210221961112086019","gender":"男  ","hisId":"714526","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"廖全发","sickbedNumber":"19","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1343478","birthDate":"1969-01-01","cardId":"42242119690114461X","gender":"男  ","hisId":"714642","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"李道尧","sickbedNumber":"1","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1343852","birthDate":"1948-03-10","cardId":"422421194803100493","gender":"男  ","hisId":"715008","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"叶明山","sickbedNumber":"101","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1343789","birthDate":"1990-01-08","cardId":"421087199001086712","gender":"男  ","hisId":"714949","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"张瑜","sickbedNumber":"6","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1343837","birthDate":"1960-03-20","cardId":"420400196003203514","gender":"男  ","hisId":"714992","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"张明付","sickbedNumber":"34","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344183","birthDate":"1950-02-17","cardId":"420400195002170568","gender":"女  ","hisId":"715337","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"王清珍","sickbedNumber":"7","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344321","birthDate":"1946-01-01","cardId":"","gender":"女  ","hisId":"715473","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"邓有芳","sickbedNumber":"26","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344332","birthDate":"1956-08-16","cardId":"422422195608166864","gender":"女  ","hisId":"715484","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"方国珍","sickbedNumber":"37","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344255","birthDate":"1951-04-02","cardId":"422425195104022434","gender":"男  ","hisId":"715408","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"杨儒林","sickbedNumber":"14","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344395","birthDate":"1958-03-16","cardId":"421004195803162538","gender":"男  ","hisId":"715552","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"徐上泽","sickbedNumber":"30","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344422","birthDate":"1965-04-01","cardId":"421003196504010022","gender":"女  ","hisId":"715577","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"刘全琴","sickbedNumber":"36","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344510","birthDate":"1969-06-23","cardId":"420400196906230521","gender":"女  ","hisId":"715662","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"杨军","sickbedNumber":"44","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344520","birthDate":"1970-02-05","cardId":"422423197002051847","gender":"女  ","hisId":"715672","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"孟祥梅","sickbedNumber":"9","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344524","birthDate":"1958-06-20","cardId":"422421195806201212","gender":"男  ","hisId":"715677","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"马於祯","sickbedNumber":"31","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344583","birthDate":"1948-01-26","cardId":"42108119480126562X","gender":"女  ","hisId":"715731","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"谭春爱","sickbedNumber":"28","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344926","birthDate":"1946-01-12","cardId":"422422194601120023","gender":"女  ","hisId":"716067","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"王绍芬","sickbedNumber":"45","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344929","birthDate":"1963-04-22","cardId":"422422196304223754","gender":"男  ","hisId":"716070","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"刘昌全","sickbedNumber":"3","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344962","birthDate":"1964-01-01","cardId":"","gender":"男  ","hisId":"716100","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"侯作普","sickbedNumber":"39","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1345123","birthDate":"1954-01-01","cardId":"422431195404122127","gender":"女  ","hisId":"716255","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"李文凤","sickbedNumber":"35","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1345133","birthDate":"1933-05-01","cardId":"420400193305011813","gender":"男  ","hisId":"716264","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"王云洪","sickbedNumber":"5","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1345176","birthDate":"1970-11-06","cardId":"422723197011061517","gender":"男  ","hisId":"716304","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"伍道根","sickbedNumber":"33","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1345393","birthDate":"1958-02-04","cardId":"421022195802045471","gender":"男  ","hisId":"716517","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"章应高","sickbedNumber":"48","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344071","birthDate":"1961-10-14","cardId":"422422196110140010","gender":"男  ","hisId":"715222","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"付良权","sickbedNumber":"38","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1344096","birthDate":"1954-01-03","cardId":"422421195401033302","gender":"女  ","hisId":"715248","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"王开年","sickbedNumber":"11","wardId":"2905","wardName":"肾内科一病区"},{"admissionId":"1345456","birthDate":"1992-05-14","cardId":"421024199205142514","gender":"男  ","hisId":"716573","infectedpatchId":"2905","infectedpatchName":"肾内科一病区","inpatientDate":"2018-11-24","patientName":"李朋朋","sickbedNumber":"16","wardId":"2905","wardName":"肾内科一病区"}]
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
         * admissionId : 1338731
         * birthDate : 1933-06-05
         * cardId : 420400193306051016
         * gender : 男
         * hisId : 710073
         * infectedpatchId : 2905
         * infectedpatchName : 肾内科一病区
         * inpatientDate : 2018-11-24
         * patientName : 朱心康
         * sickbedNumber : 21
         * wardId : 2905
         * wardName : 肾内科一病区
         */

        private String admissionId;
        private String birthDate;
        private String cardId;
        private String gender;
        private String hisId;
        private String infectedpatchId;
        private String infectedpatchName;
        private String inpatientDate;
        private String patientName;
        private String sickbedNumber;
        private String wardId;
        private String wardName;

        public String getAdmissionId() {
            return admissionId;
        }

        public void setAdmissionId(String admissionId) {
            this.admissionId = admissionId;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getHisId() {
            return hisId;
        }

        public void setHisId(String hisId) {
            this.hisId = hisId;
        }

        public String getInfectedpatchId() {
            return infectedpatchId;
        }

        public void setInfectedpatchId(String infectedpatchId) {
            this.infectedpatchId = infectedpatchId;
        }

        public String getInfectedpatchName() {
            return infectedpatchName;
        }

        public void setInfectedpatchName(String infectedpatchName) {
            this.infectedpatchName = infectedpatchName;
        }

        public String getInpatientDate() {
            return inpatientDate;
        }

        public void setInpatientDate(String inpatientDate) {
            this.inpatientDate = inpatientDate;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getSickbedNumber() {
            return sickbedNumber;
        }

        public void setSickbedNumber(String sickbedNumber) {
            this.sickbedNumber = sickbedNumber;
        }

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
