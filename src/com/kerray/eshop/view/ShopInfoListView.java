package com.kerray.eshop.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.PullToRefresh.ILoadingLayout;
import com.github.PullToRefresh.PullToRefreshBase;
import com.github.PullToRefresh.PullToRefreshListView;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.adapter.MyBaseListAdapter;
import com.kerray.eshop.bean.ShopInfoList;
import com.kerray.eshop.util.GsonUtils;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.util.xUtilsImageLoader;
import com.kerray.eshop.view.manager.BaseView;
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
 * 功能:       商品列表
 * 创建人:     kerray
 * 创建时间:   2015/7/17/21:19
 */
public class ShopInfoListView extends BaseView
{
    private final String DEFAULT = "id";             // 默认根据id排序
    private final String REPUTATION_NUM = "reputationNum";  // 评论数排序
    private final String PRICE = "price";          // 价格

    private String saveColumnName = DEFAULT;                // 保存排序的列名

    @ViewInject(R.id.rl_show_shoplist)
    private RelativeLayout rl_show_shoplist;                // 展示商品列表的父布局
    //@ViewInject(R.id.ll_linRank)
    //private LinearLayout ll_linRank;                        // 排序的父布局
    @ViewInject(R.id.tv_no_shoplist)
    private TextView tv_no_shoplist;                        // 显示没有商品列表
    //----------------- 排序按钮 -----------------
    @ViewInject(R.id.tv_shopinfo_list_rankSale)
    private TextView tv_shopinfo_list_rankSale;
    @ViewInject(R.id.tv_shopinfo_list_rankPrice)
    private TextView tv_shopinfo_list_rankPrice;
    @ViewInject(R.id.tv_shopinfo_list_rankGood)
    private TextView tv_shopinfo_list_rankGood;
    //----------------- 展示商品 -----------------
    @ViewInject(R.id.lv_pull_refresh_list)
    private PullToRefreshListView mPullRefreshListView;

    private ShopInfoAdapter mShopInfoAdapter;               // ListView适配器
    private xUtilsImageLoader mxUtilsImageLoader;           // 自定义的xUtilsBitmap

    private static Long ShopType_Id;                        // 商品分类页面传递过来的商品id
    /**
     * 存放整个商品列表
     */
    private List<ShopInfoList.Shopinfo> mShopinfoItem;
    /**
     * 存放每个商品的图片地址
     */
    private String[] imagePath;
    /**
     * 是否按升序
     */
    boolean orderBy = true;

    @Override
    protected void init()
    {
        showView = (ViewGroup) View.inflate(mContext, R.layout.view_shopinfo_list, null);
        ViewUtils.inject(this, showView);
        mxUtilsImageLoader = new xUtilsImageLoader(mContext);                           // 自定义的xUtils图片解析，增加了渐变动画
    }

    /**
     * 进来的时候设置默认排序选中，再加载数据
     */
    public void onResume()
    {
        tv_shopinfo_list_rankSale.setTextColor(Color.RED);
        getDataFromServer();
        super.onResume();
    }

    /**
     * 返回后清除排序文本的颜色
     */
    public void onPause()
    {
        initTextColor();
        super.onPause();
    }

    @Override
    protected void setListener()
    {
        ILoadingLayout startLabels = mPullRefreshListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在载入...");// 刷新时
        startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

        Listener();
    }

    /**
     * 下拉刷新控件的监听
     */
    private void Listener()
    {
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView)
            {
                String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
                  DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // Do work to refresh the list here.
                // 重新获取数据,刷新list
                getDataFromServerOrder(saveColumnName, false);
            }

        });

        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ViewHolder viewHolder = new ViewHolder();
                if (null == view)
                {
                    ViewUtils.inject(viewHolder, view);
                    //使用tag存储数据
                    view.setTag(viewHolder);
                } else
                    viewHolder = (ViewHolder) view.getTag();

                Bundle bundle = new Bundle();
                bundle.putString("shopinfo_id", viewHolder.tv_hide_id.getText().toString());
                UIManager.getInstance().changeView(ProductDetailView.class, bundle);
            }
        });

        // 添加滑动到底部的监听
        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override
            public void onLastItemVisible()
            {
                PromptManager.showToast(mContext, "没有数据了!");
            }
        });
    }


    @OnClick({ R.id.tv_shopinfo_list_rankSale,
      R.id.tv_shopinfo_list_rankPrice,
      R.id.tv_shopinfo_list_rankGood, })
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.tv_shopinfo_list_rankSale://销量
            initTextColor();
            tv_shopinfo_list_rankSale.setTextColor(Color.RED);
            getDataFromServerOrder(saveColumnName = DEFAULT, true);
            break;
        case R.id.tv_shopinfo_list_rankPrice: //价格
            initTextColor();
            tv_shopinfo_list_rankPrice.setTextColor(Color.RED);
            getDataFromServerOrder(saveColumnName = PRICE, true);
            break;
        case R.id.tv_shopinfo_list_rankGood://评论
            initTextColor();
            tv_shopinfo_list_rankGood.setTextColor(Color.RED);
            getDataFromServerOrder(saveColumnName = REPUTATION_NUM, true);
            break;
        }
    }


    /**
     * 把所有的文字颜色设置为黑色
     */
    private void initTextColor()
    {
        tv_shopinfo_list_rankSale.setTextColor(Color.BLACK);
        tv_shopinfo_list_rankGood.setTextColor(Color.BLACK);
        tv_shopinfo_list_rankPrice.setTextColor(Color.BLACK);
    }

    /** 从服务端获取数据 */
    private void getDataFromServer()
    {
        ShopType_Id = mBundle.getLong("ShopType_Id");

        final String requestUrl = ConstantValue.SHOPINFO_LIST_URL + "?id=" + ShopType_Id;
        /*String value = SharePrefUtil.getString(mContext, requestUrl, "");
        if (!TextUtils.isEmpty(value))
        {
            PromptManager.showToast(mContext, "缓存的商品列表");
            //ProcessData(value);
        }*/
        new HttpUtils().send(HttpRequest.HttpMethod.GET,
          requestUrl, new MyRequestCallBack<String>()
          {
              public void onSuccess(ResponseInfo<String> responseInfo)
              {
                  //PromptManager.showToast(mContext, "联网更新的商品列表");
                  //SharePrefUtil.saveString(mContext, requestUrl, responseInfo.result);
                  super.onSuccess(responseInfo);
                  ProcessData(responseInfo.result);
              }
          });
    }

    /**
     * 从服务端更新数据进行排序
     * @param orderColumn  排序的列名
     * @param isChangOrder 是否改变排序
     */
    private void getDataFromServerOrder(String orderColumn, boolean isChangOrder)
    {
        if (isChangOrder)
            orderBy = orderBy ? false : true; // 每次进来都会修改排序方式

        final String requestUrl = ConstantValue.SHOPINFO_INFOID_ORDER_URL + "?id=" + ShopType_Id + "&orderColumn=" + orderColumn + "&Asc=" + orderBy;
        //HttpUtils对于GET请求采用了LRU缓存处理，默认60秒内提交返回上次成功的结果。
        new HttpUtils().send(HttpRequest.HttpMethod.GET, requestUrl, new MyRequestCallBack<String>()
        {
            public void onSuccess(ResponseInfo<String> responseInfo)
            {
                super.onSuccess(responseInfo);

                ProcessData(responseInfo.result);
            }
        });
    }


    /**
     * 处理返回的json到实体类中，放到内存中用于更新
     * @param result json数据
     */
    private void ProcessData(String result)
    {
        mShopinfoItem = new ArrayList<>();
        ShopInfoList shopInfoList = GsonUtils.jsonToBean(result, ShopInfoList.class);
        if (null != shopInfoList)
        {
            // 如果返回数据为0则不加载数据
            if (0 == shopInfoList.count)
            {   // 修改展示商品列表和没有商品列表的显示和隐藏
                rl_show_shoplist.setVisibility(View.GONE);
                tv_no_shoplist.setVisibility(View.VISIBLE);
            } else
            {
                rl_show_shoplist.setVisibility(View.VISIBLE);
                tv_no_shoplist.setVisibility(View.GONE);
                if (200 == shopInfoList.retcode)
                {
                    mShopinfoItem.clear();
                    mShopinfoItem = shopInfoList.shopInfos;
                }

                // 判断adapter是否已经存在，是则更新数据，否则new
                if (null == mShopInfoAdapter)
                {
                    ListView actualListView = mPullRefreshListView.getRefreshableView();
                    mShopInfoAdapter = new ShopInfoAdapter(mContext, mShopinfoItem);
                    actualListView.setAdapter(mShopInfoAdapter);
                } else
                {
                    // 更新adpater中的list
                    mShopInfoAdapter.mList = mShopinfoItem;
                    mShopInfoAdapter.notifyDataSetChanged();
                }
                new GetDataTask().execute();        // 发送关闭下拉刷新的通知
            }
        } else
        {
            //PromptManager.showToast(mContext, "json出错！！！");
            try
            {
                Thread.sleep(500);
                getDataFromServer();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * 关闭下拉刷新的线程，不清楚为什么用Hander没用
     */
    private class GetDataTask extends AsyncTask<Void, Void, String[]>
    {
        @Override
        protected String[] doInBackground(Void... params)
        {
            try
            {
                Thread.sleep(500);
            } catch (InterruptedException e)
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            mPullRefreshListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }


    @Override
    protected void setTitleClickListener()
    {
        TitleManager.getInstance().setTitleClickListener(new TitleManager.TitleClickListener()
        {
            @Override
            public void rightButtonClick()
            {
                PromptManager.showToast(mContext, "右边按钮");
            }

            @Override
            public void leftButtonClick()
            {
                UIManager.getInstance().goBack();
            }
        });
    }


    class ShopInfoAdapter extends MyBaseListAdapter
    {
        public ShopInfoAdapter(Context pContext, List plist)
        {
            super(pContext, plist);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view = convertView;
            ViewHolder viewHolder;
            if (view != null && view instanceof LinearLayout)
                viewHolder = (ViewHolder) view.getTag();
            else
            {
                viewHolder = new ViewHolder();
                view = View.inflate(mContext, R.layout.view_shopinfo_list_item, null);
                ViewUtils.inject(viewHolder, view);
                view.setTag(viewHolder);
            }

            ShopInfoList.Shopinfo info = mShopinfoItem.get(position);

            // 图片地址为空的处理
            if (null == info.图片地址)
                mxUtilsImageLoader.display(viewHolder.imgIcon, "assets/ic_jd.png");
            else
            {
                imagePath = info.图片地址.split("[,]");
                mxUtilsImageLoader.display(viewHolder.imgIcon, ConstantValue.ESHOP_URI + imagePath[0]);
            }
            // 为 null 时的处理
            viewHolder.tv_hide_id.setText("" + info.id);    // 存放商品id
            viewHolder.name.setText(PromptManager.nullToZero(info.商品名称));
            viewHolder.price.setText("¥ " + PromptManager.nullToZero(info.价格));
            viewHolder.marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);            // 文字中间加横线
            viewHolder.marketPrice.setText(PromptManager.nullToZero(info.市场价));
            viewHolder.commetNumber.setText(info.评论 + "人评论");
            return view;
        }
    }

    class ViewHolder
    {
        @ViewInject(R.id.tv_hide_id)
        TextView tv_hide_id;
        @ViewInject(R.id.iv_shopinfo_list_item_image)
        ImageView imgIcon;
        @ViewInject(R.id.tv_shopinfo_list_item_title)
        TextView name;
        @ViewInject(R.id.tv_shopinfo_list_item_price)
        TextView price;
        @ViewInject(R.id.tv_shopinfo_list_item_marketPrice)
        TextView marketPrice;
        @ViewInject(R.id.tv_shopinfo_list_item_commentNum)
        TextView commetNumber;
    }

    @Override
    protected void setTitleContent()
    {
        TitleManager.getInstance().setMiddleTextView(mBundle.getString("ShopType_Name"));
        TitleManager.getInstance().setLeftButtonText("返回");
    }


    public ShopInfoListView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.SHOPINFO_LIST_VIEW;
    }
}
