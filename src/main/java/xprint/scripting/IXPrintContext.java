package xprint.scripting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

public interface IXPrintContext {

	/**
	 * 描画領域を取得する。
	 * @return 描画領域
	 */
	Rectangle2D getBounds();
	
	/**
	 * グラフィックオブジェクトを取得する。
	 * @return グラフィックオブジェクト
	 */
	IXPrintGraphics getGraphics();
	
	/**
	 * nn + mm|cm|in(インチ)|pt 形式の数値文字列をパースする。
	 * @param sNumber 数値文字列
	 * @return 数値
	 */
	double parseNumber(String sNumber);

	/**
	 * #rrggbb 形式の色文字列をパースする。
	 * @param sColor #rrggbb形式の色文字列
	 * @return Colorオブジェクト
	 */
	Color parseColor(String sColor);
	
	/**
	 * フォントファミリ名を取得する。
	 * @param family フォントファミリ名
	 * @return Fontオブジェクト
	 */
	Font getFont(String family);

	/**
	 * ストロークを作成する。
	 * @param sNumber 数値文字列
	 * @return Strokeオブジェクト
	 */
	Stroke createStroke(String sNumber);

	/**
	 * ノードに含まれる文字列を取得する
	 * @return ノードに含まれる文字列
	 */
	String getNodeText();

	/**
	 * ノードに設定されたフォントを取得する
	 * @return ノードに設定されたフォント
	 */
	Font getNodeFont();
}
