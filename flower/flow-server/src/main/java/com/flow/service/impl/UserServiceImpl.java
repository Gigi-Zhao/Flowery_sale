package com.flow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flow.constant.MessageConstant;
import com.flow.dto.UserLoginDTO;
import com.flow.entity.User;
import com.flow.exception.LoginFailedException;
import com.flow.mapper.UserMapper;
import com.flow.properties.WeChatProperties;
import com.flow.service.UserService;
import com.flow.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录:老用户查找user对象，新用户创建User对象
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {

        //调用微信接口服务,获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());     // 微信配置数据
        map.put("secret",weChatProperties.getSecret());   // 微信配置数据
        map.put("js_code",userLoginDTO.getCode());        // 前端传来的用户授权码
        map.put("grant_type","authorization_code");       // API规定的，固定的

        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);

        String openid = jsonObject.getString("openid");

        //判断openid是否为空,如果为空表示登录失败,抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        //如果是新用户,自动完成注册
        if(user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回这个用户对象
        return user;
    }
}
