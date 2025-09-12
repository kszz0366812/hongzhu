package com.insightflow.service.impl.common;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.insightflow.common.enums.ExcelImportType;
import com.insightflow.common.util.EasyExcelUtil;
import com.insightflow.dto.excel.ExcelDTO;
import com.insightflow.service.common.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Excel服务实现类
 * 使用EasyExcel提供统一的Excel导入导出功能
 * 支持百万级数据导入
 */
@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private ApplicationContext applicationContext;

    // 文件大小限制：100MB（支持百万级数据）
    private static final long MAX_FILE_SIZE_MB = 100;

    @Override
    public <T extends ExcelDTO> ImportResult<T> importExcel(MultipartFile file, Class<T> dtoClass) {
        try {
            // 验证文件
            if (!EasyExcelUtil.isValidExcelFile(file)) {
                return new ImportResult<>(new ArrayList<>(), new ArrayList<>(), "无效的Excel文件格式");
            }

            // 检查文件大小（限制为100MB，支持百万级数据）
            long fileSizeMB = (long) EasyExcelUtil.getFileSizeMB(file);
            if (fileSizeMB > MAX_FILE_SIZE_MB) {
                return new ImportResult<>(new ArrayList<>(), new ArrayList<>(),
                    String.format("文件大小超过%dMB限制，当前文件大小: %.2fMB", MAX_FILE_SIZE_MB, EasyExcelUtil.getFileSizeMB(file)));
            }

            log.info("开始导入Excel文件，文件大小: {:.2f}KB，文件名: {}", fileSizeMB, file.getOriginalFilename());

            List<T> successList = new ArrayList<>();
            List<T> failedList = new ArrayList<>();

            // 使用EasyExcel读取数据，使用监听器模式
            List<T> allData = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), dtoClass, new AnalysisEventListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    if (data != null) {
                        allData.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel读取完成，总行数: {}条", allData.size());
                }
            })
            .sheet()
            .headRowNumber(1) // 跳过标题行
            .doRead();

            // 验证数据有效性
            for (int i = 0; i < allData.size(); i++) {
                T data = allData.get(i);
                if (data != null && data.isValid()) {
                    successList.add(data);
                    log.debug("第{}行数据验证通过: {}", i + 2, data); // +2因为跳过了标题行
                } else {
                    failedList.add(data);
                    log.warn("第{}行数据验证失败: {}", i + 2, data);
                }
            }

            // 将成功的数据插入数据库
            if (!successList.isEmpty()) {
                insertDataToDatabase(successList);
            }

            // 构建结果消息
            String message = String.format("导入完成，总行数: %d，成功: %d条，失败: %d条",
                successList.size() + failedList.size(), successList.size(), failedList.size());

            log.info("Excel导入完成: {}", message);

            return new ImportResult<>(successList, failedList, message);

        } catch (IOException e) {
            log.error("导入Excel文件失败", e);
            return new ImportResult<>(new ArrayList<>(), new ArrayList<>(),
                "导入失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("处理Excel数据失败", e);
            return new ImportResult<>(new ArrayList<>(), new ArrayList<>(),
                "处理失败: " + e.getMessage());
        }
    }

    /**
     * 将数据插入数据库
     * @param successList 成功验证的数据列表
     */
    private <T extends ExcelDTO> void insertDataToDatabase(List<T> successList) {
        if (successList.isEmpty()) {
            return;
        }

        try {
            // 获取第一个DTO的Service类
            T firstDto = successList.get(0);
            Class<?> serviceClass = firstDto.getServiceClass();

            // 从Spring容器获取Service实例
            Object service = applicationContext.getBean(serviceClass);

            // 获取saveBatch方法
            Method saveBatchMethod = findSaveBatchMethod(service.getClass());
            if (saveBatchMethod == null) {
                log.error("未找到Service的saveBatch方法: {}", serviceClass.getSimpleName());
                return;
            }

            log.info("开始批量插入数据到数据库，数据类型: {}，数据量: {}条",
                serviceClass.getSimpleName(), successList.size());

            // 转换为Entity列表
            List<Object> entityList = new ArrayList<>();
            for (T dto : successList) {
                try {
                    Object entity = dto.toEntity();
                    entityList.add(entity);
                } catch (Exception e) {
                    log.warn("DTO转换为Entity失败: {}", dto, e);
                }
            }

            if (entityList.isEmpty()) {
                log.warn("没有有效的Entity可以插入");
                return;
            }

            // 批量插入数据
            try {
                log.info("开始调用saveBatch方法，数据类型: {}，实体数量: {}", 
                    serviceClass.getSimpleName(), entityList.size());
                Object result = saveBatchMethod.invoke(service, entityList);
                log.info("批量插入完成，数据类型: {}，成功插入: {}条，返回结果: {}",
                    serviceClass.getSimpleName(), entityList.size(), result);
            } catch (Exception e) {
                log.error("批量插入数据失败: {}", e.getMessage(), e);
            }

        } catch (Exception e) {
            log.error("批量插入数据失败", e);
        }
    }

    /**
     * 查找Service的saveBatch方法
     * @param serviceClass Service类
     * @return saveBatch方法，如果未找到返回null
     */
    private Method findSaveBatchMethod(Class<?> serviceClass) {
        // 优先查找saveBatch方法
        try {
            return serviceClass.getMethod("saveBatch", List.class);
        } catch (NoSuchMethodException e) {
            // 如果没有saveBatch方法，尝试其他常见的批量方法名
            String[] methodNames = {"batchSave", "batchInsert", "insertBatch"};
            for (String methodName : methodNames) {
                try {
                    Method method = serviceClass.getMethod(methodName, List.class);
                    if (method != null) {
                        return method;
                    }
                } catch (NoSuchMethodException ignored) {
                    // 继续尝试下一个方法名
                }
            }
        }
        return null;
    }

    @Override
    public <T extends ExcelDTO> void exportExcel(HttpServletResponse response, List<Map<String, Object>> dataList, Class<T> dtoClass) {
        try {
            if (dataList == null || dataList.isEmpty()) {
                throw new RuntimeException("导出数据为空");
            }

            // 根据DTO类获取对应的导入类型信息
            ExcelImportType importType = getImportTypeByDtoClass(dtoClass);
            if (importType == null) {
                throw new RuntimeException("无法识别的DTO类型: " + dtoClass.getSimpleName());
            }

            String fileName = importType.getFileName();
            String sheetName = importType.getFileName(); // 使用文件名作为Sheet名称

            log.info("开始导出Excel（Map数据），数据类型: {}，数据量: {}条", fileName, dataList.size());

            // 大数据量处理：如果数据量超过阈值，使用流式导出
            int threshold = 10000; // 1万条数据为阈值
            if (dataList.size() > threshold) {
                log.info("数据量超过{}条，启用流式导出模式", threshold);
                exportExcelWithStreaming(response, dataList, dtoClass, fileName, sheetName);
            } else {
                // 小数据量：直接转换并导出
                log.info("数据量在{}条以内，使用标准导出模式", threshold);
                exportExcelStandard(response, dataList, dtoClass, fileName, sheetName);
            }

        } catch (Exception e) {
            log.error("导出Excel失败（Map数据）", e);
            throw new RuntimeException("导出失败: " + e.getMessage());
        }
    }

    /**
     * 标准导出模式（适用于小数据量）
     */
    private <T extends ExcelDTO> void exportExcelStandard(HttpServletResponse response,
                                                         List<Map<String, Object>> dataList,
                                                         Class<T> dtoClass,
                                                         String fileName,
                                                         String sheetName) {
        // 将Map数据转换为DTO对象
        List<T> dtoList = new ArrayList<>();
        for (Map<String, Object> map : dataList) {
            try {
                // 使用DTO的构造函数从Map创建对象
                T dto = dtoClass.getDeclaredConstructor(Map.class).newInstance(map);
                dtoList.add(dto);
            } catch (Exception e) {
                log.warn("转换Map到DTO失败，跳过该条数据: {}", map, e);
            }
        }

        if (dtoList.isEmpty()) {
            throw new RuntimeException("没有有效的数据可以导出");
        }

        // 使用EasyExcelUtil导出
        EasyExcelUtil.exportExcel(response, fileName, sheetName, dtoList, dtoClass);
        log.info("标准导出完成，数据类型: {}，数据量: {}条", fileName, dtoList.size());
    }

        /**
     * 流式导出模式（适用于大数据量）
     */
    private <T extends ExcelDTO> void exportExcelWithStreaming(HttpServletResponse response,
                                                               List<Map<String, Object>> dataList,
                                                               Class<T> dtoClass,
                                                               String fileName,
                                                               String sheetName) {
        // 分页处理配置
        int batchSize = 5000; // 每批处理5000条
        int totalSize = dataList.size();
        int totalBatches = (totalSize + batchSize - 1) / batchSize;

        log.info("流式导出配置：总数据量: {}条，批次大小: {}条，总批次数: {}",
            totalSize, batchSize, totalBatches);

        // 分批处理数据，避免内存溢出
        List<T> allDtoList = new ArrayList<>();
        for (int i = 0; i < totalBatches; i++) {
            List<T> batch = processBatchForExport(dataList, i, batchSize, dtoClass);
            allDtoList.addAll(batch);

            log.info("已处理 {} 批数据，进度: {}/{}，当前批次数据量: {}条",
                i + 1, i + 1, totalBatches, batch.size());

            // 清理批次数据，释放内存
            batch.clear();
        }

        // 使用EasyExcelUtil导出
        EasyExcelUtil.exportExcel(response, fileName, sheetName, allDtoList, dtoClass);

        log.info("流式导出完成，数据类型: {}，数据量: {}条", fileName, totalSize);
    }

    /**
     * 处理一批数据，将Map转换为DTO（用于流式导出）
     * @param dataList 原始Map数据列表
     * @param batchIndex 批次索引
     * @param batchSize 批次大小
     * @param dtoClass DTO类
     * @return 转换后的DTO列表
     */
    private <T extends ExcelDTO> List<T> processBatchForExport(List<Map<String, Object>> dataList,
                                                               int batchIndex,
                                                               int batchSize,
                                                               Class<T> dtoClass) {
        int startIndex = batchIndex * batchSize;
        int endIndex = Math.min(startIndex + batchSize, dataList.size());

        List<Map<String, Object>> batch = dataList.subList(startIndex, endIndex);

        log.debug("开始处理第{}批数据，索引范围: {} - {}，数据量: {}条",
            batchIndex + 1, startIndex, endIndex - 1, batch.size());

        // 转换Map到DTO
        List<T> dtoList = new ArrayList<>();
        for (Map<String, Object> map : batch) {
            try {
                // 使用DTO的构造函数从Map创建对象
                T dto = dtoClass.getDeclaredConstructor(Map.class).newInstance(map);
                dtoList.add(dto);
            } catch (Exception e) {
                log.warn("转换Map到DTO失败，跳过该条数据: {}", map, e);
                // 转换失败时跳过该条数据，继续处理其他数据
            }
        }

        log.debug("第{}批数据处理完成，成功转换: {}条，原始数据: {}条",
            batchIndex + 1, dtoList.size(), batch.size());

        return dtoList;
    }

    @Override
    public <T extends ExcelDTO> void downloadTemplate(HttpServletResponse response, Class<T> dtoClass) {
        try {
            // 根据DTO类获取对应的导入类型信息
            ExcelImportType importType = getImportTypeByDtoClass(dtoClass);
            if (importType == null) {
                throw new RuntimeException("无法识别的DTO类型: " + dtoClass.getSimpleName());
            }

            String fileName = importType.getFileName() + "模板";

            log.info("开始下载Excel模板，类型: {}", fileName);

            // 使用EasyExcel下载模板
            EasyExcelUtil.downloadTemplate(response, fileName, dtoClass);

            log.info("Excel模板下载完成，类型: {}", fileName);

        } catch (Exception e) {
            log.error("下载Excel模板失败", e);
            throw new RuntimeException("下载模板失败: " + e.getMessage());
        }
    }

    /**
     * 根据DTO类获取对应的导入类型
     * @param dtoClass DTO类
     * @return 导入类型，如果未找到返回null
     */
    private ExcelImportType getImportTypeByDtoClass(Class<? extends ExcelDTO> dtoClass) {
        for (ExcelImportType type : ExcelImportType.values()) {
            if (type.getDtoClass().equals(dtoClass)) {
                return type;
            }
        }
        return null;
    }
}