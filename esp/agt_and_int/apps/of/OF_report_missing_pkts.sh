#!/usr/bin/ksh
#
# Name:         OF_report_missing_pkts.sh
# Author:       Craig Kehoe
# Date Created: 2011, May
# Date Changed: 2011, September
# Date Changed: 2012, August
# Date Changed: 2013, May - eFC4 Upgrade
#
# Purpose
# -------
# Run a SQL*Plus script that contains a query that checks to see that
# all picktickets that were sent from OMS were bridged to the WMOSes.
#
# Input
# -----
#   Command-Line Arguments
#   ----------------------
#     -r CHAR(*) <SQL*Plus driver-script's name>
#     -d CHAR(*) <DBlogin script's name>
#     -s CHAR(*) <WMOS SQL*Plus script's name>
#     -v CHAR(*) <WMOS SQL*Plus script's name>
#     -t CHAR(*) <OMS SQL*Plus script's name>
#     -o CHAR(*) <EFC1 Query Spoolfile's name>
#     -p CHAR(*) <EFC2 Query Spoolfile's name>
#     -l CHAR(*) <EFC3 Query Spoolfile's name>
#     -m CHAR(*) <EFC4 Query Spoolfile's name>
#     -q CHAR(*) <OMS Query Spoolfile's name>
#     -w CHAR(*) <OMS Sorted Picktickets File's name>
#     -x CHAR(*) <All EFCs Sorted Picktickets File's name>
#     -y CHAR(*) <Missing Picktickets Data File's name>
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
#     Overwrite $DBLOGIN for each subsequent database connection
#     Run a query against the OMS database
#     Write query-results to spool-file
#     Display the SQL*Plus driver-script's STDOUT/STDERR
#     Send an email message
#
# Example
# -------
#   OF_report_missing_pkts.sh -s /prod/apps/of/OF_efc_pkts.sql \
#                             -v /prod/apps/of/OF_efc_pkts_2012.sql \
#                             -t /prod/apps/of/OF_oms_pkts.sql \
#                             -d /prod/apps/script/ksetdblogin \
#                             -o /logs/of/OF_efc1_pkts.spool \
#                             -p /logs/of/OF_efc2_pkts.spool \
#                             -l /logs/of/OF_efc3_pkts.spool \
#                             -m /logs/of/OF_efc4_pkts.spool \
#                             -q /logs/of/OF_oms_pkts.spool \
#                             -w /logs/of/OF_oms_pkts.sorted \
#                             -x /logs/of/OF_all_efc_pkts.sorted \
#                             -y /logs/of/OF_missing_pkts.dat \
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
while getopts ":s:v:t:d:o:p:q:l:m:w:x:y:e:1:2:" opt; do
  case ${opt} in
     s) readonly efcpktsqlnm="${OPTARG}" ;;
     v) readonly efc2012pktsqlnm="${OPTARG}" ;;
     t) readonly omspktsqlnm="${OPTARG}" ;;
     d) readonly setdblogin="${OPTARG}" ;;
     o) readonly efc1spoolfilenm="${OPTARG}" ;;
     p) readonly efc2spoolfilenm="${OPTARG}" ;;
     l) readonly efc3spoolfilenm="${OPTARG}" ;;
     m) readonly efc4spoolfilenm="${OPTARG}" ;;
     q) readonly omsspoolfilenm="${OPTARG}" ;;
     w) readonly omssortedfilenm="${OPTARG}" ;;
     x) readonly allefcssortedfilenm="${OPTARG}" ;;
     y) readonly missingpktsfilenm="${OPTARG}" ;;
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
if [[ -z ${efcpktsqlnm} ]]
then
  print -n 'required argument missing: -s CHAR(*) <WMOS SQL*Plus '
  print "script's name>"
fi
if [[ -z ${efc2012pktsqlnm} ]]
then
  print -n 'required argument missing: -s CHAR(*) <WMOS SQL*Plus '
  print "script's name>"
fi
if [[ -z ${omspktsqlnm} ]]
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
if [[ -z ${efc4spoolfilenm} ]]w
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
  print "Picktickets File's name>"
fi
if [[ -z ${allefcssortedfilenm} ]]
then
  print -n 'required argument missing: -x CHAR(*) <All EFCs Sorted '
  print "Picktickets File's name>"
fi
if [[ -z ${missingpktsfilenm} ]]
then
  print -n 'required argument missing: -y CHAR(*) <Missing Picktickets '
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
if [[    -z ${efcpktsqlnm} \
      || -z ${efc2012pktsqlnm} \
      || -z ${omspktsqlnm} \
      || -z ${setdblogin} \
      || -z ${efc1spoolfilenm} \
      || -z ${efc2spoolfilenm} \
      || -z ${efc3spoolfilenm} \
      || -z ${efc4spoolfilenm} \
      || -z ${omsspoolfilenm} \
      || -z ${omssortedfilenm} \
      || -z ${allefcssortedfilenm} \
      || -z ${missingpktsfilenm} \
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
if [[ ! -a ${efcpktsqlnm} ]]
then
  print "required file is missing: ${efcpktsqlnm}"
fi
if [[ ! -a ${efc2012pktsqlnm} ]]
then
  print "required file is missing: ${efc2012pktsqlnm}"
fi
if [[ ! -a ${omspktsqlnm} ]]
then
  print "required file is missing: ${omspktqlnm}"
fi
print '**END VALIDATING THE REQUIRED FILES'
########################################################################
print
if [[    ! -a ${efcpktsqlnm} \
      || ! -a ${efc2012pktsqlnm} \
      || ! -a ${omspktsqlnm} \
      || ! -a ${setdblogin} ]]
then
  print 'At least one required file is missing, so exiting...'
  exit 110 # File Does Not Exist
fi
########################################################################
print 'BEGIN THE EFC1 ORDER-RELEASE PICKTICKET QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} efc1
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${efcpktsqlnm} \
        ${efc1spoolfilenm} ${maxdaysback}
readonly efc1sqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${efc1sqlrv}"
print '**END THE EFC1 ORDER-RELEASE PICKTICKET QUERY'
########################################################################
if [[ ${efc1sqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print 'BEGIN THE EFC2 ORDER-RELEASE PICKTICKET QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} efc2
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${efcpktsqlnm} \
        ${efc2spoolfilenm} ${maxdaysback}
readonly efc2sqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${efc2sqlrv}"
print '**END THE EFC2 ORDER-RELEASE PICKTICKET QUERY'
########################################################################
if [[ ${efc2sqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print 'BEGIN THE EFC3 ORDER-RELEASE PICKTICKET QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} efc3
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${efcpktsqlnm} \
        ${efc3spoolfilenm} ${maxdaysback}
readonly efc3sqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${efc3sqlrv}"
print '**END THE EFC3 ORDER-RELEASE PICKTICKET QUERY'
########################################################################
if [[ ${efc3sqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print 'BEGIN THE EFC4 ORDER-RELEASE PICKTICKET QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} efc4
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${efc2012pktsqlnm} \
        ${efc4spoolfilenm} ${maxdaysback}
readonly efc4sqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${efc4sqlrv}"
print '**END THE EFC4 ORDER-RELEASE PICKTICKET QUERY'
########################################################################
if [[ ${efc4sqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print 'BEGIN THE OMS ORDER-RELEASE PICKTICKET QUERY'
print 'BEGIN SOURCING THE DATABASE'
. ${setdblogin} oms
print "{DB_NAME: ${DBLOGIN##*@}, DB_USER: ${DBLOGIN%%/*}}"
print '**END SOURCING THE DATABASE'
print "BEGIN THE SQL*Plus SCRIPT'S STDOUT/STDERR"
sqlplus ${DBLOGIN} @${omspktsqlnm} \
        ${omsspoolfilenm} ${maxdaysback} ${mindaysback}
readonly omssqlrv=${?}
print "**END THE SQL*Plus SCRIPT'S STDOUT/STDERR"
print "SQL*Plus'S RETURN-CODE: ${omssqlrv}"
print '**END THE OMS ORDER-RELEASE PICKTICKET QUERY'
########################################################################
if [[ ${omssqlrv} -ne 0 ]]
then
  exit 122 # Oracle Error
fi
print
########################################################################
print "BEGIN SORTING THE EFCS' ORDER-RELEASE PICKTICKET DATA"
sort -u ${efc1spoolfilenm} \
        ${efc2spoolfilenm} \
        ${efc3spoolfilenm} \
        ${efc4spoolfilenm} \
> ${allefcssortedfilenm}  #eFC3
readonly sortefcrv=${?}
print "**END SORTING THE EFCS' ORDER-RELEASE PICKTICKET DATA"
########################################################################
if [[ ${sortefcrv} -gt 1 ]]
then
  exit 101 # Sort Error
fi
print
########################################################################
print "BEGIN SORTING OMS'S ORDER-RELEASE PICKTICKET DATA"
sort -u ${omsspoolfilenm} > ${omssortedfilenm}
readonly sortomsrv=${?}
print "**END SORTING OMS'S ORDER-RELEASE PICKTICKET DATA"
########################################################################
if [[ ${sortomsrv} -gt 1 ]]
then
  exit 101 # Sort Error
fi
print
########################################################################
print -n "BEGIN EXCEPTING OMS'S ORDER-RELEASE PICKTICKET DATA FROM WMOS"
print "'S ORDER-RELEASE PICKTICKET DATA"
join -t'\r' -v1 ${omssortedfilenm} ${allefcssortedfilenm} \
> ${missingpktsfilenm}
readonly joinrv=${?}
print -n "**END EXCEPTING OMS'S ORDER-RELEASE PICKTICKET DATA FROM WMOS"
print "'S ORDER-RELEASE PICKTICKET DATA"
########################################################################
if [[ ${joinrv} -ne 0 ]]
then
  exit 101 # Sort Error
fi
print
########################################################################
print \
'BEGIN DETERMINING WHETHER MISSING ORDER-RELEASE PICKTICKETS EXIST'
if [[ -s ${missingpktsfilenm} ]]
then
  print 'MIISSING ORDER-RELEASE PICKTICKETS EXIST'
  print 'BEGIN SENDING THE EMAIL ALERT MESSAGE'
  mail -r noreply@kohls.com -vs 'OMS Alert - Undelivered Order-Release Picktickets' \
          ${emailaddr} \
  < ${missingpktsfilenm}
  readonly mailrv=${?}
  print '**END SENDING THE EMAIL ALERT MESSAGE'
  if [[ ${mailrv} -ne 0 ]]
  then
    exit 126 # Error Sending Email
  fi
else
  print 'ALL ORDER-RELEASE PICKTICKETS RECEIVED'
  rm -f ${missingpktsfilenm}
fi
print \
'**END DETERMINING WHETHER MISSING ORDER-RELEASE PICKTICKETS EXIST'
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

