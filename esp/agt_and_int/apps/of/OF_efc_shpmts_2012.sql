SET ECHO OFF
REM ********************************************************************
REM Name:         OF_efc_shpmts.sql
REM Author:       Craig Kehoe
REM Date Created: 2011, May
REM Date Changed: 2013, May - Mike Phillips 
REM ********************************************************************
REM
REM Purpose
REM -------
REM To retrieve the list of shipments sent from the WMOSes to OMS within
REM the specified datetimes.
REM
REM Query
REM -----
REM OF_efc_shpmts.sql : string string string -> (  database-state 
REM                                              * list-of-records)
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
DEFINE min_days_back = &3
SPOOL &&spool_file_name
SET TERMOUT OFF

SELECT    '{'  || 'whse: ' || C_FACILITY_ALIAS_ID
       || ', ' || 'carton_nbr: ' || TC_LPN_ID
       || '}'  AS the_rec
FROM   OUTPT_LPN
WHERE  LAST_UPDATED_DTTM BETWEEN TRUNC(SYSDATE) - &&max_days_back
                     AND     SYSDATE - &&min_days_back
       AND REGEXP_LIKE(TC_ORDER_ID, '^[[:digit:]]+$')
       AND proc_stat_code IN (5, 90);
EXIT

