package com.insightflow.service.impl.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insightflow.common.config.OpenApiConfig;
import com.insightflow.dto.DealSyncDTO;
import com.insightflow.dto.EmployeeSyncDTO;
import com.insightflow.dto.ProductSyncDTO;
import com.insightflow.dto.TerminalSyncDTO;
import com.insightflow.dto.VisitSyncDTO;
import com.insightflow.dto.WholesalerSyncDTO;
import com.insightflow.entity.prod.DealRecord;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.entity.prod.ProductInfo;
import com.insightflow.entity.customer.TerminalInfo;
import com.insightflow.entity.customer.VisitRecord;
import com.insightflow.entity.customer.WholesalerInfo;
import com.insightflow.mapper.prod.DealRecordMapper;
import com.insightflow.mapper.employee.EmployeeInfoMapper;
import com.insightflow.mapper.prod.ProductInfoMapper;
import com.insightflow.mapper.customer.TerminalInfoMapper;
import com.insightflow.mapper.customer.VisitRecordMapper;
import com.insightflow.mapper.customer.WholesalerInfoMapper;
import com.insightflow.service.common.OpenApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OpenApiServiceImpl implements OpenApiService {

    @Autowired
    private OpenApiConfig openApiConfig;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EmployeeInfoMapper employeeInfoMapper;
    
    @Autowired
    private ProductInfoMapper productInfoMapper;
    
    @Autowired
    private TerminalInfoMapper terminalInfoMapper;
    
    @Autowired
    private WholesalerInfoMapper wholesalerInfoMapper;
    
    @Autowired
    private VisitRecordMapper visitRecordMapper;
    
    @Autowired
    private DealRecordMapper dealRecordMapper;
    
    private String accessToken;
    private long tokenExpireTime;

    @Override
    public String getAccessToken() {
        // 如果token未过期，直接返回
        if (accessToken != null && System.currentTimeMillis() < tokenExpireTime) {
            return accessToken;
        }
        
        // 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("client_id", openApiConfig.getClientId());
        params.put("client_secret", openApiConfig.getClientSecret());
        params.put("grant_type", "client_credentials");
        
        // 构建请求URL
        String url = UriComponentsBuilder.fromHttpUrl(openApiConfig.getTokenUrl())
                .queryParam("client_id", params.get("client_id"))
                .queryParam("client_secret", params.get("client_secret"))
                .queryParam("grant_type", params.get("grant_type"))
                .build()
                .toUriString();
        
        try {
            // 发送请求获取token
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (body != null && body.get("access_token") != null && body.get("expires_in") != null) {
                    accessToken = (String) body.get("access_token");
                    // 设置token过期时间（提前5分钟过期）
                    tokenExpireTime = System.currentTimeMillis() + ((Integer) body.get("expires_in") - 300) * 1000L;
                    return accessToken;
                }
            }
        } catch (Exception e) {
            log.error("获取访问令牌失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取访问令牌失败: " + e.getMessage(), e);
        }
        throw new RuntimeException("获取访问令牌失败: 响应无效");
    }

    @Override
    public void syncEmployeeData() {
        String token = getAccessToken();
        int page = 1;
        int size = 100;
        boolean hasMore = true;
        int totalProcessed = 0;
        
        while (hasMore) {
            try {
                // 构建请求URL
                String url = UriComponentsBuilder.fromHttpUrl(openApiConfig.getBaseUrl() + "/api/employees")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                        .toUriString();
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                // 发送请求
                ResponseEntity<EmployeeSyncDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        EmployeeSyncDTO.class
                );
                
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    EmployeeSyncDTO syncDTO = response.getBody();
                    if (syncDTO != null && syncDTO.getData() != null) {
                        // 处理员工数据
                        for (EmployeeSyncDTO.EmployeeData data : syncDTO.getData()) {
                            try {
                                EmployeeInfo employeeInfo = new EmployeeInfo();
                                employeeInfo.setEmployeeCode(data.getEmployeeCode());
                                employeeInfo.setName(data.getName());
                                employeeInfo.setStatus(data.getStatus());
                                employeeInfo.setRegionLevel1(data.getRegionLevel1());
                                employeeInfo.setRegionLevel2(data.getRegionLevel2());
                                employeeInfo.setRegionLevel3(data.getRegionLevel3());
                                
                                // 将负责区域列表转换为JSON字符串
                                if (data.getResponsibleRegions() != null) {
                                    employeeInfo.setResponsibleRegions(objectMapper.writeValueAsString(data.getResponsibleRegions()));
                                }
                                
                                employeeInfo.setDirectLeaderId(data.getDirectLeaderId());
                                employeeInfo.setPosition(data.getPosition());
                                employeeInfo.setLevels(data.getLevel());
                                employeeInfo.setChannel(data.getChannel());
                                
                                // 转换入职日期
                                if (data.getJoinDate() != null) {
                                    employeeInfo.setJoinDate(LocalDate.parse(data.getJoinDate(), DateTimeFormatter.ISO_DATE));
                                }
                                
                                // 保存或更新员工信息
                                employeeInfoMapper.insertOrUpdate(employeeInfo);
                                totalProcessed++;
                            } catch (Exception e) {
                                log.error("处理员工数据失败: employeeCode={}, error={}", data.getEmployeeCode(), e.getMessage(), e);
                            }
                        }
                        
                        // 检查是否还有更多数据
                        hasMore = page * size < syncDTO.getTotal();
                        page++;
                    } else {
                        hasMore = false;
                    }
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("同步员工数据失败: page={}, error={}", page, e.getMessage(), e);
                hasMore = false;
            }
        }
        
        log.info("员工数据同步完成，共处理{}条记录", totalProcessed);
    }

    @Override
    public void syncProductData() {
        String token = getAccessToken();
        int page = 1;
        int size = 100;
        boolean hasMore = true;
        int totalProcessed = 0;
        
        while (hasMore) {
            try {
                // 构建请求URL
                String url = UriComponentsBuilder.fromHttpUrl(openApiConfig.getBaseUrl() + "/api/products")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                        .toUriString();
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                // 发送请求
                ResponseEntity<ProductSyncDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        ProductSyncDTO.class
                );
                
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    ProductSyncDTO syncDTO = response.getBody();
                    if (syncDTO != null && syncDTO.getData() != null) {
                        // 处理商品数据
                        for (ProductSyncDTO.ProductData data : syncDTO.getData()) {
                            try {
                                ProductInfo productInfo = new ProductInfo();
                                productInfo.setProductCode(data.getProductCode());
                                productInfo.setProductName(data.getProductName());
                                productInfo.setSpecification(data.getSpecification());
                                productInfo.setUnitPrice(data.getUnitPrice());
                                productInfo.setCasePrice(data.getCasePrice());
                                productInfo.setSeries(data.getSeries());
                                
                                // 保存或更新商品信息
                                productInfoMapper.insertOrUpdate(productInfo);
                                totalProcessed++;
                            } catch (Exception e) {
                                log.error("处理商品数据失败: productCode={}, error={}", data.getProductCode(), e.getMessage(), e);
                            }
                        }
                        
                        // 检查是否还有更多数据
                        hasMore = page * size < syncDTO.getTotal();
                        page++;
                    } else {
                        hasMore = false;
                    }
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("同步商品数据失败: page={}, error={}", page, e.getMessage(), e);
                hasMore = false;
            }
        }
        
        log.info("商品数据同步完成，共处理{}条记录", totalProcessed);
    }

    @Override
    public void syncTerminalData() {
        String token = getAccessToken();
        int page = 1;
        int size = 100;
        boolean hasMore = true;
        int totalProcessed = 0;
        
        while (hasMore) {
            try {
                // 构建请求URL
                String url = UriComponentsBuilder.fromHttpUrl(openApiConfig.getBaseUrl() + "/api/terminals")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                        .toUriString();
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                // 发送请求
                ResponseEntity<TerminalSyncDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        TerminalSyncDTO.class
                );
                
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    TerminalSyncDTO syncDTO = response.getBody();
                    if (syncDTO != null && syncDTO.getData() != null) {
                        // 处理终端商户数据
                        for (TerminalSyncDTO.TerminalData data : syncDTO.getData()) {
                            try {
                                TerminalInfo terminalInfo = new TerminalInfo();
                                terminalInfo.setTerminalCode(data.getTerminalCode());
                                terminalInfo.setTerminalName(data.getTerminalName());
                                terminalInfo.setTerminalType(data.getTerminalType());
                                
                                // 将标签列表转换为JSON字符串
                                if (data.getTags() != null) {
                                    terminalInfo.setTags(objectMapper.writeValueAsString(data.getTags()));
                                }
                                
                                terminalInfo.setCustomerManager(data.getCustomerManager());
                                terminalInfo.setIsScheduled(data.getIsScheduled());
                                
                                // 保存或更新终端商户信息
                                terminalInfoMapper.insertOrUpdate(terminalInfo);
                                totalProcessed++;
                            } catch (Exception e) {
                                log.error("处理终端商户数据失败: terminalCode={}, error={}", data.getTerminalCode(), e.getMessage(), e);
                            }
                        }
                        
                        // 检查是否还有更多数据
                        hasMore = page * size < syncDTO.getTotal();
                        page++;
                    } else {
                        hasMore = false;
                    }
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("同步终端商户数据失败: page={}, error={}", page, e.getMessage(), e);
                hasMore = false;
            }
        }
        
        log.info("终端商户数据同步完成，共处理{}条记录", totalProcessed);
    }

    @Override
    public void syncWholesalerData() {
        String token = getAccessToken();
        int page = 1;
        int size = 100;
        boolean hasMore = true;
        int totalProcessed = 0;
        
        while (hasMore) {
            try {
                // 构建请求URL
                String url = UriComponentsBuilder.fromHttpUrl(openApiConfig.getBaseUrl() + "/api/wholesalers")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                        .toUriString();
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                // 发送请求
                ResponseEntity<WholesalerSyncDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        WholesalerSyncDTO.class
                );
                
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    WholesalerSyncDTO syncDTO = response.getBody();
                    if (syncDTO != null && syncDTO.getData() != null) {
                        // 处理批发商数据
                        for (WholesalerSyncDTO.WholesalerData data : syncDTO.getData()) {
                            try {
                                WholesalerInfo wholesalerInfo = new WholesalerInfo();
                                wholesalerInfo.setDealerCode(data.getDealerCode());
                                wholesalerInfo.setDealerName(data.getDealerName());
                                wholesalerInfo.setLevel(data.getLevel());
                                wholesalerInfo.setContactPerson(data.getContactPerson());
                                wholesalerInfo.setContactPhone(data.getContactPhone());
                                wholesalerInfo.setCustomerManager(data.getCustomerManager());
                                
                                // 保存或更新批发商信息
                                wholesalerInfoMapper.insertOrUpdate(wholesalerInfo);
                                totalProcessed++;
                            } catch (Exception e) {
                                log.error("处理批发商数据失败: dealerCode={}, error={}", data.getDealerCode(), e.getMessage(), e);
                            }
                        }
                        
                        // 检查是否还有更多数据
                        hasMore = page * size < syncDTO.getTotal();
                        page++;
                    } else {
                        hasMore = false;
                    }
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("同步批发商数据失败: page={}, error={}", page, e.getMessage(), e);
                hasMore = false;
            }
        }
        
        log.info("批发商数据同步完成，共处理{}条记录", totalProcessed);
    }

    @Override
    public void syncVisitData() {
        String token = getAccessToken();
        int page = 1;
        int size = 100;
        boolean hasMore = true;
        int totalProcessed = 0;
        
        while (hasMore) {
            try {
                // 构建请求URL
                String url = UriComponentsBuilder.fromHttpUrl(openApiConfig.getBaseUrl() + "/api/visits")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                        .toUriString();
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                // 发送请求
                ResponseEntity<VisitSyncDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        VisitSyncDTO.class
                );
                
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    VisitSyncDTO syncDTO = response.getBody();
                    if (syncDTO != null && syncDTO.getData() != null) {
                        // 处理拜访记录数据
                        for (VisitSyncDTO.VisitData data : syncDTO.getData()) {
                            try {
                                VisitRecord visitRecord = new VisitRecord();
//                                visitRecord.setVisitorId(data.getVisitorId());
//                                visitRecord.setVisitTime(LocalDateTime.parse(data.getVisitTime(), DateTimeFormatter.ISO_DATE_TIME));
//                                visitRecord.setTerminalId(data.getTerminalId());
//                                visitRecord.setIsDeal(data.getIsDeal());

                                // 保存或更新拜访记录
                                visitRecordMapper.insertOrUpdate(visitRecord);
                                totalProcessed++;
                            } catch (Exception e) {
                                log.error("处理拜访记录数据失败: visitorId={}, terminalId={}, error={}", 
                                    data.getVisitorId(), data.getTerminalId(), e.getMessage(), e);
                            }
                        }
                        
                        // 检查是否还有更多数据
                        hasMore = page * size < syncDTO.getTotal();
                        page++;
                    } else {
                        hasMore = false;
                    }
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("同步拜访记录数据失败: page={}, error={}", page, e.getMessage(), e);
                hasMore = false;
            }
        }
        
        log.info("拜访记录数据同步完成，共处理{}条记录", totalProcessed);
    }

    @Override
    public void syncDealData() {
        String token = getAccessToken();
        int page = 1;
        int size = 100;
        boolean hasMore = true;
        int totalProcessed = 0;
        
        while (hasMore) {
            try {
                // 构建请求URL
                String url = UriComponentsBuilder.fromHttpUrl(openApiConfig.getBaseUrl() + "/api/deals")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build()
                        .toUriString();
                
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(token);
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                // 发送请求
                ResponseEntity<DealSyncDTO> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        DealSyncDTO.class
                );
                
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    DealSyncDTO syncDTO = response.getBody();
                    if (syncDTO != null && syncDTO.getData() != null) {
                        // 处理成交记录数据
                        for (DealSyncDTO.DealData data : syncDTO.getData()) {
                            try {
                                DealRecord dealRecord = new DealRecord();
                                
                                // 保存或更新成交记录
                                dealRecordMapper.insertOrUpdate(dealRecord);
                                totalProcessed++;
                            } catch (Exception e) {
                                log.error("处理成交记录数据失败: visitId={}, productId={}, error={}", 
                                    data.getVisitId(), data.getProductId(), e.getMessage(), e);
                            }
                        }
                        
                        // 检查是否还有更多数据
                        hasMore = page * size < syncDTO.getTotal();
                        page++;
                    } else {
                        hasMore = false;
                    }
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("同步成交记录数据失败: page={}, error={}", page, e.getMessage(), e);
                hasMore = false;
            }
        }
        
        log.info("成交记录数据同步完成，共处理{}条记录", totalProcessed);
    }
} 