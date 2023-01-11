/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.common.models;

import java.util.Vector;

/**
 *
 * @author Zain.Hamed
 */
public class LineModel {
    
   private String fileName;
   private long lineNum;
   private String lineData;
   private Vector<Object> insertData;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getLineNum() {
        return lineNum;
    }

    public void setLineNum(long lineNum) {
        this.lineNum = lineNum;
    }

    public String getLineData() {
        return lineData;
    }

    public void setLineData(String lineData) {
        this.lineData = lineData;
    }

    public Vector<Object> getInsertData() {
        return insertData;
    }

    public void setInsertData(Vector<Object> insertData) {
        this.insertData = insertData;
    }
   
   
    
}
