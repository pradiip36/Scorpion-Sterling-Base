<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:ns2="http://pg.kohls.com"
    xmlns:ns3="urn:kohls:xml:schemas:message-header:v1_0"
    version="1.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes" />
    
    
    <!-- root template  -->
    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="ProcessTransactionRequest/DebitAck/@DebitAckReqMessage">
                <ns2:processTransactionRequest>
                    <ns2:debitAck>
                        <ns2:debitAckReqMessage>
                            <xsl:value-of select="ProcessTransactionRequest/DebitAck/@DebitAckReqMessage"/>
                        </ns2:debitAckReqMessage>
                    </ns2:debitAck>
                </ns2:processTransactionRequest>
           </xsl:when>
            <xsl:otherwise>
                <ns2:processTransactionRequest>
                    <ns2:request>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Request/@Channel"(<string>) to "ns2:channel"(<string>) -->
                        <ns2:channel>
                            <xsl:value-of select="ProcessTransactionRequest/Request/@Channel"/>
                        </ns2:channel>
                    </ns2:request>
                    <ns2:token>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Token/@CardNumber"(string) to "ns2:cardNumber"(<string>) -->
                        <ns2:cardNumber>
                            <xsl:value-of select="ProcessTransactionRequest/Token/@CardNumber"/>
                        </ns2:cardNumber>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Token/@Type"(<string>) to "ns2:type"(<string>) -->
                        <ns2:type>
                            <xsl:value-of select="ProcessTransactionRequest/Token/@Type"/>
                        </ns2:type>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Token/@Provider"(<string>) to "ns2:provider"(<string>) -->
                        <ns2:provider>
                            <xsl:value-of select="ProcessTransactionRequest/Token/@Provider"/>
                        </ns2:provider>
                    </ns2:token>
                    <ns2:transaction>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@RequestType"(<string>) to "ns2:requestType"(<string>) -->
                        <ns2:requestType>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@RequestType"/>
                        </ns2:requestType>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@TransactionType"(<string>) to "ns2:transactionType"(<string>) -->
                        <ns2:transactionType>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@TransactionType"/>
                        </ns2:transactionType>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@TransactionRequestType"(<string>) to "ns2:transactionRequestType"(<string>) -->
                        <ns2:transactionRequestType>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@TransactionRequestType"/>
                        </ns2:transactionRequestType>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@TransactionNumber"(string) to "ns2:transactionNumber"(<string>) -->
                        <ns2:transactionNumber>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@TransactionNumber"/>
                        </ns2:transactionNumber>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@Amount"(string) to "ns2:amount"(<string>) -->
                        <ns2:amount>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@Amount"/>
                        </ns2:amount>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@CardExpirationDate"(<string>) to "ns2:cardExpirationDate"(<string>) -->
                        <ns2:cardExpirationDate>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@CardExpirationDate"/>
                        </ns2:cardExpirationDate>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@CVVCode"(<string>) to "ns2:cvvCode"(<string>) -->
                        <ns2:cvvCode>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@CVVCode"/>
                        </ns2:cvvCode>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@ZipCode"(<string>) to "ns2:zipCode"(<string>) -->
                        <ns2:zipCode>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@ZipCode"/>
                        </ns2:zipCode>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@PosEcho"(string) to "ns2:posEcho"(<string>) -->
                        <ns2:posEcho>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@PosEcho"/>
                        </ns2:posEcho>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@Reversal"(<string>) to "ns2:reversal"(<string>) -->
                        <ns2:reversal>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@Reversal"/>
                        </ns2:reversal>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@DateTime"(string) to "ns2:dateTime"(<dateTime>) -->
                        <ns2:dateTime>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@DateTime"/>
                        </ns2:dateTime>
                        <!-- a simple data mapping for debitcard : "ProcessTransactionRequest/Transaction/@PinBlock"(string) to "ns2:pinBlock"(<string>) -->
						
						<xsl:choose>
							<xsl:when test="ProcessTransactionRequest/Transaction/@PinBlock">
							<ns2:pinBlock>
								<xsl:value-of select="ProcessTransactionRequest/Transaction/@PinBlock"/>
							</ns2:pinBlock>
							</xsl:when>
						</xsl:choose>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@IsSwiped"(string) to "ns2:isSwiped"(<string>) -->
                        <ns2:isSwiped>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@IsSwiped"/>
                        </ns2:isSwiped>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Transaction/@SwipeData"(<string>) to "ns2:swipeData"(<string>) -->
                        <ns2:swipeData>
                            <xsl:value-of select="ProcessTransactionRequest/Transaction/@SwipeData"/>
                        </ns2:swipeData>
                    </ns2:transaction>
                    <ns2:store>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Store/@StoreNumber"(string) to "ns2:storeNumber"(<string>) -->
                        <ns2:storeNumber>
                            <xsl:value-of select="ProcessTransactionRequest/Store/@StoreNumber"/>
                        </ns2:storeNumber>
                        <!-- a simple data mapping: "ProcessTransactionRequest/Store/@TerminalNumber"(string) to "ns2:terminalNumber"(<string>) -->
                        <ns2:terminalNumber>
                            <xsl:value-of select="ProcessTransactionRequest/Store/@TerminalNumber"/>
                        </ns2:terminalNumber>
                    </ns2:store>
                </ns2:processTransactionRequest>
            </xsl:otherwise>
        </xsl:choose>
       </xsl:template>
   </xsl:stylesheet>