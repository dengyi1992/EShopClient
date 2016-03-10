package com.kerray.eshop.view.manager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import com.kerray.eshop.R;
import com.kerray.eshop.util.FadeUtil;
import com.kerray.eshop.util.MemoryManager;
import com.kerray.eshop.util.PromptManager;
import com.kerray.eshop.util.SoftMap;
import com.kerray.eshop.view.HomeView;
import com.lidroid.xutils.util.LogUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;

/**
 * 中间容器的管理工具
 * @author Administrator
 */
public class UIManager extends Observable
{

    // 利用手机内存空间，换应用的运行速度
    private LinkedList<String> HISTORY = new LinkedList<String>();// 用户操作的历史记录

    // 利用手机内存空间，换应用的运行速度
    private static Map<String, BaseView> VIEWCACHE = new HashMap<>();        // K:唯一的标示BaseUI的子类

    static
    {
        // 16M，如果不足<16M(模拟器)
        // 32M，真机
        if (MemoryManager.hasAcailMemory())
            VIEWCACHE = new HashMap<>();
        else
            VIEWCACHE = new SoftMap<>();
    }

    private UIManager()
    {
    }

    private static UIManager instance = new UIManager();

    public static UIManager getInstance()
    {
        return instance;
    }

    /** 中间内容显示容器，显示内容只需往里加Baseview */
    private RelativeLayout middleContainer;

    public void setMiddleContainer(RelativeLayout middleContainer)
    {
        this.middleContainer = middleContainer;
    }

//    private static Map<String, BaseView> VIEWCACHE = new HashMap<String, BaseView>();// 利用内存的空间换应用的执行时间
    private BaseView currentView;// 当前正在显示

    public BaseView getCurrentView()
    {
        return currentView;
    }

    /**
     * 问题：参数的传递
     */
    public void changeView(Class<? extends BaseView> targetClazz, Bundle bundle)
    {
        // middleContainer.getChildAt(0)
        // 第一次切换
        // 比对：目标（targetClazz） VS 当前正在显示
        if (currentView != null && currentView.getClass() == targetClazz)
            return;

        // 如果是第一次，创建该对象,并存储起来

        // targetClazz.getSimpleName(); 唯一标示
        String key = targetClazz.getSimpleName();

        BaseView target = null;

        if (VIEWCACHE.containsKey(key))
        {
            target = VIEWCACHE.get(key);
            target.setmBundle(bundle);
        } else
        {
            try
            {
                Constructor<? extends BaseView> constructor = targetClazz.getConstructor(Context.class, Bundle.class);
                target = constructor.newInstance(getContext(), bundle);

                VIEWCACHE.put(key, target);
            } catch (Exception e)
            {
                throw new RuntimeException("constructor new instance error,参数出错!");
            }
        }

        LogUtils.i(target.toString());

        if (target != null)
            target.setmBundle(bundle);

        // 当前界面要被回收：注销耗费资源操作
        if (currentView != null)
            currentView.onPause();

        // 需要增加参数：切换的目标
        middleContainer.removeAllViews();
        View view2 = target.getView();
        middleContainer.addView(view2);
        // 更新界面数据+注册耗费资源操作
        target.onResume();
        /** 动画*/
        //view2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));
        FadeUtil.fadeIn(view2, 0, 400);

        currentView = target;

        // 添加元素到返回键集合的顶部
        /** 如果该元素已经存在，移动该页面到顶部*/
        if (HISTORY.contains(key))
        {
            HISTORY.remove(key);
            HISTORY.addFirst(key);
        } else
        {
            HISTORY.addFirst(key);
        }

        // 完成三个容器的联动

        changeTitleAndBottom();
        //FadeUtil.fadeOut(view2, 500);
    }

    private void changeTitleAndBottom()
    {
        setChanged();
        notifyObservers(currentView.getId());
    }


    public Context getContext()
    {
        return middleContainer.getContext();
    }

    /**
     * 返回键处理
     * @return
     */
    public boolean goBack()
    {
        // 记录一下用户操作历史
        // 频繁操作栈顶（添加）——在界面切换成功
        // 获取栈顶
        // 删除了栈顶
        // 有序集合
        if (HISTORY.size() > 0)
        {
            // 当用户误操作返回键（不退出应用）
            if (HISTORY.size() == 1)
                return false;

            // Throws:NoSuchElementException - if this LinkedList is empty.
            HISTORY.removeFirst();

            if (HISTORY.size() > 0)
            {
                // Throws:NoSuchElementException - if this LinkedList is empty.
                String key = HISTORY.getFirst();

                BaseView target = VIEWCACHE.get(key);

                if (target != null)
                {
                    currentView.onPause();
                    middleContainer.removeAllViews();
                    middleContainer.addView(target.getView());
                    target.onResume();
                    currentView = target;

                    changeTitleAndBottom();
                } else
                {
                    // 处理方式一：创建一个新的目标界面：存在问题——如果有其他的界面传递给被删除的界面
                    // 处理方式二：寻找一个不需要其他界面传递数据——跳转到首页
                    changeView(HomeView.class, null);
                    PromptManager.showToast(getContext(), "应用在低内存下运行");
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 返回键处理
     * @return
     */
    public boolean goBack1()
    {
        String key = "";
        if (HISTORY.size() > 0)
        {
            if (HISTORY.size() == 1)
                return false;
            else
                HISTORY.removeFirst();

            if (HISTORY.size() > 0)
                key = HISTORY.getFirst();

            if (StringUtils.isNotBlank(key))
            {
                BaseView target = VIEWCACHE.get(key);

                currentView.onPause();
                middleContainer.removeAllViews();
                View view2 = target.getView();
                middleContainer.addView(view2);
                target.onResume();
                view2.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.ia_view_change));

                // 记录当前正在显示界面的对象
                currentView = target;

                changeTitleAndBottom();
                return true;
            }
        }
        return false;
    }

    /**
     * 清理返回键
     */
    public void clear()
    {
        HISTORY.clear();
    }
}
