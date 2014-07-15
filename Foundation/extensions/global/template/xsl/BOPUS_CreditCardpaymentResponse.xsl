<?xml version="1.0" encoding="UTF-8"?>
<!-- This xsl is used to create xml of type ProcessTransactionResponse with namespace "http://pg.kohls.com" -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8"/>
	<!-- root template -->
	<xsl:template match="/">
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
				<ProcessTransactionResponse>
					<Response>
						<xsl:attribute name="ActionCode">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:response/ns2:actionCode" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
					</Response>
					<Transaction>
						<xsl:attribute name="DateTime">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:dateTime" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="BankResponseCode">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:bankResponseCode" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="DepositData">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:depositData" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="RetrievalRefNumber">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:retrievalRefNumber" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="SequenceNumber">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:sequenceNumber" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="PostingDate">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:postingDate" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="PS2000Data">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:ps2000Data" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="NetworkId">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:networkId" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="CustomerName">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:customerName" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="TraceAuditNumber">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:traceAuditNumber" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="Amount">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:amount" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="CvvResp">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:cvvResp" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="CardLevel">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:cardLevel" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="SwipeData">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:swipeData" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="ApprovalNumber">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:approvalNumber" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="PosEcho">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:posEcho" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="Reversal">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:reversal" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="DebitAckRespMessage">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:debitAckRespMessage" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="UserData">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:userData" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="AVSResp">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:avsResp" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
						<xsl:attribute name="Options">
							<xsl:value-of select="ns2:processTransactionResponse/ns2:transaction/ns2:options" xmlns:ns2="http://pg.kohls.com"/>
						</xsl:attribute>
					</Transaction>
				</ProcessTransactionResponse>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>