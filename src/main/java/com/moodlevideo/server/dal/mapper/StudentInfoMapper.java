package com.moodlevideo.server.dal.mapper;

import com.moodlevideo.server.dal.model.StudentInfo;

public interface StudentInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(StudentInfo record);

    int insertSelective(StudentInfo record);

    StudentInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(StudentInfo record);

    int updateByPrimaryKey(StudentInfo record);
}