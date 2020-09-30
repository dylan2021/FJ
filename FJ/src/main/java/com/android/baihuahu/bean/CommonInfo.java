package com.android.baihuahu.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Dylan
 */
public class CommonInfo implements Serializable {


    public String rectifyStatus;
    public String checkStatus;
    public String signer;//签收人编号
    public String signerName;//签收人姓名

    public String rectifyDate;
    public String buildLocation;
    public String buildOrg;
    public String deliveryDate;
    public String deliverer;
    public String receiver;
    public String buildContent;
    public String deliveryContent;
    public List<FileInfo> deliveryPic;
    public int areaId;
    public String qualityId;
    public String securityId;
    public String inspectArea;
    public String inspectInfo;
    public String inspectStatus;
    public int isdanger;
    public int inspectResult;
    public String dangerDesc;
    public List<FileInfo> attachment;
    public List<FileInfo> dangerPic;

    public int id;
    private String createTime;
    private String updateTime;
    private String groupName;
    private String wagesDate;
    private int projectId;
    private String name;
    private String pname;
    private String theme;
    private String content;
    private int thirdBillId;

    public int getThirdBillId() {
        return thirdBillId;
    }

    private String dangerStatue;
    public String inspectorName;
    public String inspector;
    private String contractNo;//合同编号
    private String category;//型号 种类?
    private int manufacturer;//厂商
    private String productionDate;
    private int leasePeriod;
    private int unitPrice;
    private Object appendPeriod;
    private int leaseCompany;//租赁公司
    private String leasePhone;
    private String leaseContact;
    private int status;
    private int peopleNum;
    private String inspectDate;
    private String detailName;
    private String pstartDate;
    private String pendDate;
    private double pcycle;
    private double writeValue;
    private double writeProgress;
    private double period;
    private double wage;
    private double pvalue;
    private double stageProgress;//本阶段累计产值
    private Object inNum;
    private String inDate;
    private Object outDate;
    private String operator;
    private String operatorPhone;
    private String operatorIdentityNo;
    private String operatorCertificateNo;
    private String remark;
    private int creator;
    private String manufacturerName;
    private int price;
    private String leaseCompanyName;
    private String billNo;
    private String CreatorName;
    private Object bizDeviceAttendVOList;
    private List<BizPlanHisListBean> bizPlanHisList;
    public List<FileInfo> pic;
    public List<FileInfo> inspectPic;

    public String getDetailName() {
        return detailName;
    }

    public String getContent() {
        return content;
    }

    public String getDangerStatue() {
        return dangerStatue;
    }


    public int getInspectResult() {
        return inspectResult;
    }

    public String getInspectDate() {
        return inspectDate;
    }

    public void setInspectDate(String inspectDate) {
        this.inspectDate = inspectDate;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    /**
     * times : 16
     * timeWages : 608.0
     * piece : 200
     * pieceWages : 600.0
     */

    private int times;
    private double timeWages;
    private int piece;
    private double pieceWages;


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getWagesDate() {
        return wagesDate;
    }

    public void setWagesDate(String wagesDate) {
        this.wagesDate = wagesDate;
    }

    public double getWriteValue() {
        return writeValue;
    }

    public void setWriteValue(double writeValue) {
        this.writeValue = writeValue;
    }

    public double getWriteProgress() {
        return writeProgress;
    }

    public void setWriteProgress(double writeProgress) {
        this.writeProgress = writeProgress;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public String getCreatorName() {
        return CreatorName;
    }

    public void setCreatorName(String creatorName) {
        CreatorName = creatorName;
    }

    public String getPname() {
        return pname;
    }

    public String getPstartDate() {
        return pstartDate;
    }

    public void setPstartDate(String pstartDate) {
        this.pstartDate = pstartDate;
    }

    public String getPendDate() {
        return pendDate;
    }

    public void setPendDate(String pendDate) {
        this.pendDate = pendDate;
    }

    public double getPcycle() {
        return pcycle;
    }

    public void setPcycle(double pcycle) {
        this.pcycle = pcycle;
    }

    public double getPvalue() {
        return pvalue;
    }

    public void setPvalue(double pvalue) {
        this.pvalue = pvalue;
    }

    public double getStageProgress() {
        return stageProgress;
    }

    public void setStageProgress(double stageProgress) {
        this.stageProgress = stageProgress;
    }

    public void setPname(String pname) {
        this.pname = pname;
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

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(int manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public int getLeasePeriod() {
        return leasePeriod;
    }

    public void setLeasePeriod(int leasePeriod) {
        this.leasePeriod = leasePeriod;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Object getAppendPeriod() {
        return appendPeriod;
    }

    public void setAppendPeriod(Object appendPeriod) {
        this.appendPeriod = appendPeriod;
    }

    public int getLeaseCompany() {
        return leaseCompany;
    }

    public void setLeaseCompany(int leaseCompany) {
        this.leaseCompany = leaseCompany;
    }

    public String getLeasePhone() {
        return leasePhone;
    }

    public void setLeasePhone(String leasePhone) {
        this.leasePhone = leasePhone;
    }

    public String getLeaseContact() {
        return leaseContact;
    }

    public void setLeaseContact(String leaseContact) {
        this.leaseContact = leaseContact;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getInNum() {
        return inNum;
    }

    public void setInNum(Object inNum) {
        this.inNum = inNum;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public Object getOutDate() {
        return outDate;
    }

    public void setOutDate(Object outDate) {
        this.outDate = outDate;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorPhone() {
        return operatorPhone;
    }

    public void setOperatorPhone(String operatorPhone) {
        this.operatorPhone = operatorPhone;
    }

    public String getOperatorIdentityNo() {
        return operatorIdentityNo;
    }

    public void setOperatorIdentityNo(String operatorIdentityNo) {
        this.operatorIdentityNo = operatorIdentityNo;
    }

    public String getOperatorCertificateNo() {
        return operatorCertificateNo;
    }

    public void setOperatorCertificateNo(String operatorCertificateNo) {
        this.operatorCertificateNo = operatorCertificateNo;
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

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLeaseCompanyName() {
        return leaseCompanyName;
    }

    public void setLeaseCompanyName(String leaseCompanyName) {
        this.leaseCompanyName = leaseCompanyName;
    }

    public Object getBizDeviceAttendVOList() {
        return bizDeviceAttendVOList;
    }

    public void setBizDeviceAttendVOList(Object bizDeviceAttendVOList) {
        this.bizDeviceAttendVOList = bizDeviceAttendVOList;
    }

    public List<BizPlanHisListBean> getBizPlanHisList() {
        return bizPlanHisList;
    }

    public void setBizPlanHisList(List<BizPlanHisListBean> bizPlanHisList) {
        this.bizPlanHisList = bizPlanHisList;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public double getTimeWages() {
        return timeWages;
    }

    public void setTimeWages(double timeWages) {
        this.timeWages = timeWages;
    }

    public int getPiece() {
        return piece;
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }

    public double getPieceWages() {
        return pieceWages;
    }

    public void setPieceWages(double pieceWages) {
        this.pieceWages = pieceWages;
    }

    public static class BizPlanHisListBean {
        /**
         * id : 5
         * createTime : 2020-07-28 17:05:38
         * updateTime : 2020-07-28 17:05:38
         * planId : 1
         * oname : 测试进度计划
         * ostartdate : 2020-07-27
         * oenddate : 2020-07-27
         * ocycle : 2
         * ovalue : 100.0
         * nname : 测试进度计划3
         * nstartdate : 2020-07-23
         * nenddate : 2020-08-01
         * ncycle : 18
         * nvalue : 130.0
         * updateCause : 测试变更
         * creator : null
         * updator : 1
         */

        @SerializedName("id")
        private int idX;
        @SerializedName("createTime")
        private String createTimeX;
        @SerializedName("updateTime")
        private String updateTimeX;
        private int planId;
        private String oname;
        private String ostartdate;
        private String oenddate;
        private int ocycle;
        private double ovalue;
        private String nname;
        private String nstartdate;
        private String nenddate;
        private int ncycle;
        private double nvalue;
        private String updateCause;
        @SerializedName("creator")
        private Object creatorX;
        @SerializedName("updator")
        private int updatorX;
        private List<FileInfo> attachment;

        public List<FileInfo> getAttachment() {
            return attachment;
        }

        public void setAttachment(List<FileInfo> attachment) {
            this.attachment = attachment;
        }

        public int getIdX() {
            return idX;
        }

        public void setIdX(int idX) {
            this.idX = idX;
        }

        public String getCreateTimeX() {
            return createTimeX;
        }

        public void setCreateTimeX(String createTimeX) {
            this.createTimeX = createTimeX;
        }

        public String getUpdateTimeX() {
            return updateTimeX;
        }

        public void setUpdateTimeX(String updateTimeX) {
            this.updateTimeX = updateTimeX;
        }

        public int getPlanId() {
            return planId;
        }

        public void setPlanId(int planId) {
            this.planId = planId;
        }

        public String getOname() {
            return oname;
        }

        public void setOname(String oname) {
            this.oname = oname;
        }

        public String getOstartdate() {
            return ostartdate;
        }

        public void setOstartdate(String ostartdate) {
            this.ostartdate = ostartdate;
        }

        public String getOenddate() {
            return oenddate;
        }

        public void setOenddate(String oenddate) {
            this.oenddate = oenddate;
        }

        public int getOcycle() {
            return ocycle;
        }

        public void setOcycle(int ocycle) {
            this.ocycle = ocycle;
        }

        public double getOvalue() {
            return ovalue;
        }

        public void setOvalue(double ovalue) {
            this.ovalue = ovalue;
        }

        public String getNname() {
            return nname;
        }

        public void setNname(String nname) {
            this.nname = nname;
        }

        public String getNstartdate() {
            return nstartdate;
        }

        public void setNstartdate(String nstartdate) {
            this.nstartdate = nstartdate;
        }

        public String getNenddate() {
            return nenddate;
        }

        public void setNenddate(String nenddate) {
            this.nenddate = nenddate;
        }

        public int getNcycle() {
            return ncycle;
        }

        public void setNcycle(int ncycle) {
            this.ncycle = ncycle;
        }

        public double getNvalue() {
            return nvalue;
        }

        public void setNvalue(double nvalue) {
            this.nvalue = nvalue;
        }

        public String getUpdateCause() {
            return updateCause;
        }

        public void setUpdateCause(String updateCause) {
            this.updateCause = updateCause;
        }

        public Object getCreatorX() {
            return creatorX;
        }

        public void setCreatorX(Object creatorX) {
            this.creatorX = creatorX;
        }

        public int getUpdatorX() {
            return updatorX;
        }

        public void setUpdatorX(int updatorX) {
            this.updatorX = updatorX;
        }
    }
}
