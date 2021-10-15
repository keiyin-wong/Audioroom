package my.edu.utar.audioroom.model;

public class User {
    private String bio;
    private String email;
    private String username;

    public User(String bio,String email,String username){
        this.bio = bio;
        this.email = email;
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
