package com.xing.xprogressviewsample;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.xing.xbase.ActivityBase;
import com.xing.xprogressview.XProgressView;

public class MainActivity extends ActivityBase {
    XProgressView xpv;
    TextView tv1;

    @Override
    protected void initView() {
        setTitle(R.string.app_name);
        toggleTitleBarLeftVisible(false);
        setContentView(R.layout.activity_main);
        xpv = getViewById(R.id.pv);
        tv1 = getViewById(R.id.tv1);
    }

    @Override
    protected void initLinster() {
        xpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (xpv.state == XProgressView.State.Start.ordinal()) {
                    new DownLoadSigTask().execute();
                    toast("Start");
                } else if (xpv.state == XProgressView.State.Run.ordinal()) {
                    toast("Run");
                } else if (xpv.state == XProgressView.State.Complete.ordinal()) {
                    toast("Complete");
                }
            }
        });
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xpv.reset();
            }
        });
    }

    class DownLoadSigTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(final String... args) {
            for (int i = 0; i <= 100; i++) {
                SystemClock.sleep(20);
                publishProgress(i);
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            xpv.setupprogress(progress[0]);
            if (progress[0] == 100) {
                xpv.complete();
            }
        }
    }
}
