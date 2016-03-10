package com.kerray.eshop;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.view.CategoryView1;
import com.kerray.eshop.view.manager.BottomManager;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.File;

public class MainActivity extends Activity
{

    private TitleManager titleManager;
    private BottomManager bottomManager;
    @ViewInject(R.id.rl_middle)
    private RelativeLayout rl_middle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        LogUtils.customTagPrefix = ConstantValue.LOGUTILS_CUSTOM_TAG_PREFIX;

        // 设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        GlobalParams.WIN_WIDTH = metrics.widthPixels;
        GlobalParams.WIN_HEIGHT = metrics.heightPixels;
        GlobalParams.CONTEXT = this;
        //GlobalParams.JSESSIONID = SharePrefUtil.getString(this, "JSESSIONID", "");

        init();

        //createFile();               // 创建缓存文件夹,xUtils已经主动创建目录在sd卡 Android/data/package-name
    }


    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
        }
        /*SystemStatusManager tintManager = new SystemStatusManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(0);//状态栏无背景*/
    }

    /**
     * 创建用于缓存的文件夹
     */
    private void createFile()
    {
        //检查手机上是否有外部存储卡
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (!sdCardExist)//如果不存在SD卡，进行提示
            PromptManager.showToast(MainActivity.this, "请插入SD存储卡");

        else//如果存在SD卡，判断文件夹目录是否存在
        {
            File dirFirstFile = new File(ConstantValue.CACHE_FILE_PATH);//新建目录
            if (!dirFirstFile.exists())
                //判断文件夹目录是否存在
                dirFirstFile.mkdir();//如果不存在则创建
        }
    }

    private void init()
    {
        titleManager = TitleManager.getInstance();
        bottomManager = BottomManager.getInstrance();

        titleManager.init(this);
        bottomManager.init(this);

        initUIManager();
    }

    private void initUIManager()
    {
        UIManager.getInstance().addObserver(titleManager);
        UIManager.getInstance().addObserver(bottomManager);
        UIManager.getInstance().setMiddleContainer(rl_middle);

        UIManager.getInstance().changeView(CategoryView1.class, null);// 设置初始显示 的位置
    }

    /**
     * 点击几次关闭程序
     */
    private long[] mHits = new long[2];

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            boolean result = UIManager.getInstance().goBack();
            if (!result)
            {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 1000))
                {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    // finish();
                } else
                    PromptManager.showToast(this, "亲，再按一次退出");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
