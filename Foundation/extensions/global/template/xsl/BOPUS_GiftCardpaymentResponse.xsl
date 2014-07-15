<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:s="http://schemas.xmlsoap.org/soap/envelope/"
 xmlns:ns1="http://giftcard.poc.service.kohls.com/GiftCardService/"
 exclude-result-prefixes="ns1"
>
	<xsl:output method="xml" encoding="UTF-8" />
	<xsl:template match="/" >
		<xsl:choose>
			<xsl:when test="soap:Body/soap:Fault" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope" >
				<FaultMessage >
					<xsl:attribute name="faultcode">
						<xsl:value-of select="soap:Body/soap:Fault/faultcode"   xmlns:soap="http://schemas.xmlsoap.org/soap/envelope"  xmlns:ns2="http://pg.kohls.com"  />
					</xsl:attribute>
					<xsl:attribute name="faultstring">
						<xsl:value-of select="soap:Body/soap:Fault/faultstring"   xmlns:soap="http://schemas.xmlsoap.org/soap/envelope"  xmlns:ns2="http://pg.kohls.com" />
					</xsl:attribute>
					<xsl:attribute name="code">
						<xsl:value-of select="soap:Body/soap:Fault/detail/ns2:paymentGatewayFault/ns2:code"   xmlns:soap="http://schemas.xmlsoap.org/soap/envelope"  xmlns:ns2="http://pg.kohls.com"  />
					</xsl:attribute>
					<xsl:attribute name="message">
						<xsl:value-of select="soap:Body/soap:Fault/detail/ns2:paymentGatewayFault/ns2:message"    xmlns:soap="http://schemas.xmlsoap.org/soap/envelope"  xmlns:ns2="http://pg.kohls.com" />
					</xsl:attribute>
					<!--<xsl:apply-templates></xsl:apply-templates>--></FaultMessage>
			</xsl:when>
			<xsl:otherwise>
				<PaymentResponse>
					<xsl:attribute name="ApprovalNumber" >
						<xsl:value-of select="/s:Envelope/s:Body/ns1:paymentResponse/ns1:authCode"  xmlns:ns1="http://giftcard.poc.service.kohls.com/GiftCardService/"/>
					</xsl:attribute>
					<xsl:attribute name="AuthID">
						<xsl:value-of select="/s:Envelope/s:Body/ns1:paymentResponse/ns1:authID"  xmlns:ns1="http://giftcard.poc.service.kohls.com/GiftCardService/" />
					</xsl:attribute>
					<xsl:attribute name="AuthResponse">
						<xsl:value-of select="/s:Envelope/s:Body/ns1:paymentResponse/ns1:authResponse"   xmlns:ns1="http://giftcard.poc.service.kohls.com/GiftCardService/" />
					</xsl:attribute>
					<xsl:attribute name="AuthSource">
						<xsl:value-of select="/s:Envelope/s:Body/ns1:paymentResponse/ns1:authSource"  xmlns:ns1="http://giftcard.poc.service.kohls.com/GiftCardService/"/>
					</xsl:attribute>
					<!-- <xsl:attribute name="ResponseType"><xsl:value-of select="ns1:paymentResponse/ns1:Request-Type"  xmlns:ns1="http://giftcard.poc.service.kohls.com/GiftCardService/"/></xsl:attribute> --><xsl:attribute name="RemainingBalance">
						<xsl:value-of select="/s:Envelope/s:Body/ns1:paymentResponse/ns1:remainingBalance"  xmlns:ns1="http://giftcard.poc.service.kohls.com/GiftCardService/"/>
					</xsl:attribute>
					<xsl:attribute name="ApprovedAmount">
						<xsl:value-of select="/s:Envelope/s:Body/ns1:paymentResponse/ns1:approvedAmount"  xmlns:ns1="http://giftcard.poc.service.kohls.com/GiftCardService/"/>
					</xsl:attribute>
				</PaymentResponse>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>