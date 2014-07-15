<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	<xsl:template match="/OrderLines">
   <Order >
				
         			<xsl:attribute name="OrderNo">
            		<xsl:text> </xsl:text>
         			</xsl:attribute>
         			<xsl:attribute name="DocumentType">
            			<xsl:text>0006</xsl:text>
         			</xsl:attribute>
         			<xsl:attribute name="OrderType">
            			<xsl:text>Transfer Order</xsl:text>
         			</xsl:attribute>
					<xsl:attribute name="EnteredBy">
            			<xsl:value-of select="./OrderLines/@test"/>
         			</xsl:attribute>
					<xsl:attribute name="EnterpriseCode">
            			<xsl:text>KOHLS.COM</xsl:text>
         			</xsl:attribute>
         			<xsl:attribute name="SellerOrganizationCode">
            			<xsl:text> </xsl:text>
         			</xsl:attribute>
         			<xsl:attribute name="BuyerOrganizationCode">
            			<xsl:text> </xsl:text>
         			</xsl:attribute>
         		<OrderLines>
         		<xsl:for-each select="OrderLine">
         		<OrderLine>
         			<xsl:attribute name="OrderedQty">
         				<xsl:value-of select="@Qty"/>
         			</xsl:attribute>
         			<xsl:attribute name="ReceivingNode">
         				<xsl:value-of select="@ReceivingNode"/>
         			</xsl:attribute>
         			<xsl:attribute name="ShipNode">
         				<xsl:value-of select="@ShipNode"/>
         			</xsl:attribute>
					<xsl:attribute name="PackListType">
         				<xsl:value-of select="position()"/>
         			</xsl:attribute>
         			<Item>
         				<xsl:attribute name="ItemID">
         					<xsl:value-of select="@ItemID"/>
         				</xsl:attribute>
         				<xsl:attribute name="ProductClass">
         						<xsl:text>Good</xsl:text>
         				</xsl:attribute>
         				<xsl:attribute name="UnitOfMeasure">
         					<xsl:text>EACH</xsl:text>
         				</xsl:attribute>
         			</Item>
 				</OrderLine>
         			</xsl:for-each>
         		
         		</OrderLines>	
				<PersonInfoShipTo>
					<xsl:attribute name="Country">
         					<xsl:text>US</xsl:text>
         			</xsl:attribute>
				</PersonInfoShipTo>
				<PersonInfoBillTo>
					<xsl:attribute name="Country">
         					<xsl:text>US</xsl:text>
         			</xsl:attribute>
				</PersonInfoBillTo>
				
	</Order>
</xsl:template>
</xsl:stylesheet>
