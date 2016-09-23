package com.roto.industrial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;

import com.roto.industrial.base.BaseActivity;
import com.roto.industrial.utils.BaseHandler;

public class SplashScreenActivity extends BaseActivity {
    private final BaseHandler handler = new BaseHandler(this) {
        public void handleRealMessage(android.os.Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("industrial://login"));
                startActivity(intent);
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        AlphaAnimation animation = new AlphaAnimation(0.7f, 1.0f);
        animation.setDuration(1500);
        findViewById(R.id.screen).startAnimation(animation);
        handler.sendEmptyMessageDelayed(1, 2000);
    }
}
