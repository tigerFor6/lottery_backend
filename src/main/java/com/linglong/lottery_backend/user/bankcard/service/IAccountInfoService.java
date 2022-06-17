package com.linglong.lottery_backend.user.bankcard.service;

import com.linglong.lottery_backend.user.bankcard.pojo.AccountInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author: qihua.li
 * @since: 2019-03-20
 */
public interface IAccountInfoService {
    /**
     * 获取绑定的银行卡列表页
     * 通过用户的userId查询所有绑定的cardId
     */
    List<AccountInfo> findBindingPage(String userId, int page);

    /**
     * 查询所有绑定
     */
    List<AccountInfo> findAllBinding(String userId);

    /**
     * 获取绑定的银行卡的详细信息
     */
    AccountInfo findBankCardDetail(Long id, String userId);

    /**
     * 解绑定
     */
    void removeBindingBankCard(String cardNum, String userId);

    /**
     * 3要素校验
     */
    Map<String, Object> checkBankcardVerity(String bankcardNum, String idCard, String name, String userId) throws Exception;

    /**
     * 预查询
     */
    Map<String, Object> preFindAccountInfo(String userId);
    /**
     * 发送短信
     */
    String sendSms(String phone, String userId);

    /**
     * 4要素验证
     */
    void checkBankcard4Verity(String bankcardNum, String idCard, String name, String mobile, String userId, String code) throws Exception;

    /**
     * 验证手机号是否绑定了卡
     */
    Boolean checkPhoneIsBinding(String phone, String userId);

    /**
     * 校验身份证号码
     */
    void checkIdCard(String idCard, String userId);

    /**
     * 校验卡号是否重复绑定
     */
    void checkBankcardNumIsDuplicate(String userId, String bankcardNum);

    /**
     * 清空状态为2的
     */
    void deleteBankcardStatusIs2(String userId, String bankcardNum);

    AccountInfo getAccountInfoById(long id);

}
