package com.lixin.account.ucost.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.Dao;
import com.lixin.account.ucost.model.Income;
import com.lixin.account.ucost.model.IncomeCat;
import com.lixin.account.ucost.model.IncomeStatistics;
import com.lixin.account.ucost.utils.DBOpenHelper;
import com.lixin.account.ucost.utils.DateUtils;
import com.lixin.account.ucost.utils.FormatUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Lixin on 2018/3/5
 */
public class IncomeDao {
    private Dao<Income, Integer> mDao;
    private Context mContext;
    private DBOpenHelper dbHelper;

    public IncomeDao(Context context) {
        dbHelper = DBOpenHelper.getInstance(context);
        mDao = dbHelper.getDao(Income.class);
        this.mContext = context;
    }

    //添加收入信息
    public boolean addIncome(Income income) {
        int row = 0;
        try {
            row = mDao.create(income);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    //修改收入信息
    public boolean updateIncome(Income income) {
        int row = 0;
        try {
            row = mDao.update(income);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    //删除收入信息
    public boolean deleteExpense(Income income) {
        int row = 0;
        try {
            row = mDao.delete(income);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    //查询指定周期的收入总和
    public float getPeriodSumIncome(Date start, Date end) {
        List<Income> incomes = getPeriodIncomes(start, end);
        float sum = 0;
        if (incomes != null && incomes.size() > 0) {
            for (int i = 0; i < incomes.size(); i++) {
                sum += incomes.get(i).getAmount();
            }
        }
        return sum;
    }

    //按类别统计指定周期的收入情况
    public List<IncomeStatistics> getPeriodCatSumExpense(Date start, Date end) {
        List<IncomeStatistics> incomeStatisticses = null;
        String sql = "select IncomeCat.name, IncomeCat.imageId, sum(amount) sumCatIncome from Income " +
                ", IncomeCat where Income.categoryId = IncomeCat.id and date between ? and ? and " +
                "categoryId in (select distinct(categoryId) from Income) group by categoryId;";
        SQLiteDatabase database = DBOpenHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, new String[]{DateUtils.date2Str(start), DateUtils.date2Str(end)});
        float sumExpense = getPeriodSumIncome(start, end);
        if (cursor.moveToFirst()) {
            incomeStatisticses = new ArrayList<>(cursor.getCount());
            do {
                String categoryName = cursor.getString(0);
                int imageId = cursor.getInt(1);
                float sumCat = cursor.getFloat(2);
                incomeStatisticses.add(new IncomeStatistics(new IncomeCat(imageId, categoryName),
                        sumCat, FormatUtils.formatFloat("#.0", sumCat / sumExpense * 100)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return incomeStatisticses;
    }

    //查询指定周期的所有收入
    public List<Income> getPeriodIncomes(Date start, Date end) {
        List<Income> incomes = null;
        try {
            incomes = mDao.queryBuilder().where().between("date", start, end).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomes;
    }
}
