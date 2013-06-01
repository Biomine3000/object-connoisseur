package org.biomine3000.connoisseur;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import biomine3000.objects.*;

public class ObjectFlowActivity extends FragmentActivity implements ObjectObserver {
    private static final String TAG = ObjectFlowActivity.class.getName();
    private ConnectionManager mManager = null;

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

                if (obj.getPayload().length < 200000) {
                    ImageView v = (ImageView) findViewById(R.id.image);
                    v.setImageBitmap(BitmapFactory.decodeByteArray(obj.getPayload(), 0, obj.getPayload().length));
                } else {
                    Log.i(TAG, "Not showing image of size " + obj.getPayload().length);
                }
            }
        });
    }

    protected ConnectionManager getConnectionManager() {
        return mManager;
    }

    @Override
    protected void onResume() {
        super.onResume();

        String host = getSharedPreferences(ServerSetupActivity.CONNECTION_PREFERENCES_NAME,
                0).getString(ServerSetupActivity.HOST_PREFERENCE_KEY, getResources().getString(R.string.default_host));
        int port = getSharedPreferences(ServerSetupActivity.CONNECTION_PREFERENCES_NAME,
                0).getInt(ServerSetupActivity.PORT_PREFERENCE_KEY, new Integer(getResources().getString(R.string.default_port)));

        if (mManager == null)
            mManager = new ConnectionManager();

        mManager.connect(host, port);
        mManager.addObjectObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mManager != null) {
            mManager.removeObjectObserver(this);
            mManager.disconnect();
        }
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

    @Override
    public void handleObject(BusinessObject obj) {
        if (obj instanceof ImageObject) {
            toast("Received an image...", 2);
            showImage((ImageObject) obj);
        } else if (obj instanceof PlainTextObject) {
            // toast(Biomine3000Utils.formatBusinessObject(obj), 2);
            showText(Biomine3000Utils.formatBusinessObject(obj));
        }
    }
}
