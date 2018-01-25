package com.moodlevideo.server.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moodlevideo.server.bo.TimeRankInfo;
import com.moodlevideo.server.dal.model.BehaviorInfo;
import com.moodlevideo.server.dal.model.TimeRankIndexInfo;

/**
 * @author wangxuebin
 * 用于计算相应学生所有观看视频的观看时间
 */
public class RankStatistics {
	
	private static Logger logger = LoggerFactory
			.getLogger(RankStatistics.class);
	
	private static boolean SAVE_DATA_SWITCH = true;
	

	// 计算每个学生一周观看视频的时间,排序并存入数据库相应表中
	public static void rankComputing(){
		// 获取当日的日期时间
		Date now_date = new Date();
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String date_str = "2017-04-05 11:59:59";
//		Date now_date = null;
//		try {
//			now_date = sdf1.parse(date_str);
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		}
		
		
		// 初始化time_rank_index的信息
		TimeRankIndexInfo timeRankIndexInfo = new TimeRankIndexInfo();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		timeRankIndexInfo.setUpdateId( sdf.format(now_date) );
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
		try { // 将UpdateTime的秒数位置为0
			now_date = sdf.parse( sdf.format(now_date) );
		} catch (ParseException e) {
			e.printStackTrace();
		}
		timeRankIndexInfo.setUpdateTime( now_date );
		logger.debug("【更新VideoTimeRank计划task服务】^^^^TimeRankIndex^^^^"
			+ " updateId=" + timeRankIndexInfo.getUpdateId() 
			+ "    updateTime=" + timeRankIndexInfo.getUpdateTime() );
		
		// 加载behavior数据表并计算得到各个学生的视频观看时间对象列表(timeRankInfoList)
		ArrayList<TimeRankInfo> timeRankInfoList = loadBehaviorInfoAndGetTimeRankInfoList
				(timeRankIndexInfo, now_date);
		
		// 按totaltime大小进行排序
		Comparator<TimeRankInfo> comp = new ComparatorImpl();
		Collections.sort(timeRankInfoList, comp);
		for(int i=0; i<timeRankInfoList.size(); ++i){
			timeRankInfoList.get(i).setRank(i+1); // 设置排名rank字段
			if( i>0 && timeRankInfoList.get(i).getTotaltime()
				.equals(timeRankInfoList.get(i-1).getTotaltime()) ){
				// 确保时间相同的同学排名相同
				timeRankInfoList.get(i).setRank(timeRankInfoList.get(i-1).getRank());
			}
			
		}
		
		
		logger.debug("【更新VideoTimeRank计划task服务】*** 所有用户观看时间计算完毕，开始导入数据库...");
		boolean save_flag = true;
		if(SAVE_DATA_SWITCH){
			save_flag = saveTimeRankInfo(timeRankIndexInfo, timeRankInfoList);
		}
		if(save_flag){
			logger.debug("【更新VideoTimeRank计划task服务】*** proccess excute successed!");
		} else {
			logger.debug("【更新VideoTimeRank计划task服务】*** proccess excute failed!");
		}
		
	}
	
	/**
	 * 从数据库中加载behavior数据表，
	 * 并计算得到各个学生的视频观看时间对象列表(timeRankInfoList)
	 * @param timeRankIndexInfo
	 * @param now_date
	 * @return behaviorInfoList
	 */
	private static ArrayList<TimeRankInfo> loadBehaviorInfoAndGetTimeRankInfoList
		(TimeRankIndexInfo timeRankIndexInfo, Date now_date){
		
		ArrayList<TimeRankInfo> timeRankInfoList = new ArrayList<TimeRankInfo>();	
		DBcon dBcon = new DBcon();
		
		// 查询数据库中学生表的信息,初始化timeRankInfo局部信息
		String sql = "select * from student;";
		ResultSet rs = dBcon.Query(sql);
		try{
			while(rs.next()){// 判断是否还有下一个数据
				
				if(rs.getString("id").equals("1601210100")
				|| rs.getString("id").equals("1601210101")){
					continue; // 忽略两个测试账号
				}
				TimeRankInfo timeRankInfo = new TimeRankInfo();
				timeRankInfo.setUserid( rs.getString("id") ); // 加载学生学号
				timeRankInfo.setName( rs.getString("name") ); // 加载学生姓名
				timeRankInfo.setUpdateId( timeRankIndexInfo.getUpdateId() ); // 加载update_id
				timeRankInfoList.add(timeRankInfo);
				
			}
		 }catch (SQLException e) {
			 e.printStackTrace();
		 }finally {
			dBcon.close();
		}
		
		// 获取上一个统计时间点的具体日期时间
		String last_week_deadline = getLastWednesdayDate(now_date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 对每个学号学生对应的行为操作进行统计计算时间
		for(TimeRankInfo timeRankInfo : timeRankInfoList){
			
			ArrayList<BehaviorInfo> behaviorInfoList = new ArrayList<BehaviorInfo>();
			sql = "select * from behavior where userid='" 
				+ timeRankInfo.getUserid()
				+ "' and happentime>='" + last_week_deadline +"'"
				+ " and happentime<'" + sdf.format(now_date) + "'";
			rs = dBcon.Query(sql);
			try{
				// 获取相应userid用户的所有行为信息
				while(rs.next()){
					BehaviorInfo behaviorInfo = new BehaviorInfo();
					behaviorInfo.setId( rs.getInt("id") );  // 记录顺序标号id
					behaviorInfo.setUserid( rs.getString("userid") );  // 用户ID
					behaviorInfo.setSectionid( rs.getString("sectionid") ); // 课程小节ID
					behaviorInfo.setBehave( rs.getInt("behave") );     // 行为标号
					behaviorInfo.setStarttime( rs.getString("starttime") ); // 行为开始时间
					behaviorInfo.setEndtime( rs.getString("endtime") );     // 行为结束时间
					behaviorInfo.setDuration( rs.getString("duration") );   // 页面打开持续时间
					behaviorInfo.setHappentime( rs.getTimestamp("happentime") ); // 行为发生时间
					behaviorInfoList.add(behaviorInfo);
				}
			 }catch (SQLException e) {
				 e.printStackTrace();
			 }finally {
				dBcon.close();
			}
			
			
			// 获取统计时间字符串
			String time_str = student_statistics(behaviorInfoList);
			SimpleDateFormat sdf_parse=new SimpleDateFormat("HH:mm:ss");//小写的mm表示的是分钟   
			Date totaltime=null;
			try {
				totaltime = sdf_parse.parse(time_str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			timeRankInfo.setTotaltime(totaltime); // 设置timeRankInfo的totaltime字段
			
		}
		
		return timeRankInfoList;
	}
	
	/**
	 * 将timeRankIndexInfo的相关信息
	 * 存入数据库的相应数据表
	 * @param timeRankIndexInfo
	 * @param timeRankInfoList
	 * @return behaviorInfoList
	 */
	private static boolean saveTimeRankInfo(TimeRankIndexInfo timeRankIndexInfo,
			ArrayList<TimeRankInfo> timeRankInfoList){
		
		boolean flag;
		DBcon dBcon = new DBcon();
		
		String sql = "insert into time_rank_index (update_id, update_time)"
				+ " VALUES ('" + timeRankIndexInfo.getUpdateId() 
				+ "','" + new Timestamp(timeRankIndexInfo.getUpdateTime().getTime()) + "');";
		
		flag = dBcon.Update(sql);
		if(!flag) return flag;
		
		for(TimeRankInfo timeRankInfo : timeRankInfoList){
			sql = "insert into time_rank (userid, update_id, name, totaltime, rank)"
					+ " VALUES ('" + timeRankInfo.getUserid()
					+ "','" + timeRankInfo.getUpdateId()
					+ "','" + timeRankInfo.getName()
					+ "','" + new Timestamp(timeRankInfo.getTotaltime().getTime())
					+ "','" + timeRankInfo.getRank()
					+ "');";
			
			flag = dBcon.Update(sql);
			if(!flag) return flag;
		}
		
		
		return true;
	}
	
	
	// 统计每个学生的观看时间
	private static String student_statistics(ArrayList<BehaviorInfo> behaviorInfoList){
		
		boolean flag_behave03=false, flag_behave07=false,
				flag_behave15=false, flag_behave16=false,
				flag_behave_start = false;
		int stat_time_sum = 0; // 总的观看时间
		int behave_time_03 = 0,
			behave_time_07 = 0, behave_time_15 = 0,
			behave_time_16 = 0, behave_time_start = 0;
		int temp_sum = 0; // 单次观看事件的时间
		
		for(BehaviorInfo be: behaviorInfoList){
			
			// behave为1或7标志着结束一个观看事件
			if(be.getBehave()==1 || be.getBehave()==7){
				
				if(be.getBehave()==7){
					flag_behave07 = true;
					behave_time_07 = timeStrToSecondInt(be.getDuration());
				}
				
				// 以最后一个视频结束时间和最后一个心跳包的时间中，
				// 较大者为视频观看最终时间
				if(flag_behave15 || flag_behave16){
					int bigger_time = behave_time_15;
					if(behave_time_15 < behave_time_16){
						bigger_time = behave_time_16;
					}
					temp_sum += bigger_time - behave_time_start;
					
				} else if(flag_behave07){ // 再没有，则以关闭页面的时间为准
					temp_sum += behave_time_07 - behave_time_start;
					if(flag_behave03){
						// 视频实际观看不足两分钟长时间暂停
						temp_sum -= behave_time_07-behave_time_03;
					}
					
				}else {
					temp_sum = 0;
					if(flag_behave_start){
						// 只要一个视频点开过开始（不足两分钟），算40s补偿时间
						// 以防止出现观看了几个短视频，但最终观看视频时间为0的情况
						temp_sum = 40; 
					}
					
				}
				
				if(!flag_behave_start){ // 从没有点开始键
					temp_sum = 0;
				}
				stat_time_sum += temp_sum; // 一次事件的时间加到总时间中
				
				// 标识变量置0
				temp_sum  = behave_time_07 = behave_time_15 
				= behave_time_16 = behave_time_start = 0;
				flag_behave_start = flag_behave07 = flag_behave03
				= flag_behave15 = flag_behave16 = false;
			}
			
			// 若当前是暂停状态，则不考虑其他操作涉及的时间点（例如行为8，隐藏页面会涉及暂停操作）
			if(flag_behave03 && be.getBehave()!=2){
				continue;
			}
			
			// 点击‘开始’行为处理
			if(be.getBehave()==2){  
				// 第一次点击开始,作为开始观看视频的时间节点
				if(!flag_behave_start){
					flag_behave_start = true;
					behave_time_start = timeStrToSecondInt(be.getDuration());
					flag_behave03 = false; // 防止第一次点开始前有8行为（暂停的一种）操作存在
				} else if(flag_behave03){ // 非第一次，且之前有过暂停操作
					temp_sum -= timeStrToSecondInt(be.getDuration()) - behave_time_03;
					flag_behave03 = false;

				}
				// 只要遇到2行为，就更新16心跳包的时间
				// 以防止出现最后时间比扣除时间少的情况
				flag_behave16 = true;
				behave_time_16 = timeStrToSecondInt(be.getDuration());
			}
			
			// 涉及到时间暂停的相关行为的操作处理
			if(be.getBehave()==3
			|| be.getBehave()==8
			|| be.getBehave()==15){
				flag_behave03 = true;
				behave_time_03 = timeStrToSecondInt(be.getDuration());
				
				if(be.getBehave()==15){ // 更新最后一次的视频结束时间
					flag_behave15 = true;
					behave_time_15 = timeStrToSecondInt(be.getDuration());
				}
			}
			
			// 更新最后一次的心跳包时间
			if(be.getBehave()==16){ 
				flag_behave16 = true;
				behave_time_16 = timeStrToSecondInt(be.getDuration());

			}
			
		}
		
		// 最后一次事件没有以7或1结束，需特殊处理
		if(flag_behave15 || flag_behave16){
			// 以最后一个视频结束时间和最后一个心跳包的时间中，
			// 较大者为视频观看最终时间
			int bigger_time = behave_time_15;
			if(behave_time_15 < behave_time_16){
				bigger_time = behave_time_16;
			}
			temp_sum += bigger_time - behave_time_start;
			
		} else {
			temp_sum = 0;
			if(flag_behave_start){
				// 只要一个视频点开过开始（不足两分钟），算40s补偿时间
				// 以防止出现观看了几个短视频，但最终观看视频时间为0的情况
				temp_sum = 40; 
			}
		}
		stat_time_sum += temp_sum; // 最后一次事件的时间加到总时间中
		
		
		return secondIntToTimeStr(stat_time_sum);
	}
	
	
	// 将时间字符串转化为Int型的秒数
	private static int timeStrToSecondInt(String timeStr){
		int minutes =0, seconds = 0;
		if(timeStr.length()==5){ // 00:00
			minutes = Integer.parseInt(timeStr.substring(0, 2));
			seconds = Integer.parseInt(timeStr.substring(3, 5));
		} else if(timeStr.length()==6){ // 000:00
			minutes = Integer.parseInt(timeStr.substring(0, 3));
			seconds = Integer.parseInt(timeStr.substring(4, 6));
		} else if(timeStr.length()==7){ // 0000:00
			minutes = Integer.parseInt(timeStr.substring(0, 4));
			seconds = Integer.parseInt(timeStr.substring(5, 7));
		}
		
		return minutes*60+seconds;
	}
	
	// 将Int型的秒数转化为标准时间字符串
	private static String secondIntToTimeStr(int time){
		
		String hours = String.valueOf(time / 3600);
		String minutes = String.valueOf(time % 3600 / 60);
		String seconds = String.valueOf(time % 60);
		
		if(hours.length()==0){
			hours = "00";
		} else if(hours.length()==1){
			hours = "0" + hours;
		}
		if(minutes.length()==0){
			minutes = "00";
		} else if(minutes.length()==1){
			minutes = "0" + minutes;
		}
		if(seconds.length()==0){
			seconds = "00";
		} else if(seconds.length()==1){
			seconds = "0" + seconds;
		}
		
		String timestr = hours + ":" + minutes + ":" + seconds;
		
		
		return timestr;
	}
	
	
	// 获取最近的一个统计节点的日期时间（上周三或本周三）
	@SuppressWarnings("deprecation")
	private static String getLastWednesdayDate(Date date){
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 轮询获取上周三的具体日期
		cal.add(Calendar.DAY_OF_WEEK, -1); 
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.WEDNESDAY) {
            cal.add(Calendar.DAY_OF_WEEK, -1); 
        }
		
		if(date.getDay()==3 && date.getHours()>=12) { 
			// 当日如果是周三，则判断是否过了中午十二点
			cal.setTime(date); // 超过十二点则按今天日期为基准，否则按上周三日期为基准
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(cal.getTime());
		String wednesdayDateStr = dateStr + " 12:00:00"; // 拼接上周三日期时间字符串
		
//		sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date wednesdayDate = null;
//		try { // 得到上周三的具体日期
//			wednesdayDate = sdf.parse(wednesdayDateStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
		return wednesdayDateStr;
	}
	

}

// 比较类
class ComparatorImpl implements Comparator<TimeRankInfo> {
	
	public int compare(TimeRankInfo tr1, TimeRankInfo tr2) {
		int s1 = tr1.getTotalTimeSeconds();
		int s2 = tr2.getTotalTimeSeconds();
		
		return s2-s1;
	}
	
}
