package org.biomine3000;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import biomine3000.objects.*;
import util.dbg.ILogger;
import util.dbg.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ObjectFlowActivity extends Activity implements ABBOEConnection.BusinessObjectHandler {
    private static final String TAG = ObjectFlowActivity.class.getName();
    private ABBOEConnection mConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_flow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnect();
    }

    @Override
    public void handleObject(final BusinessObject obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ObjectFlowActivity.this, obj.toString(), 2).show();
            }
        });

    }

    @Override
    public void connectionTerminated() {
        Log.w(TAG, "Connection terminated");
        disconnect();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        connect();
    }

    @Override
    public void connectionTerminated(Exception e) {
        Log.w(TAG, "Connection terminated", e);
        disconnect();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        connect();
    }

    /**
     * Implements internal functionality related to establishing the connection.
     */
    private void connect() {
        try {
            ClientParameters parameters = new ClientParameters(TAG, ClientReceiveMode.ALL, Subscriptions.ALL, false);
            ILogger logger = new Logger.ILoggerAdapter("BiomineTV: ");

            String host = getSharedPreferences(StartActivity.CONNECTION_PREFERENCES_NAME,
                    0).getString(StartActivity.HOST_PREFERENCE_KEY, getResources().getString(R.string.default_host));
            int port = getSharedPreferences(StartActivity.CONNECTION_PREFERENCES_NAME,
                    0).getInt(StartActivity.PORT_PREFERENCE_KEY, new Integer(getResources().getString(R.string.default_port)));

            mConnection = new ABBOEConnection(parameters, new Socket(host, port), logger);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ObjectFlowActivity.this, R.string.connection_created, 2).show();
                }
            });

            mConnection.init(this);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ObjectFlowActivity.this, R.string.connection_initialized, 4).show();
                }
            });

        } catch (final IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ObjectFlowActivity.this, getResources().getText(R.string.couldnt_connect) + "(" + e.toString() + ":" + e.getMessage() + ")" , 4).show();
                }
            });

            Log.e(TAG, "Couldn't connect!", e);
        }
    }

    /**
     * Implements internal functionality related to closing the connection.
     */
    private void disconnect() {
        if (mConnection != null)
            mConnection.initiateShutdown();
    }
}
