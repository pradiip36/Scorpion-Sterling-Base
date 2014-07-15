#!/usr/bin/ksh
#
#     Program Name:  OF_Manage_Weblogic.sh
#           Author:  Mark Yearling
#     Date Created:  2011, January
#  Program Purpose:  This is an OF script used for managing agent
#                    servers based on the arguments passed.
#         Argument:  <action> <instance>
#          Example:  OF_Manage_Weblogic.sh stop xloms-ofmc001
#
# Modification Log:
#
#
########################################################################
########################################################################
# DATA PART OF PROGRAM
########################################################################
########################################################################

########################################################################
# SET PARAMETER VALUES
readonly scriptname=${0}
readonly instance_name=${1}
readonly action_type=${2}
#readonly release_env=${3}

########################################################################
# RUN REQUIRED PROFILES
#   Execute the user's profile
. ~/.profile > /dev/null
########################################################################

########################################################################
########################################################################
# PROCEDURE PART OF PROGRAM
########################################################################
########################################################################

# EXECUTE THE SCRIPT
readonly cmdstr="${K_INFRA_SCRIPT}/is_wls11admin.sh -o ${action_type} -s ${instance_name}"

integer rc

print "COMMAND: ${cmdstr}"
print
print "BEGIN PROGRAM'S STDOUT/STDERR"
print

${cmdstr}

typeset -r rc=${?}

print
print "END PROGRAM'S STDOUT/STDERR"
print "PROGRAM'S RC: ${rc}"
exit ${rc}
