package com.salarkalantari.specsanalysis.test;

import com.salarkalantari.specsanalysis.SpecAnalysisResult;
import com.salarkalantari.specsanalysis.SMTAnalyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;

public class SMTAnalyzerTest {

    // Path to the examples SMT specification file in test resources
    private static final String filePath1 = "src/test/resources/SMTSpec1.txt";
    private static final String filePath2 = "src/test/resources/SMTSpec2.txt";

    private SMTAnalyzer analyzer1;
    private SpecAnalysisResult resultSpec1;
    
    private SMTAnalyzer analyzer2;
    private SpecAnalysisResult resultSpec2;

    
    @BeforeEach
    void setUp() {
        analyzer1 = new SMTAnalyzer();
        resultSpec1 = analyzer1.analyze(filePath1);
        
        analyzer2 = new SMTAnalyzer();
        resultSpec2 = analyzer2.analyze(filePath2);
    }
    
    @Test
    void testSMTSpec1LOC() {
    	int expectedLOC = 5;
    	assertEquals(expectedLOC, resultSpec1.getLoc());
    }
    
    @Test
    void testSMTSpec1Comments() {
    	int expectedComments = 1;
    	assertEquals(expectedComments, resultSpec1.getNumberOfComments());
    }

    @Test
    void testSMTSpec2LOC() {
        int expectedLOC = 15; 
        assertEquals(expectedLOC, resultSpec2.getLoc());
    }

    @Test
    void testSMTSpec2Comments() {
        int expectedComments = 5; 
        assertEquals(expectedComments, resultSpec2.getNumberOfComments());
    }
    
    @Test
    void testSMTSpec2Halstead() {
        int[] expectedHalstead = new int[] {12,10,49,61};
        assertArrayEquals(expectedHalstead, resultSpec2.getHalstead());
    }

}