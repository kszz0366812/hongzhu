package com.insightflow.common.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelUtil {
    
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        String cellValue = "";
        CellType cellType = cell.getCellType();
        
        switch (cellType) {
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                cellValue = cell.getCellFormula();
                break;
            case BLANK:
                cellValue = "";
                break;
            default:
                cellValue = "";
        }
        
        return cellValue.trim();
    }
} 