package com.insightflow.service.impl.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.insightflow.entity.attachment.Attachment;
import com.insightflow.mapper.attachment.AttachmentMapper;
import com.insightflow.service.common.AttachmentService;
import com.insightflow.common.util.SftpUtil;
import com.insightflow.common.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.insightflow.common.util.JwtUtils;

/**
 * 附件管理 Service 实现类
 */
@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements AttachmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);
    
    @Autowired
    private SftpUtil sftpUtil;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment uploadAndSave(MultipartFile file, String refType, Long refId) {
        try {
            // 从JWT登录信息中获取上传者ID
            Long uploaderId = JwtUtils.getLoginInfo().getEmployeeId();
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new IllegalArgumentException("文件名不能为空");
            }
            
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // 如果refId不为空，在文件名中添加refId
            if (refId != null) {
                String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf("."));
                fileName = nameWithoutExt + "_" + refId + fileExtension;
            }
            
            // 根据refType创建对应的远程文件夹路径
            String remoteFolderPath = buildRemoteFolderPath(refType);
            
            // 创建临时本地目录用于中转
            // 创建临时文件
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "uploads",
                    String.valueOf(LocalDateTime.now().getYear()),
                    String.format("%02d", LocalDateTime.now().getMonthValue()));
            Files.createDirectories(tempDir);

            Path tempFile = Files.createTempFile(tempDir, "upload_", "_" + fileName);
            file.transferTo(tempFile.toFile());
            
            try {
                // 上传文件到远程服务器的指定文件夹
                boolean uploadSuccess = sftpUtil.uploadFileToFolder(tempFile.toString(), fileName, remoteFolderPath);
                if (!uploadSuccess) {
                    throw new RuntimeException("文件上传到远程服务器失败");
                }
                
                // 构建完整的文件访问路径
                String fileAccessPath = buildFileAccessPath(refType, fileName);
                
                // 创建附件记录
                Attachment attachment = new Attachment();
                attachment.setFileName(fileName);
                attachment.setFilePath(fileAccessPath); // 保存完整的文件访问路径
                attachment.setFileSize(file.getSize());
                attachment.setFileType(file.getContentType());
                attachment.setRefType(refType);
                attachment.setRefId(refId);
                attachment.setUploaderId(uploaderId);
                
                // 使用通用字段设置方法
                EntityUtils.setCreateInfo(attachment);
                
                // 保存到数据库
                save(attachment);
                
                logger.info("附件上传成功: ID={}, 文件名={}, 大小={}字节, 远程路径={}", 
                           attachment.getId(), originalFilename, file.getSize(), remoteFolderPath);
                
                return attachment;
                
            } finally {
                // 删除临时文件
                if (tempFile.toFile().exists()) {
                    tempFile.toFile().delete();
                }
            }
            
        } catch (IOException e) {
            logger.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Attachment> getByRef(String refType, Long refId) {
        QueryWrapper<Attachment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ref_type", refType)
                   .eq("ref_id", refId)
                   .orderByDesc("create_time");
        
        return list(queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAttachment(Long attachmentId) {
        try {
            // 查询附件信息
            Attachment attachment = getById(attachmentId);
            if (attachment == null) {
                logger.warn("附件不存在: ID={}", attachmentId);
                return 0;
            }
            
            // 删除远程文件
            try {
                boolean deleteSuccess = sftpUtil.deleteFile(attachment.getFileName(), attachment.getRefType());
                if (deleteSuccess) {
                    logger.info("远程文件删除成功: {}", attachment.getFilePath());
                } else {
                    logger.warn("远程文件删除失败: {}", attachment.getFilePath());
                }
            } catch (Exception e) {
                logger.warn("远程文件删除异常: {}, 错误: {}", attachment.getFilePath(), e.getMessage());
                // 即使远程文件删除失败，也继续删除数据库记录
            }
            
            // 删除数据库记录
            int result =  getBaseMapper().physicallyDeleteById(attachmentId);
            if (result>0) {
                logger.info("附件删除成功: ID={}, 文件名={}", attachmentId, attachment.getFileName());
            } else {
                logger.error("附件删除失败: ID={}", attachmentId);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("删除附件异常: ID={}, 错误: {}", attachmentId, e.getMessage(), e);
            throw new RuntimeException("删除附件失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteAttachments(List<Long> attachmentIds) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return true;
        }
        
        try {
            // 查询所有附件信息
            List<Attachment> attachments = listByIds(attachmentIds);
            
            // 删除远程文件
            for (Attachment attachment : attachments) {
                try {
                    boolean deleteSuccess = sftpUtil.deleteFile(attachment.getFileName(), attachment.getRefType());
                    if (deleteSuccess) {
                        logger.info("远程文件删除成功: {}", attachment.getFilePath());
                    } else {
                        logger.warn("远程文件删除失败: {}", attachment.getFilePath());
                    }
                } catch (Exception e) {
                    logger.warn("远程文件删除异常: {}, 错误: {}", attachment.getFilePath(), e.getMessage());
                }
            }
            
            // 批量删除数据库记录
            boolean result = removeByIds(attachmentIds);
            if (result) {
                logger.info("批量删除附件成功: 数量={}, IDs={}", attachmentIds.size(), attachmentIds);
            } else {
                logger.error("批量删除附件失败: IDs={}", attachmentIds);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("批量删除附件异常: IDs={}, 错误: {}", attachmentIds, e.getMessage(), e);
            throw new RuntimeException("批量删除附件失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByRef(String refType, Long refId) {
        try {
            // 查询关联的附件列表
            List<Attachment> attachments = getByRef(refType, refId);
            if (attachments.isEmpty()) {
                logger.info("没有找到关联的附件: refType={}, refId={}", refType, refId);
                return true;
            }
            
            // 删除远程文件
            for (Attachment attachment : attachments) {
                try {
                    boolean deleteSuccess = sftpUtil.deleteFile(attachment.getFileName(), attachment.getRefType());
                    if (deleteSuccess) {
                        logger.info("远程文件删除成功: {}", attachment.getFilePath());
                    } else {
                        logger.warn("远程文件删除失败: {}", attachment.getFilePath());
                    }
                } catch (Exception e) {
                    logger.warn("远程文件删除异常: {}, 错误: {}", attachment.getFilePath(), e.getMessage());
                }
            }
            
            // 删除数据库记录
            QueryWrapper<Attachment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ref_type", refType)
                       .eq("ref_id", refId);
            
            boolean result = remove(queryWrapper);
            if (result) {
                logger.info("关联附件删除成功: refType={}, refId={}, 数量={}", 
                           refType, refId, attachments.size());
            } else {
                logger.error("关联附件删除失败: refType={}, refId={}", refType, refId);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("删除关联附件异常: refType={}, refId={}, 错误: {}", 
                        refType, refId, e.getMessage(), e);
            throw new RuntimeException("删除关联附件失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment updateFilePath(MultipartFile newFile,String refType, Long refId, String oldFilePath) {
        try {
            // 根据文件路径查询附件
            QueryWrapper<Attachment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_path", oldFilePath);
            
            Attachment attachment = getOne(queryWrapper);
            if (attachment == null) {
                logger.warn("未找到文件路径为 {} 的附件,重新调用新建接口", oldFilePath);
                return uploadAndSave(newFile, refType, refId);
            }
            refType = attachment.getRefType();
            refId = attachment.getRefId();
            deleteAttachment(attachment.getId());
            return  uploadAndSave(newFile, refType, refId);
            
        } catch (Exception e) {
            logger.error("更新附件关联信息异常: 文件路径={}, 错误: {}", oldFilePath, e.getMessage(), e);
            throw new RuntimeException("更新附件关联信息失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Attachment attachment) {
        // 手动设置创建信息
        EntityUtils.setCreateInfo(attachment);
        return super.save(attachment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Attachment> attachmentList) {
        // 批量设置创建信息
        attachmentList.forEach(EntityUtils::setCreateInfo);
        return super.saveBatch(attachmentList);
    }
    
    /**
     * 构建远程文件夹路径
     * @param refType 关联类型
     * @return 远程文件夹路径
     */
    private String buildRemoteFolderPath(String refType) {
        if (!StringUtils.hasText(refType)) {
            return sftpUtil.getRemoteBasePath();
        }
        
        // 根据refType构建文件夹名称，只按类型分类
        String folderName = refType.toLowerCase();
        
        return sftpUtil.getRemoteBasePath() + "/" + folderName;
    }
    
    /**
     * 构建文件访问路径
     * @param refType 关联类型
     * @param fileName 文件名
     * @return 完整的文件访问路径
     */
    private String buildFileAccessPath(String refType, String fileName) {
        if (!StringUtils.hasText(refType)) {
            return sftpUtil.getHttpBaseUrl() + "/" + fileName;
        }
        
        // 根据refType构建文件夹名称，只按类型分类
        String folderName = refType.toLowerCase();
        
        return sftpUtil.getHttpBaseUrl() + "/" + folderName + "/" + fileName;
    }
    
    /**
     * 从URL中提取文件名
     * @param url 完整的URL
     * @return 文件名
     */
    private String extractFileNameFromUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }
        
        try {
            // 如果URL包含路径分隔符，取最后一部分作为文件名
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
                return url.substring(lastSlashIndex + 1);
            }
            
            // 如果没有路径分隔符，直接返回URL
            return url;
        } catch (Exception e) {
            logger.warn("从URL提取文件名失败: URL={}, 错误: {}", url, e.getMessage());
            return null;
        }
    }
} 