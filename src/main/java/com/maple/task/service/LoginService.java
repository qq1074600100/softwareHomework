package com.maple.task.service;

import com.maple.task.beans.User;
import com.maple.task.common.PasswordException;
import com.maple.task.common.UserException;
import com.maple.task.dao.LoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    private LoginMapper loginDao;

    //判断用户名是否存在
    public boolean userExists(String username) {
        User user = loginDao.selectUserByName(username);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    //查找数据库，比对用户名和密码，登录
    public boolean login(String username, String password) throws UserException, PasswordException {
        User user = loginDao.selectUserByName(username);
        if (user == null) {
            throw new UserException("用户名不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new PasswordException("密码错误");
        }
        return true;
    }

    //给数据库添加一个用户
    public boolean addUser(String username, String password) {
        loginDao.insertUser(new User(username, password));
        return true;
    }

    public User getUserByName(String username) {
        return loginDao.selectUserByName(username);
    }
}
