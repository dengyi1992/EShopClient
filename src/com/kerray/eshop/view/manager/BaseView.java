package com.kerray.eshop.view.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.GlobalParams;
import com.kerray.eshop.R;
import com.kerray.eshop.bean.UserLogin;
import com.kerray.eshop.net.NetWorkUtil;
import com.kerray.eshop.util.GsonUtils;
import com.kerray.eshop.util.PromptManager;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public abstract class BaseView implements View.OnClickListener
{
    protected Context mContext;
    protected Bundle mBundle;
    protected SharedPreferences sp;
    protected ViewGroup showView;
    // 圆形进度条，联网请求数据前显示，成功或者失败时stop

    // 构造方法
    // 模板方法
    public BaseView(Context context, Bundle bundle)
    {
        super();
        mContext = context;
        this.mBundle = bundle;
        init();
        setListener();
    }


    /**
     * 中间容器需要显示的内容
     * @return
     */
    public View getView()
    {
        // LayoutParams 类型LinearLayout？
        // 原则：不是依据layout配置文件中设置LinearLayout
        // 看需要将LinearLayout添加到那个父容器里面（RelativeLayout）
        if (showView.getLayoutParams() == null)
        {
            // (RelativeLayout.LayoutParams)
            // child.getLayoutParams();如果设置成其他的layout，有可能会抛出类型转换的异常
            showView.setLayoutParams(new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return showView;
    }

    /**
     * 每个BaseView的子类都有一个唯一的标示
     * @return
     */
    public abstract int getId();

    /**
     * 初始化
     * @return
     */
    protected abstract void init();

    /**
     * 设置监听
     * @return
     */
    protected abstract void setListener();

    @Override
    public void onClick(View v)
    {
    }


    protected View findViewById(int resId)
    {
        return showView.findViewById(resId);
    }

    // 调用类：UIManager(addView removeView)

    /**
     * 当界面加载完成(addView之后)，更行或耗时操作
     */
    public void onResume()
    {
        /** 设置默认监听, 防止遗留的监听事件影响 */
        TitleManager.getInstance().setTitleClickListener(null);

        /** 界面显示的时候设置左右两边button的点击事件 */
        setTitleClickListener();

        /** 当界面显示时，显示自己定义的文本 */
        setTitleContent();
    }

    /**
     * 当要回收到界面的时候（removeView之前），取消掉耗时的操作
     */
    public void onPause()
    {
    }

    // Fragment

    /**
     * 访问网络的异步任务
     * @param <Params>
     * @author Administrator
     */
    protected abstract class MyHttpTask<Params, Result> extends AsyncTask<Params, Void, Result>
    {
        ProgressBar mProgressBar;

        /**
         * 在原有的execute方法上增加了判断网络状态的功能
         * @param params
         * @return
         */
        public final AsyncTask<Params, Void, Result> executeProxy(
          Params... params)
        {
            if (NetWorkUtil.checkNetWork(mContext))
                return super.execute(params);
            else
                PromptManager.showNoNetWork(mContext);
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            mProgressBar = new ProgressBar(mContext);
            // 位移的高度,使进度条在屏幕中间，但不是一个定制，调出来的，不好可以修改,，没找到更好的方案
            mProgressBar.setY(GlobalParams.WIN_HEIGHT * 7 / 20);
            mProgressBar.setX(GlobalParams.WIN_WIDTH * 9 / 20); // 位移的宽度
            // 设置进度条的颜色，
            mProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progressbar));
            showView.addView(mProgressBar);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Result result)
        {
            showView.removeView(mProgressBar);
            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled()
        {
            showView.removeView(mProgressBar);
            super.onCancelled();
        }
    }


    /**
     * 重写xUtils请求失败的处理,没有网络或者服务器异常时的处理
     */
    protected abstract class MyRequestCallBack<T> extends RequestCallBack<T>
    {
        ProgressBar mProgressBar;

        @Override
        public void onStart()
        {
            mProgressBar = new ProgressBar(mContext);
            // 位移的高度,使进度条在屏幕中间，但不是一个定制，调出来的，不好可以修改,，没找到更好的方案
            mProgressBar.setY(GlobalParams.WIN_HEIGHT * 7 / 20);
            mProgressBar.setX(GlobalParams.WIN_WIDTH * 9 / 20); // 位移的宽度
            // 设置进度条的颜色，
            mProgressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.progressbar));
            showView.addView(mProgressBar);
        }

        @Override
        public void onSuccess(ResponseInfo<T> responseInfo)
        {
            showView.removeView(mProgressBar);
        }

        public void onFailure(HttpException e, String s)
        {
            showView.removeView(mProgressBar);
            if (NetWorkUtil.checkNetWork(mContext))
                PromptManager.showToast(mContext, "服务器忙！请稍后重试......");
            else
                PromptManager.showNoNetWork(mContext);
        }
    }

    private boolean islogin = false;

    /**
     * xUtils不知道怎么发送cookie，试过很多都不行才用的HttpURLConnection
     */
    Runnable mRunnable = new Runnable()
    {
        public void run()
        {
            try
            {
                URL url = new URL(ConstantValue.IS_LOGIN);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Cookie", GlobalParams.JSESSIONID);// 带上cookie才能保持登陆状态
                conn.setReadTimeout(1 * 1000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200)
                {
                    String getResult = inputStream2String(conn.getInputStream());
                    readData(getResult);
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    };

    private String inputStream2String(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1)
            baos.write(i);

        return baos.toString();
    }

    private void readData(String result)
    {
        UserLogin userLogin = GsonUtils.jsonToBean(result, UserLogin.class);
        islogin = userLogin.loginSucceed;
    }

    /**
     * 获取登陆状态的方法
     * @return
     */
    public boolean getIslogin()
    {
        /*try
        {
            if (NetWorkUtil.checkNetWork(mContext))
            {
                Thread thread = new Thread(mRunnable);
                thread.start();
                thread.join();  // 待一个或多个子线程的结果，然后主线程再继续执行
            } else
                PromptManager.showNoNetWork(mContext);
            LogUtils.i("getIslogin===" + islogin);
            return islogin;
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/

        new MyHttpTask<String, String>()
        {
            protected String doInBackground(String... params)
            {
                return getSession(ConstantValue.IS_LOGIN);
            }

            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                UserLogin userLogin = GsonUtils.jsonToBean(result, UserLogin.class);
                if (null != userLogin)
                    islogin = userLogin.loginSucceed;
                else
                    PromptManager.showToast(mContext, "json错误！");
                LogUtils.i("getIslogin===" + islogin);
            }
        }.executeProxy();
        return islogin;
    }

    public String getSession(String urlStr)
    {
        try
        {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (null != GlobalParams.JSESSIONID)
                conn.setRequestProperty("Cookie", GlobalParams.JSESSIONID);// 带上cookie才能保持登陆状态
            conn.setReadTimeout(2 * 1000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200)
            {
                String session_value = conn.getHeaderField("Set-Cookie");

                GlobalParams.JSESSIONID = session_value;
                LogUtils.i("Session Value = " + session_value);

                String getResult = inputStream2String(conn.getInputStream());
                return getResult;
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (ProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String MyHttpURLConnection(String urlStr)
    {
        try
        {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (null != GlobalParams.JSESSIONID)
                conn.setRequestProperty("Cookie", GlobalParams.JSESSIONID);// 带上cookie才能保持登陆状态
            conn.setReadTimeout(1 * 1000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200)
            {
                String getResult = inputStream2String(conn.getInputStream());
                return getResult;
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (ProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void setmBundle(Bundle mBundle)
    {
        this.mBundle = mBundle;
    }

    /**
     * 设置标题左右两边按钮的监听 默认设置为null
     */
    protected abstract void setTitleClickListener();

    /**
     * 设置中间TextView文本 和 左右两边button的文本
     */
    protected abstract void setTitleContent();

}
