package service;

import model.FPUser;

import java.util.ArrayList;
import java.util.List;
import database.dao.UserDao;
import java.util.stream.Collectors;
import util.UserUtils;

public final class FPUserService {

    private List<FPUser> userList;
    private UserDao userDao;

    public FPUserService(UserDao userDao) {
        userList = new ArrayList<>();
        this.userDao = userDao;
        retriveUserListFromDatabase();
    }

    public void addNewUser(FPUser user) {
        userList.add(user);
    }

    public List<FPUser> getAllUsers() {
        retriveUserListFromDatabase();
        return userList;
    }

    public void retriveUserListFromDatabase() {
        userList = userDao.getAll().stream()
                .map(u -> UserUtils.convertDBUserToFPUser(u))
                .collect(Collectors.toList());

    }

}
