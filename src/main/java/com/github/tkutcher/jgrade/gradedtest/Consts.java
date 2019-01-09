package com.github.tkutcher.jgrade.gradedtest;

public class Consts {

    public static class GradescopeJson {
        public static final String VISIBLE = "visible";
        public static final String HIDDEN = "hidden";
        public static final String AFTER_DUE_DATE = "after_due_date";
        public static final String AFTER_PUBLISHED = "after_published";

        public static final String EXECUTION_TIME = "execution_time";
        public static final String STDOUT_VISIBILITY = "stdout_visibility";
        public static final String LEADERBOARD = "leaderboard";
        public static final String TESTS = "tests";
        public static final String SCORE = "score";
        public static final String MAX_SCORE = "max_score";
        public static final String NAME = "name";
        public static final String NUMBER = "number";
        public static final String OUTPUT = "output";
        public static final String TAGS = "tags";
        public static final String EXTRA_DATA = "extra_data";
        public static final String VISIBILITY = "visibility";
    }

    // GradedTest annotation defaults
    public static final String DEFAULT_NAME = "Unnamed Test";
    public static final String DEFAULT_NUMBER = "";
    public static final double DEFAULT_POINTS = 1.0;
    public static final String DEFAULT_VISIBILITY = GradescopeJson.VISIBLE;
}
