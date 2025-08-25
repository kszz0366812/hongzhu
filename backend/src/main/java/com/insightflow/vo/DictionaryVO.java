package com.insightflow.vo;

import lombok.Data;
import java.util.List;

@Data
public class DictionaryVO {
    private Long id;
    private String code;
    private String name;
    private Long parentId;
    private String parentName;
    private String type;
    private Integer sort;
    private Integer status;
    private String createTime;
    private List<DictionaryVO> children;  // 子字典项
} 