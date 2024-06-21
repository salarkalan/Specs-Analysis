package com.salarkalantari.specsanalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


//import com.yourname.specsanalysis.model.SpecAnalysisResult;

public class SATAnalyzer implements SpecAnalyzer {
    @Override
    public SpecAnalysisResult analyze(String filePath) {
        SpecAnalysisResult result = new SpecAnalysisResult();
        result.setLoc(getSATLOC(filePath));
        result.setNumberOfComments(getSATComments(filePath));
        result.setSyntaxCheck(runLimboole(filePath)[0]);
        result.setResultMessage(runLimboole(filePath)[1]);
        
        return result;
    }
    
    
    //run the spec using Limboole
    private static String[] runLimboole(String filePath){
    	String [] results = new String [] {"", ""};
   		
    	String formula = "";
			try {
				formula = readFileToString(filePath);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String LIMBOOLE_EXE = "lib/limboole.exe";
			
			String IN_FILE = "in.txt";
			String OUT_FILE = "out.txt";
			String ERROR_FILE = "error.txt";
			
			
			try {
				Files.deleteIfExists(Paths.get(IN_FILE));
				Files.deleteIfExists(Paths.get(OUT_FILE));
				Files.deleteIfExists(Paths.get(ERROR_FILE));


				Files.writeString(Paths.get(IN_FILE), formula);

				ProcessBuilder pb = new ProcessBuilder();
				
				//TODO change to optional Satisfiability / Validity
				
				// checking Validity
				//if (type.equals("VAL")) {
					//pb.command(LIMBOOLE_EXE, IN_FILE);
				//}
				// checking satisfiability
				//else {
					//pb.command(LIMBOOLE_EXE, IN_FILE, "-s");
				//}
				
				pb.command(LIMBOOLE_EXE, IN_FILE, "-s");

				// redirect output and error to files
				pb.redirectOutput(Redirect.appendTo(new File(OUT_FILE)));
				pb.redirectError(Redirect.appendTo(new File(ERROR_FILE)));

				Process p = pb.start();
				p.waitFor();

				String errors = Files.readString(Paths.get(ERROR_FILE));
				if (errors.length() != 0) {
				  //throw new RuntimeException("Limboole call produced errors: " + errors);
					results[0] = errors;
				}
				else {
					results[0] = "Correct";
				}
				
				results[1]= Files.readString(Paths.get(OUT_FILE));
				
				} catch (IOException e) {
					results[1] = "";
				//e.printStackTrace();
				} catch (Exception e) {
					results[1] = "";
				//e.printStackTrace();
				}
    	
    	return results;
    }
    
    
    public static String readFileToString(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            throw e;
        }

        return contentBuilder.toString();
    }
 
    //Count the number of lines of code (LOC) in a SAT specification.
    private static int getSATLOC(String filePath) {
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
    

    
    //Count the number of comment lines in a SAT specification.
    private static int getSATComments(String filePath) {
        int commentCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("%") || line.contains("%")) {
                    commentCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return commentCount;
    }

    
}

