package com.insightflow.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Component
public class WxApiUtil {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 调用微信 code2Session 接口
     * @param code 微信登录code
     * @return 微信登录结果
     */
    public WxLoginResult code2Session(String code) {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            log.info("微信登录响应: {}", response.body());
            
            JsonNode jsonNode = objectMapper.readTree(response.body());
            
            WxLoginResult result = new WxLoginResult();
            result.setOpenid(jsonNode.get("openid") != null ? jsonNode.get("openid").asText() : null);
            result.setSessionKey(jsonNode.get("session_key") != null ? jsonNode.get("session_key").asText() : null);
            result.setUnionid(jsonNode.get("unionid") != null ? jsonNode.get("unionid").asText() : null);
            result.setErrcode(jsonNode.get("errcode") != null ? jsonNode.get("errcode").asInt() : 0);
            result.setErrmsg(jsonNode.get("errmsg") != null ? jsonNode.get("errmsg").asText() : null);
            
            return result;
            
        } catch (IOException | InterruptedException e) {
            log.error("调用微信登录接口失败", e);
            WxLoginResult result = new WxLoginResult();
            result.setErrcode(-1);
            result.setErrmsg("系统错误: " + e.getMessage());
            return result;
        }
    }

    /**
     * 微信登录结果
     */
    public static class WxLoginResult {
        private String openid;
        private String sessionKey;
        private String unionid;
        private Integer errcode;
        private String errmsg;

        // Getters and Setters
        public String getOpenid() { return openid; }
        public void setOpenid(String openid) { this.openid = openid; }
        
        public String getSessionKey() { return sessionKey; }
        public void setSessionKey(String sessionKey) { this.sessionKey = sessionKey; }
        
        public String getUnionid() { return unionid; }
        public void setUnionid(String unionid) { this.unionid = unionid; }
        
        public Integer getErrcode() { return errcode; }
        public void setErrcode(Integer errcode) { this.errcode = errcode; }
        
        public String getErrmsg() { return errmsg; }
        public void setErrmsg(String errmsg) { this.errmsg = errmsg; }

        public boolean isSuccess() {
            return errcode != null && errcode == 0 && openid != null;
        }
    }
} 