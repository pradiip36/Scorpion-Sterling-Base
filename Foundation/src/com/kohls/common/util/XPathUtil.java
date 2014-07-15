package com.kohls.common.util;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

/**
* 
* This is an utility class which uses  
* 					Xpath to access the nodes in a xml document.
* 
* @(#) XPathUtil.java
*
* Package Declaration:
* File Name:                  XPathUtil.java
* Package Name:               com.kohls.common.util
* Project name:               OCF
* Type Declaration:
* Class Name:                 XPathUtil
* Type Comment:
* 
*
*
* (C) Copyright 2006-2007 by owner.
* All Rights Reserved.
*
* This software is the confidential and proprietary information
* of the owner. ("Confidential Information").
* Redistribution of the source code or binary form is not permitted
* without prior authorization from the owner.
*
*/
public class XPathUtil {

    /**
     * @return
     * @throws Exception
     * @deprecated use XMLUtil.newDocument() instead.
     */
    public static Document getDocument() throws Exception {
        //Create a new Document Bilder Factory instance
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        
        //Create new document builder
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        
        //Create and return document object
        return documentBuilder.newDocument();
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return node if found
     * @throws Exception exception
     */
    public static Node getNode(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        Node ret = null;
        try {
            ret = XPathAPI.selectSingleNode(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding value as a String.
     *
     * @param node  document context.
     * @param xpath xpath that has to be evaluated.
     * @return String Value of the XPath Execution.
     * @throws Exception exception
     */
    public static String  getString(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        String value = null;
        try {
            XObject xobj = XPathAPI.eval(node, xpath);
            value = xobj.toString();
        } catch (TransformerException e) {
            throw e;
        }
        return value;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node list.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeList getNodeList(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        NodeList ret = null;
        try {
            ret = XPathAPI.selectNodeList(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }
    
    /**
     * Evaluates the given Xpath and returns the corresponding node iterator.
     *
     * @param node  document context
     * @param xpath xpath to be evaluated
     * @return nodelist
     * @throws Exception exception
     */
    public static NodeIterator getNodeIterator(Node node, String xpath)
    throws Exception {
        if (null == node) {
            return null;
        }
        NodeIterator ret = null;
        try {
            ret = XPathAPI.selectNodeIterator(node, xpath);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
    }
    
    /**
     * @param elName
     * @return
     * @throws Exception
     * @deprecated use XMLUtil.createDocument(String docElementTag)
     */
    public static Document getEmptyDoc(String elName) throws Exception {
        Document ret = getDocument();
        Element el = ret.createElement(elName);
        ret.appendChild(el);
        return ret;
    }
}
