package org.biomine3000.connoisseur;

import android.util.Log;
import biomine3000.objects.*;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

public class ConnectionManager implements ABBOEConnection.BusinessObjectHandler, ConnectionProvider, ObjectSource {
    private static final String TAG = ConnectionManager.class.getName();
    private HashSet<ConnectionStateObserver> mStateObservers = new HashSet<ConnectionStateObserver>();
    private HashSet<ObjectObserver> mObjectObservers = new HashSet<ObjectObserver>();
    private ABBOEConnection mConnection;
    private Exception mLastError;

    private enum ConnectionState { UNKNOWN, CONNECTED, DISCONNECTED, ERROR };
    private ConnectionState mConnectionState = ConnectionState.UNKNOWN;

    @Override
    public void handleObject(final BusinessObject obj) {
        for (ObjectObserver l : mObjectObservers)
            l.handleObject(obj);
    }

    @Override
    public void connectionTerminated() {
        Log.w(TAG, "Connection terminated");

        for (ConnectionStateObserver c : mStateObservers)
            c.disconnected();

        mConnectionState = ConnectionState.DISCONNECTED;
    }

    @Override
    public void connectionTerminated(Exception e) {
        Log.w(TAG, "Connection terminated", e);

        for (ConnectionStateObserver c : mStateObservers)
            c.disconnected();

        mConnectionState = ConnectionState.DISCONNECTED;
    }

    @Override
    public void connect(String host, int port) {
        try {
            ClientParameters parameters = new ClientParameters(TAG, ClientReceiveMode.ALL, Subscriptions.ALL, false);
            mConnection = new ABBOEConnection(parameters, new Socket(host, port), new AndroidLoggerAdapter(TAG));
            mConnection.init(this);
            connected();
            mConnectionState = ConnectionState.CONNECTED;
        } catch (final IOException e) {
            Log.e(TAG, "Couldn't connect!", e);
            error(e);
            disconnected();
            mConnectionState = ConnectionState.DISCONNECTED;
        }
    }

    private void disconnected() {
        for (ConnectionStateObserver c : mStateObservers)
            c.disconnected();
    }

    private void error(Exception e) {
        for (ConnectionStateObserver c : mStateObservers)
            c.error(e);

        mLastError = e;
    }

    private void connected() {
        for (ConnectionStateObserver c : mStateObservers)
            c.connected();
    }

    @Override
    public void disconnect() {
        if (mConnection != null)
            mConnection.initiateShutdown();
    }

    @Override
    public void addConnectionStateObserver(ConnectionStateObserver observer) {
        mStateObservers.add(observer);

        if (mConnectionState != ConnectionState.UNKNOWN)
            switch (mConnectionState) {
                case DISCONNECTED:
                    observer.disconnected();
                    break;
                case CONNECTED:
                    observer.connected();
                    break;
                case ERROR:
                    observer.error(mLastError);
                    break;
            }
    }

    @Override
    public void removeConnectionStateObserver(ConnectionStateObserver observer) {
        mStateObservers.remove(observer);
    }

    @Override
    public void addObjectObserver(ObjectObserver observer) {
        mObjectObservers.add(observer);
    }

    @Override
    public void removeObserver(ObjectObserver observer) {
        mObjectObservers.remove(observer);
    }
}
