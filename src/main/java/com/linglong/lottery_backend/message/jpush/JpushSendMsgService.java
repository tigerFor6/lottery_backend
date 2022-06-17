package com.linglong.lottery_backend.message.jpush;


import cn.jiguang.common.DeviceType;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import com.linglong.lottery_backend.user.account.entity.TblUserDevice;
import com.linglong.lottery_backend.user.account.service.TblUserDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 极光推送
 */
@Slf4j
@Service
public class JpushSendMsgService {

    @Autowired
    JPushClient jPushClient;

    @Autowired
    TblUserDeviceService tblUserDeviceService;

    public int sendByPlatformAllAndUserAll(String content) {
        PushPayload pushPayload = buildPushPayLoadByPlatformAllAndUserAll(content);
        return sendJpush(pushPayload);
    }

    public int sendByPlatformAndRegistId(String content, String platform, Collection<String> registrationIds) {
        PushPayload pushPayload = buildPushPayLoadByPlatformAndRegistId(registrationIds,content,platform);
        return sendJpush(pushPayload);
    }

    public int sendByUserId(String userId, String content) {
        List<TblUserDevice> userDevices = tblUserDeviceService.findByUserIdAndStatus(userId, false);
        if(userDevices.isEmpty()) {
            return 0;
        }
        Collection<String> registrationIds = userDevices.stream().map(e -> e.getRegistrationId()).collect(Collectors.toList());
        PushPayload pushPayload = buildPushPayLoadByPlatformAndRegistId(registrationIds, content, "ALL");
        return sendJpush(pushPayload);
    }

    /**
     * 发送消息，返回状态码
     * @param pushPayload
     * @return
     */
    private int sendJpush(PushPayload pushPayload) {
        PushResult result = null;
        try {
            result = jPushClient.sendPush(pushPayload);
        }catch (Exception e) {
            log.error("sendJpush ERROR -> {}",e);
        }
        if(result == null)
            return 500;

        return result.getResponseCode();
    }

    /**
     * 封装发送系统参数
     * @param platform
     * @return
     */
    private Platform getPlatform(String platform) {
        if(platform.equalsIgnoreCase("all")) {
            return Platform.all();
        }

        Platform.Builder builder = Platform.newBuilder();

        if(platform.indexOf(DeviceType.Android.value()) >= 0) {
            builder.addDeviceType(DeviceType.Android);

        }
        if(platform.indexOf(DeviceType.IOS.value()) >= 0) {
            builder.addDeviceType(DeviceType.IOS);

        }
        if(platform.indexOf(DeviceType.WinPhone.value()) >= 0) {
            builder.addDeviceType(DeviceType.WinPhone);

        }

        return builder.build();
    }

    /**
     * 发送消息 -> 所有系统、所有用户
     * @param content
     * @return
     */
    private PushPayload buildPushPayLoadByPlatformAllAndUserAll(String content) {
        return buildPushPayLoad(Platform.all(), Audience.all(), Notification.alert(content), Options.newBuilder().setApnsProduction(false).setTimeToLive(86000).build());
    }

    /**
     * 根据 registrationId 发送消息
     * @param registrationIds
     * @param content
     * @param platform
     * @return
     */
    private PushPayload buildPushPayLoadByPlatformAndRegistId(Collection<String> registrationIds, String content, String platform) {
        return buildPushPayLoad(getPlatform(platform), Audience.registrationId(registrationIds), Notification.alert(content), Options.newBuilder().setApnsProduction(false).setTimeToLive(86000).build());
    }


    /**
     * 封装发送消息体
     * @param platform
     * @param audience
     * @param notification
     * @param options
     * @return
     */
    private PushPayload buildPushPayLoad(Platform platform, Audience audience, Notification notification, Options options) {
        return PushPayload.newBuilder()
                .setPlatform(platform)
                .setAudience(audience)
                .setNotification(notification)
                .setOptions(options)
                .build();
    }
}
