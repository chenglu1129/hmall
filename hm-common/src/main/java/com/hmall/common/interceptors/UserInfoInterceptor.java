package com.hmall.common.interceptors;

import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Cheng
 * @Date 2024 09 17 21 53
 **/
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取用户信息
        String userInfo = request.getHeader("user-info");
        //2.判断用户信息是否为空,如果有存入ThreadLocal
        if (userInfo != null && !userInfo.isEmpty()) {
            //存入ThreadLocal
            UserContext.setUser(Long.valueOf(userInfo));
        }
        //3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除ThreadLocal,清除用户
        UserContext.removeUser();
    }

}
