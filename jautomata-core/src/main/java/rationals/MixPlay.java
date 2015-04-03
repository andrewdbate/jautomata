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
package rationals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This class implements an algorithm for finding a synchronizing
 * word given a target letter.
 * 
 * @author bailly
 * @version $Id: MixPlay.java 2 2006-08-24 14:41:48Z oqube $
 */
public class MixPlay<L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> implements AutomatonRunner {

    private static class MixException extends Exception {

		List<Object> word;

        List<StatesTuple> states;

        MixException(List<Object> w, List<StatesTuple> st) {
            this.word = w;
            this.states = st;
        }
    }

    private Set<StatesTuple> explored;

    private final static Random random = new Random();

    private int upperBound = 1;

    private L target;

    private List<Automaton<L, Tr, T>> autos;

    private Synchronization<L> sync;

    private Set<L> syncAlphabet;

    private Set<AutomatonRunListener> listeners = new HashSet<>();

    /*
     * current set of states
     */
    private StatesTuple current;

    /**
     * Construct a mix with the given list of automata.
     * 
     * @param autos a List of automaton objects
     */
    public MixPlay(List<Automaton<L, Tr, T>> autos) {
        this.autos = autos;
        this.sync = new DefaultSynchronization<>();
    }

    /**
     * Construct an empty mix.
     *
     */
    public MixPlay() {
        this.autos = new ArrayList<>();
        this.sync = new DefaultSynchronization<>();
    }

    /**
     * Adds a new automaton to this mix.
     * 
     * @param a
     */
    public void addAutomaton(Automaton a) {
        this.autos.add(a);
    }

    /**
     * Reset the state of this mix.
     * The current state is set to the start states of the 
     * mixed automata.
     */
    public void reset() {
        this.explored = new HashSet<>();
        this.target = null;
        @SuppressWarnings("unchecked")
		Set<State>[] states = new Set[autos.size()];
        int i = 0;
        Set<L> synalph = new HashSet<>(); // FIXME: why is this here and never used?
        List<Set<L>> alphl = new ArrayList<>();
        for (Iterator<Automaton<L, Tr, T>> it = autos.iterator(); it.hasNext();) {
            Automaton<L, Tr, T> a = it.next();
            upperBound *= a.states().size();
            states[i++] = a.initials();
            Set<L> alph = a.alphabet();
            alphl.add(alph);
        }
        /* make synalph */
        this.syncAlphabet = sync.synchronizing(alphl);
        this.current = new StatesTuple(states);
    }

    /**
     * Try to play for given target with given start states in each automaton.
     * 
     * @param target
     *            the targeted letter
     * @return a list of letters ending in <code>target</code>
     */
    public List<L> play(L target) {
        this.target = target;
        List<L> word = new ArrayList<>();
        List<StatesTuple> tuples = new ArrayList<>();
        /* initial states */
        try {
            doPlay(word, tuples, current);
        } catch (MixException mex) {
            /* notify listeners of synchronization */
            notify((List<L>) mex.word, mex.states);
            return (List<L>) mex.word; // FIXME: this unsafe cast should be removed
        }
        return new ArrayList<>();
    }

    /**
     * Notify each listener of the fired transitions when a word is found.
     * 
     * @param word
     * @param states
     */
    private void notify(List<L> word, List<StatesTuple> states) {
        if (listeners.isEmpty() || word.isEmpty() || states.isEmpty())
            return;
        Iterator<L> wit = word.iterator();
        Iterator<StatesTuple> sit = states.iterator();
        for (; sit.hasNext();) {
            StatesTuple tup = sit.next();
            L lt = wit.next();
            int ln = tup.sets.length;
            /* fire event */
            for (int i = 0; i < ln; i++) {
                Automaton<L, Tr, T> a = autos.get(i);
                Set<Transition<L>> trans = new HashSet<>();
                for (Iterator<State> stit = tup.sets[i].iterator(); stit.hasNext();)
                    trans.addAll(a.delta(stit.next(), lt));
                for (Iterator<AutomatonRunListener> lit = listeners.iterator(); lit.hasNext();)
                    (lit.next()).fire(a, trans, lt);
            }
        }
    }

    /**
     * Recursive play function
     * 
     * @param word current accumulated word
     * @param tuples current accumulated list of states tuples
     * @param states current states tuple
     */
    private void doPlay(List<L> word, List<StatesTuple> tuples, StatesTuple states) throws MixException {
        /* set current states*/
        System.err.println("in states "+ states);
        current = states;
        if (!word.isEmpty() && word.get(word.size() - 1).equals(target))
            throw new MixException((List<Object>) word, tuples); // FIXME: remove unsafe cast 
        /* stop exploring on loop */
        if (explored.contains(states))
            return;
        else
            explored.add(states);
        /* contains already tested transitions */
        Set<Transition<L>> s = new HashSet<>();
        /* list of transitions */
        for (int i = 0; i < states.sets.length; i++) {
            @SuppressWarnings("unchecked")
			Transition<L>[] trs = autos.get(i).delta(states.sets[i]).toArray(new Transition[0]); // TODO: fix this unchecked mess
            int ln = trs.length;
            int k = random.nextInt(ln);
            for (int j = 0; j < ln; j++) {
                Transition<L> tr = trs[(k + j) % ln];
                System.err.println("trying random transition "+ tr);
                if (s.contains(tr))
                    continue;
                s.add(tr);
                /* check synchronization */
                if (!checkSynchronizableWith(tr.label(), states))
                    continue;
                /* ok - try this transition */
                StatesTuple tup = advanceWith(tr.label(), states);
                /* recurse - an exception is thrown if a match is found */
                word.add(tr.label());
                tuples.add(states);
                System.err.println("Trying " + word);
                doPlay(word, tuples, tup);
                System.err.println("No way for " + word);
                word.remove(word.size() - 1);
                tuples.remove(tuples.size() - 1);
            }
        }
    }

    /**
     * Checks synchronization of automaton on this letter
     * 
     * @param object
     * @param states
     * @return
     */
    private boolean checkSynchronizableWith(L object, StatesTuple states) {
        if (!syncAlphabet.contains(object))
            return true;
        for (int i = 0; i < states.sets.length; i++) {
            Automaton<L, Tr, T> auto = autos.get(i);
            if (!sync.synchronizeWith(object, auto.alphabet()))
                continue;
            /*
             * compute synchronizing transitions
             */
            Set<Transition<L>> s = auto.delta(states.sets[i]);
            Set<State> adv = auto.getStateFactory().stateSet();
            for (Iterator<Transition<L>> j = s.iterator(); j.hasNext();) {
                Transition<L> tr = j.next();
                L lbl = tr.label();
                if (sync.synchronize(lbl, object) != null)
                    adv.add(tr.end());
            }
            if (adv.isEmpty())
                return false;
        }
        return true;
    }

    /**
     * @param object
     * @param states
     * @return
     */
    private StatesTuple advanceWith(L object, StatesTuple states) {
        @SuppressWarnings("unchecked")
		Set<State>[] nstates = new Set[autos.size()];
        for (int i = 0; i < states.sets.length; i++) {
            Automaton<L, Tr, T> auto = autos.get(i);
            /*
             * compute synchronizing transitions
             */
            Set<Transition<L>> s = auto.delta(states.sets[i]);
            Set<State> adv = auto.getStateFactory().stateSet();
            for (Iterator<Transition<L>> j = s.iterator(); j.hasNext();) {
                Transition<L> tr = j.next();
                L lbl = tr.label();
                if (sync.synchronize(lbl, object) != null)
                    adv.add(tr.end());
            }
            nstates[i] = adv.isEmpty() ? states.sets[i] : adv;
        }
        return new StatesTuple(nstates);
    }

    /*
     *  (non-Javadoc)
     * @see rationals.AutomatonRunner#addRunListener(rationals.AutomatonRunListener)
     */
    public void addRunListener(AutomatonRunListener l) {
        listeners.add(l);
    }

    /*
     *  (non-Javadoc)
     * @see rationals.AutomatonRunner#removeRunListener(rationals.AutomatonRunListener)
     */
    public void removeRunListener(AutomatonRunListener l) {
        listeners.remove(l);
    }

    /**
     * 
     * @return
     */
    public Synchronization<L> getSynchronization() {
        return sync;
    }

    /**
     * 
     * @param sync
     */
    public void setSynchronization(Synchronization<L> sync) {
        this.sync = sync;
    }
}
/*
 * Created on Apr 9, 2004
 * 
 * $Log: MixPlay.java,v $ Revision 1.7 2004/08/31 14:16:22 bailly *** empty log
 * message ***
 * 
 * Revision 1.6 2004/04/15 11:51:00 bailly added randomization of MixPlay TODO:
 * check accessibility of synchronization letter
 * 
 * Revision 1.5 2004/04/14 10:02:14 bailly *** empty log message ***
 * 
 * Revision 1.4 2004/04/14 07:33:43 bailly correct version of synchronization on
 * the fly
 * 
 * Revision 1.3 2004/04/13 07:08:38 bailly *** empty log message ***
 * 
 * Revision 1.2 2004/04/12 16:37:59 bailly worked on synchronization algorithm :
 * begins to work but there are still problems with proper implementation of
 * backtracking
 * 
 * Revision 1.1 2004/04/09 15:51:50 bailly Added algorithm for computing a mixed
 * word from several automata (to be verified)
 *  
 */
