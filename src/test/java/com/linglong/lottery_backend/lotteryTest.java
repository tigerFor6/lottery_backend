package com.linglong.lottery_backend;

import org.paukov.combinatorics3.Generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 排列+笛卡尔
 */
public class lotteryTest {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>(Arrays.asList(new String[]{"7", "6", "4"}));
        AtomicInteger i = new AtomicInteger();
        Generator.combination("1", "2","4","6","7")
                .simple(3)
                .stream()
                .forEach(e->{
                    Set<String> result =  new HashSet(e);
                    if (set.containsAll(result)){
                        i.getAndIncrement();
                    }
                });
        System.out.println(i.get());
    }
//    String s ="";
//    public static void main(String[] args) {
//        List<Integer> list = new ArrayList<>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        List<List<Integer>> result = sort(list, 2);
//
//        String[] l1 = {"a", "b", "c"};
//        String[] l2 = {"d"};
//        String[] l3 = {"e", "f"};
//
//        int len = 0;
//        //排列比赛
//        for (int i = 0; i < result.size(); i++) {
//            List<List<String>> ll = new ArrayList<>();//比赛组合赔率
//            for (int j = 0; j < result.get(i).size(); j++) {
//                System.out.print("比赛:" + result.get(i).get(j) + "-");
//                if (result.get(i).get(j).equals(1)) {
//                    ll.add(Arrays.asList(l1));//比赛1的赔率
//                } else if (result.get(i).get(j).equals(2)) {
//                    ll.add(Arrays.asList(l2));//比赛2的赔率
//                } else if (result.get(i).get(j).equals(3)) {
//                    ll.add(Arrays.asList(l3));//比赛3的赔率
//                }
//            }
//            System.out.println();
//            List<List<String>> recursiveResult = new ArrayList<>();
//            //递归实现笛卡尔积  拆分组合
//            recursive(ll, recursiveResult, 0, new ArrayList<>());
//
//            System.out.println(JSON.toJSONString(recursiveResult));
//            len += recursiveResult.size();
//        }
//        System.out.println("一共" + len + "种组合方式");
//    }
//
//    private static List<List<Integer>> sort(List<Integer> elements, int num) {
//        List<List<Integer>> result = new ArrayList<>();
//        elements.forEach(data -> {
//            List<Integer> list = new ArrayList<>();
//            list.add(data);
//            result.add(list);
//        });
//        return combiner(elements, num, result);
//    }
//
//    private static List<List<Integer>> combiner(List<Integer> elements, int num, List<List<Integer>> result) {
//        if (num == 1) {
//            return result;
//        }
//        int len = result.size();
//        for (int i = 0; i < len; i++) {
//            for (int j = 0; j < elements.size(); j++) {
//                if (!result.get(i).contains(elements.get(j))) {
//                    List<Integer> list1 = new ArrayList<>();
//                    for (int k = 0; k < result.get(i).size(); k++) {
//                        list1.add(result.get(i).get(k));
//                    }
//                    list1.add(elements.get(j));
//                    Collections.sort(list1);
//                    result.add(list1);
//                }
//            }
//        }
//        for (int i = 0; i < len; i++) {
//            result.remove(0);
//        }
//        Iterator<List<Integer>> it = result.iterator();
//        List<List<Integer>> listTemp = new ArrayList<>();
//        while (it.hasNext()) {
//            List<Integer> a = it.next();
//            if (listTemp.contains(a)) {
//                it.remove();
//            } else {
//                listTemp.add(a);
//            }
//        }
//        combiner(elements, num - 1, result);
//        return result;
//    }
//
//    private static void recursive(List<List<String>> dimValue, List<List<String>> result, int layer, List<String> curList) {
//        if (layer < dimValue.size() - 1) {
//            if (dimValue.get(layer).size() == 0) {
//                recursive(dimValue, result, layer + 1, curList);
//            } else {
//                for (int i = 0; i < dimValue.get(layer).size(); i++) {
//                    List<String> list = new ArrayList<String>(curList);
//                    list.add(dimValue.get(layer).get(i));
//                    recursive(dimValue, result, layer + 1, list);
//                }
//            }
//        } else if (layer == dimValue.size() - 1) {
//            if (dimValue.get(layer).size() == 0) {
//                result.add(curList);
//            } else {
//                for (int i = 0; i < dimValue.get(layer).size(); i++) {
//                    List<String> list = new ArrayList<String>(curList);
//                    list.add(dimValue.get(layer).get(i));
//                    result.add(list);
//                }
//            }
//        }
//    }

}
