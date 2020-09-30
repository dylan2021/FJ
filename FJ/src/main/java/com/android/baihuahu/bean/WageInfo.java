package com.android.baihuahu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Dylan
 */
public class WageInfo implements Serializable {


    /**
     * id : 7
     * createTime : 2020-08-01 23:06:55
     * updateTime : 2020-08-01 23:11:11
     * wagesDate : 2020-07-31
     * groupId : 17
     * groupName : JX03
     * times : 16
     * timeWages : 608.0
     * piece : 200
     * pieceWages : 600.0
     * remark : 6月22日施工人员当日工资结算，总共参与2人，计时8小时没人，计件100每人
     * status : 1
     * creator : 1
     * updator : null
     * bizWagesWorkerList : [{"id":16,"createTime":"2020-08-01 23:06:55","updateTime":"2020-08-01 23:06:55","wagesId":7,"type":"1","workerId":4,"workerName":"新人","workType":1,"times":8,"timeWages":304,"piece":100,"pieceWages":300,"creator":1,"updator":null},{"id":17,"createTime":"2020-08-01 23:06:55","updateTime":"2020-08-01 23:06:55","wagesId":7,"type":"1","workerId":5,"workerName":"组长","workType":1,"times":8,"timeWages":304,"piece":100,"pieceWages":300,"creator":1,"updator":null}]
     */

    private int id;
    private String createTime;
    private String updateTime;
    private String wagesDate;
    private int groupId;
    private String groupName;
    private double times;
    private double timeWages;
    private double piece;
    private double pieceWages;
    private String remark;
    private String status;
    private int creator;
    private Object updator;
    private List<BizWagesWorkerListBean> bizWagesWorkerList;

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

    public String getWagesDate() {
        return wagesDate;
    }

    public void setWagesDate(String wagesDate) {
        this.wagesDate = wagesDate;
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

    public double getTimes() {
        return times;
    }

    public void setTimes(double times) {
        this.times = times;
    }

    public double getTimeWages() {
        return timeWages;
    }

    public void setTimeWages(double timeWages) {
        this.timeWages = timeWages;
    }

    public double getPiece() {
        return piece;
    }

    public void setPiece(double piece) {
        this.piece = piece;
    }

    public double getPieceWages() {
        return pieceWages;
    }

    public void setPieceWages(double pieceWages) {
        this.pieceWages = pieceWages;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<BizWagesWorkerListBean> getBizWagesWorkerList() {
        return bizWagesWorkerList;
    }

    public void setBizWagesWorkerList(List<BizWagesWorkerListBean> bizWagesWorkerList) {
        this.bizWagesWorkerList = bizWagesWorkerList;
    }

    public static class BizWagesWorkerListBean {
        /**
         * id : 16
         * createTime : 2020-08-01 23:06:55
         * updateTime : 2020-08-01 23:06:55
         * wagesId : 7
         * type : 1
         * workerId : 4
         * workerName : 新人
         * workType : 1
         * times : 8
         * timeWages : 304.0
         * piece : 100
         * pieceWages : 300.0
         * creator : 1
         * updator : null
         */

        private int id;
        private String createTime;
        private String updateTime;
        private int wagesId;
        private String type;
        private int workerId;
        private String workerName;
        private int workType;
        private int times;
        private double timeWages;
        private int piece;
        private double pieceWages;
        private int creator;
        private Object updator;

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

        public int getWagesId() {
            return wagesId;
        }

        public void setWagesId(int wagesId) {
            this.wagesId = wagesId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getWorkerId() {
            return workerId;
        }

        public void setWorkerId(int workerId) {
            this.workerId = workerId;
        }

        public String getWorkerName() {
            return workerName;
        }

        public void setWorkerName(String workerName) {
            this.workerName = workerName;
        }

        public int getWorkType() {
            return workType;
        }

        public void setWorkType(int workType) {
            this.workType = workType;
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
    }
}
