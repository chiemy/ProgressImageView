package com.chiemy.widget.progressimageview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import java.lang.ref.WeakReference;

public class MainActivity extends ActionBarActivity {
    private ProgressImageView progressImageView;
    private ProgressRunnable runnable;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressImageView = (ProgressImageView) findViewById(R.id.progressIv);
        runnable = new ProgressRunnable(this);
        handler = new Handler();
        updateProgress();
    }

    private int progress;
    private void updateProgress(){
        progressImageView.setProgress(progress++);
        handler.postDelayed(runnable, 500);
    }

    private static class ProgressRunnable implements Runnable {
        private WeakReference<MainActivity> ref;
        public ProgressRunnable(MainActivity act){
            ref = new WeakReference<>(act);
        }
        @Override
        public void run() {
            MainActivity act = ref.get();
            if(act != null){
                act.updateProgress();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
