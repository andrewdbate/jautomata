/*______________________________________________________________________________
 * 
 * Copyright 2005 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 1 juin 2005
 *
 */
package rationals.ioautomata.testing;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;

import junit.framework.TestCase;
import rationals.State;
import rationals.ioautomata.IOAlphabetType;
import rationals.ioautomata.IOAutomaton;
import rationals.ioautomata.IOSynchronization;
import rationals.ioautomata.IOTransition;

/**
 * @author nono
 * @version $Id: AlphaBetaIOTesterTest.java 12 2007-06-01 07:03:41Z oqube $
 */
public class AlphaBetaIOTesterTest extends TestCase {

    private IOAutomaton impl;

    private IOAutomaton ndet;

    private IOSynchronization synch;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        impl = new IOAutomaton();
        State s1 = impl.addState(true, true);
        State s2 = impl.addState(false, false);
        impl.addTransition(new IOTransition(s1, Matchers.equalTo("a"), s2, IOAlphabetType.INPUT));
        impl
                .addTransition(new IOTransition(s2, "b", s1,
                        IOAlphabetType.OUTPUT));

        ndet = new IOAutomaton();
        s1 = ndet.addState(true, true);
        s2 = ndet.addState(false, false);
        ndet.addTransition(new IOTransition(s1, Matchers.equalTo("a"), s2, IOAlphabetType.INPUT));
        ndet
                .addTransition(new IOTransition(s2, "b", s1,
                        IOAlphabetType.OUTPUT));
        ndet
                .addTransition(new IOTransition(s2, "c", s1,
                        IOAlphabetType.OUTPUT));
        synch = new IOSynchronization();
    }

    private void todo() throws IOTestFailure {
        SynchIOAutomatonSMAdapter ior = new SynchIOAutomatonSMAdapter(impl);
        Thread th = new Thread(ior);
        th.setDaemon(true);
        th.start();
        AlphaBetaIOTester tester = new AlphaBetaIOTester();
        /* a basic evaluator that stops when 50 tests have been done */
        Evaluator ev = new Evaluator() {
            int count = 0;
            /*
             * (non-Javadoc)
             * 
             * @see rationals.ioautomata.testing.Evaluator#setTester(rationals.ioautomata.testing.AlphaBetaIOTester)
             */
            public void setTester(AlphaBetaIOTester tester) {
            }

            /*
             * (non-Javadoc)
             * 
             * @see rationals.ioautomata.testing.Evaluator#start()
             */
            public void start() {
            }

            /*
             * (non-Javadoc)
             * 
             * @see rationals.ioautomata.testing.Evaluator#eval(java.util.Set,
             *      java.util.List, java.util.Set)
             */
            public double eval(Set traces, List trace, Set st) {
                return 1;
            }

            /*
             * (non-Javadoc)
             * 
             * @see rationals.ioautomata.testing.Evaluator#isEnough(java.util.Set)
             */
            public boolean isEnough(Set traces) {
                return count >= 50;
            }

            public void step(Set state, Set next) {
                count++;
            }

            public void reset() {
                // TODO Auto-generated method stub
                
            }

            public double eval(Set traces, IOTransition tr, Set st) {
                return 0;
            }
        };
        tester.setEvaluator(ev);
        tester.test(ior, ndet);
        ior.stop();
        assertTrue(!ior.isError());
    }
}
