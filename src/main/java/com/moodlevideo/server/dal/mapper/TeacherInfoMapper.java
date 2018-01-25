package com.moodlevideo.server.dal.mapper;

import com.moodlevideo.server.dal.model.TeacherInfo;

public interface TeacherInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TeacherInfo record);

    int insertSelective(TeacherInfo record);

    TeacherInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TeacherInfo record);

    int updateByPrimaryKey(TeacherInfo record);
}