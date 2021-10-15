package my.edu.utar.audioroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CometChatWidget;
import com.cometchat.pro.uikit.ui_components.shared.cometchatAvatar.CometChatAvatar;
import com.cometchat.pro.uikit.ui_components.shared.cometchatGroups.CometChatGroups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import my.edu.utar.audioroom.constants.AppConfig;
import my.edu.utar.audioroom.services.Auth;
import my.edu.utar.audioroom.services.SortByIAmAdmin;

public class UserHomepage extends AppCompatActivity {

    private Auth auth = new Auth();
    private GroupsRequest groupsRequest;
    private CometChatGroups rvGroupList;
    private ListView listView;
    private User curUser;
    GroupListAdapter groupListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);
        curUser = CometChat.getLoggedInUser();
        listView = findViewById(R.id.groupListView);
        rvGroupList=findViewById(com.cometchat.pro.uikit.R.id.rv_group_list);
        ImageButton joinRoom = (ImageButton) findViewById(R.id.join_room);
        fetchGroup();


        //profile icon
        //action after clicked
        CometChatAvatar profile = (CometChatAvatar) findViewById(R.id.profile_page);
        if (curUser.getAvatar() != null && !curUser.getAvatar().isEmpty())
            profile.setAvatar(curUser.getAvatar());
        else
            profile.setInitials(curUser.getName());
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userProfile = new Intent(UserHomepage.this, UserProfilePage.class);
                startActivity(userProfile);
            }
        });

        ImageButton createRoom = (ImageButton) findViewById(R.id.create_room);
        if(curUser.getName().equals("guest")){

            createRoom.setVisibility(View.INVISIBLE);
            profile.setVisibility(View.INVISIBLE);
            joinRoom.setVisibility(View.INVISIBLE);
        }
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHomepage.this, CreateRoom.class);
                startActivity(intent);
            }
        });

        joinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserHomepage.this, JoinRoom.class);
                startActivity(intent);
            }
        });

        //search function
        ImageButton search = (ImageButton) findViewById(R.id.search);
        EditText ed1 = (EditText)findViewById(R.id.groupName);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = ed1.getText().toString();
                QueryGroup(searchText);
            }
        });


        //log out Button
        ImageButton logout = (ImageButton) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loadingPage = new Intent(UserHomepage.this, Loading.class);
                loadingPage.putExtra("request","logout");
                startActivity(loadingPage);
            }
        });

        //list refresh
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchGroup();
                pullToRefresh.setRefreshing(false);
            }
        });



    }

    @Override
    public void onBackPressed(){

    }

    private void fetchGroup(){
        groupsRequest = new GroupsRequest.GroupsRequestBuilder().setLimit(30).build();

        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List <Group> list) {
                Log.d("Testing", "Groups list fetched successfully: " + list.size());
                Collections.sort(list,new SortByIAmAdmin());
                GroupListAdapter groupListAdapter = new GroupListAdapter(UserHomepage.this, (ArrayList) list);
                listView.setAdapter(groupListAdapter);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Groups list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void QueryGroup(String grpName){
        groupsRequest = new GroupsRequest.GroupsRequestBuilder().setSearchKeyWord(grpName).setLimit(30).build();

        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List <Group> list) {
                Log.d("Testing", "Groups list fetched successfully: " + list.size());
                Collections.sort(list,new SortByIAmAdmin());
                groupListAdapter = new GroupListAdapter(UserHomepage.this, (ArrayList) list);
                listView.setAdapter(groupListAdapter);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Groups list fetching failed with exception: " + e.getMessage());
            }
        });
    }


}