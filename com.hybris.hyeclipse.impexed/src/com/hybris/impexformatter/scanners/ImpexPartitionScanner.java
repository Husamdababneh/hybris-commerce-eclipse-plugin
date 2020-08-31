/*******************************************************************************
 * Copyright 2020 SAP
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.hybris.impexformatter.scanners;

//import java.util.ArrayList;
//import java.util.List;

//import org.eclipse.jface.text.rules.EndOfLineRule;
//import org.eclipse.jface.text.rules.IPredicateRule;
//import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
//import org.eclipse.jface.text.rules.SingleLineRule;
//import org.eclipse.jface.text.rules.Token;

public class ImpexPartitionScanner extends RuleBasedPartitionScanner {

	public final static String yIMPEX_DATA = "__y_impex_data";
	public final static String yIMPEX_COMMENT = "__y_impex_comment";
	public final static String yIMPEX_INSTRUCTION = "__y_impex_instruction";
	public final static String yIMPEX_HEADER = "__y_impex_header";
	
	public final static String[] yPARTITIONS = new String[] {yIMPEX_DATA, yIMPEX_COMMENT, yIMPEX_INSTRUCTION, yIMPEX_HEADER};
	
	public ImpexPartitionScanner() {
		
		/*super();
		
		IToken data = new Token(ImpexPartitionScanner.IMPEX_DATA);
		IToken comment = new Token(ImpexPartitionScanner.IMPEX_COMMENT);
		IToken instruction = new Token(ImpexPartitionScanner.IMPEX_INSTRUCTION);
		IToken header = new Token(ImpexPartitionScanner.IMPEX_HEADER);
		
		List<IPredicateRule> rules = new ArrayList<>();
		
		rules.add(new EndOfLineRule(";", data));
		rules.add(new EndOfLineRule("#", comment));
		rules.add(new EndOfLineRule("#%", instruction));
		rules.add(new SingleLineRule("INSERT", null, header, '\\', true, true));
		rules.add(new SingleLineRule("UPDATE", null, header, '\\', true, true));
		rules.add(new SingleLineRule("REMOVE", null, header, '\\', true, true));
		
		IPredicateRule[] result= new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);*/
	}

}
