package com.lixin.account.ucost.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lixin.account.ucost.R;
import com.lixin.account.ucost.model.IncomeCat;
import com.lixin.account.ucost.utils.DBOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lixin on 2018/3/5
 */
public class IncomeCatDao {
    private Dao<IncomeCat, Integer> mDao;
    private DBOpenHelper dbHelper;

    public IncomeCatDao(Context context) {
        dbHelper = DBOpenHelper.getInstance(context);
        mDao = dbHelper.getDao(IncomeCat.class);
        List<IncomeCat> cats = this.getIncomeCat();
        if(cats.size()<=0){
            initIncomeCat();
        }
    }

    //查询收入类别总数
    public List<IncomeCat> getIncomeCat() {
        List<IncomeCat> cats = null;
        try {
            cats = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cats;
    }

    //添加类别
    public boolean addCategory(IncomeCat incomeCat) {
        int row = 0;
        try {
            row = mDao.create(incomeCat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    //修改类别
    public boolean update(IncomeCat incomeCat) {
        int row = 0;
        try {
            row = mDao.update(incomeCat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    //删除类别
    public boolean delete(IncomeCat incomeCat) {
        int row = 0;
        try {
            row = mDao.delete(incomeCat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }


    public void initIncomeCat() {
        int resId[] = {R.mipmap.icon_shouru_type_gongzi, R.mipmap.icon_shouru_type_shenghuofei,
                R.mipmap.icon_shouru_type_hongbao, R.mipmap.icon_shouru_type_linghuaqian,
                R.mipmap.icon_shouru_type_jianzhiwaikuai, R.mipmap.icon_shouru_type_touzishouru,
                R.mipmap.icon_shouru_type_jieru, R.mipmap.icon_shouru_type_jiangjin, R.mipmap.baoxiao,
                R.mipmap.xianjin, R.mipmap.tuikuan, R.mipmap.icon_shouru_type_qita};
        String labels[] = {"工资", "生活费", "红包", "零花钱", "兼职", "投资收益", "借款",
                "奖金", "报销", "现金", "退款", "其他"};
        List<IncomeCat> cats = new ArrayList<>(resId.length);
        IncomeCat incomeCat;
        for (int i = 0; i < resId.length; i++) {
            incomeCat = new IncomeCat();
            incomeCat.setImageId(resId[i]);
            incomeCat.setName(labels[i]);
            cats.add(incomeCat);
        }
        try {
            for (int i = 0, j = cats.size(); i < j; i++) {
                mDao.create(cats.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
