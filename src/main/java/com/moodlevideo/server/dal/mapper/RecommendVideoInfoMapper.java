package com.moodlevideo.server.dal.mapper;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

import com.moodlevideo.server.dal.model.RecommendVideoInfo;

public interface RecommendVideoInfoMapper {
	
	ArrayList<RecommendVideoInfo> selectByUseridAndUpdateid
		(@Param("userId") String userId, @Param("updateId") String updateId);
	
    int deleteByPrimaryKey(Integer id);

    int insert(RecommendVideoInfo record);

    int insertSelective(RecommendVideoInfo record);

    RecommendVideoInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RecommendVideoInfo record);

    int updateByPrimaryKey(RecommendVideoInfo record);
}