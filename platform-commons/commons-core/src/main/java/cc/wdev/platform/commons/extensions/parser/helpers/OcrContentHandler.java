package cc.wdev.platform.commons.extensions.parser.helpers;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class OcrContentHandler extends DefaultHandler {
    private final StringBuilder buffer = new StringBuilder();
    private final List<String> pendingImages;
    public int pageNumber = 1;

    public OcrContentHandler(List<String> pendingImages) {
        this.pendingImages = pendingImages;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        buffer.append(new String(ch, start, length));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        if ("page".equalsIgnoreCase(atts.getValue("class"))) {
            buffer.append("\n第").append(pageNumber).append("页：\n");
            pageNumber++;
        }
        if ("img".equalsIgnoreCase(localName)) {
            String src = atts.getValue("src");
            if (src != null) {
                pendingImages.add(src);
                buffer.append("图片内容：").append(src);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("p".equalsIgnoreCase(localName)
            || "div".equalsIgnoreCase(localName)
            || "br".equalsIgnoreCase(localName)
            || "li".equalsIgnoreCase(localName)) {
            buffer.append("\n");
        }
    }

    public String getText() {
        return buffer.toString();
    }

}
