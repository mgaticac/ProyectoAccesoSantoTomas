package service;

import model.FPUser;

import java.util.ArrayList;
import java.util.List;

public class FPUserService {

    private List<FPUser> userList;

    public FPUserService() {
        userList = new ArrayList<>();
    }

    public void addNewUser(FPUser user) {
        userList.add(user);
    }

    public List<FPUser> getAllUsers() {
        return userList;
    }

}
