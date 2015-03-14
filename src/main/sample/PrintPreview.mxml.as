
import flash.display.Sprite;
import flash.events.Event;

import mx.managers.PopUpManager;

import xprint.XPageRenderer;


// ページレンダラ
private var _renderer : XPageRenderer;

// 拡大率
private var _scale : Number;

// 現在表示中のページ
private var _currentPage : Sprite;

// 現在のページのインデックス
[Bindable]
private var _currentPageIndex : int;

// 総ページ数
[Bindable]
private var _pageCount : int;


// RAWデータのURL
public var url : String;

private function creationCompleteHandler(event :  Event) : void {

	if (url) {
		loadPages(url);
	}
}

private function closeHandler(event : Event) : void {
	_renderer = null;
	PopUpManager.removePopUp(this);
}

private function loadPages(url : String) : void {

	_renderer = new XPageRenderer();
	_renderer.addEventListener(Event.COMPLETE, renderer_completeHandler);
	_renderer.load(url);
}

private function renderer_completeHandler(event : Event) : void {

	_scale = 1.0;
	_currentPageIndex = 0;
	_pageCount = _renderer.numPages;
	showPage(_currentPageIndex);
}

private function showPage(pageIndex : int) : void {
	
	// clear holder.
	while (pageHolder.numChildren > 0) {
		pageHolder.removeChildAt(pageHolder.numChildren - 1);
	}
	
	if (pageIndex < 0 || _renderer.numPages <= pageIndex) {
		return;
	}

	// add to holder.
	var page : Sprite = createPage(pageIndex);
	pageHolder.addChild(page);
	
	_currentPage = page;

	updateScale();
}

/**
 * ページを作成する
 * @return ページの Sprite
 */
private function createPage(pageIndex : int) : Sprite {

	var page : Sprite = new Sprite();
		
	var g : Graphics = page.graphics;
	g.clear();

	// 背景を白で塗りつぶす
	g.beginFill(0xffffff);
	g.drawRect(0, 0, _renderer.pageWidth, _renderer.pageHeight);
	g.endFill();

	// ページをレンダリングする。
	_renderer.render(g, pageIndex);
	
	return page;
}

/**
 * 前ページボタンがクリックされたときの処理
 */
private function prevPage_clickHandler(event : Event) : void {
	_currentPageIndex = (_currentPageIndex + _renderer.numPages - 1) % _renderer.numPages;
	showPage(_currentPageIndex);
}

/**
 * 次ページボタンがクリックされたときの処理
 */
private function nextPage_clickHandler(event : Event) : void {
	_currentPageIndex = (_currentPageIndex + 1) % _renderer.numPages;
	showPage(_currentPageIndex);
}

/**
 * 等倍ボタンがクリックされたときの処理
 */
private function zoomDefault_clickHandler(event : Event) : void {
	_scale = 1.0;
	updateScale();
}

/**
 * 縮小ボタンがクリックされたときの処理
 */
private function zoomIn_clickHandler(event : Event) : void {
	_scale *= 1.5;
	updateScale();
}

/**
 * 拡大ボタンがクリックされたときの処理
 */
private function zoomOut_clickHandler(event : Event) : void {
	_scale /= 1.5;
	updateScale();
}

private function updateScale() : void {
	_currentPage.scaleX = _scale;
	_currentPage.scaleY = _scale;
	pageHolder.width = _currentPage.width;
	pageHolder.height = _currentPage.height;
}

/**
 * 印刷ボタンがクリックされたときの処理
 */
private function print_clickHandler(event : Event) : void {

	var pj : PrintJob = new PrintJob();

	if (!pj.start() ) {
		// canceled.
		return;
	}

	for (var i : int = 0; i < _renderer.numPages; i++) {
		var page : Sprite = createPage(i);
		pj.addPage(page);
	}
	
	pj.send();
}
