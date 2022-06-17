package com.linglong.lottery_backend.ticket.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Created by ynght on 2019-04-26
 */
@Data
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Entity(name = "tbl_platform")
public class Platform {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
    @Id
    @Column(name = "platform_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer platformId;

    @Column(name = "platform_en")
    private String platformEn;

    @Column(name = "platform_name")
    private String platformName;

    @Column(name = "platform_account")
    private String platformAccount;

    @Column(name = "platform_code")
    private String platformCode;

    @Column(name = "platform_key")
    private String platformKey;

    @Column(name = "platform_pwd")
    private String platformPwd;

    @Column(name = "platform_url")
    private String platformUrl;

    @Column(name = "query_num")
    private Integer queryNum;//一次查询票数量

    @Column(name = "bet_num")
    private Integer betNum;//一次投注票数量

    @Version
    private Integer version;

//    public String getPlatformAccount(Integer gameType) {
//        return chooseCorrectValue(gameType, platformAccount);
//    }

//    public String getPlatformAccount(Integer gameType) {
//        return chooseCorrectValue(gameType, platformCode);
//    }
//
//    public String getPlatformKey(Integer gameType) {
//        return chooseCorrectValue(gameType, platformPwd);
//    }
//
//    private String chooseCorrectValue(Integer gameType, String splitObject) {
//        Integer type = GameCache.getGame(gameType).getType();
//        String[] platformCodes = splitObject.split(CommonConstant.COMMON_ESCAPE_STR + CommonConstant
//                .COMMON_VERTICAL_STR);
//        String code = null;
//        switch (type) {
//            case Game.TYPE_JC:
//                code = platformCodes[1];
//                break;
//            default:
//                code = platformCodes[1];
//        }
//        return code;
//    }
}
