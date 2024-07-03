package com.salarkalantari.specsanalysis.test;

import com.salarkalantari.specsanalysis.SpecAnalysisResult;
import com.salarkalantari.specsanalysis.AlloyAnalyzer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;

public class AlloyAnalyzerTest {

    // Path to the examples SMT specification file in test resources
    private static final String filePath1 = "src/test/resources/AlloySpec1.txt";
    private static final String filePath2 = "src/test/resources/AlloySpec2.txt";
    private static final String filePath3 = "src/test/resources/AlloySpec3.txt";

    private AlloyAnalyzer analyzer1;
    private SpecAnalysisResult resultSpec1;
    
    private AlloyAnalyzer analyzer2;
    private SpecAnalysisResult resultSpec2;
    
    private AlloyAnalyzer analyzer3;
    private SpecAnalysisResult resultSpec3;

    
    @BeforeEach
    void setUp() {
        analyzer1 = new AlloyAnalyzer();
        resultSpec1 = analyzer1.analyze(filePath1);
        
        analyzer2 = new AlloyAnalyzer();
        resultSpec2 = analyzer2.analyze(filePath2);
        
        analyzer3 = new AlloyAnalyzer();
        resultSpec3 = analyzer3.analyze(filePath3);
    }
    
    @Test
    void testAlloySpec1LOC() {
    	int expectedLOC = 2;
    	assertEquals(expectedLOC, resultSpec1.getLoc());
    }
    
    @Test
    void testAlloySpec1Comments() {
    	int expectedComments = 1;
    	assertEquals(expectedComments, resultSpec1.getNumberOfComments());
    }
    
    @Test
    void testAlloySpec1Halstead() {
        int[] expectedHalstead = new int[] {2,1,2,1};
        assertArrayEquals(expectedHalstead, resultSpec1.getHalstead());
    }
    
    @Test
    void testAlloySpec2LOC() {
        int expectedLOC = 11; 
        assertEquals(expectedLOC, resultSpec2.getLoc());
    }

    
    @Test
    void testAlloySpec2Halstead() {
        int[] expectedHalstead = new int[] {5,6,9,9};
        assertArrayEquals(expectedHalstead, resultSpec2.getHalstead());
    }

    @Test
    void testAlloySpec3Syntax() {
        assertNotEquals("Correct", resultSpec3.getHalstead());
    }
}