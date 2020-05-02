/**
 * \file jtag.h
 * \author Chuck Benedict
 * \date 27 April 2020
 * \brief prototypes and definitions for JTAG-related code on the Raspberry Pi.
 **/

#ifndef RASPBOOTIN2_JTAG
#define RASPBOOTIN2_JTAG

#include "platform.h"

/**
 * \brief enable the JTAG-interface on Alt-4 
 * \author Chuck Benedict
 **/
void jtag_init ( void );

#endif
