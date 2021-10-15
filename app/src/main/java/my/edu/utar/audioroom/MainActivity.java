package my.edu.utar.audioroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.CometChatWidget;

import my.edu.utar.audioroom.constants.AppConfig;

public class MainActivity extends AppCompatActivity {

    private static int splash_screen = 5000;

    Animation chatAnimOut = new AlphaAnimation(1,0);
    Animation chatAnimIn = new AlphaAnimation(0,1);

    Animation chatAnimOut2 = new AlphaAnimation(1,0);
    Animation chatAnimIn2= new AlphaAnimation(0,1);

    Animation chatAnimOut3 = new AlphaAnimation(1,0);
    Animation chatAnimIn3= new AlphaAnimation(0,1);



    ImageView chat1,chat2,chat3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CometChatWidget.init(this, AppConfig.AppDetails.APP_ID, AppConfig.AppDetails.REGION,AppConfig.AppDetails.AUTH_KEY,new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d("Testing", "Initialization completed successfully");
            }
            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Initialization failed with exception: " + e.getMessage());
            }
        });

        chatAnimOut.setInterpolator(new AccelerateInterpolator());
        chatAnimOut.setStartOffset(3000);
        chatAnimOut.setDuration(1500);
        chatAnimIn.setInterpolator(new DecelerateInterpolator());
        chatAnimIn.setStartOffset(40);
        chatAnimIn.setDuration(2000);

        chatAnimOut2.setInterpolator(new AccelerateInterpolator());
        chatAnimOut2.setStartOffset(4000);
        chatAnimOut2.setDuration(1500);
        chatAnimIn2.setInterpolator(new DecelerateInterpolator());
        chatAnimIn2.setStartOffset(3000);
        chatAnimIn2.setDuration(2000);

        chatAnimOut3.setInterpolator(new AccelerateInterpolator());
        chatAnimOut3.setStartOffset(3500);
        chatAnimOut3.setDuration(1500);
        chatAnimIn3.setInterpolator(new DecelerateInterpolator());
        chatAnimIn3.setStartOffset(2000);
        chatAnimIn3.setDuration(2000);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(chatAnimOut);
        animation.addAnimation(chatAnimIn);
        chat1 = findViewById(R.id.chat1);
        chat1.setAnimation(animation);

        AnimationSet animation2 = new AnimationSet(false);
        animation2.addAnimation(chatAnimOut2);
        animation2.addAnimation(chatAnimIn2);
        chat2 = findViewById(R.id.chat2);
        chat2.setAnimation(animation2);

        AnimationSet animation3 = new AnimationSet(false);
        animation3.addAnimation(chatAnimOut3);
        animation3.addAnimation(chatAnimIn3);
        chat3 = findViewById(R.id.chat3);
        chat3.setAnimation(animation3);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,Landingpage.class);
                startActivity(intent);
                finish();
            }
        },splash_screen);
    }
}