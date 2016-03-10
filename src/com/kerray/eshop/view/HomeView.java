package com.kerray.eshop.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.adapter.HomeListAdapter;
import com.kerray.eshop.adapter.HomePagerAdapter;
import com.kerray.eshop.bean.HomeCenterCategory;
import com.kerray.eshop.util.DensityUtil;
import com.kerray.eshop.util.GsonUtils;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.util.SharePrefUtil;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.BottomManager;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能: 首页View
 * 创建人:kerray
 * 创建时间:2015/7/11/17:50
 */
public class HomeView extends BaseView
{
    /** 首页列表的listview */
    @ViewInject(R.id.lv_homelist)
    private ListView lv_homelist;

    /** 处理图片轮转 */
    private boolean isStart = true;
    private boolean isFirstIn = true;

    /** 图片容器 */
    @ViewInject(R.id.vp_home_viewpage)
    private ViewPager mViewPager;
    private int preEnabledPos = 0;
    /** 图片集合 */
    private List<ImageView> imageViewList;
    @ViewInject(R.id.ll_point_group)
    private LinearLayout llPointGroup;

    private HomeListAdapter mHomeListAdapter;

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if (isStart)
            {
                // 每隔2秒钟, 发送一条消息到主线程, 更新viewpager的界面
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                handler.sendEmptyMessageDelayed(0, 2000);
            }
        }
    };

    @Override
    protected void init()
    {
        showView = (RelativeLayout) View.inflate(mContext, R.layout.view_main_pager, null);
        ViewUtils.inject(this, showView);
        UIManager.getInstance().clear();// 清理返回键

        initScrollViewPager();
        getDataFromServer();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        // 开启线程无限自动移动
        if (isFirstIn)
        {
            handler.sendEmptyMessage(0);
            isFirstIn = false;
        }
        isStart = true;
    }



    @OnClick({ R.id.imbt_index_shake,
      R.id.imbt_index_groupbuy,
      R.id.imbt_index_promotion,
      R.id.imbt_index_recharge })
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
        case R.id.imbt_index_shake:
            //UIManager.getInstance().changeView(SnakeView.class, null);
            PromptManager.showToast(mContext, "功能暂未开放！");
            break;
        case R.id.imbt_index_groupbuy:
            PromptManager.showToast(mContext, "功能暂未开放！");
            break;
        case R.id.imbt_index_promotion:
            PromptManager.showToast(mContext, "功能暂未开放！");
            break;
        case R.id.imbt_index_recharge:
            PromptManager.showToast(mContext, "功能暂未开放！");
            break;
        }
    }

    private void initScrollViewPager()
    {
        // 推销图片集合
        imageViewList = new ArrayList<>();
        // 图片资源
        int[] imageResIDs = { R.drawable.image01, R.drawable.image02,
          R.drawable.image03, R.drawable.image04, R.drawable.image05,
          R.drawable.image06, R.drawable.image07, R.drawable.image08 };
        ImageView iv;
        View view;
        LinearLayout.LayoutParams params;
        for (int id : imageResIDs)
        {
            iv = new ImageView(mContext);
            iv.setBackgroundResource(id);
            imageViewList.add(iv);

            // 每循环一次, 添加一个点到Linearlayout中
            view = new View(mContext);
            view.setBackgroundResource(R.drawable.point_bg);
            params = new LinearLayout.LayoutParams(DensityUtil.px2dip(mContext,50), DensityUtil.px2dip(mContext,50));
            params.leftMargin = 5;
            view.setEnabled(false);
            view.setLayoutParams(params);

            llPointGroup.addView(view); // 向线性布局中添加"点"
        }

        /** 设置 viewpager适配器 */
        mViewPager.setAdapter(new HomePagerAdapter(imageViewList));
        // 图片切换监听

        int index = (Integer.MAX_VALUE / 2) - (Integer.MAX_VALUE / 2 % imageViewList.size());
        mViewPager.setCurrentItem(index);
    }

    /**
     * ①没有网络的时候提高体验，因为不缓存数据的话会变成空白，用户体验不好
     * ②能联网的话就更新数据
     * 开发过程中，首先，必须先从本地找数据
     * 如果本地有数据就显示
     * 然后再去跟服务器交互，从服务器获取数据
     * 在显示。。。
     */
    private void getDataFromServer()
    {
        String value = SharePrefUtil.getString(mContext, ConstantValue.HOME_LIST_URL, "");
        if (!TextUtils.isEmpty(value))
        {
            PromptManager.showToast(mContext, "缓存的列表");
            ProcessData(value);
        }
        new HttpUtils().send(HttpRequest.HttpMethod.GET, ConstantValue.HOME_LIST_URL, new MyRequestCallBack<String>()
        {
            public void onSuccess(ResponseInfo<String> responseInfo)
            {
                super.onSuccess(responseInfo);
                SharePrefUtil.saveString(mContext, ConstantValue.HOME_LIST_URL, responseInfo.result);
                ProcessData(responseInfo.result);
            }
        });
    }

    int submitNum = 0;

    /**
     * 处理数据加载到主页列表的adapter中
     * @param result
     */
    private void ProcessData(String result)
    {
        List<String> mHomeCenterList = new ArrayList<String>();
        HomeCenterCategory category = GsonUtils.jsonToBean(result, HomeCenterCategory.class);
        if (null != category)
        {
            if (200 == category.retcode)
            {
                List<HomeCenterCategory.HomeCategory> data = category.homeLists;
                mHomeCenterList.clear();
                for (HomeCenterCategory.HomeCategory cate : data)
                    mHomeCenterList.add(cate.列表名称);

                initHomeListAdapter(mHomeCenterList);
            }
        } else
        {
            PromptManager.showToast(mContext, "json错误！！！");
            /*try
            {
                if (submitNum <3)
                {
                    Thread.sleep(1000);
                    getDataFromServer();
                }
                submitNum++;
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }*/
        }
    }

    private void initHomeListAdapter(List<String> pHomeCenterList)
    {
            mHomeListAdapter = new HomeListAdapter(mContext, pHomeCenterList);
            lv_homelist.setAdapter(mHomeListAdapter);
        /*if (null != mHomeListAdapter)
        {
        } else
            mHomeListAdapter.notifyDataSetChanged();*/
    }

    protected void setListener()
    {
        /** 设置viewPager监听 */
        initViewPagerListener();

        lv_homelist.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                case ConstantValue.CATEGORY_VIEW:           // 全部分类
                    UIManager.getInstance().changeView(CategoryView.class, null);
                    break;
                default:
                    PromptManager.showToast(mContext, "功能暂未开放！");
                    break;
                }
            }
        });

    }

    private void initViewPagerListener()
    {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            public void onPageScrollStateChanged(int arg0)
            {
            }

            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                // 取余后的索引
                int newPosition = position % imageViewList.size();

                // 把上一个点设置为未选中
                llPointGroup.getChildAt(preEnabledPos).setEnabled(false);
                // 根据索引设置哪一个点被选中
                llPointGroup.getChildAt(newPosition).setEnabled(true);
                preEnabledPos = newPosition;
            }
        });
    }

    @Override
    public void onPause()
    {
        isStart = false;
        super.onPause();
    }


    @Override
    protected void setTitleClickListener()
    {
        BottomManager.getInstrance().setMainRadiobutton();
    }

    @Override
    protected void setTitleContent()
    {
        TitleManager.getInstance().setMiddleTextView("移动商城");
    }


    public HomeView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.HOME_VIEW;
    }

}
