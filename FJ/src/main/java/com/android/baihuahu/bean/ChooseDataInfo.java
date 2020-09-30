package com.android.baihuahu.bean;

import java.util.List;

/**
 * Dylan
 */
public class ChooseDataInfo {
    private int id;
    private String createTime;
    private String updateTime;
    private String code;
    private String name;
    private String planBeginDate;
    private String planEndDate;
    private double invest;
    private String description;
    private double length;
    private List<FileInfo> pic;
    private String startChainage;
    private String endChainage;
    private String startLocation;
    private String endLocation;
    private int creator;
    private String provinceId;
    private String cityId;
    private String constructionUnitId;
    private String supervisionUnitId;
    private String contractUnitId;
    private int deleted;
    private String provinceName;
    public double buildHeight;
    public double landArea;
    public double buildArea;
    private double latitude;
    private double longitude;
    private String sectionName;
    private String cityName;
    private String headerName;
    private String headerPhone;
    private String constructionUnitName;
    private List<FileInfo> attachment;
    private List<Object> bizSectionVOList;
    private List<Object> bizBuildSiteVOList;
    private String projectName;
    private String contractUnitName;
    private String supervisionUnitName;
    private String designUnitName;


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    private String designUnitId;

    public String getDesignUnitId() {
        return designUnitId;
    }

    public void setDesignUnitId(String designUnitId) {
        this.designUnitId = designUnitId;
    }

    public String getSupervisionUnitId() {
        return supervisionUnitId;
    }

    public void setSupervisionUnitId(String supervisionUnitId) {
        this.supervisionUnitId = supervisionUnitId;
    }

    public String getContractUnitId() {
        return contractUnitId;
    }

    public void setContractUnitId(String contractUnitId) {
        this.contractUnitId = contractUnitId;
    }

    public String getDesignUnitName() {
        return designUnitName;
    }

    public void setDesignUnitName(String designUnitName) {
        this.designUnitName = designUnitName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderPhone() {
        return headerPhone;
    }

    public void setHeaderPhone(String headerPhone) {
        this.headerPhone = headerPhone;
    }

    public String getStartChainage() {
        return startChainage;
    }

    public void setStartChainage(String startChainage) {
        this.startChainage = startChainage;
    }

    public String getEndChainage() {
        return endChainage;
    }

    public void setEndChainage(String endChainage) {
        this.endChainage = endChainage;
    }

    public List<Object> getBizBuildSiteVOList() {
        return bizBuildSiteVOList;
    }

    public void setBizBuildSiteVOList(List<Object> bizBuildSiteVOList) {
        this.bizBuildSiteVOList = bizBuildSiteVOList;
    }

    public List<Object> getBizSectionVOList() {
        return bizSectionVOList;
    }

    public void setBizSectionVOList(List<Object> bizSectionVOList) {
        this.bizSectionVOList = bizSectionVOList;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanBeginDate() {
        return planBeginDate;
    }

    public void setPlanBeginDate(String planBeginDate) {
        this.planBeginDate = planBeginDate;
    }

    public String getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(String planEndDate) {
        this.planEndDate = planEndDate;
    }

    public double getInvest() {
        return invest;
    }

    public void setInvest(double invest) {
        this.invest = invest;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public List<FileInfo> getPic() {
        return pic;
    }

    public void setPic(List<FileInfo> pic) {
        this.pic = pic;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getConstructionUnitId() {
        return constructionUnitId;
    }

    public void setConstructionUnitId(String constructionUnitId) {
        this.constructionUnitId = constructionUnitId;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getConstructionUnitName() {
        return constructionUnitName;
    }

    public void setConstructionUnitName(String constructionUnitName) {
        this.constructionUnitName = constructionUnitName;
    }

    public List<FileInfo> getAttachment() {
        return attachment;
    }

    public void setAttachment(List<FileInfo> attachment) {
        this.attachment = attachment;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getContractUnitName() {
        return contractUnitName;
    }

    public void setContractUnitName(String contractUnitName) {
        this.contractUnitName = contractUnitName;
    }

    public String getSupervisionUnitName() {
        return supervisionUnitName;
    }

    public void setSupervisionUnitName(String supervisionUnitName) {
        this.supervisionUnitName = supervisionUnitName;
    }
}
