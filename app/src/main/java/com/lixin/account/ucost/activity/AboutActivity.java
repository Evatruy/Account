package com.lixin.account.ucost.activity;

import android.app.Activity;
import android.os.Bundle;

import com.lixin.account.ucost.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getString(R.string.about_us));
        showBackwardView(true);
    }

    @Override
    protected Activity getSubActivity() {
        return this;
    }
}
