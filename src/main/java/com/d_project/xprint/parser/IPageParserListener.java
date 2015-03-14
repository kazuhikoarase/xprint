package com.d_project.xprint.parser;

/**
 * IPageParserListener
 * @author Kazuhiko Arase
 */
public interface IPageParserListener {
    void begin(PageParser pager);
    void newPage(PageParser pager, int pageIndex);
    void end(PageParser pager);
}
