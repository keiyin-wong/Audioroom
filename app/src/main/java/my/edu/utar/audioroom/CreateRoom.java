package my.edu.utar.audioroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;

public class CreateRoom extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String room_type = null;
    String GUID = null;
    String password = "";
    private String groupType = CometChatConstants.GROUP_TYPE_PUBLIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        EditText password_field = (EditText) findViewById(R.id.group_password);
        EditText description_field = (EditText) findViewById(R.id.room_description);

        Spinner spinner = findViewById(R.id.roomtype);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.roomtype, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        Button cancel_btn = findViewById(R.id.cancel);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CreateRoom.this, UserHomepage.class);
                startActivity(intent);
            }
        });


        Button create_room_btn = findViewById(R.id.create_group);
        create_room_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText groupName = (EditText) findViewById(R.id.room_name);
                String groupNameString = groupName.getText().toString();
                password = password_field.getText().toString();
                long unixTime = System.currentTimeMillis() / 1000L;
                GUID = unixTime + "";


                if (room_type.equals("Private")) {
                    groupType = CometChatConstants.GROUP_TYPE_PRIVATE;
                } else if (room_type.equals("Public")) {
                    groupType = CometChatConstants.GROUP_TYPE_PUBLIC;
                }

                Group group = new Group(GUID, groupNameString, groupType, password);
                group.setDescription(description_field.getText().toString());

                CometChat.createGroup(group, new CometChat.CallbackListener<Group>() {
                    @Override
                    public void onSuccess(Group group) {
                        Toast.makeText(CreateRoom.this, "Room succesfully created!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateRoom.this, UserHomepage.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Toast.makeText(CreateRoom.this, "Room creation failed!", Toast.LENGTH_SHORT).show();
                    }
                });

            }

    });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        room_type  = parent.getItemAtPosition(position).toString();
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        EditText password_field = (EditText) findViewById(R.id.group_password);
        if (room_type.equals("Private")){
            password_field.setVisibility(View.VISIBLE);
        } else if(room_type.equals("Public")){
            password_field.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
