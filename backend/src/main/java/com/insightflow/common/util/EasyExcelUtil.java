package com.insightflow.common.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * EasyExcel工具类
 * 提供Excel导入导出的通用方法
 */
@Slf4j
public class EasyExcelUtil {

    /**
     * 读取Excel文件数据
     * @param file Excel文件
     * @param clazz 数据类
     * @param <T> 数据类型
     * @return 数据列表
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz) {
        try {
            List<T> dataList = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), clazz, new AnalysisEventListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    if (data != null) {
                        dataList.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel读取完成，共读取{}条数据", dataList.size());
                }
            })
            .sheet()
            .doRead();
            
            return dataList;
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new RuntimeException("读取Excel文件失败: " + e.getMessage());
        }
    }

    /**
     * 读取Excel文件数据（指定sheet）
     * @param file Excel文件
     * @param clazz 数据类
     * @param sheetNo sheet索引（从0开始）
     * @param <T> 数据类型
     * @return 数据列表
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz, int sheetNo) {
        try {
            List<T> dataList = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), clazz, new AnalysisEventListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    if (data != null) {
                        dataList.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel读取完成，共读取{}条数据", dataList.size());
                }
            })
            .sheet(sheetNo)
            .doRead();
            
            return dataList;
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new RuntimeException("读取Excel文件失败: " + e.getMessage());
        }
    }

    /**
     * 读取Excel文件数据（指定sheet名称）
     * @param file Excel文件
     * @param clazz 数据类
     * @param sheetName sheet名称
     * @param <T> 数据类型
     * @return 数据列表
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz, String sheetName) {
        try {
            List<T> dataList = new ArrayList<>();
            EasyExcel.read(file.getInputStream(), clazz, new AnalysisEventListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext context) {
                    if (data != null) {
                        dataList.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel读取完成，共读取{}条数据", dataList.size());
                }
            })
            .sheet(sheetName)
            .doRead();
            
            return dataList;
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new RuntimeException("读取Excel文件失败: " + e.getMessage());
        }
    }

    /**
     * 导出Excel文件
     * @param response HTTP响应
     * @param fileName 文件名（不包含扩展名）
     * @param sheetName sheet名称
     * @param dataList 数据列表
     * @param clazz 数据类
     * @param <T> 数据类型
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName, String sheetName,
                                       List<T> dataList, Class<T> clazz) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            // 写入Excel
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet(sheetName != null ? sheetName : "Sheet1")
                    .doWrite(dataList);
                    
            log.info("Excel导出完成，共导出{}条数据", dataList.size());
        } catch (IOException e) {
            log.error("导出Excel文件失败", e);
            throw new RuntimeException("导出Excel文件失败: " + e.getMessage());
        }
    }

    /**
     * 导出Excel文件（默认sheet名称）
     * @param response HTTP响应
     * @param fileName 文件名（不包含扩展名）
     * @param dataList 数据列表
     * @param clazz 数据类
     * @param <T> 数据类型
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName, 
                                     List<T> dataList, Class<T> clazz) {
        exportExcel(response, fileName, "Sheet1", dataList, clazz);
    }

    /**
     * 下载Excel模板
     * @param response HTTP响应
     * @param fileName 文件名（不包含扩展名）
     * @param clazz 数据类
     * @param <T> 数据类型
     */
    public static <T> void downloadTemplate(HttpServletResponse response, String fileName, Class<T> clazz) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            // 写入空数据的Excel模板
            EasyExcel.write(response.getOutputStream(), clazz)
                    .sheet("模板")
                    .doWrite(new ArrayList<>());
                    
            log.info("Excel模板下载完成");
        } catch (IOException e) {
            log.error("下载Excel模板失败", e);
            throw new RuntimeException("下载Excel模板失败: " + e.getMessage());
        }
    }

    /**
     * 验证Excel文件格式
     * @param file 文件
     * @return 是否为有效的Excel文件
     */
    public static boolean isValidExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }
        
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }

    /**
     * 获取文件大小（MB）
     * @param file 文件
     * @return 文件大小（MB）
     */
    public static double getFileSizeMB(MultipartFile file) {
        if (file == null) {
            return 0.0;
        }
        return (double) file.getSize() / (1024 * 1024);
    }
} 