/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

/**
 *
 * @author esmail.anbar
 */
public class TemplateModel {
    private int templateId;
    private String templateDescription;
    private String arabicScript;
    private String englishScript;
    private Integer expirationDuration;

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getTemplateDescription() {
        return templateDescription;
    }

    public void setTemplateDescription(String templateDescription) {
        this.templateDescription = templateDescription;
    }

    public String getArabicScript() {
        return arabicScript;
    }

    public void setArabicScript(String arabicScript) {
        this.arabicScript = arabicScript;
    }

    public String getEnglishScript() {
        return englishScript;
    }

    public void setEnglishScript(String englishScript) {
        this.englishScript = englishScript;
    }

    public Integer getExpirationDuration() {
        return expirationDuration;
    }

    public void setExpirationDuration(Integer expirationDuration) {
        this.expirationDuration = expirationDuration;
    }
}
