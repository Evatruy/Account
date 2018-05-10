package com.lixin.account.ucost.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lixin.account.ucost.R;
import com.lixin.account.ucost.activity.CategoryAty;
import com.lixin.account.ucost.adapter.GridExpCatAdapter;
import com.lixin.account.ucost.dao.ExpenseCatDao;
import com.lixin.account.ucost.dao.ExpenseDao;
import com.lixin.account.ucost.model.Expense;
import com.lixin.account.ucost.model.ExpenseCat;
import com.lixin.account.ucost.utils.Constant;
import com.lixin.account.ucost.utils.DateUtils;
import com.lixin.account.ucost.utils.T;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Lixin on 2018/3/5
 */
public class ExpenseFragment extends BaseFragment implements AdapterView.OnItemClickListener , OnDateSetListener {

    @Bind(R.id.icon_expense_cat)
    ImageView mIconExpenseCat;
    @Bind(R.id.label_expense_cat)
    TextView mLabelExpenseCat;
    @Bind(R.id.et_expense)
    EditText mEtExpense;
    @Bind(R.id.label_expense_time)
    TextView mLabelExpenseTime;
    @Bind(R.id.et_expense_note)
    EditText mEtExpenseNote;
    @Bind(R.id.ll_expense_cat)
    LinearLayout mLlExpenseCat;
    private ExpenseCatDao mExpenseCatDao;
    private static final int REQUEST_ADD_CATEGORY = 0x201;
    private static final int REQUEST_UPDATE_CATEGORY = 0x202;
    private boolean mIsUpdateExpense;
    private boolean mIsUpdateCat;
    private Expense mExpense;
    private Date mDate;
    private GridExpCatAdapter mCatAdapter;
    private PopupWindow mPopupWindow;
    private Context mContext;

    com.jzxiang.pickerview.TimePickerDialog mDialogAll;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    long mTime;

    public static ExpenseFragment getInstance(Expense expense) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.RECORD, expense);
        ExpenseFragment expenseFragment = new ExpenseFragment();
        expenseFragment.setArguments(bundle);
        return expenseFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mExpenseCatDao = new ExpenseCatDao(mContext);
        mCatAdapter = new GridExpCatAdapter(mContext, getCategory());
        Bundle arguments = getArguments();
        if (arguments != null) {
            mIsUpdateExpense = true;
            mExpense = arguments.getParcelable(Constant.RECORD);
            mEtExpenseNote.setText(mExpense.getNote());
            mEtExpense.setText(String.valueOf(mExpense.getAmount()));
            mIconExpenseCat.setImageResource(mExpense.getCategory().getImageId());
            mLabelExpenseCat.setText(mExpense.getCategory().getName());
            mDate = mExpense.getDate();
        } else {
            mIsUpdateExpense = false;
            mDate = new Date();
            mExpense = new Expense();
            mExpense.setDate(mDate);
            mExpense.setCategory((ExpenseCat) mCatAdapter.getItem(0));
        }
        mLabelExpenseTime.setText(DateUtils.date2Str(mDate));
        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.pop_category, null);
        GridView gridExpenseCat = (GridView) linearLayout.findViewById(R.id.grid_category);
        gridExpenseCat.setOnItemClickListener(this);
        gridExpenseCat.setAdapter(mCatAdapter);
        gridExpenseCat.setOnItemClickListener(this);
        gridExpenseCat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCatAdapter.getCloseVisibility() == View.VISIBLE) {
                    mCatAdapter.setCloseVisibility(View.GONE);
                }
                return false;
            }
        });
        gridExpenseCat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ExpenseCat expenseCat = (ExpenseCat) mCatAdapter.getItem(position);
                Intent intent = new Intent(mContext, CategoryAty.class);
                intent.putExtra(Constant.UPDATE_CAT, expenseCat);
                startActivityForResult(intent, REQUEST_UPDATE_CATEGORY);
                return true;
            }
        });
        mPopupWindow = new PopupWindow(linearLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        mPopupWindow.setAnimationStyle(R.style.popwindow_anim_style);

        //时间选择控件
        long twoYears = 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMaxMillseconds(System.currentTimeMillis() + twoYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(com.jzxiang.pickerview.data.Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

        return view;
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_expense;
    }

    @Override
    protected Fragment getSubFragment() {
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mIsUpdateCat && mCatAdapter != null) {
            mCatAdapter.setData(getCategory());
        }
    }

    private List<ExpenseCat> getCategory() {
        List<ExpenseCat> cats = null;
        cats = mExpenseCatDao.getExpenseCat();
        cats.add(new ExpenseCat(R.mipmap.jiahao_bai, "添加"));
        cats.add(new ExpenseCat(R.mipmap.jianhao_bai, "删除"));
        return cats;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mIsUpdateCat = requestCode == REQUEST_ADD_CATEGORY || requestCode == REQUEST_UPDATE_CATEGORY;
        } else {
            mIsUpdateCat = false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ExpenseCat expenseCat = (ExpenseCat) parent.getItemAtPosition(position);
        if (expenseCat.getImageId() == R.mipmap.jiahao_bai) {
            Intent intent = new Intent(mContext, CategoryAty.class);
            intent.putExtra(Constant.TYPE_CATEGORY, Constant.TYPE_EXPENSE);
            startActivityForResult(intent, REQUEST_ADD_CATEGORY);
        } else if (expenseCat.getImageId() == R.mipmap.jianhao_bai) {
            mCatAdapter.setCloseVisibility(View.VISIBLE);
        } else {
            mExpense.setCategory(expenseCat);
            mIconExpenseCat.setImageResource(expenseCat.getImageId());
            mLabelExpenseCat.setText(expenseCat.getName());
            mPopupWindow.dismiss();
        }
    }

    @OnClick({R.id.ll_expense_cat, R.id.label_expense_time, R.id.btn_expense_save})
    public void expenseClick(View view) {
        switch (view.getId()) {
            case R.id.ll_expense_cat: {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.showAsDropDown(mLlExpenseCat);
                }
            }
            break;
            case R.id.label_expense_time: {
                mDialogAll.show(getActivity().getSupportFragmentManager(), "all");
            }
            break;
            case R.id.btn_expense_save:
                saveExpense();
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void saveExpense() {
        String trim = mEtExpense.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            Toast.makeText(mContext, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        float amount = Float.parseFloat(trim);
        String note = mEtExpenseNote.getText().toString().trim();
        mExpense.setAmount(amount);
        mExpense.setNote(note);
        ExpenseDao expenseDao = new ExpenseDao(mContext);
        if (mIsUpdateExpense) {
            if (expenseDao.updateExpense(mExpense)) {
                T.showShort(mContext, "修改成功");
                EventBus.getDefault().post("expense_updated");
                getActivity().finish();
            } else {
                T.showShort(mContext, "修改失败");
            }
        } else {
            if (expenseDao.addExpense(mExpense)) {
                T.showShort(mContext, "保存成功");
                EventBus.getDefault().post("expense_inserted");
                getActivity().finish();
            } else {
                T.showShort(mContext, "保存失败");
            }
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mTime = millseconds;
        String text = getDateToString(millseconds);
        mExpense.setDate(new Date(millseconds));
        mLabelExpenseTime.setText(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sdf.format(d);
    }
}
