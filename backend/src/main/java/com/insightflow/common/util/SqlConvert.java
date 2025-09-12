package com.insightflow.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: sy
 * @CreateTime: 2025-06-11
 * @Description: sql参数转换
 * @Version: 1.0
 */
@Slf4j
public class SqlConvert {

    public static String convert(String sql, JSONObject jsonObject) {
        String regex = "#\\{([^}]+)\\}";
        Pattern pattern = Pattern.compile(regex);
        //逐行解析sql
        String[] lines = sql.split("\n");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            Matcher matcher = pattern.matcher(line);
            boolean found = false;
            while (matcher.find()) {
                String paramName = matcher.group(1);
                Object value = jsonObject.get(paramName);
                if (value != null) {
                    line = line.replace("#{" + paramName + "}",   "'" + value + "'");
                    builder.append(line).append(" \n ");
                }else{
                    //如果 builder最后一个非空字符串为where,则拼接“ 1=1 ”
                    if (builder.toString().trim().toLowerCase().endsWith("where")) {
                        builder.append(" 1=1 ").append(" \n ");
                    }
                }
                found = true;
            }
            if (!found) {
                builder.append(line).append(" \n ");
            }
        }

        sql = builder.toString().replaceAll("\n$", "");
        System.out.println("sql:" + sql);
        builder =  new StringBuilder();
        //解析${param}
        String regex2 = "\\$\\{([^}]+)\\}";
        Pattern pattern2 = Pattern.compile(regex2);
        //逐行解析sql
        String[] lines2 = sql.split("\n");
        for (int i = 0; i < lines2.length; i++) {
            String line = lines2[i];
            Matcher matcher2 = pattern2.matcher(line);
            boolean found = false;
            while (matcher2.find()) {
                String paramName = matcher2.group(1);
                Object value = jsonObject.get(paramName);
                if (value != null) {
                    line = line.replace("${" + paramName + "}", value.toString() );
                    builder.append(line).append(" \n ");
                }else{
                    //如果 builder最后一个非空字符串为where,则拼接“ 1=1 ”
                    if (builder.toString().trim().toLowerCase().endsWith("where")) {
                        builder.append(" 1=1 ").append(" \n ");
                    }
                }
                found = true;
            }
            if (!found) {
                builder.append(line).append(" \n ");
            }
        }

        return  builder.toString().replaceAll("\n$", "");
    }

    /**
     * 解析所有参数
     */
    public static List<String> parseParams(String sql) {
        String regex = "#\\{([^}]+)\\}";
        String regex2 = "\\$\\{([^}]+)\\}";
        Pattern pattern = Pattern.compile(regex);
        Pattern pattern2 = Pattern.compile(regex2);
        Matcher matcher = pattern.matcher(sql);
        Matcher matcher2 = pattern2.matcher(sql);
        List<String> paramNames = new ArrayList<>();
        //需要过滤重复参数
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (paramNames.contains(paramName)) {
                continue;
            }
            paramNames.add(paramName);
        }
        while (matcher2.find()) {
            String paramName = matcher2.group(1);
            if (paramNames.contains(paramName)) {
                continue;
            }
            paramNames.add(paramName);
        }
        return paramNames;

    }

    /**
     * 解析出sql的列名
     * @param sql
     */
    public static List<String> parseColumns(String sql) {
        List<String> columns = new ArrayList<>();
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select select) {
                SelectBody selectBody = select.getSelectBody();
                if (selectBody instanceof PlainSelect plainSelect) {
                    for (SelectItem item : plainSelect.getSelectItems()) {
                        String itemStr = item.toString().trim();
                        String columnName = itemStr;

                        // 使用正则提取 AS 后的别名（不区分大小写）
                        Pattern pattern = Pattern.compile("AS\\s+([\\w\"']+)\\s*", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(itemStr);
                        if (matcher.find()) {
                            columnName = matcher.group(1).trim();
                        }

                        columns.add(columnName);
                    }
                }
            }
        } catch (JSQLParserException e) {
            log.error("SQL解析失败，无法提取列名: ", e);
        }
        System.out.println("字段列表：" + columns);
        return columns;
}

    public static void main(String[] args) {
        String sql = "select id,user_name aS userName from employee_info \n " +
                "where \n " +
                "name like '%${name}%' \n " +
                "order by join_date desc";
        JSONObject params = new JSONObject();

        System.out.println("原始SQL:\n" + sql);
        String parsedSql = SqlConvert.convert(sql, params);
        List<String> list =  SqlConvert.parseColumns(sql);
        System.out.println("字段列表：" + list);

        System.out.println("解析后的SQL:\n" + parsedSql);
    }

}