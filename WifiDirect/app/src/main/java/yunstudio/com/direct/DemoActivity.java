package yunstudio.com.direct;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;


public class DemoActivity extends AppCompatActivity
{
    //ui
    private Button mDiscoverButton;
    private Button mPlayButton;
    private Button mGroupButton;
    private Button mDisconnectButton;

    final String TAG = "GAME-MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        mDiscoverButton = findViewById(R.id.discover_button);
        mDiscoverButton.setOnClickListener(v -> Log.d(TAG,"discover btn click"));

        mGroupButton = findViewById(R.id.group_button);
        mGroupButton.setOnClickListener(v -> Log.d(TAG,"group btn click"));

        mDisconnectButton = findViewById(R.id.disconnect_button);
        mDisconnectButton.setOnClickListener(v -> Log.d(TAG,"disconnect btn click"));

        mPlayButton = findViewById(R.id.play_button);
        mPlayButton.setOnClickListener(v -> Log.d(TAG,"play btn click"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
