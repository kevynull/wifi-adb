package pro.fren.adb;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import pro.fren.adb.utils.ADB;
import pro.fren.adb.utils.Root;
import pro.fren.adb.utils.Wifi;


public class MainActivity extends Activity {

    private boolean prefWifiOn = true;
    private boolean isWorking = false;

    private Switch aSwitch;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();//加载数据
        setViews();//设置UI显示

        new RootCheckTask().execute();//检查root任务状态

        boolean isWifiConnected = Wifi.isConnected(this);//链接wifi
        if(!isWifiConnected && prefWifiOn) {
            Wifi.saveInitialWifiState(this, isWifiConnected);
            Wifi.setEnabled(this, true);
        }

        updateUI();//更新UI
    }

    /**
     *
     */
    private void loadData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefWifiOn = prefs.getBoolean(getString(R.string.pref_wifi_on_key), prefWifiOn);
    }

    /**
     * 设置UI显示
     */
    private void setViews() {
        textView = (TextView)findViewById(R.id.tvConnect);
        aSwitch = (Switch)findViewById(R.id.swhSwitch);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                }
                if(!isWorking)
                    toggleAdb();
            }
        });
    }

    /**
     * 更新UI显示
     */
    private void updateUI() {
        boolean flag = ADB.isEnabled(this);
        aSwitch.setChecked(flag);
        if(flag) {
            String completeIp = Wifi.getIp(this);
                    textView.setText(String.format(getString(R.string.main_command), completeIp));
        } else {
            textView.setText("");
        }
    }

    /**
     * 切换启动ADB
     */
    private void toggleAdb() {
        if(!Wifi.isConnected(this))
            wifiOffToast();
        else {
            ADB.isEnabled(this);
            new ADBTask().execute();
        }
    }

    private void wifiOffToast() {
        Wifi.setEnabled(MainActivity.this, true);
        Toast.makeText(MainActivity.this,"没有接入WIFI网络，系统自动连接，请重试！",Toast.LENGTH_SHORT).show();
    }

    private class ADBTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            isWorking = true;
            ADB.toggle(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            updateUI();
            isWorking = false;
        }
    }

    private class RootCheckTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            return Root.hasRootPermission();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(!result){
                Toast.makeText(MainActivity.this,"没有ROOT权限",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
