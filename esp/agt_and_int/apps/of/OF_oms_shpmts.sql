SET ECHO OFF
REM ********************************************************************
REM Name:         OF_oms_shpmts.sql
REM Author:       Craig Kehoe
REM Date Created: 2011, May
REM ********************************************************************
REM
REM Purpose
REM -------
REM To retrieve the list of shipments received in OMS from the WMOSes
REM since the specified datetime.
REM
REM Query
REM -----
REM OF_oms_shpmts.sql : string string -> (  database-state 
REM                                       * list-of-records)
REM ********************************************************************
WHENEVER OSERROR EXIT 16
WHENEVER SQLERROR EXIT 12
SET HEADING OFF
SET FEEDBACK OFF
SET PAGESIZE 0
SET TAB OFF
SET PAUSE OFF
SET TAB OFF
SET TIMING OFF
SET TRIMOUT ON
SET TRIMSPOOL ON
SET TERMOUT ON
SET VERIFY OFF
DEFINE spool_file_name = &1
DEFINE max_days_back = &2
SPOOL &&spool_file_name
SET TERMOUT OFF

SELECT    '{'  || 'whse: ' || TRIM(S.shipnode_key)
       || ', ' || 'carton_nbr: ' || C.container_scm
       || '}'  AS the_rec
FROM   STERLING.yfs_shipment S
JOIN   STERLING.yfs_shipment_container C ON S.shipment_key = C.shipment_key
WHERE  C.modifyts >= TRUNC(SYSDATE) - 10;

EXIT

