package com.insightflow.service.common;

import com.insightflow.dto.excel.ExcelDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Excel服务接口
 * 提供统一的Excel导入导出功能
 */
public interface ExcelService {

    /**
     * 导入Excel数据
     * @param file Excel文件
     * @param dtoClass DTO类
     * @param <T> DTO类型
     * @return 导入结果
     */
    <T extends ExcelDTO> ImportResult<T> importExcel(MultipartFile file, Class<T> dtoClass);


    /**
     * 导出Excel数据（从Map数据）
     * @param response HTTP响应
     * @param dataList Map数据列表
     * @param dtoClass DTO类
     * @param <T> DTO类型
     */
    <T extends ExcelDTO> void exportExcel(HttpServletResponse response, List<Map<String, Object>> dataList, Class<T> dtoClass);

    /**
     * 下载Excel模板
     * @param response HTTP响应
     * @param dtoClass DTO类
     * @param <T> DTO类型
     */
    <T extends ExcelDTO> void downloadTemplate(HttpServletResponse response, Class<T> dtoClass);

    /**
     * 导入结果类
     * @param <T> DTO类型
     */
    class ImportResult<T> {
        private final List<T> successList;
        private final List<T> failedList;
        private final String message;

        public ImportResult(List<T> successList, List<T> failedList, String message) {
            this.successList = successList;
            this.failedList = failedList;
            this.message = message;
        }

        public List<T> getSuccessList() {
            return successList;
        }

        public List<T> getFailedList() {
            return failedList;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return failedList.isEmpty();
        }

        public int getTotalCount() {
            return successList.size() + failedList.size();
        }

        public int getSuccessCount() {
            return successList.size();
        }

        public int getFailedCount() {
            return failedList.size();
        }
    }
} 