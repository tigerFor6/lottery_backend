package com.linglong.lottery_backend.user.config.aop;

import com.linglong.lottery_backend.user.account.entity.TblUserDevice;
import com.linglong.lottery_backend.user.account.entity.User;
import com.linglong.lottery_backend.user.account.repository.TblUserDeviceRepository;
import com.linglong.lottery_backend.user.account.repository.UserRepository;
import com.linglong.lottery_backend.user.account.service.TblUserDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * 登录注册切面
 */
@Slf4j
@Aspect
@Component
public class OnAuthenticationAspect {

    @Autowired
    TblUserDeviceService tblUserDeviceService;

    @Autowired
    UserRepository userRepository;

    @Pointcut("execution (public * com.linglong.lottery_backend.user.config.CustomSavedRequestAwareAuthenticationSuccessHandler.onAuthenticationSuccess(..))")
    public void onAuthenticationSuccess() {
    }

    @Pointcut("execution (public * com.linglong.lottery_backend.user.account.controller.AccountController.register(..))")
    public void register() {
    }

    @After(value = "register()")
    public void registerdoAfter(JoinPoint joinPoint){
        onAuthenticationSuccessdoAfter(joinPoint);
    }

    @After(value="onAuthenticationSuccess()")
    public void onAuthenticationSuccessdoAfter(JoinPoint joinPoint){
        log.info("登录、注册AOP，获取用户设备ID");
        ServletRequestAttributes sra =  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();

        Integer platformId;
        try {
            String platform = request.getParameter("platformId");
            if(StringUtils.isEmpty(platform)) {
                throw new Exception();
            }
            platformId = Integer.valueOf(platform);
        }catch (Exception e) {
            return ;
        }

        String platform = tblUserDeviceService.getPlatform(platformId);
        if(platform.equals("web")) {
            return;
        }

        String deviceId = request.getParameter("deviceId");
        if(deviceId == null)
            return ;


        TblUserDevice userDevice = tblUserDeviceService.findByDeviceId(deviceId);
        if(userDevice != null)
            return ;

        String registrationId = request.getParameter("registrationId");
        if(registrationId == null)
            return ;

        String phone = request.getParameter("username");
        User user = userRepository.findByPhone(phone);

        TblUserDevice tblUserDevice = new TblUserDevice();
        tblUserDevice.setUserId(user.getUserId());
        tblUserDevice.setPlatform(platform);
        tblUserDevice.setPlatformId(platformId);
        tblUserDevice.setDeviceId(deviceId);
        tblUserDevice.setRegistrationId(registrationId);
        tblUserDeviceService.save(tblUserDevice);
    }

}
