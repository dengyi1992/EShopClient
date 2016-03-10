package com.kerray.eshop.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.GlobalParams;
import com.kerray.eshop.R;
import com.kerray.eshop.bean.ShopInfoList;
import com.kerray.eshop.net.NetWorkUtil;
import com.kerray.eshop.util.DensityUtil;
import com.kerray.eshop.util.GsonUtils;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.util.xUtilsImageLoader;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.TitleManager.TitleClickListener;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能:        商品详情页面
 * 创建人:      kerray
 * 创建时间:    2015-7-18 20:28:28
 */
public class ProductDetailView extends BaseView
{
    @ViewInject(R.id.tv_productNameValue)
    private TextView name;                      // 商品名称
    @ViewInject(R.id.tv_productIdValue)
    private TextView productId;                 // 编号
    @ViewInject(R.id.tv_originalPriceValue)
    private TextView originalprice;             // 市场价
    @ViewInject(R.id.rb_product_grade)
    private RatingBar rb_product_grade;         // 商品评分
    @ViewInject(R.id.tv_priceValue)
    private TextView price;                     // 售价
    @ViewInject(R.id.tv_colorValue)
    private TextView productColor;              // 颜色
    @ViewInject(R.id.tv_sizeValue)
    private TextView productSize;               // 尺寸
    @ViewInject(R.id.tv_prodIsStock)
    private TextView prodIsStock;               // 库存
    @ViewInject(R.id.tv_productCommentNum)
    private TextView commentNum;                // 评价数
    @ViewInject(R.id.tv_description)
    private TextView tv_description;            // 商品描述

    @ViewInject(R.id.tv_prodToCollect)
    private TextView prodToCollece;             // 收藏按钮
    @ViewInject(R.id.tv_productInfoIsNull)
    private TextView tv_productInfoIsNull;      // 显示商品详情信息为空！

    @ViewInject(R.id.prod_property)
    private LinearLayout prod_property;

    //********************商品获取失败时要隐藏的父布局***********************
    @ViewInject(R.id.sv_product_view)
    private ScrollView sv_product_view;
    @ViewInject(R.id.ll_product_bottom)
    private LinearLayout ll_product_bottom;

    private xUtilsImageLoader mxUtilsImageLoader;// 自定义的xUtilsBitmap

    /**
     * 存放整个商品列表
     */
    private List<ShopInfoList.Shopinfo> mShopinfoItem;

    /**
     * 商品图片地址
     */
    private static String[] mImages;

    /**
     * 横向滑动的图片
     */
    @ViewInject(R.id.ll_gallery)
    private LinearLayout mLinearLayout;
    private LayoutInflater mInflater;

    public ProductDetailView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    protected void init()
    {
        showView = (ViewGroup) View.inflate(mContext, R.layout.view_product_detail, null);
        ViewUtils.inject(this, showView);
        mxUtilsImageLoader = new xUtilsImageLoader(mContext);
        originalprice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);                 // 文字中间加横线

        //initpopupwindow();
    }

    @Override
    public void onResume()
    {
        getDataFromServer();
        super.onResume();
    }

    /** 从服务端获取数据 */
    private void getDataFromServer()
    {
        /*String value = SharePrefUtil.getString(mContext, requestUrl, "");
        if (!TextUtils.isEmpty(value))
        {
            PromptManager.showToast(mContext, "缓存的商品");
            ProcessData(value);
        }*/
        final String requestUrl = ConstantValue.SHOPINFO_INFOID_URL + "?id=" + mBundle.getString("shopinfo_id");
        new HttpUtils().send(HttpRequest.HttpMethod.GET, requestUrl,
          new RequestCallBack<String>()
          {
              @Override
              public void onSuccess(ResponseInfo<String> responseInfo)
              {
                  tv_productInfoIsNull.setVisibility(View.GONE);
                  sv_product_view.setVisibility(View.VISIBLE);
                  ll_product_bottom.setVisibility(View.VISIBLE);
                  ProcessData(responseInfo.result);
              }

              @Override
              public void onFailure(HttpException e, String s)
              {
                  PromptManager.showToast(mContext, "获取商品失败！");
                  tv_productInfoIsNull.setVisibility(View.VISIBLE);
                  sv_product_view.setVisibility(View.GONE);
                  ll_product_bottom.setVisibility(View.GONE);
              }
          });
    }

    int submitNum = 0;

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
            if (200 == shopInfoList.retcode)
            {
                mShopinfoItem.clear();
                mShopinfoItem = shopInfoList.shopInfos;
            }
            initTextView();
        } else
        {
            PromptManager.showToast(mContext, "json错误！！！");
            try
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
            }
        }
    }

    private void initTextView()
    {
        ShopInfoList.Shopinfo shopinfo = mShopinfoItem.get(0);
        name.setText(shopinfo.商品名称);
        productId.setText("商品编号：" + shopinfo.id);
        originalprice.setText("¥ " + PromptManager.nullToZero(shopinfo.市场价));
        price.setText(PromptManager.nullToZero(shopinfo.价格));
        prodIsStock.setText("库存 (" + shopinfo.数量 + ")");
        commentNum.setText("评论 (" + shopinfo.评论 + ")");
        rb_product_grade.setRating(shopinfo.评分);
        tv_description.setText("商品描述: " + shopinfo.描述);

        // 把不为空的图片路径截取出来放到 mImages 中
        if (null != shopinfo.图片地址)
        {
            mImages = shopinfo.图片地址.split("[,]");
            initGallery();
        }
    }


    /**
     * 初始化图片展示控件
     */
    private void initGallery()
    {
        ImageView img;
        mInflater = LayoutInflater.from(mContext);
        mLinearLayout.removeAllViews();         // 清空View
        for (int i = 0; i < mImages.length; i++)
        {
            View view = mInflater.inflate(R.layout.view_product_gallery_item, mLinearLayout, false);
            img = (ImageView) view.findViewById(R.id.iv_gallery_item);
            mxUtilsImageLoader.display(img, ConstantValue.ESHOP_URI + mImages[i]);
            mLinearLayout.addView(view);
        }
    }

    @Override
    protected void setListener()
    {
        prodToCollece.setOnClickListener(this);
        productColor.setOnClickListener(this);
        productSize.setOnClickListener(this);
    }

    /**
     * 以下是popuwindow~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */
    private String[] colors = new String[] { "红色", "蓝色", "绿色" };
    private String[] daxiao = new String[] { "L", "K", "M" };
    private PopupWindow popupWindow1;
    private PopupWindow popupWindow2;
    private ListView listView;
    private ListView listView2;

    @OnClick({ R.id.tv_product_addCart,
      R.id.tv_product_cart })
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.tv_product_addCart: // 加入购物车
            addProductCart();
            break;
        case R.id.tv_prodToCollect: // 收藏
            PromptManager.showToast(mContext, "收藏");
            break;
        case R.id.tv_colorValue:
            popupWindow1.showAsDropDown(productColor, 5, -5);
            break;
        case R.id.tv_sizeValue:
            popupWindow2.showAsDropDown(productSize, 5, -5);
            break;
        case R.id.tv_product_cart:
            UIManager.getInstance().changeView(CartView.class, null);       // 跳转到购物车
            break;
        }
    }

    private void addProductCart()
    {
        boolean islogin = getIslogin();
        // 用户登陆成功才能添加到购物车
        if (GlobalParams.islogin)
        {
            // 拼接url
            final String requestUrl = ConstantValue.ADD_CART_URL + "?user_id=" + GlobalParams.mUser.id + "&shopingo_id=" + mShopinfoItem.get(0).id;
            new HttpUtils().send(HttpRequest.HttpMethod.GET, requestUrl, new RequestCallBack<String>()
            {
                public void onSuccess(ResponseInfo<String> responseInfo)
                {
                    UIManager.getInstance().changeView(CartView.class, null);
                }

                public void onFailure(HttpException e, String s)
                {
                    // TODO 进度条会变得很大，有时间要解决掉
                    if (NetWorkUtil.checkNetWork(mContext))
                        PromptManager.showToast(mContext, "服务器忙！请稍后重试......");
                    else
                        PromptManager.showNoNetWork(mContext);
                }
            });
        } else
        {
            PromptManager.showToast(mContext, "请登陆...");
            UIManager.getInstance().changeView(LoginView.class, null);
        }
    }

    @Override
    protected void setTitleClickListener()
    {
        TitleManager.getInstance().setTitleClickListener(
          new TitleClickListener()
          {
              @Override
              public void rightButtonClick()
              {
                  PromptManager.showToast(mContext, "加入购物车被点击了");
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
        TitleManager.getInstance().setMiddleTextView("商品详情");
        TitleManager.getInstance().setLeftButtonText("返回");
    }


    private void dismissPopupWindow(PopupWindow popupWindow)
    {
        if (popupWindow != null)
        {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private void initpopupwindow()
    {
        listView = new ListView(mContext);
        listView.setVerticalScrollBarEnabled(false);// 隐藏滚动条
        listView.setDividerHeight(0);// 没有分割线
        listView.setDivider(null);
        listView.setOnItemClickListener(new OnItemClickListener()
        {
            // productColor = (TextView) findViewById(R.id.textColor);
            // productSize = (TextView) findViewById(R.id.textSizeValue);
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
              long arg3)
            {
                productColor.setText(colors[arg2]);
                // product.getProduct_propertys().put("颜色", (arg2 + 1) + "");
                dismissPopupWindow(popupWindow1);
            }
        });
        listView.setAdapter(new MyListViewAdapter(true));

        listView2 = new ListView(mContext);
        listView2.setVerticalScrollBarEnabled(false);// 隐藏滚动条
        listView2.setDividerHeight(0);// 没有分割线
        listView2.setDivider(null);
        listView2.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                productSize.setText(daxiao[arg2]);
                // product.getProduct_propertys().put("大小", (arg2 + 4) + "");
                dismissPopupWindow(popupWindow2);
            }
        });

        listView2.setAdapter(new MyListViewAdapter(false));
        popupWindow1 = new PopupWindow(listView, DensityUtil.px2dip(mContext, 90), -2);
        popupWindow1.setOutsideTouchable(true);// 点击外部关闭
        popupWindow1.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        popupWindow1.setFocusable(true);// 可以得到焦点

        popupWindow2 = new PopupWindow(listView2, DensityUtil.px2dip(mContext, 60), -2);
        popupWindow2.setOutsideTouchable(true);// 点击外部关闭
        popupWindow2.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        popupWindow2.setFocusable(true);// 可以得到焦点
        // dl_product_detail_textSizeValue.setOnClickListener(new
        // OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // popupWindow2.showAsDropDown(dl_product_detail_textSizeValue, 5, -5);
        // }
        // });
    }

    private class MyListViewAdapter extends BaseAdapter
    {
        private boolean isColor;

        public MyListViewAdapter(boolean isColor)
        {
            super();
            this.isColor = isColor;
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public Object getItem(int arg0)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
          ViewGroup parent)
        {
            TextView tv = new TextView(mContext);
            if (isColor)
                tv.setText(colors[position]);
            else
                tv.setText(daxiao[position]);
            return tv;
        }
    }

    @Override
    public int getId()
    {
        return ConstantValue.PRODUCT_DETAIL_VIEW;
    }
}
