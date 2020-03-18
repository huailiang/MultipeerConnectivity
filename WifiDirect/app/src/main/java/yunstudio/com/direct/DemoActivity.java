package yunstudio.com.direct;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private Button mDiscoverButton;
    private Button mDisconnectButton;
    TextView deviceView;
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        mDiscoverButton = findViewById(R.id.discover_button);
        mDiscoverButton.setOnClickListener(v -> mDirect.SearchNearPeers());

        mDisconnectButton = findViewById(R.id.disconnect_button);
        mDisconnectButton.setOnClickListener(v -> mDirect.Disconnect());

        deviceView = findViewById(R.id.content_text);
        mListView = findViewById(R.id.peer_list);

        listViewData = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listViewData);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this::onItemClick);

        mDirect.Initial();
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
                if (mDirect != null)
                {
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                else
                {
                    Log.e(TAG, "channel or manager is null");
                }
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
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        String it = listViewData.get(arg2);
        MLog.d(TAG, it);
        Toast.makeText(this, it, Toast.LENGTH_SHORT).show();
        mDirect.Connect(arg2);
    }

}
