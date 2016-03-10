package com.kerray.eshop.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.util.ShakeListenerUtils;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.Random;

/**
 * 类名: ${CLASS_NAME}
 * 功能:
 * 创建人:kerray
 * 创建时间:2015/7/30/19:17
 */
public class SnakeView extends BaseView
{
    private Vibrator vibrator;//震动
    /** 摇之前 遥之后 ,隐藏的 */
    @ViewInject(R.id.iv_shake_middle)
    private ImageView iv_shake_gmiddle;
    @ViewInject(R.id.iv_shake_tit)
    private ImageView iv_shake_gtit;
    @ViewInject(R.id.iv_shake_noth)
    private ImageView iv_shake_gnoth;
    /** 监听 */
    private ShakeListenerUtils shakeListener;

    private int icon[] = { R.drawable.image_left, R.drawable.image_middle, R.drawable.image_right };

    private int icoSotp[] = { R.drawable.lottery_result, R.drawable.lottery_head_2, R.drawable.lottery_nothing, R.drawable.lottery_something };

    private int index = 0;

    private int randomC = 0;

    private Random random;

    @Override
    protected void init()
    {
        showView = (ViewGroup) View.inflate(mContext, R.layout.view_shake, null);
        ViewUtils.inject(this, showView);

    }

    @OnClick(R.id.bt_shake_back)
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.bt_shake_back:
            UIManager.getInstance().goBack();
            break;
        }
    }

    /** 重力感应 */
    private ShakeListenerUtils.OnShakeListener onShake = new ShakeListenerUtils.OnShakeListener()
    {
        public void onShake()
        {
            iv_shake_gnoth.setVisibility(View.GONE);

            startVibrator();
            shakeListener.stop();

            handler.sendEmptyMessageDelayed(1, 200);
            handler.sendEmptyMessageDelayed(2, 2000);

            randomC = random.nextInt(6);
        }
    };

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 1)
            {
                if (index < icon.length - 1)
                    index++;
                else
                    index = 0;

                iv_shake_gmiddle.setBackgroundResource(icon[index]);
                handler.sendEmptyMessageDelayed(1, 200);
            } else
            {
                iv_shake_gmiddle.setBackgroundResource(icoSotp[0]);
                iv_shake_gtit.setBackgroundResource(icoSotp[1]);
                handler.removeMessages(1);
                shakeListener.start();
                if (randomC == 5)
                {
                    iv_shake_gnoth.setBackgroundResource(icoSotp[3]);
                    iv_shake_gnoth.setVisibility(View.VISIBLE);
                } else
                {
                    iv_shake_gnoth.setBackgroundResource(icoSotp[2]);
                    iv_shake_gnoth.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    /**
     * 播放振动效果
     */
    public void startVibrator()
    {
        vibrator.vibrate(new long[] { 500, 300, 500, 300 }, -1);
    }

    @Override
    protected void setListener()
    {
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        random = new Random();

        shakeListener = new ShakeListenerUtils(mContext);
        shakeListener.setOnShake(onShake);
    }

    @Override
    protected void setTitleClickListener()
    {

    }

    @Override
    protected void setTitleContent()
    {

    }

    public SnakeView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.HOME_SHAKE;
    }

}
