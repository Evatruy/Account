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
import com.lixin.account.ucost.adapter.GridInCatAdapter;
import com.lixin.account.ucost.dao.IncomeCatDao;
import com.lixin.account.ucost.dao.IncomeDao;
import com.lixin.account.ucost.model.Income;
import com.lixin.account.ucost.model.IncomeCat;
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

public class IncomeFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnDateSetListener {

    @Bind(R.id.icon_income_cat)
    ImageView mIconIncomeCat;
    @Bind(R.id.label_income_cat)
    TextView mLabelIncomeCat;
    @Bind(R.id.et_income)
    EditText mEtIncome;
    @Bind(R.id.label_income_time)
    TextView mEtIncomeTime;
    @Bind(R.id.et_income_note)
    EditText mEtIncomeNote;
    @Bind(R.id.ll_income_cat)
    LinearLayout mLlIncomeCat;
    private static final int REQUEST_ADD_CATEGORY = 0x101;
    private static final int REQUEST_UPDATE_CATEGORY = 0x102;
    private boolean mIsUpdateCat;
    private IncomeCatDao mIncomeCatDao;
    private Date mDate;
    private Income mIncome;
    private PopupWindow mPopupWindow;
    private boolean mIsUpdateIncome;
    private GridInCatAdapter mCatAdapter;
    private Context mContext;

    com.jzxiang.pickerview.TimePickerDialog mDialogAll;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    long mTime;

    public static IncomeFragment getInstance(Income income) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constant.RECORD, income);
        IncomeFragment incomeFragment = new IncomeFragment();
        incomeFragment.setArguments(bundle);
        return incomeFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        mIncomeCatDao = new IncomeCatDao(mContext);
        mCatAdapter = new GridInCatAdapter(mContext, getCategory());
        Bundle arguments = getArguments();
        if (arguments != null) {
            //收入记录修改
            mIsUpdateIncome = true;
            mIncome = arguments.getParcelable(Constant.RECORD);
            mEtIncome.setText(String.valueOf(mIncome.getAmount()));
            mLabelIncomeCat.setText(mIncome.getCategory().getName());
            mIconIncomeCat.setImageResource(mIncome.getCategory().getImageId());
            mEtIncomeNote.setText(mIncome.getNote());
            mDate = mIncome.getDate();
        } else {
            mIsUpdateIncome = false;
            mIncome = new Income();
            mDate = new Date();
            mIncome.setDate(mDate);
            mIncome.setCategory((IncomeCat) mCatAdapter.getItem(0));
        }
        mEtIncomeTime.setText(DateUtils.date2Str(mDate));

        //绘制弹出页的界面
        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().
                inflate(R.layout.pop_category, null);
        GridView gridIncomeCat = (GridView) linearLayout.findViewById(R.id.grid_category);
        gridIncomeCat.setAdapter(mCatAdapter);
        gridIncomeCat.setOnItemClickListener(this);
        //为类别图标设置点击监听事件，点击的时候设置账目的类别
        gridIncomeCat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCatAdapter.getCloseVisibility() == View.VISIBLE) {
                    mCatAdapter.setCloseVisibility(View.GONE);
                }
                return false;
            }
        });
        //为类别图标设置长按监听事件，长按的时候进入修改类别的界面
        gridIncomeCat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                IncomeCat incomeCat = (IncomeCat) mCatAdapter.getItem(position);
                Intent intent = new Intent(mContext, CategoryAty.class);
                intent.putExtra(Constant.UPDATE_CAT, incomeCat);
                startActivityForResult(intent, REQUEST_UPDATE_CATEGORY);
                return true;
            }
        });
        //创建弹出式对话框，让用户管理收支类别
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
        return R.layout.fragment_income;
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

    private List<IncomeCat> getCategory() {
        List<IncomeCat> cats = null;
        //获取收入类别
        cats = mIncomeCatDao.getIncomeCat();
        cats.add(new IncomeCat(R.mipmap.jiahao_bai, "添加"));
        cats.add(new IncomeCat(R.mipmap.jianhao_bai, "删除"));
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
        IncomeCat incomeCat = (IncomeCat) parent.getItemAtPosition(position);
        if (incomeCat.getImageId() == R.mipmap.jiahao_bai) {
            Intent intent = new Intent(mContext, CategoryAty.class);
            intent.putExtra(Constant.TYPE_CATEGORY, Constant.TYPE_INCOME);
            startActivityForResult(intent, REQUEST_ADD_CATEGORY);
        } else if (incomeCat.getImageId() == R.mipmap.jianhao_bai) {
            mCatAdapter.setCloseVisibility(View.VISIBLE);
        } else {
            mIncome.setCategory(incomeCat);
            mIconIncomeCat.setImageResource(incomeCat.getImageId());
            mLabelIncomeCat.setText(incomeCat.getName());
            mPopupWindow.dismiss();
        }
    }

    @OnClick({R.id.label_income_time, R.id.ll_income_cat, R.id.btn_income_save})
    public void incomeClick(View view) {
        switch (view.getId()) {
            case R.id.label_income_time: {
                mDialogAll.show(getActivity().getSupportFragmentManager(), "all");
            }
            break;
            case R.id.ll_income_cat:
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                } else {
                    mPopupWindow.showAsDropDown(mLlIncomeCat);
                }
                break;
            case R.id.btn_income_save: {
                saveIncome();
            }
            break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //收入记录保存
    private void saveIncome() {
        String trim = mEtIncome.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            Toast.makeText(mContext, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        float amount = Float.parseFloat(trim);
        String note = mEtIncomeNote.getText().toString().trim();
        mIncome.setAmount(amount);
        mIncome.setNote(note);
        IncomeDao incomeDao = new IncomeDao(mContext);
        if (!mIsUpdateIncome) {
            if (incomeDao.addIncome(mIncome)) {
                T.showShort(mContext, "保存成功");
                EventBus.getDefault().post("income_inserted");
                getActivity().finish();
            } else {
                T.showShort(mContext, "保存失败");
            }
        } else {
            if (incomeDao.updateIncome(mIncome)) {
                T.showShort(mContext, "修改成功");
                EventBus.getDefault().post("income_updated");
                getActivity().finish();
            } else {
                T.showShort(mContext, "修改失败");
            }
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mTime = millseconds;
        String text = getDateToString(millseconds);
        mIncome.setDate(new Date(millseconds));
        mEtIncomeTime.setText(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sdf.format(d);
    }
}
