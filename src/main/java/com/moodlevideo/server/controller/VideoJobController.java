package com.moodlevideo.server.controller;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.moodlevideo.server.bo.StudentAndVideoInfo;
import com.moodlevideo.server.dal.model.BehaviorInfo;
import com.moodlevideo.server.dal.model.StarRatingInfo;
import com.moodlevideo.server.dal.model.StudentInfo;
import com.moodlevideo.server.service.IVideoJobService;
import com.moodlevideo.server.task.StudentVideoStatistics;
import com.moodlevideo.server.util.BllConstantEnum;
import com.moodlevideo.server.util.HttpUtil;
import com.moodlevideo.server.util.RestModel;
import com.moodlevideo.server.util.RestModelToJson;


/**
 * @author  wangxuebin
 * video相关服务
 * 参考：**。doc
 */
@Controller
@RequestMapping("videojob")
public class VideoJobController {
	private static Logger logger = LoggerFactory
			.getLogger(VideoJobController.class);
	
	@Resource(name = "iVideoJobService")
	private IVideoJobService iVideoJobService;

	@RequestMapping("upload")
	public String upload() {
		return "videoqqq";
	}

	@RequestMapping("uploadPic")
	@ResponseBody
	public String uploadPic(String data, HttpServletRequest request) {
		try {
			String serverPath = request.getSession().getServletContext().getRealPath("/");
	        Base64 base64 = new Base64();
	        byte[] k = base64.decode(data.substring("data:image/png;base64," .length()));  
            InputStream is = new ByteArrayInputStream(k);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            String fileName = simpleDateFormat.format(new Date());  
            String imgFilePath = serverPath + "\\upload\\" + fileName + ".png";  
            double ratio = 1.0;  
            BufferedImage image = ImageIO.read(is);  
            int newWidth = (int) (image.getWidth() * ratio);  
            int newHeight = (int) (image.getHeight() * ratio);  
            Image newimage = image.getScaledInstance(newWidth, newHeight,  
            Image.SCALE_SMOOTH);  
            BufferedImage tag = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);  
            Graphics g = tag.getGraphics();  
            g.drawImage(newimage, 0, 0, null);  
            g.dispose();  
            ImageIO.write(tag, "png", new File(imgFilePath));
		} catch (Exception e) {
			e.printStackTrace();
			return "2";
		}
		return "1";
	}
	
	// 获取登录页面
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView getLoginPage(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【获取登录页面Get】 start ----->");
		String json = null;
		
		ModelAndView mav = new ModelAndView("login");
		
		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			
			StudentInfo  studentInfo = new StudentInfo();
			String sectionid = request.getParameter("sectionid");
			
			String body = "userid=" + studentInfo.getId();
			
			logger.debug("【获取登录页面Get】请求消来源={}，请求消息IP={}", userAgent, clientIp);
			logger.debug("【获取登录页面Get】请求消息体是：{}", body);

			
			mav.addObject("sectionid", sectionid);
			
		} catch (Exception e) {
			logger.error(String.format("【获取登录页面Get】请求失败，消息体=%s", json), e);
		} finally {
			logger.debug("【获取登录页面Get】 返回的mav是： " + mav.toString());
		}
		logger.debug(">>> 【获取登录页面Get】 end <<<");
		
		return mav;
	}
	
	// 登录界面post请求
	@RequestMapping(value = "/section/sectionid={sectionId}", method = RequestMethod.POST)
	public ModelAndView postLoginPage(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【登录界面post请求】 start ----->");
		String json = null;
		
		ModelAndView mav = new ModelAndView("video");
		
		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			
			StudentAndVideoInfo  studentAndVideoInfo = 
					new StudentAndVideoInfo();
			studentAndVideoInfo.setUserId( request.getParameter("userid") );
			String sectionId = request.getParameter("sectionid");
			if(sectionId!="" && sectionId!=null){
				studentAndVideoInfo.setSectionId( sectionId );
			}
			/*else{ // 没有穿sectionId就默认传beidawlf_01_01_01
				studentAndVideoInfo.setSectionId("beidawlf_01_01_01");
			}*/
			
			String body = "userid=" + studentAndVideoInfo.getUserId()
						+ ",sectionid=" + studentAndVideoInfo.getSectionId();
			
			logger.debug("【登录界面post请求】请求消来源={}，请求消息IP={}", userAgent, clientIp);
			logger.debug("【登录界面post请求】请求消息体是：{}", body);

			// 业务操作
			mav = iVideoJobService.getUserAndVideoInfo(studentAndVideoInfo);
			if(mav == null){ // 没有相应Userid用户
				mav = new ModelAndView("login");
				mav.addObject("login_error", "学号或密码不正确，请重新输入！");
				mav.addObject("sectionid", studentAndVideoInfo.getSectionId());
			}
			
		} catch (Exception e) {
			logger.error(String.format("【登录界面post请求】请求失败，消息体=%s", json), e);
		} finally {
			// HttpUtil.outStr(response, "");
			logger.debug("【登录界面post请求】 返回的mav是： " + mav.toString());
		}
		logger.debug(">>> 【登录界面post请求】 end <<<");
		
		return mav;
	}
	
	// 获得视频网页服务-登录转入
	@RequestMapping(value = "/section", method = RequestMethod.GET)
	public ModelAndView  videoSectionLogin(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【获得视频片段服务Get】 start ----->");
		String json = null;
		
		ModelAndView mav = new ModelAndView("login");
		
		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			
			String sectionid = request.getParameter("sectionid");
			String body = "sectionid=" + sectionid;
			
			logger.debug("【获得视频片段服务Get】请求消来源={}，请求消息IP={}", userAgent, clientIp);
			logger.debug("【获得视频片段服务Get】请求消息体是：{}", body);

			// 业务操作
			mav.addObject("sectionid", sectionid);
			
		} catch (Exception e) {
			logger.error(String.format("【获得视频片段服务Get】请求失败，消息体=%s", json), e);
		} finally {
			logger.debug("【获得视频片段服务Get】 返回的mav是： " + mav.toString());
		}
		logger.debug(">>> 【获得视频片段服务Get】 end <<<");
		
		return mav;
	}
	
	// 视频打分
	@RequestMapping(value = "/rating", method = RequestMethod.POST)
	public RestModel  videoStarRating(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【视频打分】 start ----->");
		String json = null;
		RestModel restModel = null;
		//ModelAndView mav = new ModelAndView("section");

		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			StarRatingInfo starRatingInfo = new StarRatingInfo();
			
			starRatingInfo.setUserid( request.getParameter("userid") );
			starRatingInfo.setSectionid( request.getParameter("sectionid") );
			starRatingInfo.setGrade( Float.parseFloat
					(request.getParameter("stargrade")) );
			starRatingInfo.setVideotime( request.getParameter("videotime") );
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			starRatingInfo.setHappentime( sdf.parse
					(request.getParameter("happentime")) );
			
			String body = "userid=" + starRatingInfo.getUserid()
					+",sectionid=" + starRatingInfo.getSectionid()
					+",grade=" + starRatingInfo.getGrade()
					+",videotime=" + starRatingInfo.getVideotime()
					+",happentime=" + starRatingInfo.getHappentime();
			
			logger.debug("【视频打分】请求消息体是：{}", body);
			logger.debug("【视频打分】请求消来源={}，请求消息IP={}", userAgent, clientIp);

			// 业务操作
			restModel = iVideoJobService.videoStarRating(starRatingInfo);
			
		} catch (Exception e) {
			logger.error(String.format("【视频打分】请求失败，消息体=%s", json), e);
		} finally {
			HttpUtil.outStr(response, RestModelToJson
						.getRestJosn(restModel));
			logger.debug("【视频打分】 返回的RestModel是： " + restModel.toString());
		}
		logger.debug(">>> 【视频打分】 end <<<");
		
		return null;
	}
	
	// 视频行为记录
	@RequestMapping(value = "/behavior", method = RequestMethod.POST)
	public RestModel  videoBehaviorRecord(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【视频行为记录】 start ----->");
		String json = null;
		RestModel restModel = null;
		//ModelAndView mav = new ModelAndView("section");

		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			BehaviorInfo behaviorInfo = new BehaviorInfo();
			
			behaviorInfo.setUserid( request.getParameter("userid") );
			behaviorInfo.setSectionid( request.getParameter("sectionid") );
			behaviorInfo.setBehave( Integer.parseInt(request.getParameter("behave")) );
			behaviorInfo.setStarttime( request.getParameter("starttime") );
			behaviorInfo.setEndtime( request.getParameter("endtime") );
			behaviorInfo.setDuration( request.getParameter("duration") );
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			behaviorInfo.setHappentime( sdf.parse(request.getParameter("happentime")) );
			
			String body = "userid=" + behaviorInfo.getUserid()
					+",sectionid=" + behaviorInfo.getSectionid()
					+",behave=" + behaviorInfo.getBehave()
					+",starttime=" + behaviorInfo.getStarttime()
					+",endtime=" + behaviorInfo.getEndtime()
					+",duration=" + behaviorInfo.getDuration()
					+",happenime=" + behaviorInfo.getHappentime();
			
			logger.debug("【视频行为记录】请求消息体是：{}", body);
			logger.debug("【视频行为记录】请求消来源={}，请求消息IP={}", userAgent, clientIp);

			// 业务操作
			restModel = iVideoJobService.videoBehaviorRecord(behaviorInfo);
			
		} catch (Exception e) {
			logger.error(String.format("【视频行为记录】请求失败，消息体=%s", json), e);
		} finally {
			HttpUtil.outStr(response, RestModelToJson
						.getRestJosn(restModel));
			logger.debug("【视频行为记录】 返回的RestModel是： " + restModel.toString());
		}
		logger.debug(">>> 【视频行为记录】 end <<<");
		
		return null;
	}
	
	// 获得用户观看视频排行数据
	@RequestMapping(value = "/getvideorankdata")
	public RestModel  getVideoRankData(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【获得用户观看视频排行榜数据】 start ----->");
		String json = null;
		RestModel restModel = null;
		String callbackStr = null;
		//ModelAndView mav = new ModelAndView("section");

		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			
			callbackStr = request.getParameter("jsoncallback");
			
			String body = "jsoncallback="+callbackStr;
			
			logger.debug("【获得用户观看视频排行榜数据】请求消息体是：{}", body);
			logger.debug("【获得用户观看视频排行榜数据】请求消来源={}，请求消息IP={}", userAgent, clientIp);

			// 业务操作
			restModel = iVideoJobService.getVideoRankData();
			
		} catch (Exception e) {
			logger.error(String.format("【获得用户观看视频排行榜数据】请求失败，消息体=%s", json), e);
		} finally {
			
			String msg_str = callbackStr + "(" + RestModelToJson
					.getRestJosn(restModel) + ")"; // 加入callback标识
			HttpUtil.outStr(response, msg_str);
			
			logger.debug("【获得用户观看视频排行榜数据】 返回的RestModel是： " + restModel.toString());
		}
		logger.debug(">>> 【获得用户观看视频排行榜数据】 end <<<");
		
		return null;
	}
	
	// 获得观看视频排行榜
	@RequestMapping(value = "/section/videorank", method = RequestMethod.GET)
	public ModelAndView  getVideoRank(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【获得用户观看视频排行页面】 start ----->");
		String json = null;
		
		ModelAndView mav = new ModelAndView("videorank");
		
		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			
			String body = "";
			
			logger.debug("【获得用户观看视频排行页面】请求消来源={}，请求消息IP={}", userAgent, clientIp);
			logger.debug("【获得用户观看视频排行页面】请求消息体是：{}", body);

			
		} catch (Exception e) {
			logger.error(String.format("【获得用户观看视频排行页面】请求失败，消息体=%s", json), e);
		} finally {
			logger.debug("【获得用户观看视频排行页面】 返回的mav是： " + mav.toString());
		}
		logger.debug(">>> 【获得用户观看视频排行页面】 end <<<");
		
		return mav;
	}
	
	
	// 获得用户观看视频热力图数据
	@RequestMapping(value = "/getvideoheatmapdata")
	public RestModel  getVideoHeatMapData(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【获得用户观看视频热力图数据】 start ----->");
		String json = null;
		RestModel restModel = null;
		String callbackStr = null;
		//ModelAndView mav = new ModelAndView("section");

		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			
			callbackStr = request.getParameter("jsoncallback");
			String userId = request.getParameter("userid");
			String statType = request.getParameter("type");
			
			String body = "jsoncallback="+callbackStr + ",userId="
					+ userId + ",type=" + statType;
			
			logger.debug("【获得用户观看视频热力图数据】请求消息体是：{}", body);
			logger.debug("【获得用户观看视频热力图数据】请求消来源={}，请求消息IP={}", userAgent, clientIp);

			// 业务操作
			ArrayList<ArrayList<Integer>> tempData = null;
			tempData = StudentVideoStatistics.studentVideoComputing
					(userId, Integer.parseInt(statType));
			restModel = RestModel.getRestModel(BllConstantEnum.RESCODE_0, tempData);
			
			
		} catch (Exception e) {
			logger.error(String.format("【获得用户观看视频热力图数据】请求失败，消息体=%s", json), e);
		} finally {
			
			String msg_str = callbackStr + "(" + RestModelToJson
					.getRestJosn(restModel) + ")"; // 加入callback标识
			HttpUtil.outStr(response, msg_str);
			
			logger.debug("【获得用户观看视频热力图数据】 返回的RestModel是： " + restModel.toString());
		}
		logger.debug(">>> 【获得用户观看视频热力图数据】 end <<<");
		
		return null;
	}
	
	// 获得观看视频热力图页面
	@RequestMapping(value = "/section/videoheatmap", method = RequestMethod.GET)
	public ModelAndView  getVideoHeatMap(HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("【获得观看视频热力图页面】 start ----->");
		String json = null;
		
		ModelAndView mav = new ModelAndView("videoheatmap");
		
		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			
			String body = "";
			
			logger.debug("【获得观看视频热力图页面】请求消来源={}，请求消息IP={}", userAgent, clientIp);
			logger.debug("【获得观看视频热力图页面】请求消息体是：{}", body);

			
		} catch (Exception e) {
			logger.error(String.format("【获得观看视频热力图页面】请求失败，消息体=%s", json), e);
		} finally {
			logger.debug("【获得观看视频热力图页面】 返回的mav是： " + mav.toString());
		}
		logger.debug(">>> 【获得观看视频热力图页面】 end <<<");
		
		return mav;
	}
		
		
/*	// 获取视频网页服务
	@RequestMapping(value = "/section", method = RequestMethod.POST)
	public ModelAndView  videoSection(HttpServletRequest request,
			HttpSaervletResponse response) {
		logger.debug("【获得视频片段服务POST】 start ----->");
		String json = null;
		
		ModelAndView mav = new ModelAndView("video");
		
		try {
			// 打印请求客户端信息
			String userAgent = request.getHeader("User-Agent");
			String clientIp = request.getHeader("x-client-ip");
			
			SectionInfo  sectionInfo = new SectionInfo(); 
			sectionInfo.setSectionid( request.getParameter("sectionid") );
//				String userName = request.getParameter("name");
//				if(userName != null)
//					userName = new String( request.getParameter("name")
//						.getBytes("iso8859-1"), "utf-8" ); // 解决get方法中文乱码问题
			
			
			String body = "sectionid=" + sectionInfo.getSectionid();
			
			logger.debug("【获得视频片段服务POST】请求消来源={}，请求消息IP={}", userAgent, clientIp);
			logger.debug("【获得视频片段服务POST】请求消息体是：{}", body);

			// 业务操作
			mav = iVideoJobService.getVideoHtml(sectionInfo);
			
		} catch (Exception e) {
			logger.error(String.format("【获得视频片段服务POST】请求失败，消息体=%s", json), e);
		} finally {
			// 对外响应
	//				HttpUtil.outStr(response, RestModelToJson
	//						.getRestJosn(restModel));
			logger.debug("【获得视频片段服务POST】 返回的mav是： " + mav.toString());
		}
		logger.debug(">>> 【获得视频片段服务POST】 end <<<");
		
		return mav;
	}*/
	
	
}

