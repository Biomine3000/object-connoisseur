package org.biomine3000.connoisseur;

public interface ObjectSource {
    public void addObjectObserver(ObjectObserver observer);
    public void removeObjectObserver(ObjectObserver observer);
}
