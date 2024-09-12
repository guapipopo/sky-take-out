package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    public  static  final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {

        String openid=getOpenid(userLoginDTO.getCode());

//        判断openId是否为空，登陆失败抛出业务
        if (openid==null){
            throw new LoginFailedException("登录失败");
        }
//        判断用户是否为新用户，不存在自动注册
        User user=userMapper.getByOpenid(openid);
        if(user==null){
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
//        返回用户对象
        return user;
    }

    /**
     * 调用微信接口，获得openid
     * @param code
     * @return
     */
    private String getOpenid(String code){
        //        调用微信登录接口，获得用户openid
        Map<String, String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
