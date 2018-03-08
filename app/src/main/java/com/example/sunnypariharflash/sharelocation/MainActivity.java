package com.example.sunnypariharflash.sharelocation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
Button btn,btn2;
EditText editText,edt2;
FirebaseAuth auth;
private static final int RC_SIGN_IN = 123;
    private boolean mVerificationInProgress = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btnnext);
        editText = findViewById(R.id.editphone);
        phoneNumber = editText.getText().toString();
        btn2 = findViewById(R.id.btnverfiy);

        edt2 = findViewById(R.id.otp);
auth = FirebaseAuth.getInstance();
mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
    @Override
    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
        Log.d("Tag","OnVerificationCompleted :"+phoneAuthCredential);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    @Override
    public void onVerificationFailed(FirebaseException e) {
        Log.w("Tag", "onVerificationFailed", e);
        if (e instanceof FirebaseAuthInvalidCredentialsException) {
           editText.setError("Invalid phone number.");
        } else if (e instanceof FirebaseTooManyRequestsException) {
            Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                    Snackbar.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onCodeSent(String verificationId,
                           PhoneAuthProvider.ForceResendingToken token) {
        Log.d("Tag", "onCodeSent:" + verificationId);
        mVerificationId = verificationId;
        mResendToken = token;
    }
};
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validatePhoneNumber()){
                    return;
                }
                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setMessage("Verifying......");
                dialog.setMax(100);
                dialog.show();
                startPhoneNumberVerification(editText.getText().toString());
                dialog.dismiss();
            }
        });
/*btn2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String code = edt2.getText().toString();
            if(TextUtils.isEmpty(code)){
                edt2.setError("Invalid field");
            }

            verifyPhoneNumberWithCode(mVerificationId,code);

        }
    });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser().getPhoneNumber()!=null){
            startActivity(new Intent(MainActivity.this,MapsActivity.class));
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(MainActivity.this, "OTP has been verified", Toast.LENGTH_SHORT).show();

                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                               edt2.setError("Invalid code.");

                            }

                        }
                    }
                });
    }
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = editText.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
          editText.setError("Invalid phone number.");
            return false;
        }
        return true;
    }


}