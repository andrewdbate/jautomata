/*
 * (C) Copyright 2004 Arnaud Bailly (arnaud.oqube@gmail.com),
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

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;
import rationals.TransitionBuilder;
import rationals.converters.ConverterException;

/**
 * Test class for equivalence between automata.
 * 
 * @author nono
 * @version $Id: EquivalentTest.java 2 2006-08-24 14:41:48Z oqube $
 */
public class EquivalentTest extends TestCase {

	/**
	 * Constructor for EquivalentTest.
	 * @param arg0
	 */
	public EquivalentTest(String arg0) {
		super(arg0);
	}

	/*
	 * Canonical example of NOT a bisimulation
	 */
	public void testBisim() throws ConverterException, NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Automaton<>();
		State a1 = a.addState(true,false);
		State a2 = a.addState(false,false);
		State a3 = a.addState(false,false);
		State a4 = a.addState(false,false);
		a.addTransition(new Transition<>(a1,"a",a2));
		a.addTransition(new Transition<>(a2,"b",a3));
		a.addTransition(new Transition<>(a2,"c",a4));
		Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Automaton<>();
		State[] sts = new State[5];
		for(int i = 0;i<5;i++)
			sts[i] = b.addState(i == 0,false);
		b.addTransition(new Transition<>(sts[0],"a",sts[1]));
		b.addTransition(new Transition<>(sts[0],"a",sts[2]));
		b.addTransition(new Transition<>(sts[1],"b",sts[3]));
		b.addTransition(new Transition<>(sts[2],"c",sts[4]));
		AreEquivalent<String, Transition<String>, TransitionBuilder<String>> eq = new AreEquivalent<>(new Bisimulation<String, Transition<String>, TransitionBuilder<String>>());
		assertTrue(!eq.test(a,b));
	}

	/*
	 * Canonical example of NOT a bisimulation
	 */
	public void testWeakBisim() throws ConverterException, NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Automaton<>();
		State a1 = a.addState(true,false);
		State a2 = a.addState(false,false);
		State a3 = a.addState(false,false);
		State a4 = a.addState(false,false);
		a.addTransition(new Transition<>(a1,"a",a2));
		a.addTransition(new Transition<String>(a2,null,a2));
		a.addTransition(new Transition<>(a2,"b",a3));
		a.addTransition(new Transition<>(a2,"c",a4));
		Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Automaton<>();
		State[] sts = new State[5];
		for(int i = 0;i<5;i++)
			sts[i] = b.addState(i == 0,false);
		b.addTransition(new Transition<>(sts[0],"a",sts[1]));
		b.addTransition(new Transition<String>(sts[1],null,sts[2]));
		b.addTransition(new Transition<>(sts[2],"b",sts[3]));
		b.addTransition(new Transition<>(sts[1],"c",sts[4]));
		AreEquivalent<String, Transition<String>, TransitionBuilder<String>> eq = new AreEquivalent<>(new WeakBisimulation<String, Transition<String>, TransitionBuilder<String>>());
		assertTrue(eq.test(a,b));
	}

	public void testEquivalence() throws NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Automaton<>();
		State initialA = a.addState(true,false);
		State finalA = a.addState(false,true);
		a.addTransition(new Transition<>(initialA,"a",finalA));
		a.addTransition(new Transition<>(initialA,"b",finalA));
		a.addTransition(new Transition<>(initialA,"c",initialA));
		a.addTransition(new Transition<>(finalA,"d",finalA));
		Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Automaton<>();
		State initialB = b.addState(true,false);
		State finalB = b.addState(false,true);
		b.addTransition(new Transition<>(initialB,"a",finalB));
		b.addTransition(new Transition<>(initialB,"b",finalB));
		b.addTransition(new Transition<>(finalB,"d",finalB));
		AreEquivalent<String, Transition<String>, TransitionBuilder<String>> eq = new AreEquivalent<>(new Bisimulation<String, Transition<String>, TransitionBuilder<String>>());
		assertTrue(!eq.test(a,b));
	}
	
	public void testBisimulation() throws NoSuchStateException {
		Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Automaton<>();
		State a0 = a.addState(true, false);
		State a1 = a.addState(false, true);
		State a2 = a.addState(false, false);
		State a3 = a.addState(false, false);
		State a4 = a.addState(false, false);
		a.addTransition(new Transition<>(a0, "Q",  a1));
		a.addTransition(new Transition<>(a0, "R1", a2));
		a.addTransition(new Transition<>(a2, "R2", a3));
		a.addTransition(new Transition<>(a3, "R3", a4));
		a.addTransition(new Transition<>(a3, "S",  a1));
		a.addTransition(new Transition<>(a4, "R4", a1));
		
		Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Automaton<>();
		State b0 = b.addState(true, false);
		State b1 = b.addState(false, false);
		State b2 = b.addState(false, false);
		State b3 = b.addState(false, false);
		State b4 = b.addState(false, true);
		b.addTransition(new Transition<>(b0, "Q",  b4));
		b.addTransition(new Transition<>(b0, "R1", b1));
		b.addTransition(new Transition<>(b1, "R2", b2));
		b.addTransition(new Transition<>(b2, "R3", b3));
		b.addTransition(new Transition<>(b2, "S",  b4));
		b.addTransition(new Transition<>(b3, "R4", b4));

		AreEquivalent<String, Transition<String>, TransitionBuilder<String>> eq = new AreEquivalent<>(new Bisimulation<String, Transition<String>, TransitionBuilder<String>>());
		assertTrue(eq.test(a,b));
				
	}

}

/* 
 * $Log: EquivalentTest.java,v $
 * Revision 1.3  2005/03/23 07:22:42  bailly
 * created transductions package
 * corrected EpsilonRemover
 * added some tests
 * removed DirectedGRaph Interface from Automaton
 *
 * Revision 1.2  2005/02/20 21:14:19  bailly
 * added API for computing equivalence relations on automata
 *
 * Revision 1.1  2004/09/21 11:50:28  bailly
 * added interface BinaryTest
 * added class for testing automaton equivalence (isomorphism of normalized automata)
 * added computation of RE from Automaton
 *
*/