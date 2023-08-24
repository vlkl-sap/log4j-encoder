package com.example.log4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Encoder {

    public static void main(String[] args) {
        Exception e1 = new RuntimeException("inner\n\tat Fantasy.java(Fantasy.java:42)");
        Exception e2 = new Exception("outer", e1);

        System.out.println(e2);
        System.out.println("-----");
        e2.printStackTrace();
        System.out.println(e2.getStackTrace().length);

        System.out.println("-----");
        encode(e2).printStackTrace();
    }


    public static Throwable encode(Throwable e) {
        if (e==null) return null;
        try {
            Class<?> clazz = e.getClass();
            Constructor<?> constructor = clazz.getConstructor(String.class, Throwable.class);
            Throwable t = (Throwable) constructor.newInstance(
                new Object[] { encode(e.getMessage()), encode(e.getCause())});
            t.setStackTrace(e.getStackTrace());
            return t;
        } catch(InstantiationException |
                IllegalAccessException |
                NoSuchMethodException |
                InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }



    public static String encode(String s) {
        String enc;
        try {
            enc = java.net.URLEncoder.encode(s, "UTF-8");
        } catch(java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
        return enc;
    }

}
