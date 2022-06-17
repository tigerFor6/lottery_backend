package com.linglong.lottery_backend.lottery.match.ctrl.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class Odds implements Serializable {

    @JsonProperty("is_danguan")
    private String is_danguan;

    @JsonProperty("sale_on")
    private String sale_on;

    @JsonProperty("play_type")
    private String play_type;

    @JsonProperty("sp")
    private String[] sp;

    @JsonProperty("handicap")
    private String handicap;

    @JsonProperty("odds")
    private String odds;

    public String getIs_danguan() {
        return is_danguan;
    }

    public Odds setIs_danguan(String is_danguan) {
        this.is_danguan = is_danguan;
        return this;
    }

    public String getSale_on() {
        return sale_on;
    }

    public Odds setSale_on(String sale_on) {
        this.sale_on = sale_on;
        return this;
    }

    public String getPlay_type() {
        return play_type;
    }

    public Odds setPlay_type(String play_type) {
        this.play_type = play_type;
        return this;
    }

    public String[] getSp() {
//        if (null != sp){
//            List<String> mlist = Arrays.asList(sp).stream().map(d ->{
//               // System.out.println(d);
//                return JSON.parseObject(d).get("v").toString();
//            }).collect(Collectors.toList());
//            return mlist.toArray(new String[mlist.size()]);
//        }
        return sp;
    }

    public Odds setSp(String[] sp) {
        this.sp = sp;
        return this;
    }

    public String getHandicap() {
        return handicap;
    }

    public Odds setHandicap(String handicap) {
        this.handicap = handicap;
        return this;
    }

    public String getOdds() {
        return odds;
    }

    public Odds setOdds(String odds) {
        this.odds = odds;
        return this;
    }

//    class Sp implements Serializable{     //内部类
//        private String k;
//        private String v;
//
//        public String getK() {
//            return k;
//        }
//
//        public Sp setK(String k) {
//            this.k = k;
//            return this;
//        }
//
//        public String getV() {
//            return v;
//        }
//
//        public Sp setV(String v) {
//            this.v = v;
//            return this;
//        }
//    }
}
