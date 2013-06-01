package org.biomine3000.connoisseur;

public interface ConnectionProvider {
    public void connect(String host, int port);
    public void disconnect();

    public void addConnectionStateObserver(ConnectionStateObserver observer);
    public void removeConnectionStateObserver(ConnectionStateObserver observer);
}
