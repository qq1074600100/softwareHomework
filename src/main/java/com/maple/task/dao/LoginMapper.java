package com.maple.task.dao;

import com.maple.task.beans.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {
    void insertUser(User user);

    User selectUserByName(String username);
}
