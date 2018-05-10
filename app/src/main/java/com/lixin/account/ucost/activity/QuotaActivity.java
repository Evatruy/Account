package com.lixin.account.ucost.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.lixin.account.ucost.R;
import com.lixin.account.ucost.dao.ExpenseDao;
import com.lixin.account.ucost.dao.IncomeDao;
import com.lixin.account.ucost.model.Expense;
import com.lixin.account.ucost.model.Income;
import com.lixin.account.ucost.utils.DateUtils;
import com.lixin.account.ucost.utils.ExcelUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class QuotaActivity extends BaseActivity implements
        DatePickerDialog.OnDateSetListener{

    @Bind(R.id.label_expense_date_chart)
    TextView mLabelExpenseDateChart;

    private ExpenseDao mExpenseDao;
    private List<Expense> mExpenses;

    private IncomeDao mIncomeDao;
    private List<Income> mIncomes;

    private int mMonth;
    private int mYear;

    private Date startTime;
    private Date endTime;

    private String excelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quota);
        setTitle(getString(R.string.excel_export));
        showBackwardView(true);
    }

    @Override
    protected Activity getSubActivity() {
        return this;
    }

    @Override
    public void onResume() {
        super.onResume();
        Calendar instance = Calendar.getInstance();
        mMonth = instance.get(Calendar.MONTH);
        mYear = instance.get(Calendar.YEAR);
        updateData(DateUtils.getMonthStart(mYear, mMonth), DateUtils.getMonthEnd(mYear, mMonth));//默认当月

        DatePickerDialog dpd = (DatePickerDialog) this.getFragmentManager().findFragmentByTag("DatePickerDialog");
        if (dpd != null) dpd.setOnDateSetListener(this);

        mExpenseDao = new ExpenseDao(this);
        mIncomeDao = new IncomeDao(this);
        startTime = DateUtils.getMonthStart();//初始化导出时间，默认当月
        endTime = DateUtils.getMonthEnd();
    }

    @OnClick({R.id.icon_date_left, R.id.label_expense_date_chart, R.id.icon_date_right, R.id.btn_excel_data})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_date_left: {
                mMonth--;
                if (mMonth < 0) {
                    mMonth = mMonth + 12;
                    if ((mMonth + 1) % 12 == 0) {
                        mYear--;
                    }
                }
                updateData(DateUtils.getMonthStart(mYear, mMonth), DateUtils.getMonthEnd(mYear, mMonth));
            }
            break;
            case R.id.label_expense_date_chart: {
                DatePickerDialog dpd = DatePickerDialog.newInstance(this, mYear, mMonth, 1);
                dpd.setStartTitle("开始日期");
                dpd.setEndTitle("结束日期");
                dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dpd.show(this.getFragmentManager(), "DatePickerDialog");
            }
            break;
            case R.id.icon_date_right: {
                Calendar calendar = Calendar.getInstance();
                if (mYear == calendar.get(Calendar.YEAR) && mMonth >= calendar.get(Calendar.MONTH)) {
                    return;
                }
                mMonth++;
                if (mMonth > 11) {
                    mMonth = mMonth - 12;
                    mYear++;
                }
                updateData(DateUtils.getMonthStart(mYear, mMonth), DateUtils.getMonthEnd(mYear, mMonth));
            }
            break;
            case R.id.btn_excel_data:
                //导出数据
                mExpenses = mExpenseDao.getPeriodExpense(startTime, endTime);
                mIncomes = mIncomeDao.getPeriodIncomes(startTime, endTime);

                if(mExpenses.size() == 0 && mIncomes.size() == 0){
                    Toast.makeText(this, "所选时间暂无数据！", Toast.LENGTH_LONG).show();
                }else{
                    try {
                        ExcelUtil.writeExcel(this, mExpenses, mIncomes, excelName + "收支表");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, year);
        start.set(Calendar.MONTH, monthOfYear);
        start.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, yearEnd);
        end.set(Calendar.MONTH, monthOfYearEnd);
        end.set(Calendar.DAY_OF_MONTH, dayOfMonthEnd);
        updateData(start.getTime(), end.getTime());
        startTime = start.getTime();
        endTime = end.getTime();
    }

    public void updateData(Date start, Date end) {
        String label;
        if (mYear < Calendar.getInstance().get(Calendar.YEAR)) {
            label = DateUtils.date2Str(start, "yyyy年MM月dd日") + " - " + DateUtils.date2Str(end, "dd日");
        } else {
            label = DateUtils.date2Str(start, "MM月dd日") + " - " + DateUtils.date2Str(end, "dd日");
        }
        mLabelExpenseDateChart.setText(label);
        excelName = label;
    }
}
