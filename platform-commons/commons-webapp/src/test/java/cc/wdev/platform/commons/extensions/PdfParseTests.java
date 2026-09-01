package cc.wdev.platform.commons.extensions;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author elvea
 */
@Slf4j
public class PdfParseTests {

    @Test
    public void baseTest() throws TikaException, IOException, SAXException {
        File root = new File("temp" + File.separator);
        File file = new File(root.getAbsolutePath(), "pdf.pdf");

        Tika tika = new Tika();
        String metaType = tika.detect(file);
        log.info("metaType: {}", metaType);

        Parser parser = new AutoDetectParser();
        BodyContentHandler bodyContentHandler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        FileInputStream inputStream = new FileInputStream(file);
        parser.parse(inputStream, bodyContentHandler, metadata, context);
        log.info("Content : {}", bodyContentHandler);
        String[] names = metadata.names();
        for (String name : names) {
            System.out.println(name);
        }
    }

}
