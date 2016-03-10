package com.kerray.eshop.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * 功能:      注册页面
 * 创建人:     kerray
 * 创建时间:    2015/7/22/16:39
 */
public class RegisterView extends BaseView
{
    @ViewInject(R.id.et_register_username)
    private EditText et_register_username;
    @ViewInject(R.id.et_register_psw)
    private EditText et_register_psw;
    @ViewInject(R.id.et_register_confir_psw)
    private EditText et_register_confir_psw;
    @ViewInject(R.id.et_register_email)
    private EditText et_register_email;
    @ViewInject(R.id.et_register_phone)
    private EditText et_register_phone;

    @ViewInject(R.id.bt_register)
    private Button bt_register;

    @Override
    protected void init()
    {
        showView = (ViewGroup) View.inflate(mContext, R.layout.view_register, null);
        ViewUtils.inject(this, showView);
    }

    private void submitData()
    {
        String username = et_register_username.getText().toString().trim();
        String psw = et_register_psw.getText().toString().trim();
        String phone = et_register_phone.getText().toString().trim();
        String email = et_register_email.getText().toString().trim();

        if ("" != username && "" != psw && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(psw)
          && 0 != username.length() && 0 != psw.length())
        {
            // 拼接url,参数:用户名 + 密码 + 邮箱 + 手机号,username= &password= &phone= &email=
            String requestUrl = ConstantValue.USER_REGISTER_URL + "?username=" + username + "&password="
              + psw + "&phone=" + phone + "&email=" + email;
            new HttpUtils().send(HttpRequest.HttpMethod.GET, requestUrl, new MyRequestCallBack<String>()
            {
                public void onSuccess(ResponseInfo<String> responseInfo)
                {
                    super.onSuccess(responseInfo);
                    PromptManager.showToast(mContext,"注册成功");
                    UIManager.getInstance().goBack();
                }
            });
        }
        else
            PromptManager.showToast(mContext,"用户名和密码不能为空");
    }

    @Override
    protected void setListener()
    {
        bt_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                submitData();
            }
        });
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
        TitleManager.getInstance().setMiddleTextView("用户注册");
        TitleManager.getInstance().setLeftButtonText("返回");
    }

    public RegisterView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.REGISTER_VIEW;
    }
}
