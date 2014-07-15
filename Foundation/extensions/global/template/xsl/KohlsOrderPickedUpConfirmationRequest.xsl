<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:conf="http://com/kohls/ch/cc/services/webservice/notification/conf" 
				xmlns:web="http://webservice.services.cc.ch.kohls.com">
    <xsl:output method="xml" indent="yes"/>
    <xsl:template match="/">
	<xsl:variable name="varCustNotif" select="Order/@CustNotificationType"/>
	<conf:OrderPickedUpConfirmationRequest>
    <conf:content>
    <web:order xmlns:web='http://webservice.services.cc.ch.kohls.com'>
		<web:customer>
			<web:listName>
				<web:folderName></web:folderName>
				<web:objectName></web:objectName>
			</web:listName>
			
			<web:customerId></web:customerId>
			<web:firstName><xsl:value-of select="Order/PersonInfoBillTo/@FirstName"/></web:firstName>
			<web:lastName><xsl:value-of select="Order/PersonInfoBillTo/@LastName"/></web:lastName>
			<web:mobileNumber></web:mobileNumber>
			<web:emailAddress><xsl:value-of select="Order/@CustomerEMailID"/></web:emailAddress>
			<web:emailFormat></web:emailFormat>
			<web:cardNum><xsl:value-of select="Order/PaymentMethods/PaymentMethod/@CreditCardNo"/></web:cardNum>
			<web:billToAddress1><xsl:value-of select="Order/PersonInfoBillTo/@AddressLine1"/></web:billToAddress1>
			<web:billToAddress2><xsl:value-of select="Order/PersonInfoBillTo/@AddressLine2"/></web:billToAddress2>
			<web:billToCity><xsl:value-of select="Order/PersonInfoBillTo/@City"/></web:billToCity>
			<web:billToZip><xsl:value-of select="Order/PersonInfoBillTo/@ZipCode"/></web:billToZip>
			<web:billToState><xsl:value-of select="Order/PersonInfoBillTo/@State"/></web:billToState>
		</web:customer>

		<web:orderDate><xsl:value-of select="Order/@OrderDate"/></web:orderDate>
		<web:orderNumber><xsl:value-of select="Order/@OrderNo"/></web:orderNumber>
		<web:orderStatus><xsl:value-of select="Order/@Status"/></web:orderStatus>
		<web:ocfIndicator><xsl:value-of select="Order/OrderLines/OrderLine/Extn/@ExtnOCF"/></web:ocfIndicator>
		<web:delayReason></web:delayReason>
		<web:cancelReason></web:cancelReason>
		<web:modificationReason></web:modificationReason>
		<web:refundReason></web:refundReason>
		<web:pickupTime><xsl:value-of select="Order/OrderLines/OrderLine/OrderStatuses/OrderStatus[@StatusDescription = 'Customer Picked Up']/@StatusDate"/></web:pickupTime>
		<web:orderReceiptId></web:orderReceiptId><!--dateTime-->
		<web:pickupDeadline><xsl:value-of select="Order/OrderLines/OrderLine/@ReqCancelDate"/></web:pickupDeadline>

		<web:pickupStore>
				<web:pickupAddress1><xsl:value-of select="Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@AddressLine1"/></web:pickupAddress1>
				<web:pickupAddress2><xsl:value-of select="Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@AddressLine2"/></web:pickupAddress2>
				<web:pickupCity><xsl:value-of select="Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@City"/></web:pickupCity>
				<web:pickupStateCd><xsl:value-of select="Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@State"/></web:pickupStateCd>
				<web:pickupPostal></web:pickupPostal>
				<web:pickupStoreHours><xsl:value-of select="Order/OrderLines/OrderLine/Shipnode/@StoreHours"/></web:pickupStoreHours>
				<web:pickupStoreZip><xsl:value-of select="Order/OrderLines/OrderLine/Shipnode/ShipNodePersonInfo/@ZipCode"/></web:pickupStoreZip>
				<web:storeName>
					<xsl:value-of
					select="Order/OrderLines/OrderLine/Shipnode/@Description" />
				</web:storeName>
			</web:pickupStore>
			    <web:paymentDetails>
                  <!--Optional:-->
                  <web:paymentType></web:paymentType>
                  <!--Optional:-->
                  <web:cardHolderName></web:cardHolderName>
                  <!--Optional:-->
                  <web:cardBrand></web:cardBrand>
                  <!--Optional:-->
                  <web:creditCard></web:creditCard>
                  <!--Optional:-->
                  <web:amtChargd></web:amtChargd>
    	</web:paymentDetails>	
		<xsl:for-each select="/Order/OrderLines/OrderLine">
			<web:products>
				<web:skuCode><xsl:value-of select="Item/@ItemID"/></web:skuCode>
				<web:skuPacklistTyp><xsl:value-of select="@PackListType"/></web:skuPacklistTyp>
				<web:name><xsl:value-of select="Item/@ItemShortDesc"/></web:name>
				<web:status><xsl:value-of select="@Status"/></web:status>
				<web:color><xsl:value-of select="Item/Extn/@ExtnColorDesc"/></web:color>
				<web:size><xsl:value-of select="Item/Extn/@ExtnSizeDesc"/></web:size>
				<web:qty><xsl:value-of select="round(@OrderedQty)"/></web:qty>
				<web:yourPrice></web:yourPrice>
				<web:totalPrice></web:totalPrice>
				<web:reYourPrice></web:reYourPrice>
				<web:reTotalPrice></web:reTotalPrice>
				<web:liStatus><xsl:value-of select="@Status"/></web:liStatus>
                <xsl:choose>
                    <xsl:when test="@GiftFlag">
                        <web:liGiftind><xsl:value-of select="@GiftFlag"/></web:liGiftind>
                    </xsl:when>
                    <xsl:otherwise>
                        <web:liGiftind>N</web:liGiftind>
					</xsl:otherwise>
				</xsl:choose>
				<web:skuDescription><xsl:value-of select="Item/@ItemShortDesc"/></web:skuDescription>
				<web:skuNumber><xsl:value-of select="Item/@ItemID"/></web:skuNumber>
			</web:products>
		</xsl:for-each>
			
			<web:subtotalAmt></web:subtotalAmt>
			<web:totalTaxAmt></web:totalTaxAmt>
			<web:discountAmt></web:discountAmt>
			<web:totalAmt><xsl:value-of select="Order/OrderAudit/OrderAuditLevels/OrderAuditLevel[@ModificationLevel = 	
					'ORDER']/OrderAuditDetails/OrderAuditDetail/Attributes/Attribute[@Name= 'TotalAmount']/@OldValue"/>
			</web:totalAmt>
			<web:reSubtotalAmt></web:reSubtotalAmt>
			<web:reTotalTaxAmt></web:reTotalTaxAmt>
			<web:reDiscountAmt></web:reDiscountAmt>
			<web:reTotalAmt><xsl:value-of select="Order/OrderAudit/OrderAuditLevels/OrderAuditLevel[@ModificationLevel =
				'ORDER']/OrderAuditDetails/OrderAuditDetail/Attributes/Attribute[@Name= 'TotalAmount']/@OldValue"/>
			</web:reTotalAmt>
		</web:order>	
		<web:campaign xmlns:web='http://webservice.services.cc.ch.kohls.com'>
			<web:folderName>Order_Pickedup_Confirmation</web:folderName>
			<web:objectName>Order_Pickedup_Confirmation_Test</web:objectName>
		</web:campaign>
		<web:priorityLevel xmlns:web='http://webservice.services.cc.ch.kohls.com'>9</web:priorityLevel>
		</conf:content>
		</conf:OrderPickedUpConfirmationRequest>
	</xsl:template>
</xsl:stylesheet>
