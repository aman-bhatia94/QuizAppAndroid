package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class ConfigurationActivity extends AppCompatActivity {


    private boolean useImage;
    private boolean timerEnabled;
    private int timeOut = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        if(savedInstanceState != null){
            timerEnabled = savedInstanceState.getBoolean("timerEnabled");
            timeOut = savedInstanceState.getInt("timeOut");
            useImage = savedInstanceState.getBoolean("useImageButton");
        }
    }

    public void onToggleButtonClicked(View view) {

        boolean on = ((ToggleButton) view).isChecked();

        if(on){
            timerEnabled = true;
        }
        else{
            timerEnabled = false;
        }

        if(timerEnabled == true){

            TextView timeOutMessage = (TextView)findViewById(R.id.setTimeLimit);
            timeOutMessage.setVisibility(View.VISIBLE);

            EditText timeOutValue = (EditText)findViewById(R.id.timerValue);
            timeOutValue.setVisibility(View.VISIBLE);



        }
        else if(timerEnabled == false){
            TextView timeOutMessage = (TextView)findViewById(R.id.setTimeLimit);
            timeOutMessage.setVisibility(View.GONE);

            EditText timeOutValue = (EditText)findViewById(R.id.timerValue);
            timeOutValue.setVisibility(View.GONE);
        }


    }

    public void onRadioButtonClicked(View view) {

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        int id = radioGroup.getCheckedRadioButtonId();

        switch(id) {
            case R.id.imageButton:
                useImage = true;
                break;
            case R.id.defaultButton:
                useImage = false;
                break;
        }

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstance) {

        super.onSaveInstanceState(savedInstance);
        savedInstance.putBoolean("timerEnabled",timerEnabled);
        savedInstance.putInt("timeOut",timeOut);
        savedInstance.putBoolean("useImageButton",useImage);
    }

    public void onNextClicked(View view) {

        //After onClick is clicked
        if(timerEnabled == true){
            EditText timeOutValue = (EditText)findViewById(R.id.timerValue);
            if(String.valueOf(timeOutValue.getText()).equals("")){
                timeOut = 0;
            }
            else {
                timeOut = Integer.parseInt(String.valueOf(timeOutValue.getText()));
            }
        }

        Intent intent = new Intent(this,MultipleChoiceQuestionActivity.class);
        intent.putExtra("timerEnabled",timerEnabled);
        intent.putExtra("timeOut",timeOut);
        intent.putExtra("useImageButton",useImage);

        startActivity(intent);

    }
}
