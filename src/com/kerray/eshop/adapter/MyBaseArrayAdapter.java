package com.kerray.eshop.adapter;

import android.widget.BaseAdapter;

/**
 * 功能:  String数组的基类adapter
 * 创建人:kerray
 * 创建时间:2015/7/17/13:06
 */
public abstract class MyBaseArrayAdapter extends BaseAdapter
{
    private String[] mArray;

    public MyBaseArrayAdapter(String[] pArray)
    {
        this.mArray = pArray;
    }

    @Override
    public int getCount()
    {
        return mArray.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mArray[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
}
