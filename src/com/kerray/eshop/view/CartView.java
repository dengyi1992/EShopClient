package com.kerray.eshop.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.GlobalParams;
import com.kerray.eshop.R;
import com.kerray.eshop.adapter.MyBaseListAdapter;
import com.kerray.eshop.bean.CartList;
import com.kerray.eshop.bean.ShopInfoList;
import com.kerray.eshop.bean.UserLogin;
import com.kerray.eshop.util.GsonUtils;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.util.xUtilsImageLoader;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.BottomManager;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.List;

/**
 * 功能:        购物车
 * 创建人:      kerray
 * 创建时间:    2015/7/19/22:03
 */
public class CartView extends BaseView
{
    @ViewInject(R.id.ll_cart_unlogin)
    private LinearLayout ll_cart_unlogin;                   // 未登陆的父布局
    @ViewInject(R.id.ll_cart_login)
    private LinearLayout ll_cart_login;                     // 登陆后显示的父布局
    @ViewInject(R.id.tv_cart_total)
    private TextView tv_cart_total;                         // 购物车的合计
    @ViewInject(R.id.tv_cart_account)
    private TextView tv_cart_account;                       // 购物车的结算

    @ViewInject(R.id.ll_cart_null)
    private LinearLayout ll_cart_null;                      // 购物车为空的显示
    @ViewInject(R.id.ll_cart_total)
    private LinearLayout ll_cart_total;                     // 底部合计的父布局
    @ViewInject(R.id.lv_cart_listview)
    private ListView lv_cart_listview;                      // 购物车商品
    private CartAdapter mCartAdapter;

    List<CartList.Cart> carts;                              // 存放购物车集合

    private xUtilsImageLoader mxUtilsImageLoader;           // 自定义的xUtilsBitmap

    double cart_total;                                      // 合计

    protected void init()
    {
        showView = (ViewGroup) View.inflate(mContext, R.layout.view_cart, null);
        ViewUtils.inject(this, showView);
        UIManager.getInstance().clear();// 清理返回键

        mxUtilsImageLoader = new xUtilsImageLoader(mContext);// 自定义的xUtils图片解析，增加了渐变动画
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
        LogUtils.i("islogin===" + islogin);
        // 判断是否登陆
        if (GlobalParams.islogin)
        {
            ll_cart_unlogin.setVisibility(View.GONE);
            ll_cart_login.setVisibility(View.VISIBLE);

            //HttpUtils对于GET请求采用了LRU缓存处理，默认60秒内提交返回上次成功的结果。
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.configCurrentHttpCacheExpiry(0 * 1000);
            //设置默认请求的缓存时间
            httpUtils.configDefaultHttpCacheExpiry(0);
            //设置线程数
            httpUtils.configRequestThreadPoolSize(1);
            // 拼接url,参数：用户id
            final String requestUrl = ConstantValue.CART_LIST_URL + "?user_id=" + GlobalParams.mUser.id;
            /*httpUtils.send(HttpRequest.HttpMethod.GET, requestUrl, new MyRequestCallBack<String>()
            {
                public void onSuccess(ResponseInfo<String> responseInfo)
                {
                    super.onSuccess(responseInfo);
                    ProcessData(responseInfo.result);
                }
            });*/

            new MyHttpTask<String, String>()
            {
                protected String doInBackground(String... params)
                {
                    return MyHttpURLConnection(requestUrl);
                }

                @Override
                protected void onPostExecute(String s)
                {
                    super.onPostExecute(s);
                    ProcessData(s);
                }
            }.executeProxy();
        } else
        {
            ll_cart_unlogin.setVisibility(View.VISIBLE);
            ll_cart_login.setVisibility(View.GONE);
        }
    }

    /**
     * 处理数据,更新listView
     * @param result
     */
    private void ProcessData(String result)
    {
        CartList data = GsonUtils.jsonToBean(result, CartList.class);
        if (null != data)
        {
            LogUtils.i("json正确了" + result);
            // 从购物车中取出购物车清单
            carts = data.carts;

            cart_total = 0;  // 购物车的合计金额

            // 如果购物车商品集合为空，显示购物车为空，不初始化listView
            if (0 == carts.size())
            {
                // 修改显示属性
                ll_cart_null.setVisibility(View.VISIBLE);
                lv_cart_listview.setVisibility(View.GONE);
                ll_cart_total.setVisibility(View.GONE);
            } else
            {
                // 设置空购物车不可见
                ll_cart_null.setVisibility(View.GONE);
                lv_cart_listview.setVisibility(View.VISIBLE);
                ll_cart_total.setVisibility(View.VISIBLE);

                // 初始化ListView或者更新
                if (null == mCartAdapter)
                {
                    mCartAdapter = new CartAdapter(mContext, carts);
                    lv_cart_listview.setAdapter(mCartAdapter);
                    mCartAdapter.notifyDataSetChanged();
                } else
                {
                    mCartAdapter.mList = carts;
                    mCartAdapter.notifyDataSetChanged();
                }

                tv_cart_account.setText("付款(" + carts.size() + ")");
                for (CartList.Cart cc : carts)
                    cart_total += cc.合计金额;
                tv_cart_total.setText("合计：¥ " + cart_total);
            }
        } else
        {
            //PromptManager.showToast(mContext, "json出错！！！");
            LogUtils.i("json出错！！！");
            try
            {
                Thread.sleep(1000);
                changeShowLogin();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @OnClick({ R.id.bt_cart_login,
      R.id.tv_cart_account,
      R.id.bt_cart_market })
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.bt_cart_market:
            UIManager.getInstance().changeView(CategoryView1.class, null);
            break;
        case R.id.bt_cart_login:
            UIManager.getInstance().changeView(LoginView.class, null);
            break;
        case R.id.tv_cart_account:
            payment();
            break;
        }
    }

    /**
     * 付款
     */
    private void payment()
    {
        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
          .setTitleText("确定支付" + cart_total + "元?")
          .setCancelText("取消")
          .setConfirmText("确定")
          .showCancelButton(true)
          .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
          {
              public void onClick(SweetAlertDialog sweetAlertDialog)
              {
                  // 关闭第一个dialog
                  sweetAlertDialog.dismiss();
                  // 拼接url,参数:用户id + 付款金额
                  String requestUrl = ConstantValue.CART_PAYMENT_URL + "?user_id=" + GlobalParams.mUser.id + "&total=" + cart_total;
                  new HttpUtils().send(HttpRequest.HttpMethod.GET, requestUrl, new MyRequestCallBack<String>()
                  {
                      public void onSuccess(ResponseInfo<String> responseInfo)
                      {
                          super.onSuccess(responseInfo);
                          UserLogin userLogin = GsonUtils.jsonToBean(responseInfo.result, UserLogin.class);
                          if (null != userLogin)
                          {
                              if (200 == userLogin.retcode)         // 付款成功的状态码为200
                              {
                                  // 如果能到这说明数据库肯定已经更改，直接更新全局变量mUser，到个人中心就不用再去请求网络了
                                  GlobalParams.mUser.余额 = GlobalParams.mUser.余额 - cart_total;
                                  // 切换到个人中心
                                  UIManager.getInstance().changeView(PersonelView.class, null);
                                  // 成功提示
                                  new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("付款成功！")
                                    .setConfirmText("确定")
                                    .show();
                              } else if (0 == userLogin.retcode)    // 余额不足的状态码为0
                              {
                                  // 余额不足提示
                                  new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("余额不足！")
                                    .setConfirmText("确定")
                                    .show();
                              }
                          } else
                          {
                              new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("付款失败！请稍后重试！")
                                .setConfirmText("确定")
                                .show();
                          }
                      }
                  });

              }
          })
          .show();
    }

    /**
     * 购物车adapter
     */
    class CartAdapter extends MyBaseListAdapter
    {
        public CartAdapter(Context pContext, List plist)
        {
            super(pContext, plist);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View view = convertView;
            final ViewHolder holder;
            if (view != null && view instanceof LinearLayout)
                holder = (ViewHolder) view.getTag();
            else
            {
                holder = new ViewHolder();
                view = View.inflate(mContext, R.layout.view_cart_shopinfo_item, null);
                ViewUtils.inject(holder, view);
                view.setTag(holder);
            }

            ShopInfoList.Shopinfo info = carts.get(position).shopInfo;

            // 图片地址为空的处理
            if (null == info.图片地址)
                mxUtilsImageLoader.display(holder.iv_cart_item_image, "assets/ic_jd.png");
            else
            {
                mxUtilsImageLoader.display(holder.iv_cart_item_image, ConstantValue.ESHOP_URI + info.图片地址.split("[,]")[0]);
            }

            // 为 null 时的处理
            holder.tv_cart_item_hide_id.setText(carts.get(position).id.toString());    // 存放商品id
            holder.tv_cart_item_title.setText(PromptManager.nullToZero(info.商品名称));
            holder.tv_cart_item_price.setText("¥ " + PromptManager.nullToZero(info.价格));
            holder.et_cart_product_num.setText("" + carts.get(position).商品数量);

            // 购物车删除
            holder.tv_cart_item_del.setOnClickListener(new MyOnClickListener(carts.get(position).id));

            //***************************************** 商品数量-1 *****************************************
            holder.tv_cart_cut_num.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    int productNum = Integer.parseInt(holder.et_cart_product_num.getText().toString());
                    if (1 == productNum)
                        PromptManager.showToast(mContext, "商品数量不能少于1件");
                    else
                    {
                        holder.et_cart_product_num.setText("" + (productNum = productNum - 1));
                        // 拼接url，参数：购物车id & 一个购物车中的商品数量
                        final String requestUrl = ConstantValue.CART_UPDATER_URL + "?cart_id=" + carts.get(position).id + "&product_num=" + productNum;
                        HttpUtilsSend(requestUrl);
                    }
                }
            });
            //***************************************** 商品数量+1 *****************************************
            holder.tv_cart_add_num.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final int productNum = Integer.parseInt(holder.et_cart_product_num.getText().toString()) + 1;
                    holder.et_cart_product_num.setText("" + productNum);

                    // 拼接url，参数：购物车id & 一个购物车中的商品数量
                    final String requestUrl = ConstantValue.CART_UPDATER_URL + "?cart_id=" + carts.get(position).id + "&product_num=" + productNum;
                    HttpUtilsSend(requestUrl);
                }
            });
            return view;
        }
    }

    class ViewHolder
    {
        @ViewInject(R.id.tv_cart_item_hide_id)
        TextView tv_cart_item_hide_id;
        @ViewInject(R.id.iv_cart_item_image)
        ImageView iv_cart_item_image;
        @ViewInject(R.id.tv_cart_item_title)
        TextView tv_cart_item_title;
        @ViewInject(R.id.tv_cart_item_price)
        TextView tv_cart_item_price;

        @ViewInject(R.id.tv_cart_cut_num)
        TextView tv_cart_cut_num;
        @ViewInject(R.id.tv_cart_add_num)
        TextView tv_cart_add_num;
        @ViewInject(R.id.et_cart_product_num)
        EditText et_cart_product_num;
        @ViewInject(R.id.tv_cart_item_del)
        TextView tv_cart_item_del;
    }

    /**
     * 删除购物车事件
     */
    class MyOnClickListener implements View.OnClickListener
    {
        private Long cartId;

        public MyOnClickListener(Long cartId)
        {
            this.cartId = cartId;
        }

        @Override
        public void onClick(View v)
        {
            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
              .setTitleText("确认要删除该商品？")
              .setCancelText("取消")
              .setConfirmText("确定")
              .showCancelButton(true)
              .setCancelClickListener(null)
              .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener()
              {
                  @Override
                  public void onClick(SweetAlertDialog sweetAlertDialog)
                  {
                      sweetAlertDialog.dismiss();
                      // 拼接url参数：购物车id
                      final String requestUrl = ConstantValue.CART_DELETE_URL + "?cart_id=" + cartId;
                      // 发送请求
                      HttpUtilsSend(requestUrl);
                  }
              })
              .show();
        }
    }

    /**
     * 封装的httpGet请求方法
     * @param requestUrl Url地址
     */
    public void HttpUtilsSend(String requestUrl)
    {
        new HttpUtils().send(HttpRequest.HttpMethod.GET, requestUrl, new MyRequestCallBack<String>()
        {
            public void onSuccess(ResponseInfo<String> responseInfo)
            {
                super.onSuccess(responseInfo);
                changeShowLogin();
            }
        });
    }

    @Override
    protected void setListener()
    {
    }

    @Override
    protected void setTitleClickListener()
    {
        BottomManager.getInstrance().setCartRadiobutton();
    }

    @Override
    protected void setTitleContent()
    {
        TitleManager.getInstance().setIndexTextView("购物车");
    }


    public CartView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.CART_VIEW;
    }
}
