package com.avast.android.butterknifezelezny.setting;

/**
 * Created by admin on 2016/12/18.
 */
public class StatementGenerator {

    public static String defaultSetFormat = "/**\n" +
            " * 设置 #{bare_field_comment}\n" +
            " * \n" +
            " * @param ${field.name} #{bare_field_comment}  \n" +
            " */ ";

}