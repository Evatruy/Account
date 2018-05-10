package com.lixin.account.ucost.model;

/**
 * Created by Lixin on 2018/3/5
 */
public class IncomeStatistics {
    private IncomeCat mIncomeCat;
    private float mSum;
    private float mPercent;

    public IncomeStatistics() {
    }

    public IncomeStatistics(IncomeCat incomeCat, float sum, float percent) {
        mIncomeCat = incomeCat;
        mSum = sum;
        mPercent = percent;
    }

    public IncomeCat getIncomeCat() {
        return mIncomeCat;
    }

    public void setIncomeCat(IncomeCat incomeCat) {
        mIncomeCat = incomeCat;
    }

    public float getSum() {
        return mSum;
    }

    public void setSum(float sum) {
        mSum = sum;
    }

    public float getPercent() {
        return mPercent;
    }

    public void setPercent(float percent) {
        mPercent = percent;
    }
}
