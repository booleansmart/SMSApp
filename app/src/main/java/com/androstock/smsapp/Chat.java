package com.androstock.smsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;



public class Chat extends AppCompatActivity {

    ListView listView;
    ChatAdapter adapter;
    LoadSms loadsmsTask;
    String name;
    String address;
    EditText new_message;
    ImageButton send_message;
    int thread_id_main;
    private Handler handler = new Handler();
    Thread t;
    ArrayList<HashMap<String, String>> smsList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> customList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> tmpList = new ArrayList<HashMap<String, String>>();

    EncDecr encDecr = new EncDecr();
    DataBaseHelper helper  = new DataBaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        thread_id_main = Integer.parseInt(intent.getStringExtra("thread_id"));

        listView = (ListView) findViewById(R.id.listView);
        new_message = (EditText) findViewById(R.id.new_message);
        send_message = (ImageButton) findViewById(R.id.send_message);

        startLoadingSms();


        send_message.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String text = new_message.getText().toString();


                if(text.length()>0) {
                    String tmp_msg = text;
                    new_message.setText("Sending....");
                    new_message.setEnabled(false);

                    //Generating candidate new PuKey
                    String newPuKey = encDecr.randomKey(12);

                    //Encrypting Message using Current PuKey
                    String enc_tmp_msg = null;
                    String key = helper.callPuKey(address);
                    Long aa = System.nanoTime();
                    try {
                        enc_tmp_msg = newPuKey + encDecr.encrypt(text, key)[0];

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Long ab = System.nanoTime();
                    Long abc = ab -aa;


                    // Sending NewPrKey and Encrypted Message
                    if (Function.sendSMS(address, enc_tmp_msg)) {
                        new_message.setText("");
                        new_message.setEnabled(true);
                        // Creating a custom list for newly added sms
                        customList.clear();
                        customList.addAll(smsList);
                        customList.add(Function.mappingInbox(null, null, null, null, tmp_msg, "2", null, "Sending..."));
                        adapter = new ChatAdapter(Chat.this, customList);
                        listView.setAdapter(adapter);
                        //helper.updatePuKey(newPuKey, address.substring(3,address.length()));
                        helper.updatePuKey(newPuKey, address);
                        Toast.makeText(getApplicationContext(),abc.toString(),Toast.LENGTH_LONG).show();
                        //=========================
                    } else {
                        new_message.setText(tmp_msg);
                        new_message.setEnabled(true);
                    }


                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                loadsmsTask.cancel(true);
                /*
                intent.putExtra("name", tmpList.get(+position).get(Function.KEY_NAME));
                intent.putExtra("address", tmpList.get(+position).get(Function.KEY_PHONE));
                intent.putExtra("thread_id", tmpList.get(+position).get(Function.KEY_THREAD_ID));
                startActivity(intent);*/

                String testkey = null/*helper.callTempPuKey(address)*/;



                    if (tmpList.get(+position).get(Function.KEY_TYPE).equals("1")) {
                        testkey = helper.callPrKey(address);
                    } else if (tmpList.get(+position).get(Function.KEY_TYPE).equals("2")) {
                        testkey = helper.callTempPuKey(address);
                    }



                String uhu = tmpList.get(+position).get(Function.KEY_MSG);
                String txtMsg = null;
                String checkInit = null;
                String time = null;
                String uhu1 = tmpList.get(+position).get(Function.KEY_TYPE);
                //String uhu2 = uhu.substring(5,uhu.length());
                //String ahaha = encDecr.randomKey(5);


                try {
                    checkInit = encDecr.decrypt(tmpList.get(+position).get(Function.KEY_MSG),"kumahaaing")[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (uhu1.equals("1")){
                    try {

                        txtMsg = encDecr.decrypt(uhu.substring(12, uhu.length()), testkey)[0];
                        time = encDecr.decrypt(uhu.substring(12, uhu.length()), testkey)[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //String newPrKey = txtMsg.substring(0, 5);
                    //helper.updatePrKey(newPrKey, address.substring(3, address.length()));
                }

                else if (uhu1.equals("2")){
                    try {

                        txtMsg = encDecr.decrypt(uhu.substring(12, uhu.length()), testkey)[0];
                        time = encDecr.decrypt(uhu.substring(12, uhu.length()), testkey)[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //String newPuKey = txtMsg.substring(0, 5);
                    //helper.updatePuKey(newPuKey, address.substring(3, address.length()));
                }

                //Toast.makeText(getApplicationContext(), uhu+" is selected", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
                if(txtMsg != null){
                    builder.setMessage(txtMsg+" from "+address+" time taken "+time);
                }
                else if(checkInit!=null && uhu1.equals("2")) builder.setMessage("this is your initiation key");
                else if(checkInit!=null && uhu1.equals("1")) builder.setMessage("This is your initiation key, wanna update?");
                else builder.setMessage("Please update from previous key");
                builder.setCancelable(true);

                if(txtMsg != null && checkInit == null) {
                    builder.setPositiveButton(
                            "UpdateKey", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //write here for further commands
                                /*try {
                                    helper.updateTempPuKey(encDecr.decrypt(tmpList.get(+position).get(Function.KEY_MSG), helper.callTempPuKey(address)).substring(0,5), address);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                                    if (tmpList.get(+position).get(Function.KEY_TYPE).equals("1")) {
                                        try {
                                            helper.updatePrKey(tmpList.get(+position).get(Function.KEY_MSG).substring(0,12)/*encDecr.decrypt(tmpList.get(+position).get(Function.KEY_MSG), helper.callPrKey(address)).substring(0, 5)*/, address);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if (tmpList.get(+position).get(Function.KEY_TYPE).equals("2")) {
                                        try {
                                            helper.updateTempPuKey(tmpList.get(+position).get(Function.KEY_MSG).substring(0,12)/*encDecr.decrypt(tmpList.get(+position).get(Function.KEY_MSG), helper.callTempPuKey(address)).substring(0, 5)*/, address);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    dialogInterface.cancel();
                                }
                            }
                    );
                }


                else if(checkInit!=null && uhu1.equals("1")){
                    final String finalCheckInit = checkInit;
                    builder.setPositiveButton("set key", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(helper.checkKunci(address)==false){
                                helper.storeKey(address, finalCheckInit.substring(12,finalCheckInit.length()), finalCheckInit.substring(0, 12));
                                Toast.makeText(getApplicationContext(),"Key is initiated succesfully " + finalCheckInit, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Keys for this contact has been set", Toast.LENGTH_LONG).show();
                            }
                            dialogInterface.cancel();
                        }
                    });
                }

                else if (txtMsg == null && checkInit == null) {
                    builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (tmpList.get(+position).get(Function.KEY_TYPE).equals("1")) {
                                try {
                                    helper.updatePrKey(tmpList.get(+position).get(Function.KEY_MSG).substring(0,12)/*encDecr.decrypt(tmpList.get(+position).get(Function.KEY_MSG), helper.callPrKey(address)).substring(0, 5)*/, address);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (tmpList.get(+position).get(Function.KEY_TYPE).equals("2")) {
                                try {
                                    helper.updateTempPuKey(tmpList.get(+position).get(Function.KEY_MSG).substring(0,12)/*encDecr.decrypt(tmpList.get(+position).get(Function.KEY_MSG), helper.callTempPuKey(address)).substring(0, 5)*/, address);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }



                            dialogInterface.cancel();
                        }
                    });
                    builder.setNegativeButton("back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                }
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });



 /*       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long uhu = (listView.getItemIdAtPosition(i));

                //String aha = Function.getKeyMsg();
                Toast.makeText(getApplicationContext(), uhu+" is selected", Toast.LENGTH_LONG).show();
            }
        });
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reset_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.resetKey:
                helper.updatePuKey(helper.callInitPuKey(address), address);
                helper.updatePrKey(helper.callInitPrKey(address), address);
                helper.updateTempPuKey(helper.callInitPuKey(address), address);
                Toast.makeText(getBaseContext(),"Keys are reset", Toast.LENGTH_LONG).show();
                return true;

            case R.id.checkKey:
                AlertDialog.Builder builder = new AlertDialog.Builder(Chat.this);
                String aua = helper.callPuKey(address);
                String aia = helper.callPrKey(address);
                builder.setMessage("X key= "+aua+" \nY key= "+aia);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            default: return super.onOptionsItemSelected(item);
        }

    }




    class LoadSms extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tmpList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            try {
                Uri uriInbox = Uri.parse("content://sms/inbox");
                Cursor inbox = getContentResolver().query(uriInbox, null, "thread_id=" + thread_id_main, null, null);
                Uri uriSent = Uri.parse("content://sms/sent");
                Cursor sent = getContentResolver().query(uriSent, null, "thread_id=" + thread_id_main, null, null);
                Cursor c = new MergeCursor(new Cursor[]{inbox,sent}); // Attaching inbox and sent sms



                if (c.moveToFirst()) {
                    for (int i = 0; i < c.getCount(); i++) {
                        String phone = "";
                        String _id = c.getString(c.getColumnIndexOrThrow("_id"));
                        String thread_id = c.getString(c.getColumnIndexOrThrow("thread_id"));
                        String msg = c.getString(c.getColumnIndexOrThrow("body"));
                        String type = c.getString(c.getColumnIndexOrThrow("type"));
                        String timestamp = c.getString(c.getColumnIndexOrThrow("date"));
                        phone = c.getString(c.getColumnIndexOrThrow("address"));
                        //String decr = encDecr.decrypt(msg, "halalala");

                        tmpList.add(Function.mappingInbox(_id, thread_id, name, phone, msg, type, timestamp, Function.converToTime(timestamp)));
                        c.moveToNext();
                    }
                }
                c.close();

            }catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Collections.sort(tmpList, new MapComparator(Function.KEY_TIMESTAMP, "asc"));

            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if(!tmpList.equals(smsList))
            {
                smsList.clear();
                smsList.addAll(tmpList);
                adapter = new ChatAdapter(Chat.this, smsList);
                listView.setAdapter(adapter);

            }





        }
    }




    public void startLoadingSms()
    {
        final Runnable r = new Runnable() {
            public void run() {

                loadsmsTask = new LoadSms();
                loadsmsTask.execute();

                handler.postDelayed(this, 5000);
            }
        };
        handler.postDelayed(r, 0);
    }
}







class ChatAdapter extends BaseAdapter {
    EncDecr encDecr = new EncDecr();

    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    public ChatAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
        activity = a;
        data = d;
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ChatViewHolder holder = null;
        if (convertView == null) {
            holder = new ChatViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.chat_item, parent, false);


            holder.txtMsgYou = (TextView)convertView.findViewById(R.id.txtMsgYou);
            holder.lblMsgYou = (TextView)convertView.findViewById(R.id.lblMsgYou);
            holder.timeMsgYou = (TextView)convertView.findViewById(R.id.timeMsgYou);
            holder.lblMsgFrom = (TextView)convertView.findViewById(R.id.lblMsgFrom);
            holder.timeMsgFrom = (TextView)convertView.findViewById(R.id.timeMsgFrom);
            holder.txtMsgFrom = (TextView)convertView.findViewById(R.id.txtMsgFrom);
            holder.msgFrom = (LinearLayout)convertView.findViewById(R.id.msgFrom);
            holder.msgYou = (LinearLayout)convertView.findViewById(R.id.msgYou);

            convertView.setTag(holder);
        } else {
            holder = (ChatViewHolder) convertView.getTag();
        }
        holder.txtMsgYou.setId(position);
        holder.lblMsgYou.setId(position);
        holder.timeMsgYou.setId(position);
        holder.lblMsgFrom.setId(position);
        holder.timeMsgFrom.setId(position);
        holder.txtMsgFrom.setId(position);
        holder.msgFrom.setId(position);
        holder.msgYou.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {
            //String decry = encDecr.decrypt(song.get(Function.KEY_MSG), "halalala");

            if(song.get(Function.KEY_TYPE).contentEquals("1"))
            {
                holder.lblMsgFrom.setText(song.get(Function.KEY_NAME));
                holder.txtMsgFrom.setText(song.get(Function.KEY_MSG));
                holder.timeMsgFrom.setText(song.get(Function.KEY_TIME));
                holder.msgFrom.setVisibility(View.VISIBLE);
                holder.msgYou.setVisibility(View.GONE);
            }else{
                holder.lblMsgYou.setText("You");

                //holder.lblMsgYou.setText(song.get(Function.KEY_NAME));
                holder.txtMsgYou.setText(song.get(Function.KEY_MSG));
                holder.timeMsgYou.setText(song.get(Function.KEY_TIME));
                holder.msgFrom.setVisibility(View.GONE);
                holder.msgYou.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {}


        return convertView;




    }

}


class ChatViewHolder {
    LinearLayout msgFrom, msgYou;
    TextView txtMsgYou, lblMsgYou, timeMsgYou, lblMsgFrom, txtMsgFrom, timeMsgFrom;
}

