package com.salarkalantari.specsanalysis;


//import com.yourname.specsanalysis.model.SpecAnalysisResult;
//import com.yourname.specsanalysis.service.*;
//import com.yourname.specsanalysis.util.FileUtil;

import java.io.IOException;

public class AnalysisController {

    public void analyzeSpecification(String specType, String filePath) {
        SpecAnalyzer analyzer;
        switch (specType.toLowerCase()) {
            case "sat":
                analyzer = new SATAnalyzer();
                break;
            case "smt":
                analyzer = new SMTAnalyzer();
                break;
            case "alloy":
                analyzer = new AlloyAnalyzer();
                break;
            case "nusmv":
            	analyzer = new NuSMVAnalyzer();
                break;
            default:
                System.out.println("Unknown specification type: " + specType);
                System.out.println("Accepted types: sat / smt / alloy / nusmv");
                return;
        }

        try {
            String code = FileUtil.readFile(filePath);
            SpecAnalysisResult result = analyzer.analyze(filePath);

            // Print or save the result
            printResult(result);

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private void printResult(SpecAnalysisResult result) {
    	System.out.println("*****************************************************************");
        System.out.println("*                       Analysis Results                        *");
        System.out.println("*****************************************************************");
        
        if (!result.getSyntaxCheck().equals("Correct")) {
        	System.out.println("Syntax check: Error!");
        	System.out.println("Error: " + result.getSyntaxCheck());
        	System.out.println("-----------------------------------------------------------------");
            System.out.println("Run the Specification: ");
            System.out.println("Error!");
        } else {
        	System.out.println("Syntax check: Correct");
        	System.out.println("-----------------------------------------------------------------");
            System.out.println("Run the Specification: ");
            System.out.println(result.getResultMessage());
        }
       	
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Number of Comments: " + result.getNumberOfComments());
        System.out.println("Lines of Code (LOC): " + result.getLoc());
        System.out.println("Unique Operators: " + result.getOperators());
        System.out.println("Unique Operands: " + result.getOperands());
        System.out.println("Total Operators: " + result.getHalstead()[2]);
        System.out.println("Total Operands: " + result.getHalstead()[3]);
        System.out.println("-----------------------------------------------------------------");
             
        int n1 = result.getHalstead()[0];
        int n2 = result.getHalstead()[1];
        int N1 = result.getHalstead()[2];
        int N2 = result.getHalstead()[3];
            
        int n = n1 + n2;
        int N = N1 + N2;
        double v = N * (Math.log(n) / Math.log(2));
        double D = (n1/2)*(N2/n2);
        double E = D * v;
        double T = E / 18;
        double B = v / 3000;
            		
        System.out.println("Halstead's Software Metrics: "+ "\n");
        System.out.println("Program vocabulary n = "+ n);
        System.out.println("Program length N = " + N);
        System.out.println("Program Volume V = " + v);
        System.out.println("Program Difficulty D = " + D);
        System.out.println("Program Effort E = " + E);
        System.out.println("Program time T = " + T);
        System.out.println("Program Bugs B = " + B);
        System.out.println("-----------------------------------------------------------------");

    }
}
