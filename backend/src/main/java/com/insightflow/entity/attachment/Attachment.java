package com.insightflow.entity.attachment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.insightflow.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_attachment")
public class Attachment extends BaseEntity {

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小(字节)
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 关联类型(PROJECT,TASK,REPORT)
     */
    @TableField("ref_type")
    private String refType;

    /**
     * 关联ID
     */
    @TableField("ref_id")
    private Long refId;

    /**
     * 上传者ID
     */
    @TableField("uploader_id")
    private Long uploaderId;
} 