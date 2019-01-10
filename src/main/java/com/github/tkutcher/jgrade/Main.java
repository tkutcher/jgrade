package com.github.tkutcher.jgrade;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

public final class Main {

    private static final String VERSION = "1.0.0-SNAPSHOT";

    private static final int MAX_CLASSES = 10;
    private static final String CLASS_OPT = "classname";
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

    // Observers
    private static GradescopeJsonObserver jsonObserver;


    private static void fatal(String msg, Exception e) {
        System.err.println(msg);
        e.printStackTrace(System.err);
        System.exit(1);
    }

    private static void usage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(80);
        formatter.printHelp("jgrade", getOptions());
    }


    public static void main(String[] args) {
        String[] temp = new String[]{"-f", "json", "-c", "BasicGraderExample", "--pretty-print"};
        CommandLine line = readCommandLine(temp);
        if (line == null || line.hasOption(HELP_OPT)) {
            usage();
            System.exit(line == null ? 1 : 0);
        } else if (line.hasOption(VERSION_OPT)) {
            System.out.println(VERSION);
        } else {
            Grader grader = initGrader(line);
            Class<?> c = getClassToGrade(line.getOptionValue(CLASS_OPT));
            grade(grader, c);
            grader.notifyOutputObservers();
            outputResult(grader, line);
        }
    }

    private static Grader initGrader(CommandLine line) {
        jsonObserver = null;

        Grader grader = new Grader();
        if (line.hasOption(FORMAT_OPT)) {
            String val = line.getOptionValue(FORMAT_OPT);
            switch (val) {
                case JSON_VAL:
                    jsonObserver = new GradescopeJsonObserver(grader);
                    grader.attachOutputObserver(jsonObserver);
                    break;
                case TXT_VAL:
                    throw new UnsupportedOperationException("have not implemented textual output");
                default:
                    throw new IllegalArgumentException("unrecognized format value " + val);
            }
        }

        if (line.hasOption(PP_OPT)) {
            if (jsonObserver == null) {
                throw new IllegalArgumentException("pretty-print without json formatting");
            }
            jsonObserver.setPrettyPrint(2);
        }

        return grader;
    }

    private static void outputResult(Grader grader, CommandLine line) {
        if (line.hasOption(NO_OUTPUT_OPT)) {
            return;
        }


        PrintStream out = System.out;
        if (line.hasOption(OUTPUT_OPT)) {
            try {
                out = new PrintStream(line.getOptionValue(OUTPUT_OPT));
            } catch (FileNotFoundException e) {
                fatal("error printing output to file", e);
            }
        }

        if (jsonObserver != null) {
            out.println(jsonObserver.toString());
        }

    }

    private static void grade(Grader grader, Class<?> c) {
        Object o = instantiateClass(c);
        for (Method m : ReflectGrade.graderMethods(c)) {
            try {
                m.invoke(o, grader);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.err.printf("failed invoking method %s\n", m.getName());
                e.printStackTrace(System.err);
            }
        }
    }

    private static Object instantiateClass(Class<?> c) {
        try {
            return c.getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException |
                NoSuchMethodException | InvocationTargetException e) {
            fatal("could not invoke constructor of " + c.getName(), e);
            throw new InternalError("unreachable statement - system should have exited");
        }
    }

    private static Class<?> getClassToGrade(String className) {
        try {
            return ReflectGrade.load(className);
        } catch (MalformedURLException | ClassNotFoundException e) {
            fatal("could not locate class " + className, e);
            throw new InternalError("unreachable statement - system should have exited");
        }
    }

    private static Options getOptions() {
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
        options.addOption(Option.builder("c").longOpt(CLASS_OPT)
                .desc("the class containing annotated methods to grade")
                .hasArg()
                .required(true)
                .build());
        options.addOption(Option.builder("h").longOpt(HELP_OPT).build());
        options.addOption(Option.builder("v").longOpt(VERSION_OPT).build());
        return options;
    }

    private static CommandLine readCommandLine(String[] args) {
        try {
            CommandLineParser parser = new DefaultParser();
            return parser.parse(getOptions(), args, false);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
