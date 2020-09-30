package com.android.baihuahu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Gool
 */
public class DeviceAttendInfo implements Serializable {

    private List<FileInfo> attachment;
    private int id;
    private int status;
    private String createTime;
    private String updateTime;
    private int deviceId;
    private String remark;
    private double period;
    private double wage;
    private int creator;
    private Object updator;
    private String creatorName;
    private String auditRemark;
    private String updatorName;
    private double auditNum;

    public void setPeriod(double period) {
        this.period = period;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getUpdatorName() {
        return updatorName;
    }

    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }

    public double getAuditNum() {
        return auditNum;
    }

    public void setAuditNum(double auditNum) {
        this.auditNum = auditNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<FileInfo> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<FileInfo> attachment) {
        this.attachment = attachment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getPeriod() {
        return period;
    }


    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public Object getUpdator() {
        return updator;
    }

    public void setUpdator(Object updator) {
        this.updator = updator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
