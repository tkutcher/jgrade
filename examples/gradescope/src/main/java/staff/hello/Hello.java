package staff.hello;

import student.hello.Greeting;

/** Say hello. */
public final class Hello implements Greeting {

    private String greeting;

    /**
     * Construct a new Hello object.
     * @param greeting The greeting to use to say hello.
     */
    public Hello(final String greeting) {
        this.greeting = greeting;
    }

    @Override
    public String greet() {
        return this.greeting;
    }

    @Override
    public String greet(final String who) {
        return String.format("%s, %s!", this.greeting, who);
    }

    @Override
    public void printGreeting() {
        System.out.println(greeting);
    }
}
