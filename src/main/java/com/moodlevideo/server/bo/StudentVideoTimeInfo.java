package com.moodlevideo.server.bo;


import java.sql.Timestamp;

/**
 * @author wangxuebin
 * 用于StudentVideoStatistics类,用户每条视频的观看记录信息
 */
public class StudentVideoTimeInfo {

	private String sectionId;    // 视频id
	private String sectionTile;  // 视频标题
	private int watchTime; // 视频观看时间(秒数)
	private int videoTime; // 视频本身时间(秒数)
	
	
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getSectionTile() {
		return sectionTile;
	}
	public void setSectionTile(String sectionTile) {
		this.sectionTile = sectionTile;
	}
	public int getWatchTime() {
		return watchTime;
	}
	public void setWatchTime(int watchTime) {
		this.watchTime = watchTime;
	}
	public int getVideoTime() {
		return videoTime;
	}
	public void setVideoTime(int videoTime) {
		this.videoTime = videoTime;
	}
	
	
}
