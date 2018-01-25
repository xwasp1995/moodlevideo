package com.moodlevideo.server.bo;

/**
 * @author wangxuebin
 * 用于StudentVideoStatistics类,‘知识点-课程’映射信息
 */
public class KnowledgeLessonMapInfo {
	
	private String  lessonId; // 知识点Id
	private String sectionId; // 视频片段Id
	private float     weight; // 权重
	
	
	public String getLessonId() {
		return lessonId;
	}
	public void setLessonId(String lessonId) {
		this.lessonId = lessonId;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float wight) {
		this.weight = wight;
	}

}
