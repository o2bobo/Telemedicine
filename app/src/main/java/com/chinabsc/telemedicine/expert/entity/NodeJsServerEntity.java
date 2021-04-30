package com.chinabsc.telemedicine.expert.entity;

/**
 * Created by zzw on 2018/4/26.
 */

public class NodeJsServerEntity {

    /**
     * resultCode : 200
     * resultMsg :
     * data : http://192.168.0.19:3000/message-001
     */

    private int resultCode;
    private String resultMsg;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
