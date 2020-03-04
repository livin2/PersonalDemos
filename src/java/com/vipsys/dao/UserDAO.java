package com.vipsys.dao;

import com.vipsys.model.User;

public interface UserDAO {
    User get(int uid);

    User get(String uname) throws Exception;
}
