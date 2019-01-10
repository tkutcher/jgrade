package edu.jhu.cs.jgrade;

public interface OutputObserver {
    void update(Grader grader);
    String getOutput();
}
