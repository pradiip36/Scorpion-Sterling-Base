#-- PROJECT INFORMATION --#
project.name=kohls
releaseJar=Kohls1_0
customizedProductJar=KohlsJar

#-- VERSION INFORMATION --#
release.version=0.1.6

##--ENV Information --#
WL_HOME=/prod/prop/weblogic/10.3.0
INSTALL_DIR=/kohls/prop/of/Foundation/
JAVA_HOME=/prod/prop/mg/java16_pap6460-20100624_01
env=mc
env.SOURCE_DB_1=MASTERCONFIG_LINUX_SRC
env.SOURCE_PASSWORD_1=stqrl1ng
env.TARGET_DB_1=MasterConfigCDTXMLTarget
env.TARGET_PASSWORD_1=
env.TARGET_HTTP_PASSWORD_1=notused

env.SOURCE_DB_2=MasterConfigCDTXML
env.SOURCE_PASSWORD_2=
env.TARGET_DB_2=MASTERCONFIG_LINUX_TGT
env.TARGET_PASSWORD_2=stqrl1ng
env.TARGET_HTTP_PASSWORD_2=notused

env.dev=
env.qa=
env.test=
env.prod=
env.stress=
env.mc=true

#mode.local=false


#-- CVS INFORMATION --#
cvs.user=pbelgaumkar
cvs.pass=pb1554
cvs.server=10.11.20.75
cvs.root=/usr/local/cvsrep/cvsrootKohls
cvs.fetch.dir=/kohls/prop/of/build/temp/
cvs.module.name=Phase1-OMSUpgrade/dev
devhome=${cvs.fetch.dir}/${cvs.module.name}
release.dir=./release

#-- SIM Build Properties --#
pca.extn.folder=/kohls/prop/of/PCAExtensions
pca.plugin.id=com.kohls.ibm.ocf.pca
pca.plugin.version=0.0.1
pca.sso.plugin.id=com.kohls.ibm.pca.sso
pca.sso.plugin.version=0.0.1
pca.update.dir=rcpupdates
pca.update.windir=win32.win32.x86
sim.appid=YFSSYS00006
sim.version=9.1.0.6
