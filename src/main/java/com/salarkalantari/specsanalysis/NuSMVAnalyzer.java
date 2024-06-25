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

public class NuSMVAnalyzer implements SpecAnalyzer {
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
        
        
        result.setLoc(getNuSMVLOC(spec));
        result.setNumberOfComments(getNuSMVComments(spec));
        
        

        
        Set<String> uniqueOperators = new HashSet<>();
        Set<String> uniqueOperands = new HashSet<>();
        int[] totalOperatorsCount = {0};
        int[] totalOperandsCount = {0};
        
        collectNuSMVOperators(spec, uniqueOperators, totalOperatorsCount);
        collectNuSMVOperands(spec, uniqueOperands, totalOperandsCount);
        
        int[] halstead = new int[]{ uniqueOperators.size(), uniqueOperands.size(),totalOperatorsCount[0],totalOperandsCount[0]};
        
        result.setOperators(uniqueOperators);
        result.setOperands(uniqueOperands);
        result.setHalstead(halstead);
        
        
        return result;
	}
	
	
	
	
	//Count the number of lines of code (LOC) in a NuSMV specification.
    private static int getNuSMVLOC(String spec) {
    	int locCount = 0;
        try (BufferedReader br = new BufferedReader(new StringReader(spec))) {
            String line;
            boolean inBlockComment = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
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
   
	
	//Count the number of comment lines in a NuSMV specification.
    private static int getNuSMVComments(String spec) {
        int commentCount = 0;
        try (BufferedReader br = new BufferedReader(new StringReader(spec))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.contains("--")) {
                    commentCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return commentCount;
    }


    private static void collectNuSMVOperators(String formula, Set<String> allOperators, int[] operatorCount) {
    	String[] lines = formula.split("\n");

    	// Sorting operators by length in descending order to ensure longer operators are prioritized


    	// these are considered operators:  "boolean", "integer", "real", "TRUE", "FALSE"  //   "in" is excluded
    	// "Int", "String" are considered constants and Operands
    	List<String> operators = Arrays.asList("!", "::", "-", "*",  "/", "mod", "+", "-", "<<", ">>", "union", "=", ":=", "!=", "<",
    			">", "<=", ">=", "&", "|", "xor", "xnor", "<->", "->",

    			"@F~", "@O~", "A", "ABF", "ABG", "abs", "AF", "AG", "array","ASSIGN", "at next", "at last", "AX, bool",
    			"BU", "case", "Clock", "clock", "COMPASSION", "COMPID", "COMPUTE", "COMPWFF", "CONSTANTS",
    			"CONSTARRAY","CONSTRAINT", "cos", "count", "CTLSPEC", "CTLWFF", "DEFINE", "E", "EBF", "EBG", "EF", "EG", "esac",
    			"EX", "exp", "extend", "F", "FAIRNESS", "floor", "FROZENVAR", "FUN", "G", "H", "IN", "INIT", "init",
    			"Integer", "INVAR", "INVARSPEC", "ISA", "ITYPE", "IVAR", "JUSTICE", "ln", "LTLSPEC", "LTLWFF",
    			"MAX", "max", "MDEFINE", "MIN", "min", "MIRROR", "MODULE", "NAME", "next", "NEXTWFF", "noncontinuous",
    			"O", "of", "PRED", "PREDICATES", "pi", "pow", "PSLSPEC", "PARSYNTH", "READ", "Real", "resize", "S", "SAT",
    			"self", "signed", "SIMPWFF", "sin", "sizeof", "SPEC", "swconst", "T", "tan", "time", "time since", "time until",
    			"toint", "TRANS", "typeof", "U", "union", "unsigned", "URGENT", "uwconst", "V", "VALID", "VAR", "Word",
    			"word", "word1", "WRITE", "X", "X~ Y", "Y~", "Z", "TRUE", "FALSE").stream()
    			.sorted((a, b) -> Integer.compare(b.length(), a.length())) // Sort by length
    			.map(Pattern::quote) // Quote to make them safe for regex
    			.toList(); 

    	// Creating a combined pattern
    	String combinedPattern = String.join("|", operators);
    	Pattern pattern = Pattern.compile(combinedPattern);

    	for (String line : lines) {
    		line = removeInlineComments(line); // Remove inline comments before processing
    		if (!line.trim().startsWith("--")) { // Ignore comment lines
    			Matcher matcher = pattern.matcher(line);
    			while (matcher.find()) {
    				String matchedOperator = matcher.group();
    				allOperators.add(matchedOperator);
    				operatorCount[0]++; // Increment total count
    			}
    		}
    	}
    }






    private static void collectNuSMVOperands(String formula, Set<String> allOperands, int[] operandCount) {
    	String[] lines = formula.split("\n");

    	// Include hyphens within operand names.
    	Pattern pattern = Pattern.compile("[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*+(?:_[a-zA-Z0-9]+)*");

    	// Set of words (Operators) to be excluded
    	Set<String> excludedWords = new HashSet<>(Arrays.asList("!", "::", "-", "*",  "/", "mod", "+", "-", "<<", ">>", "union", "=", ":=", "!=", "<",
    			">", "<=", ">=", "&", "|", "xor", "xnor", "<->", "->",

    			"@F~", "@O~", "A", "ABF", "ABG", "abs", "AF", "AG", "array","ASSIGN", "at next", "at last", "AX, bool",
    			"BU", "case", "Clock", "clock", "COMPASSION", "COMPID", "COMPUTE", "COMPWFF", "CONSTANTS",
    			"CONSTARRAY","CONSTRAINT", "cos", "count", "CTLSPEC", "CTLWFF", "DEFINE", "E", "EBF", "EBG", "EF", "EG", "esac",
    			"EX", "exp", "extend", "F", "FAIRNESS", "floor", "FROZENVAR", "FUN", "G", "H", "IN", "INIT", "init",
    			"Integer", "INVAR", "INVARSPEC", "ISA", "ITYPE", "IVAR", "JUSTICE", "ln", "LTLSPEC", "LTLWFF",
    			"MAX", "max", "MDEFINE", "MIN", "min", "MIRROR", "MODULE", "NAME", "next", "NEXTWFF", "noncontinuous",
    			"O", "of", "PRED", "PREDICATES", "pi", "pow", "PSLSPEC", "PARSYNTH", "READ", "Real", "resize", "S", "SAT",
    			"self", "signed", "SIMPWFF", "sin", "sizeof", "SPEC", "swconst", "T", "tan", "time", "time since", "time until",
    			"toint", "TRANS", "typeof", "U", "union", "unsigned", "URGENT", "uwconst", "V", "VALID", "VAR", "Word",
    			"word", "word1", "WRITE", "X", "X~ Y", "Y~", "Z", "TRUE", "FALSE"));

    	for (String line : lines) {
    		line = removeInlineComments(line); // Remove inline comments before processing
    		if (!line.trim().startsWith("--")) { // Ignore comment lines
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


    private static String removeInlineComments(String line) {
    	int commentIndexDash = line.indexOf("--");
    	int commentIndexSlash = line.indexOf("//");

    	// Find the earliest comment indicator if both exist
    	int commentIndex = -1;
    	if (commentIndexDash != -1 && commentIndexSlash != -1) {
    		commentIndex = Math.min(commentIndexDash, commentIndexSlash);
    	} else if (commentIndexDash != -1) {
    		commentIndex = commentIndexDash;
    	} else if (commentIndexSlash != -1) {
    		commentIndex = commentIndexSlash;
    	}

    	// If a comment indicator is found, remove the comment part
    	if (commentIndex != -1) {
    		return line.substring(0, commentIndex).trim();
    	}
    	return line.trim(); // Return the line directly if no comments are found
    }
	
	

}
