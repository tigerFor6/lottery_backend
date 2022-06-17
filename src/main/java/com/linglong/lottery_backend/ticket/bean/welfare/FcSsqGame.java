package com.linglong.lottery_backend.ticket.bean.welfare;

import com.google.common.base.Joiner;
import com.linglong.lottery_backend.ticket.bean.AbstractWelfareGame;
import com.linglong.lottery_backend.ticket.constant.CommonConstant;
import com.linglong.lottery_backend.ticket.constant.WordConstant;
import com.linglong.lottery_backend.ticket.entity.Game;
import com.linglong.lottery_backend.ticket.entity.bo.GameBean;
import com.linglong.lottery_backend.ticket.enums.GameEnum;
import org.paukov.combinatorics3.Generator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class FcSsqGame extends AbstractWelfareGame {

    //public static final BigDecimal PRICE_PER_STAKE = new BigDecimal(2);// 单注价格

    private static final int REDBALLNUM = 6; // 每注红球的数量
    private static final int BLUEBALLNUM = 1; // 每注蓝球的数量


    private static final int MERCHANT_DANTUO_BLUEBALL_LIMITS = 12;

    @Override
    public Game getGame() {
        return GameEnum.FC_SSQ.getGame();
    }

    @Override
    public List<GameBean> splitGameBeanList(List<GameBean> userGameBeanList, BigDecimal betTimes) {

        userGameBeanList = splitGameBeanFormat(userGameBeanList);
        userGameBeanList = mergeGameBeanList(userGameBeanList);
        List<GameBean> gameBeanList = splitGameBeanListForMaxBetTimes(userGameBeanList, betTimes);
        gameBeanList = splitGameBeanListForAmount(gameBeanList);
        return gameBeanList;
        //return super.splitGameBeanList(userGameBeanList, betTimes);
    }

    /**
     * 单倍价格大于2w拆分
     * @param userGameBean
     * @return
     */
    @Override
    protected List<GameBean> splitGameBeanListForAmountByScheme(GameBean userGameBean) {
        List<GameBean> gameBeanList = new LinkedList<>();
        String[] lotteryNumber = userGameBean.getLotteryNumber().split(CommonConstant.PERCENT_SPLIT_STR);
        String allBall = lotteryNumber[1].split(CommonConstant.COMMON_AT_STR)[0];
        String[][] balls = getBalls(allBall);
        int redBallsCounts = getRedBallsCounts(balls);
        int maxRedBallCounts = getSplitMaxRedBalls(balls[0].length); // 在胆码一定的情况下，所允许的胆+拖最大个数
        if (redBallsCounts > maxRedBallCounts) {
            //红球胆+拖大于最大允许个数，拆分红球流程
            List<GameBean> tempRedSplitList = splitRedBalls(userGameBean, balls, maxRedBallCounts);
            for (GameBean gbRed : tempRedSplitList) {
                String[][] gbRedBalls = getBalls(gbRed.getLotteryNumber());
                boolean ifExceedDantuoBlueBallLimits = ifExceedDantuoBlueBallLimits(gbRedBalls);
                //对于每一个子方案，判断总金额是否大于2w，或者蓝球数目是否超限
                if ((gbRed.getPrice().multiply(gbRed.getBetTimes())).compareTo(PRICE_MAX_PER_TICKET) >= 0 || ifExceedDantuoBlueBallLimits) {
                    //子方案总金额大于2w
                    //判断单倍方案金额是否大于2w，或者蓝球数目是否超限
                    if (gbRed.getPrice().compareTo(PRICE_MAX_PER_TICKET) >= 0 || ifExceedDantuoBlueBallLimits) {
                        //子方案单倍金额大于2w，，或者蓝球数目超限，拆分蓝球流程
                        List<GameBean> tempBlueSplitList = splitBlueBalls(gbRed, getBalls(gbRed.getLotteryNumber()),
                                ifExceedDantuoBlueBallLimits);
                        for (GameBean gbBlue : tempBlueSplitList) {
                            //对于每一个子方案，判断总金额是否大于2w
                            if ((gbBlue.getPrice().multiply(gbBlue.getBetTimes())).compareTo(PRICE_MAX_PER_TICKET) >= 0) {
                                //子方案总金额大于2w，拆分倍数流程
                                gameBeanList.addAll(splitGameBeanListForAmountByTimes(gbBlue));
                            } else {
                                gameBeanList.add(gbBlue);
                            }
                        }
                    } else {
                        //拆分倍数流程
                        gameBeanList.addAll(splitGameBeanListForAmountByTimes(gbRed));
                    }
                } else {
                    gameBeanList.add(gbRed);
                }
            }
        }else {
            //拆分蓝球流程
            boolean ifExceedDantuoBlueBallLimits = ifExceedDantuoBlueBallLimits(balls);
            List<GameBean> tempBlueSplitList = splitBlueBalls(userGameBean, balls, ifExceedDantuoBlueBallLimits);
            for (GameBean gbBlue : tempBlueSplitList) {
                //对于每一个子方案，判断总金额是否大于2w
                if ((gbBlue.getPrice().multiply(gbBlue.getBetTimes())).compareTo(PRICE_MAX_PER_TICKET) >= 0) {
                    //子方案总金额大于2w，拆分倍数流程
                    gameBeanList.addAll(splitGameBeanListForAmountByTimes(gbBlue));
                } else {
                    gameBeanList.add(gbBlue);
                }
            }
        }
        gameBeanList.stream().forEach(e->e.setLotteryNumber(Joiner.on(CommonConstant.PERCENT_SPLIT_STR).join(lotteryNumber[0],e.getLotteryNumber())));
        return gameBeanList;
    }

    /**
     * 拆分蓝球流程
     * 拆分后的结果应能够使得每注号码金额不超过2w，而且蓝球数目不超限
     *
     * @param userGameBean
     * @param balls
     * @return
     */
    private List<GameBean> splitBlueBalls(GameBean userGameBean, String[][] balls, boolean
            ifExceedDantuoBlueBallLimits) {
        List<GameBean> gameBeanList = new ArrayList<>();
        String[] blueBall = balls[2];
        // 计算一个蓝球的金额
        int oneBlueBetNumber = combine(balls[1].length, REDBALLNUM - balls[0].length);//一个蓝球的注数
        BigDecimal oneBluePrice = PRICE_PER_STAKE.multiply(new BigDecimal(oneBlueBetNumber));//一个蓝球的金额
        //计算一注号码里可以容纳的蓝球数量
        int blueCounts = PRICE_MAX_PER_TICKET.divideToIntegralValue(oneBluePrice).intValue();
        // 如果蓝球个数超过合作商限制，则取合作商可允许的蓝球数量
        if (ifExceedDantuoBlueBallLimits) {
            if (blueCounts > MERCHANT_DANTUO_BLUEBALL_LIMITS) {
                blueCounts = MERCHANT_DANTUO_BLUEBALL_LIMITS;
            }
        }

        int index = 0;
        int length = balls[2].length;
        while (index < length) {
            int newBlueBallLength = Math.min(length - index, blueCounts);
            String[] newBlueBall = new String[newBlueBallLength];
            System.arraycopy(blueBall, index, newBlueBall, 0, newBlueBallLength);
            index += blueCounts;
            balls[2] = newBlueBall;
            gameBeanList.add(generateGameBean(userGameBean, balls));
        }

        return gameBeanList;
    }

    /**
     * 根据胆拖号码数组判断蓝球数目是否超过限制
     *
     * @param balls
     * @return
     */
    public boolean ifExceedDantuoBlueBallLimits(String[][] balls) {

       return false;
    }

    /**
     * 拆分红球
     * @param userGameBean
     * @param balls
     * @param maxRedBallCounts
     * @return
     */
    private List<GameBean> splitRedBalls(GameBean userGameBean, String[][] balls, int maxRedBallCounts) {
        List<GameBean> gameBeanList = new ArrayList<>();
        String[] x = balls[0]; // 原始红球胆码数组
        String[] y = balls[1]; // 原始红球拖码数组
        int xLength = x.length;
        int yLength = y.length;
        /**
         * 注释说明：
         * X：原始胆码数量
         * Y：原始拖码数量
         * M: 原始红球数量（M=X+Y）
         * P：可以允许的的最大胆+拖数量
         */
        // 将红色拖球拆成2组：第一组A中放入最大可以容纳的红色拖球数量(P-X)个，第二组B中则剩余(Y-(P-X))个
        int aLength = maxRedBallCounts - xLength; // A组红拖球的个数
        int bLength = yLength - aLength; // B组红拖球个数
        String[] a = new String[aLength]; // A组红球拖码数组
        String[] b = new String[bLength]; // B组红球拖码数组
        System.arraycopy(y, 0, a, 0, aLength);
        System.arraycopy(y, aLength, b, 0, bLength);

        //1、 以X个红球为胆，在A组中以（P-X）个红球为拖，组成一个胆拖，作为拆分红球的第一部分
        balls[1] = a;
        gameBeanList.add(generateGameBean(userGameBean, balls));

        //计算: min为b组红球数量和(5-X)中较小的值
        int min = bLength < (REDBALLNUM - xLength) ? bLength : (REDBALLNUM - xLength - 1);

        //2、设R=1,2,..,min,从B组的(Y-P+X)个红球中，依次分别取出R个球的所有组合情况，
        //以B组中选出的R个球和传入的X个原始胆组成(R+X)个球为新的胆，以A组中的(P-X)个球为拖，
        //组成一个胆拖，作为拆分红球的第二部分
        for (int i = 1; i <= min; i++) {
            List<String[]> tempRedDanList = combine(b, i);
            for (String[] tempRedDan : tempRedDanList) {
                String[] newRedDan = this.mergeArray(x, tempRedDan);
                balls[0] = newRedDan;
                balls[1] = a;
                /**
                 * 当这部分的胆+拖数目仍然超限时，应该继续拆分红球
                 */
                GameBean gb = generateGameBean(userGameBean, balls);
                if (ifExceedDantuoRedBallLimits(balls)) {
                    gameBeanList.addAll(splitRedBalls(gb, balls, maxRedBallCounts));
                } else {
                    gameBeanList.add(gb);
                }
            }
        }

        //3、判断bLength的长度
        if (bLength == REDBALLNUM - xLength) {
            //b组可以组成一个复式
            String[] newRedTuo = this.mergeArray(x, b);
            balls[0] = new String[]
                    {};
            balls[1] = newRedTuo;
            gameBeanList.add(generateGameBean(userGameBean, balls));
        } else if (bLength > REDBALLNUM - xLength) {
            if (bLength > maxRedBallCounts - xLength) {
                // b组大于16-x个球的时候
                //以x为胆球，b为新的托球重走拆分红球流程
                balls[0] = x;
                balls[1] = b;
                GameBean gb = generateGameBean(userGameBean, balls);
                gameBeanList.addAll(splitRedBalls(gb, balls, maxRedBallCounts));
            } else {
                //b组小于等于16-x个球的时候，b组自己可以组成一个复式/胆拖
                //以x为红胆球，b组为红托球
                balls[0] = x;
                balls[1] = b;
                gameBeanList.add(generateGameBean(userGameBean, balls));
            }
        }

        return gameBeanList;
    }

    /**
     * 从n个对象中选择m个的所有排列
     *
     * @param a
     * @param m
     * @return
     */
    public static List<String[]> combine(String[] a, int m) {
        List<String[]> result = new ArrayList<String[]>();
        int n = a.length;
        int[] bs = new int[n];
        if (m > n) {
            throw new RuntimeException("Can not get " + n + " elements from " + m + " elements!");
        } else if (m == n) {
            result.add(a);
            return result;
        }
        for (int i = 0; i < n; i++) {
            bs[i] = 0;
        }
        //初始化
        for (int i = 0; i < m; i++) {
            bs[i] = 1;
        }
        boolean flag = true;
        boolean tempFlag = false;
        int pos = 0;
        int sum = 0;
        //首先找到第一个10组合，然后变成01，同时将左边所有的1移动到数组的最左边
        do {
            sum = 0;
            pos = 0;
            tempFlag = true;
            result.add(getElement(bs, a, m));

            for (int i = 0; i < n - 1; i++) {
                if (bs[i] == 1 && bs[i + 1] == 0) {
                    bs[i] = 0;
                    bs[i + 1] = 1;
                    pos = i;
                    break;
                }
            }
            //将左边的1全部移动到数组的最左边

            for (int i = 0; i < pos; i++) {
                if (bs[i] == 1) {
                    sum++;
                }
            }
            for (int i = 0; i < pos; i++) {
                if (i < sum) {
                    bs[i] = 1;
                } else {
                    bs[i] = 0;
                }
            }

            //检查是否所有的1都移动到了最右边
            for (int i = n - m; i < n; i++) {
                if (bs[i] == 0) {
                    tempFlag = false;
                    break;
                }
            }
            if (tempFlag == false) {
                flag = true;
            } else {
                flag = false;
            }

        }
        while (flag);
        result.add(getElement(bs, a, m));

        return result;
    }

    private static String[] getElement(int[] bs, String[] a, int m) {
        String[] result = new String[m];
        int pos = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == 1) {
                result[pos] = a[i];
                pos++;
            }
        }
        return result;
    }
    /**
     * 根据胆拖号码数组判断红球胆拖数目是否超过限制
     *
     * @param balls
     * @return
     */
    public boolean ifExceedDantuoRedBallLimits(String[][] balls) {
        if (balls[0].length > 0) {// 有胆码的情况
            int redBallsCounts = getRedBallsCounts(balls);
            int maxRedBallCounts = getSplitMaxRedBalls(balls[0].length);
            return redBallsCounts > maxRedBallCounts;
        } else {// 无胆码，可能是复式或单式，不在本方法校验之列
            return false;
        }
    }

    /**
     * 计算红球（胆+拖）的总数
     *
     * @return
     */
    private int getRedBallsCounts(String[][] redBalls) {
        return redBalls[0].length + redBalls[1].length;
    }

    /**
     * 获取拆分红球时，组装成的新方案
     *
     * @param userGameBean
     * @param balls
     * @return
     */
    private GameBean generateGameBean(GameBean userGameBean, String[][] balls) {

        //拆分红球会更改 注数betNumber, 方式PlayType, 号码LotteryNumber, 价格price
        //int betNumber = factorialBetNums(balls[1].length,balls[2].length);
        int betNumber = combine(balls[1].length, REDBALLNUM - balls[0].length)
                * combine(balls[2].length, BLUEBALLNUM);

        GameBean gameBean = (GameBean) userGameBean.clone();
        gameBean.setLotteryNumber(generateLotteryNumber(balls));
        gameBean.setBetNumber(betNumber);
        gameBean.setPrice(getPrice(gameBean));

        if (betNumber == 1) {
            gameBean.setPlayType(WordConstant.SINGLE);
        } else if (balls[0].length > 0) {
            gameBean.setPlayType(WordConstant.DANTUO);
        } else {
            gameBean.setPlayType(WordConstant.MULTIPLE);
        }

        return gameBean;
    }

    /**
     * 计算排列组合的值
     *
     * @param total
     * @param select
     * @return
     */
    public static int combine(int total, int select) {
        if (select > total) {
            return 0;
        } else if (select == total) {
            return 1;
        } else if (total == 0) {
            return 1;
        } else {
            if (select > total / 2)
                select = total - select;

            long result = 1;
            for (int i = total; i > total - select; i--) {
                result *= i;
                if (result < 0)
                    return -1;
            }
            for (int j = select; j > 0; j--) {
                result /= j;
            }
            if (result > Integer.MAX_VALUE)
                return -1;
            return (int) result;
        }
    }

    /**
     * 根据红球胆拖码数组和蓝球数组，拼装出投注号码
     *
     * @param balls
     * @return
     */
    public String generateLotteryNumber(String[][] balls) {
        StringBuilder b = new StringBuilder();
        if (balls[0].length > 0) {
            //有胆球，胆拖
            b.append(Joiner.on(CommonConstant.COMMA_SPLIT_STR).join(balls[0]));
            b.append(CommonConstant.COMMON_AND_STR);
        }
        b.append(Joiner.on(CommonConstant.COMMA_SPLIT_STR).join(balls[1]));
        b.append(CommonConstant.COMMON_COLON_STR);
        b.append(Joiner.on(CommonConstant.COMMA_SPLIT_STR).join(balls[2]));
        return b.toString();
    }
    /**
     * 将a和b中的元素组合到一个数组中，并排序
     *
     * @param a
     * @param b
     * @return
     */
    public String[] mergeArray(String[] a, String[] b) {
        int aLength = a.length;
        int bLength = b.length;
        String[] c = new String[aLength + bLength];
        System.arraycopy(a, 0, c, 0, aLength);
        System.arraycopy(b, 0, c, aLength, bLength);
        Arrays.sort(c);
        return c;
    }


    /**
     * 复式，胆拖超出限制 拆分
     * @param gameBean
     * @return
     */
    @Override
    protected boolean ifExceedDantuoRedBallLimits(GameBean gameBean) {
        String[][] balls = getBalls(gameBean.getLotteryNumber());
        return ifExceedDantuoRedBallLimits(balls);
    }

//    /**
//     * 计算注数
//     * @param m
//     * @param n
//     * @return
//     */
//    private int factorialBetNums(int m,int n){
//        return BigIntegerMath.factorial(m).divide((BigIntegerMath.factorial(m-REDBALLNUM).multiply(BigIntegerMath.factorial(REDBALLNUM)))).multiply(BigInteger.valueOf(n)).intValue();
//    }


    /**
     * lotteryNumber拆分投注号码为三个数组，分别为红色胆球，红色拖球，蓝球
     *
     * @param lotteryNumber
     * @return
     */
    public String[][] getBalls(String lotteryNumber) {
        String[][] balls = new String[3][];//红色胆球、红色托球、蓝球
        String[] redDanBalls = null;//红色胆球
        String[] redTuoBalls = null;//红色托球

        String[] strBalls = lotteryNumber.split(CommonConstant.COMMON_COLON_STR);//第一个为红球，第二个为蓝球
        String[] redArr = strBalls[0].split(CommonConstant.COMMON_AND_STR);
        if (redArr.length==2){
            redDanBalls = redArr[0].split(CommonConstant.COMMA_SPLIT_STR);
            redTuoBalls = redArr[1].split(CommonConstant.COMMA_SPLIT_STR);
        }else {
            redDanBalls = new String[]{};//红色胆球
            redTuoBalls = redArr[0].split(CommonConstant.COMMA_SPLIT_STR);
        }
        balls[0] = redDanBalls;
        balls[1] = redTuoBalls;
        balls[2] = strBalls[1].split(CommonConstant.COMMA_SPLIT_STR);

        return balls;
    }

    /**
     * 根据胆码数目获取拆分出的A组红球最大个数（即胆+拖允许的最大个数）
     *
     * @param danLength 胆码数目
     * @return
     */
    private int getSplitMaxRedBalls(int danLength) {
        int max = 0;
        switch (danLength) {
            case 0:
                max = 16;
                break;
            case 1:
                max = 13;
                break;
            case 2:
                max = 14; // 根据合作商限制进行调整，胆+拖不超过24个，拖码不超过20个，西安红球拖码不能超过12个
                break;
            case 3:
                max = 15; // 根据合作商限制进行调整，胆+拖不超过24个，拖码不超过20个
                break;
            case 4:
                max = 16; // 根据合作商限制进行调整，胆+拖不超过24个，拖码不超过20个
                break;
            case 5:
                max = 17; // 根据合作商限制进行调整，胆+拖不超过24个，拖码不超过20个
                break;
            default:
                break;
        }
        return max;
    }

//    public static void main(String[] args) {
//        FcSsqGame ssqGame = new FcSsqGame();
//        List<GameBean> list = new LinkedList<>();
//        GameBean gameBean = new GameBean();
//        gameBean.setLotteryNumber("2019060%01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20:01@1");
//        gameBean.setBetTimes(BigDecimal.ONE);
//        gameBean.setBetNumber(BigDecimal.ONE.intValue());
//        gameBean.setPrice(BigDecimal.valueOf(38760*2));
//        list.add(gameBean);
//        ssqGame.splitGameBeanListForAmount(list);
//    }
//
//    /**
//     * 获取拆分红球时，组装成的新方案
//     *
//     * @param userGameBean
//     * @param balls
//     * @return
//     */
//    private static GameBean generateGameBean(GameBean userGameBean, String[][] balls) {
//
//        //拆分红球会更改 注数betNumber, 方式PlayType, 号码LotteryNumber, 价格price
//        int betNumber = CommonUtil.combine(balls[1].length, REDBALLNUM - balls[0].length)
//                * CommonUtil.combine(balls[2].length, BLUEBALLNUM);
//
//        GameBean gameBean = (GameBean) userGameBean.clone();
//        gameBean.setLotteryNumber(generateLotteryNumber(balls));
//        gameBean.setBetNumber(betNumber);
//        gameBean.setPrice(getPrice(gameBean));
//
//        if (betNumber == 1) {
//            gameBean.setPlayType(WordConstant.SINGLE);
//        } else if (balls[0].length > 0) {
//            gameBean.setPlayType(WordConstant.DANTUO);
//        } else {
//            gameBean.setPlayType(WordConstant.MULTIPLE);
//        }
//
//        return gameBean;
//    }

//    public static LinkedList<String[]> loop(String[] arr, Integer num) {
//        LinkedList<String[]> list = new LinkedList<>();
//        Generator.combination(arr)
//                .simple(num)
//                .stream()
//                .forEach(data ->{
//                    list.add(data.toArray(new String[data.size()]));
//                });
//        return list;
//    }

    public static String[] loop(String[] arr, Integer num) {
        LinkedList<String> list = new LinkedList<>();
        Generator.combination(arr)
                .simple(num)
                .stream()
                .forEach(data ->{
                    //System.out.println(data);
                    list.add(Joiner.on(CommonConstant.COMMA_SPLIT_STR).join(data));
                });
        //System.out.println(JSONArray.toJSONString(list));
        return list.toArray(new String[list.size()]);
    }
    public static List<String> loop(String[] redArr, String[] blueArr){
        List<String> result = new ArrayList<>();
        Generator.cartesianProduct(Arrays.asList(redArr), Arrays.asList(blueArr))
                .stream()
                .forEach(data-> result.add(Joiner.on(CommonConstant.COMMON_COLON_STR).join(data)));
        return result;
    }



//    public static void main(String[] args) {
//
//        //String[] allArr = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
//        String[] redArr = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"};
//        String[] blueArr = {"1"};
//        //int to =  loop(redArr,6).length;
//        //System.out.println("一共:"+ loop(redArr,6).length);
//        // System.out.println("m选n"+loop(redArr,7));
//        String [] danMa = {"17","18","19"};
//        //String [] b = {"1", "2", "3", "4", "6", "7"};
//        int x = 15 - danMa.length;
//        int array_length = redArr.length%x>0?redArr.length/x+1:redArr.length%x;
//
//        //int array_length = (int) Math.ceil(redArr.length / x);
//        //System.out.println(array_length);
//        String[][] result = new String[array_length][];
//
//        for (int i = 0; i < array_length;i++) {
//
//            int from =  (i * x);
//            int to = (from + x);
//
//            if (to > redArr.length)
//                to = redArr.length;
//
//            result[i] = Arrays.copyOfRange(redArr, from, to);
//        }
//        for (int i = 0; i < result.length; i++) {
//            if (result[i].length==x){
//                System.out.println(JSON.toJSONString(result[i]));
//            }else {
//                int min = result[i].length < (REDBALLNUM - danMa.length) ? result[i].length : (REDBALLNUM - danMa.length - 1);
//                for (int j = 1; j <= min; j++) {
//                    List<String[]> tempRedDanList = combine(result[i], j);
//                    System.out.println(JSON.toJSONString(tempRedDanList));
//                }
//
//
//            }
//        }
//        //System.out.println(JSON.toJSONString(result));
////        String[] s = Arrays.copyOf(redArr,x);
////        for (int i = 0; i < s.length; i++) {
////            System.out.println(s[i]);
////        }
//        //System.out.println(loop(allArr,6).length);
//        //System.out.println(loop(redArr,6).length);
//        //System.out.println(loop(redArr,5).length*4+loop(redArr,6).length);
//
////
////        Generator.subset(danMa)
////                .simple()
////                .stream()
////                .forEach(System.out::println);
//
//    }
}
