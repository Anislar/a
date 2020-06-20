package com.example.routeur;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class startActivity extends AppCompatActivity implements startActivity.dialogListenner {
private Button button;
    TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtTitle= (TextView) findViewById(R.id.ROUTEUR);

        setContentView(R.layout.activity_start);
        button= (Button)findViewById(R.id.btnAjout);
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openDialog();
        }
    });
    }

    private void openDialog() {
    AddDialog addDialog = new AddDialog();
    addDialog .show(getSupportFragmentManager(), "Add new router");


    }


    public void applyTexts(String title) {
        txtTitle.setText(title);    }

    @Override
    public void applyTexts(String title, String addMac) {

    }

    public  interface  dialogListenner{
        void applyTexts(String title , String addMac);


    }
}
