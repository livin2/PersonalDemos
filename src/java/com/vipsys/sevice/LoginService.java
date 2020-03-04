package com.vipsys.sevice;

import com.vipsys.dao.UserDAO;
import com.vipsys.dto.LoginDTO;
import com.vipsys.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("loginService")
public class LoginService {
    @Resource(name = "userDAO")
    private UserDAO userDAO;

    public Boolean checkPwd(LoginDTO dto) throws Exception {
        User u = userDAO.get(dto.getName());
        if (u.getPassword().equals(dto.getPassword()))
            return true;
        return false;
    }
}
