/**
 * 郵便番号
 */

function getContentSize(context) {
	return [context.parseNumber("47.7mm"), context.parseNumber("8mm")];
}

function paint(context) {

	// ノードテキストを実行
	eval(String(context.nodeText) );

	paingImpl(context, args);
}

function paingImpl(context, args) {

	var g = context.graphics;

	var bounds = context.bounds;

	// 郵便番号
	var zipCode = args["zipCode"];

	// 枠描画フラグ
	var showBounds = args["showBounds"];

	// フォント
	var font = deriveFont(
		context.getFont("serif"), 
		1.0, 
		context.parseNumber("22pt") );
	
	// セルの位置リスト
	var posList = [
		context.parseNumber("0mm"),
		context.parseNumber("7mm"),
		context.parseNumber("14mm"),
		context.parseNumber("21.6mm"),
		context.parseNumber("28.4mm"),
		context.parseNumber("35.2mm"),
		context.parseNumber("42.0mm")
	];
	
	function createStroke(width) {
		return java.awt.BasicStroke(
			context.parseNumber(width),
			java.awt.BasicStroke.CAP_BUTT,
			java.awt.BasicStroke.JOIN_MITER);
	}
	
	// 全体の幅と高さ
	var size = getContentSize(context);
	var rectW = size[0];
	var rectH = size[1]; 

	// セルの幅
	var cell_w = context.parseNumber("5.7mm");

	function getX(pos) {
		return bounds.width - rectW + pos;
	}
	function getY(pos) {
		return pos;
	}

	// 枠線
	function drawBorders() {

		g.setColor(context.parseColor("#ff3300") );
	
		// 上3桁
		g.setStroke(createStroke("0.5mm") );
		for (var i = 0; i < 3; i++) {
			g.draw(java.awt.geom.Rectangle2D.Double(
				getX(posList[i]), getY(0), cell_w, rectH) );
		}
	
		// ハイフン
		g.drawLine(
			getX(posList[2] + cell_w),
			getY(rectH / 2),
			getX(posList[3]),
			getY(rectH / 2) );
	
		// 下4桁
		g.setStroke(createStroke("0.3mm") );
		for (var i = 3; i < 7; i++) {
			g.draw(java.awt.geom.Rectangle2D.Double(
				getX(posList[i]), getY(0), cell_w, rectH) );
		}
	}

	// 数値
	function drawNumbers() {

		g.setColor(context.parseColor("#000000") );
		
		var frc = java.awt.font.FontRenderContext(null, true, true);

		for (var i = 0; i < Math.min(zipCode.length, posList.length); i++) {
			var layout = java.awt.font.TextLayout(
				zipCode.charAt(i), font, frc);
			var dx = (cell_w - layout.advance) / 2;
			var dy = (rectH - layout.ascent - layout.descent) / 2;
			g.drawText(layout, 
				getX(posList[i]) + dx,
				getY(layout.ascent) + dy);
		}
	}
	
	if (showBounds == "true") {
		drawBorders();
	}
	
	drawNumbers();
}

function deriveFont(font, weight, size) {
	var attr = java.util.HashMap();
	attr.put(java.awt.font.TextAttribute.WEIGHT, 
			java.lang.Float(weight) );
	attr.put(java.awt.font.TextAttribute.SIZE,
			java.lang.Float(size) );
	return font.deriveFont(attr);
}
