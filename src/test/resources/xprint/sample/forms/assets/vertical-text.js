/**
 * vertical-text.js
 * @author Kazuhiko Arase
 * 縦書き文字列
 */

function getContentSize(context) {

	var frc = new (Java.type('java.awt.font.FontRenderContext'))(null, true, true);
	
	var font = context.nodeFont;
	var text = String(context.nodeText);
	var width = 0;
	var height = 0;
    
    for (var i = 0; i < text.length; i++) {
    	var c = text.charAt(i);
		var layout = new (Java.type('java.awt.font.TextLayout'))(c, font, frc);
		var tw = layout.advance;
		var th = layout.ascent + layout.descent;
		var vertical = isVertical(c);
		var w = !vertical? tw : th;
		var h = !vertical? th : tw;

		width = Math.max(width, w);
		height += h;
    }
    
    return [width, height];
}

function paint(context) {

	function createStroke(width) {
        return new (Java.type('java.awt.BasicStroke'))(
            context.parseNumber(width),
            Java.type('java.awt.BasicStroke').CAP_BUTT,
            Java.type('java.awt.BasicStroke').JOIN_MITER);
	}
	
	var rect = context.bounds;
    
	var g = context.graphics;
	g.setStroke(createStroke("0.1mm") );
	
	var frc = new (Java.type('java.awt.font.FontRenderContext'))(null, true, true);
	var font = context.nodeFont;
	var text = String(context.nodeText);
	var x = 0;
	var y = 0;

	for (var i = 0; i < text.length; i++) {
		var c = text.charAt(i);
	    var layout = new (Java.type('java.awt.font.TextLayout'))(c, font, frc);
	    var tw = layout.advance;
	    var th = layout.ascent + layout.descent;
	    var vertical = isVertical(c);
	    var w = !vertical? tw : th;
	    var h = !vertical? th : tw;
	    var dx = (rect.width - w) / 2;
	    var dy = vertical? layout.descent : 0;
	    g.setColor(context.parseColor("#000000") );
	    drawTextImpl(g, layout, x + dx, y + dy + h, vertical);
/*
	    g.setColor(context.parseColor("#0000ff") );
		g.draw(java.awt.geom.Rectangle2D.Double(x + dx, y + dy, w, h) );
*/		
		y += h;
	}
}
	
function drawTextImpl(g, layout, x, y, vertical) {
	
	vertical = vertical || false;
	
    var tw = layout.advance;
    var th = layout.ascent + layout.descent;

    if (vertical) {
        var transform = new (Java.type('java.awt.geom.AffineTransform'))(1, 0, 0, 1, 0, 0);
        transform.translate(x, y);
        transform.translate(layout.ascent, -tw);
        transform.rotate(Math.PI * 0.5);
        transform.concatenate(new (Java.type('java.awt.geom.AffineTransform'))(1, 0, 0, -1, 0, 0) );
        g.drawText(layout, transform);
    } else {
        var transform = new (Java.type('java.awt.geom.AffineTransform'))(1, 0, 0, 1, x, y);
        g.drawText(layout, transform);
    }
}


function isVertical(c) {
	return c.match(VERTICAL_CHAR_PATTERN);
}

var VERTICAL_CHAR_PATTERN = /[\u0020\u0027-\u0029\u002c-\u002e\u003a-\u003e\u005b-\u0060\u007b-\u007e\u3000\u3001\u3002\u3008-\u3011\u3013-\u301f\u3099-\u309c\u30a0\u30fb\u30fc\uff07-\uff09\uff0c-\uff0e\uff1a-\uff1e\uff3b-\uff40\uff5b-\uff65\uff70\uff9e-\uff9f]/g;
