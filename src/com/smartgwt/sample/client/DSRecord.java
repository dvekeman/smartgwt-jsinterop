package com.smartgwt.sample.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class DSRecord extends ListGridRecord {

    public DSRecord(String dsTitle, String dsName) {
        setDsTitle(dsTitle);
        setDsName(dsName);
    }
    
    public DSRecord(JavaScriptObject jsObj){
        super(jsObj);
    }

    public void setDsTitle(String dsTitle) {
        setAttribute("dsTitle", dsTitle);
    }

    public String getDsTitle() {
        return getAttributeAsString("dsTitle");
    }

    public void setDsName(String dsName) {
        setAttribute("dsName", dsName);
    }

    public String getDsName() {
        return getAttributeAsString("dsName");
    }

}
