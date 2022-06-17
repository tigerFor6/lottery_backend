package com.linglong.lottery_backend.ticket.remote.bjgc.callback;

import com.alibaba.fastjson.JSON;
import com.linglong.lottery_backend.ticket.remote.bjgc.entity.Message;
import com.linglong.lottery_backend.ticket.task.TicketAsyncExecutorTask;
import com.linglong.lottery_backend.ticket.util.XStreamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/callback")
@Slf4j
public class BJGCCallback {
    @Autowired
    TicketAsyncExecutorTask ticketAsyncExecutorTask;
    /**
     * 北京公彩回调地址
     * @param msg
     */
    @RequestMapping(value = "bjgc/receive")
    public void receive(@RequestParam("cmd") String cmdStr,@RequestParam("msg") String msg) {
        log.info("receive from bjgc msg: {}", msg);
        try{
            int cmd = Integer.valueOf(cmdStr).intValue();
            switch (cmd) {
                case 3003:
                    //出票通知
                    Message message = XStreamUtil.toBean(msg, Message.class);
                    log.info("receive from 3003 body: {}", JSON.toJSONString(message));
                    ticketAsyncExecutorTask.doUpdateTicketStatus(message.getBody().getTickets());
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            log.error("bjgc receive error :{}",e);
        }

    }
}
