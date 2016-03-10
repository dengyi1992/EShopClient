package com.kerray.eshop;

import android.os.Environment;

import java.io.File;

/**
 * Created by kerray on 2015/7/9.
 */
public interface ConstantValue
{
//    public static String ESHOP_URI = "http://192.168.1.124:8080/EShop";
    String ESHOP_URI = "http://eshopservice.sinaapp.com/1/eshopservice";
    /**
     * app更新地址
     */
    String UPDATE_URL = ESHOP_URI + "/update/info.html";
    /**
     * 主页列表的url
     */
    String HOME_LIST_URL = ESHOP_URI + "/home_list.do";
    /**
     * 全部分类列表的url
     */
    //http://localhost:8080/EShop/shoptype_list.do
    String SHOPTYPE_LIST_URL = ESHOP_URI + "/shoptype_list.do";
    /**
     * 商品列表的url
     */
    String SHOPINFO_LIST_URL = ESHOP_URI + "/shopinfo_infoList.do";
    /**
     * 商品列表的url
     */
    String SHOPINFO_INFOID_URL = ESHOP_URI + "/shopinfo_infoId.do";
    /**
     * 同上，添加依据列名排序
     */
    //http://localhost:8080/EShop/shopinfo_infoListOrder.do?id=19&orderColumn=reputationNum&Asc=false
    String SHOPINFO_INFOID_ORDER_URL = ESHOP_URI + "/shopinfo_infoListOrder.do";
    /**
     * 用户登陆
     */
    //http://localhost:8080/EShop/user_login.do?username=admin&password=admin
    String USER_LOGIN_URL = ESHOP_URI + "/user_login.do";
    /**
     * 用户注册
     */
    //http://localhost:8080/EShop/user_register.do?username=user1&password=admin&phone=110&email=1111
    String USER_REGISTER_URL = ESHOP_URI + "/user_register.do";
    /**
     * 进入购物车的检验用户是否登陆url
     */
    //http://localhost:8080/EShop/user_isLoging.do
    String IS_LOGIN = ESHOP_URI + "/user_isLoging.do";
    /**
     * 根据用户id查询所有购物车信息
     */
    //http://localhost:8080/EShop/cart_list.do?user_id=1
    String CART_LIST_URL = ESHOP_URI + "/cart_list.do";
    /**
     * 添加商品到购物车
     */
    //http://localhost:8080/EShop/cart_add.do?user_id=1&shopingo_id=5
    String ADD_CART_URL = ESHOP_URI + "/cart_add.do";
    /**
     * 修改购物车中商品数量
     */
    //http://localhost:8080/EShop/cart_update.do?cart_id=46&product_num=2
    String CART_UPDATER_URL = ESHOP_URI + "/cart_update.do";
    /**
     * 删除购物车中的商品
     */
    //http://localhost:8080/EShop/cart_delete.do?cart_id=46
    String CART_DELETE_URL = ESHOP_URI + "/cart_delete.do";
    /**
     * 结账
     */
    //http://localhost:8080/EShop/user_payment.do?user_id=1&total=100
    String CART_PAYMENT_URL = ESHOP_URI + "/user_payment.do";
    /**
     * 注销
     */
    //http://localhost:8080/EShop/user_logout.do
    String USER_LOGOUT_URL = ESHOP_URI + "/user_logout.do";
    /**
     * 下载新版本apk存储时的名称
     */
    String UPDATE_CACHA_FILE = "/EShop2.0.apk";

    //********************************************************************
    //************************   xUtils配置    ***************************
    //********************************************************************

    /**
     * 自定义log标记前缀
     */
    String LOGUTILS_CUSTOM_TAG_PREFIX = "kerray";
    /**
     * log日志是否打开
     */
    boolean LOG_OUTPUT = true;
    /**
     * 缓存文件夹，xUtils已经主动创建目录在sd卡 Android/data/package-name
     */
    String CACHE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android/data/EShop";

    //********************************************************************
    //************************   中间容器的ID    **************************
    //********************************************************************

    /**
     * 首页
     */
    int HOME_VIEW = 50;
    /**
     * 摇一摇
     */
    int HOME_SHAKE = 51;
    /**
     * 全部分类
     */
    int CATEGORY_VIEW = 0;
    /**
     * 商品列表
     */
    int SHOPINFO_LIST_VIEW = 60;
    /**
     * 商品详情
     */
    int PRODUCT_DETAIL_VIEW = 70;
    /**
     * 购物车页面
     */
    int CART_VIEW = 80;
    /**
     * 个人中心页面
     */
    int PERSONEL_VIEW = 90;
    /**
     * 搜索页面
     */
    int SEARCH_VIEW = 100;
    /**
     * 登陆页面
     */
    int LOGIN_VIEW = 110;
    /**
     * 注册页面
     */
    int REGISTER_VIEW = 120;
}
