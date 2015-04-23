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

import junit.framework.TestCase;
import rationals.Automaton;
import rationals.Transition;
import rationals.TransitionBuilder;
import rationals.converters.ConverterException;
import rationals.converters.Expression;
import rationals.converters.ToRExpression;

public class ComplementTest extends TestCase {

    public ComplementTest(String arg0) {
        super(arg0);
    }
    
    public void testComp() throws ConverterException {
        Automaton<String, Transition<String>, TransitionBuilder<String>> a = new Pruner<String, Transition<String>, TransitionBuilder<String>>().transform(new Expression<Transition<String>, TransitionBuilder<String>>().fromString("a(bb)*e"));
        Automaton<String, Transition<String>, TransitionBuilder<String>> c = new ToDFA<String, Transition<String>, TransitionBuilder<String>>().transform(new Complement<String, Transition<String>, TransitionBuilder<String>>().transform(a));
        System.out.println(c);
        String re = new ToRExpression<Transition<String>, TransitionBuilder<String>>().toString(c);
        System.out.println(re);
    }

}
