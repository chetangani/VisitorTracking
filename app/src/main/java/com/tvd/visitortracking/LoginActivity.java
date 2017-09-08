package com.tvd.visitortracking;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.tvd.visitortracking.values.FunctionsCall;
import com.tvd.visitortracking.values.GetSetValues;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.tvd.visitortracking.values.ConstantValues.LOGIN_FAILURE;
import static com.tvd.visitortracking.values.ConstantValues.LOGIN_SUCCESSFUL;

public class LoginActivity extends AppCompatActivity {

    private static final int RequestPermissionCode = 1;

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermissionsMandAbove();
            }
        }, 1000);

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

    @TargetApi(23)
    private void checkPermissionsMandAbove() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(LoginActivity.this, new String[]
                {
                        ACCESS_FINE_LOCATION,
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }

    private boolean checkPermission() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean ReadLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadCameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (ReadLocationPermission && ReadCameraPermission && ReadStoragePermission) {
                    } else {
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
