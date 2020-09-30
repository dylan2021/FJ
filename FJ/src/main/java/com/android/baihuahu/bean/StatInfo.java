package com.android.baihuahu.bean;

import java.io.Serializable;
import java.util.List;

/**
 */
public class StatInfo implements Serializable {

    private int id;
    private String name;
    private List<EmplyeeInfo> details;

    public StatInfo(int id, String name, List<EmplyeeInfo> details) {
        this.id = id;
        this.name = name;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EmplyeeInfo> getDetails() {
        return details;
    }

    public void setDetails(List<EmplyeeInfo> details) {
        this.details = details;
    }

}
