package com.insightflow.service.customer;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.customer.TerminalInfo;

public interface TerminalInfoService extends IService<TerminalInfo> {
    void insertOrUpdate(TerminalInfo terminalInfo);
} 