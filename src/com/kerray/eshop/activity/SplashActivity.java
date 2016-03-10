package com.kerray.eshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.util.SharePrefUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @类名:SplashActivity
 * @功能: 第一个页面，用于检查更新版本
 * @创建人:kerray
 * @创建时间:2015/7/10
 */
public class SplashActivity extends Activity implements ConstantValue
{
    /** ****状态码************************** */
    protected static final int SHOW_UPDATE_DIALOG = 1000;
    protected static final int ENTER_HOME = 1001;
    protected static final int URL_ERROR = 1002;
    protected static final int NETWORK_ERROR = 1003;
    protected static final int JSON_ERROR = 1004;

    protected String description;

    /** ****注册控件************************** */
    @ViewInject(R.id.tv_splash_version)
    private TextView tv_splash_version;
    @ViewInject(R.id.tv_update_info)
    private TextView tv_update_info;

    private String apkurl;

    private SharePrefUtil mSharePrefUtil;
    private boolean first;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();

        tv_splash_version.setText("版本号：" + getVersion());

        boolean update = mSharePrefUtil.getBoolean(this, "update", true);

        if (update)
            // 检查升级
            checkUpdate();
        else
            // 自动升级已经关闭
            handler.postDelayed(new Runnable()
            {
                public void run()
                {
                    // 进入主页面
                    isFirst();
                }
            }, 2000);

        // 播放一个动画效果
        playAnimation();
    }

    private void init()
    {
        ViewUtils.inject(this);

        LogUtils.customTagPrefix = LOGUTILS_CUSTOM_TAG_PREFIX;          // 方便调试时过滤 adb logcat 输出
        LogUtils.allowI = LOG_OUTPUT;                                   // 关闭 LogUtils.i(...) 的 adb log 输出
    }

    /**
     * 接收处理消息
     */
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
            case SHOW_UPDATE_DIALOG:// 显示升级的对话框
                showUpdateDialog();
                break;
            case ENTER_HOME:
                enterHome();
                break;
            case URL_ERROR:
                enterHome();
                PromptManager.showToast(SplashActivity.this, "URL错误");
                break;
            case NETWORK_ERROR:
                enterHome();
                PromptManager.showToast(SplashActivity.this, "网络异常");
                break;
            case JSON_ERROR:
                enterHome();
                PromptManager.showToast(SplashActivity.this, "JSON解析出错");
                break;
            default:
                break;
            }
        }
    };


    private void showUpdateDialog()
    {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
          .setTitleText("提示升级")
          .setContentText(description)
          .setCancelText("下次再说")
          .setConfirmText("立刻升级")
          .showCancelButton(true)
          .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
          {
              public void onClick(SweetAlertDialog sDialog)
              {
                  sDialog.dismiss();
                  enterHome();// 进入主页面
              }
          })
          .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
          {
              @Override
              public void onClick(SweetAlertDialog sweetAlertDialog)
              {
                  sweetAlertDialog.dismiss();
                  // 下载APK,替换安装
                  if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                  {
                      new HttpUtils().download(ESHOP_URI + apkurl, Environment.getExternalStorageDirectory().getAbsolutePath() + UPDATE_CACHA_FILE,
                        new RequestCallBack<File>()
                        {
                            @Override
                            public void onSuccess(ResponseInfo<File> responseInfo)
                            {
                                installAPK(responseInfo.result);
                            }

                            @Override
                            public void onFailure(HttpException e, String s)
                            {
                                PromptManager.showToast(SplashActivity.this, "下载失败");
                                enterHome();
                            }

                            @Override
                            public void onLoading(long total, long current, boolean isUploading)
                            {
                                super.onLoading(total, current, isUploading);
                                tv_update_info.setVisibility(View.VISIBLE);
                                // 当前下载百分比
                                int progress = (int) (current * 100 / total);
                                tv_update_info.setText("下载进度：" + progress + "%");
                            }
                        });
                  } else
                  {
                      PromptManager.showToast(SplashActivity.this, "没有sdcard，请安装上在试");
                      return;
                  }
              }
          })
          .show();
    }

    /**
     * 安装APK
     * @param t 下载的文件
     */
    private void installAPK(File t)
    {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");

        startActivity(intent);
    }

    private void enterHome()
    {
        Intent intent = new Intent(this, GuideActivity.class);
        startActivity(intent);
        // 关闭当前页面
        finish();
    }

    /**
     * 检查升级
     */
    private void checkUpdate()
    {
        new HttpUtils().configCurrentHttpCacheExpiry(1000 * 2)
          .send(HttpRequest.HttpMethod.GET, UPDATE_URL, new RequestCallBack<Object>()
          {
              public void onSuccess(ResponseInfo<Object> responseInfo)
              {
                  Message mes = Message.obtain();
                  long startTime = System.currentTimeMillis();
                  try
                  {
                      // json解析
                      String result = responseInfo.result.toString();
                      LogUtils.i("App更新的json：" + result);

                      JSONObject jobj = new JSONObject(result);
                      // 获取服务器版本信息
                      String version = (String) jobj.get("version");
                      description = (String) jobj.get("description");
                      apkurl = (String) jobj.get("apkurl");

                      // 检验是否有新版本
                      if (getVersion().equals(version))
                          // 版本一致,无需更新，进入主页
                          mes.what = ENTER_HOME;
                      else
                          // 有新版本，弹出更新提示
                          mes.what = SHOW_UPDATE_DIALOG;
                  } catch (JSONException e)
                  {
                      mes.what = JSON_ERROR;
                      e.printStackTrace();
                  } finally
                  {
                      long endTime = System.currentTimeMillis();
                      // 我们花了多少时间
                      long dTime = endTime - startTime;
                      // 2000
                      if (dTime < 2000)
                      {
                          try
                          {
                              Thread.sleep(2000 - dTime);
                          } catch (InterruptedException e)
                          {
                              e.printStackTrace();
                          }
                      }
                      handler.sendMessage(mes);
                  }
              }

              @Override
              public void onFailure(HttpException e, String s)
              {
                  enterHome();// 进入主页面
                  PromptManager.showToast(SplashActivity.this, "服务器忙....../n" + s);
              }
          });

    }

    /**
     * 页面切换动画
     */
    private void playAnimation()
    {
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(2000);
    }

    /**
     * 获取 Manifest 中的当前版本号
     * @return
     */
    private String getVersion()
    {
        // 获得一个系统包管理器
        PackageManager pm = getPackageManager();
        // 获得包管理器
        try
        {
            // 获得功能清单文件
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e)
        {
            e.printStackTrace();
            // 不可能发生的异常
            return "";
        }
    }

    public void isFirst()
    {
        boolean isfirst = mSharePrefUtil.getBoolean(SplashActivity.this, "isfirst", true);
        LogUtils.i("" + isfirst);
        /**　TODO*/
        isfirst = true;
        if (isfirst)
        {
            /*Intent intent = new Intent(this, GuideActivity2.class);
            startActivity(intent);*/
            /*editor.putBoolean("isfirst", false);
            editor.commit();*/
            enterHome();
            mSharePrefUtil.saveBoolean(SplashActivity.this, "isfirst", false);
            // 关闭当前页面
            finish();
        } else
            enterHome();
    }
}
