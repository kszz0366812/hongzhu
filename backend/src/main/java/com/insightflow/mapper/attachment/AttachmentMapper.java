package com.insightflow.mapper.attachment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.insightflow.entity.attachment.Attachment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 附件管理 Mapper 接口
 */
@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment> {

    /**
     * 物理删除附件记录
     * @param id 附件ID
     * @return 影响行数
     */
    int physicallyDeleteById(Long id);
} 