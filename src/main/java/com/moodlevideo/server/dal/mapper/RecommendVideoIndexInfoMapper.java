package com.moodlevideo.server.dal.mapper;

import com.moodlevideo.server.dal.model.RecommendVideoIndexInfo;

public interface RecommendVideoIndexInfoMapper {
	
	RecommendVideoIndexInfo selectByMaxPrimaryKey();
	
    int deleteByPrimaryKey(String updateId);

    int insert(RecommendVideoIndexInfo record);

    int insertSelective(RecommendVideoIndexInfo record);

    RecommendVideoIndexInfo selectByPrimaryKey(String updateId);

    int updateByPrimaryKeySelective(RecommendVideoIndexInfo record);

    int updateByPrimaryKey(RecommendVideoIndexInfo record);
}