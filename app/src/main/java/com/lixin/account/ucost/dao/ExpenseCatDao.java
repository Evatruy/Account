package com.lixin.account.ucost.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lixin.account.ucost.R;
import com.lixin.account.ucost.model.ExpenseCat;
import com.lixin.account.ucost.utils.DBOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lixin on 2018/3/5
 */
public class ExpenseCatDao {
    private Dao<ExpenseCat, Integer> mDao;
    private DBOpenHelper dbHelper;

    public ExpenseCatDao(Context context) {
        dbHelper = DBOpenHelper.getInstance(context);
        mDao = dbHelper.getDao(ExpenseCat.class);
        List<ExpenseCat> cats = this.getExpenseCat();
        if(cats.size()<=0){
            initExpensesCat();
        }
    }

    public List<ExpenseCat> getExpenseCat() {
        List<ExpenseCat> cats = null;
        try {
            cats = mDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cats;
    }

    public boolean addCategory(ExpenseCat expenseCat) {
        int row = 0;
        try {
            row = mDao.create(expenseCat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    public boolean delete(ExpenseCat expenseCat) {
        int row = 0;
        try {
            row = mDao.delete(expenseCat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    public boolean update(ExpenseCat expenseCat) {
        int row = 0;
        try {
            row = mDao.update(expenseCat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }
    public void initExpensesCat() {
        int resId[] = {R.mipmap.icon_shouru_type_qita, R.mipmap.icon_zhichu_type_canyin,
                R.mipmap.icon_zhichu_type_jiaotong, R.mipmap.icon_zhichu_type_yanjiuyinliao,
                R.mipmap.icon_zhichu_type_shuiguolingshi, R.mipmap.lingshi, R.mipmap.maicai, R.mipmap.yifu,
                R.mipmap.richangyongpin, R.mipmap.icon_zhichu_type_shoujitongxun, R.mipmap.huazhuangpin,
                R.mipmap.fangzu, R.mipmap.icon_zhichu_type_taobao, R.mipmap.zhifubao, R.mipmap.icon_shouru_type_hongbao};
        String labels[] = {"一般", "餐饮", "交通", "酒水饮料", "水果", "零食", "买菜", "衣服", "生活用品",
                "话费", "化妆品", "房租", "淘宝", "支付宝", "红包"};
        List<ExpenseCat> cats = new ArrayList<>(resId.length);
        ExpenseCat expenseCat;
        for (int i = 0; i < resId.length; i++) {
            expenseCat = new ExpenseCat();
            expenseCat.setImageId(resId[i]);
            expenseCat.setName(labels[i]);
            cats.add(expenseCat);
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