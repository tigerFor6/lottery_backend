package com.linglong.lottery_backend.user.bankcard.repository;

import com.linglong.lottery_backend.user.bankcard.pojo.AccountInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @Author: qihua.li
 * @since: 2019-03-20
 */
public interface IAccountInfoRepository extends JpaRepository<AccountInfo, Long>, JpaSpecificationExecutor<AccountInfo> {

    AccountInfo findByIdAndUserId(Long id, String userId);

    AccountInfo findByUserIdAndBankCardNumAndCardStatus(String userId, String bankcardNumb, Integer cardStatus);

    AccountInfo findTopByUserIdOrderByCreatedTimeAsc(String userId);

    List<AccountInfo> findByUserIdAndCardStatus(String userId, Integer status);

    Page<AccountInfo> findByUserIdAndCardStatus(String userId, Integer status, Pageable pageable);

    AccountInfo findTopByUserIdAndBankCardNumAndCardStatusOrderByCreatedTimeAsc(String userId, String bankcardNumb, Integer cardStatus);

    Page<AccountInfo> findTopByUserIdAndBankCardNumAndCardStatusInOrderByCreatedTimeAsc(String userId, String bankCardNum, int[] status, Pageable pageable);

    Page<AccountInfo> findTopByUserIdAndCardStatusIn(String userId, int[] status, Pageable pageable);
}
