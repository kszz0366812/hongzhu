package com.insightflow.common.util;

import com.alibaba.fastjson.JSON;
import com.insightflow.vo.LoginInfoVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Objects;

/**
*@Author: sy
*@CreateTime: 2025-05-31
*@Description: 签名工具类
*@Version: 1.0
*/
@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private  String secret;

    @Value( "${jwt.expire}")
    private  long expire;
    // 静态字段
    private static String staticSecret;
    private static long staticExpire;

    // 通过 @PostConstruct 初始化静态字段
    @PostConstruct
    public void init() {
        staticSecret = secret;
        staticExpire = expire;
    }


    public static String generateToken(LoginInfoVO loginInfo) {
        //bean转jsonfast
        String json = JSON.toJSONString(loginInfo);
        return  Jwts.builder()
                .setSubject(json)
                .setExpiration(new Date(System.currentTimeMillis() + staticExpire*1000))
                .signWith(Keys.hmacShaKeyFor(staticSecret.getBytes()))
                .compact();
    }

     public static LoginInfoVO getLoginInfoFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(staticSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String json = claims.getSubject();
            return JSON.parseObject(json, LoginInfoVO.class);
        } catch (Exception e) {
            log.error("验证签名失败", e);
            return null;
        }
    }

    //验证签名
    public static boolean validateToken(String token) {
         try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(staticSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //从req中获取userName
     public static LoginInfoVO getLoginInfo() {
        HttpServletRequest req = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return getLoginInfoFromToken(req.getHeader("Authorization").substring( 7));

     }

}
