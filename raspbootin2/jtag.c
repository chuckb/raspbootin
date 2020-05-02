#include "jtag.h"

//------------------------------------------------------------------------
void jtag_init ( void )
{
  unsigned int ra;

//ARM_TCK  D0  P1-22 OUT (25 ALT4)
//ARM_TDI  D1  P1-37  IN (26 ALT4)
//ARM_TDO  D2  P1-18 OUT (24 ALT4)
//ARM_TMS  D3  P1-13 OUT (27 ALT4)
//ARM_TRST D4  P1-15  IN (22 ALT4)
//ARM_GND  GND P1-39

  ra=GET32(GPFSEL2);
  ra&=~(7<<6);    //gpio22
  ra|=  3<<6;     //alt4 ARM_TRST
  ra&=~(7<<12);   //gpio24
  ra|=  3<<12;    //alt4 ARM_TDO
  ra&=~(7<<15);   //gpio25
  ra|=  3<<15;    //alt4 ARM_TCK
  ra&=~(7<<18);   //gpio26
  ra|=  3<<18;    //alt4 ARM_TDI
  ra&=~(7<<21);   //gpio27
  ra|=  3<<21;    //alt4 ARM_TMS
  PUT32(GPFSEL2,ra);

}