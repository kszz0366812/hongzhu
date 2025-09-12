package com.insightflow.service.common;

public interface OpenApiService {
    String getAccessToken();
    void syncEmployeeData();
    void syncProductData();
    void syncTerminalData();
    void syncWholesalerData();
    void syncVisitData();
    void syncDealData();
} 