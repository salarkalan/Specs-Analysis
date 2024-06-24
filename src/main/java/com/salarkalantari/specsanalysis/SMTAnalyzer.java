package com.salarkalantari.specsanalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Z3Exception;

public class SMTAnalyzer implements SpecAnalyzer {
	@Override
    public SpecAnalysisResult analyze(String filePath) {
		SpecAnalysisResult result = new SpecAnalysisResult();
		
		// Get the spec formula from the filePath
        String spec=""; 
        try {
        	spec = FileUtil.readFile(filePath);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
		result.setLoc(getSMTLOC(spec));
        result.setNumberOfComments(getSMTComments(spec));
        result.setSyntaxCheck(checkSyntaxSMT(spec));
        result.setResultMessage(runCodeSMT(spec));
        

        Set<String> uniqueOperators = new HashSet<>();
        Set<String> uniqueOperands = new HashSet<>();
        int[] totalOperatorsCount = {0};
        int[] totalOperandsCount = {0};

        Set<String> specialOperators = new HashSet<>();
        Map<String, Integer> nameOccurrences = new HashMap<>();
        
        collectSMTOperators(spec, uniqueOperators, totalOperatorsCount);
        collectSMTOperands(spec, uniqueOperands, totalOperandsCount);
        
        collectSMTSpecialOperatorNamesAndCountOccurrences(spec, specialOperators, nameOccurrences);
        
        // adding special operators to the allOperators
        for (Map.Entry<String, Integer> entry : nameOccurrences.entrySet()) {
        	uniqueOperators.add(entry.getKey());
        	// adding special operators to the totalOperatorsCount
        	totalOperatorsCount[0] = totalOperatorsCount[0] + (entry.getValue() - 1) ;
        	
        	// deleting special operators from the totalOperandsCount
        	totalOperandsCount[0] = totalOperandsCount[0] - (entry.getValue() - 1) ;
        }
        
        int[] halstead = new int[]{ uniqueOperators.size(), uniqueOperands.size(),totalOperatorsCount[0],totalOperandsCount[0]};
        
        result.setOperators(uniqueOperators);
        result.setOperands(uniqueOperands);
        result.setHalstead(halstead);
        
		return result;
	}

	//Count the number of lines of code (LOC) in a SAT specification.
    private static int getSMTLOC(String spec) {
    	int locCount = 0;
        try (BufferedReader br = new BufferedReader(new StringReader(spec))) {
            String line;
            boolean inBlockComment = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    // Skip empty lines and single-line comments
                    continue;
                }
                if (inBlockComment) {
                    // Check if the block comment ends
                    if (line.endsWith("*/")) {
                        inBlockComment = false;
                    }
                    continue;
                }
                if (line.startsWith("/*")) {
                    // Start of a block comment
                    inBlockComment = true;
                    continue;
                }
                // Increment LOC count for valid code lines
                locCount++;
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return locCount;
    }
   
	
	//Count the number of comment lines in a SAT specification.
    private static int getSMTComments(String spec) {
        int commentCount = 0;
        try (BufferedReader br = new BufferedReader(new StringReader(spec))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(";") || line.contains(";")) {
                    commentCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return commentCount;
    }
    
    
    public String checkSyntaxSMT(String spec) {
		Context ctx = new Context();
        try {	
            // Parse the SMT-LIB input
            BoolExpr[] parsedFormula = ctx.parseSMTLIB2String(spec, null, null, null, null);

            // Checking satisfiability of the parsed formula
            Solver solver = ctx.mkSolver();
            solver.add(parsedFormula);
            return "Correct";
            
        } catch (Z3Exception e) {
            // Catch and print syntax errors
            return e.getMessage();
        } finally {
            ctx.close();
        } 	
    }
    
    
    public String runCodeSMT(String spec) {
    	
    	Context ctx = new Context();
        try {     	
            // Parse the SMT-LIB input
            BoolExpr[] parsedFormula = ctx.parseSMTLIB2String(spec, null, null, null, null);
            // Example: Checking satisfiability of the parsed formula
            Solver solver = ctx.mkSolver();
            solver.add(parsedFormula);
             
            return solver.check().toString() +"\n" + solver.getModel();

        } catch (Z3Exception e) {
            // Catch and print syntax errors
        	return "runCode failed!";
        } finally {
            ctx.close();
        } 
    }
    
    private static void collectSMTOperators(String formula, Set<String> allOperators, int[] operatorCount) {
        String[] lines = formula.split("\n");
        // Sorting operators by length in descending order to ensure longer operators are prioritized
        List<String> operators = Arrays.asList("=", ">", "<", "<=", ">=", "=>", "+", "-", "*", "/", "mod", "div", "rem",
            "^", "to_real", "and", "or", "not", "distinct", "to_int", "is_int", "~", "xor", "if", "ite", "root-obj", "sat", "unsat",
            "const", "map", "store", "select", "unsat", "bit1", "bit0", "bvneg", "bvadd", "bvsub", "bvmul", "bvsdiv", "bvudiv",
            "bvsrem", "bvurem", "bvsmod", "bvule", "bvsle", "bvuge", "bvsge", "bvult", "bvslt", "bvugt", "bvsgt", "bvand", "bvor",
            "bvnot", "bvxor", "bvnand", "bvnor", "bvxnor", "concat", "sign_extend", "zero_extend", "extract", "repeat", "bvredor",
            "bvredand", "bvcomp", "bvshl", "bvlshr", "bvashr", "rotate_left", "rotate_right", "get-assertions", 
            "define-fun", "declare-fun", "declare-sort", "define-sort", "declare-datatypes",       
            "define-const", "assert", "push", "pop", "check-sat", "declare-const", "get-model", "get-value", 
            "reset", "eval", "set-logic", "help", "get-assignment", "get-proof", "exit", "set-option", "get-option",
            "declare-var", "get-unsat-core", "echo", "let", "forall", "exists",  "check-sat-using","set-info","declare-map", "declare-rel",
             "apply", "simplify", "display", "as", "!", "get-info",  "rule",
            "query", "get-user-tactics").stream()
            .sorted((a, b) -> Integer.compare(b.length(), a.length())) // Sort by length
            .map(Pattern::quote) // Quote to make them safe for regex
            .toList(); 

        // Creating a combined pattern
        String combinedPattern = String.join("|", operators);
        Pattern pattern = Pattern.compile(combinedPattern);

        for (String line : lines) {
            if (!line.trim().startsWith(";")) { // Ignore comment lines
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String matchedOperator = matcher.group();
                    allOperators.add(matchedOperator);
                    operatorCount[0]++; // Increment total count
                }
            }
        }
    }
    
    
    private static void collectSMTOperands(String formula, Set<String> allOperands, int[] operandCount) {
        String[] lines = formula.split("\n");
        // Include hyphens within operand names.
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*");

        // Set of words (Operators) to be excluded
        Set<String> excludedWords = new HashSet<>(Arrays.asList("=", ">", "<", "<=", ">=", "=>", "+", "-", "*", "/",
        		
        		"mod", "div", "rem", 
        		"^", "to_real", "and", "or", "not", "distinct","to_int", "is_int", "~", "xor", "if", "ite", "root-obj","sat", "unsat",
        	    "const", "map", "store", "select", "unsat","bit1", "bit0", "bvneg", "bvadd", "bvsub", "bvmul", "bvsdiv", "bvudiv",
        	    "bvsrem", "bvurem", "bvsmod",  "bvule", "bvsle", "bvuge", "bvsge", "bvult","bvslt", "bvugt", "bvsgt", "bvand", "bvor",
        	    "bvnot", "bvxor", "bvnand","bvnor", "bvxnor", "concat", "sign_extend", "zero_extend", "extract","repeat", "bvredor",
        	    "bvredand", "bvcomp", "bvshl", "bvlshr", "bvashr","rotate_left", "rotate_right", "get-assertions", 
        	    
        	    "define-fun", "define-const", "assert", "push", "pop", "assert", "check-sat","declare-const", "declare-fun", "get-model",
        	    "get-value", "declare-sort","declare-datatypes", "reset", "eval", "set-logic", "help", "get-assignment", "get-proof",
        	    "exit", "get-unsat-core", "echo", "let", "forall", "exists", "define-sort", "set-option", "get-option","check-sat-using", 
        	    "set-info", "apply", "simplify", "display", "as", "!", "get-info", "declare-map", "declare-rel", "declare-var", "rule",
        	    "query", "get-user-tactics"));

        for (String line : lines) {
            if (!line.trim().startsWith(";")) { // Ignore comment lines
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String operand = matcher.group();
                    // Check if the operand is not in the excluded set
                    if (!excludedWords.contains(operand)) {
                        allOperands.add(operand); // Add to global unique operands set for tracking unique operands.
                        operandCount[0]++; // Increment total operands count for each operand found, including duplicates.
                    }
                }
            }
        }
    }
  
    
    private static void collectSMTSpecialOperatorNamesAndCountOccurrences(String formula, 
			Set<String> specialOperators, 
			Map<String, Integer> nameOccurrences) {
    	String[] lines = formula.split("\n");
    	Pattern pattern = Pattern.compile("\\b(declare-fun|define-fun|declare-sort|define-sort|declare-datatypes)\\s+(\\w+)");
    	for (String line : lines) {
    		if (!line.trim().startsWith(";")) { // Ignore comment lines
    			Matcher matcher = pattern.matcher(line);
    			while (matcher.find()) {
    				String operator = matcher.group(1); // "declare-fun" or "define-fun"
    				String name = matcher.group(2); // The name following the operator

    				// Add the combined operator and name to the set of special operators
    				specialOperators.add(operator + " " + name);

    				// Initialize or increment the count for this name in the occurrences map
    				nameOccurrences.put(name, nameOccurrences.getOrDefault(name, 0) + 1);
    			}
    		}
    	}
    	// After collecting names, count their occurrences throughout the formula
    	for (String name : nameOccurrences.keySet()) {
    		Pattern namePattern = Pattern.compile("\\b" + Pattern.quote(name) + "\\b");
    		for (String line : lines) {
    			Matcher nameMatcher = namePattern.matcher(line);
    			while (nameMatcher.find()) {
    				// Increment the count for each match found
    				nameOccurrences.put(name, nameOccurrences.get(name) + 1);
    			}
    		}
    		// Since the name was already counted once when initially found, subtract one to adjust
    		nameOccurrences.put(name, nameOccurrences.get(name) - 1);
    	}
    }
   

}
