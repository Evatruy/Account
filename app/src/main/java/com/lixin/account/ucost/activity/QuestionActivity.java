package com.lixin.account.ucost.activity;

import android.app.Activity;
import android.os.Bundle;

import com.lixin.account.ucost.R;

public class QuestionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        setTitle(getString(R.string.common_question));
        showBackwardView(true);
    }

    @Override
    protected Activity getSubActivity() {
        return this;
    }
}
