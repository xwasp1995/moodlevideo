package com.moodlevideo.server.dal.mapper;

import com.moodlevideo.server.dal.model.SectionInfo;

public interface SectionInfoMapper {
    int deleteByPrimaryKey(String sectionid);

    int insert(SectionInfo record);

    int insertSelective(SectionInfo record);

    SectionInfo selectByPrimaryKey(String sectionid);

    int updateByPrimaryKeySelective(SectionInfo record);

    int updateByPrimaryKey(SectionInfo record);
}