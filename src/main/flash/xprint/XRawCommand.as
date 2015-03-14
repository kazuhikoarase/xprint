package xprint {
		
	/**
	 * XRawCommand
	 * @author Kazuhiko Arase
	 */
	internal class XRawCommand {
	
		public static const CMD_TYPE_END : int 	 		= 0x10;
		public static const CMD_TYPE_TRANSLATE : int 	= 0x11;
		public static const CMD_TYPE_FILL : int 		= 0x12;
		public static const CMD_TYPE_SET_COLOR : int 	= 0x13;
		public static const CMD_TYPE_DRAW_IMAGE : int 	= 0x14;
	
		public static const PATH_TYPE_END	: int = 0x20;
		public static const PATH_TYPE_PATH	: int = 0x21;
	}

}