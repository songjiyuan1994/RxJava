package com.song.gofaraway.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by songjiyuan
 * on 2018/3/29 # 下午3:18.
 */

public class PatternUtils {

    private final static Pattern fileNamePattern = Pattern.compile("\\S*[?]\\S*");

    /**
     * 获取链接的后缀名
     *
     * @return
     */
    public static String parseSuffix(String url) {
        Matcher matcher = fileNamePattern.matcher(url);
        String[] spUrl = url.split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];
        if (matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            return spEndUrl[0].split("\\.")[1];
        }
        return endUrl.split("\\.")[1];
    }
}
