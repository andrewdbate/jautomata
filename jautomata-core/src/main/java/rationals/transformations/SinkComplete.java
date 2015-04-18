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

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

import java.util.Iterator;
import java.util.Set;

/**
 * Complete an Automaton by adding a sink state and needed transitions.
 * <p />
 * <ul>
 * <li>C = complete(A)</li>
 * <li>S(C) = S(A) U {sink}</li>
 * <li>S0(C) = S0(A)</li>
 * <li>T(C) = T(A)</li>
 * <li>D(C) = D(A) U { (s1,a,sink)) | not exists (s1,a,s2) in D(A) }</li>
 * </ul>
 * 
 * @version $Id: SinkComplete.java 6 2006-08-30 08:56:44Z oqube $
 */
public class SinkComplete<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

	private Set<L> alphabet;

	public SinkComplete(Set<L> alphabet) {
		this.alphabet = alphabet;
	}

	public SinkComplete() {
	}

	/*
	 *  (non-Javadoc)
	 * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
	 */
	public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
		Automaton<L, Tr, T> b = a.clone();
		Set<L> alph = (alphabet == null) ? b.alphabet() : alphabet;
		State hole = null;
		Set<State> states = b.getStateFactory().stateSet();
		states.addAll(b.states());
		Iterator<State> i = states.iterator();
		while (i.hasNext()) {
			State state = i.next();
			Iterator<L> j = alph.iterator();
			while (j.hasNext()) {
				L label = j.next();
				if (b.delta(state, label).isEmpty()) {
					if (hole == null)
						hole = b.addState(false, false);
					try {
						b.addTransition(new Transition<>(state, label, hole));
					} catch (NoSuchStateException e) {
						throw new Error(e);
					}
				}
			}
		}
		if (hole != null) {
			Iterator<L> j = alph.iterator();
			while (j.hasNext()) {
				try {
					b.addTransition(new Transition<>(hole, j.next(), hole));
				} catch (NoSuchStateException e) {
					throw new Error(e);
				}
			}
		}
		return b;
	}
}
