package com.kerray.eshop.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.BottomManager;
import com.kerray.eshop.view.manager.UIManager;

/**
 * 功能:      搜索页面
 * 创建人:     kerray
 * 创建时间:    2015/7/14/14:33
 */
public class SearchView extends BaseView
{
    public SearchView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.SEARCH_VIEW;
    }

    @Override
    protected void init()
    {
        showView = (RelativeLayout) View.inflate(mContext, R.layout.view_search, null);
        UIManager.getInstance().clear();// 清理返回键
    }

    @Override
    protected void setListener()
    {

    }

    @Override
    protected void setTitleClickListener()
    {
        BottomManager.getInstrance().setSearchRadiobutton();
    }

    @Override
    protected void setTitleContent()
    {

    }
}
