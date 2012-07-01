package org.biomine3000;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {
    public static final String TAG = StartActivity.class.getName();

    public static final String CONNECTION_PREFERENCES_NAME = "ConnectionPreferences";
    public static final String HOST_PREFERENCE_KEY = "host";
    public static final String PORT_PREFERENCE_KEY = "port";

    private String mHost;
    private int mPort;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        populateHostAndPort();

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = StartActivity.this.processSettings();

                if (!valid) {
                    Toast.makeText(StartActivity.this, R.string.invalid_host_or_port, 2).show();
                    return;
                }

                Toast.makeText(StartActivity.this, mHost + ":" + mPort, 2).show();
            }
        });
    }

    private void populateHostAndPort() {
        SharedPreferences preferences = getSharedPreferences(CONNECTION_PREFERENCES_NAME, 0);
        mHost = preferences.getString(HOST_PREFERENCE_KEY, getResources().getString(R.string.default_host));
        mPort = preferences.getInt(PORT_PREFERENCE_KEY, new Integer(getResources().getString(R.string.default_port)));
    }

    private boolean processSettings() {
        String host = ((TextView) findViewById(R.id.host_input)).getText().toString();
        int port = 0;
        try {
            port = new Integer(((TextView) findViewById(R.id.port_input)).getText().toString());
        } catch (NumberFormatException e) {
            Log.w(TAG, "Error parsing port", e);
        }

        if (host != null && host.length() > 0 && port != 0) {
            mHost = host;
            mPort = port;
            return true;
        }

        return false;
    }
}
