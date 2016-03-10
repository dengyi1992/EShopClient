package com.kerray.eshop.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import com.kerray.eshop.ConstantValue;
import com.kerray.eshop.R;
import com.kerray.eshop.adapter.MyBaseListAdapter;
import com.kerray.eshop.bean.ShopTypeAll;
import com.kerray.eshop.util.GsonUtils;
import com.kerray.eshop.util.xUtilsImageLoader;
import com.kerray.eshop.view.manager.BaseView;
import com.kerray.eshop.view.manager.BottomManager;
import com.kerray.eshop.view.manager.TitleManager;
import com.kerray.eshop.view.manager.UIManager;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import static com.kerray.eshop.R.id.catergory_image;
import static com.kerray.eshop.R.id.catergoryitem_title;

/**
 * 类名: ${CLASS_NAME}
 * 功能:
 * 创建人:kerray
 * 创建时间:2015/7/13/20:24
 */
public class CategoryView1 extends BaseView implements AdapterView.OnItemClickListener, View.OnClickListener
{
    private int mFromY = 0;

    @ViewInject(R.id.cate_indicator_img)
    private ImageView mCateIndicatorImg;

    @ViewInject(R.id.lv_catergory_left)
    private ListView leftListView;                          // 左边边Listview
    private LeftAdapter mLeftAdapter;                       // 左边分类adapter

    @ViewInject(R.id.lv_catergory_right)
    private ListView rightListview;                         // 右边Listview
    private RightAdapter mRightAdapter;                     // 右边分类adapter

    private xUtilsImageLoader mXUtilsImageLoader;           // 自定义的xUtilsBitmap

    List<ShopTypeAll.ShopType> shopTypes;                   // 全部分类的集合
    List<ShopTypeAll.ShopTypeItem> children;                // 右边子分类的集合，根据选择左边不同分类更新到这个集合中显示到右边的ListView


    @Override
    protected void init()
    {
        showView = (RelativeLayout) View.inflate(mContext, R.layout.view_category, null);
        ViewUtils.inject(this, showView);
        mXUtilsImageLoader = new xUtilsImageLoader(mContext);// 自定义的xUtils图片解析，增加了渐变动画

        UIManager.getInstance().clear();                     // 清理返回键
    }

    @Override
    public void onResume()
    {
        getDataFromServer();
        super.onResume();
    }

    /**
     * 联网加载全部分类数据
     * 缓存数据到data目录下
     */
    private void getDataFromServer()
    {
        /*String value = SharePrefUtil.getString(mContext, ConstantValue.SHOPTYPE_LIST_URL, "");
        if (!TextUtils.isEmpty(value))
        {
            PromptManager.showToast(mContext, "缓存的分类");
            ProcessData(value);
        }*/
        /*HttpUtils httpUtils = new HttpUtils();
        httpUtils.configCurrentHttpCacheExpiry(0 * 1000);
        //设置默认请求的缓存时间
        httpUtils.configDefaultHttpCacheExpiry(0);
        //设置线程数
        httpUtils.configRequestThreadPoolSize(1);
        httpUtils.send(HttpRequest.HttpMethod.GET, ConstantValue.SHOPTYPE_LIST_URL, new MyRequestCallBack<String>()
        {
            public void onSuccess(ResponseInfo<String> responseInfo)
            {
                super.onSuccess(responseInfo);
                SharePrefUtil.saveString(mContext, ConstantValue.SHOPTYPE_LIST_URL, responseInfo.result);
                ProcessData(responseInfo.result);
            }
        });*/

        /*try
        {
            Thread thread = new Thread(mRunnable);
            thread.start();
            thread.join();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }*/
        new MyHttpTask<String, String>()
        {
            @Override
            protected String doInBackground(String... params)
            {
                return MyHttpURLConnection(ConstantValue.SHOPTYPE_LIST_URL);
            }

            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                ProcessData(result);
            }
        }.executeProxy();
    }


    /**
     * 处理返回的json到实体类中，放到内存中用于更新
     * @param result json数据
     */
    private void ProcessData(String result)
    {
        ShopTypeAll shopTypeAll = GsonUtils.jsonToBean(result, ShopTypeAll.class);
        if (null != shopTypeAll)
        {
            if (200 == shopTypeAll.retcode)
                shopTypes = shopTypeAll.shopTypes;

            // 把处理完的左边分类设置到全部分类listview中
            initLeftAdapter();
            // 默认更新左边第一个分类数据到右边listview
            updateRightListViewDate(0);
        } else
        {
            //PromptManager.showToast(mContext, "json出错！！！");
            try
            {
                Thread.sleep(1000);
                getDataFromServer();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据左边选择的位置更新数据到右边 listview
     * @param position 左边item的位置
     */
    public void updateRightListViewDate(int position)
    {
        // 更新右边分类的集合数据
        children = shopTypes.get(position).children;

        // 把分类名称和图片地址复制给标题和图片数组，并重新设置数组长度便于更新
        // 更新右边的listview
        initRightAdapter();
    }


    private void initLeftAdapter()
    {
        //左边导航的数据
        if (null == mLeftAdapter)
        {
            mLeftAdapter = new LeftAdapter(mContext, shopTypes);
            leftListView.setAdapter(mLeftAdapter);
        } else
            mLeftAdapter.notifyDataSetChanged();

        // 用于计算mCateIndicatorImg的高度
        int itemHeight = calculateListViewItemHeight();
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mCateIndicatorImg.measure(w, h);

        // 第一次进来设置indicator的位置
        doAnimation(itemHeight / 2 - mCateIndicatorImg.getMeasuredHeight());
    }


    public void initRightAdapter()
    {
        if (null == mRightAdapter)
        {
            mRightAdapter = new RightAdapter(mContext, children);
            rightListview.setAdapter(mRightAdapter);
        } else
        {
            mRightAdapter.mList = children;
            mRightAdapter.notifyDataSetChanged();
        }

        leftListView.setOnItemClickListener(this);

    }

    /**
     * 左边分类点击事件
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // 左边箭头移动
        if (null != mLeftAdapter)
            mLeftAdapter.setSelectedPos(position);

        int toY = view.getTop() + view.getHeight() / 2;
        doAnimation(toY);

        // 选中左边分类后更新右边的子分类
        updateRightListViewDate(position);
    }

    //****************************************************************************************
    //***************************  左边ListView分类的adapter  *********************************
    //****************************************************************************************
    public class LeftAdapter extends MyBaseListAdapter
    {
        private LayoutInflater mInflater;
        private int mSelectedPos = 0;

        public LeftAdapter(Context pContext, List plist)
        {
            super(pContext, plist);
            this.mInflater = LayoutInflater.from(pContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (null == mContext)
                return null;

            if (null == shopTypes || shopTypes.size() == 0
              || shopTypes.size() <= position)
                return null;

            final ViewHolder viewHolder;
            if (null == convertView)
            {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.cate_list_item, parent, false);
                viewHolder.cateCheckedTextView = (CheckedTextView) convertView.findViewById(R.id.cate_tv);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();
            // 左边文本
            viewHolder.cateCheckedTextView.setText(shopTypes.get(position).分类名称);
            // 左边的箭头
            if (mSelectedPos == position)
                viewHolder.cateCheckedTextView.setTextColor(Color.rgb(247, 88, 123));
            else
                viewHolder.cateCheckedTextView.setTextColor(Color.rgb(19, 12, 14));
            return convertView;
        }

        class ViewHolder
        {
            CheckedTextView cateCheckedTextView;
        }

        public void setSelectedPos(int position)
        {
            this.mSelectedPos = position;
            notifyDataSetChanged();
        }
    }

    //****************************************************************************************
    //***************************  右边ListView分类的adapter  *********************************
    //****************************************************************************************
    private class RightAdapter extends MyBaseListAdapter
    {
        LayoutInflater layoutInflater;

        public RightAdapter(Context pContext, List plist)
        {
            super(pContext, plist);
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

            String imagepath = children.get(position).imagepath;
            imagepath = (null == imagepath) ? imagepath : ConstantValue.ESHOP_URI + imagepath;

            // 没有图片地址的设置为默认图片
            if (null == imagepath)
                mXUtilsImageLoader.display(holder.image, "assets/ic_jd.png");
            else
                mXUtilsImageLoader.display(holder.image, imagepath);

            // 为 null 时的处理
            holder.title.setText("" + children.get(position).分类名称);
            return convertView;
        }

        public class ViewHolder
        {
            @ViewInject(catergory_image)
            ImageView image;
            @ViewInject(catergoryitem_title)
            TextView title;
        }
    }


    private int calculateListViewItemHeight()
    {
        ListAdapter listAdapter = leftListView.getAdapter();
        if (listAdapter == null)
            return 0;

        int totalHeight = 0;
        int count = listAdapter.getCount();
        for (int i = 0; i < count; i++)
        {
            View listItem = listAdapter.getView(i, null, leftListView);
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

        // 右边分类item点击事件
        rightListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterview, View view, final int parent, long id)
            {
                Bundle bundle = new Bundle();
                bundle.putLong("ShopType_Id", children.get(parent).id);
                bundle.putString("ShopType_Name", children.get(parent).分类名称);
                UIManager.getInstance().changeView(ShopInfoListView.class, bundle);
            }
        });
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


    public CategoryView1(Context context, Bundle bundle)
    {
        super(context, bundle);
    }

    @Override
    public int getId()
    {
        return ConstantValue.CATEGORY_VIEW;
    }
}
