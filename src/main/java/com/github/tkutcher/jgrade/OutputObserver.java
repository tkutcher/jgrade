package com.github.tkutcher.jgrade;

public interface OutputObserver {
    void update(Grader grader);
    String getOutput();
}
