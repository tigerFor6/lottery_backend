package com.linglong.lottery_backend.order.model.order_model;

/**
 * @Author: qihua.li
 * @since: 2019-04-04
 */
public enum PlayType {
    SPF("001", "spf"),
    RQSPF("002", "rqspf"),
    BIFEN("005", "bifen"),
    BQC("004", "bqc"),
    JQS("003", "jqs"),
    SF("001", "sf"),
    RFSF("002", "rfsf"),
    DXF("003", "dxf"),
    TWOCOLORTYPEONE("3-1", "单式"),
    TWOCOLORTYPETWO("3-2", "复式"),
    TWOCOLORTYPETHREE("3-3", "胆拖"),
    ELEVENCHOOSEFIVE4111("4-1-1-1", "前一"),
    ELEVENCHOOSEFIVE4121("4-1-2-1", "前二直选"),
    ELEVENCHOOSEFIVE4122("4-1-2-2", "前二组选"),
    ELEVENCHOOSEFIVE4123("4-1-2-3", "任选二"),
    ELEVENCHOOSEFIVE4131("4-1-3-1", "前三直选"),
    ELEVENCHOOSEFIVE4132("4-1-3-2", "前三组选"),
    ELEVENCHOOSEFIVE4133("4-1-3-3", "任选三"),
    ELEVENCHOOSEFIVE4143("4-1-4-3", "任选四"),
    ELEVENCHOOSEFIVE4153("4-1-5-3", "任选五"),
    ELEVENCHOOSEFIVE4163("4-1-6-3", "任选六"),
    ELEVENCHOOSEFIVE4173("4-1-7-3", "任选七"),
    ELEVENCHOOSEFIVE4183("4-1-8-3", "任选八"),
    ELEVENCHOOSEFIVE4223("4-2-2-3", "任选二胆拖"),
    ELEVENCHOOSEFIVE4222("4-2-2-2", "前二组选胆拖"),
    ELEVENCHOOSEFIVE4233("4-2-3-3", "任选三胆拖"),
    ELEVENCHOOSEFIVE4232("4-2-3-2", "前三组选胆拖"),
    ELEVENCHOOSEFIVE4243("4-2-4-3", "任选四胆拖"),
    ELEVENCHOOSEFIVE4253("4-2-5-3", "任选五胆拖"),
    ELEVENCHOOSEFIVE4263("4-2-6-3", "任选六胆拖"),
    ELEVENCHOOSEFIVE4273("4-2-7-3", "任选七胆拖"),
    ELEVENCHOOSEFIVE4283("4-2-8-3", "任选八胆拖"),
    SUPERLOTTOONE("5-1", "单式"),
    SUPERLOTTOTWO("5-2", "复式"),
    SUPERLOTTOTHREE("5-3", "胆拖"),
    RANKONE("6-1", "排列三直选"),
    RANKTWO("6-2", "排列三组三"),
    RANKTHREE("6-3", "排列三组六"),
    RANK61("6-1", "排列三直选"),
    RANK62("6-2", "排列三组三"),
    RANK63("6-3", "排列三组六"),
    RANK71("7-1", "排列五单注"),
    RANK72("7-2", "排列三组三"),
    PL5SINGLE("7-1", "单式"),
    PL5MULTIPLE("7-2", "复式");

    private String type;
    private String value;

    PlayType(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue(String type) {
        PlayType.values();
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    PlayType() {
    }}
