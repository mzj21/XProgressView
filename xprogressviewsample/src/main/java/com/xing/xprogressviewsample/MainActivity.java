package com.xing.xprogressviewsample;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.xing.xbase.ActivityBase;
import com.xing.xbase.util.LogUtil;
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
        LogUtil.init(true);
    }

    @Override
    protected void initLinster() {
        xpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xpv.wait_start();
                new WaitSigTask().execute();
            }
        });
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xpv.reset();
            }
        });
    }

    class WaitSigTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(final String... args) {
            for (int i = 0; i <= 100; i++) {
                SystemClock.sleep(30);
                publishProgress(i);
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            if (progress[0] == 100) {
                new DownLoadSigTask().execute();
            }
        }
    }

    class DownLoadSigTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(final String... args) {
            for (int i = 0; i <= 100; i++) {
                SystemClock.sleep(30);
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