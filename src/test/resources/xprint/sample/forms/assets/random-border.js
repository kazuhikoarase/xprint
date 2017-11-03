/**
 * random-border.js
 * @author Kazuhiko Arase
 * ランダム枠線
 */

function paint(context) {

	var g = context.graphics;
	
	var bounds = context.bounds;

	// ランダム
	var rand = new (Java.type('java.util.Random'))(0);


	var rnd = context.parseNumber("6mm");
	var div = context.parseNumber("2mm");

	var hDiv = bounds.width / div;
	var vDiv = bounds.height / div;
	
	var start = null;

	// パスオブジェクト作成
	var path = new (Java.type('java.awt.geom.GeneralPath'))();

	function random_point(x, y) {
		return [
			x + (rand.nextDouble() - 0.5) * rnd,
			y + (rand.nextDouble() - 0.5) * rnd]
	}
	
	for (var i = 0; i < hDiv; i++) {
		var p = random_point(bounds.width * i / hDiv, 0);
		if (i == 0) {
			start = p;
			path.moveTo(p[0], p[1]);
		} else {
			path.lineTo(p[0], p[1]);
		}
	}
	for (var i = 0; i < vDiv; i++) {
		var p = random_point(bounds.width, bounds.height * i / vDiv);
		path.lineTo(p[0], p[1]);
	}
	for (var i = 0; i < hDiv; i++) {
		var p = random_point(bounds.width - bounds.width * i / hDiv, bounds.height);
		path.lineTo(p[0], p[1]);
	}
	for (var i = 0; i < vDiv; i++) {
		var p = random_point(0, bounds.height - bounds.height * i / vDiv);
		path.lineTo(p[0], p[1]);
	}
	
	path.lineTo(start[0], start[1]);
	g.setColor(context.parseColor("#9900ff") );
	g.setStroke(context.createStroke("1mm") );
	g.draw(path);
}
