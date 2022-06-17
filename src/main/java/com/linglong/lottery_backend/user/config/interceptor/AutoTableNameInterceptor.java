//package com.linglong.lottery_backend.user.config.interceptor;
//
//import net.sf.jsqlparser.parser.CCJSqlParserManager;
//import org.hibernate.EmptyInterceptor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * Description
// *
// * @author yixun.xing
// * @since 22 三月 2019
// */
////@Component
//public class AutoTableNameInterceptor extends EmptyInterceptor {
//
//    private static final Logger logger = LoggerFactory.getLogger(AutoTableNameInterceptor.class);
//
//    @Override
//    public String onPrepareStatement(String sql) {
//        sql = replaceTableName(sql);
//        return sql;
//    }
//
//    private static String replaceTableName(String sql) {
//        CCJSqlParserManager pm = new CCJSqlParserManager();
//        net.sf.jsqlparser.statement.Statement statement = null;
////        try {
////            statement = pm.parse(new StringReader(sql));
////            if (statement instanceof Insert) {
////                Insert insertStatement = (Insert) statement;
////                TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
////                List tableList = tablesNamesFinder.getTableList(insertStatement);
////                for (Iterator iter = tableList.iterator(); iter.hasNext(); ) {
////                    String tableName = (String) iter.next();
////                    switch (tableName) {
////                        case "tbl_transaction_record":
////                            sql = sql.replaceAll(tableName, tableName + "_" + getCurrentDate());
////                            break;
////
////                    }
////                }
////            }
////            else if (statement instanceof Select) {
////                Select selectStatement = (Select) statement;
////                TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
////                List tableList = tablesNamesFinder.getTableList(selectStatement);
////
////                String timeString = sql.replaceAll(" ", "");
////                if (timeString.indexOf("user_id=") != -1) {
////
////                    for (Iterator iter = tableList.iterator(); iter.hasNext(); ) {
////                        String tableName = (String) iter.next();
////                        switch (tableName) {
////                            case "tbl_transaction_record":
////                                sql =sql;
////                                break;
////
////                        }
////                    }
////
////                }else{
////                    for (Iterator iter = tableList.iterator(); iter.hasNext(); ) {
////                        String tableName = (String) iter.next();
////                        switch (tableName) {
////                            case "tbl_transaction_record":
////                                sql = sql.replaceAll(tableName, tableName + "_" + getCurrentDate());
////                                break;
////
////                        }
////                    }
////                }
////
////
////            }
////            else if (statement instanceof Update) {
////                Update updateStatement = (Update) statement;
////                TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
////                List tableList = tablesNamesFinder.getTableList(updateStatement);
////                for (Iterator iter = tableList.iterator(); iter.hasNext(); ) {
////                    String tableName = (String) iter.next();
////                    switch (tableName) {
////                        case "tbl_transaction_record":
////                            sql = sql.replaceAll(tableName, tableName + "_" + getTimeString(sql));
////                            break;
////                    }
////                }
////            }
////        } catch (JSQLParserException e) {
////            logger.error("表名称替换失败");
////            logger.error(e.getMessage());
////        }
//        return sql;
//    }
//
//    public static String getTimeString(String sql) {
//        String timeString = sql.replaceAll(" ", "");
//
//        if (timeString.indexOf("order_id=") != -1) {
//            timeString = timeString.substring(timeString.indexOf("order_id=") + 9, timeString.indexOf("order_id=") + 15);
//        } else if (timeString.indexOf("record_no=") != -1) {
//            timeString = timeString.substring(timeString.indexOf("record_no=") + 10, timeString.indexOf("record_no=") + 16);
//        } else {
//            timeString = getCurrentDate();
//        }
//
//        return timeString;
//    }
//
//    public static String getCurrentDate() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
//        Date date = new Date();
//        return dateFormat.format(date);
//    }
//
//}
