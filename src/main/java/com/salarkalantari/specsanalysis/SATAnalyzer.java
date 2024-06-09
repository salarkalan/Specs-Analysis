package com.salarkalantari.specsanalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

//import com.yourname.specsanalysis.model.SpecAnalysisResult;

public class SATAnalyzer implements SpecAnalyzer {
    @Override
    public SpecAnalysisResult analyze(String filePath) {
        SpecAnalysisResult result = new SpecAnalysisResult();
        result.setLoc(getSATLOC(filePath));
        //TODO first read the spec form path and then set it to LOC
        
        return result;
    }
    
    
    
    private static int getSATLOC(String filePath) {
		/*
		 * int lineCount = 0; //TODO not sure about the \r\n String[] lines =
		 * spec.split("\r\n"); // Split the code into lines
		 * 
		 * for (String line : lines) { // Trim whitespace and check if the line is
		 * neither empty nor a comment if (!line.trim().isEmpty() &&
		 * !line.trim().startsWith("%")) { lineCount++; // Increment count for valid
		 * lines of code } } return lineCount;
		 */
    	int locCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inBlockComment = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("%")) {
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
    
}

