package com.lixin.account.ucost.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.Dao;
import com.lixin.account.ucost.model.Expense;
import com.lixin.account.ucost.model.ExpenseCat;
import com.lixin.account.ucost.model.ExpenseStatistics;
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
public class ExpenseDao {
    private Dao<Expense, Integer> mDao;
    private Context mContext;
    private DBOpenHelper dbHelper;

    /**
     * 构造方法
     * 获得数据库帮助类实例，通过传入Class对象得到相应的Dao
     * @param context
     */
    public ExpenseDao(Context context) {
        dbHelper = DBOpenHelper.getInstance(context);
        mDao = dbHelper.getDao(Expense.class);
        mContext = context;
    }

    public boolean addExpense(Expense expense) {
        int row = 0;
        try {
            row = mDao.create(expense);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    public float getPeriodSumExpense(Date start, Date end) {
        List<Expense> expenses = getPeriodExpense(start, end);
        float sum = 0;
        if (expenses != null && expenses.size() > 0) {
            for (int i = 0; i < expenses.size(); i++) {
                sum += expenses.get(i).getAmount();
            }
        }
        return sum;
    }

    public boolean deleteExpense(Expense expense) {
        int row = 0;
        try {
            row = mDao.delete(expense);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    public boolean updateExpense(Expense expense) {
        int row = 0;
        try {
            row = mDao.update(expense);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row > 0;
    }

    public List<ExpenseStatistics> getPeriodCatSumExpense(Date start, Date end) {
        List<ExpenseStatistics> expenseStatisticses = null;
        String sql = "select ExpenseCat.name, ExpenseCat.imageId, sum(amount) sumCatIncome from Expense " +
                ", ExpenseCat where Expense.categoryId = ExpenseCat.id and date between ? and ? and " +
                "categoryId in (select distinct(categoryId) from Expense) group by categoryId;";
        SQLiteDatabase database = DBOpenHelper.getInstance(mContext).getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, new String[]{DateUtils.date2Str(start), DateUtils.date2Str(end)});
        float sumExpense = getPeriodSumExpense(start, end);
        if (cursor.moveToFirst()) {
            expenseStatisticses = new ArrayList<>(cursor.getCount());
            do {
                String categoryName = cursor.getString(0);
                int imageId = cursor.getInt(1);
                float sumCat = cursor.getFloat(2);
                expenseStatisticses.add(new ExpenseStatistics(new ExpenseCat(imageId, categoryName),
                        sumCat, FormatUtils.formatFloat("#.0", sumCat / sumExpense * 100)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return expenseStatisticses;
    }

    public List<Expense> getPeriodExpense(Date start, Date end) {
        List<Expense> expenses = null;
        try {
            expenses = mDao.queryBuilder().where().between("date", start, end).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }
}
