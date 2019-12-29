package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MultipleChoiceQuestionActivity extends AppCompatActivity {

    AnswerResolver resolver = new AnswerResolver();
    static int countAttempts = 0;

    //code for timers
    private int secondsTimer1 = 0;
    private boolean runningTimer1;

    private int secondsTimer2 = 0;
    private boolean runningTimer2;
    private boolean wasRunningTimer2;

    //setting timer limits and other configuration options

    private boolean useImage;
    private boolean timerEnabled;
    private int timeOut = 0;

    //check for timeout
    boolean checkTimeOut = false;



    //variable for timer service
    private TimerService timers;
    private boolean bound = false;
    Intent intentTimer;

    boolean startFillBlank = false;


    //Create a service Connection

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {

            TimerService.TimerBinder timerBinder =
                    (TimerService.TimerBinder) binder;

            timers = timerBinder.getTimer();
            bound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplechoice);
        countAttempts = 0;

        //getting details from intent
        Intent intent = getIntent();

        timerEnabled = intent.getBooleanExtra("timerEnabled",false);
        useImage = intent.getBooleanExtra("useImageButton",false);
        if(timerEnabled){
            timeOut = intent.getIntExtra("timeOut",0);
        }

        if(timerEnabled == false){
            final TextView timeView = (TextView)findViewById(R.id.time_view);
            final TextView timeView2 = (TextView)findViewById(R.id.time_view2);
            timeView.setVisibility(View.GONE);
            timeView2.setVisibility(View.GONE);
        }

        if(useImage == true){
            Button normalButton = (Button)findViewById(R.id.button);
            normalButton.setVisibility(View.GONE);

            Button imageButton = (Button)findViewById(R.id.button2);
            imageButton.setVisibility(View.VISIBLE);
        }

        if(savedInstanceState != null){
            secondsTimer1 = savedInstanceState.getInt("secondsTimer1");
            runningTimer1 = savedInstanceState.getBoolean("runningTimer1");

            secondsTimer2 = savedInstanceState.getInt("secondsTimer2");
            runningTimer2 = savedInstanceState.getBoolean("runningTimer2");
            wasRunningTimer2 = savedInstanceState.getBoolean("wasRunningTimer2");

            intentTimer = new Intent(this,TimerService.class);
            intentTimer.putExtra("runningTimer1",runningTimer1);
            if(wasRunningTimer2){
                intentTimer.putExtra("runningTimer2",true);
            }
            else {
                intentTimer.putExtra("runningTimer2", runningTimer2);
            }
            intentTimer.putExtra("secondsTimer1",secondsTimer1);
            intentTimer.putExtra("secondsTimer2",secondsTimer2);
            intentTimer.putExtra("timeOutValue",timeOut);
            bindService(intentTimer,connection, Context.BIND_AUTO_CREATE);

        }
        else {
            runningTimer1 = true;
            runningTimer2 = true;

            intentTimer = new Intent(this,TimerService.class);
            intentTimer.putExtra("runningTimer1",runningTimer1);
            intentTimer.putExtra("runningTimer2",runningTimer2);
            intentTimer.putExtra("secondsTimer1",secondsTimer1);
            intentTimer.putExtra("secondsTimer2",secondsTimer2);
            intentTimer.putExtra("timeOutValue",timeOut);
            bindService(intentTimer,connection, Context.BIND_AUTO_CREATE);
        }




        if(timerEnabled) {
            //calling the methods to set timer values here
            /*timers.setSecondsTimer1(secondsTimer1);
            timers.setSecondsTimer2(secondsTimer2);
            timers.setRunningTimer1(runningTimer1);
            timers.setRunningTimer2(runningTimer2);
            */
            runTimers();
        }
    }


    public void onSubmitMultipleChoice(View view){
        Spinner options = (Spinner)findViewById(R.id.mcq_spinner);
        String[] allOptions = getResources().getStringArray(R.array.bands);


        //For the second Multiple choice question
        Spinner options2 = (Spinner)findViewById(R.id.mcq_spinner2);
        String[] allOptions2 = getResources().getStringArray(R.array.guitarists);


        int numberOfOptions = allOptions.length;
        System.out.println("Number of options"+numberOfOptions);
        int allowedAttempts = numberOfOptions - 2;
        String answer = String.valueOf(options.getSelectedItem());
        boolean result = resolver.multipleResolver(answer);

        String answer2 = String.valueOf(options2.getSelectedItem());
        boolean result2 = resolver.multipleResolver2(answer2);

        TextView tryMessage = (TextView)findViewById(R.id.tryResult);

        if(countAttempts < allowedAttempts){

            if(result == false || result2 == false){
                countAttempts++;
                tryMessage.setText("Incorrect answer try again");
            }


        }
        if(countAttempts >= allowedAttempts && ((result == false)||(result2 == false))){
            //failed the quiz
            Intent intent = new Intent(this,ResultActivity.class);
            intent.putExtra("result","Failed");
            int totalFail = AnswerResolver.getFailCount();
            AnswerResolver.setFailCount(totalFail+1);
            secondsTimer1 = 0;
            runningTimer1 = false;
            secondsTimer2 = 0;
            runningTimer2 = false;
            startActivity(intent);
            finish();
        }
        if(result == true && result2 == true){
            secondsTimer1 = 0;
            runningTimer1 = false;
            secondsTimer2 = 0;
            runningTimer2 = false;
            Intent intent = new Intent(this,FillBlankActivity.class);

            intent.putExtra("timerEnabled",timerEnabled);
            intent.putExtra("timeOut",timeOut);
            intent.putExtra("useImage",useImage);

            //timers.setRunningTimer1(true);
            //timers.setRunningTimer2(true);
            //timers.setSecondsTimer1(0);
            //timers.setSecondsTimer2(0);
            startFillBlank = true;
            //startActivity(intent);
            finish();

        }


    }


    //code to run the timer
    private void runTimers() {

        final Intent intent = new Intent(this,ResultActivity.class);
        final TextView timeView = (TextView)findViewById(R.id.time_view);
        final TextView timeView2 = (TextView)findViewById(R.id.time_view2);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {


                //new code
                ArrayList<Integer> timerValues = null;
                if(bound && timers != null){

                    timerValues = timers.getTimerValues();
                    secondsTimer1 = timerValues.get(0);
                    secondsTimer2 = timerValues.get(1);
                }



                //---end of new code-----

                int minutes = (secondsTimer1%3600)/60;
                int minutes2 = (secondsTimer2%3600)/60;
                int secs = secondsTimer1%60;
                int secs2 = secondsTimer2%60;
                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                timeView.setText(time);

                String time2 = String.format(Locale.getDefault(), "%02d:%02d", minutes2, secs2);
                timeView2.setText(time2);


                if(timerEnabled == true && timeOut != 0){
                    if(secondsTimer1 >= timeOut){

                        int totalFail = AnswerResolver.getFailCount();
                        AnswerResolver.setFailCount(totalFail+1);
                        intent.putExtra("result","Failed");
                        checkTimeOut = true;
                        secondsTimer1 = 0;
                        runningTimer1 = false;
                        secondsTimer2 = 0;
                        runningTimer2 = false;
                        startActivity(intent);
                        finish();


                    }
                }

                if (checkTimeOut == false) {
                    handler.postDelayed(this,1000);
                }

            }
        });

    }

    //saving the state so a configuration can still let the timer continue;

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {

        super.onSaveInstanceState(savedInstance);
        savedInstance.putBoolean("runningTimer1",runningTimer1);
        savedInstance.putInt("secondsTimer1",secondsTimer1);
        savedInstance.putInt("secondsTimer2",secondsTimer2);
        savedInstance.putBoolean("runningTimer2",runningTimer2);
        savedInstance.putBoolean("wasRunningTimer2",wasRunningTimer2);

        //timers.setSecondsTimer1(secondsTimer1);
        //timers.setSecondsTimer2(secondsTimer2);
        //timers.setRunningTimer1(runningTimer1);
        timers.setRunningTimer2(runningTimer2);

    }

    //on start and on stop methods for timer 2
    @Override
    public void onStop() {

        super.onStop();
        wasRunningTimer2 = runningTimer2;
        runningTimer2 = false;
        timers.setRunningTimer2(runningTimer2);



    }

    @Override
    public void onStart(){
        super.onStart();
        if(wasRunningTimer2){
            runningTimer2 = true;
            if(bound){
                timers.setRunningTimer2(runningTimer2);
            }

        }


    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if(bound){
            System.out.println("Unbound in MCQ");
            unbindService(connection);
            bound = false;
        }
        if(startFillBlank == true){
            Intent intent = new Intent(this,FillBlankActivity.class);
            intent.putExtra("timerEnabled",timerEnabled);
            intent.putExtra("timeOut",timeOut);
            intent.putExtra("useImage",useImage);

            startActivity(intent);
        }
    }

}
