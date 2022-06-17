package com.linglong.lottery_backend.lottery.area.ctrl;

import com.linglong.lottery_backend.lottery.area.service.TblLotteryAreaService;
import com.linglong.lottery_backend.lottery.match.common.ResultGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/lotteryArea")
@Slf4j
public class TblLotteryAreaController {
    @Autowired
    private TblLotteryAreaService tblLotteryAreaService;

    @GetMapping("getLottery")
    public Object getLottery(){
        return ResultGenerator.genSuccessResult(tblLotteryAreaService.getgetLottery());
    }
}
