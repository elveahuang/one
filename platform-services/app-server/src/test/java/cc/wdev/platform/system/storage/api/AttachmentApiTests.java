package cc.wdev.platform.system.storage.api;

import cc.wdev.platform.BaseTests;
import cc.wdev.platform.system.storage.domain.request.AttachmentRequest;
import cc.wdev.platform.system.storage.domain.vo.AttachmentFileVo;
import cc.wdev.platform.system.storage.enums.AttachmentBizTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/**
 * @author elvea
 */
@Slf4j
public class AttachmentApiTests extends BaseTests {

    @Autowired
    private AttachmentApi attachmentApi;

    @Test
    public void baseTest() {
        ClassPathResource resource = new ClassPathResource("html/tpl.html");

        AttachmentRequest attachmentRequest = AttachmentRequest.builder().bizType(AttachmentBizTypeEnum.TEST.getValue()).build();
        AttachmentFileVo attachmentVo = this.attachmentApi.uploadAttachment(attachmentRequest, resource);

        Assertions.assertNotNull(attachmentVo);
    }

}
