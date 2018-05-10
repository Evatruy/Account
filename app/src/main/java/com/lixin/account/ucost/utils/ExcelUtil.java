package com.lixin.account.ucost.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

import com.lixin.account.ucost.model.Expense;
import com.lixin.account.ucost.model.Income;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    //内存地址
    public static String root = Environment.getExternalStorageDirectory().getPath();

    public static void writeExcel(Context context, List<Expense> exportExpense, List<Income> exportIncome, String fileName) throws Exception {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)&&getAvailableStorage()>1000000) {
            Toast.makeText(context, "SD卡不可用", Toast.LENGTH_LONG).show();
            return;
        }
        String[] title = { "日期", "名称", "金额", "备注" };
        File file;
        File dir = new File(context.getExternalFilesDir(null).getPath());
        file = new File(dir, fileName + ".xls");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 创建Excel工作表
        WritableWorkbook wwb;
        OutputStream os = new FileOutputStream(file);
        wwb = Workbook.createWorkbook(os);
        // 添加工作表并设置Sheet名字
        WritableSheet sheet = wwb.createSheet("支出", 0);
        WritableSheet sheet1 = wwb.createSheet("收入", 1);
        Label label;
        for (int i = 0; i < title.length; i++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(i, 0, title[i], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet.addCell(label);
        }

        for (int i = 0; i < exportExpense.size(); i++) {
            Expense expense = exportExpense.get(i);

            String date = DateUtils.date2Str(expense.getDate(),"yyyy-MM-dd HH:mm");
            Label expenseDate = new Label(0, i + 1, date);
            Label expenseCat = new Label(1, i + 1, expense.getCategory().getName());
            Label expenseAmount = new Label(2,i+1, String.valueOf(expense.getAmount()));
            Label expenseNote = new Label(3, i + 1, expense.getNote());

            sheet.addCell(expenseDate);
            sheet.addCell(expenseCat);
            sheet.addCell(expenseAmount);
            sheet.addCell(expenseNote);
        }


        for (int j = 0; j < title.length; j++) {
            // Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
            // 在Label对象的子对象中指明单元格的位置和内容
            label = new Label(j, 0, title[j], getHeader());
            // 将定义好的单元格添加到工作表中
            sheet1.addCell(label);
        }

        for (int j = 0; j < exportIncome.size(); j++) {
            Income income = exportIncome.get(j);

            String date = DateUtils.date2Str(income.getDate(),"yyyy-MM-dd HH:mm");
            Label incomeDate = new Label(0, j + 1, date);
            Label incomeCat = new Label(1, j + 1, income.getCategory().getName());
            Label incomeAmount = new Label(2,j+1, String.valueOf(income.getAmount()));
            Label incomeNote = new Label(3, j + 1, income.getNote());

            sheet1.addCell(incomeDate);
            sheet1.addCell(incomeCat);
            sheet1.addCell(incomeAmount);
            sheet1.addCell(incomeNote);
        }

        Toast.makeText(context, "保存成功" + dir, Toast.LENGTH_LONG).show();
        // 写入数据
        wwb.write();
        // 关闭文件
        wwb.close();
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);// 定义字体
        try {
            font.setColour(Colour.BLACK);// 蓝色字体
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);// 左右居中
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);// 上下居中
            // format.setBorder(Border.ALL, BorderLineStyle.THIN,
            // Colour.BLACK);// 黑色边框
            // format.setBackground(Colour.YELLOW);// 黄色背景
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }

    /** 获取SD可用容量 */
    private static long getAvailableStorage() {

        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }
}
