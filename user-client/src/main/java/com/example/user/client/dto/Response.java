package com.example.user.client.dto;

import java.io.Serializable;

/**
 * 统一响应对象
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误码
     */
    private String errCode;

    /**
     * 错误信息
     */
    private String errMessage;

    /**
     * 返回数据
     */
    private Object data;

    public static Response buildSuccess() {
        return buildSuccess(null);
    }

    public static Response buildSuccess(Object data) {
        Response response = new Response();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static Response buildFailure(String errCode, String errMessage) {
        Response response = new Response();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
