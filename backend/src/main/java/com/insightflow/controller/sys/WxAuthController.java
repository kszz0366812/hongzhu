package com.insightflow.controller.sys;

import com.alibaba.fastjson.JSONObject;
import com.insightflow.dto.WxLoginDTO;
import com.insightflow.service.sys.WxAuthService;
import com.insightflow.common.util.JwtUtils;
import com.insightflow.common.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "微信认证")
@RestController
@RequestMapping("/auth/wx")
public class WxAuthController {

    @Autowired
    private WxAuthService wxAuthService;


    @Autowired
    HttpServletRequest req;

    @Operation(summary = "微信小程序登录")
    @RequestMapping("/login")
    public Result<Object> wxLogin(@RequestBody WxLoginDTO wxLoginDTO) {
        Object result = wxAuthService.wxLogin(wxLoginDTO);
        return Result.success(result);
    }

    @Operation(summary = "检查员工信息")
    @RequestMapping("/check-employee")
    public Result<Object> checkEmployee(@RequestParam String phone) {
        Object result = wxAuthService.checkEmployee(phone);
        return Result.success(result);
    }

    @Operation(summary = "绑定员工信息")
    @RequestMapping("/bind-employee")
    public Result<Object> bindEmployee(@RequestBody JSONObject params) {
        //从jwt中获取openId
        if (!JwtUtils.validateToken(req.getHeader("Authorization").substring( 7))) {
            return Result.error(HttpServletResponse.SC_UNAUTHORIZED,"请先登录");
        }
        String openId = JwtUtils.getLoginInfo().getOpenid();
        Object result = wxAuthService.bindEmployee(openId,params.getString("employeeCode"));
        return Result.success(result);
    }
} 