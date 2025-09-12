package com.insightflow.service.common;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.insightflow.entity.sys.DataDictionary;
import com.insightflow.vo.DictionaryVO;

import java.util.List;

public interface DataDictionaryService extends IService<DataDictionary> {
    /**
     * 分页查询数据字典
     */
    PageInfo<DictionaryVO> getPage(Integer current, Integer size, String name, String code, String type, Long parentId, String parentName);

    /**
     * 根据类型获取数据字典列表
     */
    List<DataDictionary> getByType(String type);

    /**
     * 根据父ID获取数据字典列表
     */
    List<DataDictionary> getByParentId(Long parentId);

    /**
     * 根据类型和父ID获取数据字典列表
     */
    List<DataDictionary> getByTypeAndParentId(String type, Long parentId);

    /**
     * 获取树形结构
     */
    List<DictionaryVO> getTree(String type);

    /**
     * 检查编码是否重复
     */
    boolean isCodeExists(String code, String type, Long excludeId);

    /**
     * 检查是否有子字典
     */
    boolean hasChildren(Long parentId);

    /**
     * 保存数据字典（新增或更新）
     */
    boolean save(DataDictionary dataDictionary);

    /**
     * 删除数据字典
     */
    boolean delete(Long id);

    /**
     * 批量删除数据字典
     */
    boolean batchDelete(List<Long> ids);

    /**
     * 清除缓存
     */
    void clearCache();
} 