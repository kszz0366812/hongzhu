package com.insightflow.controller.common;

import com.insightflow.common.util.JwtUtils;
import com.insightflow.entity.attachment.Attachment;
import com.insightflow.common.util.Result;
import com.insightflow.common.util.SftpUtil;
import com.insightflow.service.common.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "附件管理")
@RestController
@RequestMapping("/api/file")
public class AttachmentController {
    
    private static final Logger log = LoggerFactory.getLogger(AttachmentController.class);

    @Autowired
    private SftpUtil sftpUtil;
    
    @Autowired
    private AttachmentService attachmentService;


    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<Attachment> uploadWithSave(
            @RequestParam("file") MultipartFile file,
            @RequestParam("refType") String refType,
            @RequestParam("refId") Long refId,
            @RequestParam("oldFilePath")String oldFilePath
           ) {
        try {
            Attachment attachment;

            if(oldFilePath != null && !oldFilePath.isEmpty()){
                 attachment = attachmentService.updateFilePath(file,refType, refId ,oldFilePath);
            }else{
                 attachment = attachmentService.uploadAndSave(file, refType, refId);
            }

            return Result.success(attachment);
        } catch (Exception e) {
            return Result.error("文件上传并保存失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "根据关联信息查询附件列表")
    @GetMapping("/list")
    public Result<java.util.List<Attachment>> getAttachmentList(
            @RequestParam("refType") String refType,
            @RequestParam("refId") Long refId) {
        try {
            java.util.List<Attachment> attachments = attachmentService.getByRef(refType, refId);
            return Result.success(attachments);
        } catch (Exception e) {
            return Result.error("查询附件列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "删除附件")
    @DeleteMapping("/{attachmentId}")
    public Result<Boolean> deleteAttachment(@PathVariable Long attachmentId) {
        try {
            boolean result = attachmentService.deleteAttachment(attachmentId)>0;
            if (result) {
                return Result.success("附件删除成功", true);
            } else {
                return Result.error("附件删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除附件失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "批量删除附件")
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteAttachments(@RequestBody java.util.List<Long> attachmentIds) {
        try {
            boolean result = attachmentService.batchDeleteAttachments(attachmentIds);
            if (result) {
                return Result.success("批量删除附件成功", true);
            } else {
                return Result.error("批量删除附件失败");
            }
        } catch (Exception e) {
            return Result.error("批量删除附件失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "根据关联信息删除附件")
    @DeleteMapping("/ref")
    public Result<Boolean> deleteByRef(
            @RequestParam("refType") String refType,
            @RequestParam("refId") Long refId) {
        try {
            boolean result = attachmentService.deleteByRef(refType, refId);
            if (result) {
                return Result.success("关联附件删除成功", true);
            } else {
                return Result.error("关联附件删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除关联附件失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取附件详情
     */
    @GetMapping("/detail/{id}")
    public Result<Attachment> getAttachmentDetail(@PathVariable Long id) {
        try {
            Attachment attachment = attachmentService.getById(id);
            if (attachment == null) {
                return Result.error("附件不存在");
            }
            return Result.success(attachment);
        } catch (Exception e) {
            log.error("获取附件详情失败: {}", e.getMessage(), e);
            return Result.error("获取附件详情失败: " + e.getMessage());
        }
    }
    

} 