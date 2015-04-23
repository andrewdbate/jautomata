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
package rationals.converters.analyzers;

import rationals.Automaton;
import rationals.Builder;
import rationals.Transition;
import rationals.converters.ConverterException;
import rationals.transformations.Concatenation;
import rationals.transformations.Product;
import rationals.transformations.Reducer;
import rationals.transformations.Shuffle;
import rationals.transformations.Star;
import rationals.transformations.Union;
// Grammar :
// E -> T E'
// E' -> + T E' | '/' '{' L '}' | eps
// T -> S T"
// T" -> '|' S T" | '#' S T" | eps
// S -> F T'
// T' -> F T' | eps
// F -> B B'
// B' -> * | int | ^ | eps
// B -> letter | 1 | 0 | ( E )
public class Parser<Tr extends Transition<String>, T extends Builder<String, Tr, T>> {
    
  private Lexer<String> lexico ;
  
  /**
   * Parse given string using standard grammar and lexical analyzer.
   * 
   * @param expression the expression to parse
   * @see Lexer
   * @see DefaultLexer
   */
  public Parser(String expression) {
    lexico = new DefaultLexer(expression) ;
  }

  /**
   * Parse using the given lexer. 
   * 
   * @param lexer the lexer to use for parsing.
   */
  public Parser(Lexer<String> lexer) {
      this.lexico = lexer;
  }
  
  private Automaton<String, Tr, T> error(String message) throws ConverterException {
    throw new ConverterException("line " + lexico.lineNumber() + " , " + lexico.label() + " : " + message);
  }
  
  public Automaton<String, Tr, T> analyze() throws ConverterException {
    lexico.read() ;
    Automaton<String, Tr, T> r = E();
    if (lexico.current() != Lexer.END) error("end of expression expected") ;
    return r ; 
  }
  
  private Automaton<String, Tr, T> E() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : {
        Automaton<String, Tr, T> a = T();
        Automaton<String, Tr, T> b = EP();
        return new Reducer<String, Tr, T>().transform(new Union<String, Tr, T>().transform(a , b));
      }       
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION :
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("expression expected") ;
    }
  }

  private Automaton<String, Tr, T> EP() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : return error("union expected") ; 
      case Lexer.CLOSE :
      case Lexer.END : return new Automaton<String, Tr, T>() ;
      case Lexer.UNION : {
        lexico.read() ;
        Automaton<String, Tr, T> a = T() ;
        Automaton<String, Tr, T> b = EP() ;
        return new Reducer<String, Tr, T>().transform(new Union<String, Tr, T>().transform(a , b)) ;
      }
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("union expected") ; 
    }
  }

  private Automaton<String, Tr, T> T() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : {
        Automaton<String, Tr, T> a = S();
        Automaton<String, Tr, T> b = TS();
        return new Reducer<String, Tr, T>().transform(new Shuffle<String, Tr, T>().transform(a , b)) ;
      }       
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION :
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("expression expected") ;
    }
  }

  private Automaton<String, Tr, T> TS() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL :return error("concatenation expected") ;
      case Lexer.CLOSE :
      case Lexer.END : 
      case Lexer.UNION : return Automaton.epsilonAutomaton() ;
	  case Lexer.SHUFFLE : {
		lexico.read() ;
		Automaton<String, Tr, T> a = S() ;
		Automaton<String, Tr, T> b = TS() ;
		return new Reducer<String, Tr, T>().transform(new Shuffle<String, Tr, T>().transform(a , b)) ;
	  }
	  case Lexer.MIX : 
	  	{
		lexico.read() ;
		Automaton<String, Tr, T> a = S() ;
		Automaton<String, Tr, T> b = TS() ;
		return new Reducer<String, Tr, T>().transform(new Product<String, Tr, T>().transform(a , b)) ;
	  }
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("concatenation expected") ; 
    }
  }

  private Automaton<String, Tr, T> S() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : {
        Automaton<String, Tr, T> a = F() ;
        Automaton<String, Tr, T> b = TP() ;
        return new Reducer<String, Tr, T>().transform(new Concatenation<String, Tr, T>().transform(a , b)) ;
      }       
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION :
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("expression expected") ;
    }
  }

  private Automaton<String, Tr, T> TP() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL :{
        Automaton<String, Tr, T> a = F() ;
        Automaton<String, Tr, T> b = TP() ;
        return new Reducer<String, Tr, T>().transform(new Concatenation<String, Tr, T>().transform(a , b)) ;
      }
      case Lexer.CLOSE :
      case Lexer.END : 
      case Lexer.UNION : 
	  case Lexer.MIX : 
      case Lexer.SHUFFLE :return Automaton.epsilonAutomaton() ;
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("concatenation expected") ; 
    }
  }

  private Automaton<String, Tr, T> F() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.OPEN :
      case Lexer.LABEL : {
        Automaton<String, Tr, T> a = BP(B()) ; 
        return a ;
      }       
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION :
	  case Lexer.MIX : 
      case Lexer.SHUFFLE :
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("factor expected") ;
    }
  }

  private Automaton<String, Tr, T> B() throws ConverterException {
    switch(lexico.current()) {
      case Lexer.EPSILON : {
        Automaton<String, Tr, T> a = Automaton.epsilonAutomaton() ;
        lexico.read() ;
        return a ;
      }
      case Lexer.EMPTY : {
        Automaton<String, Tr, T> a = new Automaton<>() ;
        lexico.read() ;
        return a ;
      }
      case Lexer.OPEN : {
        lexico.read() ;
        Automaton<String, Tr, T> a = E() ;
        if (lexico.current() != Lexer.CLOSE) return error("( expected") ;
        lexico.read() ;
        return a ;
      }
      case Lexer.LABEL : {
        Automaton<String, Tr, T> a = Automaton.labelAutomaton(lexico.label()) ;
        lexico.read() ;
        return a ;
      }      
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.SHUFFLE :
	  case Lexer.MIX : 
      case Lexer.UNION :
      case Lexer.STAR :
      case Lexer.ITERATION :
      case Lexer.INT :
      default : return error("factor expected") ;
    }
  }

  private Automaton<String, Tr, T> BP(Automaton<String, Tr, T> a) throws ConverterException {
    switch(lexico.current()) {
      case Lexer.OPEN :
      case Lexer.LABEL :
      case Lexer.CLOSE :
      case Lexer.END :
      case Lexer.UNION : 
	  case Lexer.MIX : 
      case Lexer.SHUFFLE :return a ;
      case Lexer.STAR : {
        lexico.read() ; 
        return new Reducer<String, Tr, T>().transform(new Star<String, Tr, T>().transform(a)) ;
      }
      case Lexer.ITERATION :
        lexico.read() ; 
        return new Reducer<String, Tr, T>().transform(new Concatenation<String, Tr, T>().transform(a, new Star<String, Tr, T>().transform(a))) ;
      case Lexer.EPSILON :
      case Lexer.EMPTY :
      case Lexer.INT : {
        int value = lexico.value() ;
        lexico.read() ;
        Automaton<String, Tr, T> b = Automaton.epsilonAutomaton();
        for (int i = 0 ; i < value ; i++) {
          b = new Reducer<String, Tr, T>().transform(new Concatenation<String, Tr, T>().transform(b , a)) ;
        }
        return b ;
      }
      default : return error("Unexpected character") ;
    }
  }
}
