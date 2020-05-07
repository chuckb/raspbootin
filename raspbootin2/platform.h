#ifndef RASPBOOTIN2_PLATFORM
#define RASPBOOTIN2_PLATFORM
  #ifdef PLATFORM_RPI
      #define PBASE 0x20000000
      #define PNAME "Raspberry Pi"
  #elif PLATFORM_RPIBP
      #define PBASE 0x20000000
      #define PNAME "Raspberry Pi Model B+"
  #elif defined PLATFORM_RPI2
      #define PBASE 0x3F000000
      #define PNAME "Raspberry Pi 2"
  #elif defined PLATFORM_RPI3
      #define PBASE 0x3F000000
      #define PNAME "Raspberry Pi 3"
  #endif
  /**
   * \brief put a 32-bit word to a specific address
   * \param[in] r0 the address to write to
   * \param[in] r1 the value for writing
   * \author David Welch
   **/
  extern void PUT32 ( unsigned int, unsigned int );
  /**
   * \brief get a 32-bit word from an address
   * \param[in] r0 the address to read from
   * \return the read value
   * \author David Welch
   **/
  extern unsigned int GET32 ( unsigned int );
  /**
   * \brief jump; just waste time
   * \author David Welch
   **/
  extern void dummy ( unsigned int );

  /**
   * \brief jump to the specified address
   * \param[in] r0 the address to jump to
   * \author Stefan Naumann
   **/
  extern void BOOTUP ( unsigned int );

  // GPIO
  #define GPFSEL1         (PBASE+0x00200004)
  #define GPFSEL2         (PBASE+0x00200008)
  #define GPSET0          (PBASE+0x0020001C)
  #define GPCLR0          (PBASE+0x00200028)
  #define GPPUD           (PBASE+0x00200094)
  #define GPPUDCLK0       (PBASE+0x00200098)

  // AUX
  #define AUX_ENABLES     (PBASE+0x00215004)
  #define AUX_MU_IO_REG   (PBASE+0x00215040)
  #define AUX_MU_IER_REG  (PBASE+0x00215044)
  #define AUX_MU_IIR_REG  (PBASE+0x00215048)
  #define AUX_MU_LCR_REG  (PBASE+0x0021504C)
  #define AUX_MU_MCR_REG  (PBASE+0x00215050)
  #define AUX_MU_LSR_REG  (PBASE+0x00215054)
  #define AUX_MU_MSR_REG  (PBASE+0x00215058)
  #define AUX_MU_SCRATCH  (PBASE+0x0021505C)
  #define AUX_MU_CNTL_REG (PBASE+0x00215060)
  #define AUX_MU_STAT_REG (PBASE+0x00215064)
  #define AUX_MU_BAUD_REG (PBASE+0x00215068)

  // Timer
  #define ARM_TIMER_CTL   (PBASE+0x0000B408)
  #define ARM_TIMER_CNT   (PBASE+0x0000B420)
#endif
