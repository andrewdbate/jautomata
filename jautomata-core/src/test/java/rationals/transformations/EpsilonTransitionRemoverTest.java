package rationals.transformations;

import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.State;
import rationals.Transition;
import rationals.TransitionBuilder;
import rationals.properties.ContainsEpsilon;

public class EpsilonTransitionRemoverTest extends TestCase {

    private Automaton<String, Transition<String>, TransitionBuilder<String>> automaton;

    protected void setUp() throws Exception {
        super.setUp();
        automaton = new Automaton<>();
        State s1 = automaton.addState(true, true);
        State s2 = automaton.addState(false, false);
        State s3 = automaton.addState(false, true);
        State e1 = automaton.addState(false, false);
        State e2 = automaton.addState(false, false);
        State e3 = automaton.addState(false, false);
        String epsilon = null;
        automaton.addTransition(new Transition<>(e1, "c", e1));
        automaton.addTransition(new Transition<>(s1, epsilon, e1));
        automaton.addTransition(new Transition<>(e1, epsilon, s1));
        automaton.addTransition(new Transition<>(s1, "a", s2));
        automaton.addTransition(new Transition<>(s2, epsilon, e2));
        automaton.addTransition(new Transition<>(e2, epsilon, e3));
        automaton.addTransition(new Transition<>(e3, "b", s3));
        automaton.addTransition(new Transition<>(s3, "a", s2));
        automaton.addTransition(new Transition<>(e2, "b", s1));
    }

    public EpsilonTransitionRemoverTest(String arg0) {
        super(arg0);
    }

    @Test
    public void test1() {
    	EpsilonTransitionRemover<String, Transition<String>, TransitionBuilder<String>> norm = new EpsilonTransitionRemover<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word = new String[] { "a", "b", "a", "b" };
        assertTrue(b.accept(Arrays.asList(word)));
    }

    @Test
    public void test2() {
    	EpsilonTransitionRemover<String, Transition<String>, TransitionBuilder<String>> norm = new EpsilonTransitionRemover<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word3 = new String[] { };
        assertTrue(b.accept(Arrays.asList(word3)));
    }
    
    @Test
    public void test3() {
    	EpsilonTransitionRemover<String, Transition<String>, TransitionBuilder<String>> norm = new EpsilonTransitionRemover<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word2 = new String[] { "c","c","a", "b", "a", "b", "a", "b" };
        assertTrue(b.accept(Arrays.asList(word2)));
    }

    @Test
    public void test4() {
    	EpsilonTransitionRemover<String, Transition<String>, TransitionBuilder<String>> norm = new EpsilonTransitionRemover<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word1 = new String[] { "a", "b", "a", "b", "a" };
        assertTrue(!b.accept(Arrays.asList(word1)));
    }

    @Test
    public void test5() {
    	EpsilonTransitionRemover<String, Transition<String>, TransitionBuilder<String>> norm = new EpsilonTransitionRemover<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word2 = new String[] { "c","c","c"};
        assertTrue(b.accept(Arrays.asList(word2)));
    }
}
