package com.insightflow.service.impl.sys;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.insightflow.vo.LoginInfoVO;
import com.insightflow.dto.WxLoginDTO;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.entity.sys.User;
import com.insightflow.service.sys.WxAuthService;
import com.insightflow.service.sys.UserService;
import com.insightflow.service.employee.EmployeeInfoService;
import com.insightflow.common.util.JwtUtils;
import com.insightflow.common.util.WxApiUtil;
import com.insightflow.common.util.WxApiUtil.WxLoginResult;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WxAuthServiceImpl implements WxAuthService {

    @Autowired
    private EmployeeInfoService employeeInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private WxApiUtil wxApiUtil;

    @Override
    public Object wxLogin(WxLoginDTO wxLoginDTO) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 调用微信 code2Session 接口获取 openid 和 session_key
            WxLoginResult wxResult = wxApiUtil.code2Session(wxLoginDTO.getCode());
            
            if (!wxResult.isSuccess()) {
                log.error("微信登录失败: errcode={}, errmsg={}", wxResult.getErrcode(), wxResult.getErrmsg());
                result.put("success", false);
                result.put("message", "微信登录失败: " + wxResult.getErrmsg());
                return result;
            }
            
            String openid = wxResult.getOpenid();
            String unionid = wxResult.getUnionid();
            String sessionKey = wxResult.getSessionKey();
            
            log.info("微信登录成功: openid={}, unionid={}", openid, unionid);
            
            // 2. 查找或创建用户
            User user = findOrCreateUser(openid, unionid, wxLoginDTO);

            // 3. 生成JWT token
            String token = JwtUtils.generateToken(userService.generateLoginInfo( user));
            
            // 4. 返回结果
            result.put("success", true);
            result.put("token", token);
            result.put("openid", openid);
            result.put("unionid", unionid);
            result.put("isNewUser",user.getEmployeeId()==null);
            result.put("userInfo", buildUserInfo(user));
            
            return result;
            
        } catch (Exception e) {
            log.error("微信登录异常", e);
            result.put("success", false);
            result.put("message", "系统异常: " + e.getMessage());
            return result;
        }
    }

    /**
     * 查找或创建用户
     */
    private User findOrCreateUser(String openid, String unionid, WxLoginDTO wxLoginDTO) {
        // 先根据 openid 查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        User existingUser = userService.getOne(queryWrapper);
        
        if (existingUser != null) {
            // 用户已存在，更新信息
            updateUserInfo(existingUser, wxLoginDTO);
            userService.updateById(existingUser);
            return existingUser;
        }
        
        // 如果 unionid 不为空，尝试根据 unionid 查找用户
        if (unionid != null && !unionid.isEmpty()) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionid", unionid);
            existingUser = userService.getOne(queryWrapper);
            
            if (existingUser != null) {
                // 找到用户，更新 openid
                existingUser.setOpenid(openid);
                updateUserInfo(existingUser, wxLoginDTO);
                userService.updateById(existingUser);
                return existingUser;
            }
        }
        
        // 创建新用户
        User newUser = new User();
        newUser.setOpenid(openid);
        newUser.setUnionid(unionid);
        newUser.setStatus(1); // 默认启用
        newUser.setIsBound(0); // 默认未绑定员工
        
        updateUserInfo(newUser, wxLoginDTO);
        
        userService.save(newUser);
        return newUser;
    }

    /**
     * 更新用户信息
     */
    private void updateUserInfo(User user, WxLoginDTO wxLoginDTO) {
        if (wxLoginDTO.getUserInfo() != null) {
            WxLoginDTO.WxUserInfo userInfo = wxLoginDTO.getUserInfo();
            user.setNickname(userInfo.getNickName());
            user.setAvatarUrl(userInfo.getAvatarUrl());
            user.setGender(userInfo.getGender());
            user.setCountry(userInfo.getCountry());
            user.setProvince(userInfo.getProvince());
            user.setCity(userInfo.getCity());
        }
        user.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 构建用户信息返回
     */
    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("avatarUrl", user.getAvatarUrl());
        userInfo.put("employeeId",user.getEmployeeId());
        userInfo.put("gender", user.getGender());
        userInfo.put("country", user.getCountry());
        userInfo.put("province", user.getProvince());
        userInfo.put("city", user.getCity());
        userInfo.put("isBound", user.getIsBound());
        return userInfo;
    }

    @Override
    public Object checkEmployee(String phone) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里需要根据手机号查询员工信息
            // 由于EmployeeInfo表结构不同，需要适配查询逻辑
            
            result.put("success", false);
            result.put("message", "员工查询功能需要根据实际表结构实现");
            
        } catch (Exception e) {
            log.error("检查员工信息异常", e);
            result.put("success", false);
            result.put("message", "系统异常: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Object bindEmployee(String openid, String employeeCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            QueryWrapper<EmployeeInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("employee_code", employeeCode);
            EmployeeInfo employeeInfo = employeeInfoService.getOne(queryWrapper);

            QueryWrapper<User>  queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("openid", openid);
            User existingUser = userService.getOne(queryWrapper1);
            if (existingUser != null) {
                existingUser.setEmployeeId(employeeInfo.getId());
                existingUser.setRealName(employeeInfo.getName());
                if(StringUtils.isEmpty(existingUser.getUsername())){
                    existingUser.setUsername(employeeInfo.getEmployeeCode());
                    existingUser.setPassword("123456");
                }
                existingUser.setIsBound(1);
                userService.updateById(existingUser);

                LoginInfoVO logginInfo = userService.generateLoginInfo(existingUser);
                // 3. 生成JWT token
                String token = JwtUtils.generateToken(logginInfo);
                result.put("success", true);
                existingUser.setPassword("");
                result.put("userInfo", existingUser);
                result.put("token", token);
            }

            
            result.put("success", false);
            result.put("message", "请先登录");
            
        } catch (Exception e) {
            log.error("绑定员工信息异常", e);
            result.put("success", false);
            result.put("message", "系统异常: " + e.getMessage());
        }
        
        return result;
    }
} 