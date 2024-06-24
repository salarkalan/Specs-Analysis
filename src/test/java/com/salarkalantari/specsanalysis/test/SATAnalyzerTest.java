package com.salarkalantari.specsanalysis.test;

import com.salarkalantari.specsanalysis.SpecAnalysisResult;
import com.salarkalantari.specsanalysis.SATAnalyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SATAnalyzerTest {
	
	// Path to the example SAT specification file in test resources
	private static String filePath1 = "src/test/resources/SATSpecsExample1.txt";

	// Run the analysis
    SATAnalyzer analyzer1 = new SATAnalyzer();
    SpecAnalysisResult resultSpec1 = analyzer1.analyze(filePath1);
    
    @Test
    void testSATSpec1LOC() {
        // Check the LOC
        int expectedLoc = 2; // The expected number of lines of code in the example
        assertEquals(expectedLoc, resultSpec1.getLoc());
    }
    
    @Test
    void testSATSpec1Comments(){
        // Check the LOC
        int expectedComments = 1; // The expected number of lines of code in the example
        assertEquals(expectedComments, resultSpec1.getNumberOfComments());
    }
}
