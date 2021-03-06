package my.edu.utar.audioroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.BannedGroupMembersRequest;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Action;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_components.groups.add_members.CometChatAddMembersActivity;
import com.cometchat.pro.uikit.ui_components.groups.admin_moderator_list.CometChatAdminModeratorListActivity;
import com.cometchat.pro.uikit.ui_components.groups.banned_members.CometChatBanMembersActivity;
import com.cometchat.pro.uikit.ui_components.groups.group_members.CometChatGroupMemberListActivity;
import com.cometchat.pro.uikit.ui_components.groups.group_members.GroupMemberAdapter;
import com.cometchat.pro.uikit.ui_components.shared.cometchatAvatar.CometChatAvatar;
import com.cometchat.pro.uikit.ui_components.shared.cometchatSharedMedia.CometChatSharedMedia;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.CallUtils;
import com.cometchat.pro.uikit.ui_resources.utils.FontUtils;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;
import com.cometchat.pro.uikit.ui_resources.utils.recycler_touch.ClickListener;
import com.cometchat.pro.uikit.ui_resources.utils.recycler_touch.RecyclerTouchListener;
import com.cometchat.pro.uikit.ui_settings.UISettings;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cometchat.pro.uikit.ui_resources.utils.Utils.UserToGroupMember;

public class CometChatGroupDetailActivity extends AppCompatActivity {
    private String TAG = "CometChatGroupDetail";

    private CometChatAvatar groupIcon;

    private String groupType;

    private String ownerId;

    private TextView tvGroupName;

    private TextView tvGroupDesc;

    private TextView tvAdminCount;

    private TextView tvModeratorCount;

    private TextView tvBanMemberCount;

    private ArrayList<String> groupMemberUids = new ArrayList<>();

    private RecyclerView rvMemberList;

    private String guid, gName,gDesc,gPassword;

    private GroupMembersRequest groupMembersRequest;

    private GroupMemberAdapter groupMemberAdapter;

    private int adminCount;

    private int moderatorCount;

    String[] s = new String[0];

    private RelativeLayout rlAddMemberView;

    private RelativeLayout rlAdminListView;

    private RelativeLayout rlModeratorView;

    private RelativeLayout rlBanMembers;

    private String loggedInUserScope;

    private GroupMember groupMember;

    private TextView tvDelete;

    private TextView tvLoadMore;

    private List<GroupMember> groupMembers = new ArrayList<>();

    private AlertDialog.Builder dialog;

    private TextView tvMemberCount;

    private int groupMemberCount=0;

    private static int LIMIT = 30;

    private User loggedInUser = CometChat.getLoggedInUser();

    private FontUtils fontUtils;

    private CometChatSharedMedia sharedMediaView;

    private LinearLayout sharedMediaLayout;

    private ImageView videoCallBtn;

    private ImageView callBtn;

    private TextView dividerAdmin,dividerBan,dividerModerator,divider2, tvExit;

    private BannedGroupMembersRequest banMemberRequest;

    private MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comet_chat_group_detail);
        fontUtils= FontUtils.getInstance(this);
        initComponent();
    }

    private void initComponent() {

        dividerAdmin = findViewById(R.id.tv_seperator_admin);
        dividerModerator = findViewById(R.id.tv_seperator_moderator);
        dividerBan = findViewById(R.id.tv_seperator_ban);
        divider2 = findViewById(R.id.tv_seperator_1);
        groupIcon = findViewById(R.id.iv_group);
        tvGroupName = findViewById(R.id.tv_group_name);
        tvGroupDesc = findViewById(R.id.group_description);
        tvGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroupDialog();
            }
        });
        tvMemberCount = findViewById(R.id.tv_members);
        tvAdminCount = findViewById(R.id.tv_admin_count);
        tvModeratorCount = findViewById(R.id.tv_moderator_count);
        tvBanMemberCount = findViewById(R.id.tv_ban_count);
        rvMemberList = findViewById(R.id.member_list);
        tvLoadMore = findViewById(R.id.tv_load_more);
        tvLoadMore.setText(String.format(getResources().getString(R.string.load_more_members),LIMIT));
        TextView tvAddMember = findViewById(R.id.tv_add_member);
        callBtn = findViewById(R.id.callBtn_iv);
        videoCallBtn = findViewById(R.id.video_callBtn_iv);
        rlBanMembers = findViewById(R.id.rlBanView);
        rlBanMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBanMemberListScreen();
            }
        });
        rlAddMemberView = findViewById(R.id.rl_add_member);
        rlAddMemberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMembers();
            }
        });
        rlAdminListView = findViewById(R.id.rlAdminView);
        rlAdminListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdminListScreen(false);
            }
        });
        rlModeratorView = findViewById(R.id.rlModeratorView);
        rlModeratorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openAdminListScreen(true); }
        });
        tvDelete = findViewById(R.id.tv_delete);
        tvExit= findViewById(R.id.tv_exit);
        toolbar = findViewById(R.id.groupDetailToolbar);

        tvDelete.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        tvExit.setTypeface(fontUtils.getTypeFace(FontUtils.robotoMedium));
        tvAddMember.setTypeface(fontUtils.getTypeFace(FontUtils.robotoRegular));

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvMemberList.setLayoutManager(linearLayoutManager);
//        rvMemberList.setNestedScrollingEnabled(false);

        handleIntent();

        sharedMediaLayout = findViewById(R.id.shared_media_layout);
        sharedMediaView = findViewById(R.id.shared_media_view);
        sharedMediaView.setRecieverId(guid);
        sharedMediaView.setRecieverType(CometChatConstants.RECEIVER_TYPE_GROUP);
        sharedMediaView.reload();

        rvMemberList.addOnItemTouchListener(new RecyclerTouchListener(this, rvMemberList, new ClickListener() {
            @Override
            public void onClick(View var1, int var2) {
                GroupMember user = (GroupMember) var1.getTag(R.string.user);
                if (loggedInUserScope != null&&(loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN)||loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR))) {
                    groupMember = user;
                    boolean isAdmin =user.getScope().equals(CometChatConstants.SCOPE_ADMIN) ;
                    boolean isSelf = loggedInUser.getUid().equals(user.getUid());
                    boolean isOwner = loggedInUser.getUid().equals(ownerId);
                    if (!isSelf) {
                        if (!isAdmin||isOwner) {
                            registerForContextMenu(rvMemberList);
                            openContextMenu(var1);
                        }
                    }
                }
            }

            @Override
            public void onLongClick(View var1, int var2) {

            }
        }));

        tvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroupMembers();
            }
        });

        tvExit.setOnClickListener(view -> createDialog(getResources().getString(R.string.leave_group), getResources().getString(R.string.leave_group_message),
                getResources().getString(R.string.leave_group), getResources().getString(R.string.cancel), R.drawable.ic_exit_to_app));

        callBtn.setOnClickListener(view -> {
            callBtn.setClickable(false);
            checkOnGoingCall(CometChatConstants.CALL_TYPE_AUDIO);
        });

        videoCallBtn.setOnClickListener(view -> {
            videoCallBtn.setClickable(false);
            checkOnGoingCall(CometChatConstants.CALL_TYPE_VIDEO);
        });

        tvDelete.setOnClickListener(view -> createDialog(getResources().getString(R.string.delete_group), getResources().getString(R.string.delete_group_message),
                getResources().getString(R.string.delete_group), getResources().getString(R.string.cancel), R.drawable.ic_delete_24dp));


        //
        if (!UISettings.isJoinOrLeaveGroup())
            tvExit.setVisibility(View.GONE);

        if (!UISettings.isAllowDeleteGroups())
            tvDelete.setVisibility(View.GONE);

        if (!UISettings.isViewSharedMedia()) {
            sharedMediaLayout.setVisibility(View.GONE);
        }

        if (!UISettings.isViewGroupMember()) {
            rvMemberList.setVisibility(View.GONE);
            tvLoadMore.setVisibility(View.GONE);
            rlAddMemberView.setVisibility(View.GONE);
            tvMemberCount.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
        }
        if (!UISettings.isAllowPromoteDemoteMembers()) {
            rlModeratorView.setVisibility(View.GONE);
            rlAdminListView.setVisibility(View.GONE);
            dividerAdmin.setVisibility(View.GONE);
            dividerModerator.setVisibility(View.GONE);
        }

        callBtn.setVisibility(View.GONE);
        videoCallBtn.setVisibility(View.GONE);

        if (!UISettings.isAllowBanKickMembers())
            rlBanMembers.setVisibility(View.GONE);

        if (UISettings.getColor()!=null) {
            getWindow().setStatusBarColor(Color.parseColor(UISettings.getColor()));
            callBtn.setImageTintList(ColorStateList.valueOf(
                    Color.parseColor(UISettings.getColor())));
            videoCallBtn.setImageTintList(ColorStateList.valueOf(
                    Color.parseColor(UISettings.getColor())));
        }
    }

    private void checkOnGoingCall(String callType) {
        if(CometChat.getActiveCall()!=null && CometChat.getActiveCall().getCallStatus().equals(CometChatConstants.CALL_STATUS_ONGOING) && CometChat.getActiveCall().getSessionId()!=null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.ongoing_call))
                    .setMessage(getResources().getString(R.string.ongoing_call_message))
                    .setPositiveButton(getResources().getString(R.string.join), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CallUtils.joinOnGoingCall(CometChatGroupDetailActivity.this,CometChat.getActiveCall());
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    callBtn.setClickable(true);
                    videoCallBtn.setClickable(true);
                    dialog.dismiss();
                }
            }).create().show();
        }
        else {
            CallUtils.initiateCall(CometChatGroupDetailActivity.this,guid, CometChatConstants.RECEIVER_TYPE_GROUP,callType);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.group_action_menu, menu);

        menu.findItem(R.id.item_make_admin).setVisible(false);

        menu.setHeaderTitle(getString(R.string.group_alert));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.item_remove) {
            kickMember();
        } else if(item.getItemId() == R.id.item_ban) {
            banMember();
        }

        return super.onContextItemSelected(item);
    }

    private void createDialog(String title, String message, String positiveText, String negativeText, int drawableRes) {

        MaterialAlertDialogBuilder alert_dialog = new MaterialAlertDialogBuilder(CometChatGroupDetailActivity.this,
                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);
        alert_dialog.setTitle(title);
        alert_dialog.setMessage(message);
        alert_dialog.setPositiveButton(positiveText, (dialogInterface, i) -> {

            if (positiveText.equalsIgnoreCase(getResources().getString(R.string.leave_group))) {
                if (loggedInUser.getUid().equals(ownerId))
                    showTansferOwnerShipDialog();
                else
                    leaveGroup();

            }
            else if (positiveText.equalsIgnoreCase(getResources().getString(R.string.delete_group))
                    && loggedInUserScope.equalsIgnoreCase(CometChatConstants.SCOPE_ADMIN))
                deleteGroup();

        });

        alert_dialog.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert_dialog.create();
        alert_dialog.show();

    }

    private void showTansferOwnerShipDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.group_alert));
        dialog.setMessage(getString(R.string.transfer_ownership_message));
        dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(CometChatGroupDetailActivity.this, CometChatGroupMemberListActivity.class);
                intent.putExtra(UIKitConstants.IntentStrings.GUID,guid);
                intent.putExtra(UIKitConstants.IntentStrings.SHOW_MODERATORLIST,false);
                intent.putExtra(UIKitConstants.IntentStrings.TRANSFER_OWNERSHIP,true);
                finish();
                startActivity(intent);
            }
        });
        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void handleIntent() {
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.GUID)) {
            guid = getIntent().getStringExtra(UIKitConstants.IntentStrings.GUID);
        }
        CometChat.getGroup(guid, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                groupIcon.setAvatar(group);
                if(group.getGroupType().equals("public") && (group.isJoined()==false)){
                    //joinGroup(group,"");
                }
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "Group details fetching failed with exception: " + e.getMessage());

            }
        });
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.MEMBER_SCOPE)) {
            loggedInUserScope = getIntent().getStringExtra(UIKitConstants.IntentStrings.MEMBER_SCOPE);

            if (loggedInUserScope != null && loggedInUserScope.equals(CometChatConstants.SCOPE_ADMIN)) {

                rlAddMemberView.setVisibility(View.GONE);
                rlBanMembers.setVisibility(View.VISIBLE);
                rlModeratorView.setVisibility(View.VISIBLE);
                tvDelete.setVisibility(View.VISIBLE);
                rlAdminListView.setVisibility(View.GONE);
                tvExit.setVisibility(View.GONE);
            } else if(loggedInUserScope!=null && loggedInUserScope.equals(CometChatConstants.SCOPE_MODERATOR)) {

                rlAddMemberView.setVisibility(View.GONE);
                rlBanMembers.setVisibility(View.VISIBLE);
                rlModeratorView.setVisibility(View.VISIBLE);
                rlAdminListView.setVisibility(View.GONE);
            } else {

                dividerModerator.setVisibility(View.GONE);
                dividerAdmin.setVisibility(View.GONE);
                rlAdminListView.setVisibility(View.GONE);
                rlModeratorView.setVisibility(View.GONE);
                rlBanMembers.setVisibility(View.GONE);
                rlAddMemberView.setVisibility(View.GONE);
            }

        }
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.NAME)) {
            gName = getIntent().getStringExtra(UIKitConstants.IntentStrings.NAME);
            tvGroupName.setText(gName);
        }
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.GROUP_DESC)) {
            gDesc = getIntent().getStringExtra(UIKitConstants.IntentStrings.GROUP_DESC);
            tvGroupDesc.setText(gDesc);
        }
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.GROUP_PASSWORD)) {
            gPassword = getIntent().getStringExtra(UIKitConstants.IntentStrings.GROUP_PASSWORD);
        }
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.GROUP_OWNER)) {
            ownerId = getIntent().getStringExtra(UIKitConstants.IntentStrings.GROUP_OWNER);
        }
        if(getIntent().hasExtra(UIKitConstants.IntentStrings.MEMBER_COUNT)) {
            tvMemberCount.setVisibility(View.VISIBLE);
            groupMemberCount = getIntent().getIntExtra(UIKitConstants.IntentStrings.MEMBER_COUNT,0);
            tvMemberCount.setText((groupMemberCount)+" "+getString(R.string.members));
        }
        if (getIntent().hasExtra(UIKitConstants.IntentStrings.GROUP_TYPE)) {
            groupType = getIntent().getStringExtra(UIKitConstants.IntentStrings.GROUP_TYPE);
        }
    }
    private void openBanMemberListScreen() {
        Intent intent = new Intent(this, CometChatBanMembersActivity.class);
        intent.putExtra(UIKitConstants.IntentStrings.GUID,guid);
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_NAME,gName);
        intent.putExtra(UIKitConstants.IntentStrings.MEMBER_SCOPE,loggedInUserScope);
        startActivity(intent);
    }
    public void openAdminListScreen(boolean showModerators) {
        Intent intent = new Intent(this, CometChatAdminModeratorListActivity.class);
        intent.putExtra(UIKitConstants.IntentStrings.GUID, guid);
        intent.putExtra(UIKitConstants.IntentStrings.SHOW_MODERATORLIST,showModerators);
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER, ownerId);
        intent.putExtra(UIKitConstants.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
        startActivity(intent);
    }
    public void addMembers() {
        if (UISettings.isAllowAddMembers()) {
            Intent intent = new Intent(this, CometChatAddMembersActivity.class);
            intent.putExtra(UIKitConstants.IntentStrings.GUID, guid);
            intent.putExtra(UIKitConstants.IntentStrings.GROUP_MEMBER, groupMemberUids);
            intent.putExtra(UIKitConstants.IntentStrings.GROUP_NAME, gName);
            intent.putExtra(UIKitConstants.IntentStrings.MEMBER_SCOPE, loggedInUserScope);
            intent.putExtra(UIKitConstants.IntentStrings.IS_ADD_MEMBER, true);
            startActivity(intent);
        }
    }
    private void deleteGroup() {
        if (UISettings.isAllowDeleteGroups()) {
            CometChat.deleteGroup(guid, new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    launchUnified();
                }

                @Override
                public void onError(CometChatException e) {
                    Utils.showCometChatDialog(CometChatGroupDetailActivity.this,
                            rvMemberList, getResources().getString(R.string.group_delete_error),true);
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            });
        }
    }
    private void launchUnified() {
        Intent intent = new Intent(CometChatGroupDetailActivity.this, UserHomepage.class);
        startActivity(intent);
        finish();
    }
    private void kickMember() {
        CometChat.kickGroupMember(groupMember.getUid(), guid, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "onSuccess: " + s);
                tvMemberCount.setText((groupMemberCount-1)+" "+getString(R.string.members));
                groupMemberUids.remove(groupMember.getUid());
                groupMemberAdapter.removeGroupMember(groupMember);
            }

            @Override
            public void onError(CometChatException e) {
                Utils.showCometChatDialog(CometChatGroupDetailActivity.this,
                        rvMemberList, String.format(getResources().getString(R.string.cannot_remove_member),groupMember.getName()), true);
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }
    private void banMember() {
        CometChat.banGroupMember(groupMember.getUid(), guid, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "onSuccess: " + s);
                tvMemberCount.setText((groupMemberCount-1)+" "+getString(R.string.members));
//                int count = Integer.parseInt(tvBanMemberCount.getText().toString());
//                tvBanMemberCount.setText(String.valueOf(++count));
                groupMemberUids.remove(groupMember.getUid());
                groupMemberAdapter.removeGroupMember(groupMember);
            }

            @Override
            public void onError(CometChatException e) {
                Utils.showCometChatDialog(CometChatGroupDetailActivity.this,
                        rvMemberList, String.format(getResources().getString(R.string.cannot_remove_member),groupMember.getName()),true);
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    private void getGroupMembers() {
        List<String> groupMemeberScopes = new ArrayList<>();
        groupMemeberScopes.add(CometChatConstants.SCOPE_ADMIN);
        groupMemeberScopes.add(CometChatConstants.SCOPE_PARTICIPANT);
        if (groupMembersRequest == null) {
            groupMembersRequest = new GroupMembersRequest.GroupMembersRequestBuilder(guid)
                    .setScopes(Arrays.asList(CometChatConstants.SCOPE_ADMIN, CometChatConstants.SCOPE_PARTICIPANT, CometChatConstants.SCOPE_MODERATOR))
                    .setLimit(LIMIT).build();
        }
        groupMembersRequest.fetchNext(new CometChat.CallbackListener<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                Log.e(TAG, "onSuccess: " + groupMembers.size());
                if (groupMembers != null && groupMembers.size() != 0) {
                    adminCount = 0;
                    moderatorCount = 0;
                    groupMemberUids.clear();
                    s = new String[groupMembers.size()];
                    for (int j = 0; j < groupMembers.size(); j++) {
                        groupMemberUids.add(groupMembers.get(j).getUid());
                        if (groupMembers.get(j).getScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                            adminCount++;
                        }
                        if (groupMembers.get(j).getScope().equals(CometChatConstants.SCOPE_MODERATOR)) {
                            moderatorCount++;
                        }
                        s[j] = groupMembers.get(j).getName();
                    }
//                    tvAdminCount.setText(adminCount+"");
//                    tvModeratorCount.setText(moderatorCount+"");
                    if (groupMemberAdapter == null) {
                        groupMemberAdapter = new GroupMemberAdapter(CometChatGroupDetailActivity.this, groupMembers, ownerId);
                        rvMemberList.setAdapter(groupMemberAdapter);
                    } else {
                        groupMemberAdapter.addAll(groupMembers);
                    }
                    if (groupMembers.size()<LIMIT)
                    {
                        tvLoadMore.setVisibility(View.GONE);
                    }
                }
                else
                {
                    tvLoadMore.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(CometChatException e) {
                Utils.showCometChatDialog(CometChatGroupDetailActivity.this,
                        rvMemberList, getResources().getString(R.string.group_member_list_error),true);
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }
    private void getUserStatus() {
        CometChat.addUserListener(TAG, new CometChat.UserListener() {
            @Override
            public void onUserOnline(User user) {
                groupMemberAdapter.updateMemberByStatus(user);
            }

            @Override
            public void onUserOffline(User user) {
                groupMemberAdapter.updateMemberByStatus(user);
            }
        });
    }

    private void leaveGroup() {
        if (UISettings.isJoinOrLeaveGroup()) {
            CometChat.leaveGroup(guid, new CometChat.CallbackListener<String>() {
                @Override
                public void onSuccess(String s) {
                    launchUnified();
                }

                @Override
                public void onError(CometChatException e) {
                    Utils.showCometChatDialog(CometChatGroupDetailActivity.this,
                            rlAddMemberView, getResources().getString(R.string.leave_group_error), true);
                    Log.e(TAG, "onError: " + e.getMessage());
                }
            });
        }
    }
    public void addGroupListener() {
        CometChat.addGroupListener(TAG, new CometChat.GroupListener() {
            @Override
            public void onGroupMemberJoined(Action action, User joinedUser, Group joinedGroup) {
                Log.e(TAG, "onGroupMemberJoined: " + joinedUser.getUid());
                if (joinedGroup.getGuid().equals(guid))
                    updateGroupMember(joinedUser, false, false, action);
            }

            @Override
            public void onGroupMemberLeft(Action action, User leftUser, Group leftGroup) {
                Log.d(TAG, "onGroupMemberLeft: ");
                if (leftGroup.getGuid().equals(guid))
                    updateGroupMember(leftUser, true, false, action);
            }

            @Override
            public void onGroupMemberKicked(Action action, User kickedUser, User kickedBy, Group kickedFrom) {
                Log.d(TAG, "onGroupMemberKicked: ");
                if (kickedFrom.getGuid().equals(guid))
                    updateGroupMember(kickedUser, true, false, action);
            }

            @Override
            public void onGroupMemberScopeChanged(Action action, User updatedBy, User updatedUser, String scopeChangedTo, String scopeChangedFrom, Group group) {
                Log.d(TAG, "onGroupMemberScopeChanged: ");
                if (group.getGuid().equals(guid))
                    updateGroupMember(updatedUser,false,true,action);
            }

            @Override
            public void onMemberAddedToGroup(Action action, User addedby, User userAdded, Group addedTo) {
                if (addedTo.getGuid().equals(guid))
                    updateGroupMember(userAdded, false, false, action);
            }

            @Override
            public void onGroupMemberBanned(Action action, User bannedUser, User bannedBy, Group bannedFrom) {
                if (bannedFrom.getGuid().equals(guid)) {
//                    int count = Integer.parseInt(tvBanMemberCount.getText().toString());
//                    tvBanMemberCount.setText(String.valueOf(++count));
                    updateGroupMember(bannedUser, true, false, action);
                }
            }

            @Override
            public void onGroupMemberUnbanned(Action action, User unbannedUser, User unbannedBy, Group unbannedFrom) {
                if (unbannedFrom.getGuid().equals(guid)) {
//                    int count = Integer.parseInt(tvBanMemberCount.getText().toString());
//                    tvBanMemberCount.setText(String.valueOf(--count));
                }
            }
        });
    }
    private void updateGroupMember(User user, boolean isRemoved, boolean isScopeUpdate, Action action) {
        if (groupMemberAdapter != null) {
            if (!isRemoved && !isScopeUpdate) {
                groupMemberAdapter.addGroupMember(UserToGroupMember(user, false, action.getOldScope()));
                int count = ++groupMemberCount;
                tvMemberCount.setText(count+" "+getString(R.string.members));
            } else if (isRemoved && !isScopeUpdate) {
                groupMemberAdapter.removeGroupMember(UserToGroupMember(user, false, action.getOldScope()));
                int count = --groupMemberCount;
                tvMemberCount.setText(count+" "+getString(R.string.members));
                if(action.getNewScope()!=null) {
                    if (action.getNewScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                        adminCount = adminCount - 1;
                    } else if (action.getNewScope().equals(CometChatConstants.SCOPE_MODERATOR)) {
                        moderatorCount = moderatorCount - 1;
                    }
                }
            } else if (!isRemoved) {
                groupMemberAdapter.updateMember(UserToGroupMember(user, true, action.getNewScope()));
                if (action.getNewScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                    adminCount = adminCount + 1;
                    if (user.getUid().equals(loggedInUser.getUid())) {
                        rlAddMemberView.setVisibility(View.VISIBLE);
                        loggedInUserScope = CometChatConstants.SCOPE_ADMIN;
                        tvDelete.setVisibility(View.VISIBLE);
                    }
                } else if (action.getNewScope().equals(CometChatConstants.SCOPE_MODERATOR)) {
                    moderatorCount = moderatorCount + 1;
                    if (user.getUid().equals(loggedInUser.getUid())) {
                        rlBanMembers.setVisibility(View.VISIBLE);
                        loggedInUserScope = CometChatConstants.SCOPE_MODERATOR;
                    }
                } else if (action.getOldScope().equals(CometChatConstants.SCOPE_ADMIN)) {
                    adminCount = adminCount - 1;
                } else if (action.getOldScope().equals(CometChatConstants.SCOPE_MODERATOR)) {
                    moderatorCount = moderatorCount -1;
                }
            }
        }
    }
    private void updateGroupDialog() {
        dialog = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.cometchat_update_group_dialog,null);
        CometChatAvatar avatar = view.findViewById(R.id.group_icon);
        TextInputEditText avatar_url = view.findViewById(R.id.icon_url_edt);
        if (groupIcon.getAvatarUrl()!=null) {
            avatar.setVisibility(View.VISIBLE);
            avatar.setAvatar(groupIcon.getAvatarUrl());
            avatar_url.setText(groupIcon.getAvatarUrl());
        } else {
            avatar.setVisibility(View.GONE);
        }
        TextInputEditText groupName = view.findViewById(R.id.groupname_edt);
        TextInputEditText groupDesc = view.findViewById(R.id.groupdesc_edt);
        TextInputEditText groupOldPwd = view.findViewById(R.id.group_old_pwd);
        TextInputEditText groupNewPwd = view.findViewById(R.id.group_new_pwd);
        TextInputLayout groupOldPwdLayout = view.findViewById(R.id.input_group_old_pwd);
        TextInputLayout groupNewPwdLayout = view.findViewById(R.id.input_group_new_pwd);
        Spinner groupTypeSp = view.findViewById(R.id.groupTypes);
        MaterialButton updateGroupBtn = view.findViewById(R.id.updateGroupBtn);
        MaterialButton cancelBtn = view.findViewById(R.id.cancelBtn);
        groupName.setText(gName);
        groupDesc.setText(gDesc);
        if (groupType!=null && groupType.equals(CometChatConstants.GROUP_TYPE_PUBLIC)) {
            groupTypeSp.setSelection(0);
            groupOldPwdLayout.setVisibility(View.GONE);
            groupNewPwdLayout.setVisibility(View.GONE);
        } else if(groupType!=null && groupType.equals(CometChatConstants.GROUP_TYPE_PRIVATE)){
            groupTypeSp.setSelection(1);
            groupOldPwdLayout.setVisibility(View.GONE);
            groupNewPwdLayout.setVisibility(View.GONE);
        } else {
            groupTypeSp.setSelection(2);
            groupOldPwdLayout.setVisibility(View.VISIBLE);
            groupNewPwdLayout.setVisibility(View.VISIBLE);
        }

        groupTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItemPosition()==2) {
                    if (gPassword==null) {
                        groupOldPwdLayout.setVisibility(View.GONE);
                    } else
                        groupOldPwdLayout.setVisibility(View.VISIBLE);
                    groupNewPwdLayout.setVisibility(View.VISIBLE);
                } else
                {
                    groupOldPwdLayout.setVisibility(View.GONE);
                    groupNewPwdLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        avatar_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().isEmpty())
                {
                    avatar.setVisibility(View.VISIBLE);
                    Glide.with(CometChatGroupDetailActivity.this).load(s.toString()).into(avatar);
                } else
                    avatar.setVisibility(View.GONE);
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.setView(view);
        updateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Group group = new Group();
                group.setDescription(groupDesc.getText().toString().trim());
                if(groupName.getText().toString().isEmpty()) {
                    groupName.setError(getString(R.string.fill_this_field));
                } else if (groupTypeSp.getSelectedItemPosition()==2) {
                    if (gPassword != null && groupOldPwd.getText().toString().trim().isEmpty()) {
                        groupOldPwd.setError(getResources().getString(R.string.fill_this_field));
                    } else if (gPassword != null && !groupOldPwd.getText().toString().trim().equals(gPassword.trim())) {
                        groupOldPwd.setError(getResources().getString(R.string.password_not_matched));
                    } else if (groupNewPwd.getText().toString().trim().isEmpty()) {
                        groupNewPwd.setError(getResources().getString(R.string.fill_this_field));
                    } else {
                        group.setName(groupName.getText().toString());
                        group.setGuid(guid);
                        group.setGroupType(CometChatConstants.GROUP_TYPE_PASSWORD);
                        group.setPassword(groupNewPwd.getText().toString());
                        group.setIcon(avatar_url.getText().toString());
                        updateGroup(group, alertDialog);
                    }
                }
                else if (groupTypeSp.getSelectedItemPosition()==1){
                    group.setName(groupName.getText().toString());
                    group.setGuid(guid);
                    group.setGroupType(CometChatConstants.GROUP_TYPE_PRIVATE);
                    group.setIcon(avatar_url.getText().toString());
                } else {
                    group.setName(groupName.getText().toString());
                    group.setGroupType(CometChatConstants.GROUP_TYPE_PUBLIC);
                    group.setIcon(avatar_url.getText().toString());
                }
                group.setGuid(guid);
                updateGroup(group,alertDialog);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void updateGroup(Group group,AlertDialog dialog) {
        CometChat.updateGroup(group, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                if (rvMemberList!=null) {
                    Utils.showCometChatDialog(CometChatGroupDetailActivity.this,
                            rvMemberList,getResources().getString(R.string.group_updated),false);
                    getGroup();
                }
                dialog.dismiss();
            }

            @Override
            public void onError(CometChatException e) {
                if (rvMemberList!=null) {
                    Utils.showCometChatDialog(CometChatGroupDetailActivity.this,
                            rvMemberList,getResources().getString(R.string.group_update_failed)+" "+e.getMessage(),true);
                }
                dialog.dismiss();
            }
        });
    }
    public void removeGroupListener() {
        CometChat.removeGroupListener(TAG);
    }

    public void removeUserListener() {
        CometChat.removeUserListener(TAG);
    }
    private void getGroup() {

        CometChat.getGroup(guid, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                gName = group.getName();
                tvGroupName.setText(gName);
                groupIcon.setAvatar(group.getIcon());
                loggedInUserScope = group.getScope();
                groupMemberCount = group.getMembersCount();
                groupType = group.getGroupType();
                gDesc = group.getDescription();
                tvGroupDesc.setText(gDesc);
                tvMemberCount.setText(groupMemberCount+" "+getString(R.string.members));
            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(CometChatGroupDetailActivity.this,"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGroup();
        groupMembersRequest = null;
        if (groupMemberAdapter != null) {
            groupMemberAdapter.resetAdapter();
            groupMemberAdapter = null;

        }
        if (UISettings.isShowUserPresence())
            getUserStatus();
        if (UISettings.isViewGroupMember())
            getGroupMembers();
        addGroupListener();
        callBtn.setClickable(true);
        videoCallBtn.setClickable(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        removeGroupListener();
        removeUserListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeGroupListener();
        removeUserListener();
    }
}