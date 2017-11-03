/**
 * background-color.js
 * @author Kazuhiko Arase
 * 背景色描画
 */

function paint(context) {

	var g = context.graphics;
	
	var bounds = context.bounds;

	// ノードテキストを実行
	eval(String(context.nodeText) );

	// 実行したノードテキストにより、引数を取得
	var gap = context.parseNumber(args["gap"]);
	var color = context.parseColor(args["color"]);
	
	// 背景描画
	
	// 指定された gap 分、大きめの長方形を描く。
	g.setColor(color);
	g.fill(new (Java.type('java.awt.geom.Rectangle2D.Double'))(
		-gap, -gap, bounds.width + gap * 2, bounds.height + gap * 2) );
}
