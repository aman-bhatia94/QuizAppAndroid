package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");

        TextView resultDisplay = (TextView)findViewById(R.id.result);
        resultDisplay.setText(result);
    }

    public void onRetake(View view){
        Intent intent = new Intent(this,ConfigurationActivity.class);
        startActivity(intent);
    }

    public void onClickResult(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        StringBuilder sb = new StringBuilder();
        sb.append("Passes: "+AnswerResolver.getPassCount());
        sb.append("\n");
        sb.append("Fails: "+AnswerResolver.getFailCount());
        String results = sb.toString();
        intent.putExtra(Intent.EXTRA_TEXT,results);
        Intent chooseIntent = Intent.createChooser(intent,"Display results using..");
        startActivity(chooseIntent);
    }
}
