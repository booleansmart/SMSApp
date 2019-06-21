package com.androstock.smsapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class CheckEncrypt extends Activity {

    EditText teks1, kunci;
    TextView textView5;
    Button button1, button2;
    String outputString;
    String AES = "AES";

    EncDecr encDecr = new EncDecr();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkencrypt);

        final EditText teks1 = (EditText)findViewById(R.id.teks);
        final EditText kunci = (EditText)findViewById(R.id.kunc1);

        final TextView textView5 = (TextView) findViewById(R.id.textView5);

        Button encButton = (Button) findViewById(R.id.button1);
        Button decButon = (Button)findViewById(R.id.button2);



        encButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    outputString = encDecr.encrypt(teks1.getText().toString(), kunci.getText().toString())[0];
                    textView5.setText(outputString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        decButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    outputString = encDecr.decrypt(outputString, kunci.getText().toString())[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView5.setText(outputString);
            }
        });
    }

}
