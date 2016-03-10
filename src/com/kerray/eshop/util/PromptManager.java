package com.kerray.eshop.util;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.kerray.eshop.R;


/**
 * 提示信息的管理
 */

public class PromptManager
{
    private static ProgressDialog dialog;

    /**
     * 空字符串转换为0
     * @param s
     * @return
     */
    public static String nullToZero(String s)
    {
        return (null == s) ? 0 + "" : s;
    }

    public static String nullToZero(Double d)
    {
        return (null == d) ? 0 + "" : d + "";
    }


    public static void showProgressDialog(Context context)
    {
        dialog = new ProgressDialog(context);
        dialog.setIcon(R.drawable.icon);
        dialog.setTitle(R.string.app_name);

        dialog.setMessage("请等候，数据加载中……");
        dialog.show();
    }

    public static void closeProgressDialog()
    {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    /**
     * 当判断当前手机没有网络时使用
     * @param context
     */
    public static void showNoNetWork(final Context context)
    {
        /*Builder builder = new Builder(context);
        builder.setIcon(R.drawable.icon)//
          .setTitle(R.string.app_name)//
          .setMessage("当前无网络").setPositiveButton("设置", new OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // 跳转到系统的网络设置界面
                Intent intent = new Intent();
                intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
                context.startActivity(intent);

            }
        }).setNegativeButton("知道了", null).show();*/

        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
          .setTitleText("当前没有网络")
          .show();
    }

    /**
     * 退出系统
     * @param context
     */
    public static void showExitSystem(Context context)
    {
        Builder builder = new Builder(context);
        builder.setIcon(R.drawable.icon)//
          .setTitle(R.string.app_name)//
          .setMessage("是否退出应用").setPositiveButton("确定", new OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                android.os.Process.killProcess(android.os.Process.myPid());
                // 多个Activity——懒人听书：没有彻底退出应用
                // 将所有用到的Activity都存起来，获取全部，干掉
                // BaseActivity——onCreated——放到容器中
            }
        })//
          .setNegativeButton("取消", null)//
          .show();

    }

    public static void SweetAlertDialog(final Context pContext, String msg)
    {
        new SweetAlertDialog(pContext, SweetAlertDialog.WARNING_TYPE)
          .setTitleText("你确定吗?")
          .setContentText("这个文件将无法恢复!")
          .setCancelText("No,cancel plx!")
          .setConfirmText("是的,删除它!")
          .showCancelButton(true)
          .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener()
          {
              @Override
              public void onClick(SweetAlertDialog sDialog)
              {
                  // reuse previous dialog instance, keep widget user state, reset them if you need
                  sDialog.setTitleText("取消!")
                    .setContentText("文件是安全的:)")
                    .setConfirmText("确定")
                    .showCancelButton(false)
                    .setCancelClickListener(null)
                    .setConfirmClickListener(null)
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                  // or you can new a SweetAlertDialog to show
                  sDialog.dismiss();
                  new SweetAlertDialog(pContext, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("取消!")
                    .setContentText("文件是安全的 :)")
                    .setConfirmText("确定")
                    .show();
              }
          })
          .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
          {
              @Override
              public void onClick(SweetAlertDialog sDialog)
              {
                  sDialog.setTitleText("取消!")
                    .setContentText("你的文件已被删除!")
                    .setConfirmText("确定")
                    .showCancelButton(false)
                    .setCancelClickListener(null)
                    .setConfirmClickListener(null)
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
              }
          })
          .show();
    }

    public static void showToast(Context context, String msg)
    {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int msgResId)
    {
        Toast.makeText(context, msgResId, Toast.LENGTH_SHORT).show();
    }

    // 当测试阶段时true
    private static final boolean isShow = true;

    /**
     * 测试用 在正式投入市场：删
     * @param context
     * @param msg
     */
    public static void showToastTest(Context context, String msg)
    {
        if (isShow)
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
