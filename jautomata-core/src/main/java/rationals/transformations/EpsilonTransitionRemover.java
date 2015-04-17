/*
 * (C) Copyright 2002 Arnaud Bailly (arnaud.oqube@gmail.com),
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
import rationals.properties.ModelCheck;

import java.util.*;

/**
 * This class allows to remove epsilon transitions in an automaton. Epsilon
 * transition are transitions (q , l , q') where l is null.
 * 
 * @author Yves Roos
 * @author Andrew Bate
 */
public class EpsilonTransitionRemover<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements UnaryTransformation<L, Tr, T> {

	// TODO: add tests for this class
	
	protected final ModelCheck<L, Tr, T> m = new ModelCheck<>();
	
    /*
     * (non-Javadoc)
     * 
     * @see rationals.transformations.UnaryTransformation#transform(rationals.Automaton)
     */
    public Automaton<L, Tr, T> transform(Automaton<L, Tr, T> a) {
        Automaton<L, Tr, T> ret = new Automaton<>(); /* resulting automaton */
        Map<Set<State>, State> sm = new HashMap<>();
        Set<Set<State>> done = new HashSet<>();
        Queue<Set<State>> todo = new LinkedList<>(); /* set of states to explore */
        Set<State> cur = TransformationsToolBox.epsilonClosure(a.initials(), a);
        /* add cur as initial state of ret */
        State is = ret.addState(true, TransformationsToolBox.containsATerminalState(cur));
        Set<State> hv = new HashSet<>(cur);
        sm.put(hv,is);
        todo.add(hv);
        do {
            Set<State> s = todo.poll();
            State ns =  sm.get(s);
            if(ns == null) {
                ns = ret.addState(false,TransformationsToolBox.containsATerminalState(s));
                sm.put(s,ns);
            }
            /* set s as explored */
            done.add(s);
            /* look for all transitions in s */
            Map<L, Set<State>> trm = instructions(a.delta(s), a);
            Iterator<Map.Entry<L, Set<State>>> it = trm.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<L, Set<State>> e = it.next();
                L o = e.getKey();
                Set<State> ar = e.getValue();
                /* compute closure of arrival set */
                ar = TransformationsToolBox.epsilonClosure(ar, a);
                hv = new HashSet<>(ar);
                /* retrieve state in new automaton from hash value */
                State ne = sm.get(hv);
                if(ne == null) {
                    ne = ret.addState(false,TransformationsToolBox.containsATerminalState(ar));
                    sm.put(hv,ne);
                }
                try {
                    /* create transition */
                    ret.addTransition(new Transition<L>(ns,o,ne));
                } catch (NoSuchStateException e1) {
                }
                /* explore new state */
                if(!done.contains(hv))
                    todo.add(hv);
            }
        } while (!todo.isEmpty());
        return reduceFinalStates(ret);
    }

    private Map<L, Set<State>> instructions(Set<Transition<L>> s, Automaton<L, Tr, T> a) {
        Map<L, Set<State>> m = new HashMap<L, Set<State>>();
        Iterator<Transition<L>> it = s.iterator();
        while (it.hasNext()) {
            Transition<L> tr = it.next();
            L l = tr.label();
            if (l != null) {
                Set<State> st = m.get(l);
                if (st == null) {
                    st = a.getStateFactory().stateSet();
                    m.put(l,st);
                }
                /* add arrival state */
                st.add(tr.end());
            }
        }
        return m;
    }
    
    /**
     * The epsilon transition procedure implemented in transform() will introduce
     * multiple terminal states whenever a final state is reachable along different
     * paths through the NFA, even some final states do not have any outgoing
     * transitions. This method will try to reduce the number of final states by
     * merging final states wherever possible, by checking if two final states are 
     * equivalent by determining whether the regular languages starting from those
     * two states are the same.
     * 
     * @param a
     * @return
     */
    protected Automaton<L, Tr, T> reduceFinalStates(Automaton<L, Tr, T> a) {
    	// reduced is the possible smaller automaton to be constructed
    	Automaton<L, Tr, T> reduced = new Automaton<>();
    	// Map from each terminal state to the automaton encoding the language accessible from the state
    	Map<State, Automaton<L, Tr, T>> finalToAccessible = new HashMap<>();
    	for (State terminal : a.terminals()) {
    		Accessible<L, Tr, T> accessible = new Accessible<>(terminal);
    		Automaton<L, Tr, T> b = accessible.transform(a);
    		finalToAccessible.put(terminal, b);
    	}
    	// Map each state in the input automaton to an equivalent state in the resultant automaton
    	Map<State, State> canonicalStateMap = new HashMap<>();
    	// Put terminal states into a total order (in order to establish which terminal state in an equivalence class is the canonical member)
    	List<State> order = new ArrayList<>(finalToAccessible.keySet());
    	// Map from each terminal state in a to the smallest equivalent terminal state
    	for (int i = 0; i < order.size(); i++) {
    		State s1 = order.get(i);
    		Automaton<L, Tr, T> s1Accessible = finalToAccessible.get(s1);
    		for (int j = 0; j < i; j++) {
    			State s2 = order.get(j);
    			Automaton<L, Tr, T> s2Accessible = finalToAccessible.get(s2);
    			if (sameLanguage(s1Accessible, s2Accessible)) {
    				canonicalStateMap.put(s1, s2);
    			}
    		}
    		if (!canonicalStateMap.containsKey(s1)) {
    			canonicalStateMap.put(s1, s1);
    		}
    	}
    	// All other states will map to themselves (until the implementation is more sophisticated)
    	for (State s : a.states()) {
    		if (!s.isTerminal()) {
    			canonicalStateMap.put(s, s);
    		}
    	}
    	// Build the new automaton
    	Map<State, State> stateMap = new HashMap<>();
    	for (State s : a.states()) {
    		State equivalentState = canonicalStateMap.get(s);
    		if (!stateMap.containsKey(equivalentState)) {
    			// Avoid adding unreachable states to the automaton
       			State newS = reduced.addState(equivalentState.isInitial(), equivalentState.isTerminal());
       			stateMap.put(s, newS);	
    		}
    	}
    	for (Transition<L> t : a.delta()) {
    		try {
    			State equivalentStart = canonicalStateMap.get(t.start());
    			State equivalentEnd = canonicalStateMap.get(t.end());
				reduced.addTransition(new Transition<>(stateMap.get(equivalentStart), t.label(), stateMap.get(equivalentEnd)));
			} catch (NoSuchStateException e) {
				throw new Error(e);
			}
    	}
    	return reduced;
    }
    
    protected boolean sameLanguage(Automaton<L, Tr, T> a, Automaton<L, Tr, T> b) {
    	return m.test(a, b) && m.test(b, a); 
    }

}

