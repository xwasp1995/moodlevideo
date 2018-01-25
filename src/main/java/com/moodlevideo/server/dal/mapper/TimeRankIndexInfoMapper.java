package com.moodlevideo.server.dal.mapper;

import com.moodlevideo.server.dal.model.TimeRankIndexInfo;

public interface TimeRankIndexInfoMapper {
	
	TimeRankIndexInfo selectByMaxPrimaryKey();
	
    int deleteByPrimaryKey(String updateId);

    int insert(TimeRankIndexInfo record);

    int insertSelective(TimeRankIndexInfo record);

    TimeRankIndexInfo selectByPrimaryKey(String updateId);

    int updateByPrimaryKeySelective(TimeRankIndexInfo record);

    int updateByPrimaryKey(TimeRankIndexInfo record);
}