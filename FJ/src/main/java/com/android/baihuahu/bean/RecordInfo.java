package com.android.baihuahu.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
Dylan
 */

public class RecordInfo {

    public String rectifyOpinion;
    public String feedback;
    public String checkOpinion;
    public int checkStatus;
    private int id;
    private String createTime;
    private String updateTime;
    private int safetyProductId;
    private String type;
    private int status;
    private int groupId;
    private int workerId;
    private double receiveNum;//领用数量
    private double useNum;//实际数量
    private String useRemark;
    private String remark;
    private int creator;
    private String updatorName;
    private String groupName;
    private String workerName;
    private String auditNum;
    private String auditRemark;
    private String reportNum;
    private String reportRemark;
    private String confirmResult;
    private String comfirmRemark;
    private String creatorName;
    private String numberPlate;
    private List<FileInfo> pic;
    private List<FileInfo> attachment;
    /**
     * planId : 1
     * writeCycle : 8
     * writeValue : 100.0
     * writePicture : ["/opt/image1"]
     * writeAttachment : [{"url":"/opt/image1","name":"test12"},{"url":"/opt/image2","name":"test22"}]
     * writor : 1
     * writeTime : 2020-07-29 22:51:54
     * signValue : null
     * signPicture : null
     * signAttachment : null
     * signor : null
     * signTime : null
     * status : 0
     * creator : null
     * updator : null
     */

    private int planId;
    private int writeCycle;
    private double writeValue;
    private int writor;
    private String writeTime;
    private double signValue;
    private String signor;
    private String writorName;
    private String signorName;
    private String signTime;
    private String updator;
    private List<FileInfo> writeAttachment;

    public String getWritorName() {
        return writorName;
    }

    public void setWritorName(String writorName) {
        this.writorName = writorName;
    }

    public String getSignorName() {
        return signorName;
    }

    public void setSignorName(String signorName) {
        this.signorName = signorName;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    public String getReportNum() {
        return reportNum;
    }

    public void setReportNum(String reportNum) {
        this.reportNum = reportNum;
    }

    public String getReportRemark() {
        return reportRemark;
    }

    public void setReportRemark(String reportRemark) {
        this.reportRemark = reportRemark;
    }

    public String getConfirmResult() {
        return confirmResult;
    }

    public void setConfirmResult(String confirmResult) {
        this.confirmResult = confirmResult;
    }

    public String getComfirmRemark() {
        return comfirmRemark;
    }

    public void setComfirmRemark(String comfirmRemark) {
        this.comfirmRemark = comfirmRemark;
    }

    public List<FileInfo> getPic() {
        return pic;
    }

    public void setPic(List<FileInfo> pic) {
        this.pic = pic;
    }

    public List<FileInfo> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<FileInfo> attachment) {
        this.attachment = attachment;
    }

    public String getUpdatorName() {
        return updatorName;
    }

    public void setUpdatorName(String updatorName) {
        this.updatorName = updatorName;
    }

    public String getUseRemark() {
        return useRemark;
    }

    public void setUseRemark(String useRemark) {
        this.useRemark = useRemark;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getAuditNum() {
        return auditNum;
    }

    public void setAuditNum(String auditNum) {
        this.auditNum = auditNum;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public int getSafetyProductId() {
        return safetyProductId;
    }

    public void setSafetyProductId(int safetyProductId) {
        this.safetyProductId = safetyProductId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public double getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(double receiveNum) {
        this.receiveNum = receiveNum;
    }

    public double getUseNum() {
        return useNum;
    }

    public void setUseNum(double useNum) {
        this.useNum = useNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getWriteCycle() {
        return writeCycle;
    }

    public void setWriteCycle(int writeCycle) {
        this.writeCycle = writeCycle;
    }

    public double getWriteValue() {
        return writeValue;
    }

    public void setWriteValue(double writeValue) {
        this.writeValue = writeValue;
    }

    public int getWritor() {
        return writor;
    }

    public void setWritor(int writor) {
        this.writor = writor;
    }

    public String getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(String writeTime) {
        this.writeTime = writeTime;
    }

    public Object getSignValue() {
        return signValue;
    }

    public void setSignValue(double signValue) {
        this.signValue = signValue;
    }

    public Object getSignor() {
        return signor;
    }

    public void setSignor(String signor) {
        this.signor = signor;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }



    public Object getUpdator() {
        return updator;
    }



    public List<FileInfo> getWriteAttachment() {
        return writeAttachment;
    }

    public void setWriteAttachment(List<FileInfo> writeAttachment) {
        this.writeAttachment = writeAttachment;
    }
}