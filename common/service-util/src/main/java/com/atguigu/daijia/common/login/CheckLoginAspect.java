package com.atguigu.daijia.common.login;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.common.util.AuthContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
@Aspect
@Order(100)
public class CheckLoginAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    //增强包含了注解的方法
    @Around("execution(* com.atguigu.daijia.*.controller.*.*(..)) && @annotation(checkLogin)")
    public Object process(ProceedingJoinPoint joinPoint, CheckLogin checkLogin) throws Throwable {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();
        String token = request.getHeader("token");

        if(!StringUtils.hasText(token)) {
            throw new GuiguException(ResultCodeEnum.LOGIN_AUTH);
        }
        String userId = (String)redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX+token);
        System.out.println("");

        //把用户id放到ThreadLocal里，后续请求中可以拿出
        if(StringUtils.hasText(userId)) {
            AuthContextHolder.setUserId(Long.parseLong(userId));
        }

        //执行业务
        return joinPoint.proceed();
    }
}
