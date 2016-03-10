package com.kerray.eshop.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.GlobalParams;
import com.kerray.eshop.R;
import com.kerray.eshop.bean.UserLogin;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.BottomManager;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.UIManager;
import com.kerray.eshop.widgets.CustomScrollView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 功能:       个人中心
 * 创建人:      kerray
 * 创建时间:    2015/7/19/22:49
 */
public class PersonelView extends BaseView
{
    @ViewInject(R.id.personal_background_image)
    private ImageView mBackgroundImageView = null;
    @ViewInject(R.id.personal_scrollView)
    private CustomScrollView mScrollView = null;
    @ViewInject(R.id.bt_personal_login_button)
    private Button bt_personal_login_button;
    @ViewInject(R.id.bt_personal_exit)
    private Button bt_personal_exit;

    @ViewInject(R.id.bt_personal_title)
    private TextView tv_personal_title;
    @ViewInject(R.id.tv_personal_money)
    private TextView tv_personal_money;
    @ViewInject(R.id.tv_personal_phone)
    private TextView tv_personal_phone;
    @ViewInject(R.id.tv_personal_email)
    private TextView tv_personal_email;
    @ViewInject(R.id.tv_personal_address)
    private TextView tv_personal_address;

    @Override
    protected void init()
    {
        showView = (ViewGroup) View.inflate(mContext, R.layout.view_personal, null);
        ViewUtils.inject(this, showView);
        UIManager.getInstance().clear();// 清理返回键
    }

    @Override
    public void onResume()
    {
        changeShowLogin();
        super.onResume();
    }


    private void changeShowLogin()
    {
        boolean islogin = getIslogin();
        // 判断是否登陆
        if (GlobalParams.islogin)
        {
            UserLogin.User mUser = GlobalParams.mUser;

            bt_personal_login_button.setVisibility(View.GONE);
            tv_personal_title.setVisibility(View.VISIBLE);
            bt_personal_exit.setVisibility(View.VISIBLE);

            tv_personal_title.setText("欢迎登陆:" + mUser.用户名);
            tv_personal_money.setText(PromptManager.nullToZero(mUser.余额));
            tv_personal_phone.setText(PromptManager.nullToZero(mUser.手机号));
            tv_personal_email.setText(PromptManager.nullToZero(mUser.邮箱));
            tv_personal_address.setText(PromptManager.nullToZero(mUser.地址));
        } else
        {
            bt_personal_login_button.setVisibility(View.VISIBLE);
            tv_personal_title.setVisibility(View.GONE);
            bt_personal_exit.setVisibility(View.GONE);

            tv_personal_money.setText("");
            tv_personal_phone.setText("");
            tv_personal_email.setText("");
            tv_personal_address.setText("");
        }
    }

    @Override
    protected void setListener()
    {
        mScrollView.setImageView(mBackgroundImageView);
    }

    @OnClick({ R.id.bt_personal_exit,
      R.id.bt_personal_login_button })
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.bt_personal_login_button:     // 登陆
            UIManager.getInstance().changeView(LoginView.class, null);
            break;
        case R.id.bt_personal_exit:             // 注销
            logout();
            break;
        }
    }

    /**
     * 退出登录
     */
    public void logout()
    {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
          .setTitleText("确认退出当前用户？")
          .setCancelText("取消")
          .setConfirmText("确定")
          .showCancelButton(true)
          .setCancelClickListener(null)
          .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
          {
              public void onClick(SweetAlertDialog sweetAlertDialog)
              {
                  sweetAlertDialog.dismiss();
                  // 拼接url
                  final String requestUrl = ConstantValue.USER_LOGOUT_URL;
                  // 发送请求
                  HttpUtilsSend(requestUrl);
              }
          })
          .show();
    }


    /**
     * 封装的httpGet请求方法
     * @param requestUrl Url地址
     */
    public void HttpUtilsSend(String requestUrl)
    {
        // 退出登录
        new HttpUtils().send(HttpRequest.HttpMethod.GET, requestUrl, new MyRequestCallBack<String>()
        {
            public void onSuccess(ResponseInfo<String> responseInfo)
            {
                super.onSuccess(responseInfo);
                GlobalParams.JSESSIONID = "";
                GlobalParams.mUser = null;
                GlobalParams.islogin = false;
                changeShowLogin();
            }
        });
    }

    @Override
    protected void setTitleClickListener()
    {
        BottomManager.getInstrance().setPersonalRadiobutton();
    }

    @Override
    protected void setTitleContent()
    {
        TitleManager.getInstance().setIndexTextView("个人中心");
    }

    public PersonelView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.PERSONEL_VIEW;
    }
}
