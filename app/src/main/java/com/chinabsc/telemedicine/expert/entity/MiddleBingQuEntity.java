package com.chinabsc.telemedicine.expert.entity;

import java.util.List;

public class MiddleBingQuEntity {

    /**
     * code : 001
     * data : [{"infectedpatchId":"2901","infectedpatchName":"泌尿外科病区"},{"infectedpatchId":"2902","infectedpatchName":"产科病区"},{"infectedpatchId":"2903","infectedpatchName":"皮肤科病区"},{"infectedpatchId":"2904","infectedpatchName":"心血管内科一病区"},{"infectedpatchId":"2905","infectedpatchName":"肾内科一病区"},{"infectedpatchId":"2906","infectedpatchName":"妇科病区"},{"infectedpatchId":"2907","infectedpatchName":"消化内科一病区"},{"infectedpatchId":"2909","infectedpatchName":"胃肠肛肠病区"},{"infectedpatchId":"2910","infectedpatchName":"儿科一病区"},{"infectedpatchId":"2911","infectedpatchName":"心胸血管介入病区"},{"infectedpatchId":"2912","infectedpatchName":"乳腺胰腺口腔美容病区"},{"infectedpatchId":"2913","infectedpatchName":"神经外科病区"},{"infectedpatchId":"2914","infectedpatchName":"眼科一病区"},{"infectedpatchId":"2915","infectedpatchName":"眼科二病区"},{"infectedpatchId":"2916","infectedpatchName":"呼吸内科病区"},{"infectedpatchId":"2918","infectedpatchName":"(ICU)重症医学科病区"},{"infectedpatchId":"2919","infectedpatchName":"神经内科一病区"},{"infectedpatchId":"2920","infectedpatchName":"神经内科二病区"},{"infectedpatchId":"2921","infectedpatchName":"内分泌科一病区"},{"infectedpatchId":"2923","infectedpatchName":"(NCU)重症医学科病区"},{"infectedpatchId":"2924","infectedpatchName":"消化内科二病区"},{"infectedpatchId":"2925","infectedpatchName":"心血管内科二病区"},{"infectedpatchId":"2926","infectedpatchName":"儿科二病区"},{"infectedpatchId":"2927","infectedpatchName":"新生儿病区"},{"infectedpatchId":"2928","infectedpatchName":"心电监护病区"},{"infectedpatchId":"2952","infectedpatchName":"脊柱关节外科病区"},{"infectedpatchId":"2953","infectedpatchName":"创伤手足外科病区"},{"infectedpatchId":"2954","infectedpatchName":"神经内科三病区"},{"infectedpatchId":"2956","infectedpatchName":"血透病区"},{"infectedpatchId":"2957","infectedpatchName":"康复疼痛病区"},{"infectedpatchId":"2960","infectedpatchName":"甲腹小儿男科病区"},{"infectedpatchId":"2961","infectedpatchName":"耳鼻咽喉头颈外科病区"},{"infectedpatchId":"2962","infectedpatchName":"呼吸重症病区"},{"infectedpatchId":"2964","infectedpatchName":"血液净化病区"},{"infectedpatchId":"2965","infectedpatchName":"肝胆外科病区"},{"infectedpatchId":"2966","infectedpatchName":"肾内科二病区"},{"infectedpatchId":"2967","infectedpatchName":"内分泌科二中医病区"},{"infectedpatchId":"2968","infectedpatchName":"神经内科四病区"},{"infectedpatchId":"2969","infectedpatchName":"心血管内科三病区"},{"infectedpatchId":"2970","infectedpatchName":"风湿免疫科病区"},{"infectedpatchId":"5901","infectedpatchName":"西院骨科病区"},{"infectedpatchId":"5902","infectedpatchName":"全科医学科"},{"infectedpatchId":"5903","infectedpatchName":"肿一病区"},{"infectedpatchId":"5904","infectedpatchName":"肿二病区"},{"infectedpatchId":"5905","infectedpatchName":"西院妇瘤血液二病区"},{"infectedpatchId":"5906","infectedpatchName":"西院外科病区"},{"infectedpatchId":"5907","infectedpatchName":"西院中康科病区"},{"infectedpatchId":"5908","infectedpatchName":"西院五官科病区"},{"infectedpatchId":"5909","infectedpatchName":"西院核医学科病区"},{"infectedpatchId":"5910","infectedpatchName":"肿三病区"},{"infectedpatchId":"5911","infectedpatchName":"肿四病区"},{"infectedpatchId":"5912","infectedpatchName":"肿五病区"},{"infectedpatchId":"5913","infectedpatchName":"西院内科二病区"},{"infectedpatchId":"5915","infectedpatchName":"西院血液一病区"},{"infectedpatchId":"5917","infectedpatchName":"西院神经内科病区"},{"infectedpatchId":"9527","infectedpatchName":"测试病区"}]
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
         * infectedpatchId : 2901
         * infectedpatchName : 泌尿外科病区
         */

        private String infectedpatchId;
        private String infectedpatchName;

        private String wardId;
        private String wardName;
        private String sickbedNumber;
        private String hisId;
        private String patientName;
        private String gender;
        private String birthDate;
        private String cardId;
        private String inpatientDate;
        private String checkStatus;

        public String getCheckStatus() {
            return checkStatus;
        }

        public void setCheckStatus(String checkStatus) {
            this.checkStatus = checkStatus;
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

        public String getSickbedNumber() {
            return sickbedNumber;
        }

        public void setSickbedNumber(String sickbedNumber) {
            this.sickbedNumber = sickbedNumber;
        }

        public String getHisId() {
            return hisId;
        }

        public void setHisId(String hisId) {
            this.hisId = hisId;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
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

        public String getInpatientDate() {
            return inpatientDate;
        }

        public void setInpatientDate(String inpatientDate) {
            this.inpatientDate = inpatientDate;
        }
    }
}
