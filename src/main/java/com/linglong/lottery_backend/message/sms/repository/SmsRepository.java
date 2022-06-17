package com.linglong.lottery_backend.message.sms.repository;

import com.linglong.lottery_backend.message.sms.entity.TblSms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/3
 */
public interface SmsRepository extends JpaRepository<TblSms, Long> {

    TblSms findFirstByBusIdAndUserId(Long busId, Long userId);

    TblSms findByBusId(Long busId);
}
