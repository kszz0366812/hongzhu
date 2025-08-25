package com.insightflow.service.common;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Map;

public interface ExcelExportService {
    ByteArrayOutputStream exportEmployeeData(LocalDate startDate, LocalDate endDate);
    ByteArrayOutputStream exportProductData();
    ByteArrayOutputStream exportTerminalData();
    ByteArrayOutputStream exportWholesalerData();
    ByteArrayOutputStream exportByTemplate(String templateCode, Map<String, Object> params);
} 