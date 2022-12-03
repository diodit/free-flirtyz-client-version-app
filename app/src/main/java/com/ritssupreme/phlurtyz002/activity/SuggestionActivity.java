package com.ritssupreme.phlurtyz002.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.ritssupreme.phlurtyz002.R;
import com.ritssupreme.phlurtyz002.core.BaseActivity;
import com.ritssupreme.phlurtyz002.utils.GMailSender;

public class SuggestionActivity extends BaseActivity {


    private TextView suggestFlirtyz;

    private TextView suggestionDescription;
    private EditText ideaBox;
    private Button sendSuggestion;

    //mail reciever
    private static String MAIL_SENDER_RECEIVER = "support@flirtyz.com";

    //    replace with the email and password of the sender mail
    private static String MAIL_SENDER_USERNAME = "flirtyzapp@gmail.com";
    private static String MAIL_SENDER_PASSWORD = "Dirtyphlurtyz420";

//    private static String MAIL_SENDER_USERNAME = "support@flirtyz.com";
//    private static String MAIL_SENDER_PASSWORD = "Phlurtyz$etup!18";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggestion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();

        Typeface faceBigSurprise = Typeface.createFromAsset(getAssets(), "font/bigSurprise.ttf");

        suggestFlirtyz.setTypeface(faceBigSurprise);

        Typeface faceMyriadSemiBold = Typeface.createFromAsset(getAssets(), "font/myriadProSemibold.otf");

        suggestionDescription.setTypeface(faceMyriadSemiBold);


    }

    private void initView() {

        suggestFlirtyz = (TextView) findViewById(R.id.suggestion_header);

        suggestionDescription = (TextView) findViewById(R.id.suggestion_description);

        ideaBox = (EditText) findViewById(R.id.idea_box);
        sendSuggestion = (Button) findViewById(R.id.send_suggestion);
        sendSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline(SuggestionActivity.this)) {
                    if(ideaBox.getText().toString().isEmpty()){
                        Toast.makeText(SuggestionActivity.this, "The idea box cannot be blank.", Toast.LENGTH_SHORT).show();
                    }else{
                        sendEmailToFlirty(ideaBox.getText().toString());
                    }

                }else{
                    Toast.makeText(SuggestionActivity.this, "Please check your network connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendEmailToFlirty(final String messageBody) {
        Toast.makeText(SuggestionActivity.this, "Sending message...", Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(@NonNull Message msg) {

                AlertDialog alertDialog = new AlertDialog.Builder(SuggestionActivity.this).create();
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                if(msg.arg1==1)
                {
                    ideaBox.setText("");
                    alertDialog.setTitle("Success!");
                    alertDialog.setMessage("Your suggestion was sent successfully.");
                    alertDialog.show();
                }else{
                    alertDialog.setTitle("Oops!");
                    alertDialog.setMessage("Your suggestion failed to deliver. Please retry");
                    alertDialog.show();
                }
                return false;
            }
        });

        final Message msg = new Message();

        final Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {

                    GMailSender sender = new GMailSender(MAIL_SENDER_USERNAME,
                            MAIL_SENDER_PASSWORD);
                    sender.sendMail("Suggestion", messageBody,
                            MAIL_SENDER_USERNAME, MAIL_SENDER_RECEIVER);
                    msg.arg1=1;
                    handler.sendMessage(msg);

                } catch (Exception e) {
                    msg.arg1=0;
                    handler.sendMessage(msg);
                    Log.e("ERROR_MAIL", e.getMessage());
                }
            }

        });
        thread.start();


    }


    public boolean isOnline(Context context){

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
