package com.insightflow.controller.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insightflow.entity.sys.DataDictionary;
import com.insightflow.service.common.DataDictionaryService;
import com.insightflow.common.util.Result;
import com.insightflow.vo.DictionaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "数据字典管理")
@RestController
@RequestMapping("/dictionary")
public class DataDictionaryController {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Operation(summary = "分页查询数据字典")
    @RequestMapping("/list")
    public Result<Page<DictionaryVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String parentName) {
        
        Page<DictionaryVO> result = dataDictionaryService.getPage(pageNum, pageSize, name, code, type, parentId, parentName);
        return Result.success(result);
    }

    @Operation(summary = "获取数据字典树形结构")
    @RequestMapping("/tree")
    public Result<List<DictionaryVO>> getTree(@RequestParam(required = false) String type) {
        try {
            List<DictionaryVO> tree = dataDictionaryService.getTree(type);
            return Result.success(tree);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取树形结构失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据类型获取数据字典")
    @RequestMapping("/type/{type}")
    public Result<List<DataDictionary>> getByType(@PathVariable String type) {
        List<DataDictionary> list = dataDictionaryService.getByType(type);
        return Result.success(list);
    }

    @Operation(summary = "根据父ID获取数据字典")
    @RequestMapping("/parent/{parentId}")
    public Result<List<DataDictionary>> getByParentId(@PathVariable Long parentId) {
        List<DataDictionary> list = dataDictionaryService.getByParentId(parentId);
        return Result.success(list);
    }

    @Operation(summary = "新增数据字典")
    @RequestMapping("/add")
    public Result<Boolean> add(@RequestBody DataDictionary dataDictionary) {
        // 检查编码是否重复
        if (dataDictionaryService.isCodeExists(dataDictionary.getCode(), dataDictionary.getType(), null)) {
            return Result.error("字典编码已存在");
        }
        
        boolean success = dataDictionaryService.save(dataDictionary);
        return success ? Result.success(true) : Result.error("新增失败");
    }

    @Operation(summary = "修改数据字典")
    @RequestMapping("/update")
    public Result<Boolean> update(@RequestBody DataDictionary dataDictionary) {
        // 检查编码是否重复（排除自己）
        if (dataDictionaryService.isCodeExists(dataDictionary.getCode(), dataDictionary.getType(), dataDictionary.getId())) {
            return Result.error("字典编码已存在");
        }
        
        boolean success = dataDictionaryService.save(dataDictionary);
        return success ? Result.success(true) : Result.error("修改失败");
    }

    @Operation(summary = "删除数据字典")
    @RequestMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        // 检查是否有子字典
        if (dataDictionaryService.hasChildren(id)) {
            return Result.error("存在子字典，无法删除");
        }
        
        boolean success = dataDictionaryService.delete(id);
        return success ? Result.success(true) : Result.error("删除失败");
    }

    @Operation(summary = "批量删除数据字典")
    @RequestMapping("/batch-delete")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        // 检查是否有子字典
        for (Long id : ids) {
            if (dataDictionaryService.hasChildren(id)) {
                return Result.error("存在子字典，无法删除");
            }
        }
        
        boolean success = dataDictionaryService.batchDelete(ids);
        return success ? Result.success(true) : Result.error("批量删除失败");
    }

    @Operation(summary = "清除缓存")
    @RequestMapping("/clear-cache")
    public Result<Boolean> clearCache() {
        dataDictionaryService.clearCache();
        return Result.success(true);
    }
} 