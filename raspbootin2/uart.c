#include "uart.h"

//GPIO14  TXD1
//GPIO15  RXD1
//------------------------------------------------------------------------
unsigned int uart_lcr ( void )
{
    return(GET32(AUX_MU_LSR_REG));
}
//------------------------------------------------------------------------
unsigned int uart_recv ( void )
{
    while(1)
    {
        if(GET32(AUX_MU_LSR_REG)&0x01) break;
    }
    return(GET32(AUX_MU_IO_REG)&0xFF);
}
//------------------------------------------------------------------------
unsigned int uart_recv_timeout ( unsigned int timeout_ms )
{
    unsigned int ra;
    unsigned int rb;

    rb = GET32(ARM_TIMER_CNT);
    ra = GET32(ARM_TIMER_CNT);
    while((ra-rb) < timeout_ms)
    {
        if(GET32(AUX_MU_LSR_REG)&0x01) {
          return(GET32(AUX_MU_IO_REG)&0xFF);
        };
        ra = GET32(ARM_TIMER_CNT);
    }
    return 0xFFFFFFFF;
}
//------------------------------------------------------------------------
unsigned int uart_check ( void )
{
    if(GET32(AUX_MU_LSR_REG)&0x01) return(1);
    return(0);
}
//------------------------------------------------------------------------
void uart_send ( unsigned int c )
{
    while(1)
    {
        if(GET32(AUX_MU_LSR_REG)&0x20) break;
    }
    PUT32(AUX_MU_IO_REG,c);
}
//------------------------------------------------------------------------
void uart_flush ( void )
{
    while(1)
    {
        if((GET32(AUX_MU_LSR_REG)&0x40)!=0) break;
    }
}

void uart_puts ( char* str )
{
    while (*str)
    {
        uart_send ( (unsigned int) *str );
        str++;
    }    
}

//------------------------------------------------------------------------
void uart_init ( void )
{
    unsigned int ra;

    // Enable the mini UART
    PUT32(AUX_ENABLES,1);
    PUT32(AUX_MU_IER_REG,0);
    PUT32(AUX_MU_CNTL_REG,0);
    PUT32(AUX_MU_LCR_REG,3);
    PUT32(AUX_MU_MCR_REG,0);
    PUT32(AUX_MU_IER_REG,0);
    PUT32(AUX_MU_IIR_REG,0xC6);
    PUT32(AUX_MU_BAUD_REG,270);
    ra=GET32(GPFSEL1);
    ra&=~(7<<12); //gpio14
    ra|=2<<12;    //alt5
    ra&=~(7<<15); //gpio15
    ra|=2<<15;    //alt5
    PUT32(GPFSEL1,ra);
    PUT32(GPPUD,0);
    for(ra=0;ra<150;ra++) dummy(ra);
    PUT32(GPPUDCLK0,(1<<14)|(1<<15));
    for(ra=0;ra<150;ra++) dummy(ra);
    PUT32(GPPUDCLK0,0);
    PUT32(AUX_MU_CNTL_REG,3);

    // Set the ARM timer for millisecond resolution
    // We do this for timeout processing
    PUT32(ARM_TIMER_CTL, 0x00F90000);   // Set prescale
    PUT32(ARM_TIMER_CTL, 0x00F90200);   // Prescale = 0xF9 or 249; Freq = 250MHz / (249 + 1) = 1 microsecond
}
//------------------------------------------------------------------------
void uart_clear_fifos ( void )
{
  PUT32(AUX_MU_IIR_REG,0xC6);
}
