package org.biomine3000.connoisseur;

import android.util.Log;
import util.dbg.ILogger;

public class AndroidLoggerAdapter implements ILogger {
    private final String mTag;

    public AndroidLoggerAdapter(final String tag) {
        mTag = tag;
    }

    @Override
    public void dbg(String msg) {
        Log.d(mTag, msg);
    }

    @Override
    public void info(String msg) {
        Log.i(mTag, msg);
    }

    @Override
    public void warning(String warning) {
        Log.w(mTag, warning);
    }

    @Override
    public void error(String msg) {
        Log.e(mTag, msg);
    }

    @Override
    public void error(Exception e) {
        Log.e(mTag, e.getMessage(), e);
    }

    @Override
    public void error(String msg, Exception e) {
        Log.e(mTag, msg, e);
    }

    @Override
    public void closeStreams() {
        // NOP
    }
}
