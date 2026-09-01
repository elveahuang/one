package cc.wdev.platform.system.ai.api;

import cc.wdev.platform.BaseTests;
import cc.wdev.platform.commons.ai.AiManager;
import cc.wdev.platform.commons.ai.enums.AiVectorizationStatus;
import cc.wdev.platform.commons.constants.DateTimeConstants;
import cc.wdev.platform.commons.utils.DateTimeUtils;
import cc.wdev.platform.system.ai.domain.request.AiKbItemSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiModelGetRequest;
import cc.wdev.platform.system.ai.domain.vo.AiKbItemVo;
import cc.wdev.platform.system.ai.domain.vo.AiKbVo;
import cc.wdev.platform.system.ai.domain.vo.AiModelVo;
import cc.wdev.platform.system.ai.enums.AiKbBizTypeEnum;
import cc.wdev.platform.system.ai.enums.AiKbItemTypeEnum;
import cc.wdev.platform.system.ai.enums.AiModelBizTypeEnum;
import cc.wdev.platform.system.ai.helpers.AiHelper;
import cc.wdev.platform.system.commons.domain.request.GetRequest;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static cc.wdev.platform.commons.ai.AiConstants.DEFAULT_TEST_VECTOR_STORE_COLLECTION_NAME;

/**
 * @author elvea
 */
@Slf4j
public class AiKbApiTests extends BaseTests {

    @Autowired
    private AiManager aiManager;

    @Autowired
    private AiHelper aiHelper;

    @Autowired
    private AiKbApi aiKbApi;

    @Autowired
    private AiModelApi aiModelApi;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(aiKbApi);
        AiKbVo aiKbVo = this.aiKbApi.getKb(GetRequest.builder().code(AiKbBizTypeEnum.TEST.getValue()).build());
        Assertions.assertEquals(AiKbBizTypeEnum.TEST.getValue(), aiKbVo.getCode());
    }

    @Test
    public void baseExecuteTest() throws Exception {

        aiKbApi.execute();
    }

    @Test
    public void baseVectorStoreTest() {
        // 获取知识库向量模型
        AiModelVo modelVo = aiModelApi.getAiModel(AiModelGetRequest.builder().code(AiModelBizTypeEnum.ALIYUN_TEXT_EMBEDDING.getValue()).build());
        Assertions.assertNotNull(modelVo);

        // 初始知识库向量存储
        VectorStore store = this.aiManager.getVectorStore(this.aiHelper.resolveModelConfig(modelVo), DEFAULT_TEST_VECTOR_STORE_COLLECTION_NAME);
        Assertions.assertNotNull(modelVo);

        // 初始知识库文档
        Map<String, Object> metaMap = Maps.newHashMap();
        metaMap.put("type", "doc");

        Map<String, Object> metaMapA = Maps.newHashMap(metaMap);
        metaMapA.put("docId", 1L);
        metaMapA.put("tenantId", 1L);

        Map<String, Object> metaMapB = Maps.newHashMap(metaMap);
        metaMapB.put("docId", 2L);
        metaMapB.put("tenantId", 1L);

        Map<String, Object> metaMapC = Maps.newHashMap(metaMap);
        metaMapC.put("docId", 3L);
        metaMapC.put("tenantId", 1L);

        List<Document> documents = List.of(
            new Document("1", "教师节是每年的9月11号", metaMapA),
            new Document("2", "劳动节是每年的5月1号", metaMapB),
            new Document("3", "植树节是每年的3月12号", metaMapC)
        );

        // 保存知识库文档
        store.add(documents);

        // 检索知识库文档
        List<Document> results = store.similaritySearch(SearchRequest.builder()
            .query("教师节")
            .similarityThreshold(0.5)
            .topK(5)
            .build()
        );
        Assertions.assertNotNull(results);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createTextItemTest() {
        AiKbVo aiKbVo = this.aiKbApi.getKb(GetRequest.builder().code(AiKbBizTypeEnum.TEST.getValue()).build());
        Assertions.assertEquals(AiKbBizTypeEnum.TEST.getValue(), aiKbVo.getCode());

        String now = DateTimeUtils.format(LocalDateTime.now(), DateTimeConstants.Pattern.DATE_TIME);
        AiKbItemSaveRequest request = AiKbItemSaveRequest.builder()
            .kbCode(aiKbVo.getCode())
            .type(AiKbItemTypeEnum.TEXT.getValue())
            .title("教师节是几月几号 - " + now)
            .content("教师节是每年9月9号 - " + now)
            .build();
        Long kbItemId = this.aiKbApi.createItem(request);

        AiKbItemVo item = this.await(kbItemId);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(AiVectorizationStatus.COMPLETED.getValue(), item.getStatus(),
            () -> "createTextItemTest, itemId=" + item.getId());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createQaItemTest() {
        AiKbVo aiKbVo = this.aiKbApi.getKb(GetRequest.builder().code(AiKbBizTypeEnum.TEST.getValue()).build());
        Assertions.assertEquals(AiKbBizTypeEnum.TEST.getValue(), aiKbVo.getCode());

        String now = DateTimeUtils.format(LocalDateTime.now(), DateTimeConstants.Pattern.DATE_TIME);
        AiKbItemSaveRequest request = AiKbItemSaveRequest.builder()
            .kbCode(aiKbVo.getCode())
            .type(AiKbItemTypeEnum.QA.getValue())
            .question("国庆是几月几号 - " + now)
            .answer("国庆节每年10月1号 - " + now)
            .build();
        Long kbItemId = this.aiKbApi.createItem(request);

        AiKbItemVo item = this.await(kbItemId);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(AiVectorizationStatus.COMPLETED.getValue(), item.getStatus(),
            () -> "createQaItemTest, itemId=" + item.getId());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createMarkdownTest() throws Exception {
        AiKbVo aiKbVo = this.aiKbApi.getKb(GetRequest.builder().code(AiKbBizTypeEnum.TEST.getValue()).build());
        Assertions.assertEquals(AiKbBizTypeEnum.TEST.getValue(), aiKbVo.getCode());

        Resource resource = new ClassPathResource("docs/test.md");
        if (!resource.exists()) {
            log.info("Markdown [{}] not exists", resource.getFilePath().toAbsolutePath());
            return;
        }

        AiKbItemSaveRequest request = AiKbItemSaveRequest.builder()
            .kbCode(aiKbVo.getCode())
            .type(AiKbItemTypeEnum.DOCUMENT.getValue())
            .title(resource.getFilename())
            .build();
        Long kbItemId = this.aiKbApi.createDocumentItem(request, resource);

        AiKbItemVo item = this.await(kbItemId);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(AiVectorizationStatus.COMPLETED.getValue(), item.getStatus(),
            () -> "createMarkdownTest failed, itemId=" + item.getId());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createPdfTest() throws Exception {
        AiKbVo aiKbVo = this.aiKbApi.getKb(GetRequest.builder().code(AiKbBizTypeEnum.TEST.getValue()).build());
        Assertions.assertEquals(AiKbBizTypeEnum.TEST.getValue(), aiKbVo.getCode());

        Resource resource = new ClassPathResource("docs/test.pdf");
        if (!resource.exists()) {
            log.info("PDF [{}] not exists", resource.getFilePath().toAbsolutePath());
            return;
        }

        AiKbItemSaveRequest request = AiKbItemSaveRequest.builder()
            .kbCode(aiKbVo.getCode())
            .type(AiKbItemTypeEnum.DOCUMENT.getValue())
            .title(resource.getFilename())
            .build();
        Long kbItemId = this.aiKbApi.createDocumentItem(request, resource);

        AiKbItemVo item = this.await(kbItemId);
        Assertions.assertNotNull(item);
        Assertions.assertEquals(AiVectorizationStatus.COMPLETED.getValue(), item.getStatus(),
            () -> "createPdfTest failed, itemId=" + item.getId());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createDocumentTest() throws Exception {
        AiKbVo aiKbVo = this.aiKbApi.getKb(GetRequest.builder().code(AiKbBizTypeEnum.TEST.getValue()).build());
        Assertions.assertEquals(AiKbBizTypeEnum.TEST.getValue(), aiKbVo.getCode());

        Resource resource = new ClassPathResource("documents");
        if (!resource.exists()) {
            log.info("Documents [{}] not exists", resource.getFilePath().toAbsolutePath());
            return;
        }

        try (Stream<Path> stream = Files.list(resource.getFilePath())) {
            stream.filter(Files::isRegularFile).forEach(path -> {
                File file = path.toFile();
                if (!file.exists()) {
                    return;
                }

                AiKbItemSaveRequest request = AiKbItemSaveRequest.builder()
                    .kbCode(aiKbVo.getCode())
                    .type(AiKbItemTypeEnum.DOCUMENT.getValue())
                    .title(file.getName())
                    .build();
                Long kbItemId = this.aiKbApi.createDocumentItem(request, file);

                AiKbItemVo item = this.await(kbItemId);
                Assertions.assertNotNull(item);
                Assertions.assertEquals(AiVectorizationStatus.COMPLETED.getValue(), item.getStatus(),
                    () -> "createDocumentTest failed, itemId=" + item.getId());
            });
        } catch (IOException e) {
            log.error("resource list error: {}", resource.getFile().getAbsolutePath(), e);
        }
    }

    private AiKbItemVo await(Long kbItemId) {
        try {
            Awaitility.await().atMost(Duration.ofSeconds(180)).pollInterval(Duration.ofSeconds(2)).until(() -> {
                AiKbItemVo item = aiKbApi.getKbItem(GetRequest.builder().id(kbItemId).build());
                return AiVectorizationStatus.COMPLETED.getValue().equals(item.getStatus())
                    || AiVectorizationStatus.FAILED.getValue().equals(item.getStatus());
            });
            return aiKbApi.getKbItem(GetRequest.builder().id(kbItemId).build());
        } catch (ConditionTimeoutException e) {
            return null;
        }
    }

}
