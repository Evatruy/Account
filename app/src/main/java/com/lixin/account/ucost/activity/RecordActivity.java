package com.lixin.account.ucost.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.lixin.account.ucost.R;
import com.lixin.account.ucost.adapter.PagerAdapter;
import com.lixin.account.ucost.fragment.ExpenseFragment;
import com.lixin.account.ucost.fragment.IncomeFragment;
import com.lixin.account.ucost.model.Expense;
import com.lixin.account.ucost.model.Income;
import com.lixin.account.ucost.utils.Constant;
import com.lixin.account.ucost.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class RecordActivity extends BaseActivity{

    @Bind(R.id.record_tab_strip)
    PagerSlidingTabStrip mRecordTabStrip;
    @Bind(R.id.pager_record)
    ViewPager mPagerRecord;
    private FragmentManager mFragmentManager;
    private IncomeFragment mIncomeFragment;
    private ExpenseFragment mExpenseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setTitle(getString(R.string.record_accout));
        showBackwardView(true);
        mFragmentManager = getSupportFragmentManager();
        Parcelable extra = getIntent().getParcelableExtra(Constant.RECORD);
        int index = 0;//收支滑动切换
        //判断为修改操作或新增操作
        if (extra != null) {
            if (extra instanceof Income) {
                //收入记录修改
                mIncomeFragment = IncomeFragment.getInstance((Income) extra);
                mExpenseFragment = new ExpenseFragment();
            } else if (extra instanceof Expense) {
                //支出记录修改
                mExpenseFragment = ExpenseFragment.getInstance((Expense) extra);
                mIncomeFragment = new IncomeFragment();
                index = 1;
            }
        } else {
            mExpenseFragment = new ExpenseFragment();
            mIncomeFragment = new IncomeFragment();
        }
        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(mIncomeFragment);
        fragments.add(mExpenseFragment);
        mPagerRecord.setAdapter(new PagerAdapter(mFragmentManager, fragments));
        mPagerRecord.setCurrentItem(index);
        mRecordTabStrip.setViewPager(mPagerRecord);
        mRecordTabStrip.setTextSize(ScreenUtils.dp2sp(this, 16));
    }

    @Override
    protected Activity getSubActivity() {
        return this;
    }

}
