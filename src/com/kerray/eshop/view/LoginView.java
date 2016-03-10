package com.kerray.eshop.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.GlobalParams;
import com.kerray.eshop.R;
import com.kerray.eshop.bean.UserLogin;
import com.kerray.eshop.util.GsonUtils;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import org.apache.http.Header;

/**
 * 功能:      登陆页面
 * 创建人:     kerray
 * 创建时间:    2015/7/20/17:11
 */
public class LoginView extends BaseView implements View.OnClickListener
{
    @ViewInject(R.id.et_username)
    private EditText et_username;
    @ViewInject(R.id.et_password)
    private EditText et_password;

    @ViewInject(R.id.bt_register)
    private Button bt_register;
    @ViewInject(R.id.tb_isShowPassword)
    private ToggleButton tb_isShowPassword;

    private boolean isDisplayflag = false;//是否显示密码
    private String getUsername;
    private String getPassword;

    @Override
    protected void init()
    {
        showView = (ViewGroup) View.inflate(mContext, R.layout.view_login, null);
        ViewUtils.inject(this, showView);

        getUsername = et_username.getText().toString();
        getPassword = et_password.getText().toString();
    }

    @Override
    protected void setListener()
    {
        bt_register.setOnClickListener(this);

        tb_isShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                    //隐藏
                    et_password.setInputType(0x90);
                else
                    //明文显示
                    et_password.setInputType(0x81);
            }
        });


    }

    @OnClick({ R.id.bt_login, R.id.bt_register })
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.bt_login:
            SubmitData1();
            break;
        case R.id.bt_register://  跳转到注册页面
            UIManager.getInstance().changeView(RegisterView.class, null);
            break;
        }
    }

    private void SubmitData()
    {
        getUsername = et_username.getText().toString();
        getPassword = et_password.getText().toString();

        // 简单判断
        if (getUsername.equals("") || getUsername.length() <= 0 || getPassword.equals("") || getPassword.length() <= 0)
            PromptManager.showToast(mContext, "密码不能为空");

        // 拼接url
        final String requestUrl = ConstantValue.USER_LOGIN_URL + "?username=" + getUsername + "&password=" + getPassword;
        final HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0 * 1000);
        //设置默认请求的缓存时间
        httpUtils.configDefaultHttpCacheExpiry(0);
        //设置线程数
        httpUtils.configRequestThreadPoolSize(1);
        // 拼接url
        httpUtils.send(HttpRequest.HttpMethod.GET, requestUrl, new MyRequestCallBack<String>()
        {
            public void onSuccess(ResponseInfo<String> responseInfo)
            {
                super.onSuccess(responseInfo);
                //截取session
                Header[] allHeaders = responseInfo.getAllHeaders();
                if (null != allHeaders && !"".equals(allHeaders))       // Headers 的空值判断
                {
                    for (int i = 0; i < allHeaders.length; i++)
                    {
                        String res = allHeaders[i].toString();
                        int begin = res.indexOf("JSESSIONID");
                        int end = res.indexOf("; Path=");
                        if (begin > 0)
                            GlobalParams.JSESSIONID = res.substring(begin, end);
                    }
                } else
                    LogUtils.i("Headers为空");

                LogUtils.i("GlobalParams.JSESSIONID===" + GlobalParams.JSESSIONID);
                //SharePrefUtil.saveString(mContext, "JSESSIONID", GlobalParams.JSESSIONID);
                /**
                 * 一定要等session获取完成后再往下执行,低级错误!!!!!!
                 */
                ProcessData(responseInfo.result);

            }
        });
    }

    private void SubmitData1()
    {
        getUsername = et_username.getText().toString();
        getPassword = et_password.getText().toString();

        // 简单判断
        if (getUsername.equals("") || getUsername.length() <= 0 || getPassword.equals("") || getPassword.length() <= 0)
            PromptManager.showToast(mContext, "密码不能为空");

        // 拼接url
        final String requestUrl = ConstantValue.USER_LOGIN_URL + "?username=" + getUsername + "&password=" + getPassword;

        new MyHttpTask<String, String>()
        {
            @Override
            protected String doInBackground(String... params)
            {
                return getSession(requestUrl);
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
                ProcessData(s);
            }
        }.executeProxy();
    }

    /**
     * 处理数据
     * @param result
     */
    private void ProcessData(String result)
    {
        UserLogin data = GsonUtils.jsonToBean(result, UserLogin.class);
        if (null != data)
        {
            LogUtils.i("json正确了" + result);
            if (data.loginSucceed)
            {
                GlobalParams.mUser = data.user;// 存放用户对象到全局方便获取
                GlobalParams.islogin = data.loginSucceed;
                PromptManager.showToast(mContext, "登陆成功");
                UIManager.getInstance().goBack();// 登陆成功就返回之前页面
            } else
                PromptManager.showToast(mContext, "登陆失败");
        } else
        {
            //PromptManager.showToast(mContext, "json出错！！！");
            LogUtils.i("json出错！！！");
            try
            {
                Thread.sleep(500);
                SubmitData1();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void setTitleClickListener()
    {
        // 返回键处理
        TitleManager.getInstance().setTitleClickListener(new TitleManager.TitleClickListener()
        {
            @Override
            public void rightButtonClick()
            {
            }

            @Override
            public void leftButtonClick()
            {
                UIManager.getInstance().goBack();
            }
        });
    }

    @Override
    protected void setTitleContent()
    {
        TitleManager.getInstance().setMiddleTextView("登陆");
        TitleManager.getInstance().setLeftButtonText("返回");
    }

    public LoginView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.LOGIN_VIEW;
    }


}
