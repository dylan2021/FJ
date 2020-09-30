package com.android.baihuahu.bean;

import java.io.Serializable;

/**
 * Dylan
 */
public class WageDailyInfo implements Serializable {


    /**
     * deptId : 2
     * pieceWageId : 2
     * workDate : 2019-05-05
     * deptName : 总经办
     * projectName : 测试项目
     * totalHourNum : 20
     * totalHourlyWage : 270
     * totalPieceNum : 40
     * totalPieceWage : 480
     * totalDeduction : 2000
     * totalWage : -1250
     * peopleNum : 4
     * unit : 件
     */
    private int deptId;
    private int pieceWageId;
    private String workDate;
    private String deptName;
    private String projectName;
    private double totalHourNum;
    private double totalHourlyWage;
    private double totalPieceNum;
    private double totalPieceWage;
    private double totalDeduction;
    private double totalWage;
    private int peopleNum;
    private String unit;

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getPieceWageId() {
        return pieceWageId;
    }

    public void setPieceWageId(int pieceWageId) {
        this.pieceWageId = pieceWageId;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public double getTotalHourNum() {
        return totalHourNum;
    }


    public double getTotalHourlyWage() {
        return totalHourlyWage;
    }


    public double getTotalPieceNum() {
        return totalPieceNum;
    }


    public double getTotalPieceWage() {
        return totalPieceWage;
    }


    public double getTotalDeduction() {
        return totalDeduction;
    }


    public double getTotalWage() {
        return totalWage;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
