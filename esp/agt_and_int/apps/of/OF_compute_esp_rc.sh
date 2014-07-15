#!/usr/bin/ksh
# This script computes the ESP return message to send.

readonly input_caller=${1}
typeset -ri inpt_rc=${2}

case ${inpt_rc} in
    0) print 'Successful' ;;
    4) print 'Warning' ;;
   16) print 'Error' ;;
  100) print 'Invalid Parameter(s)' ;;
  101) print 'Sort Error' ;;
  110) print 'File Does Not Exist' ;;
  111) print 'Open File Error' ;;
  112) print 'Close File Error' ;;
  113) print 'Read Error' ;;
  114) print 'Write Error' ;;
  115) print 'File Sequence Error' ;;
  116) print 'Invalid Data in Dataset' ;;
  117) print 'Out of Space' ;;
  120) print 'DB2 Error' ;;
  121) print 'UDB Error' ;;
  122) print 'Oracle Error' ;;
  123) print 'SQL Server Error' ;;
  124) print 'MQSeries Error' ;;
  125) print 'FTP Error' ;;
  126) print 'Error Sending Email' ;;
  127) print 'SCP Error' ;;
  130) print 'Subroutine Error' ;;
  131) print 'Calendar Subroutine Error' ;;
  201) print 'Aborted due to waving' ;;
  # The following three cases are application-specific
  216) print "${input_caller} failed." ;;
    *)   if ((200 <= inpt_rc)) && ((inpt_rc <= 254)); then
           print 'Application Specific - check JOBDOC'
         elif ((inpt_rc > 254)); then
           # SET RC TO 254 IF GREATER THAN 254 DUE TO
           # ESP LIMITATION
           typeset -ri computed_rc=254
           print "${input_caller} did not complete successfully"
           print "Return code ${inpt_rc} converted to ${computed_rc}"
           print 'Application Specific - check JOBDOC'
         elif ((inpt_rc < 200)); then
           print 'Undefined Error'
         fi ;;
esac

eval exit ${computed_rc:-\$\{inpt_rc\}}

