package com.kerray.eshop.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import com.kerray.eshop.GlobalParams;
import com.kerray.eshop.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * @类名: HomeListAdapter
 * @功能: 首页列表的展示
 * @创建人:kerray
 * @创建时间:2015/7/12/12:07
 */
public class HomeListAdapter extends MyBaseListAdapter implements AdapterView.OnItemClickListener
{
    private int[] logoResIds = new int[] { R.drawable.home_classify_03, R.drawable.home_classify_01,
      R.drawable.home_classify_02, R.drawable.home_classify_03,
      R.drawable.home_classify_04, R.drawable.home_classify_05, };


    public HomeListAdapter(Context mContext, List plist)
    {
        super(mContext, plist);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();
            view = View.inflate(GlobalParams.CONTEXT, R.layout.ll_main_item, null);

            ViewUtils.inject(holder, view);
            view.setTag(holder);
        } else
        {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.iv_left.setImageResource(logoResIds[position]);
        holder.tv_medder.setText("" + mList.get(position));
        return view;
    }

    class ViewHolder
    {
        @ViewInject(R.id.iv_left)
        ImageView iv_left;
        @ViewInject(R.id.tv_medder)
        TextView tv_medder;
        @ViewInject(R.id.iv_right)
        ImageView iv_right;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }
}
