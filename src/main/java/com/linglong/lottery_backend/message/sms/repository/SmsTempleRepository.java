package com.linglong.lottery_backend.message.sms.repository;

import com.linglong.lottery_backend.message.sms.entity.TblSms;
import com.linglong.lottery_backend.message.sms.entity.TblSmsTemple;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description
 *
 * @author xiaohu.liu
 * @since: 2019/7/13
 */
public interface SmsTempleRepository extends JpaRepository<TblSmsTemple, Long> {

}
