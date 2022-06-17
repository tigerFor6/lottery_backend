package com.linglong.lottery_backend.user.config;

import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Description
 *
 * @author yixun.xing
 * @since 18 三月 2019
 */
@Component
@Slf4j
public class CustomSavedRequestAwareAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Autowired
    private UserRepository repository;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws ServletException, IOException {
        final SavedRequest savedRequest = requestCache.getRequest(request, response);

        String phoneNum = request.getParameter("username");
        String channelNo = request.getParameter("cn");
        String origin = request.getParameter("origin");

        log.info("登录用户手机号:"+phoneNum+"  渠道号:"+channelNo+"  客户端:"+origin);
        User user = updateUserSource(phoneNum,channelNo,origin);

        request.getSession().setAttribute("userId", user.getUserId());
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(ResultGenerator.genSuccessResult());
        out.flush();
        if (savedRequest == null) {
            clearAuthenticationAttributes(request);
            return;
        }
        final String targetUrlParameter = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
            requestCache.removeRequest(request, response);
            clearAuthenticationAttributes(request);
            return;
        }
        clearAuthenticationAttributes(request);
    }

    public void setRequestCache(final RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Async
    public User updateUserSource(String phoneNum, String channelNo, String origin) {
        User user = repository.findByPhone(phoneNum);
        if(Strings.isNotBlank(channelNo) && Strings.isBlank(user.getChannelNo()))
        {
            if (channelNo.equals("football1")){
                user.setChannelNo("hangzhou");
                user.setOrigin(1);
            }else if (channelNo.equals("football2")){
                user.setChannelNo("hangzhou");
                user.setOrigin(2);
            }else if (channelNo.equals("football3")){
                user.setChannelNo("hangzhou");
                user.setOrigin(3);
            }else if (channelNo.equals("dx-android")){
                user.setChannelNo("dianduidian");
                user.setOrigin(1);
            }else if (channelNo.equals("dx-ios")){
                user.setChannelNo("dianduidian");
                user.setOrigin(2);
            }else if (channelNo.equals("gw-android")){
                user.setChannelNo("360");
                user.setOrigin(1);
            }else if (channelNo.equals("qiye-ios")){
                user.setChannelNo("360");
                user.setOrigin(2);
            }else if (channelNo.equals("gw-ios")){
                user.setChannelNo("hangzhou");
                user.setOrigin(2);
            } else{
                if (StringUtils.isEmpty(channelNo)){
                    user.setChannelNo("0");
                }else{
                    user.setChannelNo(channelNo);
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(origin)){
                    user.setOrigin(Integer.valueOf(origin));
                }
            }
            repository.save(user);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(origin)&& null == user.getOrigin()){
            user.setOrigin(Integer.valueOf(origin));
            repository.save(user);
        }
        return user;
    }
}