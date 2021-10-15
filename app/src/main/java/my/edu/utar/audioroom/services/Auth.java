package my.edu.utar.audioroom.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.CometChatWidget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import my.edu.utar.audioroom.Landingpage;
import my.edu.utar.audioroom.Register;
import my.edu.utar.audioroom.UserHomepage;


public class Auth {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Database db = new Database();



    //sign in as guest
    public void signInAnonymously(Context context){
        auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success
                    String uid = "va4prp9ohmhug4twvoac9qo4ebp1";
                    CometChatWidget.login(uid, new CometChat.CallbackListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            Log.d("Testing", "Login Successful : " + user.toString());
                            Toast.makeText(context, "Login Successful.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, UserHomepage.class);
                            context.startActivity(intent);
                        }
                        @Override
                        public void onError(CometChatException e) {
                            Log.d("Testing", "Login failed with exception: " + e.getMessage());
                            return;
                        }
                    });

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //register an account
    public void Register(Context context,String name, String e,String psw){
        auth.createUserWithEmailAndPassword(e,psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uidComechat = e.replaceAll("[@, .]", "");
                    User user = new User(uidComechat,name);
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();
                    //Register success, direct user to homepage
                    db.updateUserData(uid,name,"");
                    Toast.makeText(context, "Register Successful.",
                            Toast.LENGTH_SHORT).show();

                    CometChatWidget.createOrUpdate(user,new CometChat.CallbackListener<User>() {
                        @Override
                        public void onSuccess(User user) {

                            CometChatWidget.login(uidComechat, new CometChat.CallbackListener<User>() {
                                @Override
                                public void onSuccess(User user) {
                                    Log.d("Testing", "Login Successful : " + user.toString());
                                    Toast.makeText(context, "Login Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, UserHomepage.class);
                                    context.startActivity(intent);
                                }
                                @Override
                                public void onError(CometChatException e) {
                                    Log.d("Testing", "Login failed with exception: " + e.getMessage());
                                    return;
                                }
                            });
                        }

                        @Override
                        public void onError(CometChatException e) {
                            Log.e("Testing", e.getMessage());
                            return;
                        }
                    });


                } else {
                    // If register fails, display a message to the user.
                    Intent intent = new Intent(context, Register.class);
                    context.startActivity(intent);
                    Toast.makeText(context, "Please enter a valid email address",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //sign in as user
    public void Signin(Context context, String e,String psw){
        auth.signInWithEmailAndPassword(e,psw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success
                    //if (CometChat.getLoggedInUser() == null) {
                        String uidComechat = e.replaceAll("[@, .]", "");
                        System.out.println(uidComechat);

                        CometChatWidget.login(uidComechat, new CometChat.CallbackListener<User>() {
                            @Override
                            public void onSuccess(User user) {
                                Log.d("Testing", "Login Successful : " + user.toString());
                                Toast.makeText(context, "Login Successful.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, UserHomepage.class);
                                context.startActivity(intent);
                            }
                            @Override
                            public void onError(CometChatException e) {
                                Log.d("Testing", "Login failed with exception: " + e.getMessage());
                                return;
                            }
                        });
                     /*else {
                        // User already logged in
                        Log.d("Testing", "Already log in: Hello user " + CometChat.getLoggedInUser().getName());
                        Toast.makeText(context, "Login Successful.",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, UserHomepage.class);
                        context.startActivity(intent);
                    }*/

                } else {
                    // If sign in fails, display a message to the user.
                    Intent intent = new Intent(context, Landingpage.class);
                    context.startActivity(intent);
                    Toast.makeText(context, "Please enter an valid email or password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //log out
    public void Signout(Context context){
        CometChatWidget.logout(new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d("Testing", "cometchat user successfully logout");
                auth.signOut();
                CometChat.logout(new CometChat.CallbackListener<String>() {
                    @Override
                    public void onSuccess(String successMessage) {
                        Intent intent = new Intent(context, Landingpage.class);
                        context.startActivity(intent);
                        Toast.makeText(context, "Logout Successfully",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(CometChatException e) {
                        Log.d("Testing", "Logout failed with exception: " + e.getMessage());
                    }
                });

            }

            @Override
            public void onError(CometChatException e) {
                Log.d("Testing", "cometchat user logout failed");
                return;
            }
        });

    }
}
