package my.edu.utar.audioroom.services;

import com.cometchat.pro.models.Group;

import java.util.Comparator;

public class SortByIAmAdmin implements Comparator<Group> {
    @Override
    public int compare(Group o1, Group o2) {
        if(o1.getScope()==null && o2.getScope()==null) {
            return 0;
        }else if (o1.getScope()!=null && o2.getScope()==null){
            return -1;
        }else if (o1.getScope()==null && o2.getScope()!=null){
            return 1;
        }else if(o1.getScope().equals("admin") && o2.getScope().equals("admin")){
            return 0;
        }else if(o1.getScope().equals("admin") && !o2.getScope().equals("admin")){
            return -1;
        }else if(!o1.getScope().equals("admin") && o2.getScope().equals("admin")){
            return 1;
        }else
            return 0;


    }
}
