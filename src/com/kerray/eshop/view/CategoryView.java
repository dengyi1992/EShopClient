package com.kerray.eshop.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.adapter.CateListAdapter;
import com.kerray.eshop.bean.ShopTypeAll;
import com.kerray.eshop.util.GsonUtils;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.util.SharePrefUtil;
import com.kerray.eshop.util.xUtilsImageLoader;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.BottomManager;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kerray.eshop.R.id.*;

/**
 * 类名: ${CLASS_NAME}
 * 功能:
 * 创建人:kerray
 * 创建时间:2015/7/13/20:24
 */
public class CategoryView extends BaseView implements AdapterView.OnItemClickListener, View.OnClickListener
{
    @ViewInject(lv_catergory_left)
    private ListView mCateListView;
    @ViewInject(cate_indicator_img)
    private ImageView mCateIndicatorImg;

    private int mFromY = 0;

    private LayoutInflater layoutInflater;
    /**
     * 一级分类adapter
     */
    private CateListAdapter mCateListAdapter;

    @ViewInject(R.id.lv_catergory_right)
    private ListView catergory_seconde_listview;            //菜单的二级分类

    private xUtilsImageLoader mxUtilsImageLoader;           // 自定义的xUtilsBitmap
    /**
     * 二级分类adapter
     */
    private CatergorAdapter mCatergorAdapter;
    /**
     * 全部分类的list
     */
    List<List<ShopTypeAll.ShopTypeItem>> mShopTypelist;

    /** ****************两个listview的初始数据长度************************************* */
    // 一级分类默认数据
    private static String[] mCategories = new String[20];

    private static Long[] mId = new Long[20];
    // 二级分类的图片数组
    private static String[] mImage = new String[20];
    // 二级分类的文字(Title)
    private static String[] mTitleValues = new String[20];

    public CategoryView(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.CATEGORY_VIEW;
    }

    @Override
    protected void init()
    {
        showView = (RelativeLayout) View.inflate(mContext, R.layout.view_category, null);
        ViewUtils.inject(this, showView);
        mxUtilsImageLoader = new xUtilsImageLoader(mContext);                           // 自定义的xUtils图片解析，增加了渐变动画
        initView();
        initData();
        UIManager.getInstance().clear();// 清理返回键

        //初始化左边导航的数据
        //initLeftAdapter();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void initLeftAdapter()
    {
        //左边导航的数据
        if (null == mCateListAdapter)
        {
            mCateListAdapter = new CateListAdapter(mContext, mCategories);
            mCateListView.setAdapter(mCateListAdapter);
        } else
            mCateListAdapter.notifyDataSetChanged();

        // 用于计算mCateIndicatorImg的高度
        int itemHeight = calculateListViewItemHeight();
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mCateIndicatorImg.measure(w, h);

        // 第一次进来设置indicator的位置
        doAnimation(itemHeight / 2 - mCateIndicatorImg.getMeasuredHeight());
    }

    /**
     * 联网加载全部分类数据
     * 缓存数据到data目录下
     */
    private void initData()
    {
        String value = SharePrefUtil.getString(mContext, ConstantValue.SHOPTYPE_LIST_URL, "");
        if (!TextUtils.isEmpty(value))
        {
            PromptManager.showToast(mContext, "缓存的分类");
            ProcessData(value);
        }
        new HttpUtils().send(HttpRequest.HttpMethod.GET, ConstantValue.SHOPTYPE_LIST_URL, new MyRequestCallBack<String>()
        {
            public void onSuccess(ResponseInfo<String> responseInfo)
            {
                super.onSuccess(responseInfo);
                SharePrefUtil.saveString(mContext, ConstantValue.SHOPTYPE_LIST_URL, responseInfo.result);
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
        int i = 0;
        mShopTypelist = new ArrayList<>();

        ShopTypeAll shopTypeAll = GsonUtils.jsonToBean(result, ShopTypeAll.class);

        if (200 == shopTypeAll.retcode)
        {
            List<ShopTypeAll.ShopType> data = shopTypeAll.shopTypes;
            mShopTypelist.clear();
            for (ShopTypeAll.ShopType cate : data)
            {
                mShopTypelist.add(cate.children);
                mCategories[i] = cate.分类名称;
                i++;
            }


        }
        mCategories = Arrays.copyOf(mCategories, i);
        // 把处理完的左边分类设置到全部分类listview中
        initLeftAdapter();
        // 默认更新左边第一个分类数据到右边listview
        updateRightListViewDate(0);
    }

    /**
     * 根据左边选择的位置更新数据到右边 listview
     * @param position 左边item的位置
     */
    public void updateRightListViewDate(int position)
    {
        String[] nameItem = new String[20];
        String[] image = new String[20];
        Long[] id = new Long[20];
        int num = 0;

        for (ShopTypeAll.ShopTypeItem s : mShopTypelist.get(position))
        {
            // 取出分类名称和图片地址，图片地址要加上服务器的公共 URI，地址为空则不添加 URI
            image[num] = (s.imagepath == null) ? s.imagepath : ConstantValue.ESHOP_URI + s.imagepath;
            nameItem[num] = s.分类名称;
            id[num] = s.id;
            num++;
        }

        // 把分类名称和图片地址复制给标题和图片数组，并重新设置数组长度便于更新
        mId = Arrays.copyOf(id, num);
        mImage = Arrays.copyOf(image, num);
        mTitleValues = Arrays.copyOf(nameItem, num);
        // 更新右边的listview
        mCatergorAdapter.notifyDataSetChanged();
    }

    protected void initView()
    {
        mCateListView.setOnItemClickListener(this);
        mCatergorAdapter = new CatergorAdapter();
        catergory_seconde_listview.setAdapter(mCatergorAdapter);
        catergory_seconde_listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterview, View view, final int parent, long id)
            {
                ViewHolder holder = new ViewHolder();
                //layoutInflater = LayoutInflater.from(mContext);
                //组装数据
                if (null == view)
                {
                    ViewUtils.inject(holder, view);
                    //使用tag存储数据
                    view.setTag(holder);
                } else
                    holder = (ViewHolder) view.getTag();

                Bundle bundle = new Bundle();
                bundle.putLong("ShopType_Id", mId[parent]);
                bundle.putString("ShopType_Name", holder.title.getText().toString());
                UIManager.getInstance().changeView(ShopInfoListView.class, bundle);

            }
        });
    }

    /**
     * 二级分类菜单
     * @author zhihong
     */
    private class CatergorAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return mTitleValues.length;
        }

        @Override
        public Object getItem(int position)
        {
            return mTitleValues[position];
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @SuppressWarnings("null")
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = new ViewHolder();
            layoutInflater = LayoutInflater.from(mContext);

            //组装数据
            if (convertView == null)
            {
                convertView = layoutInflater.inflate(R.layout.view_category_item, null);
                ViewUtils.inject(holder, convertView);
                //使用tag存储数据
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            // 没有图片地址的设置为默认图片
            if (null == mImage[position])
                mxUtilsImageLoader.display(holder.image, "assets/ic_jd.png");
            else
                mxUtilsImageLoader.display(holder.image, mImage[position]);

            // "" 为 null 时的处理
            holder.title.setText("" + mTitleValues[position]);
            return convertView;
        }
    }

    public static class ViewHolder
    {
        @ViewInject(catergory_image)
        ImageView image;
        @ViewInject(catergoryitem_title)
        TextView title;
    }


    /**
     * 左边分类点击事件
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // 左边箭头移动
        if (null != mCateListAdapter)
            mCateListAdapter.setSelectedPos(position);

        int toY = view.getTop() + view.getHeight() / 2;
        doAnimation(toY);

        // 选中左边分类后更新右边的子分类
        updateRightListViewDate(position);
    }

    private int calculateListViewItemHeight()
    {
        ListAdapter listAdapter = mCateListView.getAdapter();
        if (listAdapter == null)
            return 0;

        int totalHeight = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < count; i++)
        {
            View listItem = listAdapter.getView(i, null, mCateListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        return totalHeight / count;
    }

    private void doAnimation(int toY)
    {
        int cateIndicatorY = mCateIndicatorImg.getTop()
          + mCateIndicatorImg.getMeasuredHeight() / 2;
        TranslateAnimation animation = new TranslateAnimation(0, 0, mFromY
          - cateIndicatorY, toY - cateIndicatorY);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(400);
        mCateIndicatorImg.startAnimation(animation);
        mFromY = toY;
    }


    @Override
    protected void setListener()
    {
    }

    @Override
    protected void setTitleClickListener()
    {
        BottomManager.getInstrance().setCategoryRadiobutton();
    }

    @Override
    protected void setTitleContent()
    {
        TitleManager.getInstance().setIndexTextView("全部分类");
    }
}
