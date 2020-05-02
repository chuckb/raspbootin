SECTIONS {
	.init 0x02000000 : {
		*(.init)
	}
	
	.text : {
		*(.text)
	}
	
	.data : {
		*(.data)
	}

	.rodata : {
		*(.rodata)
	}
    .bss : {
        *(.bss)
    }

}
