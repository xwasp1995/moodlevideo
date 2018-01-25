package com.moodlevideo.server.dal.mapper;

import java.util.ArrayList;

import com.moodlevideo.server.dal.model.TimeRankInfo;

public interface TimeRankInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TimeRankInfo record);

    int insertSelective(TimeRankInfo record);

    TimeRankInfo selectByPrimaryKey(Integer id);
    
    ArrayList<TimeRankInfo> selectByUpdateId(String updateId);

    int updateByPrimaryKeySelective(TimeRankInfo record);

    int updateByPrimaryKey(TimeRankInfo record);
}