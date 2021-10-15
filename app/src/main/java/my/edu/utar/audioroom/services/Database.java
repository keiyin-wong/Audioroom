package my.edu.utar.audioroom.services;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.CometChatWidget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import my.edu.utar.audioroom.Landingpage;
import my.edu.utar.audioroom.model.User;

public class Database {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = db.collection("user");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "Register";

    public void updateUserData(String uid,String name,String bio){
        Map<String, Object> userd = new HashMap<>();
        userd.put("username", name);
        userd.put("bio", bio);

        userCollection.document(uid).set(userd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "DocumentSnapshot write Failed!");
                    }
                });

    }

    public void updateUserProfile(String uid, String name, String bio,String e ,Dialog form, Context context){
        Map<String, Object> userd = new HashMap<>();
        userd.put("username", name);
        userd.put("bio", bio);

        userCollection.document(uid).set(userd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String uidComechat = e.replaceAll("[@, .]", "");
                com.cometchat.pro.models.User user = new com.cometchat.pro.models.User(uidComechat,name);
                CometChatWidget.createOrUpdate(user,new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
                    @Override
                    public void onSuccess(com.cometchat.pro.models.User user) {
                        Log.d("Testing", user.toString());
                    }

                    @Override
                    public void onError(CometChatException e) {
                        Log.e("Testing", e.getMessage());
                        return;
                    }
                });
                form.dismiss();
                Toast.makeText(context, "Updated Sucessfully",
                        Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "DocumentSnapshot write Failed!");
                    }
                });

    }


    public User getUserData(DocumentSnapshot document){

        String username = document.getString("username");
        String email = user.getEmail();
        String bio = document.getString("bio");

        User curUser = new User(bio,email,username);
        return curUser;

    }
}
