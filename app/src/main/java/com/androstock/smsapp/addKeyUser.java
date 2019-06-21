package com.androstock.smsapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class addKeyUser extends Activity {

    DataBaseHelper helper = new DataBaseHelper(this);
    EncDecr encDecr = new EncDecr();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addkey);

    }
    public void randomKey(View view){
        EditText initPuKey = (EditText) findViewById(R.id.initPassPu);
        EditText initPrKey = (EditText) findViewById(R.id.initPassPr);
        initPuKey.setText(encDecr.randomKey(12));
        initPrKey.setText(encDecr.randomKey(12));
    }

    public void sendCode(View view){
        EditText phoneNum = (EditText) findViewById(R.id.phone_num);
        EditText initPuKey = (EditText) findViewById(R.id.initPassPu);
        EditText initPrKey = (EditText) findViewById(R.id.initPassPr);
        String Xkey = initPuKey.getText().toString();
        String Ykey = initPrKey.getText().toString();
        String addr = phoneNum.getText().toString();
        String phoneTarget = null;

        int check = 0;

        if(addr.substring(0,1).equals("0")) phoneTarget = addr.substring(1,addr.length());
        else if (addr.substring(0,3).equals("+62")) phoneTarget = addr.substring(3,addr.length());
        else phoneTarget = addr;

        if (helper.checkKunci(phoneTarget)==false) check = 1;
        else check = 0;

        if (Xkey.length() > 0 && Ykey.length() > 0 && check == 1/*&& helper.checkKunci(phoneTarget)==true*/) {

            String str_enc_msg = null;
            try {
                str_enc_msg = encDecr.encrypt(Ykey+Xkey/*str_message2*/, "kumahaaing")[0];
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (Function.sendSMS(phoneNum.getText().toString(), str_enc_msg)) {
                Toast.makeText(getApplicationContext(), "Key acknowledgement sent", Toast.LENGTH_SHORT).show();
                helper.storeKey(phoneTarget, Ykey, Xkey);
                finish();
            }
        }
        else Toast.makeText(getApplicationContext(), "The key for selected address exists", Toast.LENGTH_LONG).show();
    }


    public void createKey(View view){
        EditText phoneNum = (EditText) findViewById(R.id.phone_num);
        EditText initPrKey = (EditText) findViewById(R.id.initPassPr);
        EditText initPuKey = (EditText) findViewById(R.id.initPassPu);
        Button setKey = (Button) findViewById(R.id.set_key);

        String phoneNumber = phoneNum.getText().toString();

        String phoneSave = null;

        if(phoneNumber.substring(0,1).equals("0")) phoneSave = phoneNumber.substring(1,phoneNumber.length());
        else if (phoneNumber.substring(0,3).equals("+62")) phoneSave = phoneNumber.substring(3,phoneNumber.length());
        else phoneSave = phoneNumber;

        String initialPrKey = initPrKey.getText().toString();
        String initialPuKey = initPuKey.getText().toString();


        if (helper.checkKunci(phoneSave)==false && phoneSave.charAt(0)!='0'){
            helper.storeKey(phoneSave, initialPrKey, initialPuKey);
            Toast.makeText(this, "New Key Stored", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, "The phone number has existed or format is wrong", Toast.LENGTH_LONG).show();
            phoneNum.getText().clear();
            initPrKey.getText().clear();
            initPuKey.getText().clear();
        }

        /*helper.storeKey(phoneNumber, initialKey);
        Toast.makeText(this, "New Key Stored", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();*/

    }

}
