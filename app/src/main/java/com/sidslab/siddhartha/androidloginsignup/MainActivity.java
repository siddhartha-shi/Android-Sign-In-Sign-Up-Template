package com.sidslab.siddhartha.androidloginsignup;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.sidslab.siddhartha.androidloginsignup.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.logInBtn)
    AppCompatButton logInBtn;
    @BindView(R.id.signUpBtn)
    AppCompatButton signUpBtn;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.hideAbleName)
    TextInputLayout hideAbleName;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.contactNumber)
    EditText contactNumber;
    @BindView(R.id.hideAbleContactNumber)
    TextInputLayout hideAbleContactNumber;
    @BindView(R.id.logInRegisterBtn)
    AppCompatButton logInRegisterBtn;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        actionBarModification();
        defaultUIConfigAndBtnImplementation();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        signUpBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
        logInBtn.setBackgroundColor(Color.parseColor("#A9D899"));
        logInRegisterBtn.setText("Sign In");
        logInRegisterBtn.setTag("login");
        hideAbleName.setVisibility(View.INVISIBLE);
        hideAbleContactNumber.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @SuppressLint("SetTextI18n")
    private void actionBarModification() {
        //ActionBar actionbar = getActionBar();
        android.support.v7.app.ActionBar actionbar = getSupportActionBar();
        TextView textview = new TextView(MainActivity.this);
        LayoutParams layoutparams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textview.setLayoutParams(layoutparams);
        textview.setText("LogIn / SignUp");
        //textview.setTextColor(Color.MAGENTA);
        textview.setGravity(Gravity.CENTER);
        textview.setTextSize(20);
        //noinspection ConstantConditions
        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(textview);
    }

    @SuppressLint("SetTextI18n")
    private void defaultUIConfigAndBtnImplementation() {
        logInRegisterBtn.setTag("login");
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                signUpBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                logInBtn.setBackgroundColor(Color.parseColor("#A9D899"));
                logInRegisterBtn.setText("Sign In");
                logInRegisterBtn.setTag("login");
                hideAbleName.setVisibility(View.INVISIBLE);
                hideAbleContactNumber.setVisibility(View.GONE);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                logInBtn.setBackgroundColor(Color.parseColor("#FFFFFF"));
                signUpBtn.setBackgroundColor(Color.parseColor("#A9D899"));
                logInRegisterBtn.setText("Create Account");
                logInRegisterBtn.setTag("reg");
                hideAbleName.setVisibility(View.VISIBLE);
                hideAbleContactNumber.setVisibility(View.VISIBLE);
                name.requestFocus();
            }
        });

        logInRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MainActivity.this, LoginWelcomeActivity.class);
                if (logInRegisterBtn.getTag().equals("login")) {
                    if (logInValidation()) {
                        RealmResults<User> persons = realm.where(User.class).equalTo("email", email.getText().toString()).findAll();
                        persons.load();
                        if (persons.isEmpty()) {
                            //Toast.makeText(MainActivity.this, "No user found with this email. Please sign up first.", Toast.LENGTH_LONG).show();
                            alertDialog("You are not registered. Please Sign Up now.");
                        } else {
                            for (User user : persons) {
                                if (user.getPassword().equals(password.getText().toString())){
                                    intent.putExtra("name", user.getName());
                                    intent.putExtra("email", user.getEmail());
                                    intent.putExtra("contact", user.getContactNumber());
                                    startActivity(intent);
                                    password.getText().clear();
                                } else {
                                        alertDialog("Please enter correct password.");
                                }
                            }
                        }
                    }

                } else if (logInRegisterBtn.getTag().equals("reg")) {
                    if (signUpValidation()) {
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("email", email.getText().toString());
                        intent.putExtra("contact", contactNumber.getText().toString());
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm bgRealm) {
                                User user = bgRealm.createObject(User.class, email.getText().toString());
                                user.setName(name.getText().toString());
                                //user.setEmail(email.getText().toString());
                                user.setPassword(password.getText().toString());
                                user.setContactNumber(contactNumber.getText().toString());
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                // Transaction was a success.
                                startActivity(intent);
                                password.getText().clear();
                                name.getText().clear();
                                contactNumber.getText().clear();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(@NonNull Throwable error) {
                                // Transaction failed and was automatically canceled.
                                if (error.getMessage().contains("Primary key value already exists")) {
                                    alertDialog("You are already registered. Please give new email to Sign Up.");
                                    //Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                Log.d("Sid", "onError: "+error.toString());
                            }
                        });
                    }
                }
            }
        });
    }

    private void alertDialog(String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(title);
                /*alertDialogBuilder.setPositiveButton("Okay",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                            }
                        });*/
        alertDialogBuilder.setNegativeButton("Close",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private boolean logInValidation() {
        if (!email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty()) {
            if (!email.getText().toString().matches("^[\\w-+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$")) {
                email.requestFocus();
                email.setError("Please give a valid email.");
                return false;
            } else if (!password.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$")) {
                password.requestFocus();
                password.setError("Minimum 6 characters log password should contains digits, uppercase & lowercase and special character.");
                return false;
            }
            return true;
        } else {
            if (email.getText().toString().isEmpty()) {
                email.requestFocus();
                email.setError("Please give your email.");
            } else if (password.getText().toString().isEmpty()) {
                password.requestFocus();
                password.setError("Please give your password.");
            }
        }
        return false;
    }

    private boolean signUpValidation() {
        if (!name.getText().toString().isEmpty() && !email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() && !contactNumber.getText().toString().isEmpty()) {
            if (!name.getText().toString().matches("(?=.*\\S)[a-zA-Z0-9\\s]*")) {
                name.requestFocus();
                name.setError("Name should be Alphanumeric.");
                return false;
            } else
                if (!email.getText().toString().matches("^[\\w-+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$")) {
                email.requestFocus();
                email.setError("Please give a valid email.");
                return false;
            } else if (!password.getText().toString().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$")) {
                password.requestFocus();
                password.setError("Minimum 6 characters log password should contains digits, uppercase & lowercase and special character.");
                return false;
            } else if (!contactNumber.getText().toString().matches("^[1-9]{2}[0-9]{8}$")) {
                contactNumber.requestFocus();
                contactNumber.setError("Contact number can not start with 0 and must be 10 digits.");
                return false;
            }
            return true;
        } else {
            if (name.getText().toString().isEmpty()) {
                name.requestFocus();
                name.setError("Please give your good name.");
            } else if (email.getText().toString().isEmpty()) {
                email.requestFocus();
                email.setError("Please give your email.");
            } else if (password.getText().toString().isEmpty()) {
                password.requestFocus();
                password.setError("Please set a password.");
            } else if (contactNumber.getText().toString().isEmpty()) {
                contactNumber.requestFocus();
                contactNumber.setError("Please give your 10 digit contact number.");
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        (new AlertDialog.Builder(this))
                .setTitle("Confirm action")
                .setMessage("Do you want to close the app?")
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