package com.github.tkutcher.jgrade;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class Main {

    private static void grade(Class<?> c) {
        Object o = null;
        try {
            Constructor<?> con = c.getConstructor();
            o = con.newInstance();
        } catch (NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            System.err.printf("cannot instantiate %s%n", c);
            return;
        }

        Grader grader = new Grader();
        for (Method m : ReflectGrade.graderMethods(c)) {
            try {
                m.invoke(o, grader);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.err.printf("failed invoking method %s\n", m.getName());
                e.printStackTrace();
            }
        }

        System.out.print(grader.toJson(2));
    }

    private static void usage() {
        System.out.println("usage: jgrade [classes...]");
        System.exit(1);
    }


    public static void main(String[] args) {
        if (args.length < 1) {
            usage();
        }

        for (String className: args){
            Class<?> c = ReflectGrade.load(className);
            if (c == null) {
                System.err.printf("error loading class %s", className);
            } else {
                grade(c);
            }
        }
    }
}
