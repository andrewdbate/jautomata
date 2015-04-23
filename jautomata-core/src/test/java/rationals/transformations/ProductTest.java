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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.Transition;
import rationals.TransitionBuilder;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.converters.ToRExpression;
import rationals.properties.isEmpty;

public class ProductTest extends TestCase {

    public ProductTest(String arg0) {
        super(arg0);
    }

    public void testMix1() throws ConverterException {
        Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Expression<Transition<String>, TransitionBuilder<String>>().fromString("ab*cd");
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Pruner<String, Transition<String>, TransitionBuilder<String>>().transform(new Expression<Transition<String>, TransitionBuilder<String>>().fromString("a*ebc"));
        Automaton<String, Transition<String>, TransitionBuilder<String>> c = new Product<String, Transition<String>, TransitionBuilder<String>>().transform(a, b);
        String re = new ToRExpression<Transition<String>, TransitionBuilder<String>>().toString(c);
        System.out.println(re);
        assertEquals("aebcd", re);
    }

    public void testMix2() throws ConverterException {
        Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Pruner<String, Transition<String>, TransitionBuilder<String>>().transform(new Expression<Transition<String>, TransitionBuilder<String>>().fromString("a(bb)*e"));
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Pruner<String, Transition<String>, TransitionBuilder<String>>().transform(new Expression<Transition<String>, TransitionBuilder<String>>().fromString("a(bbb)*e"));
        Automaton<String, Transition<String>, TransitionBuilder<String>> c = new Reducer<String, Transition<String>, TransitionBuilder<String>>().transform(new Product<String, Transition<String>, TransitionBuilder<String>>().transform(a, b));
        System.out.println(new ToRExpression<Transition<String>, TransitionBuilder<String>>().toString(c));
        assertTrue("automata should accept word", c.accept(makeList("abbbbbbbbbbbbe")));
        assertTrue("automata should accept word", c.accept(makeList("ae")));
        assertTrue("automata should not accept word", !c.accept(makeList("abbe")));
    }

    private List<String> makeList(String string) {
      List<String> l = new ArrayList<>();
      for(int i = 0; i < string.length(); i++)
        l.add(string.charAt(i)+"");
      return l;
    }

    public void testMix4() throws ConverterException {
        Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Expression<Transition<String>, TransitionBuilder<String>>().fromString("a(b+c)(ab)*");
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Expression<Transition<String>, TransitionBuilder<String>>().fromString("(a+b)*c");
        Automaton<String, Transition<String>, TransitionBuilder<String>> c = new Reducer<String, Transition<String>, TransitionBuilder<String>>().transform(new Product<String, Transition<String>, TransitionBuilder<String>>().transform(a, b));
        String re = new ToRExpression<Transition<String>, TransitionBuilder<String>>().toString(c);
        System.out.println(re);
        assertEquals("ac", re);
    }

    public void testMixCommute() throws ConverterException {
        Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Expression<Transition<String>, TransitionBuilder<String>>().fromString("ab*cd");
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Pruner<String, Transition<String>, TransitionBuilder<String>>().transform(new Expression<Transition<String>, TransitionBuilder<String>>().fromString("a*ebc"));
        Automaton<String, Transition<String>, TransitionBuilder<String>> c = new Product<String, Transition<String>, TransitionBuilder<String>>().transform(a, b);
        Automaton<String, Transition<String>, TransitionBuilder<String>> d = new Product<String, Transition<String>, TransitionBuilder<String>>().transform(b, a);
        String rec = new ToRExpression<Transition<String>, TransitionBuilder<String>>().toString(c);
        System.err.println("a m b =" +rec);
        String red = new ToRExpression<Transition<String>, TransitionBuilder<String>>().toString(d);
        System.err.println("b m a =" +red);
        assertEquals(rec,red);
    }

    public void testMixEmpty() throws ConverterException {
        Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Expression<Transition<String>, TransitionBuilder<String>>().fromString("abc");
        Automaton<String, Transition<String>, TransitionBuilder<String>> b = new Expression<Transition<String>, TransitionBuilder<String>>().fromString("acb");
        Automaton<String, Transition<String>, TransitionBuilder<String>> c = new Product<String, Transition<String>, TransitionBuilder<String>>().transform(a, b);
        assertTrue(new isEmpty<String, Transition<String>, TransitionBuilder<String>>().test(c));
    }

}
