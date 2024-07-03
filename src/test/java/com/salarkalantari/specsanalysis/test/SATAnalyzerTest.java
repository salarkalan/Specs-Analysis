package com.salarkalantari.specsanalysis.test;

import com.salarkalantari.specsanalysis.SpecAnalysisResult;
import com.salarkalantari.specsanalysis.SATAnalyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;

public class SATAnalyzerTest {
	
	// Path to the example SAT specification file in test resources
	private static final String filePath1 = "src/test/resources/SATSpec1.txt";
	private static final String filePath2 = "src/test/resources/SATSpec2.txt";
	private static final String filePath3 = "src/test/resources/SATSpec3.txt";

    private SATAnalyzer analyzer1;
    private SpecAnalysisResult resultSpec1;
    
    private SATAnalyzer analyzer2;
    private SpecAnalysisResult resultSpec2;
    
    private SATAnalyzer analyzer3;
    private SpecAnalysisResult resultSpec3;
    
    @BeforeEach
    void setUp() {
    	analyzer1 = new SATAnalyzer();   
    	resultSpec1 = analyzer1.analyze(filePath1);
    	
    	analyzer2 = new SATAnalyzer();   
    	resultSpec2 = analyzer2.analyze(filePath2);
    	
    	analyzer3 = new SATAnalyzer();   
    	resultSpec3 = analyzer3.analyze(filePath3);
    }
    
    @Test
    void testSATSpec1LOC() {
        // Check the LOC
        int expectedLoc = 2; 
        assertEquals(expectedLoc, resultSpec1.getLoc());
    }
    
    @Test
    void testSATSpec1Comments(){
        // Check the LOC
        int expectedComments = 1; 
        assertEquals(expectedComments, resultSpec1.getNumberOfComments());
    }
    
    @Test
    void testSATSpec2LOC() {
        // Check the LOC
        int expectedLoc = 1; 
        assertEquals(expectedLoc, resultSpec2.getLoc());
    }
    
    @Test
    void testSATSpec2Comments(){
        // Check the LOC
        int expectedComments = 2; 
        assertEquals(expectedComments, resultSpec2.getNumberOfComments());
    }
    
    @Test
    void testSATSpec2Halstead() {
    	int[] expectedHalstead = new int [] {2,3,3,4};
    	assertArrayEquals(expectedHalstead, resultSpec2.getHalstead());
    }
    
    @Test
    void testSATSpec3Syntax() {
    	assertNotEquals("Correct", resultSpec3.getSyntaxCheck());
    }
    
}
