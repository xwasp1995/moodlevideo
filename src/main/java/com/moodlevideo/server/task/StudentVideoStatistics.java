package com.moodlevideo.server.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moodlevideo.server.bo.KnowledgeLessonMapInfo;
import com.moodlevideo.server.bo.StudentVideoTimeInfo;
import com.moodlevideo.server.dal.model.BehaviorInfo;


/**
 * @author wangxuebin
 * 时间-排名统计程序
 */
public class StudentVideoStatistics {
	
	private static Logger logger = LoggerFactory
			.getLogger(StudentVideoStatistics.class);

	/**
	 * 计算相应学生所有观看视频的观看时间
	 * @param userId 用户Id
	 * @param type   统计类型（1:按每个用户观看的知识点的完成(比率)，2:按每个用户观看的视频的时间，
	 *                       3:按所有用户观看的知识点汇总的时间）
	 * @return studentVideoList
	 */
	public static ArrayList<ArrayList<Integer>> studentVideoComputing(String userId, int statType){
		
		ArrayList<StudentVideoTimeInfo> studentVideoTimeInfoList = 
				new ArrayList<StudentVideoTimeInfo>(); // 用户观看视频的List
		ArrayList<KnowledgeLessonMapInfo> knowledgeLessonMapInfoList = 
				new ArrayList<KnowledgeLessonMapInfo>(); // ‘知识点-课程’映射信息表
		
		// 从数据库加载(一个或所有)用户看过的视频以及原视频标准时间
		// {sectionid, sectionTitle, videoTime}
		studentVideoTimeInfoList = loadStudentVideoTime(userId, statType);
		logger.debug("【计算热力图相关数据服务】用户(一个或所有)看过的视频以及原视频标准时间加载完毕！");
		
		// 从数据库加载'知识点id-视频id'映射列表-{lessonId, sectionId, wight}
		knowledgeLessonMapInfoList = loadKnowledgeLessonMapList();
		logger.debug("【计算热力图相关数据服务】知识点id-视频id'映射列表加载完毕！");
		
		// 计算用户(单个或所有)观看的所有视频的实际观看时间
		watchBehaveTimeComputing(userId, studentVideoTimeInfoList, statType);
		logger.debug("【计算热力图相关数据服务】用户观看的所有视频的实际观看时间计算完毕！");

		
		// 分情况，计算返回数据
		ArrayList<ArrayList<Integer>> dataList = new ArrayList<ArrayList<Integer>>(); 
	
		HashMap<String, Integer> sumWatchTimeDict = 
				new HashMap<String, Integer>(); // 每个知识点看的总时间
		HashMap<String, Integer> maxVideoTimeDict = 
				new HashMap<String, Integer>(); // 每个知识点内额定的最大视频时间
		for(KnowledgeLessonMapInfo klmapInfo:knowledgeLessonMapInfoList){
			// 从用户看过的视频列表中找映射知识点中是否有相应的课程id
			for(StudentVideoTimeInfo stInfo: studentVideoTimeInfoList)
				if (stInfo.getSectionId().equals(klmapInfo.getSectionId())) {
					// 更新相应知识点视频的总观看时间
					if( sumWatchTimeDict.get(klmapInfo.getLessonId())!=null ){ 
						int sum = sumWatchTimeDict.get(klmapInfo.getLessonId()) 
								+ (int)(stInfo.getWatchTime()*klmapInfo.getWeight());
						sumWatchTimeDict.put(klmapInfo.getLessonId(), sum);
					}else {
						sumWatchTimeDict.put(klmapInfo.getLessonId(),  
								(int)(stInfo.getWatchTime()*klmapInfo.getWeight()) );
					}
					// 统计相应知识点视频的最大时间
					if( maxVideoTimeDict.get(klmapInfo.getLessonId())!=null ){
						int num1 = maxVideoTimeDict.get(klmapInfo.getLessonId());
						int num2 = (int)(stInfo.getVideoTime()*klmapInfo.getWeight());
						maxVideoTimeDict.put(klmapInfo.getLessonId(), getMax(num1,num2));
					}else{
						maxVideoTimeDict.put(klmapInfo.getLessonId(), 
								(int)(stInfo.getVideoTime()*klmapInfo.getWeight()));
					}
						
				}
			}
			logger.debug("【计算热力图相关数据服务】+++++sumWatchDict={}", sumWatchTimeDict);
			
			// 遍历map值
			for(String key:sumWatchTimeDict.keySet())
			{
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				tempList.add(Integer.parseInt( key.substring(9, 11) )-1);  // 列号
				tempList.add(Integer.parseInt( key.substring(12, 14) )-1); // 行号
				
				
				if(statType==1){ // 按每个用户观看的知识点，返回知识点观看比率
					// 形如：[[0,1,20], [1,1,22], [2,3,40]]
					// 相应知识点下最大的视频时间的1.3倍作为知识点时间，视频观看时间/(视频长度*1.3)
					int tempdata =  sumWatchTimeDict.get(key) * 100 
							/ (int)(maxVideoTimeDict.get(key) * 1.5); 
					tempdata = getMin(tempdata, 100); // 最大百分比就是100 
					tempdata = getMax(tempdata, 1);  // 保证不为0
					
					tempList.add(tempdata);
				}else if(statType==2 || statType==3){ 
				// 按每个用户（或所有用户）观看的视频，返回视频的观看时间（秒数）
					tempList.add( sumWatchTimeDict.get(key) );
				}
				
				dataList.add(tempList);
			}
			// 对三元组按第三个元素（时间属性）进行排序
			Comparator<ArrayList<Integer>> comp = new ComparatorImpl_List();
			Collections.sort(dataList, comp);
			
		return dataList;
	}
	
	
	/**
	 * 从数据库加载(一个或所有)用户看过的视频以及原视频标准时间
	 * @param userid
	 * @return studentVideoList
	 */
	private static ArrayList<StudentVideoTimeInfo> loadStudentVideoTime(String userId, int staticType){
		
		ArrayList<StudentVideoTimeInfo> studentVideoTimeInfoList = 
				new ArrayList<StudentVideoTimeInfo>(); // 用户观看视频的List
		DBcon dBcon = new DBcon();
		
		// 查询数据库中学生表的信息,初始化timeRankInfo局部信息
		String sql = null;
		if(staticType==1 || staticType==2){ // 单个用户看过的视频
			// {sectionid, sectionTitle, videoTime}
			sql = "SELECT distinct behavior.sectionid, section.description,"
					+ "substring(TIMEDIFF(section.endtime,section.starttime), 1, 5) "
					+ "as videotime from behavior,section where userid='" + userId
					+ "' and behavior.sectionid=section.sectionid ;";
		}else if(staticType==3){ // 所有用户看过的视频
			// {sectionid, sectionTitle, videoTime}
			sql = "SELECT distinct behavior.sectionid, section.description,"
					+ "substring(TIMEDIFF(section.endtime,section.starttime), 1, 5) "
					+ "as videotime from behavior,section where "
					+ "behavior.sectionid=section.sectionid ;";
		}
		
		ResultSet rs = dBcon.Query(sql);
		try{
			while(rs.next()){// 判断是否还有下一个数据
				
				StudentVideoTimeInfo studentVideoTimeInfo = new StudentVideoTimeInfo();
				studentVideoTimeInfo.setSectionId( rs.getString("sectionid") );     // 加载课程id
				studentVideoTimeInfo.setSectionTile( rs.getString("description") ); // 加载课程标题
				String vt_str = rs.getString("videotime"); // 转化为指定式的字符串
				int vminutes = timeStrToSecondInt( vt_str );  // str时间转化为int秒数
				studentVideoTimeInfo.setVideoTime( vminutes );   // 加载视频时间
				studentVideoTimeInfoList.add(studentVideoTimeInfo);
				
			}
		 }catch (SQLException e) {
			 e.printStackTrace();
		 }finally {
			dBcon.close();
		}
		
		return studentVideoTimeInfoList;
	}
	
	
	/**
	 * 从数据库加载'知识点id-视频id'映射列表
	 * @return studentVideoList
	 */
	private static ArrayList<KnowledgeLessonMapInfo> loadKnowledgeLessonMapList(){
		
		ArrayList<KnowledgeLessonMapInfo> knowledgeLessonMapInfoList = 
				new ArrayList<KnowledgeLessonMapInfo>(); // '知识点-视频'映射列表
		DBcon dBcon = new DBcon();
		
		String sql = "select * from knowledge_lesson_map";
		
		ResultSet rs = dBcon.Query(sql);
		try{
			while(rs.next()){// 判断是否还有下一个数据
				
				KnowledgeLessonMapInfo knowledgeLessonMapInfo = new KnowledgeLessonMapInfo();
				knowledgeLessonMapInfo.setLessonId( rs.getString("lessonId") );   // 加载知识点id
				knowledgeLessonMapInfo.setSectionId( rs.getString("sectionId") ); // 加载课程id
				knowledgeLessonMapInfo.setWeight( rs.getFloat("weight"));         // 加载课程权重
				knowledgeLessonMapInfoList.add(knowledgeLessonMapInfo);
				
			}
		 }catch (SQLException e) {
			 e.printStackTrace();
		 }finally {
			dBcon.close();
		}
		
		return knowledgeLessonMapInfoList;
	}
	
	
	/**
	 * 对（所有或者某个用户）观看行为时间进行统计
	 * @param userId
	 * @param studentVideoTimeInfoList
	 * @param statType
	 */
	private static void watchBehaveTimeComputing
		(String userId, ArrayList<StudentVideoTimeInfo> studentVideoTimeInfoList, int statType){
		
		DBcon dBcon = new DBcon();
		
		// 遍历一个用户观看过的所有视频
		for(StudentVideoTimeInfo stuInfo: studentVideoTimeInfoList){
			
			ArrayList<BehaviorInfo> behaviorInfoList = new ArrayList<BehaviorInfo>();
			String sql = null;
			if(statType==1 || statType==2){
				sql = "select * from behavior where userid='" + userId
						+ "' and sectionid='" + stuInfo.getSectionId() +"';";
			} else {
				sql = "select * from behavior where sectionid='" 
						+ stuInfo.getSectionId() +"';";
			}
			
			ResultSet rs = dBcon.Query(sql);
			// 统计每个视频的观看时间
			try{
				// 获取相应userid用户对应sectionid的行为信息
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
			
			// 获取统计时间(秒数)
			int temp_time = student_statistics(behaviorInfoList);
			if(temp_time > 0){
				stuInfo.setWatchTime( temp_time );
			}else { // 过滤掉负值数据（早期行为数据不全造成的）
				stuInfo.setWatchTime( 1 );
			}
		}
		
		
	}
	
	
	/**
	 * 统计每个视频行为的观看时间
	 * @return watchTime
	 */
	private static int student_statistics(ArrayList<BehaviorInfo> behaviorInfoList){
		
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
		
		
		return stat_time_sum;
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
	
	// 取较大值
	private static int getMax(int a, int b){
		return a > b ? a : b;
	}
	
	// 取较小值
	private static int getMin(int a, int b){
		return a < b ? a : b;
	}
}


//比较类
class ComparatorImpl_List implements Comparator<ArrayList<Integer>> {
	
	public int compare(ArrayList<Integer> d1, ArrayList<Integer> d2) {
		int data1 = d1.get(2);
		int data2 = d2.get(2);
		
		return data2-data1;
	}
	
}
