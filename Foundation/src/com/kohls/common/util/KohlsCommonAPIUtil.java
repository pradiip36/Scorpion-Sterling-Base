/**
 * 
 */
package com.kohls.common.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author pkrishnaswamy
 *
 */
public class KohlsCommonAPIUtil {
	
	public static Document callGetSurroundingNodeListAPI(YFSEnvironment env, int radius, Element shipToAddress) throws Exception {
		Document inputDoc = SCXmlUtil.createDocument(KohlsConstants.GET_SURROUNDING_NODE_LIST);

		Element inElement = inputDoc.getDocumentElement();
		inElement.setAttribute(KohlsConstants.DISTANCE_TO_CONSIDER, String.valueOf(radius));
		inElement.setAttribute(KohlsConstants.DISTANCE_TO_CONSIDER_UOM, KohlsConstants.MILE);
		inElement.setAttribute(KohlsConstants.NODE_TYPE, KohlsConstants.STORE);
		inElement.setAttribute(KohlsConstants.FULFILLMENT_TYPE,KohlsConstants.PRODUCTSOURCING);
		inElement.setAttribute(KohlsConstants.ORGANIZATION_CODE, ((YFSContext) env).getUEParam(KohlsConstants.ENTERPRISE_CODE));

		Element shipToEle = inputDoc.createElement(KohlsConstants.SHIP_TO_ADDRESS);
		inElement.appendChild(shipToEle);
		shipToEle.setAttribute(KohlsConstants.CITY, shipToAddress.getAttribute(KohlsConstants.CITY));
		shipToEle.setAttribute(KohlsConstants.STATE, shipToAddress.getAttribute(KohlsConstants.STATE));
		shipToEle.setAttribute(KohlsConstants.COUNTRY, shipToAddress.getAttribute(KohlsConstants.COUNTRY));
		shipToEle.setAttribute(KohlsConstants.ZIPCODE, shipToAddress.getAttribute(KohlsConstants.ZIPCODE));
		System.out.println(KohlsXMLUtil.getXMLString(inputDoc));
		Document outputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_SURROUNDING_NODE_LIST_TEMPLATE, KohlsConstants.GETSURROUNDING_NODE_LIST, inputDoc);

		return outputDoc;
	}
	
	/**
	 * Call getOrderDetails API to get the Items in the order.
	 * 
	 * @param env
	 * @param orderHeaderKey
	 * @return
	 * @throws Exception
	 */
	public static Document callGetOrderDetails(YFSEnvironment env, String orderHeaderKey) throws Exception {

		// TODO what will happen with the OHK is null?

		Document inputDoc = SCXmlUtil.createDocument(KohlsConstants.ORDER);

		Element inElement = inputDoc.getDocumentElement();
		inElement.setAttribute(KohlsConstants.ORDER_HEADER_KEY, orderHeaderKey);

	

		Document outputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ORDER_DETAILS_OUT_TEMPLATE, KohlsConstants.GET_ORDER_DETAILS, inputDoc);

	

		return outputDoc;

	}
	
	/**
	 * This method invokes the getItemDetails API to get the extn column details
	 * for the Item and also created the input XML that has to be passed to the
	 * KohlsIdentifyPromiseType util.
	 * 
	 * @param env
	 * @param orderDetails
	 * @return Input Document for the KohlsIdentifyPromiseType util.
	 *         <PromiseLines> <PromiseLine ItemID="" ItemType="" ExtnNomadic=""
	 *         ExtnShipAlone="" ExtnBreakable="" ExtnCage=""
	 *         ExtnShipNodeSource="" UOM="" OrganizationCode=""/>
	 *         </PromiseLines>
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws Exception
	 */
	public static Document callGetItemDetails(YFSEnvironment env, Document orderDetails) throws  Exception {

		Element orderInput = orderDetails.getDocumentElement();
		String entCode = orderInput.getAttribute(KohlsConstants.ENTERPRISE_CODE);

		Element orderLinesEle = (Element) orderInput.getElementsByTagName(KohlsConstants.ORDER_LINES).item(0);

		

		Document inputDocForUtil = SCXmlUtil.createDocument(KohlsConstants.PROMISE_LINES); // Input
																				// Document
																				// for
																				// the
																				// KohlsIdentifyPromiseType
																				// util
		Element inputEleForUtil = inputDocForUtil.getDocumentElement();

		if(!YFCCommon.isVoid(orderLinesEle)){
		NodeList orderLines = orderLinesEle.getElementsByTagName(KohlsConstants.ORDER_LINE);
		
		int length = orderLines.getLength();

		for (int i = 0; i < length; i++) {
			Element orderLineEle = (Element) orderLines.item(i);
			
			String fulfillType = orderLineEle.getAttribute(KohlsConstants.FULFILLMENT_TYPE);

			Element itemElement = (Element) orderLineEle.getElementsByTagName(KohlsConstants.ITEM).item(0);

			Element itemDetails = KohlsIdentifyPromiseType.callGetItemDetailsAPI(env, itemElement.getAttribute(KohlsConstants.ITEM_ID), itemElement.getAttribute(KohlsConstants.UNIT_OF_MEASURE), entCode);

			if (!YFCCommon.isVoid(itemDetails)) {
				Element primaryInfoEle = (Element)itemDetails.getElementsByTagName(KohlsConstants.PRIMARY_INFORMATION).item(0);
				Element promiseEleForUtil = inputDocForUtil.createElement(KohlsConstants.PROMISE_LINE);
				promiseEleForUtil.setAttribute(KohlsConstants.ITEM_ID, itemDetails.getAttribute(KohlsConstants.ITEM_ID));
				promiseEleForUtil.setAttribute(KohlsConstants.UNIT_OF_MEASURE, itemDetails.getAttribute(KohlsConstants.UNIT_OF_MEASURE));
				promiseEleForUtil.setAttribute(KohlsConstants.ITEM_TYPE, primaryInfoEle.getAttribute(KohlsConstants.ITEM_TYPE));
				promiseEleForUtil.setAttribute(KohlsConstants.ORGANIZATION_CODE, entCode);
				
				if(!YFCCommon.isVoid(fulfillType))
				{
					promiseEleForUtil.setAttribute(KohlsConstants.FULFILLMENT_TYPE, fulfillType);
				}

				Element itemExtnEle = (Element) itemDetails.getElementsByTagName(KohlsConstants.EXTN).item(0);
				if (!YFCCommon.isVoid(itemExtnEle)) {
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_NOMADIC, itemExtnEle.getAttribute(KohlsConstants.EXTN_NOMADIC));
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_SHIP_ALONE, itemExtnEle.getAttribute(KohlsConstants.EXTN_SHIP_ALONE));
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_BREAKABLE, itemExtnEle.getAttribute(KohlsConstants.EXTN_BREAKABLE));
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_CAGE, itemExtnEle.getAttribute(KohlsConstants.EXTN_CAGE));
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE, itemExtnEle.getAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE));
				}

				inputEleForUtil.appendChild(promiseEleForUtil);
			}

		}
		}
		return inputDocForUtil;
		}

	/*
	 * Wrapper for the getSurroundingNodeList - Start
	 */
	
	 //
    private final static double EARTH_RADIUS = 3960.0D;

    /**
     * A utility class that gets the surrounding node list The method expects a
     * document in the following format
     * 
     */
    public static final Document getSurroundingNodeList(final YFSEnvironment env, final Document inXml) throws YFSException {
        //
        KohlsSurroundNodeListBean bean = null;
        Document temp = null;
        Document shipTo = null;
        Document nodeList = null;
        Element address = null;
        try {
            bean = KohlsSurroundNodeListBean.unmarshal(inXml);
            temp = getShipToCoordinatesInput(bean);
            shipTo = getShipToCoordinates(env, temp);
            if (shipTo == null) {
                inXml.getDocumentElement().appendChild(
                        inXml.createElement("NodeList"));
                return inXml;
            }// end: if (shipTo == null)
             //
            bean.setLatitude(new Double(shipTo.getDocumentElement()
                    .getAttribute("Latitude")));
            bean.setLongitude(new Double(shipTo.getDocumentElement()
                    .getAttribute("Longitude")));
            //
            nodeList = getNodeList(env, bean);
            address = (Element) inXml.getElementsByTagName("ShipToAddress")
                    .item(0);
            address.setAttribute("Latitude", bean.getLatitude() + "");
            address.setAttribute("Longitude", bean.getLongitude() + "");
            inXml.getDocumentElement().appendChild(
                    inXml.importNode(nodeList.getDocumentElement(), true));
            return inXml;
        } catch (Exception ex) {
            // TODO - log exception
            if (ex instanceof YFSException) {
                throw (YFSException) ex;
            }// end:if (ex instanceof YFSException)
             //
            final YFSException yfe = new YFSException();
            yfe.setErrorCode("KSN-100");
            yfe.setErrorDescription(ex.getMessage());
            yfe.setStackTrace(ex.getStackTrace());
            throw yfe;
        }
    }// getSurroundingNodeList(YFSEnvironment, Document):Document

    /**
     * A helper method that gets the sourcing rule associated with the
     * fulfillment type in the input
     * 
     * @param env
     *            handle to the environment
     * @param inXml
     *            document containing the fulfillment type and org code
     * @return a document contain the nodes found by the sourcing rule
     * @throws YFSException
     *             if processing errors occur
     */
    private static final Document getNodeList(final YFSEnvironment env,
            final KohlsSurroundNodeListBean bean) throws YFSException {
        //
        List<Object> params = null;
        String sql = null;
        Connection conn = null;
        Document list = null;
        NodeList nodes = null;
        Element node = null;
        List<Node> deletes = null;
        double distance = 0.0;
        double latitude = 0.0;
        double longitude = 0.0;
        DecimalFormat format = null;
        try {
            format = new DecimalFormat("###0.0000");
            params = new ArrayList<Object>();
            params.add(bean.getFulfillType());
            //params.add(bean.getNodeType());
            sql = getSurroundingListSql();
            conn = getConnection(env);
            //
            list = sqlToDocument(conn, sql, params, "NodeList", "Node");
            nodes = list.getElementsByTagName("Node");
            deletes = new ArrayList<Node>();
            for (int i = 0; i < nodes.getLength(); i++) {
                node = (Element) nodes.item(i);
                latitude = (new Double(node.getAttribute("Latitude")))
                        * Math.PI / 180.0;
                longitude = (new Double(node.getAttribute("Longitude")))
                        * Math.PI / 180.0;
                //
                double a = Math.pow(
                        Math.sin((latitude - bean.getLatRadians()) / 2), 2);
                double b = Math.cos(latitude) * Math.cos(bean.getLatRadians());
                double c = Math.pow(
                        Math.sin((longitude - bean.getLongRadians()) / 2), 2);
                distance = 2 * Math.asin(Math.sqrt(a + b * c)) * 1.15078 * 180
                        * 60 / Math.PI;
                //
                if (distance > bean.getDistanceToConsider()) {
                    deletes.add(node);
                    continue;
                }// end: if (distance > bean.getDistanceToConsider())
                 //
                node.setAttribute("DistanceFromShipToAddress",
                        format.format(distance));
            }// end: for (int i = 0; i < nodes.getLength(); i++)
             //
            for (int i = 0; i < deletes.size(); i++) {
                list.getDocumentElement().removeChild(deletes.get(i));
            }// end: for (int i = 0; i < deletes.size(); i++)
             //
            return list;
        } finally {
            KohlsCommonAPIUtil.safeClose(conn, null, null);
        }
    }// getNodeList(YFSEnvironment, KohlsSurroundNodeList):Document

    /**
     * A method that draws a box around the ship to zip code that is distance to
     * consider in every direction. This determines the latitude and longitude
     * box to search for surrounding nodes
     * 
     * @param shipTo
     *            the ship to location with latitude and longitude
     * @param distanceToConsider
     *            the distance to consider
     * @return the input document with EastLongitude, WestLongitude,
     *         NorthLatitude, and SouthLatitude attributes added
     */
    private static final void getSearchBounds(final Document shipTo,
            final int distanceToConsider) {
        //
        Element root = null;
        double distance = -1;
        double deltaDegrees = 0;
        double latitude = 0;
        double longitude = 0;
        double northLatitude = 0;
        double southLatitude = 0;
        double radius = 0;
        double westLongitude = 0;
        double eastLongitude = 0;
        DecimalFormat format = null;
        try {
            format = new DecimalFormat("###0.0000");
            root = shipTo.getDocumentElement();
            distance = new Double(distanceToConsider);
            latitude = new Double(root.getAttribute("Latitude"));
            longitude = new Double(root.getAttribute("Longitude"));
            //
            // calculate north and south latitudes
            deltaDegrees = (distance / EARTH_RADIUS) * 180.0 / Math.PI;
            northLatitude = latitude + deltaDegrees;
            southLatitude = latitude - deltaDegrees;
            root.setAttribute("NorthLatitude", format.format(northLatitude));
            root.setAttribute("SouthLatitude", format.format(southLatitude));
            //
            // now, calculate West and East longitudes
            radius = (EARTH_RADIUS * Math.cos(latitude * Math.PI / 180.0));
            deltaDegrees = (distance / radius) * 180.0 / Math.PI;
            westLongitude = longitude - deltaDegrees;
            eastLongitude = longitude + deltaDegrees;
            root.setAttribute("WestLongitude", format.format(westLongitude));
            root.setAttribute("EastLongitude", format.format(eastLongitude));
        } finally {
        }
    }// getSearchBounds(Document, String):Document

    /**
     * A helper method that makes the input to getShipToCoordinates
     * 
     * @param bean
     *            the bean of information
     * @return the coordinates of the ship-to location
     */
    private static final Document getShipToCoordinatesInput(
            final KohlsSurroundNodeListBean bean) {
        //
        Document out = null;
        Element root = null;
        try {
            out = YFCDocument.createDocument("ZipCodeLocation").getDocument();
            root = out.getDocumentElement();
            root.setAttribute("Country", bean.getCountry().trim());
            root.setAttribute("ZipCode", bean.getZipCode().trim());
            //
            return out;
        } finally {
        }
    }// getShipToCoordinatesInput(KohlsSurroundNodeListBean):Document

    /**
     * A helper method that gets the lat/long of the ship-to address. In // *
     * receives an input document in the following format:
     * <p/>
     * 
     * <code>
     * &lt;ZipCodeLocation ZipCode=&quot;02035&quot; Country=&quot;US&quot; /&gt;
     * </code>
     * <p/>
     * The return document is of the following format:
     * <p/>
     * 
     * <code>
     * &lt;ZipCodeLocation ZipCodeLocationKey="&quot;16152&quot; Country=&quot;US&quot; ZipCode=&quot;02035&quot; Latitude=&quot;42.0631&quot; Longitude=&quot;-71.2456&quot;/&gt;<br/>
     * </code>
     * 
     * @param env
     *            handle to the environment
     * @param inXml
     *            input document
     * @return the lat/long of the ship-to store; null if not found in zip code
     *         location.
     */
    public static final Document getShipToCoordinates(final YFSEnvironment env,
            final Document inXml) throws YFSException {
        //
        Element root = null;
        List<Object> params = null;
        String country = null;
        String zipCode = null;
        Connection conn = null;
        String sql = null;
        Document out = null;
        NodeList locations = null;
        Element location = null;
        try {
            // extract params from input document
            params = new ArrayList<Object>();
            root = inXml.getDocumentElement();
            country = root.getAttribute("Country");
            zipCode = root.getAttribute("ZipCode");
            params.add(country);
            params.add(zipCode);
            sql = getShipToLocationSql();
            //
            conn = getConnection(env);
            out = sqlToDocument(conn, sql, params, "ZipCodeLocations",
                    "ZipCodeLocation");
            locations = out.getElementsByTagName("ZipCodeLocation");
            if (locations.getLength() == 0) {
                return null;
            }// end: if (locations.getLength() == 0)
             //
            location = (Element) locations.item(0);
            out.removeChild(out.getDocumentElement());
            out.appendChild(location);
            return out;
        } finally {
            KohlsCommonAPIUtil.safeClose(conn, null, null);
        }
    }// getShipToCoordinates(YFSEnvironment, Document):Document

    /**
     * A utility helper method that converts an SQL statement into a list-type
     * document
     * 
     * @param sql
     *            the sql statement to execute\
     * @params list of parameters (Integer or String)
     * @param listName
     *            the name of the grouping element for the returned resultset
     * @param rowName
     *            the element name to use for each row in the result set
     * @return the results of the query as a document
     */
    private static final Document sqlToDocument(final Connection conn,
            final String sql, List<Object> params, final String listName,
            final String rowName) {
        //
        PreparedStatement stmt = null;
        ResultSet rst = null;
        ResultSetMetaData rsmd = null;
        Document out = null;
        Element list = null;
        Element row = null;
        String attrName = null;
        Object attrValue = null;
        String value = null;
        Calendar calendar = null;
        java.sql.Timestamp jstamp = null;
        try {
            stmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject((i + 1), params.get(i));
                }// end: for (int i = 0; i < params.size(); i++)
            }// end: if (params != null)
             //
            rst = stmt.executeQuery();
            rsmd = rst.getMetaData();
            out = YFCDocument.createDocument(listName).getDocument();
            list = out.getDocumentElement();
            while (rst.next()) {
                row = out.createElement(rowName);
                list.appendChild(row);
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    attrName = rsmd.getColumnName(i + 1);
                    attrValue = rst.getObject(attrName);
                    if (attrValue != null) {
                        if (attrValue instanceof java.sql.Timestamp) {
                            jstamp = (java.sql.Timestamp) attrValue;
                            calendar = Calendar
                                    .getInstance(Locale.getDefault());
                            calendar.setTime(new Date(jstamp.getTime()));
                            value = DatatypeConverter.printDateTime(calendar);
                            row.setAttribute(attrName, value);
                        } else {
                            row.setAttribute(attrName, attrValue.toString().trim());
                        }// end: if (attrValue instanceof java.sql.Timestamp)
                    }// end: if (attrValue != null)
                }// end: for (int i = 0; i < rsmd.getColumnCount(); i++)
            }// end: while (rst.next())
            return out;
        } catch (SQLException ex) {
            // TODO -- log error
            final YFSException yfe = new YFSException();
            yfe.setErrorCode("KSTQ-100");
            yfe.setErrorDescription(ex.getMessage());
            yfe.setStackTrace(ex.getStackTrace());
            throw yfe;
        } finally {
            KohlsCommonAPIUtil.safeClose(null, stmt, rst);
        }
    }// sqlToDocument(String, List, String, String):Document

    /**
     * A utility that simultaneously closes a connection, statement, and result
     * set.
     * 
     * @param conn
     *            the connection to close
     * @param stmt
     *            the connection to close
     * @param rst
     *            the result set to close
     */
    public final static void safeClose(final Connection conn,
            final Statement stmt, final ResultSet rst) {
        //
        if (rst != null) {
            try {
                rst.close();
            } catch (SQLException ex) {
                // TODO -- replace with logging
                ex.printStackTrace();
            }
        }// end: if (rst != null)

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                // TODO -- replace with error logging
                ex.printStackTrace();
            }
        }// end: if (stmt != null)

        if (conn != null) {
            try {
                // DO NOT CLOSE MCF CONNECTIONS
            } catch (Exception ex) {
                // TODO -- replace with error logging
                ex.printStackTrace();
            }
        }// end: if (conn != null)

    }// safeClose(Connection, Statement, Resultset)

    /**
     * A method that gets a connection from the environment
     * 
     * @param env
     *            the environment
     * @return the underlying connection object
     */
    private static final Connection getConnection(final YFSEnvironment env)
            throws YFSException {
        //
        YFSConnectionHolder holder = null;
        try {
            holder = (YFSConnectionHolder) env;
            return holder.getDBConnection();
        } finally {
        }
    }// getConnection(YFSEnvironment):Connection

    /**
     * A helper method that builds the SQL that gets the co-ordinates of the
     * ship-to-zip code
     * 
     * @return the sql used to get the co-ordinates of the ship-to zip code
     */
    private static final String getShipToLocationSql() {
        //
        StringBuffer sql = null;
        try {
            sql = new StringBuffer();
            //
            sql.append("select ");
            sql.append("z.zip_code_location_key \"ZipCodeLocationKey\"");
            sql.append(", z.country \"Country\"");
            sql.append(", z.zip_code \"ZipCode\"");
            sql.append(", z.latitude \"Latitude\"");
            sql.append(", z.longitude \"Longitude\" ");
            sql.append("from ");
            sql.append("yfs_zip_code_location z ");
            sql.append("where ");
            sql.append("z.country = ? ");
            sql.append("and z.zip_code = ?");
            //
            return sql.toString();
        } finally {
        }
    }// getShipToLocationSql:String

    /**
     * A helper method that creates the input to get all nodes based on the
     * fulfillment type
     * 
     * @return the sql to get surrounding node list
     */
    private static final String getSurroundingListSql() {
        //
        StringBuffer sql = null;
        try {
            sql = new StringBuffer();
            //
            sql.append("select distinct ");
            sql.append("n.shipnode_key \"ShipNodeKey\"");
            sql.append(", n.ship_node \"ShipNode\"");
            sql.append(", n.description \"Description\"");
            sql.append(", n.localecode \"Localecode\"");
            sql.append(", n.node_type \"NodeType\"");
            sql.append(", p.city \"City\"");
            sql.append(", p.state \"State\"");
            sql.append(", p.zip_code \"ZipCode\"");
            sql.append(", p.country \"Country\"");
            sql.append(", z.latitude \"Latitude\"");
            sql.append(", z.longitude \"Longitude\" ");
            sql.append("from ");
            sql.append("yfs_sourcing_rule_hdr h");
            sql.append(", yfs_sourcing_rule_dtl d");
            sql.append(", yfs_item_ship_node i");
            sql.append(", yfs_ship_node n");
            sql.append(", yfs_person_info p");
            sql.append(", yfs_zip_code_location z ");
            sql.append("where ");
            sql.append("h.sourcing_rule_hdr_key = d.sourcing_rule_hdr_key ");
            sql.append("and d.distribution_rule_id = i.distribution_rule_id ");
            sql.append("and i.shipnode_key = n.shipnode_key ");
            sql.append("and n.ship_node_address_key = p.person_info_key ");
            sql.append("and z.country = p.country ");
            sql.append("and z.zip_code = p.zip_code ");
            sql.append("and i.effective_start_date <= SYSDATE ");
            sql.append("and i.effective_end_date >= SYSDATE ");
            sql.append("and h.fulfillment_type = ? ");
            //sql.append("and n.node_type = ? ");
            //
            return sql.toString();
        } finally {
        }
    }// getSurroundiingListSql:String
    
    
    public static Document prepareGetetSurroundingNodeListAPIInput(YFSEnvironment env, int radius, Element shipToAddress) throws Exception {
		Document inputDoc = SCXmlUtil.createDocument(KohlsConstants.GET_SURROUNDING_NODE_LIST);

		Element inElement = inputDoc.getDocumentElement();
		inElement.setAttribute(KohlsConstants.DISTANCE_TO_CONSIDER, String.valueOf(radius));
		inElement.setAttribute(KohlsConstants.DISTANCE_TO_CONSIDER_UOM, KohlsConstants.MILE);
		inElement.setAttribute(KohlsConstants.NODE_TYPE, KohlsConstants.STORE);
		inElement.setAttribute(KohlsConstants.FULFILLMENT_TYPE,KohlsConstants.PRODUCTSOURCING);
		inElement.setAttribute(KohlsConstants.ORGANIZATION_CODE, ((YFSContext) env).getUEParam(KohlsConstants.ENTERPRISE_CODE));

		Element shipToEle = inputDoc.createElement(KohlsConstants.SHIP_TO_ADDRESS);
		inElement.appendChild(shipToEle);
		shipToEle.setAttribute(KohlsConstants.CITY, shipToAddress.getAttribute(KohlsConstants.CITY));
		shipToEle.setAttribute(KohlsConstants.STATE, shipToAddress.getAttribute(KohlsConstants.STATE));
		shipToEle.setAttribute(KohlsConstants.COUNTRY, shipToAddress.getAttribute(KohlsConstants.COUNTRY));
		shipToEle.setAttribute(KohlsConstants.ZIPCODE, shipToAddress.getAttribute(KohlsConstants.ZIPCODE));
		System.out.println(KohlsXMLUtil.getXMLString(inputDoc));
		//Document outputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_SURROUNDING_NODE_LIST_TEMPLATE, KohlsConstants.GETSURROUNDING_NODE_LIST, inputDoc);

		return inputDoc;
	}
    
    /*
	 * Wrapper for the getSurroundingNodeList - END
	 */
	
    
    /**
     * A helper method that gets the lat/long of the ship-to address. In // *
     * receives an input document in the following format:
     * <p/>
     * 
     * <code>
     * &lt;ZipCodeLocation ZipCode=&quot;02035&quot; Country=&quot;US&quot; /&gt;
     * </code>
     * <p/>
     * The return document is of the following format:
     * <p/>
     * 
     * <code>
     * &lt;ZipCodeLocation ZipCodeLocationKey="&quot;16152&quot; Country=&quot;US&quot; ZipCode=&quot;02035&quot; Latitude=&quot;42.0631&quot; Longitude=&quot;-71.2456&quot;/&gt;<br/>
     * </code>
     * 
     * @param env
     *            handle to the environment
     * @param inXml
     *            input document
     * @return the lat/long of the ship-to store; null if not found in zip code
     *         location.
     */
    public static final Document getCoordinates(final YFSEnvironment env,
            final Document inXml) throws YFSException {
        //
        Element root = null;
        List<Object> params = null;
        String country = null;
        String zipCode = null;
        Connection conn = null;
        String sql = null;
        Document out = null;
        NodeList locations = null;
        Element location = null;
        try {
            // extract params from input document
            params = new ArrayList<Object>();
            root = inXml.getDocumentElement();
            country = root.getAttribute("Country");
            zipCode = root.getAttribute("ZipCode");
            params.add(country);
            params.add(zipCode);
            sql = getShipToLocationSql();
            //
            conn = getConnection(env);
            out = sqlToDocument(conn, sql, params, "ZipCodeLocations",
                    "ZipCodeLocation");
            locations = out.getElementsByTagName("ZipCodeLocation");
            if (locations.getLength() == 0) {
                return null;
            }// end: if (locations.getLength() == 0)
             //
            location = (Element) locations.item(0);
            out.removeChild(out.getDocumentElement());
            out.appendChild(location);
            return out;
        } finally {
            KohlsCommonAPIUtil.safeClose(conn, null, null);
        }
    }// getShipToCoordinates(YFSEnvironment, Document):Document
    
    public static double getDistance(double dblToLatitude, double dblToLongitude, double dblFromLatitude, double dblFromLongitude){
    	double dblToLatitudeRad =  dblToLatitude*Math.PI / 180.0;
    	double dblToLongitudeRad = dblToLongitude*Math.PI / 180.0;
    	
    	
    	double dblFromLatitudeRad =  dblFromLatitude*Math.PI / 180.0;
    	double dblFromLongitudeRad = dblFromLongitude*Math.PI / 180.0;

    	double a = Math.pow(Math.sin((dblToLatitudeRad - dblFromLatitudeRad) / 2), 2);
    	double b = Math.cos(dblToLatitudeRad) * Math.cos(dblToLatitudeRad);
    	double c = Math.pow(Math.sin((dblToLongitudeRad - dblFromLongitudeRad) / 2), 2);
    	double distance = 2 * Math.asin(Math.sqrt(a + b * c)) * 1.15078 * 180* 60 / Math.PI;
    	return distance;
    }
}
