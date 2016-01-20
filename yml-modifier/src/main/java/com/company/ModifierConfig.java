package com.company;

import company.config.Config;

/**
 * Created by Naya on 12.01.2016.
 */
public class ModifierConfig implements Config{
    private String id;
    private String inputFileURL;
    private String inputFile;
    private String outputDir;
    private String encoding;
    private boolean modifyDescription;
    private boolean modifyOfferId;
    private String offerIdPrefix;
    private boolean modifyCategoryId;
    private String categoryIdPrefix;
    private String removedCategoryId;
    private String template;
    private int filesCount;

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getInputFileURL() {
        return inputFileURL;
    }

    public void setInputFileURL(String inputFileURL) {
        this.inputFileURL = inputFileURL;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isModifyDescription() {
        return modifyDescription;
    }

    public void setModifyDescription(boolean modifyDescription) {
        this.modifyDescription = modifyDescription;
    }

    public boolean isModifyOfferId() {
        return modifyOfferId;
    }

    public void setModifyOfferId(boolean modifyOfferId) {
        this.modifyOfferId = modifyOfferId;
    }

    public String getOfferIdPrefix() {
        return offerIdPrefix;
    }

    public void setOfferIdPrefix(String offerIdPrefix) {
        this.offerIdPrefix = offerIdPrefix;
    }

    public boolean isModifyCategoryId() {
        return modifyCategoryId;
    }

    public void setModifyCategoryId(boolean modifyCategoryId) {
        this.modifyCategoryId = modifyCategoryId;
    }

    public String getCategoryIdPrefix() {
        return categoryIdPrefix;
    }

    public void setCategoryIdPrefix(String categoryIdPrefix) {
        this.categoryIdPrefix = categoryIdPrefix;
    }

    public String getRemovedCategoryId() {
        return removedCategoryId;
    }

    public void setRemovedCategoryId(String removedCategoryId) {
        this.removedCategoryId = removedCategoryId;
    }

    public int getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(int filesCount) {
        if (filesCount <= 0)
            throw new IllegalArgumentException("filesCount has to be a positive integer");
        this.filesCount = filesCount;
    }
}
