package my.edu.utar.audioroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.core.CallSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CometChatWidget;
import com.cometchat.pro.uikit.ui_components.shared.cometchatAvatar.CometChatAvatar;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.audioroom.constants.AppConfig;

public class GroupHomePage extends AppCompatActivity {
    TextView tv_name;
    CometChatAvatar avatar;
    CometChatAvatar admin_pic;
    TextView member_name;
    GridView adminListView;
    Group group;
    //String groupId;
    RelativeLayout relativeCallLayout;
    TextView admin_name;
    String groupId, gName,gDesc, gPassword,loggedInUserScope,ownerId,groupType;
    User user = CometChat.getLoggedInUser();
    int groupMemberCount;
    TextView guestInform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_home_page);
        CometChatWidget.init(this, AppConfig.AppDetails.APP_ID, AppConfig.AppDetails.REGION, AppConfig.AppDetails.AUTH_KEY, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d("Testing", "Initialization completed successfully");

            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Initialization failed with exception: " + e.getMessage());
            }
        });
        groupId = getIntent().getStringExtra("groupId");
        findGroup(groupId);

        View toolbar = findViewById(R.id.group_toolbar);
        View groupAdmin = findViewById(R.id.groupAdmin);
        tv_name = (TextView) toolbar.findViewById(R.id.tv_name);
        avatar = toolbar.findViewById(R.id.iv_chat_avatar);
        member_name = toolbar.findViewById(R.id.tv_status);
        admin_name = groupAdmin.findViewById(R.id.popUpName);
        admin_pic = groupAdmin.findViewById(R.id.iv_chat_avatar);

        adminListView = findViewById(R.id.adminList);
        relativeCallLayout = findViewById(R.id.callRelativeLayout);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGroupDetail();
            }
        });

        if(user.getName().equals("guest")){
            guestInform = findViewById(R.id.guestInform);
            guestInform.setVisibility(View.VISIBLE);
        }

        getGroupMember(groupId);
        getGroupAdmin(groupId);

    }

    private void openGroupDetail() {
        Intent intent = new Intent(this, CometChatGroupDetailActivity.class);
        intent.putExtra(UIKitConstants.IntentStrings.GUID,groupId);
        intent.putExtra(UIKitConstants.IntentStrings.AVATAR,gName);
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_DESC,groupId);
        intent.putExtra(UIKitConstants.IntentStrings.MEMBER_SCOPE,loggedInUserScope);
        intent.putExtra(UIKitConstants.IntentStrings.NAME,gName);
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_PASSWORD,gPassword);
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER,ownerId);
        intent.putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT,groupMemberCount);
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_TYPE,groupType);

        startActivity(intent);
    }

    private void findGroup(String Guid) {
        CometChat.getGroup(Guid, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group g) {
                Log.d("Testing", "Group details fetched successfully: " + g.toString());
                group = g;
                tv_name.setText(g.getName());
                avatar.setAvatar(g);
                gName=group.getName();
                gDesc=group.getDescription();
                gPassword=null;
                ownerId=group.getOwner();
                groupType=group.getGroupType();
                groupMemberCount=group.getMembersCount();
                loggedInUserScope = group.getScope();
                if (g.getGroupType().equals("public") && (g.isJoined() == false) && (!user.getName().equals("guest"))) {
                    joinGroup(group,"");
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Group details fetching failed with exception: " + e.getMessage());

            }
        });
    }

    private void getGroupAdmin(String Guid) {
        List<String> scopes = new ArrayList<>();
        scopes.add("admin");

        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(Guid).setLimit(1).setScopes(scopes).build();

        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                Log.d("Testing", "Group Member list fetched successfully: " + list.size());
                String memberName = "";
                try{
                    admin_name.setText(list.get(0).getName());
                    admin_pic.setAvatar(list.get(0).getAvatar());
                    if (list.get(0).getAvatar() != null && !list.get(0).getAvatar().isEmpty())
                        admin_pic.setAvatar(list.get(0).getAvatar());
                    else
                        admin_pic.setInitials(list.get(0).getName());

                }catch (Exception e){};


            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Group Admin fetching failed with exception: " + e.getMessage());
            }

        });
    }



    private void getGroupMember(String Guid) {
        List<String> scopes = new ArrayList<>();
        scopes.add("moderator");
        scopes.add("participant");

        GroupMembersRequest groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(Guid).setLimit(100).setScopes(scopes).build();

        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> list) {
                MemberGridListAdapter memberGridListAdapter = new MemberGridListAdapter(GroupHomePage.this, (ArrayList) list);
                adminListView.setAdapter(memberGridListAdapter);
                Log.d("Testing", "Group Member list fetched successfully: " + list.size());
                String memberName = "";
                /*for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getScope().equals("admin")) {
                        admin_name.setText(list.get(i).getName());
                    }

                    if (i == list.size() - 1) {
                        memberName = memberName + list.get(i).getName();
                        *//*if(list.get(i).getUid().equals(user.getUid())){
                            loggedInUserScope= list.get(i).getScope();
                        }*//*
                    } else {
                        memberName = memberName + list.get(i).getName() + ", ";
                    }

                }*/
                member_name.setText(groupId);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Group Member list fetching failed with exception: " + e.getMessage());
            }

        });
    }


    private void joinGroup(Group group, String password) {

        if (group.getGroupType().equals("public") && (group.isJoined() == false))
            password = "";
        CometChat.joinGroup(group.getGuid(), CometChatConstants.GROUP_TYPE_PUBLIC, password, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group joinedGroup) {
                Log.d("Testing", joinedGroup.toString());
                getGroupMember(groupId);
                getGroupAdmin(groupId);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Group joining failed with exception: " + e.getMessage());
            }
        });
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    public void startCall(View view) {


        Intent intent = new Intent(this, CometChatStartCallActivity.class);
        intent.putExtra("groupId", group.getGuid());
        startActivity(intent);
    }
}