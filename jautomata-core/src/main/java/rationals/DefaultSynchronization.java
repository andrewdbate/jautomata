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
package rationals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Default synchronization scheme for standard automaton. This class
 * synchronizes the labels of two transitions if they are equal as returned by
 * {@see java.lang.Object#equals(java.lang.Object)}.
 * 
 * @version $Id: DefaultSynchronization.java 2 2006-08-24 14:41:48Z oqube $
 */
public class DefaultSynchronization<L> implements Synchronization<L> {

    /*
     * (non-Javadoc)
     * 
     * @see rationals.Synchronization#synchronize(rationals.Transition,
     *      rationals.Transition)
     */
    public L synchronize(L t1, L t2) {
        return t1 == null ? null : (t1.equals(t2) ? t1 : null);
    }

    /* (non-Javadoc)
     * @see rationals.Synchronization#synchronizing(java.util.Set, java.util.Set)
     */
    public Set<L> synchronizable(Set<L> a, Set<L> b) {
        Set<L> r = new HashSet<>(a);
        r.retainAll(b);
        return r;
    }

    /*
     * TO VERIFY (non-Javadoc)
     * @see rationals.Synchronization#synchronizing(java.util.Collection)
     */
    public Set<L> synchronizing(Collection<Set<L>> alphabets) {
        Set<L> niou = new HashSet<>();
        /*
         * synchronization set is the union of pairwise 
         * intersection of the sets in alphl
         */
        for(Iterator<Set<L>> i = alphabets.iterator();i.hasNext();) {
            Set<L> s = i.next();
            for(Iterator<Set<L>> j = alphabets.iterator();j.hasNext();) {
                Set<L> b = j.next();
                niou.addAll(synchronizable(s,b));
            }
        }
        return niou;
    }

    public boolean synchronizeWith(L object, Set<L> alph) {
        return alph.contains(object);
    }

}