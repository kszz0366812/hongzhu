package com.insightflow.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.sys.DataDictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataDictionaryMapper extends BaseMapper<DataDictionary> {
    List<DataDictionary> findByType(@Param("type") String type);
    
    List<DataDictionary> findByParentId(@Param("parentId") Long parentId);
    
    List<DataDictionary> findByTypeAndParentId(@Param("type") String type, @Param("parentId") Long parentId);
} 