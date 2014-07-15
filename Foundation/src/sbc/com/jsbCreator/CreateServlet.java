package com.jsbCreator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import java.io.*;
 
import com.sterlingcommerce.ui.web.framework.helpers.SCUIJSLibraryHelper;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;

public class CreateServlet extends HttpServlet {
        
    public synchronized void init(final ServletConfig config) throws ServletException {
		SCUIJSLibraryHelper.loadJSLibraryXml("/extn/builder/kohlsocf.jsb", config.getServletContext());
    	SCUIMashupHelper.loadMashupXml("/extn/mashupxmls/sbc/item/KohlsItemIneligibility_mashup.xml", config.getServletContext());
		SCUIMashupHelper.loadMashupXml("/extn/mashupxmls/sbc/item/KohlsItemEligibility_mashup.xml", config.getServletContext());
		SCUIMashupHelper.loadMashupXml("/extn/mashupxmls/sbc/item/kohlsManageSafetyLevel_mashup.xml", config.getServletContext());
   	}
}

