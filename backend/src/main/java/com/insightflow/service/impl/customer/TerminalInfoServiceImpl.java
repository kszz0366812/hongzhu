package com.insightflow.service.impl.customer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.mapper.customer.TerminalInfoMapper;
import com.insightflow.service.customer.TerminalInfoService;
import com.insightflow.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TerminalInfoServiceImpl extends ServiceImpl<TerminalInfoMapper, TerminalInfo> implements TerminalInfoService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(TerminalInfo terminalInfo) {
        if (terminalInfo.getId() == null) {
            // 新增 - 自动设置创建人ID和时间
            EntityUtils.setCreateInfo(terminalInfo);
            terminalInfo.setDeleted(0);
            save(terminalInfo);
        } else {
            // 更新 - 自动设置更新时间
            EntityUtils.setUpdateInfo(terminalInfo);
            updateById(terminalInfo);
        }
    }
} 