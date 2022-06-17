package com.linglong.lottery_backend.ticket.remote.aoli;

import com.linglong.lottery_backend.ticket.enums.AoliEnum;
import com.linglong.lottery_backend.ticket.task.TicketAsyncExecutorTask;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/callback")
@Slf4j
public class AOLICallBack {

    @Autowired
    TicketAsyncExecutorTask ticketAsyncExecutorTask;

    @Autowired
    AOLIService aoliService;


    /**
     * {"resultXML":[""],"data":["{\"cardCode\":80000075,\"key\":\"3178805CCDBC64FDFAAA8ACAFA2E7040\",\"lotteryCode\":\"JCZQ\",\"message\":[{\"localPrint\":\"{\\\"localPrint\\\":true}\",\"odds\":{\"spMap\":{\"matchKey\":[{\"matchKey\":\"119962\",\"value\":\"{\\\"1\\\":\\\"5.25\\\"}\"},{\"matchKey\":\"119963\",\"value\":\"{\\\"0\\\":\\\"1.94\\\"}\"}],\"matchNumber\":[{\"matchNumber\":\"20190717001\",\"value\":\"{\\\"1\\\":\\\"5.25\\\"}\"},{\"matchNumber\":\"20190717002\",\"value\":\"{\\\"0\\\":\\\"1.94\\\"}\"}]}},\"orderNumber\":\"249JCZQ1563348223240310885\",\"result\":\"SUC_TICKET\",\"successTime\":\"2019-07-17 15:24:41.0\",\"ticketId\":\"1151392014500237313\"}],\"messageType\":\"ticketResult\",\"resultCode\":\"SUCCESS\"}"]}
     * @param request
     * @return
     */
    @RequestMapping(value = "aoli/receive")
    public String receive(HttpServletRequest request) {
        Map<String, String[]> stringMap = request.getParameterMap();
        log.info("aoli callback receive ：" + JSONObject.fromObject(stringMap).toString());

        if(stringMap.containsKey("data")) {
            JSONObject resultJSON = JSONObject.fromObject(stringMap.get("data")[0]);
            String lotteryCode = resultJSON.getString("lotteryCode");

            List<Message> messages;
            if(lotteryCode.equals(AoliEnum.SHX115.getStatus())) {
                messages = aoliService.resSHX115resultData(AoliEnum.SHX115.getCode(), resultJSON);

            }else {
                Integer gameType = null;
                if(lotteryCode.equals(AoliEnum.JCZQ.getStatus()))
                    gameType = AoliEnum.JCZQ.getCode();
                if(lotteryCode.equals(AoliEnum.JCLQ.getStatus()))
                    gameType = AoliEnum.JCLQ.getCode();
                if(lotteryCode.equals(AoliEnum.SSQ.getStatus()))
                    gameType = AoliEnum.SSQ.getCode();
                if(lotteryCode.equals(AoliEnum.DLT.getStatus()))
                    gameType = AoliEnum.DLT.getCode();
                if(lotteryCode.equals(AoliEnum.PL3.getStatus()))
                    gameType = AoliEnum.PL3.getCode();
                if(lotteryCode.equals(AoliEnum.PL5.getStatus()))
                    gameType = AoliEnum.PL5.getCode();

                messages = aoliService.resJCZQLQSSQresultData(gameType, resultJSON);
            }

            ticketAsyncExecutorTask.doUpdateOrderStatusByAOLI(messages);
        }

        return "0000";
    }
}
