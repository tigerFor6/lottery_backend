//package com.linglong.lottery_backend.prize.service.impl;
//
//import com.linglong.lottery_backend.prize.arithmetic.BettingArit;
//import com.linglong.lottery_backend.prize.entity.TblBettingGroup;
//import com.linglong.lottery_backend.prize.entity.TblBettingGroupDetails;
//import com.linglong.lottery_backend.prize.entity.TblBettingPrize;
//import com.linglong.lottery_backend.prize.entity.custom.BettingMatch;
//import com.linglong.lottery_backend.prize.entity.custom.BettingOdds;
//import com.linglong.lottery_backend.prize.entity.custom.BettingOrder;
//import com.linglong.lottery_backend.prize.exception.GroupSplitException;
//import com.linglong.lottery_backend.prize.repository.TblBettingGroupDetailsRepository;
//import com.linglong.lottery_backend.prize.repository.TblBettingGroupRepository;
//import com.linglong.lottery_backend.prize.repository.TblBettingPrizeRepository;
//import com.linglong.lottery_backend.prize.service.TblBettingPrizeService;
//import com.linglong.lottery_backend.utils.IdWorker;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//@Service
//public class Test implements TblBettingPrizeService {
//
//    @Autowired
//    private TblBettingPrizeRepository repository;
//
//    @Autowired
//    private TblBettingGroupRepository groupRepository;
//
//    @Autowired
//    private TblBettingGroupDetailsRepository groupDetailsRepository;
//
//    @Autowired
//    private IdWorker idWorker;
//
//    /**
//     * 保存投注单
//     * @param bettingOrder
//     */
//    @Override
//    @Transactional
//    public void saveBettingOrder(BettingOrder bettingOrder) {
//        String[] chuanguan = bettingOrder.getChuanGuan().split(",");
//        List<TblBettingPrize> prizes = new ArrayList<>();
//        String voucherNo = String.valueOf(idWorker.nextId());
//        for (int i = 0; i < chuanguan.length; i++) {
//            TblBettingPrize bettingPrize = new TblBettingPrize();
//            bettingPrize.setVoucherNo(voucherNo);
//            bettingPrize.setOrderId(bettingOrder.getOrderId());
//            bettingPrize.setUserId(bettingOrder.getUserId());
//            bettingPrize.setBettingType(Integer.valueOf(chuanguan[i]));
//            bettingPrize.setMultiple(bettingOrder.getMultiple());
//            prizes.add(bettingPrize);
//        }
//        //插入所有串关类型
//        repository.saveAll(prizes);
//
//        prizes.forEach(data -> {
//            List<BettingMatch> bettingMatch = bettingOrder.getMatchs();
//            List<List<BettingMatch>> bettingMatchGroup = BettingArit.arrange(bettingMatch, data.getBettingType());
//
//            bettingMatchGroup.forEach(g1 -> {
//                //比赛1+比赛2
//                TblBettingGroup bg1 = new TblBettingGroup();
//                bg1.setPrizeId(data.getId());
//                List<Long> matchIds = g1.stream().map(e -> e.getMatchId()).collect(Collectors.toList());
//                bg1.setName(StringUtils.join(matchIds, "_"));
//                groupRepository.save(bg1);
//
//                List<List<BettingOdds>> ll = new ArrayList<>();
//                g1.forEach(match -> ll.add(match.getAllOdds(match.getMatchId(),match.getMatchSn())));
//
//                List<List<BettingOdds>> recursiveResult = new ArrayList<>();
//                //递归实现笛卡尔积  拆分组合
//                BettingArit.recursive(ll, recursiveResult, 0, new ArrayList<>());
//                if (recursiveResult.isEmpty()){
//                    throw new GroupSplitException("拆分组合异常");
//                }
//                recursiveResult.forEach(g2 -> {
//                    TblBettingGroup bg2 = new TblBettingGroup();
//                    bg2.setPrizeId(data.getId());
//                    bg2.setName(StringUtils.join(g2.stream().map(e -> e.getItem()).collect(Collectors.toList()), "_"));
//                    bg2.setParentId(bg1.getId());
//                    groupRepository.save(bg2);
//                    List<TblBettingGroupDetails> groupDetails = g2.stream().map(g3 -> {
//                        TblBettingGroupDetails details = new TblBettingGroupDetails();
//                        details.setBettingGroupId(bg2.getId());
//                        details.setMatchId(g3.getMatchId());
//                        details.setMatchSn(g3.getMatchSn());
//                        details.setOrderId(bettingOrder.getOrderId());
//                        details.setPlayCode(g3.getPlayCode());
//                        details.setOddsCode(g3.getItem());
//                        details.setOdds(new BigDecimal(g3.getOdds()));
//                        return details;
//                    }).collect(Collectors.toList());
//                    groupDetailsRepository.saveAll(groupDetails);
//                });
//            });
//        });
//    }
//
//
//    public static  <T> List<List<T>> split(List<T> resList,int count){
//        if(resList==null ||count<1)
//            return  null ;
//        List<List<T>> ret=new ArrayList<List<T>>();
//        int size=resList.size();
//        if(size<=count){ //数据量不足count指定的大小
//            ret.add(resList);
//        }else{
//            int pre=size/count;
//            int last=size%count;
//            //前面pre个集合，每个大小都是count个元素
//            for(int i=0;i<pre;i++){
//                List<T> itemList=new ArrayList<T>();
//                for(int j=0;j<count;j++){
//                    itemList.add(resList.get(i*count+j));
//                }
//                ret.add(itemList);
//            }
//            //last的进行处理
//            if(last>0){
//                List<T> itemList=new ArrayList<T>();
//                for(int i=0;i<last;i++){
//                    itemList.add(resList.get(pre*count+i));
//                }
//                ret.add(itemList);
//            }
//        }
//        return ret;
//
//    }
//
//    @Override
//    public List<TblBettingPrize> findBettingPrizeList(Long orderId) {
//        return repository.findByOrderId(orderId);
//    }
//}
