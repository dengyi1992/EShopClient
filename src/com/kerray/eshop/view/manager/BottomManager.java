package com.kerray.eshop.view.manager;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.view.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Observable;
import java.util.Observer;

/**
 * 控制底部导航容器
 * @author Administrator
 */
public class BottomManager implements Observer, OnClickListener
{
    /** **** 第一步：管理对象的创建(单例模式) ************************************************** */
    // 创建一个静态实例
    private static BottomManager instrance;

    private RadioGroup home_radio_button_group;

    private RadioButton rb_home_tab_main;
    private RadioButton rb_home_tab_search;
    private RadioButton rb_home_tab_category;
    private RadioButton rb_home_tab_cart;
    private RadioButton rb_home_tab_personal;


    // 构造私有
    private BottomManager()
    {
    }

    // 提供统一的对外获取实例的入口
    public static BottomManager getInstrance()
    {
        if (instrance == null)
            instrance = new BottomManager();
        return instrance;
    }

    public void init(Activity activity)
    {
        home_radio_button_group = (RadioGroup) activity.findViewById(R.id.home_radio_button_group);
        rb_home_tab_main = (RadioButton) home_radio_button_group.findViewById(R.id.rb_home_tab_main);
        rb_home_tab_search = (RadioButton) home_radio_button_group.findViewById(R.id.rb_home_tab_search);
        rb_home_tab_category = (RadioButton) home_radio_button_group.findViewById(R.id.rb_home_tab_category);
        rb_home_tab_cart = (RadioButton) home_radio_button_group.findViewById(R.id.rb_home_tab_cart);
        rb_home_tab_personal = (RadioButton) home_radio_button_group.findViewById(R.id.rb_home_tab_personal);
        // 设置监听
        setListener();
    }

    /**
     * 设置监听
     */
    private void setListener()
    {
        rb_home_tab_main.setOnClickListener(this);
        rb_home_tab_search.setOnClickListener(this);
        rb_home_tab_category.setOnClickListener(this);
        rb_home_tab_cart.setOnClickListener(this);
        rb_home_tab_personal.setOnClickListener(this);
    }
    /**
     *主页选中
     */
    public void setMainRadiobutton()
    {
        rb_home_tab_main.setChecked(true);
    }
    /**
     * 搜索选中
     */
    public void setSearchRadiobutton()
    {
        rb_home_tab_search.setChecked(true);
    }
    /**
     * 分类选中
     */
    public void setCategoryRadiobutton()
    {
        rb_home_tab_category.setChecked(true);
    }
    /**
     * 购物车选中
     */
    public void setCartRadiobutton()
    {
        rb_home_tab_cart.setChecked(true);
    }
    /**
     * 个人中心
     */
    public void setPersonalRadiobutton()
    {
        rb_home_tab_personal.setChecked(true);
    }

    // 控制底部导航的变动
    @Override
    public void update(Observable observable, Object data)
    {
        if (data != null && StringUtils.isNumeric(data.toString()))
        {
            int id = Integer.parseInt(data.toString());
            switch (id)
            {
            case ConstantValue.HOME_VIEW:
            case ConstantValue.CATEGORY_VIEW:
            case ConstantValue.CART_VIEW:
            case ConstantValue.PERSONEL_VIEW:
                setBottomVisibility();
                break;
            case ConstantValue.SHOPINFO_LIST_VIEW:
            case ConstantValue.PRODUCT_DETAIL_VIEW:
            case ConstantValue.LOGIN_VIEW:
            case ConstantValue.REGISTER_VIEW:
            case ConstantValue.HOME_SHAKE:
                setBottomInVisibility();
                break;
            }
        }
    }

    /** 切换到相应的页面 */
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.rb_home_tab_main:                                         // 首页
            UIManager.getInstance().changeView(HomeView.class, null);
            break;
        case R.id.rb_home_tab_search:                                       // 搜索
            UIManager.getInstance().changeView(SearchView.class, null);
            break;
        case R.id.rb_home_tab_category:                                     // 全部分类
            UIManager.getInstance().changeView(CategoryView1.class, null);
            break;
        case R.id.rb_home_tab_cart:                                         // 购物车
            UIManager.getInstance().changeView(CartView.class, null);
            break;
        case R.id.rb_home_tab_personal:                                     // 个人中心
            UIManager.getInstance().changeView(PersonelView.class, null);
            break;
        }
    }

    /**
     * 设置底部导航可见
     */
    public void setBottomVisibility()
    {
        if (home_radio_button_group.getVisibility() != View.VISIBLE)
            home_radio_button_group.setVisibility(View.VISIBLE);
    }

    /**
     * 设置底部导航不可见
     */
    public void setBottomInVisibility()
    {
        if (home_radio_button_group.getVisibility() != View.GONE)
            home_radio_button_group.setVisibility(View.GONE);
    }

    /**
     * 得到购物车商品的数量
     * @return
     */
    public int getShoppingCateNumber()
    {
        return 0;
    }

}
