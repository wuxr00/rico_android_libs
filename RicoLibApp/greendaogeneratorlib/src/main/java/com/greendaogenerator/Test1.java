package com.greendaogenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Created by Rico on 16/8/29.
 */
public class Test1 {

    private final int a = 1;
    public final int b = 2;
    final int e = 2;
    static final int f = 2;
    public int c = 2;
    private int d = 2;

    public static void main(String[] args) {
//        Class<Test1> clz = Test1.class;
//        Field[] fields = clz.getDeclaredFields();
//        for (Field f :
//                fields) {
//            System.out.println(f.getName() + " - " + Modifier.FINAL + "  - " + ( Modifier.FINAL & f.getModifiers()) + " - " + Modifier.toString(f.getModifiers()));
//        }

        Class clz1 = TestObj1.class;
        Class clz2 = TestObj1.Sub1.class;
        Class clz3 = TestObj1.Sub2.class;
        Class clz4 = TestObj1.Sub3.class;
        Class clz5 = TestObj1.Sub4.class;

        System.out.println("clz1: " + Arrays.toString(clz1.getDeclaredConstructors()));
        System.out.println("clz2: " + Arrays.toString(clz2.getDeclaredConstructors()));
        System.out.println("clz3: " + Arrays.toString(clz3.getDeclaredConstructors()));
        System.out.println("clz4: " + Arrays.toString(clz4.getDeclaredConstructors()));
        System.out.println("clz5: " + Arrays.toString(clz5.getDeclaredConstructors()));
        try {
            System.out.println(clz4.getDeclaredConstructor(clz1,String.class).toString());
            System.out.println(clz5.getDeclaredConstructor(String.class).toString());
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        }
    }
}
