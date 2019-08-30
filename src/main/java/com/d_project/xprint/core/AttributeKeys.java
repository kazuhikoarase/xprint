package com.d_project.xprint.core;

/**
 * AttributeKeys
 * @author Kazuhiko Arase
 */
public interface AttributeKeys {

    String WIDTH	= "width";
    String HEIGHT	= "height";

    String ALIGN	= "align";
    String SRC		= "src";

	// text
    String LINE_GAP         = "line-gap";

	// bar
    String TYPE             = "type";
    String UNIT_WIDTH       = "unit-width";

	// qr
    String TYPE_NUMBER = "type-number";
    String ERROR_CORRECTION_LEVEL = "error-correction-level";

    String COLOR            = "color";
    String BACKGROUND_COLOR = "background-color";
    
    String FONT_FAMILY      = "font-family";
    String FONT_SIZE        = "font-size";
    String FONT_WEIGHT      = "font-weight";


    String CONTENT_ALIGN            = "content-align";
    String CONTENT_VERTICAL_ALIGN   = "content-vertical-align";

    
    String SELECT           = "select";
    String PAGE_BREAK       = "page-break";

    String PADDING          = "padding";
    String PADDING_TOP      = "padding-top";
    String PADDING_BOTTOM   = "padding-bottom";
    String PADDING_LEFT     = "padding-left";
    String PADDING_RIGHT    = "padding-right";

    String MARGIN           = "margin";
    String MARGIN_TOP       = "margin-top";
    String MARGIN_BOTTOM    = "margin-bottom";
    String MARGIN_LEFT      = "margin-left";
    String MARGIN_RIGHT     = "margin-right";

    String BORDER_COLOR         = "border-color";
    String BORDER_WIDTH         = "border-width";
    String BORDER_WIDTH_TOP     = "border-width-top";
    String BORDER_WIDTH_BOTTOM  = "border-width-bottom";
    String BORDER_WIDTH_LEFT    = "border-width-left";
    String BORDER_WIDTH_RIGHT   = "border-width-right";

    String BORDER_WIDTH_LT_RB   = "border-width-lt-rb";
    String BORDER_WIDTH_LB_RT   = "border-width-lb-rt";

    String STROKE_CAP = "stroke-cap";
    String STROKE_JOIN = "stroke-join";
    String STROKE_MITER_LIMIT = "stroke-miter-limit";
    String STROKE_DASH_PATTERN = "stroke-dash-pattern";
    String STROKE_DASH_PHASE = "stroke-dash-phase";

    String CORNER_WIDTH  = "corner-width";
    String CORNER_HEIGHT = "corner-height";
    String CORNER_RADIUS = "corner-radius";

    String PARAGRAPH = "paragraph";

    String PAGE_NO    = "page-no";
    String PAGE_COUNT = "page-count";
    
}
