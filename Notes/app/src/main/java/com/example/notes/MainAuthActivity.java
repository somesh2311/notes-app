package com.example.notes;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;

public class MainAuthActivity extends AppCompatActivity implements FingerPrintAuthCallback {
    FingerPrintAuthHelper mFingerPrintAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_auth);
        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }


    @Override
    public void onNoFingerPrintHardwareFound() {
        Toast.makeText(this, "No Finger Print sensor found", Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onNoFingerPrintRegistered() {
        Toast.makeText(this, "Register a Finger Print", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Settings.ACTION_FINGERPRINT_ENROLL));
    }

    @Override
    public void onBelowMarshmallow() {
        Toast.makeText(this, "Your Device doesn't support finger print aunthetication", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainAuthActivity.this, MainActivity.class));
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {    //Parse the error code for recoverable/non recoverable error.
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                //Cannot recognize the fingerprint scanned.
                Toast.makeText(this, "Cannot Recognized!", Toast.LENGTH_SHORT).show();
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                //This is not recoverable error. Try other options for user authentication. like pin, password.
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                //Any recoverable error. Display message to the user.
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
