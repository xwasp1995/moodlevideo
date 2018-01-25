package com.moodlevideo.server.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.moodlevideo.server.bo.StudentAndVideoInfo;
import com.moodlevideo.server.dal.mapper.BehaviorInfoMapper;
import com.moodlevideo.server.dal.mapper.RecommendVideoIndexInfoMapper;
import com.moodlevideo.server.dal.mapper.RecommendVideoInfoMapper;
import com.moodlevideo.server.dal.mapper.SectionInfoMapper;
import com.moodlevideo.server.dal.mapper.StarRatingInfoMapper;
import com.moodlevideo.server.dal.mapper.StudentInfoMapper;
import com.moodlevideo.server.dal.mapper.TimeRankIndexInfoMapper;
import com.moodlevideo.server.dal.mapper.TimeRankInfoMapper;
import com.moodlevideo.server.dal.model.BehaviorInfo;
import com.moodlevideo.server.dal.model.RecommendVideoIndexInfo;
import com.moodlevideo.server.dal.model.RecommendVideoInfo;
import com.moodlevideo.server.dal.model.SectionInfo;
import com.moodlevideo.server.dal.model.StarRatingInfo;
import com.moodlevideo.server.dal.model.StudentInfo;
import com.moodlevideo.server.dal.model.TimeRankIndexInfo;
import com.moodlevideo.server.dal.model.TimeRankInfo;
import com.moodlevideo.server.service.IVideoJobService;
import com.moodlevideo.server.util.BllConstantEnum;
import com.moodlevideo.server.util.RestModel;


/**
 * @author wangxuebin
 * video信息服务
 */
@Service("iVideoJobService")
public class VideoJobServiceImpl implements IVideoJobService {
	private static Logger logger = LoggerFactory.getLogger(VideoJobServiceImpl.class);
	
	@Resource(name = "studentInfoMapper")
	private StudentInfoMapper studentInfoMapper;
	@Resource(name = "sectionInfoMapper")
	private SectionInfoMapper sectionInfoMapper;
	@Resource(name = "starRatingInfoMapper")
	private StarRatingInfoMapper starRatingInfoMapper;
	@Resource(name = "behaviorInfoMapper")
	private BehaviorInfoMapper behaviorInfoMapper;
	@Resource(name = "timeRankIndexInfoMapper")
	private TimeRankIndexInfoMapper timeRankIndexInfoMapper;
	@Resource(name = "timeRankInfoMapper")
	private TimeRankInfoMapper timeRankInfoMapper;
	@Resource(name = "recommendVideoInfoMapper")
	private RecommendVideoInfoMapper recommendVideoInfoMapper;
	@Resource(name = "recommendVideoIndexInfoMapper")
	private  RecommendVideoIndexInfoMapper recommendVideoIndexInfoMapper;
	
	
	// 获取student信息服务以及视频小节信息
	@Override
	public ModelAndView getUserAndVideoInfo(StudentAndVideoInfo studentAndVideoInfo) throws Exception {
		
		// 定义modelandview
		ModelAndView mav=new ModelAndView("video");
		
		if( studentAndVideoInfo.getUserId()!=null ){
			
			StudentInfo studentInfo = studentInfoMapper
					.selectByPrimaryKey( studentAndVideoInfo.getUserId() );
			
			// 检查数据库中是否存在相应UserID的用户，有的话返回相应UserInfo
			if(studentInfo!=null){
				mav.addObject("login_userid", studentInfo.getId());
				mav.addObject("login_username", studentInfo.getName());
				
				SectionInfo sectionInfo = sectionInfoMapper
					.selectByPrimaryKey( studentAndVideoInfo.getSectionId() );
				// 检查数据库中是否存在相应sectionid的视频片段，有的话返回相应sectionInfo
				if(sectionInfo!=null){
					mav.addObject("video_sectionid", sectionInfo.getSectionid());
					mav.addObject("video_url", sectionInfo.getUrl());
					mav.addObject("video_description", sectionInfo.getDescription());
					logger.debug("【获取UserAndVideoInfo信息服务】++++++" + sectionInfo.getUrl().substring(0, 4));
					// 根据所看课程返回不同的moodle平台‘返回’界面
					if (sectionInfo.getUrl().substring(0, 5).equals("beida")) {
						mav.addObject("video_moodle_url", "http://120.77.86.170/moodle/course/view.php?id=11");
					} else if(sectionInfo.getUrl().substring(0, 5).equals("zheda")){
						mav.addObject("video_moodle_url", "http://120.77.86.170/moodle/course/view.php?id=23");
					} else if(sectionInfo.getUrl().substring(0, 5).equals("guofa")){
						mav.addObject("video_moodle_url", "http://120.77.86.170/moodle/course/view.php?id=22");
					}
					logger.debug("【获取UserAndVideoInfo信息服务】***获取的video_url为："+sectionInfo.getUrl());
					
					
					// 查找最近的update_id对应的recommendVideoIndexInfo
					RecommendVideoIndexInfo recommendVideoIndexInfo = 
							recommendVideoIndexInfoMapper.selectByMaxPrimaryKey();
					// 获得相应id用户的最新推荐列表
					ArrayList<RecommendVideoInfo> recommendVideoInfoList = 
						recommendVideoInfoMapper.selectByUseridAndUpdateid
						(studentInfo.getId(), recommendVideoIndexInfo.getUpdateId());
					
				    // 构造传输的推荐列表部分的json串
					ArrayList<HashMap<String, Object>> recommendList = 
							new ArrayList<HashMap<String, Object>>(); 
					for(RecommendVideoInfo recommendInfo : recommendVideoInfoList){
						HashMap<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("video_url", "/moodlevideo/videojob/section?sectionid="
							+ recommendInfo.getVideoId());
						
//						// 构造视频描述的前缀字符串
//						String temp_prefix = "";
//						String courseId = recommendInfo.getVideoId();
//						if (courseId.substring(0, 5).equals("beida")) {
//							temp_prefix = "北大软工_" +  courseId.substring(9);
//						} else if(courseId.substring(0, 5).equals("zheda")){
//							temp_prefix = "浙大软工_" +  courseId.substring(8);
//						} else if(courseId.substring(0, 5).equals("guofa")){
//							temp_prefix = "国防科大软工_" +  courseId.substring(15);
//						}
						tempMap.put("video_description", recommendInfo.getVideoDescription());
						
						recommendList.add(tempMap);
					}
					logger.debug("【获取UserAndVideoInfo信息服务】推荐列表：" + recommendList);
					mav.addObject("recommendList", recommendList);
					
				}else{
					mav.addObject("video_moodle_url", "http://120.77.86.170/moodle/my/");
					logger.debug("【获取UserAndVideoInfo信息服务】***数据库中没有相应sectionid的记录");
				}
				
				
				logger.debug("【获取UserAndVideoInfo信息服务】***获取用户的userId为："+studentInfo.getId());
				return mav;
			}else{
				logger.debug("【获取UserAndVideoInfo信息服务】***数据库中没有相应userId的记录");
			}
		}
	
		// 否则返回null
		return null;
	}
	
	
	// 获取video网页
	@Override
	public ModelAndView getVideoHtml(SectionInfo sectionInfo) throws Exception {
		
		// 定义modelandview
		ModelAndView mav=new ModelAndView("video");
		
		if( sectionInfo.getSectionid()!=null ){
			sectionInfo = sectionInfoMapper.selectByPrimaryKey
					( sectionInfo.getSectionid() );
			// 检查数据库中是否存在相应sectionid的视频片段，有的话返回相应sectionInfo
			if(sectionInfo!=null){
				mav.addObject("video_url", sectionInfo.getUrl());
				mav.addObject("video_description", sectionInfo.getDescription());
				logger.debug("【获得视频片段服务】***获取的video_url为："+sectionInfo.getUrl());
			}else{
				logger.debug("【获得视频片段服务】***数据库中没有相应sectionid的记录");
			}
		}
		
		// 否则仅返回view页面
		return mav;
	}
	
	
	// 视频打分
	@Override
	public RestModel videoStarRating(StarRatingInfo starRatingInfo)
	{
		RestModel restModel = new RestModel();
		int rflag = starRatingInfoMapper.insertSelective(starRatingInfo);
		
		if(rflag == 1){
			restModel = RestModel.getRestModel(BllConstantEnum.RESCODE_0);
			logger.debug("【视频打分】***打分数据插入成功！");
		} else{
			restModel = RestModel.getRestModel(BllConstantEnum.RESCODE_1);
			logger.debug("【视频打分】***打分数据插入失败！");
		}
		
		return restModel;
	}
	
	
	// 视频行为记录
	@Override
	public RestModel videoBehaviorRecord(BehaviorInfo behaviorInfo)
	{
		RestModel restModel = new RestModel();
		int rflag = behaviorInfoMapper.insertSelective(behaviorInfo);
		
		if(rflag == 1){
			restModel = RestModel.getRestModel(BllConstantEnum.RESCODE_0);
			logger.debug("【视频行为记录】***行为数据插入成功！");
		} else{
			restModel = RestModel.getRestModel(BllConstantEnum.RESCODE_1);
			logger.debug("【视频行为记录】***行为数据插入失败！");
		}
		
		return restModel;
	}
	
	// 获得用户观看视频时间排名
	@Override
	public RestModel getVideoRankData()
	{
		RestModel restModel = new RestModel();
		// 查找最大updateId对应的timeRankIndex记录
		TimeRankIndexInfo timeRankIndexInfo =  timeRankIndexInfoMapper.selectByMaxPrimaryKey();
		
		logger.debug("【获得用户观看视频排行榜数据】***最大的updateId={}", timeRankIndexInfo.getUpdateId());
		
		// 查找数据库相应updateId的用户排名列表
		ArrayList<TimeRankInfo> timeRankInfoList = timeRankInfoMapper.selectByUpdateId
				(timeRankIndexInfo.getUpdateId());
		// 提取timeRankInfoList中元素相应字段组成传输List
		ArrayList<HashMap<String, Object>> userRankList = 
				new ArrayList<HashMap<String, Object>>();
		for(TimeRankInfo timeRankInfo: timeRankInfoList){
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("userid", timeRankInfo.getUserid());
			tempMap.put("name", timeRankInfo.getName());
			if(timeRankInfo.getTotaltime()!=null){ //格式化时间
				SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm:ss"); 
				tempMap.put("totaltime", formatter.format( timeRankInfo.getTotaltime() ));
			}else{
				tempMap.put("totaltime",  timeRankInfo.getTotaltime());
			}
			tempMap.put("rank", timeRankInfo.getRank());
			
			userRankList.add(tempMap);
		}
		
		// 构造返回的json串
		HashMap<String, Object> jsonMap = new HashMap<String, Object>(); 
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
		jsonMap.put("rankUpdateTime", formatter.format( timeRankIndexInfo.getUpdateTime() ));
		jsonMap.put("userRank", userRankList);
		
		logger.debug("【获得用户观看视频排行榜数据】***传输的json串为：{}", jsonMap);
		
		restModel = RestModel.getRestModel(BllConstantEnum.RESCODE_0, jsonMap);
		
		return restModel;
	}
		
}
