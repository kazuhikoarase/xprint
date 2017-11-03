/**
 * trim-mark.js
 * @author Kazuhiko Arase
 * トリムマーク
 */

function paint(context) {

	var g = context.graphics;
	
	var bounds = context.bounds;
	
	var gap = context.parseNumber("3mm");
	
	var size = context.parseNumber("5mm");
	
	function draw_line(g, x, y, w, h) {
		g.drawLine(x, y, x + w, y + h);
	}
	function draw_trim(g, x ,y, hDir, vDir) {
		draw_line(g, x + gap * hDir, y, size * hDir, 0);
		draw_line(g, x + gap * hDir, y, 0, size * vDir);
		draw_line(g, x, y + gap * vDir, size * hDir, 0);
		draw_line(g, x, y + gap * vDir, 0, size * vDir);
	}
	function draw_cross(g, x, y, w, h, dW, dH) {
		g.drawLine(x - w / 2 + dW, y, x + w / 2 + dW, y)
		g.drawLine(x, y - h / 2 + dH, x, y + h / 2 + dH)
	}

	g.setStroke(context.createStroke("0.1mm") );
	
	// トリムマークを引く。
	
	draw_trim(g, 0, 0, -1, -1);
	draw_trim(g, bounds.width, 0, 1, -1);
	draw_trim(g, 0, bounds.height, -1, 1);
	draw_trim(g, bounds.width, bounds.height, 1, 1);
	
	draw_cross(g, bounds.width / 2, -gap, size, size, 0, -size / 4);
	draw_cross(g, bounds.width / 2, bounds.height + gap, size, size, 0, size / 4);
	draw_cross(g, -gap, bounds.height / 2, size, size, -size / 4, 0);
	draw_cross(g, bounds.width + gap, bounds.height / 2, size, size, size / 4, 0);
}
