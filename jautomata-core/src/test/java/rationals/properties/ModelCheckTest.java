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
package rationals.properties;

import java.util.Arrays;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import rationals.TransitionBuilder;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.transformations.Complement;

/**
 * @version $Id: ModelCheckTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class ModelCheckTest extends TestCase {

    public ModelCheckTest(String arg0) {
        super(arg0);
    }
    
    public void test() throws ConverterException {
        Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Expression<Transition<String>, TransitionBuilder<String>>().fromString("a(b+c)(ab)*");
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Expression<Transition<String>, TransitionBuilder<String>>().fromString("(a+b)*c");
        ModelCheck<String, Transition<String>, TransitionBuilder<String>> mc = new ModelCheck<>();
        assertFalse(mc.test(b,a));
        //System.err.println(mc.counterExamples());
    }
    
	public void testEquivalence() throws NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Automaton<>();
		State initialA = a.addState(true,false);
		State finalA = a.addState(false,true);
		a.addTransition(new Transition<>(initialA, "a", finalA));
		a.addTransition(new Transition<>(initialA, "b", finalA));
		a.addTransition(new Transition<>(initialA, "c", initialA));
		a.addTransition(new Transition<>(finalA,   "d", finalA));
		Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Automaton<>();
		State initialB = b.addState(true,false);
		State finalB = b.addState(false,true);
		b.addTransition(new Transition<>(initialB, "a", finalB));
		b.addTransition(new Transition<>(initialB, "b", finalB));
		b.addTransition(new Transition<>(finalB,   "d", finalB));
		ModelCheck<String, Transition<String>, TransitionBuilder<String>> mc = new ModelCheck<>();
		assertFalse(mc.test(a,b) && mc.test(b,a));
	}
	
	public void testComplement1() throws NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Automaton<>();
		State initialA = a.addState(true,false);
		State finalA = a.addState(false,true);
		a.addTransition(new Transition<>(initialA, "a", finalA));
		a.addTransition(new Transition<>(initialA, "b", finalA));
		a.addTransition(new Transition<>(initialA, "c", initialA));
		a.addTransition(new Transition<>(finalA,   "d", finalA));
		
		Automaton<String, Transition<String>, TransitionBuilder<String>> aComplement = new Complement<String, Transition<String>, TransitionBuilder<String>>().transform(a);
		
		assertTrue(aComplement.accept(Arrays.asList("d")));
		assertTrue(aComplement.accept(Arrays.asList("a", "b", "c")));
		assertTrue(aComplement.accept(Arrays.asList("a", "b", "d", "c")));
		assertTrue(aComplement.accept(Arrays.asList("a", "b", "d", "d", "c")));
	}
	
	public void testComplement2() throws NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Automaton<>();
		State initialB = b.addState(true,false);
		State finalB = b.addState(false,true);
		b.addTransition(new Transition<>(initialB, "a", finalB));
		b.addTransition(new Transition<>(initialB, "b", finalB));
		b.addTransition(new Transition<>(finalB,   "d", finalB));
		
		Automaton<String, Transition<String>, TransitionBuilder<String>> bComplement = new Complement<String, Transition<String>, TransitionBuilder<String>>().transform(b);
			
		assertTrue(bComplement.accept(Arrays.asList("d")));
		assertTrue(bComplement.accept(Arrays.asList("a", "b", "a")));
		assertTrue(bComplement.accept(Arrays.asList("a", "b", "d", "a")));
		assertTrue(bComplement.accept(Arrays.asList("a", "b", "d", "d", "b")));
	}
	
	// TODO: add tests for the product operator (called the Mix product here)
	
}
