package com.lixin.account.ucost.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Lixin on 2018/3/5
 * 收入实体类
 */
@DatabaseTable(tableName = "Income")
public class Income implements Parcelable {
    @DatabaseField(generatedId = true, columnName = "id")
    private int mId;              //主键
    @DatabaseField(canBeNull = false, columnName = "date")
    private Date mDate;         //日期
    @DatabaseField(canBeNull = false, columnName = "amount")
    private float mAmount;        //金额
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, columnName = "categoryId")
    private IncomeCat mCategory;     //类别
    @DatabaseField(columnName = "note")
    private String mNote;         //备注

    public Income() {

    }

    public Income(Date date, float amount, IncomeCat category, String note) {
        mDate = date;
        mAmount = amount;
        mCategory = category;
        mNote = note;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public float getAmount() {
        return mAmount;
    }

    public void setAmount(float amount) {
        mAmount = amount;
    }

    public IncomeCat getCategory() {
        return mCategory;
    }

    public void setCategory(IncomeCat category) {
        mCategory = category;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    @Override
    public String toString() {
        return "Income{" +
                "mId=" + mId +
                ", mDate=" + mDate +
                ", mAmount=" + mAmount +
                ", mCategory=" + mCategory.getName() +
                ", mNote='" + mNote + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeLong(mDate != null ? mDate.getTime() : -1);
        dest.writeFloat(this.mAmount);
        dest.writeParcelable(this.mCategory, flags);
        dest.writeString(this.mNote);
    }

    protected Income(Parcel in) {
        this.mId = in.readInt();
        long tmpMDate = in.readLong();
        this.mDate = tmpMDate == -1 ? null : new Date(tmpMDate);
        this.mAmount = in.readFloat();
        this.mCategory = in.readParcelable(IncomeCat.class.getClassLoader());
        this.mNote = in.readString();
    }

    public static final Parcelable.Creator<Income> CREATOR = new Parcelable.Creator<Income>() {
        public Income createFromParcel(Parcel source) {
            return new Income(source);
        }

        public Income[] newArray(int size) {
            return new Income[size];
        }
    };
}