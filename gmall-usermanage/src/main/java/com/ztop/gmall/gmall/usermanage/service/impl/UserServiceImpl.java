package com.ztop.gmall.gmall.usermanage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.ztop.gmall.bean.UserAddress;
import com.ztop.gmall.bean.UserInfo;
import com.ztop.gmall.gmall.usermanage.Mapper.UserAddressMapper;
import com.ztop.gmall.gmall.usermanage.Mapper.UserMapper;
import com.ztop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserInfo> findAll() {
        //查询所有用户
        List<UserInfo> userInfos = userMapper.selectAll();
        return userInfos;
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        //根据用户id查询用户所有地址
        List<UserAddress> addressList = userAddressMapper.select(userAddress);
        return addressList;
    }
}
