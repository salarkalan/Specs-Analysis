package com.salarkalantari.specsanalysis;


//import com.salarkalantari.specsanalysis.controller.AnalysisController;

public class App {
    public static void main(String[] args) {
    	
    	
    	// Example how to run the App by defining specType and filePath
    	args = new String [] {"sat", "src/main/resources/SATSpecsExample1.txt"};
    	
        if (args.length < 2) {
            System.out.println("Usage: java -jar specs-analysis.jar <spec-type> <file-path>");
            System.exit(1);
        }

        String specType = args[0];
        String filePath = args[1];

        AnalysisController controller = new AnalysisController();
        controller.analyzeSpecification(specType, filePath);
    }
}
