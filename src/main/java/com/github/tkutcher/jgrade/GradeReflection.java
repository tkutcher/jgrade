package com.github.tkutcher.jgrade;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

final class GradeReflection {

    // Borrowed mostly from phf
    static Class<?> load(String className) {
        Class<?> c = null;
        try {
            URL url = FileSystems.getDefault().getPath("").toUri().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{url});
            c = loader.loadClass(className);
        } catch (ClassNotFoundException | MalformedURLException e) {
            System.err.println(e);
        }

        return c;
    }

    private static class GradeMethods {
        private SortedMap<String, Method> beforeGradeMethods;
        private SortedMap<String, Method> doneGradeMethods;
        private SortedMap<String, Method> gradeMethods;

        GradeMethods() {
            beforeGradeMethods = new TreeMap<>();
            doneGradeMethods = new TreeMap<>();
            gradeMethods = new TreeMap<>();
        }

        List<Method> toMethodList() {
            List<Method> l = new ArrayList<>();
            l.addAll(beforeGradeMethods.values());
            l.addAll(gradeMethods.values());
            l.addAll(doneGradeMethods.values());
            return l;
        }
    }

    private static boolean hasGradeAnnotation(Method m) {
        int num = (m.isAnnotationPresent(BeforeGrade.class) ? 1 : 0) +
                (m.isAnnotationPresent(Grade.class) ? 1 : 0) +
                (m.isAnnotationPresent(DoneGrade.class) ? 1 : 0);

        if (num > 1) {
            System.err.printf("method %s has too many grade annotations, should only have 1", m.getName());
        }

        return num == 1;
    }

    private static void addIfValid(GradeMethods gradeMethods, Method m) {
        if (!hasGradeAnnotation(m) || !isValidSignature(m)) {
            return;
        }

        if (m.isAnnotationPresent(BeforeGrade.class)) {
            gradeMethods.beforeGradeMethods.put(m.getName(), m);
        } else if (m.isAnnotationPresent(Grade.class)) {
            gradeMethods.gradeMethods.put(m.getName(), m);
        } else if (m.isAnnotationPresent(DoneGrade.class)) {
            gradeMethods.doneGradeMethods.put(m.getName(), m);
        }
    }

    private static boolean isValidSignature(Method m) {
        if (m.getParameterCount() != 1) {
            System.err.printf("method %s should have exactly 1 parameter\n", m.getName());
        } else if (!m.getParameterTypes()[0].equals(Grader.class)) {
            System.err.printf("method %s parameter should be of type Grader\n", m.getName());
        } else if (m.getReturnType() != Void.TYPE) {
            System.err.printf("method %s must return void, not %s\n", m.getName(), m.getReturnType());
        } else if (Modifier.isStatic(m.getModifiers())) {
            System.err.printf("method %s must not be static\n", m.getName());
        } else if (!Modifier.isPublic(m.getModifiers())) {
            System.err.printf("method %s must be declared public\n", m.getName());
        } else {
            return true;
        }

        return false;
    }

    static List<Method> graderMethods(Class<?> c) {
        GradeMethods gradeMethods = new GradeMethods();
        for (Method m: c.getMethods()) {
            addIfValid(gradeMethods, m);
        }
        return gradeMethods.toMethodList();
    }
}
