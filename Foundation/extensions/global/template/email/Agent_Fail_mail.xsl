<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:lxslt="http://xml.apache.org/xslt" version="1.0">
<xsl:template match="/">
   <HTML>
      <xsl:comment>RECIPIENTS=
      <xsl:value-of select="/EmailNotification/@ToAddress" />

      <xsl:value-of select="/EmailNotification/@CcAddress" />

      <xsl:value-of select="/EmailNotification/@BccAddress" />
      </xsl:comment>

      <xsl:comment>FROM=sales@yourcompany.com</xsl:comment>

      <xsl:comment>SUBJECT=Agent has failed</xsl:comment>

      <xsl:comment>CONTENT_TYPE=text/html</xsl:comment>

      <HEAD>
         <title>Agent Failed</title>
      </HEAD>

      <BODY topmargin="0" leftmargin="0">
         <BR />

         <BR />

         <font>Attention!</font>

         <BR />

         <BR />

         <font>Your agent 
         <xsl:value-of select="/EmailNotification/@ItemEligibilityAgent" />

         has failed. Please take the required actions.
         <BR />
         <BR/><BR/>Thank you.
         </font>
      </BODY>
   </HTML>
</xsl:template>
</xsl:stylesheet>