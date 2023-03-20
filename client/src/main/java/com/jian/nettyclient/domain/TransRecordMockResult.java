package com.jian.nettyclient.domain;

import java.io.Serializable;
import java.util.Date;

public class TransRecordMockResult implements Serializable {
    private Long id;

    private String ser23;

    private String touchFlag;

    private Date updTime;

    private byte[] req;

    private byte[] resp;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSer23() {
        return ser23;
    }

    public void setSer23(String ser23) {
        this.ser23 = ser23 == null ? null : ser23.trim();
    }

    public String getTouchFlag() {
        return touchFlag;
    }

    public void setTouchFlag(String touchFlag) {
        this.touchFlag = touchFlag == null ? null : touchFlag.trim();
    }

    public Date getUpdTime() {
        return updTime;
    }

    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    public byte[] getReq() {
        return req;
    }

    public void setReq(byte[] req) {
        this.req = req;
    }

    public byte[] getResp() {
        return resp;
    }

    public void setResp(byte[] resp) {
        this.resp = resp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ser23=").append(ser23);
        sb.append(", touchFlag=").append(touchFlag);
        sb.append(", updTime=").append(updTime);
        sb.append(", req=").append(req);
        sb.append(", resp=").append(resp);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}