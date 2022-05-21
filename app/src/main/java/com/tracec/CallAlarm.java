package com.tracec;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tracec.data.Patient;
import com.tracec.utils.DBOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallAlarm extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent) {
        if("com.trace.android_broadcastreceiver.01".equals(intent.getAction())){

            String pid = intent.getStringExtra("pid");
            DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
            Patient patient = dbOpenHelper.getPatient(pid);
            String mes="Warning!! Patient "+patient.getPatientid()+" "+patient.getSurname()+" "+patient.getGivenname()+" close to Casino. Please stop patient and call to patient";

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate =  new Date(System.currentTimeMillis());
            String starttime = formatter.format(curDate);

            dbOpenHelper.addRecord(pid,starttime);

            AlertDialog alertDialog1 = new AlertDialog.Builder(context)
                    .setTitle("ALERT")//标题
                    .setMessage(mes)//内容
                    .setIcon(R.mipmap.ic_launcher)//图标
                    .create();
            alertDialog1.show();
        }
        if("com.trace.android_broadcastreceiver.02".equals(intent.getAction())){
            String pid = intent.getStringExtra("pid");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate =  new Date(System.currentTimeMillis());
            String endtime = formatter.format(curDate);

            DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
            int lastid = dbOpenHelper.getLastRecord(pid);
            dbOpenHelper.updateRecord(endtime,lastid);
        }
    }
}
