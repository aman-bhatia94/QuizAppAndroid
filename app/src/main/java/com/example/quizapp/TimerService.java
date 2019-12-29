package com.example.quizapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class TimerService extends Service {


    //To return the values of timers to the activities
    private final ArrayList<Integer> timerValues = new ArrayList<>();
    private int secondsTimer1 = 0;
    private int secondsTimer2 = 0;
    private boolean runningTimer1 = true;
    private boolean runningTimer2 = true;

    private int timeOut;

    private final IBinder binder = new TimerBinder();

    public class TimerBinder extends Binder {

        TimerService getTimer(){
            return TimerService.this;
        }


    }



    //called when a component wants to bind to a service
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        runningTimer1 = intent.getExtras().getBoolean("runningTimer1");
        runningTimer2 = intent.getExtras().getBoolean("runningTimer2");
        secondsTimer1 = intent.getExtras().getInt("secondsTimer1");
        secondsTimer2 = intent.getExtras().getInt("secondsTimer2");
        timeOut = intent.getExtras().getInt("timeOutValue");
        System.out.println("on Bind Gets called ");
        return binder;
    }

    public ArrayList<Integer> getTimerValues(){



        if(runningTimer1)
            secondsTimer1++;

        if(runningTimer2)
            secondsTimer2++;

        if(timerValues.isEmpty()){
            timerValues.add(secondsTimer1);
            timerValues.add(secondsTimer2);
        }
        else {
            timerValues.set(0, secondsTimer1);
            timerValues.set(1, secondsTimer2);
        }

        if(secondsTimer1 >= timeOut){
            Toast.makeText(getApplicationContext(),"Time Limit Reached",Toast.LENGTH_SHORT).show();
            timeExpiredNotification();
        }

        return timerValues;

    }

    public void setRunningTimer2(boolean runningTimer2) {
        this.runningTimer2 = runningTimer2;
    }

    /*public void setRunningTimer1(boolean runningTimer1){

    }

    public void setSecondsTimer1(int seconds){

    }

    public void setSecondsTimer2(int seconds){

    }*/

    private void timeExpiredNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle("Time limit expired")
                .setContentText("Result is ready")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0,1000})
                .setAutoCancel(true);
        Intent actionIntent = new Intent(this,ResultActivity.class);
        actionIntent.putExtra("message","Fail");
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                this,
                0,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(actionPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(2345,builder.build());
    }
}
