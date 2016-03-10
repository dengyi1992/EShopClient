package com.kerray.eshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import com.github.viewpagerindicator.UnderlinePageIndicator;
import com.kerray.eshop.MainActivity;
import com.kerray.eshop.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * @类名:com.kerray.EShopClient.activity
 * @功能: 引导页
 * @参数:
 * @创建人:kerray
 * @创建时间:2015/7/10
 */
public class GuideActivity extends FragmentActivity
{
    /**
     * 资源文件，数目是动态获取，不用维护
     */
    private static final int[] images = { R.drawable.welcome_01, R.drawable.welcome_02, R.drawable.welcome_03, R.drawable.welcome_04 };

    /**
     * 动态获取文件数量
     */
    private int mCount = images.length;
    private static int PageUnm = 0;

    @ViewInject(R.id.bt_guide_go_main)
    private Button bt_guide_go_main;
    @ViewInject(R.id.pager)
    private ViewPager mPager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        ViewUtils.inject(this);

        //viewpager 的adapter
        FragmentPagerAdapter adapter = new SampleCirclesDefault(getSupportFragmentManager());
        //mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(adapter);

        //实例化TabPageIndicator然后设置ViewPager与之关联
        UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        //如果我们要对ViewPager设置监听，用indicator设置就可以
        indicator.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    @OnClick(R.id.bt_guide_go_main)
    public void onClick(View v)
    {
        startActivity(new Intent(GuideActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Viewpager 适配器
     */
    class SampleCirclesDefault extends FragmentPagerAdapter
    {
        public SampleCirclesDefault(FragmentManager fm)
        {
            super(fm);
        }

        public Fragment getItem(int position)
        {
            Fragment fragment = new GuideItemFragment();
            Bundle b = new Bundle();
            b.putInt("images", images[position]);
            fragment.setArguments(b);
            return fragment;
        }

        @Override
        public int getCount()
        {
            return mCount;
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener
    {
        //This method will be invoked when the current page is scrolled, either as part of a programmatically
        //initiated smooth scroll or a user initiated touch scroll.
        //当页面在滑动的时候会调用这个方法，在滑动被停止之前，此方法会一直得到调用
        //position 当前页面的位置， positionOffset 页面偏移量0到1，positionOffsetPixels 当前页面偏移的像素位置
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            if (PageUnm != position)
                LogUtils.i("当前页面的位置" + position);
            PageUnm = position;

            if (mCount == position + 1)
                bt_guide_go_main.setVisibility(View.VISIBLE);
            else
                bt_guide_go_main.setVisibility(View.GONE);

            //LogUtils.i("页面偏移量" + positionOffset);
            //LogUtils.i("页面偏移的像素位置" + positionOffsetPixels);
        }

        //This method will be invoked when a new page becomes selected. Animation is not necessarily complete.
        //页面跳转完成后调用，position是当前选中页面的位置
        public void onPageSelected(int position)
        {
        }

        //Called when the scroll state changes. Useful for discovering when the user begins dragging,
        //when the pager is automatically settling to the current page, or when it is fully stopped/idle.
        //状态改变的时候会调用，有三种状态（0，1，2）。state ==1 正在滑动，state==2 滑动完毕了，state==0 什么都没做。
        public void onPageScrollStateChanged(int state)
        {
        }
    }
}

