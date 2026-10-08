package cc.wdev.platform.commons.ai.utils;

import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author elvea
 */
public abstract class AiRagUtils {

    /**
     * 生成向量索引
     */
    public static String resolveIndexName(String indexPrefix, String collectionName) {
        String suffix = collectionName == null ? "" : collectionName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_-]", "-");
        return StringUtils.isNotEmpty(indexPrefix) ? indexPrefix + "-" + suffix : suffix;
    }

    /**
     * 生成带租户前缀的向量索引
     */
    public static String resolveIndexName(String indexPrefix, String collectionName, Long tenantId) {
        String base = resolveIndexName(indexPrefix, collectionName);
        if (tenantId == null || tenantId <= 0) {
            return base;
        }
        return "t" + tenantId + "-" + base;
    }

    /**
     * 生成向量表名
     */
    public static String resolveTableName(String indexPrefix, String collectionName) {
        String prefix = indexPrefix == null ? "" : indexPrefix.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9_]", "_")
            .replaceAll("_+$", "");
        String suffix = collectionName == null ? "" : collectionName.toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9_]", "_")
            .replaceAll("_+", "_")
            .replaceAll("^_+|_+$", "");
        String tableName;
        if (StringUtils.isEmpty(prefix)) {
            tableName = suffix;
        } else if (StringUtils.isEmpty(suffix)) {
            tableName = prefix;
        } else {
            tableName = prefix + "_" + suffix;
        }
        if (tableName.length() > 63) {
            tableName = tableName.substring(0, 63);
        }
        return tableName;
    }

    /**
     * 生成带租户前缀的向量表名
     */
    public static String resolveTableName(String indexPrefix, String collectionName, Long tenantId) {
        String base = resolveTableName(indexPrefix, collectionName);
        if (tenantId == null || tenantId <= 0) {
            return base;
        }
        String tenant = "t" + tenantId;
        String tableName = tenant + "_" + base;
        if (tableName.length() > 63) {
            tableName = tableName.substring(0, 63);
        }
        return tableName;
    }

    /**
     * 注入元数据
     */
    public static void applyDocumentMetadata(List<Document> documents, String key, Object value) {
        if (CollectionUtils.isEmpty(documents)) {
            return;
        }
        documents.stream().filter(Objects::nonNull).forEach(document -> {
            if (!document.getMetadata().containsKey(key)) {
                document.getMetadata().put(key, value);
            }
        });
    }

    /**
     * 注入元数据
     */
    public static void applyDocumentMetadata(List<Document> documents, Map<String, Object> metadata) {
        if (CollectionUtils.isEmpty(documents) || CollectionUtils.isEmpty(metadata)) {
            return;
        }
        documents.stream().filter(Objects::nonNull).forEach(document -> metadata.forEach((k, v) -> {
            if (!document.getMetadata().containsKey(k)) {
                document.getMetadata().put(k, v);
            }
        }));
    }

    /**
     * 把文档转成纯文本
     */
    public static String toText(List<Document> documents) {
        if (CollectionUtils.isEmpty(documents)) {
            return "";
        }

        return documents.stream()
            .map(Document::getText)
            .filter(Objects::nonNull)
            .collect(Collectors.joining("\n\n"));
    }

}
