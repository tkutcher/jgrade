package student.hello;

/** Greeting interface. */
public interface Greeting {

    /**
     * Say hello.
     * @return The greeting.
     */
    String greet();

    /**
     * Say hello to a specific person.
     * @param who The person to greet.
     * @return The string greeting them.
     */
    String greet(String who);

    /** Print greeting to stdout. */
    void printGreeting();
}
