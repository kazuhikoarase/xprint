
function paint(context) {

	// グラフィックス取得
	var g = context.graphics;
	
	// 描画領域取得
	var bounds = context.bounds;
	
	// 線の色を設定
	g.setColor(context.parseColor('99ffcc') );
	
	// 線種を設定
	g.setStroke(context.createStroke('0.3mm') );
	
	var div = 20
	for (var i = 0; i <= div; i += 1) {
		var w = i * bounds.width / div;
		var h = i * bounds.height / div;
		g.drawLine(bounds.width, h, bounds.width - w, bounds.height)
	}		
}