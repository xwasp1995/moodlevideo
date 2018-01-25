package com.moodlevideo.server.bo;

/**
 * @author wangxuebin
 * 用于getUserAndVideoInfo接口传递参数
 */
public class StudentAndVideoInfo {
	
	private String userId;

    private String password;

    private String sectionId;

    
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
    
    

}
