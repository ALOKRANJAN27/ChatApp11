package com.example.chatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.mbms.MbmsErrors;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{

    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Button LoginButton,PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView NeedNewAccountLink,ForgetPasswordLink;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();




        InitializeFields();

       ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               final EditText resetMail =new EditText(view.getContext());
               AlertDialog.Builder passwordResetDialog =new AlertDialog.Builder(view.getContext(),R.style.Theme_AppCompat_Light_Dialog_Alert);
               passwordResetDialog.setTitle("Reset Password ?");
               passwordResetDialog.setMessage("Enter your Email To Receive Reset Link");
               passwordResetDialog.setView(resetMail);


               passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       //Extract the email and send reset link
                       String mail = resetMail.getText().toString();

                       if(TextUtils.isEmpty(mail)){
                           Toast.makeText(LoginActivity.this, "Email Address Is Required To Reset Password", Toast.LENGTH_SHORT).show();
                           return;
                       }


                       mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();

                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(LoginActivity.this, "Error! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });
                   }

                   });
               passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       // Close the dialog
                   }
               });
               passwordResetDialog.create().show();
           }
       });





        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToRegisterActivity();
            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AllowUserToLogin();

            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent phoneloginIntent = new Intent(LoginActivity.this , PhoneLoginActivity.class);
                startActivity(phoneloginIntent);
            }
        });

    }

    private void AllowUserToLogin()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password...",Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle(("Sign In"));
            loadingBar.setMessage("Please Wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Logged in Successful...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error :" + message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();


                            }

                        }
                    });

        }
    }

    private void InitializeFields()
    {
        LoginButton =(Button) findViewById(R.id.login_button);
        PhoneLoginButton =(Button) findViewById(R.id.phone_login_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword =(EditText) findViewById(R.id.login_password);
        NeedNewAccountLink =(TextView) findViewById(R.id.need_new_account_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);

    }







    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent RegisterIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(RegisterIntent);
    }
}