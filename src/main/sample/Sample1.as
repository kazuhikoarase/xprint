package {

	import flash.display.*;
	import flash.events.Event;
	
	import xprint.XPageRenderer;

	public class Sample1 extends Sprite {

		public function Sample1() {
			addEventListener(Event.ADDED_TO_STAGE, addedToStageHandler);
		}
		
		private function addedToStageHandler(event : Event) : void {

			var url : String = "out/sample7.dat";

			var renderer : XPageRenderer = new XPageRenderer();
			renderer.addEventListener(Event.COMPLETE, renderer_completeHandler);
			renderer.load(url);
		}
		
		private function renderer_completeHandler(event : Event) : void {
			
			var renderer : XPageRenderer = event.target as XPageRenderer;
			var sp : Sprite = new Sprite();

			var g : Graphics = sp.graphics;
			
			g.clear();
			g.beginFill(0xffffff);
			g.drawRect(0, 0, renderer.pageWidth, renderer.pageHeight);
			g.endFill();

			renderer.render(g, 0);
			
			sp.scaleX = 0.5;
			sp.scaleY = 0.5;
			addChild(sp);
		}
	}
}

