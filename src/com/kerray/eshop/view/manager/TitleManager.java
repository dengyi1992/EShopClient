package com.kerray.eshop.view.manager;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.widgets.AutoClearEditText;
import org.apache.commons.lang3.StringUtils;

import java.util.Observable;
import java.util.Observer;


/**
 * @类名:TitleManager
 * @功能: 标题容器的管理工具
 * @创建人:kerray
 * @创建时间:2015/7/11/15:54
 */
public class TitleManager implements Observer, OnClickListener
{
    private RelativeLayout rl_title;
    /** 默认主页 */
    private RelativeLayout rl_index;
    /** 显示自定义内容 */
    private RelativeLayout rl_show;
    /** 显示搜索标题 */
    private LinearLayout rl_search;
    /**
     * 默认显示的标题
     */
    private TextView tv_index;
    /** 返回键 */
    private TextView tv_left;
    /** 中间的信息 */
    private TextView tv_info;
    /** 右键 */
    private TextView tv_right;
    /**
     * 搜索输入框
     */
    private AutoClearEditText et_search;
    /**
     * 搜索按钮
     */
    private ImageButton ibtn_search;

    public TitleManager()
    {
    }

    private static TitleManager instance = new TitleManager();

    public static TitleManager getInstance()
    {
        return instance;
    }

    public void init(Activity activity)
    {
        // 根标题布局
        rl_title = (RelativeLayout) activity.findViewById(R.id.rl_title);
        // 默认的标题
        rl_index = (RelativeLayout) activity.findViewById(R.id.rl_index);
        // 默认显示的标题
        tv_index = (TextView) rl_index.findViewById(R.id.tv_index);

        //---------------------------自定义的标题--------------------------------
        // 自定义的显示布局
        rl_show = (RelativeLayout) activity.findViewById(R.id.rl_show);
        // 返回键
        tv_left = (TextView) rl_show.findViewById(R.id.tv_back);
        // 中间的textView
        tv_info = (TextView) rl_show.findViewById(R.id.tv_info);
        // 最右边的按钮
        tv_right = (TextView) rl_show.findViewById(R.id.tv_right);

        //---------------------------搜索的标题--------------------------------
        // 显示搜索标题
        rl_search = (LinearLayout) activity.findViewById(R.id.rl_search);
        // 输入框
        et_search = (AutoClearEditText) rl_search.findViewById(R.id.et_search);
        // 搜索按钮
        ibtn_search = (ImageButton) rl_search.findViewById(R.id.ibtn_search);

        setListener();
    }

    private void setListener()
    {
        /** 设置左键监听 */
        tv_left.setOnClickListener(this);
        /** 设置右键监听 */
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
        case R.id.tv_back: // 返回键
            if (titleClickListener != null)
                titleClickListener.leftButtonClick();
            break;
        case R.id.tv_right: // 右键的内容
            if (titleClickListener != null)
                titleClickListener.rightButtonClick();
            break;
        case R.id.et_search:

            break;
        }
    }

    /**
     * 暴露一个左右按钮点击事件的监听接口 通过不同的BaseView传递这个接口的实现，来控制左右点击实现不同的功能
     */
    public interface TitleClickListener
    {
        public void rightButtonClick();

        public void leftButtonClick();
    }

    public void setTitleClickListener(TitleClickListener titleClickListener)
    {
        this.titleClickListener = titleClickListener;
    }

    private TitleClickListener titleClickListener;

    // ============================================ 控制显示 内容 隐藏 ==============================

    /**
     * 显示默认的标题
     */
    public void showIndex()
    {
        initTitle();
        rl_title.setVisibility(View.VISIBLE);
        rl_index.setVisibility(View.VISIBLE);
        rl_show.setVisibility(View.GONE);
        rl_search.setVisibility(View.GONE);
    }

    /**
     * 显示返回键和中间的textView
     */
    public void showBackAndTextView()
    {
        initTitle();
        rl_title.setVisibility(View.VISIBLE);
        rl_show.setVisibility(View.VISIBLE);
        tv_left.setVisibility(View.VISIBLE);
        tv_info.setVisibility(View.VISIBLE);
    }

    /**
     * 正常的显示：返回，TextView和右边的按钮都显示
     */
    public void showCommon()
    {
        initTitle();
        rl_title.setVisibility(View.VISIBLE);
        rl_show.setVisibility(View.VISIBLE);
        tv_left.setVisibility(View.VISIBLE);
        tv_info.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.VISIBLE);
    }

    /**
     * 只显示中间的TextView
     */
    public void showMiddleTextView()
    {
        initTitle();
        rl_title.setVisibility(View.VISIBLE);
        rl_show.setVisibility(View.VISIBLE);
        tv_left.setVisibility(View.INVISIBLE);
        tv_info.setVisibility(View.VISIBLE);
        tv_right.setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化所有的标题，设置为不可见
     */
    public void initTitle()
    {
        rl_title.setVisibility(View.GONE);
        rl_index.setVisibility(View.GONE);
        rl_show.setVisibility(View.GONE);
        tv_left.setVisibility(View.GONE);
        tv_info.setVisibility(View.GONE);
        tv_right.setVisibility(View.GONE);
    }

    /**
     * 显示搜索标题
     */
    public void showSearch()
    {
        initTitle();
        rl_title.setVisibility(View.VISIBLE);
        rl_search.setVisibility(View.VISIBLE);
    }

    // ======================================================设置文本内容=============================

    /**
     * 设置默认标题的文本内容
     * @param text
     */
    public void setIndexTextView(String text)
    {
        tv_index.setText(text);
    }
    /**
     * 设置中间的文本内容
     * @param text
     */
    public void setMiddleTextView(String text)
    {
        tv_info.setText(text);
    }
    /**
     * 设置左边文本的内容
     * @param text
     */
    public void setLeftButtonText(String text)
    {
        tv_left.setText(text);
    }
    /**
     * 设置右边文本的内容
     * @param text
     */
    public void setRightButtonText(String text)
    {
        tv_right.setText(text);
    }

    // ============================================ 控制显示 内容 隐藏==============================

    public void update(Observable observable, Object data)
    {
        // 控制标题变动
        if (data != null && StringUtils.isNumeric(data.toString()))
        {
            int id = Integer.parseInt(data.toString());
            switch (id)
            {
            case ConstantValue.HOME_VIEW:                   // 主界面
            case ConstantValue.SEARCH_VIEW:                 // 搜索页面
                showSearch();
                break;
            case ConstantValue.CATEGORY_VIEW:               // 全部分类界面
            case ConstantValue.CART_VIEW:                   // 购物车界面
            case ConstantValue.PERSONEL_VIEW:               // 个人中心界面
                showIndex();
                break;
            case ConstantValue.PRODUCT_DETAIL_VIEW:         // 商品详情界面
            case ConstantValue.SHOPINFO_LIST_VIEW:          // 商品列表界面
            case ConstantValue.LOGIN_VIEW:                  // 用户登陆界面
            case ConstantValue.REGISTER_VIEW:               // 用户注册界面
                showBackAndTextView();
                break;
            case ConstantValue.HOME_SHAKE:
                initTitle();
                break;
            }
        }
    }

    /**
     * 得到左边按钮的文本
     * @return
     */
    public String getLeftButtonText()
    {
        return tv_left.getText().toString();
    }

}
