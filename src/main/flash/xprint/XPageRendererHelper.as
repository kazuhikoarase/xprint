package xprint {
	
	import flash.display.Graphics;
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.GraphicsPathCommand;
	import flash.display.GraphicsPathWinding;
	import flash.geom.Matrix;
	
	/**
	 * XPageRendererHelper
	 * @author Kazuhiko Arase
	 */
	internal class XPageRendererHelper {

		private var _color : uint;
		private var _offsetX : Number;
		private var _offsetY : Number;
		
		public function XPageRendererHelper()  {
		}

		public function render(g : Graphics, cmdList : Array) : void {

			_color = 0x000000;
			_offsetX = 0;
			_offsetY = 0;

			for (var i : int = 0; i < cmdList.length; i++) {

				var cmd : Array = cmdList[i];

				switch(cmd[0]) {

				case XRawCommand.CMD_TYPE_TRANSLATE :
					_offsetX += cmd[1];
					_offsetY += cmd[2];
					break;

				case XRawCommand.CMD_TYPE_SET_COLOR :
					_color = cmd[1];
					break;

				case XRawCommand.CMD_TYPE_FILL :
					g.beginFill(_color);	
					drawPath(g, cmd[1], cmd[2]);
					g.endFill();
					break;

				case XRawCommand.CMD_TYPE_DRAW_IMAGE :
					drawImage(
						g, Bitmap(cmd[5].content).bitmapData,
						cmd[1], cmd[2], cmd[3], cmd[4]
					);
					break;

				default :
					throw new Error("unknown command:" + cmd[0]);
				}
				
			}
		}

		private function drawPath(
			g : Graphics,
			windingMode : int,
			pathList : Array
		) : void {
			
			var commands : Vector.<int> = new Vector.<int>();
			var data : Vector.<Number> = new Vector.<Number>();

			for (var i : int = 0; i < pathList.length; i++) {
				
				var path : Array = pathList[i];
				
				switch(path[0]) {
	
				case PathIterator.SEG_MOVETO :
					commands.push(GraphicsPathCommand.MOVE_TO);
					data.push(path[1] + _offsetX, path[2] + _offsetY);
					break;
	
				case PathIterator.SEG_LINETO :	
					commands.push(GraphicsPathCommand.LINE_TO);
					data.push(path[1] + _offsetX, path[2] + _offsetY);
					break;
	
				case PathIterator.SEG_QUADTO :	
					commands.push(GraphicsPathCommand.CURVE_TO);
					data.push(path[1] + _offsetX, path[2] + _offsetY);
					data.push(path[3] + _offsetX, path[4] + _offsetY);
					break;
	
				case PathIterator.SEG_CUBICTO :	

					var p0x : Number = path[1] + _offsetX;
					var p0y : Number = path[2] + _offsetY;
					var p1x : Number = path[3] + _offsetX;
					var p1y : Number = path[4] + _offsetY;
					var p2x : Number = path[5] + _offsetX;
					var p2y : Number = path[6] + _offsetY;
					
					var p01x : Number = (p0x + p1x) / 2;
					var p01y : Number = (p0y + p1y) / 2;

					commands.push(GraphicsPathCommand.CURVE_TO);
					data.push(p0x, p0y);
					data.push(p01x, p01y);

					commands.push(GraphicsPathCommand.CURVE_TO);
					data.push(p1x, p1y);
					data.push(p2x, p2y);

					break;
	
				case PathIterator.SEG_CLOSE :
					break;

				default :
					throw new Error("unknown path:" + path[0]);
				}
			}
			
			g.drawPath(commands, data, 
				(windingMode == PathIterator.WIND_NON_ZERO)?
					GraphicsPathWinding.NON_ZERO :
					GraphicsPathWinding.EVEN_ODD);
		}
		
		private function drawImage(
			g : Graphics, image : BitmapData,
			x : Number, y : Number, width : Number, height : Number
		) : void {

	        if (width > 0 && height == 0) {
	            //  高さを幅に合わせて再計算
	            height = width * image.height / image.width;
	        } else if (width == 0 && height > 0) {
	            //  幅を高さに合わせて再計算
	            width = height * image.width / image.height;
	        }
			
	        var scaleX : Number = width / image.width;
	        var scaleY : Number = height / image.height;

			x += _offsetX;
			y += _offsetY;

			var matrix : Matrix = new Matrix(scaleX, 0, 0, scaleY, x, y);
			g.beginBitmapFill(image, matrix, false, false);
			g.drawRect(x, y, width, height);
			g.endFill();
		}
	}
}