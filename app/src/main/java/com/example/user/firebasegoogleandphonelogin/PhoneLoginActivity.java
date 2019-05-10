package com.example.user.firebasegoogleandphonelogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    Button sendVerificationCodeBtn,verifyCodeBtn;
    EditText phoneNumberInput,verificationCodeInput;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        loadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        sendVerificationCodeBtn=findViewById(R.id.send_verification_code_button);
        verifyCodeBtn=findViewById(R.id.verify_code_button);
        phoneNumberInput=findViewById(R.id.inout_phn_number_edit_text);
        phoneNumberInput.setText("+880");
        verificationCodeInput=findViewById(R.id.get_varification_number_edit_text);


        sendVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber=phoneNumberInput.getText().toString();


                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this,"Insert Your Phone Number",Toast.LENGTH_SHORT).show();
                }else {

                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Wait,while getting your verification code");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendVerificationCodeBtn.setVisibility(View.INVISIBLE);
                phoneNumberInput.setVisibility(View.INVISIBLE);

                String verificationCode=verificationCodeInput.getText().toString();

                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneLoginActivity.this,"Inter valid code" ,Toast.LENGTH_SHORT).show();
                }else {

                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Wait,while verifying the code");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            //This method is called when the verification will be completed successfully
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                //This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.


                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this,"Invalid, enter your phone number with country code" ,Toast.LENGTH_SHORT).show();

                sendVerificationCodeBtn.setVisibility(View.VISIBLE);
                verifyCodeBtn.setVisibility(View.INVISIBLE);

                phoneNumberInput.setVisibility(View.VISIBLE);
                verificationCodeInput.setVisibility(View.INVISIBLE);

            }

            public void onCodeSent(String verificationId,PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this,"Code has been sent" ,Toast.LENGTH_SHORT).show();

                sendVerificationCodeBtn.setVisibility(View.INVISIBLE);
                verifyCodeBtn.setVisibility(View.VISIBLE);

                phoneNumberInput.setVisibility(View.INVISIBLE);
                verificationCodeInput.setVisibility(View.VISIBLE);
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            sendUserToHome();

                        } else {

                            String message=task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void sendUserToHome() {
        Intent intent=new Intent(PhoneLoginActivity.this,HomeActivity.class);
        startActivity(intent);
    }
}
