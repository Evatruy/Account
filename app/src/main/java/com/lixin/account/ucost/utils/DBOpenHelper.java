package com.lixin.account.ucost.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lixin.account.ucost.model.Expense;
import com.lixin.account.ucost.model.ExpenseCat;
import com.lixin.account.ucost.model.Income;
import com.lixin.account.ucost.model.IncomeCat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Lixin on 2018/3/2
 */
public class DBOpenHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "UCost.db";
    private static final int DB_VERSION = 1;
    private static DBOpenHelper instance ;
    private Map<String, Dao> mDaoMap;

    private DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mDaoMap = new HashMap<>();
    }

    //获取数据库连接，采用单例模式
    public static synchronized DBOpenHelper getInstance(Context context) {
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (DBOpenHelper.class) {
                if (instance == null) {
                    instance = new DBOpenHelper(context);
                }
            }
        }
        return instance ;
    }

    //清空数据表
    public void dropTable() {
        SQLiteDatabase database = instance .getWritableDatabase();
        database.execSQL("delete from IncomeCat;");
        database.execSQL("delete from ExpenseCat;");
        database.execSQL("delete from Income;");
        database.execSQL("delete from Expense;");
    }

    //创建数据库表
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, IncomeCat.class);
            TableUtils.createTable(connectionSource, ExpenseCat.class);
            TableUtils.createTable(connectionSource, Income.class);
            TableUtils.createTable(connectionSource, Expense.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //数据库升级
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists UCost;");
        onCreate(sqLiteDatabase, connectionSource);
    }

    @Override
    public synchronized Dao getDao(Class clazz) {
        String className = clazz.getSimpleName();
        Dao dao = null;
        if (mDaoMap.containsKey(className)) {
            dao = mDaoMap.get(className);
        } else {
            try {
                dao = super.getDao(clazz);
                mDaoMap.put(className, dao);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dao;
    }

    @Override
    public void close() {
        super.close();
        mDaoMap.clear();
    }
}
