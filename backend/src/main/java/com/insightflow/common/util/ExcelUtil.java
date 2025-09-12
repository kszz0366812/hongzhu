package com.insightflow.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class ExcelUtil {

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ISO_DATE,
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy")
    };

    private static final DateTimeFormatter[] DATETIME_FORMATTERS = {
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    };

    // ==================== 字符串类型转换 ====================

    public static String getString(Cell cell) {
        return getString(cell, null);
    }

    public static String getString(Cell cell, String defaultValue) {
        if (cell == null) return defaultValue;

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                case BLANK:
                    return "";
                default:
                    return defaultValue;
            }
        } catch (Exception e) {
            log.warn("获取字符串值失败: {}", e.getMessage());
            return defaultValue;
        }
    }

    // ==================== 数值类型转换 ====================

    public static Integer getInteger(Cell cell) {
        return getInteger(cell, null);
    }

    public static Integer getInteger(Cell cell, Integer defaultValue) {
        if (cell == null) return defaultValue;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return (int) numericValue;
                    } else {
                        return (int) Math.round(numericValue);
                    }
                case STRING:
                    String strValue = cell.getStringCellValue().trim();
                    if (!strValue.isEmpty()) {
                        return Integer.parseInt(strValue);
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("获取整数值失败: {}", e.getMessage());
        }
        return defaultValue;
    }

    public static Long getLong(Cell cell) {
        return getLong(cell, null);
    }

    public static Long getLong(Cell cell, Long defaultValue) {
        if (cell == null) return defaultValue;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return (long) numericValue;
                    } else {
                        return Math.round(numericValue);
                    }
                case STRING:
                    String strValue = cell.getStringCellValue().trim();
                    if (!strValue.isEmpty()) {
                        return Long.parseLong(strValue);
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("获取长整数值失败: {}", e.getMessage());
        }
        return defaultValue;
    }

    public static Double getDouble(Cell cell) {
        return getDouble(cell, null);
    }

    public static Double getDouble(Cell cell, Double defaultValue) {
        if (cell == null) return defaultValue;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return cell.getNumericCellValue();
                case STRING:
                    String strValue = cell.getStringCellValue().trim();
                    if (!strValue.isEmpty()) {
                        return Double.parseDouble(strValue);
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("获取双精度值失败: {}", e.getMessage());
        }
        return defaultValue;
    }

    public static BigDecimal getBigDecimal(Cell cell) {
        return getBigDecimal(cell, null);
    }

    public static BigDecimal getBigDecimal(Cell cell, BigDecimal defaultValue) {
        if (cell == null) return defaultValue;

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING:
                    String strValue = cell.getStringCellValue().trim();
                    if (!strValue.isEmpty()) {
                        return new BigDecimal(strValue);
                    }
                    break;
            }
        } catch (Exception e) {
            log.warn("获取BigDecimal值失败: {}", e.getMessage());
        }
        return defaultValue;
    }

    // ==================== 布尔类型转换 ====================

    public static Boolean getBoolean(Cell cell) {
        return getBoolean(cell, null);
    }

    public static Boolean getBoolean(Cell cell, Boolean defaultValue) {
        if (cell == null) return defaultValue;

        try {
            switch (cell.getCellType()) {
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                case STRING:
                    String strValue = cell.getStringCellValue().trim().toLowerCase();
                    if ("true".equals(strValue) || "1".equals(strValue) || "是".equals(strValue)) {
                        return true;
                    } else if ("false".equals(strValue) || "0".equals(strValue) || "否".equals(strValue)) {
                        return false;
                    }
                    break;
                case NUMERIC:
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == 1.0) return true;
                    if (numericValue == 0.0) return false;
                    break;
            }
        } catch (Exception e) {
            log.warn("获取布尔值失败: {}", e.getMessage());
        }
        return defaultValue;
    }

    // ==================== 日期类型转换 ====================

    public static LocalDate getDate(Cell cell) {
        return getDate(cell, LocalDate.now());
    }

    public static LocalDate getDate(Cell cell, LocalDate defaultValue) {
        if (cell == null) return defaultValue;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date dateValue = cell.getDateCellValue();
                if (dateValue != null) {
                    return dateValue.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                }
            }

            String dateStr = getString(cell);
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                LocalDate parsedDate = parseDateString(dateStr);
                if (parsedDate != null) {
                    return parsedDate;
                }
            }
        } catch (Exception e) {
            log.warn("获取LocalDate值失败: {}", e.getMessage());
        }
        return defaultValue;
    }

    public static LocalDateTime getDateTime(Cell cell) {
        return getDateTime(cell, LocalDateTime.now());
    }

    public static LocalDateTime getDateTime(Cell cell, LocalDateTime defaultValue) {
        if (cell == null) return defaultValue;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date dateValue = cell.getDateCellValue();
                if (dateValue != null) {
                    return dateValue.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
                }
            }

            String dateTimeStr = getString(cell);
            if (dateTimeStr != null && !dateTimeStr.trim().isEmpty()) {
                LocalDateTime parsedDateTime = parseDateTimeString(dateTimeStr);
                if (parsedDateTime != null) {
                    return parsedDateTime;
                }
            }
        } catch (Exception e) {
            log.warn("获取LocalDateTime值失败: {}", e.getMessage());
        }
        return defaultValue;
    }

    // ==================== 验证方法 ====================

    /**
     * 检查行是否为空行（所有指定列都为空）
     * @param row Excel行
     * @param columnCount 要检查的列数
     * @return true表示整行为空，false表示行中有数据
     */
    public static boolean isEmptyRow(Row row, int columnCount) {
        if (row == null) return true;
        
        for (int j = 0; j < columnCount; j++) {
            Cell cell = row.getCell(j);
            if (cell != null && !getString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查行是否为空行（检查前6列）
     * @param row Excel行
     * @return true表示整行为空，false表示行中有数据
     */
    public static boolean isEmptyRow(Row row) {
        return isEmptyRow(row, 6);
    }

    public static boolean isValidDate(Cell cell) {
        if (cell == null) return false;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return true;
            }

            String dateStr = getString(cell);
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                return parseDateString(dateStr) != null;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return false;
    }

    public static boolean isValidDateTime(Cell cell) {
        if (cell == null) return false;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return true;
            }

            String dateTimeStr = getString(cell);
            if (dateTimeStr != null && !dateTimeStr.trim().isEmpty()) {
                return parseDateTimeString(dateTimeStr) != null;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return false;
    }

    // ==================== 私有辅助方法 ====================

    private static LocalDate parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;

        String trimmedDateStr = dateStr.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmedDateStr, formatter);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    private static LocalDateTime parseDateTimeString(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;

        String trimmedDateTimeStr = dateTimeStr.trim();
        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(trimmedDateTimeStr, formatter);
            } catch (Exception e) {
                continue;
            }
        }
        return null;
    }

    // ==================== 向后兼容方法 ====================

    @Deprecated
    public static String getCellValueAsString(Cell cell) {
        return getString(cell);
    }

    @Deprecated
    public static LocalDate parseLocalDate(Cell cell) {
        return getDate(cell);
    }

    @Deprecated
    public static LocalDate parseLocalDate(Cell cell, LocalDate defaultValue) {
        return getDate(cell, defaultValue);
    }

    @Deprecated
    public static LocalDateTime parseLocalDateTime(Cell cell) {
        return getDateTime(cell);
    }

    @Deprecated
    public static LocalDateTime parseLocalDateTime(Cell cell, LocalDateTime defaultValue) {
        return getDateTime(cell, defaultValue);
    }
} 