package xprint {

	import flash.display.Graphics;
	import flash.display.Loader;
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.net.URLLoader;
	import flash.net.URLLoaderDataFormat;
	import flash.net.URLRequest;
	import flash.utils.ByteArray;
	import flash.utils.Endian;

	/**
	 * RAWデータの読込が完了し、レンダリング可能になると送出されます。
	 * @eventType flash.events.Event.COMPLETE
	 */
 	[Event(name="complete", type="flash.events.Event")]

	/**
	 * XPageRenderer オブジェクトは、RAWデータを読み込んで、レンダリングする際に使用します。
	 * @author Kazuhiko Arase
	 */
	public class XPageRenderer extends EventDispatcher {

		private var _pageWidth : Number;
		private var _pageHeight : Number;
		private var _pages : Array;
		private var _loadingCount : int;

		/**
		 * XPageRenderer オブジェクトを作成します。
		 */		
		public function XPageRenderer() {
		}

		/**
		 * ページの幅
		 */		
		public function get pageWidth() : Number {
			return _pageWidth;
		}

		/**
		 * ページの高さ
		 */		
		public function get pageHeight() : Number {
			return _pageHeight;
		}

		/**
		 * 総ページ数
		 */		
		public function get numPages() : int {
			return _pages.length;
		}

		/**
		 * ページをレンダリングする。
		 * @param g グラフィックス
		 * @param pageIndex ページのインデックス
		 */
		public function render(g : Graphics, pageIndex : int) : void {

			var helper : XPageRendererHelper = new XPageRendererHelper();
			helper.render(g, _pages[pageIndex]);
		}

		/**
		 * 指定された URL から RAWデータを読み込んで、parse を呼び出します。
		 * @param url RAWデータのURL
		 */
		public function load(url : String) : void {

			var rawLoader : URLLoader = new URLLoader();
			rawLoader.dataFormat = URLLoaderDataFormat.BINARY;
			rawLoader.load(new URLRequest(url) );
			rawLoader.addEventListener(Event.COMPLETE, rawLoader_completeHandler);
		}
		
		private function rawLoader_completeHandler(event : Event) : void {
			var loader : URLLoader = event.target as URLLoader;
			var rawData : ByteArray = loader.data as ByteArray;
			parse(rawData);
		}

		/**
		 * RAWデータを解析し、レンダリングデータを作成する。
		 * @param rawData RAWデータ
		 */
		public function parse(rawData : ByteArray) : void {

			rawData.uncompress();
			rawData.endian = Endian.BIG_ENDIAN;

			_pageWidth = rawData.readDouble();
			_pageHeight = rawData.readDouble();
			_pages = new Array();
			_loadingCount = 0;
			
			var numPages : int = rawData.readInt();
			for (var i : int = 0; i < numPages; i++) {
				_pages.push(parsePage(rawData) );
			}
	
			if (rawData.position != rawData.length) {
				throw new Error(rawData.position + " != " + rawData.length);
			}

			dispatchCompleteEvent();
		}
		
		private function parsePage(rawData : ByteArray) : Array {
			
			var cmdList : Array = new Array();
	
			var cmdType : int;
			
			while ( (cmdType = rawData.readByte() ) != XRawCommand.CMD_TYPE_END) {
	
				switch(cmdType) {
	
				case XRawCommand.CMD_TYPE_TRANSLATE :
					cmdList.push([cmdType, rawData.readDouble(), rawData.readDouble()]);
					break;
	
				case XRawCommand.CMD_TYPE_FILL :
	
					var windingMode : int = rawData.readByte();

					var pathList : Array = new Array();
					
					cmdList.push([
						cmdType,
						windingMode,
						pathList
					]);
	
					var pathType : int;
	
					while ( (pathType = rawData.readByte() ) != XRawCommand.PATH_TYPE_END) {
	
						if (pathType != XRawCommand.PATH_TYPE_PATH) {
							throw new Error("bad pathType:" + pathType);
						}

						var path : Array = [rawData.readByte()];
								
						switch(path[0]) {
						
						case PathIterator.SEG_MOVETO :
						case PathIterator.SEG_LINETO :
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							break;
		
						case PathIterator.SEG_QUADTO :
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							break;
		
						case PathIterator.SEG_CUBICTO :
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							path.push(rawData.readDouble() );						
							break;
						
						case PathIterator.SEG_CLOSE :
							break;
		
						default :
							throw new Error("seg:" + path[0]);
						}

						pathList.push(path);
					}		
					break;
	
				case XRawCommand.CMD_TYPE_SET_COLOR :
					cmdList.push([cmdType, rawData.readInt()]);
					break;
	
				case XRawCommand.CMD_TYPE_DRAW_IMAGE :

					var drawCmd : Array = [
						cmdType,
						rawData.readDouble(),
						rawData.readDouble(),
						rawData.readDouble(),
						rawData.readDouble()
					];	
	
					var jpegData : ByteArray = new ByteArray();
					var length : int = rawData.readInt();
					rawData.readBytes(jpegData, 0, length);
					
					drawCmd.push(loadAssets(jpegData) );
					
					cmdList.push(drawCmd);
					break;
	
				default :
					throw new Error("bad cmdType:" + cmdType);
				}
			}
	
			return cmdList;
		}

		private function loadAssets(data : ByteArray) : Loader {
			var loader : Loader = new Loader();
			loader.contentLoaderInfo.addEventListener(Event.COMPLETE, loader_completeHandler);
			loader.loadBytes(data);
			_loadingCount++;
			return loader;
		}
		
		private function loader_completeHandler(event : Event) : void {

			_loadingCount--;

			dispatchCompleteEvent();
		}
		
		private function dispatchCompleteEvent() : void {

			if (_loadingCount > 0) {
				// loading assets.
				return;
			}

			dispatchEvent(new Event(Event.COMPLETE) );	
		}
	}
}
