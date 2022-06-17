package com.linglong.lottery_backend.utils;

import com.alibaba.fastjson.JSONObject;
import com.linglong.lottery_backend.order.model.order_model.PageFloorOrderStatus;
import com.linglong.lottery_backend.order.model.order_model.PageFloorPayStatus;
import com.linglong.lottery_backend.order.model.order_model.PageFloorSimpleOrderStatus;
import com.linglong.lottery_backend.order.model.order_model.PageListStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * @Author: qihua.li
 * @since: 2019-04-13
 */
@Component
public class UpdateOrderStatusUtil {

    public void updateDetailsOrderStatus(String orderDetails, Map<String, Object> orderMap, Integer orderStatus) {
        JSONObject details = JSONObject.parseObject(orderDetails);
        //订单状态
        //支付状态
        Integer payStatus = Integer.valueOf(details.get("pay_status").toString());
        //出票状态
        Integer billStatus = (Integer) details.get("bill_status");
        //开奖状态
        Integer openPrizeStatus = (Integer) details.get("open_prize_status");
        //中奖状态
        Integer hitPrizeStatus = (Integer) details.get("hit_prize_status");
        //派奖状态
        Integer deliveryPrizeStatus = (Integer) details.get("delivery_prize_status");
        if (orderStatus == 0 && payStatus == 0) {
            //未截止
            orderMap.put("order_status", PageFloorOrderStatus.NOT_ABORT.getStatus());
            orderMap.put("order_code", 0);
            //继续支付
            orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.CONTINUE_PAY.getStatus());
            orderMap.put("simple_order_code", 0);
        } else if (orderStatus == 1 && payStatus != 1) {
            //已取消
            orderMap.put("order_status", PageFloorOrderStatus.CANCEL.getStatus());
            orderMap.put("order_code", 1);
            //已取消
            orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.CANCEL.getStatus());
            orderMap.put("simple_order_code", 1);
        } else if (billStatus == 0 && hitPrizeStatus == 0 && openPrizeStatus == 0) {
            if (payStatus == 1) {
                //待出票
                orderMap.put("order_status", PageFloorOrderStatus.NOT_ISSUE.getStatus());
                orderMap.put("order_code", 2);
                //待出票
                orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.WAIT_BILL.getStatus());
                orderMap.put("simple_order_code", 2);
            } else if ((payStatus == 2 || payStatus == 3) && orderStatus != 0) {
                //出票失败
                orderMap.put("order_status", PageFloorOrderStatus.ISSUE_FAIL.getStatus());
                orderMap.put("order_code", 3);
                //出票失败
                orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.BILL_FAIL.getStatus());
                orderMap.put("simple_order_code", 3);
            }
        } else if (billStatus == 1 && openPrizeStatus == 0) {
            if (payStatus == 3 || payStatus == 1) {
                //部分出票，待开奖
                orderMap.put("order_status", PageFloorOrderStatus.PART_ISSUE_NOT_OPEN_LOTTERY.getStatus());
                orderMap.put("order_code", 5);
                orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.WAIT_LOTTERY.getStatus());
                orderMap.put("simple_order_code", 4);
            }
        } else if (openPrizeStatus == 1 && hitPrizeStatus == 0 && billStatus == 1 && (payStatus == 1 || payStatus == 3)) {
            //部分出票，待开奖
            orderMap.put("order_status", PageFloorOrderStatus.PART_ISSUE_NOT_OPEN_LOTTERY.getStatus());
            orderMap.put("order_code", 5);
            orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.WAIT_LOTTERY.getStatus());
            orderMap.put("simple_order_code", 4);
        } else if (billStatus == 2 && hitPrizeStatus == 0) {
            //待开奖
            if (openPrizeStatus == 0) {
                orderMap.put("order_status", PageFloorOrderStatus.NOT_OPEN_LOTTERY.getStatus());
                orderMap.put("order_code", 4);
                orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.WAIT_LOTTERY.getStatus());
                orderMap.put("simple_order_code", 4);
            } else if (openPrizeStatus == 1) {
                orderMap.put("order_status", PageFloorOrderStatus.NOT_OPEN_LOTTERY.getStatus());
                orderMap.put("order_code", 4);
                orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.WAIT_LOTTERY.getStatus());
                orderMap.put("simple_order_code", 4);
            }
        } else if (hitPrizeStatus == 1) {
            //已YY奖元
            orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.WIN_LOTTERY.getStatus());
            orderMap.put("simple_order_code", 5);
            if (openPrizeStatus == 1) {
                if (billStatus == 2 && payStatus == 1) {
                    //部分开奖，已中奖
                    orderMap.put("order_status", PageFloorOrderStatus.PART_LOTTERY_WIN.getStatus());
                    orderMap.put("order_code", 6);
                } else if (billStatus == 1 && (payStatus == 1 || payStatus == 3)) {
                    //部分出票，部分开奖，已中奖
                    orderMap.put("order_status", PageFloorOrderStatus.PART_ISSUE_PART_LOTTERY_WIN.getStatus());
                    orderMap.put("order_code", 7);
                }
            } else if (openPrizeStatus == 2) {
                if (billStatus == 1 && (payStatus == 1 || payStatus == 3)) {
                    //部分出票全部开奖已中奖
                    orderMap.put("order_status", PageFloorOrderStatus.PART_ISSUE_ALL_LOTTERY_WIN.getStatus());
                    orderMap.put("order_code", 8);
                } else if (billStatus == 2 && payStatus == 1) {
                    //全部开奖已中奖
                    orderMap.put("order_status", PageFloorOrderStatus.ALL_LOTTERY_WIN.getStatus());
                    orderMap.put("order_code", 9);
                }
            }
        } else if (hitPrizeStatus == 99) {
            //已YY奖元
            orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.WIN_LOTTERY.getStatus());
            orderMap.put("simple_order_code", 5);
            if (deliveryPrizeStatus == 2) {
                orderMap.put("order_status", PageFloorOrderStatus.DELIVERY_VERIFYING.getStatus());
                orderMap.put("order_code", 12);
            } else if (openPrizeStatus == 1) {
                if (billStatus == 2 && payStatus == 1) {
                    //部分开奖，已中奖
                    orderMap.put("order_status", PageFloorOrderStatus.PART_LOTTERY_WIN.getStatus());
                    orderMap.put("order_code", 6);
                } else if (billStatus == 1 && (payStatus == 1 || payStatus == 3)) {
                    //部分出票，部分开奖，已中奖
                    orderMap.put("order_status", PageFloorOrderStatus.PART_ISSUE_PART_LOTTERY_WIN.getStatus());
                    orderMap.put("order_code", 7);
                }
            } else if (openPrizeStatus == 2) {
                if (billStatus == 1 && (payStatus == 1 || payStatus == 3)) {
                    //部分出票全部开奖已中奖
                    orderMap.put("order_status", PageFloorOrderStatus.PART_ISSUE_ALL_LOTTERY_WIN.getStatus());
                    orderMap.put("order_code", 8);
                } else if (billStatus == 2 && payStatus == 1) {
                    //全部开奖已中奖
                    orderMap.put("order_status", PageFloorOrderStatus.ALL_LOTTERY_WIN.getStatus());
                    orderMap.put("order_code", 9);
                }
            }
        } else if (openPrizeStatus == 2 && hitPrizeStatus == 0) {
            //未中奖
            orderMap.put("order_status", PageFloorOrderStatus.LOST.getStatus());
            orderMap.put("order_code", 10);
            orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.LOST_LOTTERY.getStatus());
            orderMap.put("simple_order_code", 6);
        } else if (orderStatus == 3 && (payStatus == 2 || payStatus == 3)) {
            //订单失败
            orderMap.put("order_status", PageFloorOrderStatus.ORDER_FAIL.getStatus());
            orderMap.put("order_code", 11);
            orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.ORDER_FAIL.getStatus());
            orderMap.put("simple_order_code", 7);
        }else if (billStatus == 3 && payStatus == 3) {
            //订单失败
            orderMap.put("order_status", PageFloorOrderStatus.ISSUE_FAIL.getStatus());
            orderMap.put("order_code", 3);
            orderMap.put("simple_order_status", PageFloorSimpleOrderStatus.BILL_FAIL.getStatus());
            orderMap.put("simple_order_code", 3);
        }
    }

    public void updateDetailsPayStatus(String orderDetails, Map<String, Object> orderMap, Integer orderStatus) {
        JSONObject details = JSONObject.parseObject(orderDetails);
        //订单状态
        //支付状态
        Integer payStatus = Integer.valueOf(details.get("pay_status").toString());
        //出票状态
        Integer billStatus = (Integer) details.get("bill_status");
        //开奖状态
        Integer openPrizeStatus = (Integer) details.get("open_prize_status");
        //中奖状态
        Integer hitPrizeStatus = (Integer) details.get("hit_prize_status");
        //派奖状态
        Integer deliveryPrizeStatus = (Integer) details.get("delivery_prize_status");
        if (orderStatus == 0) {
            if (payStatus == 0) {
                //待支付
                orderMap.put("pay_status", PageFloorPayStatus.WAIT_PAY.getStatus());
                orderMap.put("pay_code", 0);
            } else if (payStatus == 1) {
                //已支付
                orderMap.put("pay_status", PageFloorPayStatus.PAID.getStatus());
                orderMap.put("pay_code", 4);
            } else if (payStatus == 2) {
                //xx退款中
                orderMap.put("pay_status", PageFloorPayStatus.XX_REFUNDING.getStatus());
                orderMap.put("pay_code", 5);
            } else if (payStatus == 3) {
                //xx已退款
                orderMap.put("pay_status", PageFloorPayStatus.XX_REFUNDED.getStatus());
                orderMap.put("pay_code", 6);
            }
        } else if (orderStatus == 1 && billStatus == 0) {
            if (payStatus == 0) {
                //未支付
                orderMap.put("pay_status", PageFloorPayStatus.NOT_PAY.getStatus());
                orderMap.put("pay_code", 1);
            } else if (payStatus == 1) {
                //支付成功
                orderMap.put("pay_status", PageFloorPayStatus.PAID.getStatus());
                orderMap.put("pay_code", 4);
            } else if (payStatus == 2) {
                //支付超时，退款中
                orderMap.put("pay_status", PageFloorPayStatus.OVERTIME_XX_REFUNDING.getStatus());
                orderMap.put("pay_code", 2);
            } else if (payStatus == 3) {
                //支付超时，已退款
                orderMap.put("pay_status", PageFloorPayStatus.OVERTIME_XX_REFUNDED.getStatus());
                orderMap.put("pay_code", 3);
            }
        } else if (payStatus == 1 && (billStatus == 0 || billStatus == 1) && openPrizeStatus == 0) {
            //已支付
            orderMap.put("pay_status", PageFloorPayStatus.PAID.getStatus());
            orderMap.put("pay_code", 4);
        } else if (openPrizeStatus == 0) {
            if (payStatus == 3 && billStatus == 1) {
                //xx退款中
                orderMap.put("pay_status", PageFloorPayStatus.XX_REFUNDING.getStatus());
                orderMap.put("pay_code", 5);
            } else if (payStatus == 1 && billStatus == 2) {
                //xx已退款
                orderMap.put("pay_status", PageFloorPayStatus.XX_REFUNDED.getStatus());
                orderMap.put("pay_code", 6);
            }
        } else if (hitPrizeStatus == 1) {
            //已中奖
            if (openPrizeStatus == 1) {
                if (payStatus == 1 && deliveryPrizeStatus == 1) {
                    if (billStatus == 2) {
                        //yy元已派奖
                        orderMap.put("pay_status", PageFloorPayStatus.XX_GRANT.getStatus());
                        orderMap.put("pay_code", 7);
                    } else if (billStatus == 1) {
                        //yy元已派奖，xx元退款中
                        orderMap.put("pay_status", PageFloorPayStatus.YY_GRANT_XX_REFUNDING.getStatus());
                        orderMap.put("pay_code", 8);
                    }
                } else if (payStatus == 3 && billStatus == 1) {
                    //yy元已派奖，xx元已退款
                    orderMap.put("pay_status", PageFloorPayStatus.YY_GRANT_XX_REFUNDED.getStatus());
                    orderMap.put("pay_code", 9);
                }
            } else if (openPrizeStatus == 2) {
                if (payStatus == 1 && billStatus == 2) {
                    //yy元已派奖
                    orderMap.put("pay_status", PageFloorPayStatus.XX_GRANT.getStatus());
                    orderMap.put("pay_code", 7);
                } else if (payStatus == 1 && billStatus == 1 && deliveryPrizeStatus == 1) {
                    //yy元已派奖，xx元退款中
                    orderMap.put("pay_status", PageFloorPayStatus.YY_GRANT_XX_REFUNDING.getStatus());
                    orderMap.put("pay_code", 8);
                } else if (payStatus == 3 && billStatus == 1 && deliveryPrizeStatus == 1) {
                    //yy元已派奖，xx元已退款
                    orderMap.put("pay_status", PageFloorPayStatus.YY_GRANT_XX_REFUNDED.getStatus());
                    orderMap.put("pay_code", 9);
                }
            }
        } else if (hitPrizeStatus == 99) {
            if (deliveryPrizeStatus == 2) {
                orderMap.put("pay_status", PageFloorPayStatus.BIG_REWARD_VERIFYING.getStatus());
                orderMap.put("pay_code", 11);
            } else if (openPrizeStatus == 1) {
                if (payStatus == 1 && deliveryPrizeStatus == 1) {
                    if (billStatus == 2) {
                        //yy元已派奖
                        orderMap.put("pay_status", PageFloorPayStatus.XX_GRANT.getStatus());
                        orderMap.put("pay_code", 7);
                    } else if (billStatus == 1) {
                        //yy元已派奖，xx元退款中
                        orderMap.put("pay_status", PageFloorPayStatus.YY_GRANT_XX_REFUNDING.getStatus());
                        orderMap.put("pay_code", 8);
                    }
                } else if (payStatus == 3 && billStatus == 1) {
                    //yy元已派奖，xx元已退款
                    orderMap.put("pay_status", PageFloorPayStatus.YY_GRANT_XX_REFUNDED.getStatus());
                    orderMap.put("pay_code", 9);
                }
            } else if (openPrizeStatus == 2) {
                if (payStatus == 1 && billStatus == 2) {
                    //yy元已派奖
                    orderMap.put("pay_status", PageFloorPayStatus.XX_GRANT.getStatus());
                    orderMap.put("pay_code", 7);
                } else if (payStatus == 1 && billStatus == 1 && deliveryPrizeStatus == 1) {
                    //yy元已派奖，xx元退款中
                    orderMap.put("pay_status", PageFloorPayStatus.YY_GRANT_XX_REFUNDING.getStatus());
                    orderMap.put("pay_code", 8);
                } else if (payStatus == 3 && billStatus == 1 && deliveryPrizeStatus == 1) {
                    //yy元已派奖，xx元已退款
                    orderMap.put("pay_status", PageFloorPayStatus.YY_GRANT_XX_REFUNDED.getStatus());
                    orderMap.put("pay_code", 9);
                }
            }
        } else if (openPrizeStatus == 2 && hitPrizeStatus == 0) {
            //未中奖
            if (payStatus == 1 && billStatus == 2) {
                //已支付
                orderMap.put("pay_status", PageFloorPayStatus.PAID.getStatus());
                orderMap.put("pay_code", 4);
            } else if (payStatus == 1 && billStatus == 1) {
                //xx元已退款
                orderMap.put("pay_status", PageFloorPayStatus.XX_REFUNDING.getStatus());
                orderMap.put("pay_code", 5);
            } else if (payStatus == 3 && billStatus == 1) {
                //xx元已退款
                orderMap.put("pay_status", PageFloorPayStatus.XX_REFUNDED.getStatus());
                orderMap.put("pay_code", 6);
            }
        } else if (orderStatus == 3) {
            //订单失败
            if (payStatus == 2) {
                //xx元退款中
                orderMap.put("pay_status", PageFloorPayStatus.XX_REFUNDING.getStatus());
                orderMap.put("pay_code", 5);
            } else if (payStatus == 3) {
                //xx元退款到账户余额
                orderMap.put("pay_status", PageFloorPayStatus.XX_REFUNDED_TO_BALANCE.getStatus());
                orderMap.put("pay_code", 10);
            }
        }
    }

    public void updateListPageOrdersStatus(String orderDetails, Map<String, Object> orderMap, Integer orderStatus) {
        JSONObject details = JSONObject.parseObject(orderDetails);
        //订单状态
        //支付状态
        Integer payStatus = Integer.valueOf(details.get("pay_status").toString());
        //出票状态
        Integer billStatus = (Integer) details.get("bill_status");
        //开奖状态
        Integer openPrizeStatus = (Integer) details.get("open_prize_status");
        //中奖状态
        Integer hitPrizeStatus = (Integer) details.get("hit_prize_status");
        //派奖状态
        Integer deliveryPrizeStatus = (Integer) details.get("delivery_prize_status");

        BigDecimal baseNum = new BigDecimal(100);
        BigDecimal bonus = new BigDecimal(details.get("bonus").toString());
        bonus = bonus.divide(baseNum).setScale(2, RoundingMode.FLOOR);;
        details.put("bonus", bonus);

        if (orderStatus == 0 && payStatus == 0) {
            //待支付
            orderMap.put("status", PageListStatus.WAIT_PAY.getStatus());
            orderMap.put("status_code", 0);
        } else if (orderStatus == 1 && payStatus != 1) {
            //已取消
            orderMap.put("status", PageListStatus.CANCEL.getStatus());
            orderMap.put("status_code", 1);
        } else if (billStatus == 0 && openPrizeStatus == 0 && hitPrizeStatus == 0) {
            if (payStatus == 1) {
                //待出票
                orderMap.put("status", PageListStatus.NOT_ISSUE.getStatus());
                orderMap.put("status_code", 2);
            } else if (payStatus == 2 || payStatus == 3) {
                //出票失败
                orderMap.put("status", PageListStatus.BILL_FAIL.getStatus());
                orderMap.put("status_code", 3);
            }
        } else if (openPrizeStatus == 0 && hitPrizeStatus == 0) {
            //待开奖
            if (payStatus == 1 && billStatus == 2) {
                orderMap.put("status", PageListStatus.NOT_LOTTERY.getStatus());
                orderMap.put("status_code", 4);
            } else if ((payStatus == 1 || payStatus == 3) && billStatus == 1) {
                //部分出票，待开奖
                orderMap.put("status", PageListStatus.PART_NOT_LOTTERY.getStatus());
                orderMap.put("status_code", 5);
            }else if (payStatus == 3 && billStatus == 3){
                //出票失败
                orderMap.put("status", PageListStatus.BILL_FAIL.getStatus());
                orderMap.put("status_code", 3);
            }
        } else if (openPrizeStatus == 1 && hitPrizeStatus == 0 && payStatus == 1 && billStatus == 2) {
            //待开奖
            orderMap.put("status", PageListStatus.NOT_LOTTERY.getStatus());
            orderMap.put("status_code", 4);
        } else if (hitPrizeStatus == 1) {
            //已YY奖元
            if (openPrizeStatus == 1) {
                if (payStatus == 1 && (billStatus == 1 || billStatus == 2)) {
                    orderMap.put("status", PageListStatus.WIN_BONUS.getStatus());
                    orderMap.put("status_code", 6);
                    orderMap.put("bonus", details.get("bonus"));
                } else if (payStatus == 3 && billStatus == 1) {
                    orderMap.put("status", PageListStatus.WIN_BONUS.getStatus());
                    orderMap.put("status_code", 6);
                    orderMap.put("bonus", details.get("bonus"));
                }
            } else if (openPrizeStatus == 2) {
                if (billStatus == 1 && (payStatus == 1 || payStatus == 3)) {
                    orderMap.put("status", PageListStatus.WIN_BONUS.getStatus());
                    orderMap.put("status_code", 6);
                    orderMap.put("bonus", details.get("bonus"));
                } else if (billStatus == 2 && payStatus == 1) {
                    orderMap.put("status", PageListStatus.WIN_BONUS.getStatus());
                    orderMap.put("status_code", 6);
                    orderMap.put("bonus", details.get("bonus"));
                }
            }
        } else if (hitPrizeStatus == 99) {
            if (deliveryPrizeStatus == 2) {
                orderMap.put("status", PageListStatus.BIG_REWARD_VERIFYING.getStatus());
                orderMap.put("status_code", 8);
                orderMap.put("bonus", details.get("bonus"));
            }//已YY奖元
            if (openPrizeStatus == 1) {
                if (payStatus == 1 && (billStatus == 1 || billStatus == 2)) {
                    orderMap.put("status", PageListStatus.WIN_BONUS.getStatus());
                    orderMap.put("status_code", 6);
                    orderMap.put("bonus", details.get("bonus"));
                } else if (payStatus == 3 && billStatus == 1) {
                    orderMap.put("status", PageListStatus.WIN_BONUS.getStatus());
                    orderMap.put("status_code", 6);
                    orderMap.put("bonus", details.get("bonus"));
                }
            } else if (openPrizeStatus == 2) {
                if (billStatus == 1 && (payStatus == 1 || payStatus == 3)) {
                    orderMap.put("status", PageListStatus.WIN_BONUS.getStatus());
                    orderMap.put("status_code", 6);
                    orderMap.put("bonus", details.get("bonus"));
                } else if (billStatus == 2 && payStatus == 1) {
                    orderMap.put("status", PageListStatus.WIN_BONUS.getStatus());
                    orderMap.put("status_code", 6);
                    orderMap.put("bonus", details.get("bonus"));
                }
            }
        } else if (openPrizeStatus == 2 && hitPrizeStatus == 0) {
            //未中奖
            if (payStatus == 1 && (billStatus == 2 || billStatus == 1)) {
                orderMap.put("status", PageListStatus.LOST.getStatus());
                orderMap.put("status_code", 7);
            } else if (payStatus == 3 && billStatus == 1) {
                orderMap.put("status", PageListStatus.LOST.getStatus());
                orderMap.put("status_code", 7);
            }
        } else if (orderStatus == 3 && (payStatus == 2 || payStatus == 3)) {
            //订单失败
            orderMap.put("status", PageListStatus.ORDER_FAIL.getStatus());
            orderMap.put("status_code", 8);
        }
    }
}
