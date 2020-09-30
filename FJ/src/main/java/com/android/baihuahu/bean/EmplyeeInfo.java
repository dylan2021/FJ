package com.android.baihuahu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Gool
 */
public class EmplyeeInfo implements Serializable {

    private int id;
    private String createTime;
    private String updateTime;
    private int employeeId;
    private String workDate;
    private int pieceWageId;
    private String hourNum;
    private String employeeMobile;
    public String employeeName;
    private String pieceNum;
    private String groupName;
    private int buildSiteId;
    private String buildSiteName;
    private Object attendHour;
    private Object attendMinute;
    private int sectionId;

    private String sectionName;
    private String identityNo;
    private String gender;
    private String age;
    private String education;
    private String address;
    private String workExperience;
    private String certificate;
    private String emergencyContact;
    private String emergencyPhone;
    private List<FileInfo> pic;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }


    public String getEmployeeMobile() {
        return employeeMobile;
    }

    public void setEmployeeMobile(String employeeMobile) {
        this.employeeMobile = employeeMobile;
    }

    private String deduction;
    private String remark;
    private String projectName;
    private int creator;
    private String hourlyWage;
    private String pieceWage;
    private String attendReward;
    private String incomeTax;
    private String taxDeduction;
    private String employeeTypeStr;
    private String deptName;
    private String payableWage;
    private String taxAmount;
    private String phone;
    private double realWage;
    private int deptId;
    private String employeeType;
    private String name;
    private double totalWage;
    private int wageYear;
    private int wageMonth;
    private int type;

    private boolean seleted;
    public boolean getSeleted() {
        return seleted;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public EmplyeeInfo(String name) {
        this.name = name;
    }

    public void setSeleted(boolean seleted) {
        this.seleted = seleted;
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

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public int getPieceWageId() {
        return pieceWageId;
    }

    public void setPieceWageId(int pieceWageId) {
        this.pieceWageId = pieceWageId;
    }

    public String getHourNum() {
        return hourNum;
    }

    public void setHourNum(String hourNum) {
        this.hourNum = hourNum;
    }

    public String getPieceNum() {
        return pieceNum;
    }

    public void setPieceNum(String pieceNum) {
        this.pieceNum = pieceNum;
    }

    public String getDeduction() {
        return deduction;
    }

    public void setDeduction(String deduction) {
        this.deduction = deduction;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(String hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public String getPieceWage() {
        return pieceWage;
    }

    public void setPieceWage(String pieceWage) {
        this.pieceWage = pieceWage;
    }

    public String getAttendReward() {
        return attendReward;
    }

    public void setAttendReward(String attendReward) {
        this.attendReward = attendReward;
    }

    public String getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(String incomeTax) {
        this.incomeTax = incomeTax;
    }

    public String getTaxDeduction() {
        return taxDeduction;
    }

    public void setTaxDeduction(String taxDeduction) {
        this.taxDeduction = taxDeduction;
    }

    public String getEmployeeTypeStr() {
        return employeeTypeStr;
    }

    public void setEmployeeTypeStr(String employeeTypeStr) {
        this.employeeTypeStr = employeeTypeStr;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPayableWage() {
        return payableWage;
    }

    public void setPayableWage(String payableWage) {
        this.payableWage = payableWage;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getRealWage() {
        return realWage;
    }

    public void setRealWage(double realWage) {
        this.realWage = realWage;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.employeeType = employeeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTotalWage() {
        return totalWage;
    }

    public void setTotalWage(double totalWage) {
        this.totalWage = totalWage;
    }

    public int getWageYear() {
        return wageYear;
    }

    public void setWageYear(int wageYear) {
        this.wageYear = wageYear;
    }

    public int getWageMonth() {
        return wageMonth;
    }

    public void setWageMonth(int wageMonth) {
        this.wageMonth = wageMonth;
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

    public Object getAttendHour() {
        return attendHour;
    }

    public void setAttendHour(Object attendHour) {
        this.attendHour = attendHour;
    }

    public Object getAttendMinute() {
        return attendMinute;
    }

    public void setAttendMinute(Object attendMinute) {
        this.attendMinute = attendMinute;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }


    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public List<FileInfo> getPic() {
        return pic;
    }

    public void setPic(List<FileInfo> pic) {
        this.pic = pic;
    }
}
