package com.insightflow.service.common;

import com.baomidou.mybatisplus.extension.service.IService;
import com.insightflow.entity.attachment.Attachment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 附件管理 Service 接口
 */
public interface AttachmentService extends IService<Attachment> {
    
    /**
     * 上传文件并保存附件信息
     * @param file 上传的文件
     * @param refType 关联类型
     * @param refId 关联ID
     * @return 附件信息
     */
    Attachment uploadAndSave(MultipartFile file, String refType, Long refId);
    
    /**
     * 根据关联信息查询附件列表
     * @param refType 关联类型
     * @param refId 关联ID
     * @return 附件列表
     */
    List<Attachment> getByRef(String refType, Long refId);
    
    /**
     * 删除附件（同时删除远程文件和数据库记录）
     * @param attachmentId 附件ID
     * @return 删除记录数
     */
    int deleteAttachment(Long attachmentId);
    
    /**
     * 批量删除附件
     * @param attachmentIds 附件ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteAttachments(List<Long> attachmentIds);
    
    /**
     * 根据关联信息删除附件
     * @param refType 关联类型
     * @param refId 关联ID
     * @return 是否删除成功
     */
    boolean deleteByRef(String refType, Long refId);
    
    /**
     * 文件路径更新
     * @return Attachment 新的附件信息
     */
    Attachment updateFilePath(MultipartFile newFilePath,String refType, Long refId, String oldFilePath);
    
    /**
     * 批量保存附件信息
     * @param attachmentList 附件列表
     * @return 是否保存成功
     */
    boolean saveBatch(List<Attachment> attachmentList);
}