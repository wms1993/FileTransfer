package com.wms.kuaichuan.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wms.kuaichuan.Constant;
import com.wms.kuaichuan.R;
import com.wms.kuaichuan.common.BaseActivity;
import com.wms.kuaichuan.core.utils.FileUtils;
import com.wms.kuaichuan.core.utils.TextUtils;
import com.wms.kuaichuan.core.utils.ToastUtils;
import com.wms.kuaichuan.ui.view.MyScrollView;
import com.wms.kuaichuan.utils.NavigatorUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyScrollView.OnScrollListener {

    private static final String TAG = HomeActivity.class.getSimpleName();


    /**
     * 左右两大块 UI
     */
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    TextView tv_name;

    /**
     * top bar 相关UI
     */
    @Bind(R.id.ll_mini_main)
    LinearLayout ll_mini_main;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.iv_mini_avator)
    ImageView iv_mini_avator;
    @Bind(R.id.btn_send)
    Button btn_send;
    @Bind(R.id.btn_receive)
    Button btn_receive;

    /**
     * 其他UI
     */
    @Bind(R.id.msv_content)
    MyScrollView mScrollView;
    @Bind(R.id.ll_main)
    LinearLayout ll_main;
    @Bind(R.id.btn_send_big)
    Button btn_send_big;
    @Bind(R.id.btn_receive_big)
    Button btn_receive_big;

    @Bind(R.id.rl_device)
    RelativeLayout rl_device;
    @Bind(R.id.tv_device_desc)
    TextView tv_device_desc;
    @Bind(R.id.rl_file)
    RelativeLayout rl_file;
    @Bind(R.id.tv_file_desc)
    TextView tv_file_desc;
    @Bind(R.id.rl_storage)
    RelativeLayout rl_storage;
    @Bind(R.id.tv_storage_desc)
    TextView tv_storage_desc;


    //大的我要发送和我要接受按钮的LinearLayout的高度
    int mContentHeight = 0;


    //
    boolean mIsExist = false;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        //初始化
        init();
    }

    @Override
    protected void onResume() {
        updateBottomData();
        super.onResume();
    }

    /**
     * 初始化
     */
    private void init() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        //设置设备名称
        String device = TextUtils.isNullOrBlank(android.os.Build.DEVICE) ? Constant.DEFAULT_SSID : android.os.Build.DEVICE;
        try {//设置左边抽屉的设备名称
            tv_name = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);
            tv_name.setText(device);
        } catch (Exception e) {
            //maybe occur some exception
        }

        mScrollView.setOnScrollListener(this);

        ll_mini_main.setClickable(false);
        ll_mini_main.setVisibility(View.GONE);

        updateBottomData();
    }

    /**
     * 更新底部 设备数，文件数，节省流量数的数据
     */
    private void updateBottomData() {
        //TODO 设备数的更新
        //TODO 文件数的更新
        tv_file_desc.setText(String.valueOf(FileUtils.getReceiveFileCount()));
        //TODO 节省流量数的更新
        tv_storage_desc.setText(String.valueOf(FileUtils.getReceiveFileListTotalLength()));

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
//                super.onBackPressed();
                if (mIsExist) {
                    this.finish();
                } else {
                    ToastUtils.show(getContext(), getContext().getResources().getString(R.string.tip_call_back_agin_and_exist)
                            .replace("{appName}", getContext().getResources().getString(R.string.app_name)));
                    mIsExist = true;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsExist = false;
                        }
                    }, 2 * 1000);

                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        if (id == R.id.nav_about) {
            Log.i(TAG, "R.id.nav_about------>>> click");
            showAboutMeDialog();
        } else if (id == R.id.nav_web_transfer) {
            Log.i(TAG, "R.id.nav_web_transfer------>>> click");
//            NavigatorUtils.toWebTransferUI(getContext());
            NavigatorUtils.toChooseFileUI(getContext(), true);
        } else {
            ToastUtils.show(getContext(), getResources().getString(R.string.tip_next_version_update));
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({R.id.btn_send, R.id.btn_receive, R.id.btn_send_big, R.id.btn_receive_big, R.id.iv_mini_avator,
            R.id.rl_device, R.id.rl_file, R.id.rl_storage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
            case R.id.btn_send_big: {
                NavigatorUtils.toChooseFileUI(getContext());
                break;
            }
            case R.id.btn_receive:
            case R.id.btn_receive_big: {
                NavigatorUtils.toReceiverWaitingUI(getContext());
                break;
            }
            case R.id.iv_mini_avator: {
                if (mDrawerLayout != null) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            }
            case R.id.rl_file:
            case R.id.rl_storage: {
                NavigatorUtils.toSystemFileChooser(getContext());
                break;
            }

        }
    }

    //自定义ScrollView的监听
    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        Log.i(TAG, "l-->" + l + ",t-->" + t + ",oldl-->" + oldl + ",oldt-->" + oldt);
        mContentHeight = ll_main.getMeasuredHeight();
//        Log.i(TAG, "content height : " + mContentHeight);
//        float alpha = t / (float)mContentHeight;
//        Log.i(TAG, "content alpha : " + alpha);
//        tv_title.setAlpha(alpha);
        //一半的位置时候
        // topbar上面的两个小按钮 跟 主页上面的两个大按钮的alpha值是对立的 即 alpha 与 1-alpha的关系
        if (t > mContentHeight / 2) {
            float sAlpha = (t - mContentHeight / 2) / (float) (mContentHeight / 2);
            ll_mini_main.setVisibility(View.VISIBLE);
            ll_main.setAlpha(1 - sAlpha);
            ll_mini_main.setAlpha(sAlpha);
            tv_title.setAlpha(0);
        } else {
            float tAlpha = t / (float) mContentHeight / 2;
            tv_title.setAlpha(1 - tAlpha);
            ll_mini_main.setVisibility(View.INVISIBLE);
            ll_mini_main.setAlpha(0);
        }

    }

    /**
     * 显示对话框
     */
    private void showAboutMeDialog() {
        View contentView = View.inflate(getContext(), R.layout.view_about_me, null);
        contentView.findViewById(R.id.tv_github).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toProject();
            }
        });
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.title_about_me))
                .setView(contentView)
                .setPositiveButton(getResources().getString(R.string.str_weiguan), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toProject();
                    }
                })
                .create()
                .show();
    }

    /**
     * 跳转到项目
     */
    private void toProject() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(Constant.GITHUB_PROJECT_SITE);
        intent.setData(uri);
        getContext().startActivity(intent);
    }
}
