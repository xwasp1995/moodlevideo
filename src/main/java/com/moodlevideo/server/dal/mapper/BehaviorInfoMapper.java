package com.moodlevideo.server.dal.mapper;

import com.moodlevideo.server.dal.model.BehaviorInfo;

public interface BehaviorInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BehaviorInfo record);

    int insertSelective(BehaviorInfo record);

    BehaviorInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BehaviorInfo record);

    int updateByPrimaryKey(BehaviorInfo record);
}