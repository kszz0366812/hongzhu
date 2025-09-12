package com.insightflow.dto;

import lombok.Data;

@Data
public class WxLoginDTO {

    /**
     * 微信登录code
     */
    private String code;

    /**
     * 手机号code
     */
    private String phoneCode;

    /**
     * 用户信息
     */
    private WxUserInfo userInfo;

    @Data
    public static class WxUserInfo {
        /**
         * 微信昵称
         */
        private String nickName;

        /**
         * 微信头像
         */
        private String avatarUrl;

        /**
         * 性别
         */
        private Integer gender;

        /**
         * 国家
         */
        private String country;

        /**
         * 省份
         */
        private String province;

        /**
         * 城市
         */
        private String city;
    }
} 