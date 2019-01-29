package com.github.tkutche1.jgrade;

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


/**
 * Helper class used to consolidate all {@link java.lang.reflect} functionality
 * in grabbing annotated methods and making sure they have the proper
 * signature. Much of this code is borrowed from Peter Froehlich's Jaybee
 * which is open source at
 * <a href="https://github.com/phf/jb">https://github.com/phf/jb</a>.
 */
final class ReflectGrade {

    // Appease checkstyle
    private ReflectGrade() { }

    // Borrowed mostly from phf
    static Class<?> load(String className) throws ClassNotFoundException, MalformedURLException {
        URL url = FileSystems.getDefault().getPath("").toUri().toURL();
        URLClassLoader loader = new URLClassLoader(new URL[]{url});
        return loader.loadClass(className);
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
        int num = (m.isAnnotationPresent(BeforeGrading.class) ? 1 : 0)
                + (m.isAnnotationPresent(Grade.class) ? 1 : 0)
                + (m.isAnnotationPresent(AfterGrading.class) ? 1 : 0);

        if (num > 1) {
            System.err.printf("method %s has too many grade annotations, should only have 1", m.getName());
        }

        return num == 1;
    }

    private static void addIfValid(GradeMethods gradeMethods, Method m) {
        if (!hasGradeAnnotation(m) || !isValidSignature(m)) {
            return;
        }

        if (m.isAnnotationPresent(BeforeGrading.class)) {
            gradeMethods.beforeGradeMethods.put(m.getName(), m);
        } else if (m.isAnnotationPresent(Grade.class)) {
            gradeMethods.gradeMethods.put(m.getName(), m);
        } else if (m.isAnnotationPresent(AfterGrading.class)) {
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
