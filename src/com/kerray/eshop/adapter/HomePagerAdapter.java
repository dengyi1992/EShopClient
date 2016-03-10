package com.kerray.eshop.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.lidroid.xutils.util.LogUtils;

import java.util.List;

/**
 * 功能:		首页轮播图viewpage的adapter
 *
 * 创建人: 	kerray
 *
 * 创建时间:
 */
public class HomePagerAdapter extends PagerAdapter{

	private List<ImageView> imageViewList;
	
	
	public HomePagerAdapter(List<ImageView> imageViewList) {
		super();
		this.imageViewList = imageViewList;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(imageViewList.get(position
				% imageViewList.size()));
	}
	
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = imageViewList.get(position % imageViewList.size());
		/** 设置图片点击触发的事件*/
		setImageClickListener(position, view);
		container.addView(view);
		return imageViewList.get(position % imageViewList.size());
	}

	/**
	 * 设置图片点击触发的事件
	 * @param position
	 * @param view
	 */
	private void setImageClickListener(final int position, View view) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (position % imageViewList.size()) {
				case 0:
					LogUtils.i("第一张图片");
					break;
				case 1:

					break;

				}
				
			}
		});
	}
	
}
