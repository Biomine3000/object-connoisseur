package org.biomine3000;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    private void showText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout items = (LinearLayout) findViewById(R.id.flow_layout);
                TextView v = new TextView(ObjectFlowActivity.this);
                v.setText(text);
                items.addView(v, 0);
            }
        });
    }

    private void showImage(final ImageObject obj) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout items = (LinearLayout) findViewById(R.id.flow_layout);

                if (!obj.getMetaData().getType().contains("png"))
                    return;

                ImageView v = new ImageView(ObjectFlowActivity.this);
                v.setImageBitmap(BitmapFactory.decodeByteArray(obj.getPayload(), 0, obj.getPayload().length));

                items.addView(v, 0);
            }
        });
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
        if (obj instanceof ImageObject) {
            toast("Received an image...", 2);
            showImage((ImageObject)obj);
        } else if (obj instanceof PlainTextObject) {
            // toast(Biomine3000Utils.formatBusinessObject(obj), 2);
            showText(Biomine3000Utils.formatBusinessObject(obj));
        }
    }

    @Override
    public void connectionTerminated() {
        Log.w(TAG, "Connection terminated");
        disconnect();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            e1.printStackTrace();
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
            toast(R.string.connection_created, 2);

            mConnection.init(this);
            toast(R.string.connection_initialized, 2);
        } catch (final IOException e) {
            toast(e, 3);
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

    private void toast(final Exception e, final int secs) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ObjectFlowActivity.this, getResources().getText(R.string.couldnt_connect) + "(" + e.toString() + ":" + e.getMessage() + ")", secs).show();
            }
        });
    }

    private void toast(final String text, final int secs) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ObjectFlowActivity.this, text, secs).show();
            }
        });
    }

    private void toast(final int resourceId, final int secs) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ObjectFlowActivity.this, resourceId, secs).show();
            }
        });
    }
}
