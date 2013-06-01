package org.biomine3000.connoisseur;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ServerSetupActivity extends Activity {
    public static final String TAG = ServerSetupActivity.class.getName();

    public static final String CONNECTION_PREFERENCES_NAME = "ConnectionPreferences";
    public static final String HOST_PREFERENCE_KEY = "host";
    public static final String PORT_PREFERENCE_KEY = "port";

    private String mHost;
    private int mPort;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);

        populateHostAndPort();

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = ServerSetupActivity.this.processSettings();

                if (!valid) {
                    Toast.makeText(ServerSetupActivity.this, R.string.invalid_host_or_port, 2).show();
                    return;
                }

                savePreferences();
                Toast.makeText(ServerSetupActivity.this, mHost + ":" + mPort, 2).show();
                startConnoisseur();
            }
        });
    }

    private void startConnoisseur() {
        Intent intent = new Intent(this, ObjectFlowActivity.class);
        startActivity(intent);
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

    private void savePreferences() {
        SharedPreferences preferences = getSharedPreferences(CONNECTION_PREFERENCES_NAME, 0);
        SharedPreferences.Editor e = preferences.edit();

        e.putString(HOST_PREFERENCE_KEY, mHost);
        e.putInt(PORT_PREFERENCE_KEY, mPort);

        e.commit();
    }
}
