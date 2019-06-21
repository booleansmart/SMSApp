package com.androstock.smsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class NewSmsActivity extends AppCompatActivity{

    EditText address, message;
    Button send_btn, check_button;
    String phoneTarget = null;

    DataBaseHelper helper = new DataBaseHelper(this);
    EncDecr encDecr = new EncDecr();
    int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_new);

        address = (EditText) findViewById(R.id.address);
        message = (EditText) findViewById(R.id.message);
        send_btn = (Button) findViewById(R.id.send_btn);
        check_button = (Button) findViewById(R.id.checkAddress);

        check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addr = address.getText().toString();


                if(addr.substring(0,1).equals("0")) phoneTarget = addr.substring(1,addr.length());
                else if (addr.substring(0,3).equals("+62")) phoneTarget = addr.substring(3,addr.length());
                else phoneTarget = addr;

                if (helper.checkKunci(phoneTarget)==true){
                    check = 1;
                    Toast.makeText(getApplicationContext(), "The number is available "+ helper.callTempPuKey(phoneTarget), Toast.LENGTH_LONG).show();
                }
                else{
                    check = 0;
                    Toast.makeText(getApplicationContext(), "The number is not available, please go to addkey to add number and key", Toast.LENGTH_LONG).show();
                }
            }
        });


        send_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String str_addtes = address.getText().toString();
                String str_message = message.getText().toString();

                // Modify address to be strored in DB
               /* String phoneTarget = null;
                if(str_addtes.substring(0,1).equals("0")) phoneTarget = str_addtes.substring(1,str_addtes.length());
                else if (str_addtes.substring(0,3).equals("+62")) phoneTarget = str_addtes.substring(3,str_addtes.length());
                else phoneTarget = str_addtes;*/

                //Generate next random PuKey
                String newKey = encDecr.randomKey(12);

                //modified str_message
                String str_message2 = newKey+str_message;






                if (str_addtes.length() > 0 && str_message.length() > 0 && check == 1/*&& helper.checkKunci(phoneTarget)==true*/) {

                    String str_enc_msg = null;
                    String time = null;
                    try {
                        str_enc_msg = encDecr.encrypt(str_message/*str_message2*/, helper.callPuKey(phoneTarget))[0];
                        time = encDecr.encrypt(str_message/*str_message2*/, helper.callPuKey(phoneTarget))[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (Function.sendSMS(str_addtes, newKey+str_enc_msg/*str_enc_msg*/)) {
                        Toast.makeText(getApplicationContext(), "Message sent with elapsed time "+time, Toast.LENGTH_SHORT).show();
                        // updating PuKey
                        helper.updatePuKey(newKey, phoneTarget);
                        finish();
                    }
                }
                else Toast.makeText(getApplicationContext(), "Address and text are empty, or the key is not exist.", Toast.LENGTH_LONG).show();
            }

        });
    }
}
