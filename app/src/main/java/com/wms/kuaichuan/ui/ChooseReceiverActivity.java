package com.wms.kuaichuan.ui;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wms.kuaichuan.AppContext;
import com.wms.kuaichuan.Constant;
import com.wms.kuaichuan.R;
import com.wms.kuaichuan.common.BaseActivity;
import com.wms.kuaichuan.core.BaseTransfer;
import com.wms.kuaichuan.core.entity.FileInfo;
import com.wms.kuaichuan.core.utils.ToastUtils;
import com.wms.kuaichuan.core.utils.WifiMgr;
import com.wms.kuaichuan.ui.adapter.WifiScanResultAdapter;
import com.wms.kuaichuan.ui.view.RadarScanView;
import com.wms.kuaichuan.utils.ListUtils;
import com.wms.kuaichuan.utils.NavigatorUtils;
import com.wms.kuaichuan.utils.NetUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
public class ChooseReceiverActivity extends BaseActivity {

    public static final int MSG_TO_FILE_SENDER_UI = 0X88;   //消息：跳转到文件发送列表UI
    public static final int MSG_TO_SHOW_SCAN_RESULT = 0X99; //消息：更新扫描可连接Wifi网络的列表
    private static final String TAG = ChooseReceiverActivity.class.getSimpleName();
//    @Bind(R.id.tab_layout)
//    TabLayout tab_layout;
//    @Bind(R.id.view_pager)
//    ViewPager view_pager;
    /**
     * Topbar相关UI
     */
    @Bind(R.id.tv_back)
    TextView tv_back;
    /**
     * 其他UI
     */
    @Bind(R.id.radarView)
    RadarScanView radarScanView;
    /**
     * 扫描结果
     */
    @Bind(R.id.lv_result)
    ListView lv_result;
    List<ScanResult> mScanResultList;
    WifiScanResultAdapter mWifiScanResultAdapter;
    /**
     * 与 文件发送方 通信的 线程
     */
    Runnable mUdpServerRuannable;
    /**
     * 开启 文件发送方 通信服务 (必须在子线程执行)
     */
    DatagramSocket mDatagramSocket;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TO_FILE_SENDER_UI) {
                ToastUtils.show(getContext(), "进入文件发送列表");
                NavigatorUtils.toFileSenderListUI(getContext());
                finishNormal();
            } else if (msg.what == MSG_TO_SHOW_SCAN_RESULT) {
                getOrUpdateWifiScanResult();
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TO_SHOW_SCAN_RESULT), 1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_receiver);

        ButterKnife.bind(this);

        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeSocket();

        //断开当前的Wifi网络
        WifiMgr.getInstance(getContext()).disconnectCurrentNetwork();

        this.finish();
    }

    /**
     * 成功进入 文件发送列表UI 调用的finishNormal()
     */
    private void finishNormal() {
        closeSocket();
        this.finish();
    }

    /**
     * 初始化
     */
    private void init() {
        radarScanView.startScan();

//        if(WifiMgr.getInstance(getContext()).isWifiEnable()){//wifi打开的情况
//        }else{//wifi关闭的情况
//            WifiMgr.getInstance(getContext()).openWifi();
//        }

        if (!WifiMgr.getInstance(getContext()).isWifiEnable()) {//wifi未打开的情况
            WifiMgr.getInstance(getContext()).openWifi();
        }


        getOrUpdateWifiScanResult();
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TO_SHOW_SCAN_RESULT), 1000);
    }

    /**
     * 获取或者更新wifi扫描列表
     */
    private void getOrUpdateWifiScanResult() {
        WifiMgr.getInstance(getContext()).startScan();
        mScanResultList = WifiMgr.getInstance(getContext()).getScanResultList();
        mScanResultList = ListUtils.filterWithNoPassword(mScanResultList);

        if (mScanResultList != null) {
            mWifiScanResultAdapter = new WifiScanResultAdapter(getContext(), mScanResultList);
            lv_result.setAdapter(mWifiScanResultAdapter);
            lv_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //TODO 进入文件传输部分
                    ScanResult scanResult = mScanResultList.get(position);
                    Log.i(TAG, "###select the wifi info ======>>>" + scanResult.toString());

                    //1.连接网络
                    String ssid = Constant.DEFAULT_SSID;
                    ssid = scanResult.SSID;
                    WifiMgr.getInstance(getContext()).openWifi();
                    WifiMgr.getInstance(getContext()).addNetwork(WifiMgr.createWifiCfg(ssid, null, WifiMgr.WIFICIPHER_NOPASS));

                    //2.发送UDP通知信息到 文件接收方 开启ServerSocketRunnable
                    mUdpServerRuannable = createSendMsgToServerRunnable(WifiMgr.getInstance(getContext()).getIpAddressFromHotspot());
                    AppContext.MAIN_EXECUTOR.execute(mUdpServerRuannable);
                }
            });
        }
    }

    @OnClick({R.id.tv_back, R.id.radarView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back: {
                onBackPressed();
                break;
            }
            case R.id.radarView: {
                Log.i(TAG, "radarView ------>>> click!");
                mUdpServerRuannable = createSendMsgToServerRunnable(WifiMgr.getInstance(getContext()).getIpAddressFromHotspot());
                AppContext.MAIN_EXECUTOR.execute(mUdpServerRuannable);
                break;
            }
        }
    }

    /**
     * 创建发送UDP消息到 文件接收方 的服务线程
     */
    private Runnable createSendMsgToServerRunnable(final String serverIP) {
        Log.i(TAG, "receiver serverIp ----->>>" + serverIP);
        return new Runnable() {
            @Override
            public void run() {
                try {
                    startFileSenderServer(serverIP, Constant.DEFAULT_SERVER_COM_PORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void startFileSenderServer(String targetIpAddr, int serverPort) throws Exception {
//        Thread.sleep(3*1000);
        // 确保Wifi连接上之后获取得到IP地址
        int count = 0;
        while (targetIpAddr.equals(Constant.DEFAULT_UNKOWN_IP) && count < Constant.DEFAULT_TRY_TIME) {
            Thread.sleep(1000);
            targetIpAddr = WifiMgr.getInstance(getContext()).getIpAddressFromHotspot();
            Log.i(TAG, "receiver serverIp ----->>>" + targetIpAddr);
            count++;
        }

        // 即使获取到连接的热点wifi的IP地址也是无法连接网络 所以采取此策略
        count = 0;
        while (!NetUtils.pingIpAddress(targetIpAddr) && count < Constant.DEFAULT_TRY_TIME) {
            Thread.sleep(500);
            Log.i(TAG, "try to ping ----->>>" + targetIpAddr + " - " + count);
            count++;
        }

        mDatagramSocket = new DatagramSocket(serverPort);
        byte[] receiveData = new byte[1024];
        byte[] sendData = null;
        InetAddress ipAddress = InetAddress.getByName(targetIpAddr);

        //0.发送 即将发送的文件列表 到文件接收方
        sendFileInfoListToFileReceiverWithUdp(serverPort, ipAddress);

        //1.发送 文件接收方 初始化
        sendData = Constant.MSG_FILE_RECEIVER_INIT.getBytes(BaseTransfer.UTF_8);
        DatagramPacket sendPacket =
                new DatagramPacket(sendData, sendData.length, ipAddress, serverPort);
        mDatagramSocket.send(sendPacket);
        Log.i(TAG, "Send Msg To FileReceiver######>>>" + Constant.MSG_FILE_RECEIVER_INIT);

//        sendFileInfoListToFileReceiverWithUdp(serverPort, ipAddress);


        //2.接收 文件接收方 初始化 反馈
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            mDatagramSocket.receive(receivePacket);
            String response = new String(receivePacket.getData(), BaseTransfer.UTF_8).trim();
            Log.i(TAG, "Get the msg from FileReceiver######>>>" + response);
            if (response != null && response.equals(Constant.MSG_FILE_RECEIVER_INIT_SUCCESS)) {
                // 进入文件发送列表界面 （并且通知文件接收方进入文件接收列表界面）
                mHandler.obtainMessage(MSG_TO_FILE_SENDER_UI).sendToTarget();
            }
        }
    }

    /**
     * 发送即将发送的文件列表到文件接收方
     */
    private void sendFileInfoListToFileReceiverWithUdp(int serverPort, InetAddress ipAddress) throws IOException {
        //1.1将发送的List<FileInfo> 发送给 文件接收方
        //如何将发送的数据列表封装成JSON
        Map<String, FileInfo> sendFileInfoMap = AppContext.getAppContext().getFileInfoMap();
        List<Map.Entry<String, FileInfo>> fileInfoMapList = new ArrayList<Map.Entry<String, FileInfo>>(sendFileInfoMap.entrySet());
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        //排序
        Collections.sort(fileInfoMapList, Constant.DEFAULT_COMPARATOR);
        for (Map.Entry<String, FileInfo> entry : fileInfoMapList) {
            if (entry.getValue() != null) {
                FileInfo fileInfo = entry.getValue();
                String fileInfoStr = FileInfo.toJsonStr(fileInfo);
                DatagramPacket sendFileInfoListPacket =
                        new DatagramPacket(fileInfoStr.getBytes(), fileInfoStr.getBytes().length, ipAddress, serverPort);
                try {
                    mDatagramSocket.send(sendFileInfoListPacket);
                    Log.i(TAG, "sendFileInfoListToFileReceiverWithUdp------>>>" + fileInfoStr + "=== Success!");
                } catch (Exception e) {
                    Log.i(TAG, "sendFileInfoListToFileReceiverWithUdp------>>>" + fileInfoStr + "=== Failure!");
                }

            }
        }
    }

    /**
     * 关闭UDP Socket 流
     */
    private void closeSocket() {
        if (mDatagramSocket != null) {
            mDatagramSocket.disconnect();
            mDatagramSocket.close();
            mDatagramSocket = null;
        }
    }

}
