/*
 * (C) Copyright 2005 Arnaud Bailly (arnaud.oqube@gmail.com),
 *     Yves Roos (yroos@lifl.fr) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an AS IS BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rationals.transformations;

import java.util.Arrays;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.State;
import rationals.Transition;
import rationals.TransitionBuilder;
import rationals.properties.ContainsEpsilon;

public class NormalizerTest extends TestCase {

    private Automaton<String, Transition<String>, TransitionBuilder<String>> automaton;

    protected void setUp() throws Exception {
        super.setUp();
        automaton = new Automaton<>();
        State s1 = automaton.addState(true, true);
        State s2 = automaton.addState(false, false);
        State s3 = automaton.addState(false, true);
        automaton.addTransition(new Transition<>(s1, "c", s1));
        automaton.addTransition(new Transition<>(s1, "a", s2));
        automaton.addTransition(new Transition<>(s2, "b", s3));
        automaton.addTransition(new Transition<>(s3, "a", s2));
        automaton.addTransition(new Transition<>(s2, "b", s1));
    }

    public NormalizerTest(String arg0) {
        super(arg0);
    }

    public void test1() {
        Normalizer<String, Transition<String>, TransitionBuilder<String>> norm = new Normalizer<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word = new String[] { "a", "b", "a", "b" };
        assertTrue(b.accept(Arrays.asList(word)));
    }

    public void test2() {
        Normalizer<String, Transition<String>, TransitionBuilder<String>> norm = new Normalizer<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word3 = new String[] { };
        assertTrue(b.accept(Arrays.asList(word3)));
    }
    public void test3() {
        Normalizer<String, Transition<String>, TransitionBuilder<String>> norm = new Normalizer<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word2 = new String[] { "c","c","a", "b", "a", "b", "a", "b" };
        assertTrue(b.accept(Arrays.asList(word2)));
    }

    public void test4() {
        Normalizer<String, Transition<String>, TransitionBuilder<String>> norm = new Normalizer<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word1 = new String[] { "a", "b", "a", "b", "a" };
        assertTrue(!b.accept(Arrays.asList(word1)));
    }

    public void test5() {
        Normalizer<String, Transition<String>, TransitionBuilder<String>> norm = new Normalizer<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = norm.transform(automaton);
        assertTrue(new ContainsEpsilon<String, Transition<String>, TransitionBuilder<String>>().test(b));
        String[] word2 = new String[] { "c","c","c"};
        assertTrue(b.accept(Arrays.asList(word2)));
    }
}
