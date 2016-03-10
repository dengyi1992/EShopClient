package com.kerray.eshop.view.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.kerray.eshop.net.NetUtil;
import com.kerray.eshop.util.PromptManager;

/**
 * 所有界面的基类
 * Created by kerray on 2015/5/15.
 */
public abstract class BaseUI implements View.OnClickListener
{
    protected Context mContext;
    // 显示到中间容器
    protected ViewGroup showView;
    protected Bundle bundle;

    public BaseUI(Context pContext)
    {
        this.mContext = pContext;
        init();
        setListener();
    }
    protected abstract int getID();

    /**
     * 界面的初始化
     */
    protected abstract void init();

    /**
     * 设置监听
     */
    protected abstract void setListener();

    /**
     * 获取需要在中间容器加载的内容
     * @return
     */
    public View getChild()
    {
        // 设置layout参数

        // root = null
        // root != null
        // return root

        if (showView.getLayoutParams() == null)
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            showView.setLayoutParams(params);
        }

        return showView;
    }

    public View findViewById(int id)
    {
        return showView.findViewById(id);
    }

    /**
     * 获取每个界面的标示——容器联动时的比对依据
     */

    public void setBundle(Bundle pBundle)
    {
        this.bundle = pBundle;
    }

    /**
     * 访问网络的工具
     * @param <Params>
     */
    protected abstract class MyHttpTask<Params> extends AsyncTask<Params, Void, Message>
    {
        /**
         * 类似与Thread.start方法 由于final修饰，无法Override，方法重命名 省略掉网络判断
         * @param params
         * @return
         */
        public final AsyncTask<Params, Void, Message> executeProxy(Params... params)
        {
            if (NetUtil.checkNet(mContext))
                return super.execute(params);
            else
                PromptManager.showNoNetWork(mContext);
            return null;
        }
    }

    /**
     * 要出去的时候调用
     */
    protected void onPause() {
    }

    /**
     * 进入到界面之后
     */
    protected void onResume() {
    }
}
