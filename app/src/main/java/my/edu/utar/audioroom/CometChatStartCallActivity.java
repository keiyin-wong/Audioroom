package my.edu.utar.audioroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallManager;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CometChatWidget;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.audioroom.constants.AppConfig;

public class CometChatStartCallActivity extends AppCompatActivity {

    RelativeLayout relativeCallLayout;
    String groupId;
    Group group;
    boolean showMuteAudioButton;
    boolean showSwitchCameraButton;
    boolean showPauseVideoButton;
    boolean audioCallOnly;
    //User user = CometChat.getLoggedInUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_chat_start_call);
        groupId = getIntent().getStringExtra("groupId");
        findGroup(groupId);

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
        checkAndRequestPermissions();


        relativeCallLayout = findViewById(R.id.callRelativeLayout);



    }

    public void startChat(){
        if(group.getScope()==null||group.getScope().equals("participant")){
            showMuteAudioButton = true;
            showSwitchCameraButton = false;
            audioCallOnly = true;
            showPauseVideoButton = false;
        }else {
            showMuteAudioButton = true;
            showSwitchCameraButton = true;
            audioCallOnly = false;
            showPauseVideoButton = true;
        }
        CallSettings callSettings = new CallSettings.CallSettingsBuilder(this,relativeCallLayout)
                .setSessionId(groupId)
                .setAudioOnlyCall(audioCallOnly)
                .enableDefaultLayout(true)
                .showMuteAudioButton(showMuteAudioButton)
                .showEndCallButton(true)
                .showAudioModeButton(true)
                .showPauseVideoButton(showPauseVideoButton)
                .setMode(CallSettings.MODE_DEFAULT)
                .showSwitchCameraButton(showSwitchCameraButton)
                .build();
        CometChat.startCall(callSettings,
                new CometChat.OngoingCallListener() {
                    @Override
                    public void onUserJoined(User user) {
                        Log.d("Testing", "onUserJoined: Name "+user.getName());
                    }

                    @Override
                    public void onUserLeft(User user) {
                        Log.d("Testing", "onUserLeft: "+user.getName());
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Log.d("Testing", "onError: "+e.getMessage());
                    }

                    @Override
                    public void onCallEnded(Call call) {
                        Log.d("Testing", "onCallEnded: ");
                        finish();
                    }

                });
    }

    private void findGroup(String Guid) {
        CometChat.getGroup(Guid, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group g) {
                Log.d("Startchatactivity", "Group details fetched successfully: " + g.toString());
                group = g;
                startChat();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Group details fetching failed with exception: " + e.getMessage());

            }
        });
    }

    private  boolean checkAndRequestPermissions() {
        int permissionAudio = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        int permissonCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissonCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),100);
            return false;
        }
        return true;
    }

}