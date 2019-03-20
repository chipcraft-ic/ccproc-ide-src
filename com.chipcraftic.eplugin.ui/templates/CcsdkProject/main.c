<#setting number_format="computer">
#include <stdio.h>
#include <board.h>
#include <ccproc.h>
#include <ccproc-amba.h>
<#if spiList?has_content>
#include <ccproc-amba-spi.h>
#include <spi.h>
</#if>
<#if uartList?has_content>
#include <ccproc-amba-uart.h>
</#if>

<#list spiList as spiConfig>
static void initSPI${spiConfig.index}() {
	volatile amba_spi_t *spi = AMBA_SPI_PTR(${spiConfig.index});
    <#if spiConfig.master>
    spi_enable_master_mode(spi);
    </#if>
    spi_set_baud_div(spi, ${spiConfig.baudrate}, PERIPH0_FREQ);
    <#if spiConfig.msbFirst>
    spi_transmit_data_MSB_first(spi);
    <#else>
    spi_transmit_data_LSB_first(spi);
    </#if>
    spi_set_transmission_mode(spi, SPI_CTRL_${spiConfig.transmissionMode});
    spi_set_frame_length(spi, SPI_CTRL_${spiConfig.frameLength});
    spi_enable(spi);
}

</#list>
<#list uartList as uartConfig>
static void initUART${uartConfig.index}() {
	volatile amba_uart_t *uart = AMBA_UART_PTR(${uartConfig.index});
    uint32_t baudrate = ${uartConfig.baudrate};
    uart->PRES = AMBA_UART_PRES((PERIPH0_FREQ / baudrate) / 16, (PERIPH0_FREQ / baudrate) % 16);
    uart->MODE = UART_MODE_CHRL8 | UART_MODE_STOP_BITS_${uartConfig.stopBits} | UART_MODE_PARITY_${uartConfig.parity};
    uart->CTRL = ${uartConfig.transmitter?string('UART_CTRL_TXEN|', '')}${uartConfig.receiver?string('UART_CTRL_RXEN|', '')}${uartConfig.rts?string('UART_CTRL_RTSEN|', '')}${uartConfig.cts?string('UART_CTRL_CTSEN|', '')}0;
}

</#list>

int main(void)
{
	/* Initialization */
    <#list spiList as spiConfig>
    initSPI${spiConfig.index}();
    </#list>
    <#list uartList as uartConfig>
    initUART${uartConfig.index}();
    </#list>

    /* Display hello world */
    printf("Hello World\n");

    while (1) {
        /* Main loop - add user code here */
    }

    return 0;
}
