package org.biomine3000.connoisseur;

import android.app.Activity;
import android.view.View;
import com.markupartist.android.widget.ActionBar;

public class RunnableAction extends ActionBar.AbstractAction {
    private final Runnable mRunnable;
    private final Activity mActivity;

    public RunnableAction(Activity activity, int drawable, Runnable runnable) {
        super(drawable);

        mActivity = activity;
        mRunnable = runnable;
    }

    @Override
    public void performAction(View view) {
        mActivity.runOnUiThread(mRunnable);
    }
}
