package com.example.damxat.Notification;

public class ResponseModel {
    public int success;
    public int failure;

    public ResponseModel(int success, int failure) {
        this.success = success;
        this.failure = failure;
    }

    public ResponseModel() {
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }
}
