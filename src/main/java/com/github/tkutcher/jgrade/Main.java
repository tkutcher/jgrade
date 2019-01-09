package com.github.tkutcher.jgrade;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
    }

    private static void usage() {
        System.out.println("usage: jgrade [classes...]");
        System.exit(1);
    }


    public static void main(String[] args) {
        String[] temp = new String[]{"-f", "json"};
        CommandLine line = readCommandLine(temp);
//        CommandLine line = readCommandLine(args);

//        for (String className: args){
//            Class<?> c = ReflectGrade.load(className);
//            if (c == null) {
//                System.err.printf("error loading class %s", className);
//            } else {
//                grade(c);
//            }
//        }
    }

    private static CommandLine readCommandLine(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("f").longOpt("format")
                .desc("specify output, one of \'json\' (default) or \'text\'")
                .hasArg(true)
                .argName("output-format")
                .build());
        options.addOption(Option.builder().longOpt("pretty-print")
                .desc("pretty-print json output")
                .build());
        options.addOption(Option.builder().longOpt("no-output")
                .desc("don't produce any output (if user overriding)")
                .build());
        options.addOption(Option.builder("o")
                .desc("save output to another file (if not specified, prints to standard out)")
                .hasArg(true)
                .argName("destination")
                .build());
        options.addOption(Option.builder("h").longOpt("help").build());
        options.addOption(Option.builder("v").longOpt("version").build());

        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(80);
        formatter.printHelp("jgrade", options);

        CommandLineParser parser = new DefaultParser();
        try {
            System.out.println("parsing...");
            CommandLine line  = parser.parse(options, args, false);
            line.getArgList();
            for (Option s : line.getOptions()) {
                s.getValue();
                System.out.print(s.getValue() +" ");
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }

        return null;
    }
}
