PROGNAME      := ${projectName}

COMMON_SOURCES := syscalls.c uart.c
<#if spiList?has_content>
# SPI Driver
COMMON_SOURCES += spi.c
</#if>

include $(CCSDK_HOME)/common/generic.mk
