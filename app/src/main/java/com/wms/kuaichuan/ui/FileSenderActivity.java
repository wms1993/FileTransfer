package com.wms.kuaichuan.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wms.kuaichuan.AppContext;
import com.wms.kuaichuan.Constant;
import com.wms.kuaichuan.R;
import com.wms.kuaichuan.common.BaseActivity;
import com.wms.kuaichuan.core.FileSender;
import com.wms.kuaichuan.core.entity.FileInfo;
import com.wms.kuaichuan.core.utils.FileUtils;
import com.wms.kuaichuan.core.utils.WifiMgr;
import com.wms.kuaichuan.ui.adapter.FileSenderAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 王梦思 on 2016/11/28.
 * <p/>
 */
public class FileSenderActivity extends BaseActivity {

    public static final int MSG_UPDATE_FILE_INFO = 0X6666;
    private static final String TAG = FileSenderActivity.class.getSimpleName();
    /**
     * Topbar相关UI
     */
    @Bind(R.id.tv_back)
    TextView tv_back;
    @Bind(R.id.tv_title)
    TextView tv_title;
    /**
     * 进度条 已传 耗时等UI组件
     */
    @Bind(R.id.pb_total)
    ProgressBar pb_total;
    @Bind(R.id.tv_value_storage)
    TextView tv_value_storage;
    @Bind(R.id.tv_unit_storage)
    TextView tv_unit_storage;
    @Bind(R.id.tv_value_time)
    TextView tv_value_time;
    @Bind(R.id.tv_unit_time)
    TextView tv_unit_time;
    /**
     * 其他UI
     */
    @Bind(R.id.lv_result)
    ListView lv_result;
    FileSenderAdapter mFileSenderAdapter;
    //    ScanResult mScanResult;
    List<FileSender> mFileSenderList = new ArrayList<FileSender>();
    long mTotalLen = 0;     //所有总文件的进度
    long mCurOffset = 0;    //每次传送的偏移量
    long mLastUpdateLen = 0; //每个文件传送onProgress() 之前的进度
    String[] mStorageArray = null;
    long mTotalTime = 0;
    long mCurTimeOffset = 0;
    long mLastUpdateTime = 0;
    String[] mTimeArray = null;
    int mHasSendedFileCount = 0;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //TODO 未完成 handler实现细节以及封装
            if (msg.what == MSG_UPDATE_FILE_INFO) {
                updateTotalProgressView();

                if (mFileSenderAdapter != null) mFileSenderAdapter.notifyDataSetChanged();
            }

        }
    };

    /**
     * 更新进度 和 耗时的 View
     */
    private void updateTotalProgressView() {
        try {
            //设置传送的总容量大小
            mStorageArray = FileUtils.getFileSizeArrayStr(mTotalLen);
            tv_value_storage.setText(mStorageArray[0]);
            tv_unit_storage.setText(mStorageArray[1]);

            //设置传送的时间情况
            mTimeArray = FileUtils.getTimeByArrayStr(mTotalTime);
            tv_value_time.setText(mTimeArray[0]);
            tv_unit_time.setText(mTimeArray[1]);


            //设置传送的进度条情况
            if (mHasSendedFileCount == AppContext.getAppContext().getFileInfoMap().size()) {
                pb_total.setProgress(0);
                tv_value_storage.setTextColor(getResources().getColor(R.color.color_yellow));
                tv_value_time.setTextColor(getResources().getColor(R.color.color_yellow));
                return;
            }

            long total = AppContext.getAppContext().getAllSendFileInfoSize();
            int percent = (int) (mTotalLen * 100 / total);
            pb_total.setProgress(percent);

            if (total == mTotalLen) {
                pb_total.setProgress(0);
                tv_value_storage.setTextColor(getResources().getColor(R.color.color_yellow));
                tv_value_time.setTextColor(getResources().getColor(R.color.color_yellow));
            }
        } catch (Exception e) {
            //convert storage array has some problem
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sender);

        ButterKnife.bind(this);

        init();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //需要判断是否有文件在发送？
        if (hasFileSending()) {
            showExistDialog();
            return;
        }

        finishNormal();
    }

    /**
     * 正常退出
     */
    private void finishNormal() {
//        AppContext.FILE_SENDER_EXECUTOR.
        stopAllFileSendingTask();
        AppContext.getAppContext().getFileInfoMap().clear();
        this.finish();
    }

    /**
     * 停止所有的文件发送任务
     */
    private void stopAllFileSendingTask() {
        for (FileSender fileSender : mFileSenderList) {
            if (fileSender != null) {
                fileSender.stop();
            }
        }
    }

    /**
     * 判断是否有文件在传送
     */
    private boolean hasFileSending() {
        for (FileSender fileSender : mFileSenderList) {
            if (fileSender.isRunning()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 显示是否退出 对话框
     */
    private void showExistDialog() {
//        new AlertDialog.Builder(getContext())
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getResources().getString(R.string.tip_now_has_task_is_running_exist_now))
                .setPositiveButton(getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishNormal();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.str_no), null)
                .create()
                .show();
    }

    /**
     * 初始化
     */
    private void init() {
//        mScanResult = getIntent().getParcelableExtra(Constant.KEY_SCAN_RESULT);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getResources().getString(R.string.title_file_transfer));

        pb_total.setMax(100);

        mFileSenderAdapter = new FileSenderAdapter(getContext());
        lv_result.setAdapter(mFileSenderAdapter);


//        Map<String, FileInfo> dataMap = AppContext.getAppContext().getFileInfoMap();
//        List<FileInfo> fileInfoList = new ArrayList<>(dataMap.values());
//        Collections.sort(fileInfoList, Constant.DEFAULT_COMPARATOR2);

        List<Map.Entry<String, FileInfo>> fileInfoMapList = new ArrayList<Map.Entry<String, FileInfo>>(AppContext.getAppContext().getFileInfoMap().entrySet());
        Collections.sort(fileInfoMapList, Constant.DEFAULT_COMPARATOR);

        String serverIp = WifiMgr.getInstance(getContext()).getIpAddressFromHotspot();
        for (Map.Entry<String, FileInfo> entry : fileInfoMapList) {
            final FileInfo fileInfo = entry.getValue();
            FileSender fileSender = new FileSender(getContext(), fileInfo, serverIp, Constant.DEFAULT_SERVER_PORT);
            fileSender.setOnSendListener(new FileSender.OnSendListener() {
                @Override
                public void onStart() {
                    mLastUpdateLen = 0;
                    mLastUpdateTime = System.currentTimeMillis();

                }

                @Override
                public void onProgress(long progress, long total) {
                    //TODO 更新
                    //=====更新进度 流量 时间视图 start ====//
                    mCurOffset = progress - mLastUpdateLen > 0 ? progress - mLastUpdateLen : 0;
                    mTotalLen = mTotalLen + mCurOffset;
                    mLastUpdateLen = progress;

                    mCurTimeOffset = System.currentTimeMillis() - mLastUpdateTime > 0 ? System.currentTimeMillis() - mLastUpdateTime : 0;
                    mTotalTime = mTotalTime + mCurTimeOffset;
                    mLastUpdateTime = System.currentTimeMillis();
                    //=====更新进度 流量 时间视图 end ====//

                    //更新文件传送进度的ＵＩ
                    fileInfo.setProcceed(progress);
                    AppContext.getAppContext().updateFileInfo(fileInfo);
                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }

                @Override
                public void onSuccess(FileInfo fileInfo) {
                    //=====更新进度 流量 时间视图 start ====//
                    mHasSendedFileCount++;

                    mTotalLen = mTotalLen + (fileInfo.getSize() - mLastUpdateLen);
                    mLastUpdateLen = 0;
                    mLastUpdateTime = System.currentTimeMillis();
                    //=====更新进度 流量 时间视图 end ====//

                    System.out.println(Thread.currentThread().getName());
                    //TODO 成功
                    fileInfo.setResult(FileInfo.FLAG_SUCCESS);
                    AppContext.getAppContext().updateFileInfo(fileInfo);
                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }

                @Override
                public void onFailure(Throwable t, FileInfo fileInfo) {
                    mHasSendedFileCount++;//统计发送文件
                    //TODO 失败
                    fileInfo.setResult(FileInfo.FLAG_FAILURE);
                    AppContext.getAppContext().updateFileInfo(fileInfo);
                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }
            });

            mFileSenderList.add(fileSender);
            AppContext.FILE_SENDER_EXECUTOR.execute(fileSender);
        }

//        AppContext.FILE_SENDER_EXECUTOR.execute();
    }

    @OnClick({R.id.tv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back: {
                onBackPressed();
                break;
            }
        }
    }

}
