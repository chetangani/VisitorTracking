package com.tvd.visitortracking;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.tvd.visitortracking.values.FunctionsCall;
import com.tvd.visitortracking.values.GetSetValues;

import static com.tvd.visitortracking.values.ConstantValues.LOGIN_FAILURE;
import static com.tvd.visitortracking.values.ConstantValues.LOGIN_SUCCESSFUL;

public class LoginActivity extends AppCompatActivity {

    Button sign_in_btn;
    EditText et_login_id, et_password;
    String Login_ID, Login_Password;
    ProgressDialog progressDialog;

    GetSetValues getSetValues;
    FunctionsCall functionsCall;

    private final Handler mHandler;
    {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LOGIN_SUCCESSFUL:
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;

                    case LOGIN_FAILURE:
                        break;
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_login);

        et_login_id = (EditText) findViewById(R.id.et_login_id);
        et_password = (EditText) findViewById(R.id.et_password);
        sign_in_btn = (Button) findViewById(R.id.sign_in_btn);

        getSetValues = new GetSetValues();
        functionsCall = new FunctionsCall();

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                /*Login_ID = et_login_id.getText().toString();
                if (functionsCall.checkEditTextValue(Login_ID, et_login_id, "Enter Login ID")) {
                    Login_Password = et_password.getText().toString();
                    if (functionsCall.checkEditTextValue(Login_Password, et_password, "Enter Password")) {
                        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging In please wait..", true);
                        SendingData sendingData = new SendingData();
                        SendingData.Login_Details loginDetails = sendingData.new Login_Details(mHandler, getSetValues);
                        loginDetails.execute();
                    }
                }*/
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
