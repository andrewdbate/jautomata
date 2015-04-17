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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * A transformation that computes the complement of an automaton.
 * 
 * This transformation computes the complement of an automaton: The automaton is first converted to a DFA (if it is not
 * already a DFA), and the set of final states becomes the entire set of states minus the original set of final states.
 * 
 * @author Arnaud Bailly
 * @author Andrew Bate
 */
public class Complement<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

	protected final Set<L> alphabet;

	public Complement(Set<L> alphabet) {
		this.alphabet = alphabet;
	}

	public Complement() {
		this.alphabet = null;
	}
	
    /*
     * (non-Javadoc)
     * 
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
    	Set<L> alph = alphabet != null ? alphabet : a.alphabet(); 
    	Automaton<L, Tr, T> complement = new SinkComplete<L, Tr, T>(alph).transform(new ToDFA<L, Tr, T>().transform(a));
    	Map<State, State> map = new HashMap<>();
    	Automaton<L, Tr, T> result = new Automaton<>();
    	for (State s : complement.states()) {
    		State newState = result.addState(s.isInitial(), !s.isTerminal());
    		map.put(s, newState);
    	}
    	for (Transition<L> t : complement.delta()) {
    		try {
				result.addTransition(new Transition<>(map.get(t.start()), t.label(), map.get(t.end())));
			} catch (NoSuchStateException e) {
				throw new Error(e);
			}
    	}
        return result;
    }

}