package com.sidslab.siddhartha.androidloginsignup;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginWelcomeActivity extends AppCompatActivity {

    @BindView(R.id.result)
    TextView result;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_welcome);
        ButterKnife.bind(this);
        actionBarModification();
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String contact = getIntent().getStringExtra("contact");
        result.setTextSize(25);
        result.setText("\n\nHello, "+name+"\n\nEmail: "+email+"\n\nContact: "+contact);
    }

    @SuppressLint("SetTextI18n")
    private void actionBarModification() {
        //ActionBar actionbar = getActionBar();
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        TextView textview = new TextView(LoginWelcomeActivity.this);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textview.setLayoutParams(layoutparams);
        textview.setText("Welcome");
        //textview.setTextColor(Color.MAGENTA);
        textview.setGravity(Gravity.CENTER);
        textview.setTextSize(18);
        //noinspection ConstantConditions
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(textview);
    }

    @Override
    public void onBackPressed() {
        (new AlertDialog.Builder(this))
                .setTitle("Confirm action")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}