package com.atguigu.daijia.customer.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerServiceImpl implements CustomerService {


    @Autowired
    private CustomerInfoFeignClient customerInfoFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String login(String code) {
        //检测状态码
        Result<Long> result = customerInfoFeignClient.login(code);
        if(result.getCode() != 200) {
            throw new GuiguException(result.getCode(), result.getMessage());
        }
        //获取用户openId
        Long customerId = result.getData();
        if(null == customerId) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        //生成token字符串
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        //把token作为key，用户openId作为value设置进redis，设置有效期
        //之后前端每次请求，都会把token设置进请求头传给后端校验登录态
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX+token,
                customerId.toString(), RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);
        return token;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(Long customerId) {
        Result<CustomerLoginVo> result = customerInfoFeignClient.getCustomerLoginInfo(customerId);
        if(result.getCode() != 200) {
            throw new GuiguException(result.getCode(), result.getMessage());
        }
        CustomerLoginVo customerLoginVo = result.getData();
        if(null == customerLoginVo) {
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return customerLoginVo;
    }

    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {
        customerInfoFeignClient.updateWxPhoneNumber(updateWxPhoneForm);
        return true;
    }

}
