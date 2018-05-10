package com.lixin.account.ucost.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Lixin on 2018/3/5
 * 收入分类表
 */
@DatabaseTable(tableName = "IncomeCat")
public class IncomeCat implements Parcelable {
    @DatabaseField(generatedId = true, columnName = "id")
    private int mId;        //主键
    @DatabaseField(canBeNull = false, columnName = "name")
    private String mName;   //名称
    @DatabaseField(canBeNull = false, columnName = "imageId")
    private int mImageId;   //图标

    public IncomeCat() {
    }

    public IncomeCat(int id, String name, int imageId) {
        mId = id;
        mName = name;
        mImageId = imageId;
    }

    public IncomeCat(int imageId, String name) {
        mImageId = imageId;
        mName = name;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getImageId() {
        return mImageId;
    }

    public void setImageId(int imageId) {
        mImageId = imageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
        dest.writeInt(this.mImageId);
    }

    protected IncomeCat(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
        this.mImageId = in.readInt();
    }

    public static final Parcelable.Creator<IncomeCat> CREATOR = new Parcelable.Creator<IncomeCat>() {
        public IncomeCat createFromParcel(Parcel source) {
            return new IncomeCat(source);
        }

        public IncomeCat[] newArray(int size) {
            return new IncomeCat[size];
        }
    };
}
