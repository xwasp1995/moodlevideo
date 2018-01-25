package com.moodlevideo.server.task;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("taskJob")  
/**
 * @author wangxuebin
 * 定时任务设置
 */
public class MoodleTaskJob {
	
	private static Logger logger = LoggerFactory
			.getLogger(MoodleTaskJob.class);
	
	static int video_time_rank_count = 0;  
	
	 // 计算学生观看视频时间任务
	 // 每天早上5点59开始执行，之后每隔3小时执行一次
	 @Scheduled(cron = "0 59 5/3 * * ?")  
	 public void videoTimeRankComputing() {   
		
		++video_time_rank_count;  
		logger.debug("【更新VideoTimeRank计划task服务】***时间=" + new Date() 
				+ " 开始执行" + video_time_rank_count + "次");
		RankStatistics.rankComputing(); // 计算每个学生一周观看视频的时间
		logger.debug("【更新VideoTimeRank计划task服务】***时间=" + new Date()
				+ " 执行完第" + video_time_rank_count + "次");
	    
	  }  


	
	
}
