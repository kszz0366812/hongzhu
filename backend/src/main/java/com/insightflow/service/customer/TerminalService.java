package com.insightflow.service.customer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.customer.TerminalInfo;

import java.util.List;
import java.util.Map;

public interface TerminalService extends IService<TerminalInfo> {
    
    /**
     * 获取终端列表
     * @param type 类型
     * @param keyword 关键词
     * @return 终端列表
     */
    List<TerminalInfo> getTerminalList(String type, String keyword);
    
    /**
     * 获取终端销售统计
     * @param terminalId 终端ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售统计数据
     */
    Map<String, Object> getTerminalSalesStats(Long terminalId, String startTime, String endTime);
    
    /**
     * 获取终端类型列表
     * @return 类型列表
     */
    List<String> getTerminalTypeList();
    
    /**
     * 更新终端信息
     * @param terminalInfo 终端信息
     * @return 是否更新成功
     */
    boolean updateTerminalInfo(TerminalInfo terminalInfo);
    
    /**
     * 获取终端客户经理
     * @param terminalId 终端ID
     * @return 客户经理信息
     */
    Map<String, Object> getTerminalManager(Long terminalId);
    
    /**
     * 批量导入终端信息
     * @param terminalList 终端列表
     * @return 导入结果
     */
    Map<String, Object> batchImportTerminals(List<TerminalInfo> terminalList);
    
    /**
     * 获取终端标签列表
     * @return 标签列表
     */
    List<String> getTerminalTagList();
    
    /**
     * 更新终端标签
     * @param terminalId 终端ID
     * @param tags 标签列表
     * @return 是否更新成功
     */
    boolean updateTerminalTags(Long terminalId, List<String> tags);
} 