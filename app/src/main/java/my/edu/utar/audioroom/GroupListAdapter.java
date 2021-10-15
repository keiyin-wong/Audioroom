package my.edu.utar.audioroom;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupMembersRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.uikit.ui_components.shared.cometchatAvatar.CometChatAvatar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import my.edu.utar.audioroom.model.User;

public class GroupListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Group> groupList;
    TextView groupName;



    public GroupListAdapter(Context context, ArrayList<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Group group;
        group = groupList.get(i);
        view = LayoutInflater.from(context).inflate(R.layout.group_list_row, null);
        groupName = (TextView) view.findViewById(R.id.txt_user_name);
        CometChatAvatar avatar = view.findViewById(R.id.av_group);
        TextView groupMessage = (TextView) view.findViewById(R.id.txt_user_message);
        TextView iAmAdmin = (TextView) view.findViewById(R.id.iAmAdmin);
        TextView groupDesription = (TextView) view.findViewById(R.id.groupDesription);
        TextView memberCount = (TextView) view.findViewById(R.id.memberCount);
        avatar.setAvatar(group);
        groupName.setText(group.getName());
        groupMessage.setText(group.getGroupType());
        groupDesription.setText(group.getDescription());
        if(group.getScope() !=null && group.getScope().equals("admin")){
            iAmAdmin.setVisibility(View.VISIBLE);
        }else{
            iAmAdmin.setVisibility(View.INVISIBLE);
        }
        if(group.getDescription()==null){
            groupDesription.setText("No Group Description");
        }else{
            groupDesription.setText(group.getDescription());
        }

        if(group.getMembersCount()==0 || group.getMembersCount()==1){
            memberCount.setText(String.valueOf(group.getMembersCount()) + " member");
        }else {
            memberCount.setText(String.valueOf(group.getMembersCount()) + " members");
        }



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject json = new JSONObject();
                try {
                    json.put("Likes", "1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                groupList.get(i).setMetadata(json);

                CometChat.updateGroup(groupList.get(i),  new CometChat.CallbackListener<Group>() {
                    @Override
                    public void onSuccess(Group group) {
                        Log.d("Testing", "Groups details metadata updated successfully: " + group.toString());
                    }
                    @Override
                    public void onError(CometChatException e) {
                        Log.d("Testing", "Group details update metadata failed with exception: " + e.getMessage());
                    }
                });


                Intent intent = new Intent(context, GroupHomePage.class);
                intent.putExtra("groupId", groupList.get(i).getGuid());
                context.startActivity(intent);
            }
        });



        return view;
    }



}
