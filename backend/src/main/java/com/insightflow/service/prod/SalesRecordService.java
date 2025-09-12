package com.insightflow.service.prod;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.prod.SalesRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.github.pagehelper.PageInfo;

public interface SalesRecordService extends IService<SalesRecord> {
    
    /**
     * 获取销售统计数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param regionCode 区域编码
     * @return 销售统计数据
     */
    Map<String, Object> getSalesStats(LocalDateTime startTime, LocalDateTime endTime, String regionCode);
    
    /**
     * 获取商品销售排行
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 商品销售排行数据
     */
    List<Map<String, Object>> getProductSalesRanking(LocalDateTime startTime, LocalDateTime endTime, Integer limit);
    
    /**
     * 获取员工销售业绩
     * @param employeeId 员工ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售业绩数据
     */
    Map<String, Object> getEmployeeSalesPerformance(Long employeeId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取区域销售趋势
     * @param regionCode 区域编码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售趋势数据
     */
    List<Map<String, Object>> getRegionSalesTrend(String regionCode, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 创建销售记录
     * @param salesRecord 销售记录信息
     * @return 是否创建成功
     */
    boolean createSalesRecord(SalesRecord salesRecord);
    
    /**
     * 更新销售记录
     * @param salesRecord 销售记录信息
     * @return 是否更新成功
     */
    boolean updateSalesRecord(SalesRecord salesRecord);
    
    /**
     * 获取销售目标完成率
     * @param employeeId 员工ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 目标完成率
     */
    BigDecimal getSalesTargetCompletionRate(Long employeeId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 批量保存销售记录
     * @param salesRecordList 销售记录列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<SalesRecord> salesRecordList);
    
    /**
     * 分页查询销售记录列表
     * @param current 当前页
     * @param size 每页大小
     * @param salesOrderNo 销售订单号
     * @param customerName 客户名称
     * @param customerManager 客户经理
     * @param salesperson 销售人员
     * @return 分页结果
     */
    PageInfo<SalesRecord> getSalesRecordPage(Integer current, Integer size, String salesOrderNo, String customerName, String customerManager, String salesperson);
} 