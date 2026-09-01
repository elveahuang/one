package cc.wdev.platform.commons.oapis.facebody;

import cc.wdev.dev.webapp.BaseTests;
import cn.hutool.core.codec.Base64;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/**
 * @author elvea
 */
@Slf4j
public class FaceBodyServiceTests extends BaseTests {

    @Autowired
    private FaceBodyFactory faceBodyFactory;

    @Test
    public void detectFaceTest() throws Exception {
        ClassPathResource resource = new ClassPathResource("images/user.jpg");
        String image = Base64.encode(ByteStreams.toByteArray(resource.getInputStream()));
        FaceBodyResult result = this.faceBodyFactory.getFaceBodyService().detectFace(image);
        Assertions.assertFalse(result.isSuccess());
    }

    @Test
    public void compareFaceTest() throws Exception {
        ClassPathResource resource = new ClassPathResource("images/user.jpg");
        String image = Base64.encode(ByteStreams.toByteArray(resource.getInputStream()));
        FaceBodyResult result = this.faceBodyFactory.getAliyunFaceBodyService().compareFace(image, image);
        Assertions.assertTrue(result.isSuccess());
    }

}
