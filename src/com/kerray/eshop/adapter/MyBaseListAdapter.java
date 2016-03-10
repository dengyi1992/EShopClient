package com.kerray.eshop.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 作用:	Adapter基类
 * 创建人: kerray
 * 时间:
 */
public abstract class MyBaseListAdapter<T, Q> extends BaseAdapter
{
	public Context mContext;
	public List<T> mList;//
	public Q mView; // 这里不一定是ListView,比如GridView,CustomListView

	public MyBaseListAdapter(Context pContext, List<T> plist)
	{
		super();
		this.mContext = pContext;
		this.mList = plist;
	}

	public MyBaseListAdapter(Context context, List<T> plist, Q pView)
	{
		super();
		this.mContext = context;
		this.mList = plist;
		this.mView = pView;
	}

	@Override
	public int getCount()
	{
		return mList.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mList.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}


}
