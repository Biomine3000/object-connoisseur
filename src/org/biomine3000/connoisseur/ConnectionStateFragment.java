package org.biomine3000.connoisseur;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ConnectionStateFragment extends Fragment implements ConnectionStateObserver {
    private static final String TAG = ConnectionStateFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.connection_state_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() != null && (getActivity() instanceof ObjectFlowActivity)) {
            ObjectFlowActivity o = (ObjectFlowActivity) getActivity();

            if (o.getConnectionManager() != null)
                    o.getConnectionManager().addConnectionStateObserver(this);
        }
    }

    @Override
    public void onPause() {
        if (getActivity() != null && (getActivity() instanceof ObjectFlowActivity)) {
            ObjectFlowActivity o = (ObjectFlowActivity) getActivity();

            if (o.getConnectionManager() != null)
                    o.getConnectionManager().removeConnectionStateObserver(this);
        }

        super.onPause();
    }

    @Override
    public void connected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getTextView().setText(R.string.connection_state_connected);
            }
        });
    }

    @Override
    public void disconnected() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getTextView().setText(R.string.connection_state_disconnected);
            }
        });
    }

    @Override
    public void error(final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getTextView().setText(getString(R.string.connection_state_error) + ": " + e.getMessage());
            }
        });
    }

    private TextView getTextView() {
        return (TextView) getView().findViewById(R.id.connection_state_text);
    }
}
