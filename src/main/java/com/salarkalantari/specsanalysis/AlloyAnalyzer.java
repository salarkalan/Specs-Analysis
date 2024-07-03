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

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;


public class AlloyAnalyzer implements SpecAnalyzer {
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
        

        Set<String> uniqueOperators = new HashSet<>();
        Set<String> uniqueOperands = new HashSet<>();
        int[] totalOperatorsCount = {0};
        int[] totalOperandsCount = {0};

        Set<String> specialOperators = new HashSet<>();
        Map<String, Integer> nameOccurrences = new HashMap<>();
        
        collectAlloyOperators(spec, uniqueOperators, totalOperatorsCount);
        collectAlloyOperands(spec, uniqueOperands, totalOperandsCount);
        
        collectSpecialOperatorNamesAndCountOccurrencesAlloy(spec, specialOperators, nameOccurrences);
        
        // adding special operators to the allOperators
        for (Map.Entry<String, Integer> entry : nameOccurrences.entrySet()) {
        	uniqueOperators.add(entry.getKey());
        	// adding special operators to the totalOperatorsCount
        	totalOperatorsCount[0] = totalOperatorsCount[0] + (entry.getValue() - 1) ;
        	
        	// deleting special operators from the totalOperandsCount
        	totalOperandsCount[0] = totalOperandsCount[0] - (entry.getValue() - 1) ;
        }
        
        int[] halstead = new int[]{ uniqueOperators.size(), uniqueOperands.size(),totalOperatorsCount[0],totalOperandsCount[0]};
        
        
        
        result.setLoc(getAlloyLOC(spec));
        result.setNumberOfComments(getAlloyComments(spec));
		result.setSyntaxCheck(checkAlloySyntax(filePath));
		result.setResultMessage(runAlloy(filePath));
        
        result.setOperators(uniqueOperators);
        result.setOperands(uniqueOperands);
        result.setHalstead(halstead);
		
		return result;
	}
	
	//Count the number of lines of code (LOC) in a Alloy specification.
    private static int getAlloyLOC(String spec) {
    	int locCount = 0;
        try (BufferedReader br = new BufferedReader(new StringReader(spec))) {
            String line;
            boolean inBlockComment = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
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
    
  //Count the number of comment lines in a Alloy specification.
    private static int getAlloyComments(String spec) {
        int commentCount = 0;
        try (BufferedReader br = new BufferedReader(new StringReader(spec))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.contains("--") || line.startsWith("//") || line.contains("//") ) {
                    commentCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return commentCount;
    }
    
    private static void collectAlloyOperators(String formula, Set<String> allOperators, int[] operatorCount) {
    	String[] lines = formula.split("\n");
        // Sorting operators by length in descending order to ensure longer operators are prioritized
        
        // "Int", "String" are considered constants and Operands
        List<String> operators = Arrays.asList("=>", "<=>", "++", "=<", "->", ">=", "||", "<:", ":>", "&&", "!=", "+", "-", "&", ".", "~", "*",
        		"^","!", "#",
        		"one", "lone", "none", "some", "abstract", "all", "iff", "but", "else", "extends", "set", "implies", 
        	    "module", "open", "and", "disj", "for", "in", "no", "or", "as", "sum", "exactly", 
        	    "iden", "let", "not", "univ", "enum", "var", "steps", "always", "historically", "eventually", "once", 
        	    "after", "before", "until", "since", "releases", "triggered", "check", "fact", "sig", "fun", "pred", 
        	    "assert", "run").stream()
            .sorted((a, b) -> Integer.compare(b.length(), a.length())) // Sort by length
            .map(Pattern::quote) // Quote to make them safe for regex
            .toList(); 

        // Creating a combined pattern
        String combinedPattern = String.join("|", operators);
        Pattern pattern = Pattern.compile(combinedPattern);

        for (String line : lines) {
            if (!line.trim().startsWith("--") && !line.trim().startsWith("//")) { // Ignore comment lines
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String matchedOperator = matcher.group();
                    allOperators.add(matchedOperator);
                    operatorCount[0]++; // Increment total count
                }
            }
        }
    }
    
    
    private static void collectAlloyOperands(String formula, Set<String> allOperands, int[] operandCount) {
    	String[] lines = formula.split("\n");
    	// Include hyphens within operand names.
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*+(?:_[a-zA-Z0-9]+)*");

        // Set of words (Operators) to be excluded
        Set<String> excludedWords = new HashSet<>(Arrays.asList("=>", "<=>", "++", "=<", "->", ">=", "||", "<:", ":>", "&&", "!=", "+", "-", "&", ".", "~", "*",
        		"^","!", "#",
        		"one", "lone", "none", "some", "abstract", "all", "iff", "but", "else", "extends", "set", "implies", 
        	    "module", "open", "and", "disj", "for", "in", "no", "or", "as", "sum", "exactly", 
        	    "iden", "let", "not", "univ", "enum", "var", "steps", "always", "historically", "eventually", "once", 
        	    "after", "before", "until", "since", "releases", "triggered", "check", "fact", "sig", "fun", "pred", 
        	    "assert", "run"));

        for (String line : lines) {
            if (!line.trim().startsWith("--") && !line.trim().startsWith("//")) { // Ignore comment lines
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
    
    
    private static void collectSpecialOperatorNamesAndCountOccurrencesAlloy(String formula, 
			Set<String> specialOperators, 
			Map<String, Integer> nameOccurrences) {
    	String[] lines = formula.split("\n");
    	Pattern pattern = Pattern.compile("\\b(fun|pred|assert)\\s+(\\w+)");

    	for (String line : lines) {
    		if (!line.trim().startsWith("--") && !line.trim().startsWith("//")) { // Ignore comment lines
    			Matcher matcher = pattern.matcher(line);
    			while (matcher.find()) {
    				String operator = matcher.group(1); // 
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
    
    public static String checkAlloySyntax(String alloyFilePath) {
        try {
            // Parse the Alloy model file
            CompUtil.parseEverything_fromFile(A4Reporter.NOP, null, alloyFilePath);
            return "Correct";
        } catch (Err e) {
            return "Error: " + e.toString();
        }
    }

    
    public String runAlloy(String alloyFilePath) {
    	
    	String syntaxResult = new String();

		try {
			String[] args = new String [] {alloyFilePath};
			
			A4Reporter rep = new A4Reporter() {};
			
			for (String filename : args) {
				
				Module world = CompUtil.parseEverything_fromFile(rep, null, filename);
				
				A4Options options = new A4Options();
	
				options.solver = A4Options.SatSolver.SAT4J;
	
				for (Command command : world.getAllCommands()) {
					
					// Execute the command
					A4Solution ans = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), command, options);

					if (ans.satisfiable()) {
						syntaxResult ="Instance found. Predicate is consistent. Use Alloy Analyzer to visualize the instances.";
					}	
					if (!ans.satisfiable()) {
						syntaxResult ="Unsatisfiable. No instance found. Predicate may be inconsistent.";
					}
				}
			}
			
		}catch (Err e) {
            // This block catches syntax errors or other issues in the Alloy model.
            syntaxResult = "runCode failed!";
		} catch (Exception e) {
		}
		
		return syntaxResult;
    			
    }


}
