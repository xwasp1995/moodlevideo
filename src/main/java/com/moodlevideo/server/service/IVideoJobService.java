package com.moodlevideo.server.service;

import org.springframework.web.servlet.ModelAndView;

import com.moodlevideo.server.bo.StudentAndVideoInfo;
import com.moodlevideo.server.dal.model.BehaviorInfo;
import com.moodlevideo.server.dal.model.SectionInfo;
import com.moodlevideo.server.dal.model.StarRatingInfo;
import com.moodlevideo.server.util.RestModel;

/**
 * video服务接口
 * @version 1.0
 * @author wangxuebin
 */
public interface IVideoJobService {
	
	/**
	 * 获取用户详细及频小节信息信息接口
	 * @param studentInfo 
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getUserAndVideoInfo(StudentAndVideoInfo studentAndVideoInfo) throws Exception;
	
	/**
	 * 获取video网页接口
	 * @param sectionInfo 
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getVideoHtml(SectionInfo sectionInfo) throws Exception;
	
	/**
	 * 视频评星接口
	 * @param StarRatingInfo 
	 * @return
	 * @throws Exception
	 */
	public RestModel videoStarRating(StarRatingInfo starRatingInfo) throws Exception;
	
	/**
	 * 视频播放行为记录
	 * @param BehaviorInfo 
	 * @return
	 * @throws Exception
	 */
	public RestModel videoBehaviorRecord(BehaviorInfo behaviorInfo) throws Exception;
	
	/**
	 * 获得用户观看视频排行榜数据
	 * @param null 
	 * @return
	 * @throws Exception
	 */
	public RestModel getVideoRankData() throws Exception;
}