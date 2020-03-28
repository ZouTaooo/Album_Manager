package com.example.album_manager.Util;

import java.text.*;
import static java.lang.System.*;

public class StringUtils extends Format {

    private static final long serialVersion = 1L;

    /*枚举，哪种对齐方式*/
    public enum Alignment{
        /*左对齐*/
        LEFT,
        /*居中对齐*/
        CENTER,
        /*右对齐*/
        RIGHT,
    }

    private Alignment aligment;//当前对齐
    private int maxPages;//当前最大长度

    /*构造方法，用来设置字符串的居中方式以及最大长度*/
    public StringUtils(int maxPages, Alignment alignment) {

        switch(alignment) {
            case LEFT:
            case CENTER:
            case RIGHT:
                this.aligment = alignment;//将传过来的对齐方式赋值给全局的alignment变量
                break;

            default:
                throw new IllegalArgumentException("对齐参数错误！");
        }

        if(maxPages < 0) {//长度为负数时会抛出异常
            throw new IllegalArgumentException("页数参数错误");
        }

        this.maxPages = maxPages;

    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        out.println("房东电话给一还是对方国家偶数个佛山东方宫is东方精工静极思动if个");//随便打的字，只是用来测试
        out.println();

        StringUtils align = new StringUtils(50, StringUtils.Alignment.CENTER);//调用构造方法，设置字符串对齐为居中对齐，最大长度为50

        out.println(align.format("- i -"));

        out.println(align.format(Integer.toString(10)));

    }

    @Override
    public StringBuffer format(Object input, StringBuffer where, FieldPosition ignore) {
        // TODO Auto-generated method stub
        String s = input.toString();
        String wanted = s.substring(0,Math.min(s.length(), maxPages));

        //得到右侧的空格
        switch(aligment) {
            case RIGHT:
                pad(where, maxPages - wanted.length());
                where.append(wanted);
                break;
            case CENTER:
                int toAdd = maxPages - wanted.length();
                pad(where, toAdd/2);
                where.append(wanted);
                pad(where, toAdd-toAdd/2);
                break;
            case LEFT:
                where.append(wanted);
                pad(where, maxPages-wanted.length());
                break;
        }
        return where;
    }

    private void pad(StringBuffer where, int howMany) {
        // TODO Auto-generated method stub
        for(int i = 0; i < howMany; i++) {
            where.append(' ');//添加空格
        }
    }

    String format(String s) {
        return format(s, new StringBuffer(), null).toString();
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {//用处不大
        // TODO Auto-generated method stub
        return source;
    }

}