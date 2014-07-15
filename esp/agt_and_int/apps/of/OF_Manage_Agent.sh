#!/usr/bin/ksh
#
#     Program Name:  OF_Manage_Agent.sh
#           Author:  Mark Yearling
#     Date Created:  2011, January
#  Program Purpose:  This is an OF script used for managing agent
#                    servers based on the arguments passed.
#         Argument:  <process_name> <action> <release>
#          Example:  OF_Manage_Agent.sh ShipConfirmationServer stop of
#
# Modification Log:
#                    02/23/2014 - Todd Winter(TKMAZ37) - Linux Conversion
#                          Updated for new infra scripts
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
readonly process_name=${1}
readonly action_type=${2}
readonly release_env=${3}

########################################################################
# RUN REQUIRED PROFILES
#   Execute the user's profile
  . /srv/apps/profile/admin
########################################################################

########################################################################
########################################################################
# PROCEDURE PART OF PROGRAM
########################################################################
########################################################################

# EXECUTE THE SCRIPT
readonly cmdstr="${K_SH}/is_sterling_admin.ksh -t a -s ${process_name} -o ${action_type}"

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

