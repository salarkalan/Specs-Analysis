package com.salarkalantari.specsanalysis;

public class SpecAnalysisResult {
    private String syntaxCheck;
    private String resultMessage;
    private int numberOfComments;
    private int loc;
    private int halsteadComplexity;
    private String operators;
    private String operands;

    // Getters and setters

    public String getSyntaxCheck() {
        return syntaxCheck;
    }

    public void setSyntaxCheck(String syntaxCheck) {
        this.syntaxCheck = syntaxCheck;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public int getHalsteadComplexity() {
        return halsteadComplexity;
    }

    public void setHalsteadComplexity(int halsteadComplexity) {
        this.halsteadComplexity = halsteadComplexity;
    }

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
    }

    public String getOperands() {
        return operands;
    }

    public void setOperands(String operands) {
        this.operands = operands;
    }
}
