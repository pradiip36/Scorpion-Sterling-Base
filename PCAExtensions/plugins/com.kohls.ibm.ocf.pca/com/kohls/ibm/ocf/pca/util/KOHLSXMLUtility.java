package com.kohls.ibm.ocf.pca.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Defines the XML utilities used by Crox.
 * 
 * @author Roy Nicholls
 */
public final class KOHLSXMLUtility {
	/**
	 * Returns a blank W3C document
	 * 
	 * @return blank W3C document
	 */
	public static final Document newDOM() throws ParserConfigurationException {
		//
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
			return builder.newDocument();
		} finally {
		}

	}// newDOM:Document

	/**
	 * Applies a transformation to a document
	 * 
	 * @param in
	 *            the document to strip namespaces from
	 * @param trans
	 *            the compiled transformation document
	 * @return the document stripped of all namespaces
	 * @throws IllegalStateException
	 *             if the transformation fails.
	 */
	public final static Document applyXsl(Document in, Document trans)
			throws IllegalStateException {
		//
		TransformerFactory factory = null;
		Transformer transform = null;
		DOMResult out = null;
		try {
			factory = TransformerFactory.newInstance();
			transform = factory.newTransformer(new DOMSource(trans));
			out = new DOMResult(KOHLSXMLUtility.newDOM());
			transform.transform(new DOMSource(in), out);
			return (Document) out.getNode();
			//
		} catch (TransformerConfigurationException ex) {
			final IllegalStateException ise = new IllegalStateException(ex
					.getMessage(), ex);
			ise.setStackTrace(ex.getStackTrace());
			throw ise;
		} catch (ParserConfigurationException ex) {
			final IllegalStateException ise = new IllegalStateException(ex
					.getMessage(), ex);
			ise.setStackTrace(ex.getStackTrace());
			throw ise;
		} catch (TransformerException ex) {
			final IllegalStateException ise = new IllegalStateException(ex
					.getMessage(), ex);
			ise.setStackTrace(ex.getStackTrace());
			throw ise;
		}
		//
	}// applyXsl(Document, Document):Document

	/**
	 * A method that parses a string into an XML document
	 * 
	 * @param xml
	 *            the string to parse into a W3CDOM
	 * @return the parsed document
	 */
	public final static Document toDocument(final String xml)
			throws ParserConfigurationException, IOException, SAXException {
		//
		DocumentBuilderFactory factory = null;
		DocumentBuilder builder = null;
		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new StringReader(xml)));
		} finally {
		}
	}// toDOM(String):Document

	/**
	 * This utility method applies an XPath to a W3C DOM node, and returns the
	 * resulting result tree.
	 * 
	 * @param inXml
	 *            the input XML document
	 * @param expression
	 *            the xpath expression to apply
	 * @return a node list that satisfies the XPath
	 * @throws XPathExpressionExcetion
	 *             if errors occur
	 */
	public static final NodeList applyXPath(Node inXml, String expression)
			throws XPathExpressionException {
		XPath myXpath = XPathFactory.newInstance().newXPath();
		XPathExpression xpath = myXpath.compile(expression);

		return KOHLSXMLUtility.applyXPath(inXml, xpath);

	}// applyXPath(Document, String):NodeList

	/**
	 * A utility method to apply an XPath to a W3C DOM node, returning the
	 * resulting node list.
	 * 
	 * @param inXml
	 *            the input XML document
	 * @param expression
	 *            the pre-compiled XPath expression
	 * @return a node list that matches the XPath
	 * @throws XPathExpressionException
	 *             if the XPath is invalid
	 */
	public final static NodeList applyXPath(Node inXml,
			XPathExpression expression) throws XPathExpressionException {
		return (NodeList) (expression.evaluate(inXml, XPathConstants.NODESET));

	}// applyXPath(Document, XPathExpression):NodeList

	/**
	 * A utility method that gets an element given its XPath as a compliled
	 * expression.
	 * 
	 * @param inXml
	 *            the document from which to extract the element
	 * @param expression
	 *            the compiled XPath expression
	 * @return the extracted element
	 * @throws XPathExpressionException
	 *             if the XPath is faulty or does not point to an element
	 */
	public final static Element getElementFromXPath(final Node inXml,
			final XPathExpression expression) throws XPathExpressionException {
		//
		return (Element) (expression.evaluate(inXml, XPathConstants.NODE));

	}// getElementFromXPath(Node, XPathExpression):Element

	/**
	 * An overloaded version of the <code>getElementFromXPaty</code> that
	 * returns an element from a document given its XPath as a string
	 * 
	 * @param inXml
	 *            the document from which to extract the element
	 * @param xpath
	 *            the XPath as a string
	 * @return the extracted element
	 * @throws XPathExpressionException
	 *             if the Xpath is faulty
	 */
	public final static Element getElementFromXPath(final Node inXml,
			final String xpath) throws XPathExpressionException {
		//
		XPathFactory factory = XPathFactory.newInstance();
		XPathExpression expression = factory.newXPath().compile(xpath);

		return KOHLSXMLUtility.getElementFromXPath(inXml, expression);
		//
	}// getElementFromXPath(Node, String):Element

	/**
	 * Overloaded version of <code>getAttributeFromXPath()</code> that
	 * specifies a string instead of a pre-compiled XPath.
	 * 
	 * @param inXml
	 *            the input XML document
	 * @param xpath
	 *            the XPath string
	 * @throws XPathExpressionException
	 *             if errors occur
	 */
	public final static String getAttributeFromXPath(final Node inXml,
			final String xpath) throws XPathExpressionException {
		//
		XPathExpression expression = XPathFactory.newInstance().newXPath()
				.compile(xpath);

		return KOHLSXMLUtility.getAttributeFromXPath(inXml, expression);

	}// getAttributeFromXPath(Node, String):String

	/**
	 * This utility gets the value of an attribute or element by applying an
	 * XPath.
	 * 
	 * @param inXml
	 *            the document from which to extract value
	 * @param expression
	 *            the compiled XPath expression
	 * @return value of attribute
	 * @throws XPathExpressionException
	 *             if the expression is invalid
	 */
	public final static String getAttributeFromXPath(final Node inXml,
			XPathExpression expression) throws XPathExpressionException {
		//
		return expression.evaluate(inXml);

	}// getAttributeFromXpath(Node, XPathExpression):String

	//
	/**
	 * Private constructor for utility class.
	 */
	private KOHLSXMLUtility() {
		//
	}// constructor:KOHLSXMLUtility

	/**
	 * Converts a W3C document object into an XML string, indenting the output
	 * 
	 * @param node
	 *            the document to convert to XML
	 * @return the formatted document as as string
	 */
	public final static String toXml(final Node node) {
		//
		return KOHLSXMLUtility.toXml(node, true);
		//
	}// toXml(Node):String

	/**
	 * Converts a W3C document object into an XML string.
	 * 
	 * @param node
	 *            the input W3C node object
	 * @param indent
	 *            when true, format the XML with indents; otherwise, formats as
	 *            continuous string
	 * @return the incoming XML document as as string; null if the incoming
	 *         document is null, or contains no document element.
	 */
	public final static String toXml(final Node node, boolean indent) {
		//
		XMLSerializer serializer = null;
		OutputFormat format = null;
		StringWriter out = null;
		try {
			if (node == null) {
				return null;
			}// end: if (inXml == null)

			format = new OutputFormat("XML", "UTF-8", indent);
			out = new StringWriter();
			serializer = new XMLSerializer(out, format);

			// Test node type
			switch (node.getNodeType()) {
			case Node.DOCUMENT_NODE: {
				serializer.serialize((Document) node);
				break;
			}// end: case Node.DOCUMENT_NODE:

			case Node.DOCUMENT_FRAGMENT_NODE: {
				serializer.serialize((DocumentFragment) node);
				break;
			}// end: case Node.DOCUMENT_FRAGMENT_NODE:

			case Node.ELEMENT_NODE: {
				serializer.serialize((Element) node);
				break;
			}// end: case Node.ELEMENT_NODE:

			default: {
				final String message = "Cannot serialize node of type ["
						+ node.getClass().getName() + "].";
				throw new IOException(message);
			}// end: default:
			}// end: switch (node.getNodeType())

			return out.toString();
		} catch (IOException ex) {
			return "<Error><![CDATA[" + ex.getMessage() + "]]></Error>";
		} finally {
		}
	}// toXml(Node, boolean):String

	/**
	 * Converts a W3C <code>dateTime</code> string into a java date object.
	 * This is an overloaded version of the method that automatically preservers
	 * the time portion of the date time.
	 * 
	 * @param dateTime
	 *            a W3C dateTime string
	 * @return a <code>java.util.Date</code> object representation of the date
	 * @throws DatatypeConfigurationException
	 *             if an invalid date is passed
	 */
	public final static Date toDate(final String dateTime)
			throws DatatypeConfigurationException, IllegalArgumentException {
		//
		return KOHLSXMLUtility.toDate(dateTime, false);

	}// toDate(String):Date

	/**
	 * Converts a W3C <code>dateTime</code> string into a Java date object
	 * 
	 * @param dateTime
	 *            W3C date/time string
	 * @param dayOnly
	 *            if set to yes, the time portion is truncated from the dateTime
	 *            passed in; if false, the time portion is maintained
	 * @return a <code>java.util.Date</code> representation
	 * @throws DatatypeConfigurationException
	 *             if the datatype factory can't be instantiated
	 * @throws IllegalArgumentException
	 *             if the dateTime parameter is null or an empty string, or
	 *             invalid W3C dateTime.
	 */
	public static Date toDate(final String dateTime, boolean dayOnly)
			throws DatatypeConfigurationException, IllegalArgumentException {
		//
		if (KOHLSStringUtility.isEmpty(dateTime)) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"Mandatory parameter 'dateTime' missing.");
			throw iae;

		}// end: if (StringUtil.isEmpty(dateTime))

		DatatypeFactory factory = DatatypeFactory.newInstance();
		XMLGregorianCalendar xml = factory.newXMLGregorianCalendar(dateTime);

		// if the day only portion is required, remove all time elements
		if (dayOnly) {
			xml.setHour(0);
			xml.setMinute(0);
			xml.setSecond(0);
			xml.setMillisecond(0);

		}// end: if (dayOnly)

		TimeZone timeZone = TimeZone.getDefault();
		Locale locale = Locale.getDefault();

		return xml.toGregorianCalendar(timeZone, locale, null).getTime();

	}// toDate(String, boolean):Date

	/**
	 * A method that converts a <code>java.util.Date</code> object into a W3C
	 * Date in <code>yyyy-mm-dd</code> format.
	 * 
	 * @param date
	 *            input date to convert to W3C Date
	 * @return date formatted as yyyy-mm-dd; null if the input date is null
	 * @throws DatatypeConfigurationException
	 *             if the date cannot be processsed
	 */
	public final static String toW3CDate(final Date date)
			throws DatatypeConfigurationException {
		//
		DatatypeFactory factory = null;
		GregorianCalendar greg = null;
		XMLGregorianCalendar xml = null;
		try {
			if (date == null) {
				return null;
			}// end:if (date == null)
			//
			// create factory
			factory = DatatypeFactory.newInstance();
			greg = new GregorianCalendar(Locale.getDefault());
			greg.setTime(date);

			// format date; remove seconds and time zone
			xml = factory.newXMLGregorianCalendar(greg);
			xml.setTime(DatatypeConstants.FIELD_UNDEFINED,
					DatatypeConstants.FIELD_UNDEFINED,
					DatatypeConstants.FIELD_UNDEFINED,
					DatatypeConstants.FIELD_UNDEFINED);
			xml.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			//
			return xml.toXMLFormat();
		} finally {
		}
	}// toW3CDate(Date):String

	/**
	 * An overloaded version of the <code>toW3CDateTime(Date, boolean)</code>
	 * method that returns no milliseconds in the W3C dateTime field. That is,
	 * the returned time stamp resembles the following:
	 * <code>yyyy-mm-ddThh:mm:ss-zz:zz</code>.
	 * 
	 * @param date
	 *            the date to convert to a W3C dateTime string
	 * @throws DatatypeConfigurationException
	 *             if the XML calendar can't be configured
	 */
	public final static String toW3CDateTime(final Date date)
			throws javax.xml.datatype.DatatypeConfigurationException {
		//
		return KOHLSXMLUtility.toW3CDateTime(date, false);

	}// toW3CDateTime(Date):String

	/**
	 * A utility method to format a java Date object into its XSD
	 * <code>dateTime</code> representation. The W3C representation is
	 * yyyy-MM-DDTHH:mm:ss.sss-zz:zz.
	 * 
	 * @param date
	 *            java date object
	 * @param milliseconds
	 *            if true, milliseconds are included in the returned string;
	 *            otherwise, milliseconds are omitted.
	 * @return the date formated as W3C dateTime
	 * @throws DatatypeConfigurationException
	 *             if the datatype factory can't be instantiaged
	 * @throws IllegalArgumentException
	 *             if the date parameter is null.
	 */
	public static final String toW3CDateTime(final Date date,
			boolean milliseconds)
			throws javax.xml.datatype.DatatypeConfigurationException {
		//
		if (date == null) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"Mandatory parameter 'date' missing.");
			throw iae;

		}// end: if (date == null)

		DatatypeFactory factory = DatatypeFactory.newInstance();
		GregorianCalendar greg = new GregorianCalendar(Locale.getDefault());
		greg.setTime(date);

		XMLGregorianCalendar xml = factory.newXMLGregorianCalendar(greg);

		// optionally, remove milliseconds from the timestamp
		if (!milliseconds) {
			xml.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

		}// end: if (!milliseconds)

		return xml.toXMLFormat();

	}// toW3CDateTime(Date, boolean):String

}// class:KOHLSXMLUtility
