package com.d_project.xprint.io;

/**
 * XRawCommand
 * @author Kazuhiko Arase
 */
public interface XRawCommand {

	int CMD_TYPE_END 	 	= 0x10;
	int CMD_TYPE_TRANSLATE 	= 0x11;
	int CMD_TYPE_FILL 		= 0x12;
	int CMD_TYPE_SET_COLOR 	= 0x13;
	int CMD_TYPE_DRAW_IMAGE 	= 0x14;

	int PATH_TYPE_END 	= 0x20;
	int PATH_TYPE_PATH	= 0x21;
}