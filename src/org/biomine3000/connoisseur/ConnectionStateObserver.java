package org.biomine3000.connoisseur;

import biomine3000.objects.BusinessObject;

import java.io.IOException;

public interface ConnectionStateObserver {
    void connected();
    void disconnected();
    void error(Exception e);
}
