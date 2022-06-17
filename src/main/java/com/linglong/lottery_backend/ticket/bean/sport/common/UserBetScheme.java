package com.linglong.lottery_backend.ticket.bean.sport.common;

import com.linglong.lottery_backend.common.error.BusinessException;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.utils.CommonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class UserBetScheme {//Prototype

    private static final Log log = LogFactory.getLog(UserBetScheme.class);

    //规则类，Singleton
    private RuleBetScheme ruleBetScheme;
    private RulePassScheme rulePassScheme;

    //用户投注内容，Prototype
    private SetBet setBet;
    protected SetPass setPass;

    Comparator<String> betCompare = new Comparator<String>() {
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    public UserBetScheme(RuleBetScheme ruleBetScheme, RulePassScheme rulePassScheme) {
        this.ruleBetScheme = ruleBetScheme;
        this.rulePassScheme = rulePassScheme;
    }

    private UserBetScheme(RuleBetScheme ruleBetScheme, RulePassScheme rulePassScheme, SetBet setBet, SetPass setPass) {

        this.ruleBetScheme = ruleBetScheme;
        this.rulePassScheme = rulePassScheme;
        this.setSetBet(setBet);
        this.setPass = setPass;
    }

    public void parser(String stack, String extra, boolean dan) {
        setSetBet(ruleBetScheme.parser(stack, dan));
        setPass = rulePassScheme.parser(extra);
        for (EntryPass pass : setPass.set) {
            if (pass.getM() > getSetBet().getTotalSecene()) {
                throw new BusinessException("投注信息格式错误：场数应大于等于关数");
            }
            if ((pass.getM()) <= getSetBet().getTotalSeceneDan()) {
                throw new BusinessException("投注信息格式错误：胆数应小于关数");
            }
            if (pass.getM() == setBet.getTotalSecene() && pass.getN() == 1 && setBet.getTotalSeceneDan() > 0) {
                throw new BusinessException("投注信息格式错误：N场N串1不允许选胆！");
            }
        }
    }

    public Map<String, BetCounter> calculateBetCounter(int times) {
        return calculateBetCounter(times, null);
    }

    public Map<String, BetCounter> calculateBetCounter(int times, Map<String, BetCounter> map) {
        if (map == null) {
            map = new TreeMap<String, BetCounter>(betCompare);
        }
        for (EntryBet entry : getSetBet().getSet()) {

            BetCounter betCounter;
            if (map.get(entry.getId()) == null) {
                betCounter = new BetCounter(ruleBetScheme.ruleBet);
            } else {
                betCounter = map.get(entry.getId());
            }
            betCounter.addAll(entry.getBets(), times);
            map.put(entry.getId(), betCounter);
        }
        return map;
    }

    public int calculateSecene() {

        int counter = 0;
        int[][] matrix;
        List<Combination[]> list;
        for (EntryPass mn : setPass.set) {
            for (int n : rulePassScheme.compose(mn.getM(), mn.getN())) {
                //适合非串1的过关方式中组合关数大于胆数
                //适合关数大于胆
                if (n > getSetBet().getTotalSeceneDan()) {

                    matrix = getSetBet().getBetMatrix();
                    list = processScheme(n, matrix);
                    counter += calculateSchemeSecene(list, matrix);
                    //根据淘宝的算法，如果是非串1的过关方式，当组合的关数小于胆的时候
                    //则：在胆里选关数
                } else {

                    matrix = getSetBet().getBetDanMatrix();
                    list = processScheme(n, matrix);
                    counter += calculateSchemeSecene(list, matrix);
                }
            }
        }
//		if (log.isDebugEnabled())
//		{
//			log.debug("计算注数共计：" + counter + "注！");
//		}
        return counter;
    }

    private int calculateSchemeSecene(List<Combination[]> list, int[][] matrix) {
        int result = 0;
        for (Combination[] tab : list) {
            int temp = 1;
            for (int i = 0; i < tab.length; i++) {
                temp *= (int) CommonUtil.combine(tab[i].c, tab[i].n) * Math.pow(matrix[0][i], tab[i].n + matrix[2][i]);
            }
            result += temp;
        }
        return result;
    }

    public List<BetVo> splitToAward() {
        //只针对拆过单的投注号，重新拆，为了计算中奖金额，只拆非串1类的
        List<BetVo> result = new ArrayList<BetVo>();

        List<Combination[]> list;
        if (getSetBet().getTotalSeceneDan() != 0)
            throw new BusinessException("拆过单后的投注号不应有胆！");
        if (setPass.set.size() != 1)
            throw new BusinessException("拆过单后只能有一种串关方式！");

        for (EntryPass mn : setPass.set) {
            if (mn.getM() == getSetBet().getTotalSecene() && mn.getN() == 1) {
                BetVo betVo = new BetVo();
                betVo.setPassWay(mn);
                betVo.setBets(getSetBet().getSet());
                result.add(betVo);
            }
            //过关串1 or 单关
            else if ((mn.getM() == getSetBet().getTotalSecene() && mn.getN() > 1) || (mn.getM() == 1 && mn.getN() ==
                    1)) {
                for (int n : rulePassScheme.compose(mn.getM(), mn.getN())) {
                    list = processScheme(n, getSetBet().getBetMatrix());

                    List<List<EntryBet>> combinations = buildCombination(list, getSetBet());
                    for (List<EntryBet> combination : combinations) {
                        EntryPass subEntryPass = new EntryPass(n, 1);
                        BetVo betVo = new BetVo();
                        betVo.setPassWay(subEntryPass);
                        betVo.setBets(combination);
                        result.add(betVo);
                    }
                }
            } else {
                throw new BusinessException("拆过单后投注号格式仍然错误！");
            }
        }

        return result;
    }

    public List<BetVo> splitSecene(boolean splitToOne) {

        List<BetVo> result = new ArrayList<BetVo>();
        int[][] matrix;
        List<Combination[]> list;

        for (EntryPass mn : setPass.set) {
            //没选胆的情况下，非单关模式且选的场数等于过关的关数(这种情况中心支持该串关方法的投注)，不用拆单
            if (mn.getM() == getSetBet().getTotalSecene() && (splitToOne ? mn.getN() == 1 : Boolean.TRUE)
                    && getSetBet().getTotalSeceneDan() == 0) {
                int counter = 0;
                for (int n : rulePassScheme.compose(mn.getM(), mn.getN())) {
                    matrix = getSetBet().getBetMatrix();
                    list = processScheme(n, matrix);
                    counter += calculateSchemeSecene(list, matrix);
                }

                BetVo betVo = new BetVo();
                betVo.setPassWay(mn);
                betVo.setBets(getSetBet().getSet());
                betVo.setBetTimes(counter);
                betVo.setBetWay(!getSetBet().isMultiple());
                result.add(betVo);

            } else
            //其他情况需要拆成m串1的投注单
            {
                for (int n : rulePassScheme.compose(mn.getM(), mn.getN())) {
                    SetBet subSetBet;
                    //适合非串1的过关方式中组合关数大于胆数
                    //适合关数大于胆
                    if (n > getSetBet().getTotalSeceneDan()) {

                        subSetBet = getSetBet();
                        list = processScheme(n, subSetBet.getBetMatrix());

                        //根据淘宝的算法，如果是非串1的过关方式，当组合的关数小于等于胆的时候
                        //则：只在胆里选关数
                    } else {
                        subSetBet = getSetBet().getDanSetBet();
                        list = processScheme(n, subSetBet.getBetMatrix());
                    }

                    //根据上面得到的组合信息，建立所有的场次组合
                    List<List<EntryBet>> combinations = buildCombination(list, subSetBet);
                    for (List<EntryBet> combination : combinations) {

                        EntryPass subEntryPass = new EntryPass(n, 1);
                        ArrayList<EntryPass> subEntryPassList = new ArrayList<EntryPass>();
                        subEntryPassList.add(subEntryPass);

                        SetBet splitedSetBet = new SetBet(combination);
                        SetPass subSetPass = new SetPass(subEntryPassList);

                        //TODO 这里已经拆成m串1的形式，注数直接等于复选的场次相乘就可以
                        //比如，拆后的单为5关，有2场投胜，1场投胜负，2场投胜负平，注数是2*3=6
                        UserBetScheme subUserBetScheme = new UserBetScheme(ruleBetScheme, rulePassScheme,
                                splitedSetBet, subSetPass);
                        int betTimes = subUserBetScheme.calculateSecene();

                        boolean single = true;
                        for (EntryBet entry : combination) {
                            if (entry.getBets().length > 1)
                                single = false;
                        }

                        BetVo betVo = new BetVo();
                        betVo.setPassWay(subEntryPass);
                        betVo.setBets(combination);
                        betVo.setBetTimes(betTimes);
                        betVo.setBetWay(single);
                        result.add(betVo);
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(getBetVoListString(result));
            log.debug("一共拆成 " + result.size() + "单");
        }
        return result;
    }

    private List<List<EntryBet>> buildCombination(List<Combination[]> list, SetBet subSetBet) {

        List<List<EntryBet>> result = new ArrayList<List<EntryBet>>();
        //对排列组合记录队列中每一条元素进行循环
        for (int i = 0; i < list.size(); i++) {

            List<List<EntryBet>> temp = null;
            List<List<EntryBet>> current;

            //对一条排列组合记录数组中的每一元素进行循环
            Combination[] tab = list.get(i);
            int[][] matrix = subSetBet.getBetMatrix();

            for (int j = 0; j < tab.length; j++) {
                current = new ArrayList<List<EntryBet>>();

                //把有胆的场从selectFrom里去掉
                List<EntryBet> selectFrom = new ArrayList<EntryBet>();
                selectFrom.addAll(subSetBet.counterMap.get(matrix[0][j]));
                List<EntryBet> danList = subSetBet.counterDanMap.get(matrix[0][j]);

                if (danList != null && !danList.isEmpty())
                    selectFrom.removeAll(danList);

                if (selectFrom.size() != tab[j].c) {
                    //LOG error
                    throw new IllegalArgumentException("竞彩拆单发生错误，请检查拆单逻辑");
                }

                //从剔除胆后的场次中进行组合
                if (tab[j].n > 0) {
                    current = tab[j].combination(selectFrom);
                }
                //在当前组合的每条记录中，加入有胆的场
                if (danList != null && !danList.isEmpty()) {
                    if (current == null || current.isEmpty()) {
                        current.add(danList);
                    } else {
                        for (List<EntryBet> dl : current) {
                            dl.addAll(danList);
                        }
                    }
                }

                //笛卡尔积
                if (temp != null) {
                    temp = cartesianProduct(temp, current);
                } else {
                    temp = current;
                }
            }
            result.addAll(temp);
        }
        return result;
    }

    //计算两个集合的笛卡尔积 TODO 应该有更高效的算法
    private List<List<EntryBet>> cartesianProduct(List<List<EntryBet>> l1, List<List<EntryBet>> l2) {

        boolean flag1 = l1 == null || l1.isEmpty();
        boolean flag2 = l2 == null || l2.isEmpty();

        if (flag1 && flag2)
            return null;
        else if (flag1)
            return l2;
        else if (flag2)
            return l1;

        List<List<EntryBet>> result = new ArrayList<List<EntryBet>>();
        for (List<EntryBet> e2 : l2) {
            for (List<EntryBet> e1 : l1) {
                List<EntryBet> same = new ArrayList<EntryBet>();
                same.addAll(e1);
                same.addAll(e2);
                result.add(same);
            }
        }
        return result;
    }

    private List<Combination[]> processScheme(int choice, int[][] matrix) {

        //		if(log.isDebugEnabled()){
        //			log.debug("关数：" + choice);
        //			log.debug("信息\n" + setBet.getMatrixString(matrix));
        //			log.debug("胆信息\n"+setBet.getMatrixString(setBet.getBetDanMatrix()));
        //		}

        int length = matrix[0].length;
        List<Combination[]> list = new ArrayList<Combination[]>();

        int partOne = matrix[1][0];
        int partTwo = matrix[3][0];
        int danPartOne = matrix[2][0];
        int danPartTwo = matrix[4][0];
        int selectFrom = matrix[1][0] - matrix[2][0];
        int choiceLeft = choice - (matrix[2][0] + matrix[4][0]);

        int loopFromPartOne = choiceLeft > (partTwo - danPartTwo) ? (choiceLeft - (partTwo - danPartTwo)) : 0;
        int loopToPartOne = choiceLeft
                - (choiceLeft > (partOne - danPartOne) ? (choiceLeft - (partOne - danPartOne)) : 0);

        for (int k = loopFromPartOne; k <= loopToPartOne; k++) {
            Combination[] c = new Combination[length];
            c[0] = new Combination(selectFrom, k, choiceLeft - k);
            list.add(c);
        }

        for (int i = 1; i < length; i++) {

            while (!list.isEmpty() && list.get(0)[i] == null) {
                Combination[] tab = list.remove(0);
                //减去上次循环还剩下一共多少场可以选

                partOne = matrix[1][i];
                partTwo = matrix[3][i];
                danPartOne = matrix[2][i];
                danPartTwo = matrix[4][i];

                selectFrom = partOne - danPartOne;
                //除掉胆以外还剩下多长场可以选 = 上次选剩下的 - 当前部分和下次部分的胆
                choiceLeft = tab[i - 1].remain;

                //如果下一部分有比目前剩下的场次还多的场次可以选，那目前场次中最少可以选：0场
                //如果下一部分可选的场次小于目前剩下的场次，那目前场次最少可以选： 剩下的场次 - 下部分场次和 - 当前部分的胆
                loopFromPartOne = choiceLeft > (partTwo - danPartTwo) ? (choiceLeft - (partTwo - danPartTwo)) : 0;
                //如果当前部分足够选择所有剩下的场次，那当前场次中最多可选：所有剩下的场数
                //如果当前部分场次小于剩下的场次，那当前场次中最多可选：当前部分除去胆以后的所有场数
                loopToPartOne = choiceLeft
                        - (choiceLeft > (partOne - danPartOne) ? (choiceLeft - (partOne - danPartOne)) : 0);

                for (int j = loopFromPartOne; j <= loopToPartOne; j++) {
                    Combination c = new Combination(selectFrom, j, choiceLeft - j);
                    Combination[] duplicate = tab.clone();
                    duplicate[i] = c;
                    list.add(duplicate);
                }
            }
        }
        return list;
    }

    private String getCListString(List<Combination[]> list) {
        String str = "";
        Iterator<Combination[]> it = list.iterator();
        while (it.hasNext()) {
            str += getcTabString(it.next()) + "\n";
        }
        return str;
    }

    private String getEntryBetListString(List<EntryBet> list) {
        String str = "";
        for (EntryBet ent : list) {
            str += ent.format() + CommonConstant.SPACE_SPLIT_STR + CommonConstant.SPACE_SPLIT_STR;
        }
        return str;
    }

    private String getEntryBetListListString(List<List<EntryBet>> list) {
        String str = "";
        for (List<EntryBet> l : list) {
            str += getEntryBetListString(l) + "\n";
        }
        return str;
    }

    private String getBetVoListString(List<BetVo> list) {

        int counter = 0;
        String str = "";
        for (BetVo ent : list) {
            if (counter == 0) {
                str += "\n";
            }
            counter += ent.getBetTimes();
            str += ent.toString() + "\n";
        }
        str += "总计 " + counter + " 注";
        return str;
    }

    private String getcTabString(Combination[] c) {
        String str = "";
        for (Combination d : c)
            if (d != null)
                str += d.toString() + "  ";
        return str;
    }

    public void setSetBet(SetBet setBet) {
        this.setBet = setBet;
    }

    public SetBet getSetBet() {
        return setBet;
    }

    public static void main(String[] args) {

		/*
        List<String> listID = new ArrayList<String>();
		listID.add("20110519001");
		listID.add("20110519002");
		listID.add("20110519003");
		listID.add("20110519004");
		listID.add("20110519005");
		listID.add("20110519006");
		listID.add("20110519007");
		listID.add("20110519008");
		listID.add("20110519009");
		listID.add("20110519010");
		listID.add("20110519011");
		listID.add("20110519012");
		List<String> listBetType = new ArrayList<String>();
		listBetType.add("01");
		listBetType.add("02");
		listBetType.add("03");
		//		listBetType.add("04");
		//		listBetType.add("05");
		List<String> listDanType = new ArrayList<String>();
		listDanType.add("0");
		listDanType.add("1");


		int[][][] ruleTable = {
				//ex.3关可以有3_1,3_3,3_4;其中3_1可以由3_1组合,3_3可以由2_1组合,3_4可以由2_1和3_1组合
				{{0}},
				{{1},							{2}},
				{{1,3,4},						{3},{2},{2,3}},
				{{1,4,5,6,11},					{4},{3},{3,4},{2},{2,3,4}},
				{{1,5,6,10,16,20,26},			{5},{4},{4,5},{2},{3,4,5},{2,3},{2,3,4,5}},
				{{1,6,7,15,20,22,35,42,50,57},	{6},{5},{5,6},{2},{3},{4,5,6},{2,3},{3,4,5,6},{2,3,4},{2,3,4,5,6}},
				{{1,7,8,21,35,120},				{7},{6},{6,7},{5},{4},{2,3,4,5,6,7}},
				{{1,8,9,28,56,70,247},			{8},{7},{7,8},{6},{5},{4},{2,3,4,5,6,7,8}}
		};


		String input =
			"20110519001:01.02.03:0" +
			" 20110519002:01.02:0" +
			" 20110519003:01.02:0" +
			" 20110519004:01.02:0" +
			" 20110519005:01.02:0" +
			" 20110519006:01:1" +
			" 20110519007:01:1" +
			" 20110519008:01:0" +
			" 20110519009:01:0" +
			" 20110519010:01:0" +
			" 20110519011:01:0" +
			" 20110519012:01:0";

		String input1 =
			"20110519001:01.02.03:0" +
			" 20110519002:01.02:0" +
			" 20110519003:01.02:1" +
			" 20110519005:01.02:0" +
			" 20110519006:01:0";


		String input2 =
			"20110519001:01.02:0" +
			" 20110519002:01.02.03.04:0" +
			" 20110519003:01.02.03:0" +
			" 20110519005:01:0" +
			" 20110519007:01:1" +
			" 20110519008:01:0" +
			" 20110519009:01.02.03.04.05:0" +
			" 20110519010:01:0" +
			" 20110519006:01:0";

		String input3 =
			"20110519001:01:0" +
			" 20110519002:01:0" +
			" 20110519003:01:1" +
			" 20110519004:01:1" +
			" 20110519005:01:0" +
			" 20110519006:01.02:0" +
			" 20110519007:01.02:0";

		String input4 =
			"20110519001:01" +
			" 20110519002:01" +
			" 20110519003:01" +
			" 20110519004:01" +
			" 20110519005:01" +
			" 20110519006:01.02" +
			" 20110519007:01.02";



        RuleID ruleId = new RuleID(listID);
		RuleBet ruleBet = new RuleBet(listBetType, ".");
		RuleDan ruleDan = new RuleDan(listDanType);
		
		RuleBetScheme ruleBetScheme = new RuleBetScheme(ruleId, ruleBet, ruleDan, ":", " ");
		
		
		
		long start = System.currentTimeMillis();
		RulePassScheme sc = new RulePassScheme(ruleTable, "_", ",");
		UserBetScheme user = new UserBetScheme(ruleBetScheme, sc);
		
		
		
		
		MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
		System.out.println("0 总内存:" + bean.getHeapMemoryUsage().getMax());
		System.out.println("0 初始化后内存:" + bean.getHeapMemoryUsage().getUsed());
		
		///////////////////////////////
		user.parser(input, "4_11,5_16,5_26,6_22,6_57,6_42,3_1,4_1,5_1,6_1,7_1,8_1", true);
		
		System.out.println("开始计算注数");
		int times = user.calculateSecene();
		System.out.println("结束计算注数");
		long endTimes = System.currentTimeMillis();
		
		
		MemoryMXBean bean1 = ManagementFactory.getMemoryMXBean();
		System.out.println("1 总内存:" + bean1.getHeapMemoryUsage().getMax());
		System.out.println("1 算注数后内存:" + bean1.getHeapMemoryUsage().getUsed());
		
		System.out.println("未拆单，总计 " + times + "注");


		System.out.println("开始拆单");
		user.splitSecene();
		System.out.println("拆单结束");
		MemoryMXBean bean2 = ManagementFactory.getMemoryMXBean();
		System.out.println("2 总内存:" + bean2.getHeapMemoryUsage().getMax());
		System.out.println("2 拆单后内存:" + bean2.getHeapMemoryUsage().getUsed());

		long endTotal = System.currentTimeMillis();

		System.out.println("总计用时 : " 
				+ (endTotal - start) 
				+ ", 计算注数用时 ：" 
				+ (endTimes - start)
				+ ", 拆单用时 ："
				+ (endTotal - endTimes));
		
		
		ruleBetScheme.addID("20110519020");
		System.out.println(ruleBetScheme.containsID("20110519020"));

		user.parser(input, "6_1,5_1,3_1,4_1");
		*/

		/*
        RuleID ruleId = new RuleID(listID);
		RuleBet ruleBet = new RuleBet(listBetType, ".");
		RuleDan ruleDan = new RuleDan(listDanType);
		
		RuleBetScheme ruleBetScheme = new RuleBetScheme(ruleId, ruleBet, ruleDan, ":", " ");

		RulePassScheme sc = new RulePassScheme(ruleTable, "_", ",");
		UserBetScheme user = new UserBetScheme(ruleBetScheme, sc);
		
		//user.parser(input4, "7_120", false);
		//user.splitToAward();
		*/

    }

}
