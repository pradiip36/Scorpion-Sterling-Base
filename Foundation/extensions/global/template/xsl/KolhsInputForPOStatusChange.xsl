<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" indent="yes" />
	
   <xsl:template match="/">
   	<xsl:variable name="ackStatus" select="/messageAck/messageDisposition/@status"/>
  	<OrderStatusChange>
   	<xsl:attribute name="TransactionId">ORDER_STATUS_CHANGE.0005.ex</xsl:attribute>
        <xsl:attribute name="DocumentType">0005</xsl:attribute>
        <xsl:attribute name="OrderNo"><xsl:value-of select="/messageAck/trxID"/></xsl:attribute>
        <xsl:attribute name="EnterpriseCode">KOHLS.COM</xsl:attribute>
        <xsl:attribute name="BaseDropStatus">
        		<xsl:if test="$ackStatus = 'A' ">3300.003</xsl:if>
        		<xsl:if test="$ackStatus = 'D' ">3300.002</xsl:if>
        </xsl:attribute>

   	</OrderStatusChange>
   </xsl:template>
</xsl:stylesheet>


 