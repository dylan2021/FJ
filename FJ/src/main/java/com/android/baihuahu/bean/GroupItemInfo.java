package com.android.baihuahu.bean;

import java.io.Serializable;

/**
 * Dylan
 */
public class GroupItemInfo implements Serializable {

    private int id;
    private String createTime;
    private String updateTime;
    private int buildSiteId;
    private String name;
    private String code;
    private int planInNum;
    private String planInDate;
    private String planOutDate;
    private int creator;
    private int updator;
    private int deleted;
    private int bizWorkerNum;
    private Object ids;
    private String buildSiteName;
    private int realInNum;
    private String realInDate;
    private String realOutDate;
    private Object bizWorkerVOList;
    private Object groupLeader;
    private Object groupLeaderId;

    public int getBizWorkerNum() {
        return bizWorkerNum;
    }

    public void setBizWorkerNum(int bizWorkerNum) {
        this.bizWorkerNum = bizWorkerNum;
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

    public int getBuildSiteId() {
        return buildSiteId;
    }

    public void setBuildSiteId(int buildSiteId) {
        this.buildSiteId = buildSiteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPlanInNum() {
        return planInNum;
    }

    public void setPlanInNum(int planInNum) {
        this.planInNum = planInNum;
    }

    public String getPlanInDate() {
        return planInDate;
    }

    public void setPlanInDate(String planInDate) {
        this.planInDate = planInDate;
    }

    public String getPlanOutDate() {
        return planOutDate;
    }

    public void setPlanOutDate(String planOutDate) {
        this.planOutDate = planOutDate;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getUpdator() {
        return updator;
    }

    public void setUpdator(int updator) {
        this.updator = updator;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public Object getIds() {
        return ids;
    }

    public void setIds(Object ids) {
        this.ids = ids;
    }

    public String getBuildSiteName() {
        return buildSiteName;
    }

    public void setBuildSiteName(String buildSiteName) {
        this.buildSiteName = buildSiteName;
    }

    public int getRealInNum() {
        return realInNum;
    }

    public void setRealInNum(int realInNum) {
        this.realInNum = realInNum;
    }

    public String getRealInDate() {
        return realInDate;
    }

    public void setRealInDate(String realInDate) {
        this.realInDate = realInDate;
    }

    public String getRealOutDate() {
        return realOutDate;
    }

    public void setRealOutDate(String realOutDate) {
        this.realOutDate = realOutDate;
    }

    public Object getBizWorkerVOList() {
        return bizWorkerVOList;
    }

    public void setBizWorkerVOList(Object bizWorkerVOList) {
        this.bizWorkerVOList = bizWorkerVOList;
    }

    public Object getGroupLeader() {
        return groupLeader;
    }

    public void setGroupLeader(Object groupLeader) {
        this.groupLeader = groupLeader;
    }

    public Object getGroupLeaderId() {
        return groupLeaderId;
    }

    public void setGroupLeaderId(Object groupLeaderId) {
        this.groupLeaderId = groupLeaderId;
    }
}
