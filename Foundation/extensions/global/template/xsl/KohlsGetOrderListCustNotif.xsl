<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/">
	<content>
	  <xsl:variable name="varCustNotif" select="Order/@CustNotificationType"/>
	  <customer emailaddress="{Order/@CustomerEMailID}" firstname="{Order/PersonInfoBillTo/@FirstName}" lastname="{Order/PersonInfoBillTo/@LastName}">	     
		 <xsl:if test="$varCustNotif = 'CUSTOMER_PICKEDUP'">							
				<xsl:attribute name="creditcard">
				    <xsl:value-of select="Order/PaymentMethods/PaymentMethod/@CreditCardNo"/>
				</xsl:attribute>
		 </xsl:if>
	     <order number="{Order/@OrderNo}" orderdate="{Order/@OrderDate}" status="{Order/@Status}">
				<xsl:if test="$varCustNotif = 'PICKUP_REMINDER' or $varCustNotif = 'ORDER_READY_FOR_PICK' or $varCustNotif = 'FINAL_PICKUP_REMINDER'">							
				<xsl:attribute name="pickupdeadline">
				    <xsl:value-of select="Order/OrderLines/OrderLine/@ReqCancelDate"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$varCustNotif = 'PICKUP_REMINDER' or $varCustNotif = 'ORDER_READY_FOR_PICK' or $varCustNotif = 'FINAL_PICKUP_REMINDER'">							
				<xsl:attribute name="receiptID">
				    <xsl:value-of select="Order/PersonInfoBillTo/@FirstName"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$varCustNotif = 'ORDER_DELAY_NOTICE'">							
				<xsl:attribute name="statusreason">
				    <xsl:value-of select="'TBD'"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$varCustNotif = 'ORDER_MODIFICATION' or 'ORDER_CANCELLATION'">							
				<xsl:attribute name="total">
				    <xsl:value-of select="Order/OrderAudit/OrderAuditLevels/OrderAuditLevel[@ModificationLevel = 'ORDER']/OrderAuditDetails/OrderAuditDetail/Attributes/Attribute[@Name= 'TotalAmount']/@OldValue"/>
				</xsl:attribute>							
				<xsl:attribute name="retotal">
				    <xsl:value-of select="Order/OrderAudit/OrderAuditLevels/OrderAuditLevel[@ModificationLevel = 'ORDER']/OrderAuditDetails/OrderAuditDetail/Attributes/Attribute[@Name= 'TotalAmount']/@OldValue"/>
				</xsl:attribute>							
				<xsl:attribute name="subtotal">
				    <xsl:value-of select="'TBD'"/>
				</xsl:attribute>							
				<xsl:attribute name="tax">
				    <xsl:value-of select="'TBD'"/>
				</xsl:attribute>							
				<xsl:attribute name="discount">
				    <xsl:value-of select="'TBD'"/>
				</xsl:attribute>							
				<xsl:attribute name="resubtotal">
				    <xsl:value-of select="'TBD'"/>
				</xsl:attribute>							
				<xsl:attribute name="retax">
				    <xsl:value-of select="'TBD'"/>
				</xsl:attribute>							
				<xsl:attribute name="rediscount">
				    <xsl:value-of select="'TBD'"/>
				</xsl:attribute>							
				<xsl:attribute name="statusreason">
				    <xsl:value-of select="'TBD'"/>
				</xsl:attribute>
			</xsl:if>	
			<xsl:variable name="varOCFInd" select="Order/OrderLines/OrderLine/Extn/@ExtnOCF"/>
			<xsl:if test="$varOCFInd = 'BPS'">
				<xsl:attribute name="ocfind">
					<xsl:value-of select="'BPS'"/>
				</xsl:attribute>
			</xsl:if>				
		    <pickupstore address1="{Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@AddressLine1}" address2="{Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@AddressLine2}" city="{Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@City}" hours="{Order/OrderLines/OrderLine/Shipnode/@StoreHours}" pincode="{Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@ZipCode}" state="{Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@State}">
			<xsl:if test="$varCustNotif = 'CUSTOMER_PICKEDUP'">							
				<xsl:attribute name="time">
				    <xsl:value-of select="Order/OrderLines/OrderLine/OrderStatuses/OrderStatus[@StatusDescription = 'Customer Picked Up']/@StatusDate"/>
				</xsl:attribute>
			</xsl:if>
			</pickupstore>
			<lineitems>
			 <xsl:for-each select="/Order/OrderLines/OrderLine">
			     <lineitem color="{Item/Extn/@ExtnColorDesc}" qty="{@OrderedQty}" size="{Item/Extn/@ExtnSizeDesc}" skudescr="{Item/@ItemShortDesc}" skunumber="{Item/@ItemID}">
				 
					<xsl:if test="$varCustNotif = 'PICKUP_REMINDER' or $varCustNotif = 'ORDER_READY_FOR_PICK' or $varCustNotif = 'FINAL_PICKUP_REMINDER'">							
						<xsl:attribute name="packlisttyp">
							<xsl:value-of select="@PackListType"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$varCustNotif = 'ORDER_DELAY_NOTICE'">							
						<xsl:attribute name="status">
							<xsl:value-of select="Order/OrderLines/OrderLine/@Status"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$varCustNotif = 'ORDER_MODIFICATION' or 'ORDER_CANCELLATION'">							
						<xsl:attribute name="totalprice">
							<xsl:value-of select="OrderAudit/OrderAuditLevels/OrderAuditLevel[@ModificationLevel = 'ORDER_LINE']/OrderAuditDetails/OrderAuditDetail/Attributes/Attribute[@Name= 'LineTotal']/@OldValue"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$varCustNotif = 'ORDER_MODIFICATION' or 'ORDER_CANCELLATION'">							
						<xsl:attribute name="retotalprice">
							<xsl:value-of select="OrderAudit/OrderAuditLevels/OrderAuditLevel[@ModificationLevel = 'ORDER_LINE']/OrderAuditDetails/OrderAuditDetail/Attributes/Attribute[@Name= 'LineTotal']/@OldValue"/>
						</xsl:attribute>
					</xsl:if>
				  </lineitem>
			 </xsl:for-each>
			</lineitems>
		 </order>
		 <xsl:if test="$varCustNotif = 'CUSTOMER_PICKEDUP'">
			 <billto address1="{Order/PersonInfoBillTo/@AddressLine1}" address2="{Order/PersonInfoBillTo/@AddressLine2}" city="{Order/PersonInfoBillTo/@City}" pincode="{Order/PersonInfoBillTo/@ZipCode}" state="{Order/PersonInfoBillTo/@State}" >
			 </billto>
		 </xsl:if>
	  </customer>
	</content>
	</xsl:template>
</xsl:stylesheet>