package com.lixin.account.ucost.model;

/**
 * Created by Lixin on 2018/3/5
 */
public class AllYearStatistics {
    private int mMonth;
    private float mIncome;
    private float mExpense;
    private float mBalance;

    public AllYearStatistics() {
    }

    public AllYearStatistics(int month, float income, float expense, float balance) {
        mMonth = month;
        mIncome = income;
        mExpense = expense;
        mBalance = balance;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public float getIncome() {
        return mIncome;
    }

    public void setIncome(float income) {
        mIncome = income;
    }

    public float getExpense() {
        return mExpense;
    }

    public void setExpense(float expense) {
        mExpense = expense;
    }

    public float getBalance() {
        return mBalance;
    }

    public void setBalance(float balance) {
        mBalance = balance;
    }
}
