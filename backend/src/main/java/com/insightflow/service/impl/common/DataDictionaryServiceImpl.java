package com.insightflow.service.impl.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.insightflow.entity.sys.DataDictionary;
import com.insightflow.mapper.sys.DataDictionaryMapper;
import com.insightflow.service.common.DataDictionaryService;
import com.insightflow.vo.DictionaryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.insightflow.common.util.EntityUtils;

@Service
public class DataDictionaryServiceImpl extends ServiceImpl<DataDictionaryMapper, DataDictionary> implements DataDictionaryService {

    @Override
    public PageInfo<DictionaryVO> getPage(Integer current, Integer size, String name, String code, String type, Long parentId, String parentName) {
        // 检查parentId是否存在
        if (parentId != null) {
            DataDictionary parent = getById(parentId);
            if (parent == null) {
                // 如果parentId不存在，返回空的分页结果
                return new PageInfo<>();
            }
        }

        // 使用PageHelper进行分页
        PageHelper.startPage(current, size);
        LambdaQueryWrapper<DataDictionary> wrapper = buildQueryWrapper(name, code, type, parentId, parentName);
        List<DataDictionary> dataList = list(wrapper);

        PageInfo<DataDictionary> pageInfo = new PageInfo<>(dataList);

        // 性能优化：只查询当前页涉及的parentId对应的名称
        Set<Long> parentIds = pageInfo.getList().stream()
                .map(DataDictionary::getParentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        final Map<Long, String> idNameMap;
        if (!parentIds.isEmpty()) {
            List<DataDictionary> parentList = list(
                new LambdaQueryWrapper<DataDictionary>().in(DataDictionary::getId, parentIds)
            );
            idNameMap = parentList.stream().collect(Collectors.toMap(DataDictionary::getId, DataDictionary::getName));
        } else {
            idNameMap = new HashMap<>();
        }

        // 转换为VO
        List<DictionaryVO> voList = pageInfo.getList().stream()
                .map(dict -> convertToVO(dict, idNameMap))
                .collect(Collectors.toList());

        // 创建新的PageInfo对象，包含转换后的VO数据
        PageInfo<DictionaryVO> voPageInfo = new PageInfo<>();
        voPageInfo.setList(voList);
        voPageInfo.setTotal(pageInfo.getTotal());
        voPageInfo.setPageSize(pageInfo.getPageSize());
        voPageInfo.setPageNum(pageInfo.getPageNum());
        voPageInfo.setPages(pageInfo.getPages());
        
        return voPageInfo;
    }

    @Override
    public List<DataDictionary> getByType(String type) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDictionary::getType, type)
                .orderByAsc(DataDictionary::getCode);
        return list(wrapper);
    }

    @Override
    public List<DataDictionary> getByParentId(Long parentId) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDictionary::getParentId, parentId)
                .orderByAsc(DataDictionary::getCode);
        return list(wrapper);
    }

    @Override
    public List<DataDictionary> getByTypeAndParentId(String type, Long parentId) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDictionary::getType, type)
                .eq(DataDictionary::getParentId, parentId)
                .orderByAsc(DataDictionary::getCode);
        return list(wrapper);
    }

    @Override
    public List<DictionaryVO> getTree(String type) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            wrapper.eq(DataDictionary::getType, type);
        }
        wrapper.orderByAsc(DataDictionary::getType)
               .orderByAsc(DataDictionary::getCode);
        
        List<DataDictionary> allDicts = list(wrapper);
        return buildTree(allDicts);
    }

    @Override
    public boolean isCodeExists(String code, String type, Long excludeId) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDictionary::getCode, code)
               .eq(DataDictionary::getType, type);
        
        if (excludeId != null) {
            wrapper.ne(DataDictionary::getId, excludeId);
        }
        
        return count(wrapper) > 0;
    }

    @Override
    public boolean hasChildren(Long parentId) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataDictionary::getParentId, parentId);
        return count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(DataDictionary dataDictionary) {
        if (dataDictionary.getSort() == null) {
            dataDictionary.setSort(0);
        }
        if (dataDictionary.getId() == null) {
            // 自动设置创建人ID和时间
            EntityUtils.setCreateInfo(dataDictionary);
            return super.save(dataDictionary);
        } else {
            // 自动设置更新时间
            EntityUtils.setUpdateInfo(dataDictionary);
            return updateById(dataDictionary);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public void clearCache() {
        // 暂时不实现缓存清除
    }

    // 私有方法：构建查询条件
    private LambdaQueryWrapper<DataDictionary> buildQueryWrapper(String name, String code, String type, Long parentId, String parentName) {
        LambdaQueryWrapper<DataDictionary> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(DataDictionary::getName, name);
        }
        if (StringUtils.hasText(code)) {
            wrapper.like(DataDictionary::getCode, code);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(DataDictionary::getType, type);
        }
        if (parentId != null) {
            wrapper.eq(DataDictionary::getParentId, parentId);
        }
        
        // 父级名称模糊查询
        if (StringUtils.hasText(parentName)) {
            List<DataDictionary> parentList = list(
                new LambdaQueryWrapper<DataDictionary>().like(DataDictionary::getName, parentName)
            );
            List<Long> parentIds = parentList.stream()
                    .map(DataDictionary::getId)
                    .collect(Collectors.toList());
            
            if (!parentIds.isEmpty()) {
                wrapper.in(DataDictionary::getParentId, parentIds);
            }
            // 如果没有匹配的父级，在getPage方法中已经处理了空结果的情况
        }
        
        wrapper.orderByDesc(DataDictionary::getId);
        
        return wrapper;
    }

    // 私有方法：转换为VO
    private DictionaryVO convertToVO(DataDictionary dict, Map<Long, String> idNameMap) {
        DictionaryVO vo = new DictionaryVO();
        vo.setId(dict.getId());
        vo.setCode(dict.getCode());
        vo.setName(dict.getName());
        vo.setParentId(dict.getParentId());
        vo.setParentName(dict.getParentId() != null ? idNameMap.get(dict.getParentId()) : null);
        vo.setType(dict.getType());
        vo.setSort(dict.getSort());
        vo.setStatus(dict.getStatus());
        vo.setCreateTime(dict.getCreateTime() != null ? 
            dict.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        return vo;
    }

    // 私有方法：构建树形结构
    private List<DictionaryVO> buildTree(List<DataDictionary> allDicts) {
        Map<Long, DictionaryVO> voMap = new HashMap<>();
        List<DictionaryVO> rootList = new ArrayList<>();
        
        // 转换为VO
        for (DataDictionary dict : allDicts) {
            DictionaryVO vo = convertToVO(dict, new HashMap<>());
            voMap.put(dict.getId(), vo);
        }
        
        // 构建树形结构
        for (DataDictionary dict : allDicts) {
            DictionaryVO vo = voMap.get(dict.getId());
            if (dict.getParentId() == null) {
                rootList.add(vo);
            } else {
                DictionaryVO parent = voMap.get(dict.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }
        
        return rootList;
    }
} 