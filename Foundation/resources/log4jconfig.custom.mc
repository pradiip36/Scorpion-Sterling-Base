<?xml version="1.0" encoding="utf-8"?>
<!-- (C) Copyright  2009 Sterling Comerce, Inc. -->
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">

	<appender name="ALL" class="com.sterlingcommerce.woodstock.util.frame.logex.SCIAppender">
        <param name="rotateLogs" value="true" />
        <param name="maxLogSize" value="100000" />
        <param name="file" value="/kohls/prop/of/Foundation/logs/kohls_ALL_Appender.log" />
        <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
            <param name="ConversionPattern" value="%d:%-7p:%t: %-60m [%X{AppUserId}]: %-25c{1}%n"/>
        </layout>
        <!-- layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIBasicLayout" / -->
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCICommonFilter" />
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>
	
	<appender name="CONSOLE" class="org.apache.log4j.ConsoleAppender">
        <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
            <param name="ConversionPattern" value="%d:%-7p:%t: %-60m [%X{AppUserId}]: %-25c{1}%n"/>
        </layout>
        <!-- layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCILayout" / -->
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCICommonFilter" />
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>    
	
	<appender name="AGENT_LOG_APPENDER" class="com.sterlingcommerce.woodstock.util.frame.logex.SCIAppender">
        <param name="rotateLogs" value="true" />
        <param name="maxLogSize" value="100000" />
        <param name="file" value="/kohls/prop/of/Foundation/logs/agentserver.log" />
        <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
            <param name="ConversionPattern" value="%d:%-7p:%t: %-60m [%X{AppUserId}]: %-25c{1}%n"/>
        </layout>
        <!-- layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIBasicLayout" / -->
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>
    
    <appender name="SECURITY_LOG_APPENDER" class="org.apache.log4j.RollingFileAppender">
        <param name="MaxFileSize" value="2048KB" />
        <param name="MaxBackupIndex" value="2" />
        <param name="File" value="/kohls/prop/of/Foundation/logs/securityinfo.log" />
        <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
            <param name="ConversionPattern" value="%d:%-7p:%t: %-60m [%X{AppUserId}]: %-25c{1}%n"/>
        </layout>
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>
    
    <appender name="LOGIN_CLIENT_IP_DETAILS" class="org.apache.log4j.RollingFileAppender">
            <param name="MaxFileSize" value="2048KB" />
            <param name="MaxBackupIndex" value="2" />
            <param name="File" value="/kohls/prop/of/Foundation/logs/login_ip_details.log" />
            <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
                <param name="ConversionPattern" value="%d:%-7p:%t: %-60m: %-25c{1}%n"/>
            </layout>
            <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>

      <appender name="PROPERTY_MANAGER" class="com.sterlingcommerce.woodstock.util.frame.logex.SCIAppender">
        <param name="rotateLogs" value="true" />
        <param name="maxLogSize" value="100000" />
        <param name="file" value="/kohls/prop/of/Foundation/logs/property_manager.log" />
        <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
            <param name="ConversionPattern" value="%d:%-7p:%t: %-60m [%X{AppUserId}]: %-25c{1}%n"/>
        </layout>       
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>

    <appender name="REQUEST_LOG_APPENDER" class="org.apache.log4j.RollingFileAppender">
        <param name="MaxFileSize" value="2048KB" />
        <param name="MaxBackupIndex" value="2" />
        <param name="File" value="/kohls/prop/of/Foundation/logs/requestinfo.log" />
        <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
            <param name="ConversionPattern" value="%d:%-7p:%t: %-60m [%X{AppUserId}]: %-25c{1}%n"/>
        </layout>
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>
	
	<appender name="NETCOOL_LOG_APPENDER" class="org.apache.log4j.RollingFileAppender">
        <param name="MaxFileSize" value="2048KB" />
        <param name="MaxBackupIndex" value="2" />
        <param name="File" value="/kohls/prop/of/Foundation/logs/NetcoolLog.log" />
        <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
            <param name="ConversionPattern" value="%d:%-7p:%t: %-60m [%X{AppUserId}]: %-25c{1}%n"/>
        </layout>
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>

	<appender name="DSV_INV_APPENDER" class="org.apache.log4j.RollingFileAppender">
        <param name="MaxFileSize" value="2048KB" />
        <param name="MaxBackupIndex" value="2" />
        <param name="File" value="/kohls/prop/of/Foundation/logs/DSVInv.log" />
        <layout class="com.sterlingcommerce.woodstock.util.frame.logex.SCIFilteredPatternLayout">
            <param name="ConversionPattern" value="%d:%-7p:%t: %-60m [%X{AppUserId}]: %-25c{1}%n"/>
        </layout>
        <filter class="com.sterlingcommerce.woodstock.util.frame.logex.SCIPatternFilter" />
    </appender>

	<category  name="DSVInvLogger" class="com.yantra.yfc.log.YFCLogCategory" >
        <priority class="com.yantra.yfc.log.YFCLogLevel" value="DEBUG"  /> 
        <appender-ref ref="DSV_INV_APPENDER" />
		<appender-ref ref="ALL" />
    </category>	

	<category  name="NetcoolLogger" class="com.yantra.yfc.log.YFCLogCategory" >
        <priority class="com.yantra.yfc.log.YFCLogLevel" value="DEBUG"  /> 
        <appender-ref ref="NETCOOL_LOG_APPENDER" />
		<appender-ref ref="ALL" />
    </category>
	
	<category  name="com.kohls" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <level class="com.yantra.yfc.log.YFCLogLevel" value="VERBOSE"  />
        <!--<appender-ref ref="CONSOLE" />-->
        <appender-ref ref="ALL" />
    </category>

    <category  name="requestlogger" class="com.yantra.yfc.log.YFCLogCategory" >
        <level class="com.yantra.yfc.log.YFCLogLevel" value="DEBUG"  />
        <appender-ref ref="REQUEST_LOG_APPENDER" />
    </category>

    <category  name="com.yantra.tools.property" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <level class="com.yantra.yfc.log.YFCLogLevel" value="DEBUG"  />
        <appender-ref ref="PROPERTY_MANAGER" /> 	 
        <!--<appender-ref ref="CONSOLE" />--> 	 
    </category>


    <category  name="com.yantra" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <level class="com.yantra.yfc.log.YFCLogLevel" value="INFO"  />
        <!--<appender-ref ref="CONSOLE" />-->
        <appender-ref ref="ALL" />
    </category>

    <category  name="com.sterlingcommerce" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <level class="com.yantra.yfc.log.YFCLogLevel" value="INFO"  />
        <!--<appender-ref ref="CONSOLE" />-->
        <appender-ref ref="ALL" />
    </category>

    <category  name="org.apache.struts2" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <level class="com.yantra.yfc.log.YFCLogLevel" value="INFO"  />
        <!--<appender-ref ref="CONSOLE" />-->
        <appender-ref ref="ALL" />
    </category>

     <category  name="com.opensymphony.xwork2" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <level class="com.yantra.yfc.log.YFCLogLevel" value="INFO"  />
        <!--<appender-ref ref="CONSOLE" />-->
        <appender-ref ref="ALL" />
    </category>

   <category  name="com.stercomm.SecurityLogger" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <priority class="com.yantra.yfc.log.YFCLogLevel" value="DEBUG"  /> 
        <appender-ref ref="SECURITY_LOG_APPENDER" />
    </category>

    <category  name="api.security" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <priority class="com.yantra.yfc.log.YFCLogLevel" value="DEBUG"  />
        <!--<appender-ref ref="CONSOLE" />--> 
        <appender-ref ref="ALL" />
    </category>

    <category  name="com.yantra.integration.adapter" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <priority class="com.yantra.yfc.log.YFCLogLevel" value="VERBOSE"  />
        <!--<appender-ref ref="CONSOLE" />--> 
        <appender-ref ref="AGENT_LOG_APPENDER" />
    </category>    

   <category  name="com.yantra.yfs.ui.backend.YFSLoginIPLogger" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
       <priority class="com.yantra.yfc.log.YFCLogLevel" value="INFO"  /> 
       <!--<appender-ref ref="CONSOLE" />-->
       <appender-ref ref="ALL" />
       <appender-ref ref="LOGIN_CLIENT_IP_DETAILS" />
   </category>

   <category  name="DataValidationLogger" class="com.yantra.yfc.log.YFCLogCategory" additivity="false" >
        <level class="com.yantra.yfc.log.YFCLogLevel" value="INFO"  />
        <!--<appender-ref ref="CONSOLE" />-->
        <appender-ref ref="ALL" />
    </category>
    
</log4j:configuration>
