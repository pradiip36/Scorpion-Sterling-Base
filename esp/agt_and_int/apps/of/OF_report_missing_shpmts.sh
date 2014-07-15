#!/usr/bin/ksh
#
# Name:         OF_report_missing_shpmts.sh
# Author:       Craig Kehoe
# Date Created: 2011, May
# Date Created: 2011, September
# Date Created: 2012, August
# Date Changed: 2013, May - eFC4 Upgrade
#
# Purpose
# -------
# Run a SQL*Plus script that contains a query that checks to see that 
# all shipments that were sent from the WMOSes were bridged to OMS. 
#
# Input
# -----
#   Command-Line Arguments
#   ----------------------
#     -r CHAR(*) <SQL*Plus driver-script's name>
#     -d CHAR(*) <DBlogin script's name>
#     -s CHAR(*) <WMOS SQL*Plus script's name>
#     -v CHAR(*) <WMOS 2012 SQL*Plus script's name>
#     -t CHAR(*) <OMS SQL*Plus script's name>
#     -o CHAR(*) <EFC1 Query Spoolfile's name>
#     -p CHAR(*) <EFC2 Query Spoolfile's name>
#     -l CHAR(*) <EFC3 Query Spoolfile's name>
#     -m CHAR(*) <EFC4 Query Spoolfile's name>
#     -q CHAR(*) <OMS Query Spoolfile's name>
#     -w CHAR(*) <OMS Sorted Shipments File's name>
#     -x CHAR(*) <All EFCs Sorted Shipments File's name>
#     -y CHAR(*) <Missing Shipments Data File's name>
#     -e CHAR(*) <Email-address to send report to>
#     -1 NUMBER <Maximum number of days back for the script to look>
#     -2 NUMBER <Minimun number of days back for the script to look>
#
# Output
# ------
#   Exit Number
#   -----------
#      0 = Success
#    100 = Invalid Parameter(s)
#    101 = Sort Error
#    110 = File Does Not Exist
#    122 = Oracle Error
#    126 = Error Sending Email
#
#   Effects
#   -------
#     Display the command that invoked this script
#     Overwrite $DBLOGIN for subsequent queries
#     Run a query against the OMS database
#     Write query-results to spool-file
#     Display the SQL*Plus driver-script's STDOUT/STDERR
#     Send an email message
# 
# Example
# -------
#   OF_report_missing_shpmts_r1.sh \
#                             -s /prod/apps/of/OF_efc_shpmts.sql \
#                             -v /prod/apps/of/OF_efc_shpmts_2012.sql \
#                             -t /prod/apps/of/OF_oms_shpmts.sql \
#                             -d /prod/apps/script/ksetdblogin \
#                             -o /logs/of/OF_efc1_shpmts.spool \
#                             -p /logs/of/OF_efc2_shpmts.spool \
#                             -l /logs/of/OF_efc3_shpmts.spool \
#                             -m /logs/of/OF_efc4_shpmts.spool \
#                             -q /logs/of/OF_oms_shpmts.spool \
#                             -w /logs/of/OF_oms_shpmts.sorted \
#                             -x /logs/of/OF_all_efc_shpmts.sorted \
#                             -y /logs/of/OF_missing_shpmts.dat \
#                             -e is-wscorp@kohls.com \
#                             -1 3 \
#                             -2 1/24
#
########################################################################

readonly my_name="${0}"
readonly my_basename="${my_name##/*/}"
readonly my_invocation="${@}"
readonly my_pid="${$}"
readonly my_starting_datetime="$(date +%Y%m%dT%H%M%S)"

########################################################################
print 'BEGIN DISPLAYING THE STARTING STATE'
set
print '**END DISPLAYING THE STARTING STATE'
########################################################################
print
while getopts ":s:v:t:d:o:m:p:q:l:w:x:y:e:1:2:" opt; do
  case ${opt} in
     s) readonly efcshpmtsqlnm="${OPTARG}" ;;
     v) readonly efc2012shpmtsqlnm="${OPTARG}" ;;
     t) readonly omsshpmtsqlnm="${OPTARG}" ;;
     d) readonly setdblogin="${OPTARG}" ;;
     o) readonly efc1spoolfilenm="${OPTARG}" ;;
     p) readonly efc2spoolfilenm="${OPTARG}" ;;
     l) readonly efc3spoolfilenm="${OPTARG}" ;;
     m) readonly efc4spoolfilenm="${OPTARG}" ;;
     q) readonly omsspoolfilenm="${OPTARG}" ;;
     w) readonly omssortedfilenm="${OPTARG}" ;;
     x) readonly allefcssortedfilenm="${OPTARG}" ;;
     y) readonly missingshpmtsfilenm="${OPTARG}" ;;
     e) readonly emailaddr="${OPTARG}" ;;
     1) readonly maxdaysback="${OPTARG}" ;;
     2) readonly mindaysback="${OPTARG}" ;;
    \?) print "bad argument passed: -${OPTARG}"
        exit 100 # Invalid Parameter(s) ;;
  esac
done

shift $((OPTIND-1))

########################################################################
print 'BEGIN VALIDATING THE PARAMETERS'
if [[ -z ${efcshpmtsqlnm} ]]
then
  print -n 'required argument missing: -s CHAR(*) <WMOS SQL*Plus '
  print "script's name>"
fi
if [[ -z ${efc2012shpmtsqlnm} ]]
then
  print -n 'required argument missing: -s CHAR(*) <WMOS SQL*Plus '
  print "script's name>"
fi
if [[ -z ${omsshpmtsqlnm} ]]
then
  print -n 'required argument missing: -t CHAR(*) <OMS SQL*Plus '
  print "script's name>"
fi
if [[ -z ${setdblogin} ]]
then
  print -n 'required argument missing: -d CHAR(*) <DBlogin '
  print "script's name>"
fi
if [[ -z ${efc1spoolfilenm} ]]
then
  print -n 'required argument missing: -o CHAR(*) <EFC1 Query '
  print "Spoolfile's name>"
fi
if [[ -z ${efc2spoolfilenm} ]]
then
  print -n 'required argument missing: -p CHAR(*) <EFC2 Query '
  print "Spoolfile's name>"
fi
if [[ -z ${efc3spoolfilenm} ]]
then
  print -n 'required argument missing: -p CHAR(*) <EFC3 Query '
  print "Spoolfile's name>"
fi
if [[ -z ${efc4spoolfilenm} ]]
then
  print -n 'required argument missing: -p CHAR(*) <EFC4 Query '
  print "Spoolfile's name>"
fi
if [[ -z ${omsspoolfilenm} ]]
then
  print -n 'required argument missing: -q CHAR(*) <OMS Query '
  print "Spoolfile's name>"
fi
if [[ -z ${omssortedfilenm} ]]
then
  print -n 'required argument missing: -w CHAR(*) <OMS Sorted '
  print "Shipments File's name>"
fi
if [[ -z ${allefcssortedfilenm} ]]
then
  print -n 'required argument missing: -x CHAR(*) <All EFCs Sorted '
  print "Shipments File's name>"
fi
if [[ -z ${missingshpmtsfilenm} ]]
then
  print -n 'required argument missing: -y CHAR(*) <Missing Shipments '
  print "Data File's name>"
fi
if [[ -z ${emailaddr} ]]
then
  print -n 'required argument missing: -e CHAR(*) <Email-address to '
  print 'send report to>'
fi
if [[ -z ${maxdaysback} ]]
then
  print -n 'required argument missing: -1 NUMBER <Maximum number of '
  print 'days back for the script to look>'
fi
if [[ -z ${mindaysback} ]]
then
  print -n 'required argument missing: -2 NUMBER <Minimun number of '
  print 'days back for the script to look>'
fi
print '**END VALIDATING THE PARAMETERS'
########################################################################
print
if [[    -z ${efcshpmtsqlnm} \
      || -z ${efc2012shpmtsqlnm} \
      || -z ${omsshpmtsqlnm} \
      || -z ${setdblogin} \
      || -z ${efc1spoolfilenm} \
      || -z ${efc2spoolfilenm} \
      || -z ${efc3spoolfilenm} \
      || -z ${efc4spoolfilenm} \
      || -z ${omsspoolfilenm} \
      || -z ${omssortedfilenm} \
      || -z ${allefcssortedfilenm} \
      || -z ${missingshpmtsfilenm} \
      || -z ${emailaddr} \
      || -z ${maxdaysback} \
      || -z ${mindaysback} ]]
then
  print 'At least one required argument is missing, so exiting...'
  exit 100 # Invalid Parameter(s)
fi
########################################################################
print 'BEGIN VALIDATING THE REQUIRED FILES'
if [[ ! -a ${setdblogin} ]]
then
  print "required file is missing: ${setdblogin}"
fi
if [[ ! -a ${efcshpmtsqlnm} ]]
then
  print "required file is missing: ${efcshpmtsqlnm}"
fi
if [[ ! -a ${efc2012shpmtsqlnm} ]]
then
  print "required file is missing: ${efc2012shpmtsqlnm}"
fi
if [[ ! -a ${omsshpmtsqlnm} ]]
then
  print "required file is missing: ${omsshpmtsqlnm}"
fi
print '**END VALIDATING THE REQUIRED FILES'
########################################################################
print
if [[    ! -a ${efcshpmtsqlnm} \
      || ! -a ${efc2012shpmtsqlnm} \
      || ! -a ${omsshpmtsqlnm} \
      || ! -a ${setdblogin} ]]
then
  print 'At least one required file is missing, so exiting...'
  exit 110 # File Does Not Exist
fi
########################################################################
print 'BEGIN THE EFC1 CARTON-INVOICE QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} efc1
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${efcshpmtsqlnm} \
        ${efc1spoolfilenm} ${maxdaysback} ${mindaysback}
readonly efc1sqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${efc1sqlrv}"
print '**END THE EFC1 CARTON-INVOICE QUERY'
########################################################################
if [[ ${efc1sqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print 'BEGIN THE EFC2 CARTON-INVOICE QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} efc2
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${efcshpmtsqlnm} \
        ${efc2spoolfilenm} ${maxdaysback} ${mindaysback}
readonly efc2sqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${efc2sqlrv}"
print '**END THE EFC2 CARTON-INVOICE QUERY'
########################################################################
if [[ ${efc2sqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print 'BEGIN THE EFC3 CARTON-INVOICE QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} efc3
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${efcshpmtsqlnm} \
        ${efc3spoolfilenm} ${maxdaysback} ${mindaysback}
readonly efc3sqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${efc3sqlrv}"
print '**END THE EFC3 CARTON-INVOICE QUERY'
########################################################################
if [[ ${efc3sqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print 'BEGIN THE EFC4 CARTON-INVOICE QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} efc4
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${efc2012shpmtsqlnm} \
        ${efc4spoolfilenm} ${maxdaysback} ${mindaysback}
readonly efc4sqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${efc4sqlrv}"
print '**END THE EFC4 CARTON-INVOICE QUERY'
########################################################################
if [[ ${efc4sqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print 'BEGIN THE OMS CARTON-INVOICE QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} oms 
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${omsshpmtsqlnm} \
        ${omsspoolfilenm} ${maxdaysback}
readonly omssqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${omssqlrv}"
print '**END THE OMS CARTON-INVOICE QUERY'
########################################################################
if [[ ${omssqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print "BEGIN SORTING THE EFCS' CARTON-INVOICE DATA"
sort -u ${efc1spoolfilenm} \
        ${efc2spoolfilenm} \
        ${efc3spoolfilenm} \
        ${efc4spoolfilenm} \
> ${allefcssortedfilenm}
readonly sortefcrv=${?}
print "**END SORTING THE EFCS' CARTON-INVOICE DATA"
########################################################################
if [[ ${sortefcrv} -gt 1 ]]
then
  exit 101 # Sort Error
fi
print
########################################################################
print "BEGIN SORTING OMS'S CARTON-INVOICE DATA"
sort -u ${omsspoolfilenm} > ${omssortedfilenm}
readonly sortomsrv=${?}
print "**END SORTING OMS'S CARTON-INVOICE DATA"
########################################################################
if [[ ${sortomsrv} -gt 1 ]]
then
  exit 101 # Sort Error
fi
print
########################################################################
print -n "BEGIN EXCEPTING OMS'S CARTON-INVOICE DATA FROM WMOS'S CARTON-"
print 'INVOICE DATA'
join -t'\r' -v1 ${allefcssortedfilenm} ${omssortedfilenm} \
> ${missingshpmtsfilenm}
readonly joinrv=${?}
print -n "**END EXCEPTING OMS'S CARTON-INVOICE DATA FROM WMOS'S CARTON-"
print 'INVOICE DATA'
########################################################################
if [[ ${joinrv} -ne 0 ]]
then
  exit 101 # Sort Error
fi
print
########################################################################
print 'BEGIN DETERMINING WHETHER MISSING CARTON-INVOICES EXIST'
if [[ -s ${missingshpmtsfilenm} ]]
then
  print 'MIISSING CARTON-INVOICES EXIST'
  print 'BEGIN SENDING THE EMAIL ALERT MESSAGE'
  mail -r noreply@kohls.com -vs 'OMS Alert - Missing Carton-Invoices' ${emailaddr} \
  < ${missingshpmtsfilenm}
  readonly mailrv=${?}
  print '**END SENDING THE EMAIL ALERT MESSAGE'
  if [[ ${mailrv} -ne 0 ]]
  then
    exit 126 # Error Sending Email
  fi
else
  print 'ALL CARTON-INVOICES RECEIVED'
  rm -f ${missingshpmtsfilenm}
fi
print '**END DETERMINING WHETHER MISSING CARTON-INVOICES EXIST'
########################################################################
print
########################################################################
print 'BEGIN DISPLAYING THE ENDING STATE'
set \
| sed -e 's/DBLOGIN=.*$/DBLOGIN=/' \
      -e 's/DB_PWD=.*$/DB_PWD=/' 
print '**END DISPLAYING THE ENDING STATE'
########################################################################
print
########################################################################
print 'EXIT FROM THE SCRIPT'

exit 0  # RC 0 indicates success

