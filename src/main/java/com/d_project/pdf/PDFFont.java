package com.d_project.pdf;

import java.io.IOException;


/**
 * PDFFont
 * @author Kazuhiko Arase
 */
public class PDFFont extends PDFDictionary {

    private static final int FONT_FIXED_PITCH    =   1 << (1 - 1);
    private static final int FONT_SERIF          =   1 << (2 - 1);
    private static final int FONT_SYMBOLIC       =   1 << (3 - 1);
    private static final int FONT_SCRIPT         =   1 << (4 - 1);
    private static final int FONT_NONSYMBOLIC    =   1 << (6 - 1);
    private static final int FONT_ITALIC         =   1 << (7 - 1);

    private static final int FONT_ALL_CAP        =   1 << (17 - 1);
    private static final int FONT_SMALL_CAP      =   1 << (18 - 1);
    private static final int FONT_FORCE_BOLD     =   1 << (19 - 1);

	private PDFFont() {
	}

    /**
     * 日本語フォントを作成する。
     * @param doc 関連付けるPDFドキュメント
     * @param serif セリフ付き(明朝)の場合は true、セリフ無し(ゴシック)の場合は false
     */
    public static PDFFont createJapaneseFont(PDFDocument doc, boolean serif) throws IOException {

        PDFFont font = new PDFFont();
        PDFDictionary fontDesc = new PDFDictionary();
        PDFDictionary cidFont = new PDFDictionary();

        doc.addObject(font);
        doc.addObject(cidFont);
        doc.addObject(fontDesc);

        PDFName fontName;

		if (serif) {
//			fontName = new PDFName("Ryumin-Light-H");
			fontName = new PDFName("Mincho");
		} else {
//			fontName = new PDFName("GothicBBB-Medium-H");
			fontName = new PDFName("Gothic");
		}
        
		{
            font.put("Type", new PDFName("Font") );
            font.put("Subtype", new PDFName("Type0") );
            font.put("BaseFont", fontName);
            PDFArray cidFonts = new PDFArray();
            cidFonts.add(cidFont);
            font.put("DescendantFonts", cidFonts);
            font.put("Encoding", new PDFName("90ms-RKSJ-H") );
        }

        {
            cidFont.put("Type", new PDFName("Font") );
            cidFont.put("Subtype", new PDFName("CIDFontType2") );
            cidFont.put("BaseFont", fontName);
//            cidFont.put("WinCharSet", new PDFInteger(128) );
            cidFont.put("FontDescriptor", fontDesc);

            PDFDictionary cidSystemInfo = new PDFDictionary();
            cidSystemInfo.put("Registry", new PDFLiterealString("Adobe") );
            cidSystemInfo.put("Ordering", new PDFLiterealString("Japan1") );
            cidSystemInfo.put("Supplement", new PDFInteger(2) );

            cidFont.put("CIDSystemInfo", cidSystemInfo);

            cidFont.put("DW", new PDFInteger(1000) );

            PDFArray cidW = new PDFArray();

            // CID - CODE (Adobe Japan1-2)

            cidW.add(new PDFInteger(231) );
            cidW.add(new PDFInteger(389) );
            cidW.add(new PDFInteger(500) );

            cidW.add(new PDFInteger(631) );
            cidW.add(new PDFInteger(631) );
            cidW.add(new PDFInteger(500) );

            cidFont.put("W", cidW);
        }

        {

            fontDesc.put("Type", new PDFName("FontDescriptor") );
/*
            fontDesc.put("Ascent", new PDFInteger(853) );
            fontDesc.put("CapHeight", new PDFInteger(853) );
            fontDesc.put("Descent", new PDFInteger(-147) );
*/
            fontDesc.put("Ascent", new PDFInteger(1000) );
            fontDesc.put("CapHeight", new PDFInteger(1000) );
            fontDesc.put("Descent",  new PDFInteger(0) );

			if (serif) {
				fontDesc.put("Flags", new PDFInteger(FONT_FIXED_PITCH | FONT_SERIF | FONT_SYMBOLIC | FONT_NONSYMBOLIC) );
			} else {
				fontDesc.put("Flags", new PDFInteger(FONT_FIXED_PITCH | FONT_SYMBOLIC | FONT_NONSYMBOLIC) );
			}

//            fontDesc.put("FontBBox", new PDFRectangle(-150, -147, 1100, 853) );
            fontDesc.put("FontBBox", new PDFRectangle(0, 0, 1000, 1000) );

            fontDesc.put("FontName", fontName);
            fontDesc.put("ItalicAngle", new PDFInteger(0) );

//            fontDesc.put("StemV", new PDFInteger(92) );
            fontDesc.put("StemV", new PDFInteger(0) );

/*
            fontDesc.put("AvgWidth", new PDFInteger(507) );
            fontDesc.put("Leading",  new PDFInteger(0) );
            fontDesc.put("MaxWidth", new PDFInteger(1000) );
            fontDesc.put("MissingWidth", new PDFInteger(507) );
            fontDesc.put("StemH", new PDFInteger(92) );
            fontDesc.put("XHeight",   new PDFInteger(597) );
*/
/*
            PDFDictionary fontStyle = new PDFDictionary();
            fontStyle.put("Panose", new PDFString("<08 05 02 0B 06 09 00 00 00 00 00 00>") );
            fontDesc.put("Style", fontStyle);
*/
        }


        return font;    
    }

}

