package my.edu.utar.audioroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import my.edu.utar.audioroom.services.Auth;

public class Landingpage extends AppCompatActivity {

    private Auth auth = new Auth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpage);

        Button guest = (Button) findViewById(R.id.guestB);
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loadingPage = new Intent(Landingpage.this, Loading.class);
                loadingPage.putExtra("request","signInAnonymously");
                startActivity(loadingPage);
                //auth.signInAnonymously(Landingpage.this);
            }
        });

        Button signIn = (Button) findViewById(R.id.loginB);
        Button register = (Button) findViewById(R.id.signUpB);
        EditText ed1 = (EditText)findViewById(R.id.login_email); //email text box
        EditText ed2=(EditText)findViewById(R.id.login_password); //password text box

        //Hide password
        CheckBox shPassword = (CheckBox) findViewById(R.id.showPassword);
        shPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ed2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                    ed2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Landingpage.this,Register.class);
                startActivity(intent);
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ed1.getText().toString();
                String password = ed2.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(Landingpage.this, "Please enter an valid email",
                            Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(Landingpage.this, "Please enter an valid password",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent loadingPage = new Intent(Landingpage.this, Loading.class);
                    loadingPage.putExtra("request","login");
                    loadingPage.putExtra("email",email);
                    loadingPage.putExtra("password",password);
                    startActivity(loadingPage);
                    //auth.Signin(Landingpage.this,email,password);
                }


            }
        });
    }
}