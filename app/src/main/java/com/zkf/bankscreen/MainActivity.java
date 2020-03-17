package com.zkf.bankscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.zkf.bankscreen.network.ApiParams;
import com.zkf.bankscreen.network.ApiRequestTag;
import com.zkf.bankscreen.network.NetRequest;
import com.zkf.bankscreen.network.NetRequestResultListener;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        postMacToServer();
    }



    private void postMacToServer(){
        String url = ApiParams.API_HOST + "/dapingApp/cesip.action";
        Map<String, String> params = new HashMap<>();
        String mac = getLocalMacAddress(this);
        if (!TextUtils.isEmpty(mac)){
            params.put("ip", mac);
        }
        Log.d("zkf","mac:" + mac);
        NetRequest.request(url, ApiRequestTag.DATA, params, new NetRequestResultListener() {
            @Override
            public void requestSuccess(int tag, String successResult) {
                Log.d("zkf","successResult:" + successResult);

            }

            @Override
            public void requestFailure(int tag, int code, String msg) {
                Log.d("zkf","successResult:" + msg);

            }
        });
    }


    public static String getLocalMacAddress(Context context) {
        String mac;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mac = getMachineHardwareAddress();
        } else {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress().replace(":", "");
        }

        return mac;
    }

    /**
     * 获取设备的mac地址和IP地址（android6.0以上专用）
     * @return
     */
    public static String getMachineHardwareAddress(){
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if(hardWareAddress == null) continue;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if(iF != null && iF.getName().equals("wlan0")){
            hardWareAddress = hardWareAddress.replace(":","");
        }
        return hardWareAddress ;
    }

    /***
     * byte转为String
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes){
        if (bytes == null || bytes.length == 0) {
            return null ;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

}
