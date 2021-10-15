package my.edu.utar.audioroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;

public class JoinRoom extends AppCompatActivity {

    EditText roomIdEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        roomIdEditText = findViewById(R.id.room_id_1);

    }

    public void joinRoom(View view) {
        String roomId = roomIdEditText.getText().toString();

        CometChat.getGroup(roomId, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group g) {
                    Toast.makeText(JoinRoom.this, "Room succesfully joined!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(JoinRoom.this, GroupHomePage.class);
                    intent.putExtra("groupId", g.getGuid());
                    startActivity(intent);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Group details fetching failed with exception: " + e.getMessage());
                Toast.makeText(JoinRoom.this, "Room does not exist!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void goBack(View view) {
        Intent intent = new Intent(JoinRoom.this, UserHomepage.class);
        startActivity(intent);
    }
}