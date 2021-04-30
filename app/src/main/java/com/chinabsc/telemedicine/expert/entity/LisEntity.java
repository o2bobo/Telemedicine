package com.chinabsc.telemedicine.expert.entity;

import java.io.Serializable;
import java.util.List;

public class LisEntity implements Serializable {

    /**
     * code : 001
     * data : [{"diagnosis":"","hisId":"709965","lisStatus":"","purpose":"血凝四项+HIV+HBsAg(急)","risExamItemReturnList":[{"hisId":"709965","itemName":"艾滋病抗体","itemResult":"","itemUnit":"","itemValue":"阴性","lowerLimit":"阴性","rank":"","testId":"20180126XNB219","upperLimit":""},{"hisId":"709965","itemName":"凝血酶凝结时间（TT）","itemResult":"","itemUnit":"秒","itemValue":"16.6","lowerLimit":"14.0","rank":"","testId":"20180126XNB219","upperLimit":"25.0"},{"hisId":"709965","itemName":"乙肝表面抗原","itemResult":"","itemUnit":"","itemValue":"阴性","lowerLimit":"阴性","rank":"","testId":"20180126XNB219","upperLimit":""},{"hisId":"709965","itemName":"国际标准化比值（INR）","itemResult":"","itemUnit":"","itemValue":"0.84","lowerLimit":"0.80","rank":"","testId":"20180126XNB219","upperLimit":"1.20"},{"hisId":"709965","itemName":"活化部分凝血酶原时间（A）","itemResult":"","itemUnit":"秒","itemValue":"26.8","lowerLimit":"20.0","rank":"","testId":"20180126XNB219","upperLimit":"40.0"},{"hisId":"709965","itemName":"纤维蛋白原（FIB）","itemResult":"↑","itemUnit":"g/L","itemValue":"6.24","lowerLimit":"2.00","rank":"","testId":"20180126XNB219","upperLimit":"4.00"},{"hisId":"709965","itemName":"凝血酶原时间（PT）","itemResult":"","itemUnit":"秒","itemValue":"9.9","lowerLimit":"9.0","rank":"","testId":"20180126XNB219","upperLimit":"14.0"}],"sample":"血浆","sampleMemo":"","sampleTime":"2018-01-26 07:11:45","siteId":"2afb9d7507e342588c99f6e75f54b831","sndDepart":"","sndDoctor":"","testId":"20180126XNB219"},{"diagnosis":"","hisId":"709965","lisStatus":"","purpose":"肝功能(大)+空腹血糖+肾功能(住院)+K/Na/Cl/Ca/P/CO2","risExamItemReturnList":[],"sample":"血清","sampleMemo":"","sampleTime":"2018-01-27 09:35:53","siteId":"2afb9d7507e342588c99f6e75f54b831","sndDepart":"","sndDoctor":"","testId":"20180127SHS167"},{"diagnosis":"","hisId":"709965","lisStatus":"","purpose":"血常规五分类(急)","risExamItemReturnList":[{"hisId":"709965","itemName":"淋巴细胞计数","itemResult":"","itemUnit":"10^9/L","itemValue":"2.76","lowerLimit":"0.80","rank":"","testId":"20180126BCF212","upperLimit":"4.00"},{"hisId":"709965","itemName":"平均血小板体积","itemResult":"↑","itemUnit":"fL","itemValue":"14.10","lowerLimit":"6.50","rank":"","testId":"20180126BCF212","upperLimit":"12.00"},{"hisId":"709965","itemName":"红细胞压积","itemResult":"","itemUnit":"%","itemValue":"38.3","lowerLimit":"32.0","rank":"","testId":"20180126BCF212","upperLimit":"50.8"},{"hisId":"709965","itemName":"RDW-CV","itemResult":"","itemUnit":"","itemValue":"15","lowerLimit":"11","rank":"","testId":"20180126BCF212","upperLimit":"16"},{"hisId":"709965","itemName":"嗜酸粒细胞计数","itemResult":"","itemUnit":"10^9/L","itemValue":"0.04","lowerLimit":"0.02","rank":"","testId":"20180126BCF212","upperLimit":"0.50"},{"hisId":"709965","itemName":"淋巴细胞%","itemResult":"","itemUnit":"%","itemValue":"31.10","lowerLimit":"20.00","rank":"","testId":"20180126BCF212","upperLimit":"40.00"},{"hisId":"709965","itemName":"大血小板比率","itemResult":"↑","itemUnit":"%","itemValue":"56.6","lowerLimit":"13.0","rank":"","testId":"20180126BCF212","upperLimit":"43.0"},{"hisId":"709965","itemName":"嗜碱粒细胞%","itemResult":"","itemUnit":"%","itemValue":"0.20","lowerLimit":"0.00","rank":"","testId":"20180126BCF212","upperLimit":"1.00"},{"hisId":"709965","itemName":"白细胞计数","itemResult":"","itemUnit":"10^9/L","itemValue":"8.89","lowerLimit":"4.00","rank":"","testId":"20180126BCF212","upperLimit":"10.00"},{"hisId":"709965","itemName":"血小板分布宽度","itemResult":"","itemUnit":"%","itemValue":"17.20","lowerLimit":"7.00","rank":"","testId":"20180126BCF212","upperLimit":"19.00"},{"hisId":"709965","itemName":"平均红细胞体积","itemResult":"","itemUnit":"fL","itemValue":"94.10","lowerLimit":"70.00","rank":"","testId":"20180126BCF212","upperLimit":"100.00"},{"hisId":"709965","itemName":"红细胞计数","itemResult":"","itemUnit":"10^12/L","itemValue":"4.07","lowerLimit":"4.30","rank":"","testId":"20180126BCF212","upperLimit":"5.80"},{"hisId":"709965","itemName":"中性粒细胞%","itemResult":"","itemUnit":"%","itemValue":"63.00","lowerLimit":"50.00","rank":"","testId":"20180126BCF212","upperLimit":"70.00"},{"hisId":"709965","itemName":"大血小板计数","itemResult":"","itemUnit":"10^9/L","itemValue":"63","lowerLimit":"30","rank":"","testId":"20180126BCF212","upperLimit":"90"},{"hisId":"709965","itemName":"中性粒细胞计数","itemResult":"","itemUnit":"10^9/L","itemValue":"5.61","lowerLimit":"2.00","rank":"","testId":"20180126BCF212","upperLimit":"7.00"},{"hisId":"709965","itemName":"血小板计数","itemResult":"","itemUnit":"10^9/L","itemValue":"111.0","lowerLimit":"100.0","rank":"","testId":"20180126BCF212","upperLimit":"300.0"},{"hisId":"709965","itemName":"嗜酸粒细胞%","itemResult":"","itemUnit":"%","itemValue":"0.50","lowerLimit":"0.50","rank":"","testId":"20180126BCF212","upperLimit":"5.00"},{"hisId":"709965","itemName":"平均血红蛋白含量","itemResult":"","itemUnit":"pg","itemValue":"30.50","lowerLimit":"25.00","rank":"","testId":"20180126BCF212","upperLimit":"35.00"},{"hisId":"709965","itemName":"平均血红蛋白浓度","itemResult":"","itemUnit":"g/L","itemValue":"324","lowerLimit":"300","rank":"","testId":"20180126BCF212","upperLimit":"380"},{"hisId":"709965","itemName":"血红蛋白","itemResult":"","itemUnit":"g/L","itemValue":"124.0","lowerLimit":"110.0","rank":"","testId":"20180126BCF212","upperLimit":"150.0"},{"hisId":"709965","itemName":"RDW-SD","itemResult":"","itemUnit":"","itemValue":"52","lowerLimit":"35","rank":"","testId":"20180126BCF212","upperLimit":"56"},{"hisId":"709965","itemName":"单核细胞计数","itemResult":"","itemUnit":"10^9/L","itemValue":"0.46","lowerLimit":"0.12","rank":"","testId":"20180126BCF212","upperLimit":"0.80"},{"hisId":"709965","itemName":"血小板压积","itemResult":"","itemUnit":"%","itemValue":"0.156","lowerLimit":"0.100","rank":"","testId":"20180126BCF212","upperLimit":"0.600"},{"hisId":"709965","itemName":"嗜碱粒细胞计数","itemResult":"","itemUnit":"10^9/L","itemValue":"0.02","lowerLimit":"0.01","rank":"","testId":"20180126BCF212","upperLimit":"0.06"},{"hisId":"709965","itemName":"单核细胞%","itemResult":"","itemUnit":"%","itemValue":"5.20","lowerLimit":"3.00","rank":"","testId":"20180126BCF212","upperLimit":"8.00"}],"sample":"全血","sampleMemo":"","sampleTime":"2018-01-26 06:50:52","siteId":"2afb9d7507e342588c99f6e75f54b831","sndDepart":"","sndDoctor":"","testId":"20180126BCF212"},{"diagnosis":"","hisId":"709965","lisStatus":"","purpose":"尿常规+尿沉渣检测","risExamItemReturnList":[],"sample":"尿液","sampleMemo":"","sampleTime":"2018-07-24 11:22:48","siteId":"2afb9d7507e342588c99f6e75f54b831","sndDepart":"","sndDoctor":"","testId":"20180724NLS036"},{"diagnosis":"","hisId":"709965","lisStatus":"","purpose":"ABO血型+RH血型+抗筛（急诊）","risExamItemReturnList":[{"hisId":"709965","itemName":"Rh(D)","itemResult":"","itemUnit":"","itemValue":"阳性","lowerLimit":"","rank":"","testId":"20180126LLL065","upperLimit":""},{"hisId":"709965","itemName":"抗体筛查","itemResult":"","itemUnit":"","itemValue":"阴性","lowerLimit":"","rank":"","testId":"20180126LLL065","upperLimit":""},{"hisId":"709965","itemName":"血型测定","itemResult":"","itemUnit":"","itemValue":"A型","lowerLimit":"","rank":"","testId":"20180126LLL065","upperLimit":""}],"sample":"血液","sampleMemo":"","sampleTime":"2018-01-26 07:36:12","siteId":"2afb9d7507e342588c99f6e75f54b831","sndDepart":"","sndDoctor":"","testId":"20180126LLL065"},{"diagnosis":"","hisId":"709965","lisStatus":"","purpose":"传染病套餐(定量)","risExamItemReturnList":[],"sample":"血液","sampleMemo":"","sampleTime":"2018-01-27 11:09:48","siteId":"2afb9d7507e342588c99f6e75f54b831","sndDepart":"","sndDoctor":"","testId":"20180127DWA027"}]
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

    public static class DataBean implements Serializable{
        /**
         * diagnosis :
         * hisId : 709965
         * lisStatus :
         * purpose : 血凝四项+HIV+HBsAg(急)
         * risExamItemReturnList : [{"hisId":"709965","itemName":"艾滋病抗体","itemResult":"","itemUnit":"","itemValue":"阴性","lowerLimit":"阴性","rank":"","testId":"20180126XNB219","upperLimit":""},{"hisId":"709965","itemName":"凝血酶凝结时间（TT）","itemResult":"","itemUnit":"秒","itemValue":"16.6","lowerLimit":"14.0","rank":"","testId":"20180126XNB219","upperLimit":"25.0"},{"hisId":"709965","itemName":"乙肝表面抗原","itemResult":"","itemUnit":"","itemValue":"阴性","lowerLimit":"阴性","rank":"","testId":"20180126XNB219","upperLimit":""},{"hisId":"709965","itemName":"国际标准化比值（INR）","itemResult":"","itemUnit":"","itemValue":"0.84","lowerLimit":"0.80","rank":"","testId":"20180126XNB219","upperLimit":"1.20"},{"hisId":"709965","itemName":"活化部分凝血酶原时间（A）","itemResult":"","itemUnit":"秒","itemValue":"26.8","lowerLimit":"20.0","rank":"","testId":"20180126XNB219","upperLimit":"40.0"},{"hisId":"709965","itemName":"纤维蛋白原（FIB）","itemResult":"↑","itemUnit":"g/L","itemValue":"6.24","lowerLimit":"2.00","rank":"","testId":"20180126XNB219","upperLimit":"4.00"},{"hisId":"709965","itemName":"凝血酶原时间（PT）","itemResult":"","itemUnit":"秒","itemValue":"9.9","lowerLimit":"9.0","rank":"","testId":"20180126XNB219","upperLimit":"14.0"}]
         * sample : 血浆
         * sampleMemo :
         * sampleTime : 2018-01-26 07:11:45
         * siteId : 2afb9d7507e342588c99f6e75f54b831
         * sndDepart :
         * sndDoctor :
         * testId : 20180126XNB219
         */

        private String diagnosis;
        private String hisId;
        private String lisStatus;
        private String purpose;
        private String sample;
        private String sampleMemo;
        private String sampleTime;
        private String siteId;
        private String sndDepart;
        private String sndDoctor;
        private String testId;
        private List<RisExamItemReturnListBean> risExamItemReturnList;

        public String getDiagnosis() {
            return diagnosis;
        }

        public void setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
        }

        public String getHisId() {
            return hisId;
        }

        public void setHisId(String hisId) {
            this.hisId = hisId;
        }

        public String getLisStatus() {
            return lisStatus;
        }

        public void setLisStatus(String lisStatus) {
            this.lisStatus = lisStatus;
        }

        public String getPurpose() {
            return purpose;
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose;
        }

        public String getSample() {
            return sample;
        }

        public void setSample(String sample) {
            this.sample = sample;
        }

        public String getSampleMemo() {
            return sampleMemo;
        }

        public void setSampleMemo(String sampleMemo) {
            this.sampleMemo = sampleMemo;
        }

        public String getSampleTime() {
            return sampleTime;
        }

        public void setSampleTime(String sampleTime) {
            this.sampleTime = sampleTime;
        }

        public String getSiteId() {
            return siteId;
        }

        public void setSiteId(String siteId) {
            this.siteId = siteId;
        }

        public String getSndDepart() {
            return sndDepart;
        }

        public void setSndDepart(String sndDepart) {
            this.sndDepart = sndDepart;
        }

        public String getSndDoctor() {
            return sndDoctor;
        }

        public void setSndDoctor(String sndDoctor) {
            this.sndDoctor = sndDoctor;
        }

        public String getTestId() {
            return testId;
        }

        public void setTestId(String testId) {
            this.testId = testId;
        }

        public List<RisExamItemReturnListBean> getRisExamItemReturnList() {
            return risExamItemReturnList;
        }

        public void setRisExamItemReturnList(List<RisExamItemReturnListBean> risExamItemReturnList) {
            this.risExamItemReturnList = risExamItemReturnList;
        }

        public static class RisExamItemReturnListBean implements Serializable{
            /**
             * hisId : 709965
             * itemName : 艾滋病抗体
             * itemResult :
             * itemUnit :
             * itemValue : 阴性
             * lowerLimit : 阴性
             * rank :
             * testId : 20180126XNB219
             * upperLimit :
             */

            private String hisId;
            private String itemName;
            private String itemResult;
            private String itemUnit;
            private String itemValue;
            private String lowerLimit;
            private String rank;
            private String testId;
            private String upperLimit;

            public String getHisId() {
                return hisId;
            }

            public void setHisId(String hisId) {
                this.hisId = hisId;
            }

            public String getItemName() {
                return itemName;
            }

            public void setItemName(String itemName) {
                this.itemName = itemName;
            }

            public String getItemResult() {
                return itemResult;
            }

            public void setItemResult(String itemResult) {
                this.itemResult = itemResult;
            }

            public String getItemUnit() {
                return itemUnit;
            }

            public void setItemUnit(String itemUnit) {
                this.itemUnit = itemUnit;
            }

            public String getItemValue() {
                return itemValue;
            }

            public void setItemValue(String itemValue) {
                this.itemValue = itemValue;
            }

            public String getLowerLimit() {
                return lowerLimit;
            }

            public void setLowerLimit(String lowerLimit) {
                this.lowerLimit = lowerLimit;
            }

            public String getRank() {
                return rank;
            }

            public void setRank(String rank) {
                this.rank = rank;
            }

            public String getTestId() {
                return testId;
            }

            public void setTestId(String testId) {
                this.testId = testId;
            }

            public String getUpperLimit() {
                return upperLimit;
            }

            public void setUpperLimit(String upperLimit) {
                this.upperLimit = upperLimit;
            }
        }
    }
}
