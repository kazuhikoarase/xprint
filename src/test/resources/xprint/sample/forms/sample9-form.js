
function paint(context) {
	
	// グラフィックス取得
	var g = context.graphics;
	
	// 描画領域取得
	var bounds = context.bounds;

	var frc = java.awt.font.FontRenderContext(null, true, true);
	var font = context.nodeFont;
	var text = '' + context.nodeText;
	var x = 0;
	var y = context.parseNumber('50mm');
	var t = 0;
    var dt = 0.1;
	
	for (var i = 0; i < text.length; i += 1) {

		var c = text.charAt(i);
	    var layout = java.awt.font.TextLayout(c, font, frc);
	    var tw = layout.advance;
	    var th = layout.ascent + layout.descent;
	    
	    dt -= 0.005

	    g.setColor(context.parseColor('#000000') );

	    var transform = java.awt.geom.AffineTransform(1, 0, 0, 1, 0, 0);
        transform.translate(x, y);
//        transform.translate(layout.ascent, -tw);
        transform.rotate(t);
//        transform.concatenate(java.awt.geom.AffineTransform(1, 0, 0, -1, 0, 0) );
        g.drawText(layout, transform);
        
        x += Math.cos(t) * tw;
        y += Math.sin(t) * tw;
        t += dt;
	}
}