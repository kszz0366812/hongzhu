package com.insightflow.service.customer;

import com.github.pagehelper.PageInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.customer.TerminalInfo;

import java.util.List;

public interface TerminalInfoService extends IService<TerminalInfo> {
    void insertOrUpdate(TerminalInfo terminalInfo);
    
    List<TerminalInfo> getTerminalList(String terminalName, String terminalType, String customerManager);
    
    boolean saveBatch(List<TerminalInfo> terminalInfoList);
    
    /**
     * 分页查询终端列表
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词（终端名称、终端编码、客户经理、标签、终端类型）
     * @param isScheduled 是否排线
     * @return 分页结果
     */
    PageInfo<TerminalInfo> getTerminalPage(Integer current, Integer size, String keyword, Integer isScheduled);
} 