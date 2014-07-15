SET ECHO OFF
REM ********************************************************************
REM Name:         OF_efc_pkts.sql
REM Author:       Craig Kehoe
REM Date Created: 2011, May
REM Date Changed: 2011, September
REM Date Changed: 2013, May - Mike Phillips 
REM ********************************************************************
REM
REM Purpose
REM -------
REM To retrieve the list of order-release picktickets delivered to a
REM WMOS from OMS since the specified datetime.
REM
REM Query
REM -----
REM OF_efc_pkts.sql : string string -> (  database-state
REM                                     * list-of-records)
REM ********************************************************************
WHENEVER OSERROR EXIT 16
WHENEVER SQLERROR EXIT 12
SET HEADING OFF
SET FEEDBACK OFF
SET LINESIZE 82
SET PAGESIZE 0
SET TAB OFF
SET PAUSE OFF
SET TIMING OFF
SET TRIMOUT ON
SET TRIMSPOOL ON
SET TERMOUT ON
SET VERIFY OFF
DEFINE spool_file_name = &1
DEFINE max_days_back = &2

-- Compute the dates here--saves a second or two of processing time to
-- do it here than to do it in the main query.
COLUMN computed_min_date NOPRINT NEW_VALUE min_date
SELECT TO_CHAR(SYSDATE - &max_days_back, 'YYYY-MM-DD"T"HH24:MI:SS')
       AS computed_min_date
FROM   dual;

SPOOL &&spool_file_name
SET TERMOUT OFF

SELECT    '{'  || 'cust_po_nbr: '  || cust_po_nbr
       || ', ' || 'whse: '         || whse
       || ', ' || 'pkt_ctrl_nbr: ' || pkt_ctrl_nbr
       || ', ' || 'ord_type: '     || CASE ord_type
                                        WHEN 'SA' THEN 'ShipAlone'
                                                  ELSE 'Standard'
                                      END
       || '}' AS the_rec
FROM   pkt_hdr
WHERE  create_date_time >= TO_DATE('&min_date'
                                  ,'YYYY-MM-DD"T"HH24:MI:SS')
       AND cd_master_id = 1
       AND whse = K_WSFN_rpt_get_whse
       AND pkt_consol_prof IS NULL
       AND store_nbr IS NULL;

SPOOL OFF

EXIT

