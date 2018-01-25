package com.moodlevideo.server.dal.mapper;

import com.moodlevideo.server.dal.model.LessonInfo;

public interface LessonInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LessonInfo record);

    int insertSelective(LessonInfo record);

    LessonInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LessonInfo record);

    int updateByPrimaryKey(LessonInfo record);
}