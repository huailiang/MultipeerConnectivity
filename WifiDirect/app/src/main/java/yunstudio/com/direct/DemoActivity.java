package yunstudio.com.direct;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdk.MLog;
import com.sdk.WiFiDirect;

import java.util.ArrayList;
import java.util.List;


public class DemoActivity extends AppCompatActivity implements WiFiDirect.iWifiInterface
{
    // ui
    private Button mDisconnectBtn, mSendBtn;
    private EditText mEditText;
    private TextView deviceView;
    private ListView mListView;

    ArrayAdapter<String> adapter;

    WiFiDirect mDirect;

    final String TAG = "GAME-MAIN";

    private List<String> listViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDirect = new WiFiDirect(this, this);

        mEditText = findViewById(R.id.msg_text);
        mSendBtn = findViewById(R.id.send_button);
        mSendBtn.setOnClickListener(this::onSendBtnClick);

        mDisconnectBtn = findViewById(R.id.disconnect_button);
        mDisconnectBtn.setOnClickListener(v -> mDirect.Disconnect());

        deviceView = findViewById(R.id.content_text);
        mListView = findViewById(R.id.peer_list);

        listViewData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listViewData);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this::onItemClick);

        mDirect.Initial();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mDirect.onResume();
    }

    @Override
    protected void onPause()
    {
        mDirect.onPause();
        super.onPause();
    }


    @Override
    protected void onStop()
    {
        mDirect.onStop();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.atn_direct_enable:
                if (mDirect != null) mDirect.EnableSetting();
                return true;
            case R.id.atn_direct_discover:
                if (mDirect != null) mDirect.SearchNearPeers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void UpdateDevice(String st)
    {
        deviceView.setText(st);
    }

    @Override
    public void UpdatePeers(List<WifiP2pDevice> peers)
    {
        listViewData.clear();
        for (int i = 0; i < peers.size(); i++)
        {
            WifiP2pDevice device = peers.get(i);
            String st = mDirect.getDevicesInfo(device);
            listViewData.add(device.deviceName + " " + st);
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "peers count: " + peers.size(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnDisconnect()
    {
        MLog.d(TAG, "WiFi Direct Disconnect");
    }

    @Override
    public void ReciveMsg(String msg)
    {
        MLog.d(WiFiDirect.TAG, msg);
        Toast.makeText(this, "server: " + msg, Toast.LENGTH_SHORT).show();
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        String it = listViewData.get(arg2);
        MLog.d(TAG, it);
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show();
        mDirect.Connect(arg2);
    }

    public void onSendBtnClick(View v)
    {
        String txt = mEditText.getText().toString();
        mDirect.createClientThread(txt);
        mEditText.setText("");
    }

}
