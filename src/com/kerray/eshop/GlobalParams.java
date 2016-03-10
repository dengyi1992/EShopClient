package com.kerray.eshop;

import android.app.Activity;
import android.content.Context;
import com.kerray.eshop.bean.UserLogin;
import org.apache.http.client.CookieStore;

public class GlobalParams
{
    /**
     * 代理的ip
     */
    public static String PROXY = "";
    /**
     * 代理的端口
     */
    public static int PORT = 0;

    public static String PROXY_IP = "";
    public static int PROXY_PORT;

    /**
     * 获取手机屏幕的分辨率
     */
    public static int WIN_WIDTH;
    public static int WIN_HEIGHT;

    public static Activity gMainActivity;

    public static Context CONTEXT;

    public static int ID;
    /** 判断用户是否登录的全局变量 */
    //public static boolean isLogin = false;
    /** 保存用户登陆后的id */
    public static Long user_id = 0L;

    public static String JSESSIONID;

    public static CookieStore cookieStore = null;
    /**
     * 存放登陆成功后的用户信息的全局变量
     */
    public static UserLogin mUserLogin;

    public static UserLogin.User mUser;

    public static boolean islogin = false;

    /** 购物车商品数量 */
    public static int count;
    /**
     * 判断是否需要再次连接网络获取分类信息
     */
    public static boolean isdownload = true;

}
