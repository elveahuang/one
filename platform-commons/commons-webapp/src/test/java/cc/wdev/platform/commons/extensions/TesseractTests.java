package cc.wdev.platform.commons.extensions;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.bytedeco.leptonica.global.leptonica.pixDestroy;
import static org.bytedeco.leptonica.global.leptonica.pixRead;

/**
 * @author elvea
 */
@Slf4j
public class TesseractTests {

    @Test
    public void baseTest() {
        File root = new File("." + File.separator + "temp" + File.separator);
        File file = new File(root.getAbsolutePath(), "image.png");

        BytePointer outText;
        try (TessBaseAPI api = new TessBaseAPI()) {
            if (api.Init("D:\\Workspace\\tools\\tessdata", "eng") != 0) {
                System.err.println("Could not initialize tesseract.");
                System.exit(1);
            }
            PIX image = pixRead(file.getAbsolutePath());
            api.SetImage(image);
            outText = api.GetUTF8Text();
            System.out.println("OCR output:\n" + outText.getString());
            api.End();
            outText.deallocate();
            pixDestroy(image);
        }
    }

}
