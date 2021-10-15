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
import android.widget.ImageButton;
import android.widget.Toast;

import my.edu.utar.audioroom.services.Auth;

public class Register extends AppCompatActivity {

    private Auth auth = new Auth();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText ed1 = (EditText) findViewById(R.id.register_email);
        EditText ed2 = (EditText) findViewById(R.id.register_password);
        EditText ed3 = (EditText) findViewById(R.id.register_name);
        Button register = (Button) findViewById(R.id.registerB);

        //go back to landing page
        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Landingpage.class);
                startActivity(intent);
            }
        });

        //hide password
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

        //register button action
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = ed3.getText().toString();
                String email = ed1.getText().toString();
                String password = ed2.getText().toString();
                if(username.isEmpty()){
                    Toast.makeText(Register.this, "Username is required",
                            Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty()){
                    Toast.makeText(Register.this, "Email is required",
                            Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty() || password.length() < 6){
                    Toast.makeText(Register.this, "Password must at least 6 characters",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent loadingPage = new Intent(Register.this, Loading.class);
                    loadingPage.putExtra("request","register");
                    loadingPage.putExtra("email",email);
                    loadingPage.putExtra("username",username);
                    loadingPage.putExtra("password",password);
                    startActivity(loadingPage);
                    //auth.Register(Register.this,username,email,password);
                }

            }
        });
    }

    //disable back button of Android
    @Override
    public void onBackPressed(){

    }
}