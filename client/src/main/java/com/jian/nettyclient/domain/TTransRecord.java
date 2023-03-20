package com.jian.nettyclient.domain;

import io.netty.util.CharsetUtil;

import java.io.Serializable;

public class TTransRecord implements Serializable {
    private Long id;

    private String ser23;

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
        sb.append(", req=").append(req);
        sb.append(", resp=").append(resp);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public String getReqStr() {
        return this.req == null ? null : new String(req, CharsetUtil.UTF_8);
    }

    public String getRespStr() {
        return this.resp == null ? null : new String(resp, CharsetUtil.UTF_8);
    }
}