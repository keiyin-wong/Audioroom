package my.edu.utar.audioroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.uikit.CometChatWidget;
import com.cometchat.pro.uikit.ui_components.shared.cometchatAvatar.CometChatAvatar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import my.edu.utar.audioroom.model.User;
import my.edu.utar.audioroom.services.Database;


public class UserProfilePage extends AppCompatActivity {
    private Database db = new Database();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private com.cometchat.pro.models.User curUser = CometChat.getLoggedInUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);


        //go back
        ImageButton back = (ImageButton) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfilePage.this, UserHomepage.class);
                startActivity(intent);
            }
        });

        //profile icon

        CometChatAvatar profile = (CometChatAvatar) findViewById(R.id.profile_pic);
        if (curUser.getAvatar() != null && !curUser.getAvatar().isEmpty())
            profile.setAvatar(curUser.getAvatar());
        else
            profile.setInitials(curUser.getName());

        TextView username = (TextView) findViewById(R.id.username);
        TextView email = (TextView) findViewById(R.id.profile_email);
        TextView bio = (TextView) findViewById(R.id.bio);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
                openGallery();
            }
        });


        //Edit user Profile button
        Button editProfile = (Button) findViewById(R.id.edit_profile);
        Dialog userProfileUpdateForm = new Dialog(this);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userProfileUpdateForm.setContentView(R.layout.user_profile_update_from);
                EditText newUsernameField = (EditText) userProfileUpdateForm.findViewById(R.id.newUsername);
                EditText newBioField = (EditText) userProfileUpdateForm.findViewById(R.id.newBio);
                Button updateData = (Button) userProfileUpdateForm.findViewById(R.id.Buttonforupdate);

                updateData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newUsername = newUsernameField.getText().toString();
                        String newBio = newBioField.getText().toString();
                        db.updateUserProfile(user.getUid(), newUsername, newBio, user.getEmail(), userProfileUpdateForm, UserProfilePage.this);
                        finish();
                        startActivity(getIntent());
                    }
                });


                userProfileUpdateForm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                userProfileUpdateForm.show();
            }
        });

        //get user Data
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference docRef = firestore.collection("user").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User userData = db.getUserData(document);
                        username.setText(userData.getUsername());
                        email.setText(userData.getEmail());
                        bio.setText(userData.getBio());
                    } else {
                        username.setText("doesn't exist");
                        email.setText("doesn't exist");
                        bio.setText("doesn't exist");
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    username.setText("Failed to get Data");
                    email.setText("Failed to get Data");
                    bio.setText("Failed to get Data");
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //User curUser = db.getUserData();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            //Sent image successfully
            Uri imageUri = data.getData();
            String filePath = getRealPathFromURI(imageUri);
            Log.d("Testing", filePath);
            String receiverID = CometChat.getLoggedInUser().getUid();
            String messageType = CometChatConstants.MESSAGE_TYPE_IMAGE;
            String receiverType = CometChatConstants.RECEIVER_TYPE_USER;

            MediaMessage mediaMessage = new MediaMessage(receiverID, new File(filePath), messageType, receiverType);

            CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
                @Override
                public void onSuccess(MediaMessage mediaMessage) {
                    Log.d("Testing", "Media message sent successfully: " + mediaMessage.toString());
                    // Get Image
                    List<String> categories = new ArrayList<>();
                    categories.add("message");
                    List<String> types = new ArrayList<>();
                    types.add("image");
                    MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                            .setCategories(categories)
                            .setTypes(types)
                            .setLimit(50)
                            .setUID(CometChat.getLoggedInUser().getUid())
                            .build();
                    messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
                        @Override
                        public void onSuccess(List<BaseMessage> list) {
                            MediaMessage msgMedia = (MediaMessage) list.get(list.size() - 1);
                            curUser.setAvatar(msgMedia.getAttachment().getFileUrl());

                            CometChatWidget.createOrUpdate(curUser, new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
                                @Override
                                public void onSuccess(com.cometchat.pro.models.User user) {
                                    Log.d("Testing", user.toString());
//                                    finish();
//                                    startActivity(getIntent());
                                    CometChatAvatar profile = (CometChatAvatar) findViewById(R.id.profile_pic);
                                    if (curUser.getAvatar() != null && !curUser.getAvatar().isEmpty())
                                        profile.setAvatar(curUser.getAvatar());
                                    else
                                        profile.setInitials(curUser.getName());
                                }

                                @Override
                                public void onError(CometChatException e) {
                                    Log.e("Testing", e.getMessage());
                                    return;
                                }
                            });

                        }

                        @Override
                        public void onError(CometChatException e) {
                            Log.d("Testing", "Message fetching failed with exception: " + e.getMessage());
                        }
                    });
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d("Testing", "Media message sending failed with exception: " + e.getMessage());
                }
            });


        }

    }
    private String getRealPathFromURI (Uri contentURI){
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private  boolean checkAndRequestPermissions() {
        int permissionWRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),100);
            return false;
        }
        return true;
    }
}