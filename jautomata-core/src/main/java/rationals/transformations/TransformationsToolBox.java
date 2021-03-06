/*
 * (C) Copyright 2001 Arnaud Bailly (arnaud.oqube@gmail.com),
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
import rationals.State;
import rationals.Transition;

import java.util.*;

/**
 * A set of utility methods used in transformations of automaton.
 */
public class TransformationsToolBox {

  public static boolean containsATerminalState(Set<State> states) {
	  for (State s: states) if (s.isTerminal()) return true;
	  return false;
  } 

  public static boolean containsAnInitialState(Set<State> states) {
	  for (State s: states) if (s.isInitial()) return true;
	  return false;
  } 
  
  /**
   * Compute the set of states that are reachable in a given automaton
   * from a set of states using epsilon transitions.
   * An epsilon transition is a transition which is labelled <code>null</code>.
   * 
   * @param s the set of starting states 
   * @param a the automaton 
   * @return a - possibly empty - set of states reachable from <code>s</code> through
   * epsilon transitions. 
   */
  public static <L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> Set<State> epsilonClosure(Set<State> s, Automaton<L, Tr, T> a) {
      Set<State> exp = a.getStateFactory().stateSet();
      exp.addAll(s); /* set of states to visit */
      Set<State> view = a.getStateFactory().stateSet(); /* set of states visited */
      Set<State> arr = a.getStateFactory().stateSet(); /* the set of arrival states */
      arr.addAll(s);
      do {
          Set<State> ns = a.getStateFactory().stateSet();
          ns.addAll(exp); /* arrival states */
          Iterator<State> it = ns.iterator();
          while (it.hasNext()) {
              State st = it.next();
              Iterator<Transition<L>> it2 = a.delta(st).iterator();
              while (it2.hasNext()) {
                  Transition<L> tr = it2.next();
                  if (tr.label() == null && !view.contains(tr.end()) && !tr.end().equals(st)) {
                      /* compute closure of epsilon transitions */
                      exp.add(tr.end());
                      arr.add(tr.end());
                  }
              }
              exp.remove(st);
              view.add(st);
          }
      } while (!exp.isEmpty());
      return arr;
  }

  
  /**
   * Compute a map from letters to set of states given a set of transitions.
   * This method computes the arrival set of states for each letter occurring in
   * a given set of transitions. Epsilon transitions are not taken into account.
   *  
   * @param ts a Set of Transition objects.
   * @return a Map from L - transition labels - to Set of State objects. 
   */
  public static <L, Tr extends Transition<L>, T extends Builder<L, Tr, T>> Map<L, Set<State>> mapAlphabet(Set<Transition<L>> ts, Automaton<L, Tr, T> a) {
      Map<L, Set<State>> am = new HashMap<>();
      List<Transition<L>> tas = new ArrayList<>(ts);
      /* compute set of states for each letter */
      while (!tas.isEmpty()) {
          Transition<L> tr = tas.remove(0);
          L l = tr.label();
          if (l == null)
              continue;
          Set<State> as = am.get(l);
          if (as == null) {
              as = a.getStateFactory().stateSet();
              am.put(l, as);
          }
          as.add(tr.end());
      }
      return am;
  }

}
