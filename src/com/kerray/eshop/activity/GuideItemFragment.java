package com.kerray.eshop.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.kerray.eshop.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class GuideItemFragment extends Fragment
{
    @ViewInject(R.id.iv_image)
    private ImageView iv_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_guide_item, container, false);
        ViewUtils.inject(this, view);

        //获取Activity传递过来的参数
        Bundle b = getArguments();
        int images = b.getInt("images");
        iv_image.setImageResource(images);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }
}
