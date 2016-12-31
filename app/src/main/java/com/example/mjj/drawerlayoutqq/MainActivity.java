package com.example.mjj.drawerlayoutqq;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 仿QQ6.6.0版本侧滑效果
 * <p>
 * DrawerLayout内容偏移，背景动画，主页面导航图标渐变，状态栏渐变色.
 * <p>
 * Created by Mjj on 2016/12/7.
 */
public class MainActivity extends AppCompatActivity {

    private SystemBarTintManager tintManager;
    private DrawerLayout mDrawerLayout;
    private ImageView ivNavigation;

    private ImageView ivDrawerBg; // 侧边有动画效果的背景图片

    private TextView tvCenter, tvRight;

    private int[] tabIcons = {R.drawable.ngq, R.drawable.nti, R.drawable.nbb};
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ListView listView;
    private String strings[] = {"开通会员", "QQ钱包", "个性装扮", "我的收藏", "我的相册", "我的文件", "我的日程", "我的名片夹"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(Color.parseColor("#14B6F6"));
        }
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        ivNavigation = (ImageView) findViewById(R.id.iv_navigation_icon);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);

        ivDrawerBg = (ImageView) findViewById(R.id.iv_drawer_bg);
        float curTranslationY = ivDrawerBg.getTranslationY();
        ObjectAnimator animator = ObjectAnimator.ofFloat(ivDrawerBg, "translationY", curTranslationY,
                -70f, 60, curTranslationY);
        animator.setDuration(5000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();

        tvCenter = (TextView) findViewById(R.id.tv_center);
        tvRight = (TextView) findViewById(R.id.tv_right);

        tabLayout = (TabLayout) findViewById(R.id.tab_main_bottom);
        viewPager = (ViewPager) findViewById(R.id.vp_main_contents);

        // 同时添加图标和文字需要自定义view,此种方式无效,和适配器关联的时候,就被隐藏掉了.
//        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[0]));
//        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[1]));
//        tabLayout.addTab(tabLayout.newTab().setIcon(tabIcons[2]));

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),
                tabIcons, this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //设置监听
        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        listView = (ListView) mDrawerLayout.findViewById(R.id.id_draw_menu_item_list_select);
        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, strings));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, strings[i], Toast.LENGTH_SHORT).show();
            }
        });

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            /**
             * @param drawerView
             * @param slideOffset   偏移(0-1)
             */
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // 导航图标渐变效果
                ivNavigation.setAlpha(1 - slideOffset);
                // 判断是否左菜单并设置移动(如果不这样设置,则主页面的内容不会向右移动)
                if (drawerView.getTag().equals("left")) {
                    View content = mDrawerLayout.getChildAt(0);
                    int offset = (int) (drawerView.getWidth() * slideOffset);
                    content.setTranslationX(offset);

                    // 缩放效果(之前QQ效果)
//                    content.setTranslationX(1 - slideOffset * 0.5f);
//                    content.setTranslationY(1 - slideOffset * 0.5f);
                }
                tintManager.setStatusBarAlpha(1 - slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            /**
             * 当抽屉滑动状态改变的时候被调用
             * 状态值是STATE_IDLE（闲置-0），STATE_DRAGGING（拖拽-1），STATE_SETTLING（固定-2）中之一。
             * 抽屉打开的时候，点击抽屉，drawer的状态就会变成STATE_DRAGGING，然后变成STATE_IDLE.
             *
             * @param newState
             */
            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    // 设置状态栏透明状态
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 自定义NavigationIcon设置关联DrawerLayout
     */
    private void toggle() {
        int drawerLockMode = mDrawerLayout.getDrawerLockMode(GravityCompat.START);
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)
                && (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

}
