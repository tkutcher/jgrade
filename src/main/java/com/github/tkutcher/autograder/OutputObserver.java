package com.github.tkutcher.autograder;

public interface OutputObserver {
    void update();
    String getOutput();
}
