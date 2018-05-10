package com.lixin.account.ucost.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.lixin.account.ucost.R;
import com.lixin.account.ucost.activity.AboutActivity;
import com.lixin.account.ucost.activity.MainActivity;
import com.lixin.account.ucost.activity.QuestionActivity;
import com.lixin.account.ucost.activity.QuotaActivity;
import com.lixin.account.ucost.receiver.AlarmReceiver;
import com.lixin.account.ucost.utils.Constant;
import com.lixin.account.ucost.utils.DBOpenHelper;
import com.lixin.account.ucost.utils.DateUtils;
import com.lixin.account.ucost.view.ShareDialog;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Lixin on 2018/3/5
 * 我的模块
 */
public class MineFragment extends BaseFragment implements OnDateSetListener {

    @Bind(R.id.iv_user_photo)
    ImageView mIvUserPhoto;
    @Bind(R.id.me_username)
    TextView mUsername;
    private MainActivity mContext;
    private static final int UPDATE_USER = 0X1;

    com.jzxiang.pickerview.TimePickerDialog mDialogAll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        //时间选择控件
        long twoYears = 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMaxMillseconds(System.currentTimeMillis() + twoYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(com.jzxiang.pickerview.data.Type.ALL)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

        return view;
    }

    @Override
    protected int getResId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected Fragment getSubFragment() {
        return this;
    }

    @OnClick({R.id.ll_me_user, R.id.ll_data_export, R.id.ll_me_share,
            R.id.ll_me_about, R.id.ll_common_question,R.id.ll_me_init, R.id.ll_me_reminder})
    public void mineClick(View view) {
        switch (view.getId()) {
            case R.id.ll_data_export:
                startActivity(new Intent(mContext, QuotaActivity.class));
            break;
            case R.id.ll_me_reminder: {
                mDialogAll.show(getActivity().getSupportFragmentManager(), "all");
            }
            break;
            case R.id.ll_common_question:
                startActivity(new Intent(mContext, QuestionActivity.class));
                break;
            case R.id.ll_me_init: {//初始化
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("警告");
                builder.setCancelable(true);
                builder.setMessage(" 初始化将删除所有数据并恢复最初设置，你确定这么做吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //在这里初始化所有数据表，并退出
                        DBOpenHelper.getInstance(mContext).dropTable();
                        mContext.finish();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
            break;
            case R.id.ll_me_share: {
                ShareDialog dialog = new ShareDialog(getContext());
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setTitle(R.string.share_to);
                dialog.setShareInfo("U你记", "一个简单实用的记账APP哦,欢迎下载APP体验！", "https://pan.baidu.com/s/1_7hn6t8UbXXB0SZ_nSF4Jg");
                dialog.show();
                break;
            }
            case R.id.ll_me_about: {
                startActivity(new Intent(mContext, AboutActivity.class));
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == UPDATE_USER) {
            String newFile = data.getStringExtra(Constant.NEW_FILENAME);
            /*if (newFile != null) {
                mIvUserPhoto.setImageBitmap(BitmapFactory.decodeFile(BmobPro.getInstance(mContext)
                        .getCacheDownloadDir() + File.separator + newFile));
            }*/
            String newName = data.getStringExtra(Constant.NEW_USERNAME);
            if (newName != null) {
                mUsername.setText(newName);
            }
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        Date date = new Date(millseconds);

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.setAction(Constant.ACTION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        Toast.makeText(mContext, "闹钟将在" + DateUtils.date2Str(date, "MM-dd HH:mm")
                + "发出提醒", Toast.LENGTH_SHORT).show();
    }
}
