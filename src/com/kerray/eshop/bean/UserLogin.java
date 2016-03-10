package com.kerray.eshop.bean;

/**
 * 功能:      用户登陆
 * 创建人:     kerray
 * 创建时间:    2015/7/22/22:36
 */
public class UserLogin
{
    public boolean loginSucceed;
    public int retcode;
    public User user;

    public static class User
    {
        public Long id;
        public String 用户名;
        public String 密码;
        public Double 余额;
        public String 邮箱;
        public String 手机号;
        public String 地址;
        public boolean state;
    }
}
