package com.moodlevideo.server.dal.mapper;

import com.moodlevideo.server.dal.model.StarRatingInfo;

public interface StarRatingInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(StarRatingInfo record);

    int insertSelective(StarRatingInfo record);

    StarRatingInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StarRatingInfo record);

    int updateByPrimaryKey(StarRatingInfo record);
}