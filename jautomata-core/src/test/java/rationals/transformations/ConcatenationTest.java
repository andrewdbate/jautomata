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

public class ConcatenationTest extends TestCase {

    private Automaton<String, Transition<String>, TransitionBuilder<String>> a;
    private Automaton<String, Transition<String>, TransitionBuilder<String>> b;

    protected void setUp() throws Exception {
        super.setUp();
        a = new Automaton<>();
        State s1 = a.addState(true, false);
        State s2 = a.addState(false, false);
        State s3 = a.addState(false, true);
        a.addTransition(new Transition<>(s1, "a", s2));
        a.addTransition(new Transition<>(s2, "b", s3));
        b = new Automaton<>();
        s1 = b.addState(true, true);
        s2 = b.addState(false, false);
        s3 = b.addState(false, true);
        b.addTransition(new Transition<>(s1, "c", s1));
        b.addTransition(new Transition<>(s1, "a", s2));
        b.addTransition(new Transition<>(s2, "b", s3));
        
    }

    public ConcatenationTest(String arg0) {
        super(arg0);
    }

    public void test1() {
        Concatenation<String, Transition<String>, TransitionBuilder<String>> conc = new Concatenation<>();
        Automaton<String, Transition<String>, TransitionBuilder<String>> c = conc.transform(a,b);
        String[] word = new String[] { "a", "b", "c" , "c" , "a", "b" };
        String[] word1 = new String[] { "a", "b", "a", "b" };
        String[] word2 = new String[] { "a", "b", "c" , "a"};
        assertTrue(c.accept(Arrays.asList(word)));
        assertTrue(c.accept(Arrays.asList(word1)));
        assertTrue(!c.accept(Arrays.asList(word2)));
    }
}
