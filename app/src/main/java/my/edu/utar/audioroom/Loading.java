package my.edu.utar.audioroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import my.edu.utar.audioroom.services.Auth;

public class Loading extends AppCompatActivity {

    private Auth auth = new Auth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        String request = getIntent().getExtras().getString("request");
        String email = getIntent().getExtras().getString("email");
        String password = getIntent().getExtras().getString("password");
        String username = getIntent().getExtras().getString("username");


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(request.equals("login")){
                    auth.Signin(Loading.this,email,password);
                }
                else if(request.equals("register")){
                    auth.Register(Loading.this,username,email,password);
                }
                else if(request.equals("logout"))
                    auth.Signout(Loading.this);
                else
                    auth.signInAnonymously(Loading.this);

            }
        },500);
    }
}