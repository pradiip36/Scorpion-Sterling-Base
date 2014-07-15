#!/usr/bin/ksh
#     Program Name:  OF_run_a_sqlplus_script.sh
#           Author:  Craig Kehoe
#Responsible Group:  Order Fulfillment Service Delivery
#     Date Created:  2011-01-28
#  Program Purpose:  Execute a SQL*Plus script. 
#      Declaration:  OF_run_a_sqlplus_script.sh \
#                      CALLER \
#                      DBSOURCE@KEY \
#                      SQLPLUSSCRIPTNM[@ARG1..[@ARGN]]
#          Example:  OF_run_a_sqlplus_script.sh \
#                      OF_report_missing_pkts.sh \
#                      ${K_SH}/ksetdblogin@oa \
#                      ${K_SRC}/of/OF_missing_picktickets.sql@~/mylog.txt@3
#
readonly my_invocation=${*}
readonly my_name=${0}
readonly my_caller=${1}
readonly my_db_source=${2}
readonly my_sqlplus_script_and_args=${3}

## get this script's basename
readonly my_basename=${my_name##/*/}

readonly my_sqlplus_scriptnm=${my_sqlplus_script_and_args%%@*}
readonly my_sqlplus_script_basename=${my_sqlplus_scriptnm##/*/}
readonly my_db_sourcing_scriptnm=${my_db_source%%@*}
readonly my_db_sourcing_key=${my_db_source##*@}

########################################################################
print 'BEGIN DISPLAYING THE COMMAND THAT INVOKED THIS SCRIPT'
print "${my_name} ${my_invocation}"
print '**END DISPLAYING THE COMMAND THAT INVOKED THIS SCRIPT'
print
########################################################################

########################################################################
print "BEGIN DISPLAYING ${my_basename}'S CODE"
fold -w123 < ${my_name} \
| cat -n
print "**END DISPLAYING ${my_basename}'S CODE"
print
########################################################################

########################################################################
print -n "BEGIN DISPLAYING THE ${my_db_sourcing_scriptnm}'S DATABASE-SO"
print    "URCING-SCRIPT'S CODE"
fold -w123 < ${my_db_sourcing_scriptnm} \
| cat -n
print -n "**END DISPLAYING THE ${my_db_sourcing_scriptnm}'S DATABASE-SO"
print    "URCING-SCRIPT'S CODE"
print
########################################################################

########################################################################
print -n "BEGIN DISPLAYING THE ${my_sqlplus_script_basename} SQL*Plus S"
print "CRIPT'S CODE"
fold -w123 < ${my_sqlplus_scriptnm} \
| cat -n
print -n "**END DISPLAYING THE ${my_sqlplus_script_basename} SQL*Plus S"
print "CRIPT'S CODE"
print
########################################################################

########################################################################
print "BEGIN SOURCING THE DATABASE"
. ${my_db_sourcing_scriptnm} ${my_db_sourcing_key}
readonly DBLOGIN
print "BEGIN SOURCING THE DATABASE"
print
########################################################################

########################################################################
print -n "BEGIN DISPLAYING ${my_basename}'S STATE BEFORE RUNNING THE "
print "${my_sqlplus_basename} SQL*Plus SCRIPT"
set \
|  sed -e 's/DBLOGIN=.*$/DBLOGIN=/' \
       -e 's/DB_PWD=.*$/DB_PWD=/' \
|  fold -w123 \
|  cat -n
print -n "**END DISPLAYING ${my_basename}'S STATE BEFORE RUNNING THE "
print "${my_sqlplus_basename} SQL*Plus SCRIPT"
print
########################################################################

########################################################################
print "BEGIN RUNNING THE ${my_sqlplus_script_basename} SQL*Plus SCRIPT"
sqlplus ${DBLOGIN} @$(print ${my_sqlplus_script_and_args} | tr '@' ' ')
readonly sqlplusrv=${?}
print "**END RUNNING THE ${my_sqlplus_script_basename} SQL*Plus SCRIPT"
print "SQL*Plus'S RETURN-CODE: ${sqlplusrv}"
print
########################################################################

########################################################################
if (( sqlplusrv == 0 )); then
  exit 0  # RC 0 indicates success
else
  exit 16 # RC 16 indicates general failure
fi

