package com.salarkalantari.specsanalysis;

import java.io.FileNotFoundException;
import java.util.Scanner;

//import com.yourname.specsanalysis.model.SpecAnalysisResult;

public class SATAnalyzer implements SpecAnalyzer {
    @Override
    public SpecAnalysisResult analyze(String filePath) {
        SpecAnalysisResult result = new SpecAnalysisResult();
        
        //TODO first read the spec form path and then set it to LOC
        
        return result;
    }
    
    
    
    private static int getSATLOC(String spec) {
        int lineCount = 0;
        //TODO not sure about the \r\n
        String[] lines = spec.split("\r\n"); // Split the code into lines

        for (String line : lines) {
            // Trim whitespace and check if the line is neither empty nor a comment
            if (!line.trim().isEmpty() && !line.trim().startsWith("%")) {
                lineCount++; // Increment count for valid lines of code
            }
        }
        return lineCount;
    }
}
