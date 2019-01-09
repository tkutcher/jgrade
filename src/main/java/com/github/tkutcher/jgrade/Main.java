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
    private static final String HELP_OPT = "help";
    private static final String VERSION_OPT = "version";
    private static final String NO_OUTPUT_OPT = "no-output";
    private static final String OUTPUT_OPT = "o";
    private static final String DEST_ARG = "destination";
    private static final String FORMAT_OPT = "format";
    private static final String FORMAT_ARG = "output-format";
    private static final String PP_OPT = "pretty-print";
    private static final String JSON_VAL = "json";
    private static final String TXT_VAL = "txt";

    private static class Config {
        String outputFormat;
    }

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
        if (line == null) {
            return;
        }


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
        options.addOption(Option.builder("f").longOpt(FORMAT_OPT)
                .desc("specify output, one of \'json\' (default) or \'text\'")
                .hasArg(true)
                .argName(FORMAT_ARG)
                .build());
        options.addOption(Option.builder().longOpt(PP_OPT)
                .desc("pretty-print output (when format is json)")
                .build());
        options.addOption(Option.builder().longOpt(NO_OUTPUT_OPT)
                .desc("don't produce any output (if user overriding)")
                .build());
        options.addOption(Option.builder(OUTPUT_OPT)
                .desc("save output to another file (if not specified, prints to standard out)")
                .hasArg(true)
                .argName(DEST_ARG)
                .build());
        options.addOption(Option.builder("h").longOpt(HELP_OPT).build());
        options.addOption(Option.builder("v").longOpt(VERSION_OPT).build());

//        HelpFormatter formatter = new HelpFormatter();
//        formatter.setWidth(80);
//        formatter.printHelp("jgrade", options);

        try {
            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args, false);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
