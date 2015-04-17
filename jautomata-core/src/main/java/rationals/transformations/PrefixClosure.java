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
package rationals.transformations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import rationals.Automaton;
import rationals.Builder;
import rationals.NoSuchStateException;
import rationals.State;
import rationals.Transition;

/**
 * Computes the prefix closure of given automaton.
 * <p>
 * The resulting automaton is simply the starting automaton with all states made
 * terminal.
 * <ul>
 * <li>C = Pref(A)</li>
 * <li>S(C) = S(A)</li>
 * <li>S0(C) = S0(A)</li>
 * <li>T(C) = S(A)</li>
 * <li>D(C) = D(A)</li>
 * </ul>
 * 
 * @version $Id: PrefixClosure.java 2 2006-08-24 14:41:48Z oqube $
 */
public class PrefixClosure<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

    /*
     * (non-Javadoc)
     * 
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
        Automaton<L, Tr, T> ret = new Automaton<>();
        Map<State, State> sm = new HashMap<>();
        for (Iterator<State> it = a.states().iterator(); it.hasNext();) {
            State st = it.next();
            State sr = ret.addState(st.isInitial(), true);
            sm.put(st, sr);
        }
        /* add all transitions */
        for (Iterator<Transition<L>> it = a.delta().iterator(); it.hasNext();) {
        	Transition<L> tr = it.next();
            try {
                ret.addTransition(new Transition<>(sm.get(tr.start()), tr.label(), sm.get(tr.end())));
            } catch (NoSuchStateException e) {
            	throw new Error(e);
            }
        }
        return ret;
    }

}

/*
 * $Log: PrefixClosure.java,v $ Revision 1.1 2004/11/15 12:39:14 bailly added
 * PrefixClosure transformation
 *  
 */