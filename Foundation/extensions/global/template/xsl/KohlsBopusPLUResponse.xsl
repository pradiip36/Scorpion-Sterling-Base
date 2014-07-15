<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:template match="/">
		<xsl:choose>
			<xsl:when test="S:Fault" xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
				<xsl:element name="Errors">
					<xsl:element name="Error">
						<xsl:attribute name="ErrorCode">
							<xsl:value-of select="S:Fault/detail/code" xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"/>
						</xsl:attribute>
						<xsl:attribute name="ErrorDescription">
							<xsl:value-of select="S:Fault/detail/message" xmlns:S="http://schemas.xmlsoap.org/soap/envelope/"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- root template  -->
	<xsl:template match="*">
		<xsl:element name="{local-name(.)}">
			<xsl:apply-templates select="@* | node()"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="@*">
		<xsl:attribute name="{local-name(.)}">
			<xsl:value-of select="."/>
		</xsl:attribute>
	</xsl:template>




	<xsl:template match="Parameters">
		<Parameters>
			<xsl:for-each select="@*">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>

			<xsl:for-each select="*">
				<xsl:attribute name="{name()}">
					<xsl:value-of select="text()"/>
				</xsl:attribute>

			</xsl:for-each>
		</Parameters>
	</xsl:template>

	<xsl:template match="RECORD">
		<RECORD>
			<xsl:for-each select="@*">
				<xsl:attribute name="{local-name(.)}">
					<xsl:value-of select="."/>
				</xsl:attribute>
			</xsl:for-each>

			<xsl:for-each select="*">
				<xsl:attribute name="{name()}">
					<xsl:value-of select="text()"/>
				</xsl:attribute>

			</xsl:for-each>
		</RECORD>
	</xsl:template>





</xsl:stylesheet>
