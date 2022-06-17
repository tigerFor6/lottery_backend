package com.linglong.lottery_backend.user.config.filter;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/6/26
 */

import com.google.common.hash.Hashing;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码过滤器
 *
 * @author ： CatalpaFlat
 */
@Component
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ValidateCodeFilter.class.getName());

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    private UserRepository repository;

    private RequestMatcher requiresAuthenticationRequestMatcher;

    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public void setAuthenticationFailureHandler(
            AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }


    public ValidateCodeFilter() {
        requiresAuthenticationRequestMatcher = new AntPathRequestMatcher("/mobile/login", "POST");
    }

    protected boolean requiresAuthentication(HttpServletRequest request,
                                             HttpServletResponse response) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (requiresAuthentication(request,response)) {
            String mobile = request.getParameter("username");
            String inputCode = request.getParameter("password");
            String loginType = request.getParameter("loginType");

            if ("1".equals(loginType)){
                //用户密码登录
                User u = repository.findByPhone(mobile);
                if (null == u){
                    failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("您输入的用户名不存在"));
                    return;
                }else {
                    String pwd = u.getPassword();
                    if (StringUtils.isBlank(pwd)){
                        failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("您还未设置密码，可通过忘记密码进行设置"));
                        return;
                    }
                }
                inputCode = MD5Util.getMD5String(inputCode);
                User user = repository.findByPhoneAndPassword(mobile,inputCode);
                if (null == user){
                    failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("您输入的用户名或密码不正确"));
                    return;
                }else{
                    filterChain.doFilter(request, response);
                    return;
                }
            }else if ("2".equals(loginType)){
                //验证码登录
                String key = Hashing.md5().hashBytes(mobile.getBytes()).toString();
                String smsCode = template.opsForValue().get(key);
                if(smsCode == null) {
                    failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("未检测到申请验证码"));
                    return;
                }
                if (!StringUtils.equals(inputCode, smsCode)) {
                    //let it go
                    failureHandler.onAuthenticationFailure(request, response, new AuthenticationServiceException("验证码错误"));
                    return;
                }
                if (StringUtils.equals(inputCode, smsCode)) {
                    //用过就销毁
                    template.delete(key);
                }
            }
        }
        //let it go
        filterChain.doFilter(request, response);
    }
}
