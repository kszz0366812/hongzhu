package com.insightflow.controller.prod;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.insightflow.common.Result;
import com.insightflow.entity.prod.SalesRecord;
import com.insightflow.service.prod.SalesRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
    @RequestMapping("/sales")
public class SalesRecordController {

    @Autowired
    private SalesRecordService salesRecordService;

    @RequestMapping("/page")
    public Result<Page<SalesRecord>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String salesOrderNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerManager,
            @RequestParam(required = false) String salesperson) {
        
        Page<SalesRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<SalesRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 使用正确的字段名进行查询
        wrapper.like(salesOrderNo != null, SalesRecord::getSalesOrderNo, salesOrderNo)
               .like(customerName != null, SalesRecord::getCustomerName, customerName)
               .like(customerManager != null, SalesRecord::getCustomerManager, customerManager)
               .like(salesperson != null, SalesRecord::getSalesperson, salesperson)
               .eq(SalesRecord::getDeleted, 0)
               .orderByDesc(SalesRecord::getCreateTime);
        
        return Result.success(salesRecordService.page(page, wrapper));
    }

    @RequestMapping("add")
    public Result<SalesRecord> add(@RequestBody SalesRecord salesRecord) {
        salesRecordService.save(salesRecord);
        return Result.success(salesRecord);
    }

    @RequestMapping("update")
    public Result<SalesRecord> update(@RequestBody SalesRecord salesRecord) {
        salesRecordService.updateById(salesRecord);
        return Result.success(salesRecord);
    }

    @RequestMapping("delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        salesRecordService.removeById(id);
        return Result.success();
    }

    @RequestMapping("getById/{id}")
    public Result<SalesRecord> getById(@PathVariable Long id) {
        return Result.success(salesRecordService.getById(id));
    }
} 