package com.salarkalantari.specsanalysis;


//import com.yourname.specsanalysis.model.SpecAnalysisResult;
//import com.yourname.specsanalysis.service.*;
//import com.yourname.specsanalysis.util.FileUtil;

import java.io.IOException;
import java.nio.file.Paths;

public class AnalysisController {

    public void analyzeSpecification(String specType, String filePath) {
        SpecAnalyzer analyzer;
        switch (specType.toLowerCase()) {
            case "sat":
                analyzer = new SATAnalyzer();
                break;
//            case "smt":
//                analyzer = new SMTAnalyzer();
//                break;
//            case "alloy":
//                analyzer = new AlloyAnalyzer();
//                break;
//            case "nusmv":
//                analyzer = new NuSMVAnalyzer();
//                break;
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
        if (!result.getSyntaxCheck().equals("Correct")) {
            System.out.println("Error: " + result.getSyntaxCheck());
        } else {
            System.out.println("Result: " + result.getResultMessage());
            System.out.println("Number of Comments: " + result.getNumberOfComments());
            System.out.println("Lines of Code (LOC): " + result.getLoc());
            System.out.println("Halstead Complexity: " + result.getHalsteadComplexity());
            System.out.println("Operators: " + result.getOperators());
            System.out.println("Operands: " + result.getOperands());
        }
    }
}
