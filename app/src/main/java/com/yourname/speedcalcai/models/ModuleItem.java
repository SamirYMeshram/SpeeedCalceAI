package com.yourname.speedcalcai.models;

public class ModuleItem {
    private final String moduleId;
    private final String moduleName;
    private final String categoryName;
    private final String iconText;
    private final String questionType;
    private final String difficulty;

    public ModuleItem(String moduleId, String moduleName, String categoryName, String iconText, String questionType, String difficulty) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.categoryName = categoryName;
        this.iconText = iconText;
        this.questionType = questionType;
        this.difficulty = difficulty;
    }

    public String getModuleId() { return moduleId; }
    public String getModuleName() { return moduleName; }
    public String getCategoryName() { return categoryName; }
    public String getIconText() { return iconText; }
    public String getQuestionType() { return questionType; }
    public String getDifficulty() { return difficulty; }
}
