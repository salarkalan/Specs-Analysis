package com.salarkalantari.specsanalysis;

public class App {
    public static void main(String[] args) {
    	
    	// Example how to run the App by defining specType and filePath
    	args = new String [] {"nusmv", "src/test/resources/NuSMVSpec1.txt"};
    	
        if (args.length < 2) {
            System.out.println("Usage: java -cp out com.salarkalantari.specsanalysis.App <spec-type> <file-path>");
            System.exit(1);
        }

        String specType = args[0];
        String filePath = args[1];

        AnalysisController controller = new AnalysisController();
        controller.analyzeSpecification(specType, filePath);
    }
}