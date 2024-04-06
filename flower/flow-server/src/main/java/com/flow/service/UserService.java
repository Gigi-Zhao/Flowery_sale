package com.flow.service;

import com.flow.dto.UserLoginDTO;
import com.flow.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

}
