package com.kohls.ibm.ocf.pca.util;

import java.util.Iterator;

import org.w3c.dom.Document;

import com.yantra.ycp.ui.io.YCPWeighingScaleConnector;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.util.YFCException;

// Referenced classes of package com.yantra.ycp.ui.io:
// YCPWeighingScaleConnector

public final class KohlsScaleFactory {
    //
    // singleton instance of the factory
    private static KohlsScaleFactory instance = null;

    // cached scale implementation instance
    private YCPWeighingScaleConnector yws = null;

    /**
     * Returns the singleton instance of the scale
     * 
     * @return
     * 
     */
    public static synchronized KohlsScaleFactory getInstance() {
        //
        if (instance == null) {
            instance = new KohlsScaleFactory();
        }// end: if (instance == null)
         //
        return instance;
        //
    }// getInstance:KohlsScaleFactory

    /**
     * Private constructor for singleton factory
     */
    private KohlsScaleFactory() {
        //
    }// constructor

    /**
     * Initializes the scale factory with parameters.
     * 
     * @param params
     *            parameters as XML document
     * @throws YFCException
     *             if initialization fails
     * 
     */
    public final synchronized void init(final Document params) {
        //
        YFCDocument temp = null;
        YFCElement deviceParams = null;
        YFCElement attrsEle = null;
        YFCElement configEle = null;
        String name = null;
        String value = null;
        Iterator<YFCElement> attributes = null;
        YFCElement attribute = null;
        try {
            temp = YFCDocument.getDocumentFor(params);
            // System.out.println(temp.toString());
            deviceParams = temp.getDocumentElement();
            //
            if (deviceParams == null) {
                // TODO -- log exception
                final YFCException yfce = new YFCException(
                        "No device parameters passed to factory init.");
                throw yfce;
            }// end: if (deviceParams == null)
             //
            attrsEle = deviceParams.getChildElement("Attributes");
            if (attrsEle == null) {
                // TODO log exception
                final YFCException yfce = new YFCException(
                        "No attrbutes passed in initialization XML.");
                throw yfce;
            }// end: if (attrsEle == null)
             //
             //
            configEle = YFCDocument.createDocument("Config")
                    .getDocumentElement();
            attributes = attrsEle.getChildren();
            while (attributes.hasNext()) {
                attribute = attributes.next();
                name = attribute.getAttribute("Name");
                value = "";
                if (!YFCObject.isVoid(attribute.getAttribute("Value"))) {
                    value = attribute.getAttribute("Value");
                }// end: if (!YFCObject.isVoid(attribute.getAttribute("Value")))
                configEle.setAttribute(name, value);
            }// end: while (attributes.hasNext())
             //
            initInternal(configEle);
        } finally {
        }
    }// init

    /**
     * Internal initialization that creates the scale class
     * 
     * @param configElement
     *            contains attributes
     * 
     */
    private void initInternal(YFCElement configElement) {
        //
        String className = null;
        try {
            className = configElement.getAttribute("ClassName");
            if (YFCObject.isVoid(className)) {
                // TODO -- log exception
                final YFCException yfce = new YFCException(
                        "Scale initialization parameter 'ClassName' not passed.");
                throw yfce;
            }// end: if (YFCObject.isVoid(className))
             //
            yws = (YCPWeighingScaleConnector) Class.forName(className)
                    .newInstance();
            yws.init(configElement);
        } catch (Exception ex) {
            // TODO -- log exception
            final YFCException yfce = new YFCException(ex);
            yfce.setStackTrace(ex.getStackTrace());
            yfce.setErrorDescription(ex.getMessage());
            throw yfce;
        }
    }

    /**
     * Returns the initialized instance of the scale
     * 
     * @return the scale instance
     * @throws YFCException
     *             if errors occur
     * 
     */
    public final YCPWeighingScaleConnector getConnector() {
        //
        try {
            if (yws != null) {
                return yws;
            }// end: if (yws != null)
             //
            final YFCException yfc = new YFCException(
                    "YCPWeighingScaleConnector implementation is null. Verify if YCPWeighingFactory.init is called.");
            throw yfc;
        } finally {
        }
        //
    }// getConnector:YCPWeighingScaleConnector

}// class: KohlsScaleFactory
