
import flash.events.Event;

import mx.collections.XMLListCollection;
import mx.managers.PopUpManager;

[Bindable]
private var _sampleList : XMLListCollection;

private function creationCompleteHandler(event : Event) : void {

	var sampleList : XML = <samples>
		<sample label="sample1" url="out/sample1.dat" />
		<sample label="sample2" url="out/sample2.dat" />
		<sample label="sample3" url="out/sample3.dat" />
		<sample label="sample4" url="out/sample4.dat" />
		<sample label="sample5" url="out/sample5.dat" />
		<sample label="sample6" url="out/sample6.dat" />
		<sample label="sample7" url="out/sample7.dat" />
		<sample label="sample8" url="out/sample8.dat" />
	</samples>;
	_sampleList = new XMLListCollection(sampleList.sample);
	
	loadSample();
}

private function sampleList_changeHandler(event : Event) : void {

	loadSample();
}

private function loadSample() : void {

	if (!sampleList.selectedItem) {	
		return;
	}

	var preview : PrintPreview = new PrintPreview();
	preview.title = "プレビュー - " + sampleList.selectedItem.@label;
	preview.width = 900;
	preview.height = 500;
	preview.url = sampleList.selectedItem.@url;
	PopUpManager.addPopUp(preview, this);
	PopUpManager.centerPopUp(preview);	
}
