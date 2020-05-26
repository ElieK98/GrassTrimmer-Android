package com.example.grasstrimmer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grasstrimmer.Model.Helpers;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class SplashScreen extends AppCompatActivity {

    SignInButton signInButton;
    TextView welcomeTextView;
    private static final String TAG="GoogleActivity";
    private static final int RC_SIGN_IN=100;
    //declare Auth
    private FirebaseAuth mAuth;



    public static int TIME_OUT=2000;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen1);

       // startActivity(new Intent(this,MainActivity.class));
//        Handler handler=new Handler();

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, TIME_OUT);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        mAuth=FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.signInButton);
        welcomeTextView = findViewById(R.id.weclomeTextView);

        TextView textView = (TextView) signInButton.getChildAt(0);
        textView.setText("Sign In With Google");
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null  && !Helpers.getFromPreferences("displayName", getApplicationContext()).equals("")) {
            signInButton.setVisibility(SignInButton.INVISIBLE);
            welcomeTextView.setVisibility(TextView.VISIBLE);
            welcomeTextView.setText(String.format("Welcome Back %s", Helpers.getFromPreferences("displayName", getApplicationContext())));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, TIME_OUT);
        } else {
            signInButton.setVisibility(SignInButton.VISIBLE);
            welcomeTextView.setVisibility(TextView.INVISIBLE);
        }
    }
    @Override
    public void onStart() {

        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

   /* private void updateUI(FirebaseUser user){

    }*/
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG,"signInWithCredential:success");
                    FirebaseUser user=mAuth.getCurrentUser();
                    final GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    Helpers.addToPreferences("userID",account.getId(), getApplicationContext() );
                    Helpers.addToPreferences("displayName", user.getDisplayName(), getApplicationContext());
                    Helpers.addToPreferences("email", user.getEmail(), getApplicationContext());

                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
//                    Toast.makeText(SplashScreen.this, "Authentication with Firebase Successful", Toast.LENGTH_LONG).show();
                    //updateUI(user);
                }else{
                    Log.w(TAG,"signInWithCredential:failure",task.getException());
                    Toast.makeText(SplashScreen.this, "Authentication with Firebase FAILED", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
