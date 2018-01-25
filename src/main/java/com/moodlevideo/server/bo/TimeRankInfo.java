package com.moodlevideo.server.bo;

import java.util.Date;

public class TimeRankInfo {
    private Integer id;

    private String userid;

    private String updateId;

    private String name;

    private Date totaltime;

    private Integer rank;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId == null ? null : updateId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Date getTotaltime() {
        return totaltime;
    }

    public void setTotaltime(Date totaltime) {
        this.totaltime = totaltime;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
    
    @SuppressWarnings("deprecation")
	public int getTotalTimeSeconds(){
    		return totaltime.getHours()*3600+totaltime.getMinutes()*60+totaltime.getSeconds();
    }
}