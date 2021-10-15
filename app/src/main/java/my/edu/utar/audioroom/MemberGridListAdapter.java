package my.edu.utar.audioroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.GroupMember;
import com.cometchat.pro.uikit.ui_components.shared.cometchatAvatar.CometChatAvatar;

import java.util.ArrayList;

public class MemberGridListAdapter extends BaseAdapter {
    Context context;
    ArrayList<GroupMember> memberList;

    public MemberGridListAdapter(Context context, ArrayList<GroupMember> memberList) {
        this.context = context;
        this.memberList = memberList;
    }
    @Override
    public int getCount() {
        return memberList.size();
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
        view = LayoutInflater.from(context).inflate(R.layout.grid_list_row_user, null);
        TextView userName = view.findViewById(R.id.popUpName);
        CometChatAvatar avatar = view.findViewById(R.id.iv_chat_avatar);

        userName.setText(memberList.get(i).getName());
        avatar.setAvatar(memberList.get(i).getAvatar());
        if (memberList.get(i).getAvatar() != null && !memberList.get(i).getAvatar().isEmpty())
            avatar.setAvatar(memberList.get(i).getAvatar());
        else
            avatar.setInitials(memberList.get(i).getName());
        return view;
    }
}
