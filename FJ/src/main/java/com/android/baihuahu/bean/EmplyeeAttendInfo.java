package com.android.baihuahu.bean;

import java.io.Serializable;

/**
 * Gool
 */
public class EmplyeeAttendInfo implements Serializable {
    private double attendHours;
    private String startTime;
    private String endTime;
    private String attendName;
    private String deptName;
    private String remark;
    /**
     * id : 4
     * createTime : 2019-12-03 10:40:21
     * updateTime : 2019-12-03 10:40:21
     * workerId : 1
     * groupId : 3
     * remark : null
     * creator : 1
     * updator : null
     * groupName : 01班组
     * buildSiteId : 1
     * buildSiteName : 房建1
     * attendHour : 121
     * attendMinute : 0
     * creatorName : 超级管理员
     */

    private int id;
    private String createTime;
    private String updateTime;
    private int workerId;
    private int groupId;
    private String groupName;
    private int buildSiteId;
    private String buildSiteName;
    private int attendHour;
    private int attendMinute;
    private String creatorName;

    public String getAttendName() {
        return attendName;
    }

    public void setAttendName(String attendName) {
        this.attendName = attendName;
    }

    public EmplyeeAttendInfo(String employeeName) {
        this.attendHours = attendHours;
    }

    public double getAttendHours() {
        return attendHours;
    }

    public void setAttendHours(double attendHours) {
        this.attendHours = attendHours;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getBuildSiteId() {
        return buildSiteId;
    }

    public void setBuildSiteId(int buildSiteId) {
        this.buildSiteId = buildSiteId;
    }

    public String getBuildSiteName() {
        return buildSiteName;
    }

    public void setBuildSiteName(String buildSiteName) {
        this.buildSiteName = buildSiteName;
    }

    public int getAttendHour() {
        return attendHour;
    }

    public void setAttendHour(int attendHour) {
        this.attendHour = attendHour;
    }

    public int getAttendMinute() {
        return attendMinute;
    }

    public void setAttendMinute(int attendMinute) {
        this.attendMinute = attendMinute;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
