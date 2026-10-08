package cc.wdev.platform.commons.data.elasticsearch.utils;

import cc.wdev.platform.commons.constants.EsConstants;
import cc.wdev.platform.commons.data.elasticsearch.domain.EsSearchResp;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.ArrayUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest5_client.low_level.ResponseException;
import jakarta.json.stream.JsonGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class EsUtils {

    /**
     * 查询
     *
     * @param elasticsearchClient es客户端
     * @param searchRequest       查询条件
     * @return 查询结果
     */
    public static <T> EsSearchResp<T> search(ElasticsearchClient elasticsearchClient, SearchRequest searchRequest, Class<T> clazz) {
        EsSearchResp<T> esSearchDto = new EsSearchResp<>();
        try {
            EsUtils.printDSL(elasticsearchClient._transport(), searchRequest);
            SearchResponse<T> response = elasticsearchClient.search(searchRequest, clazz);
            convertResponse(esSearchDto, response);
        } catch (IOException e) {
            log.error("查询es数据异常,", e);
            throw new ServiceException(ResponseCodeEnum.ES_DOCUMENT_QUERY_ERROR);
        }
        return esSearchDto;
    }

    /**
     * 转换返回结果 为EsSearchDto
     *
     * @param esSearchDto es搜索结果
     * @param response    es返回结果
     * @param <T>         泛型
     * @param <R>         泛型
     */
    private static <T, R extends ResponseBody<T>> void convertResponse(EsSearchResp<T> esSearchDto, R response) {
        if (response == null || response.hits() == null) {
            return;
        }
        HitsMetadata<T> hits = response.hits();
        if (hits.total() != null) {
            esSearchDto.setTotal(hits.total().value());
        }
        esSearchDto.setData(hits.hits().stream().filter(Objects::nonNull).map(Hit::source).collect(Collectors.toList()));
        esSearchDto.setScrollId(response.scrollId());
    }

    /**
     * 保存文档
     */
    public static <T> Result saveDocument(ElasticsearchClient esClient, String index, String id, T esDto, Class<T> clazz) throws IOException {
        try {
            UpdateResponse<T> response = esClient.update(u -> u
                    .index(index)
                    .id(id)
                    .doc(esDto)   // 只更新传入对象中的非 null 字段
                    .docAsUpsert(Boolean.TRUE)  // 不存在就新建
                , clazz);

            return response.result();
        } catch (ResponseException e) {
            log.error("es保存文档异常 -[{}]", JSONUtil.toJsonStr(esDto), e);
            int statusCode = e.getResponse().getStatusCode();
            if (statusCode == EsConstants.ES_ID_CONFLICT_CODE) {
                return Result.NoOp;
            }
            throw new ServiceException(ResponseCodeEnum.ES_DOCUMENT_SAVE_ERROR);
        }
    }

    /**
     * 保存文档
     */
    public static <T> Result deleteDocument(ElasticsearchClient esClient, String index, String id) throws IOException {
        try {
            return esClient.delete(delete -> delete.index(index).id(id)).result();
        } catch (ResponseException e) {
            int statusCode = e.getResponse().getStatusCode();
            log.error("es删除文档异常 -id[{}] -statusCode[{}]", id, statusCode, e);
            throw new ServiceException(ResponseCodeEnum.ES_DOCUMENT_DELETE_ERROR);
        }
    }

    /**
     * 打印DSL
     */
    public static void printDSL(ElasticsearchTransport transport,
                                SearchRequest searchRequest) {
        try (StringWriter writer = new StringWriter();
             JsonGenerator generator = transport.jsonpMapper().jsonProvider().createGenerator(writer)) {
            searchRequest.serialize(generator, transport.jsonpMapper());
            // 刷新并关闭 JsonGenerator
            generator.flush();
            log.info("index：{}，DSL:{}", searchRequest.index(), writer);
        } catch (Exception e) {
            log.error("print SearchRequest DSL error: {}", e.getMessage(), e);
        }
    }

    /**
     * 设置索引
     *
     * @param searchRequestBuilder 搜索源
     * @param index                索引类型
     */
    public static SearchRequest.Builder index(SearchRequest.Builder searchRequestBuilder, String index) {
        searchRequestBuilder.index(index);
        return searchRequestBuilder;
    }

    /**
     * 构建对象搜索源
     */
    public static SearchRequest.Builder searchRequestBuilder() {
        SearchRequest.Builder searchSourceBuilder = new SearchRequest.Builder();
        // 设置没数据时不报错,直接返回空
        searchSourceBuilder.ignoreUnavailable(true);
        return searchSourceBuilder;
    }

    /**
     * 设置页码
     *
     * @param page                 页
     * @param searchRequestBuilder 搜索源
     * @param size                 页大小
     */
    public static SearchRequest.Builder pageSize(SearchRequest.Builder searchRequestBuilder, Integer page, Integer size) {
        if (Objects.nonNull(searchRequestBuilder)) {
            long currentPage = Math.max(page, 1);
            searchRequestBuilder.from((int) ((currentPage - 1) * size));
            searchRequestBuilder.size(Math.toIntExact(size));
        }
        return searchRequestBuilder;
    }

    /**
     * 搜索字段
     *
     * @param builder 查询条件
     * @param field   字段
     */
    public static <T> SearchRequest.Builder select(SearchRequest.Builder builder, String field) {
        if (Objects.nonNull(builder) && StrUtil.isNotBlank(field)) {
            builder.source(src -> src.filter(f -> f.includes(field)));
        }
        return builder;
    }

    /**
     * 深度查询查询页设置
     *
     * @param searchRequestBuilder 搜索源
     * @param size                 页大小
     */
    public static SearchRequest.Builder scrollPageSize(SearchRequest.Builder searchRequestBuilder, Integer size, String validTime) {
        if (Objects.nonNull(searchRequestBuilder)) {
            searchRequestBuilder.size(size);
            searchRequestBuilder.scroll(Time.of(t -> t.time(validTime)));
        }
        return searchRequestBuilder;
    }


    /**
     * 排序
     *
     * @param searchSourceBuilder 搜索源
     * @param field               字段
     * @param sortOrder           排序规则
     */
    public static SearchRequest.Builder sort(SearchRequest.Builder searchSourceBuilder, String field, SortOrder sortOrder) {
        FieldSort.Builder builder = new FieldSort.Builder();
        builder.field(field).order(sortOrder);
        searchSourceBuilder.sort(SortOptions.of(s -> s.field(builder.build())));
        return searchSourceBuilder;
    }

    /**
     * 精确not查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param value      值
     */
    public static BoolQuery.Builder notEq(boolean expression, BoolQuery.Builder builder, String field, String value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field)) {
            builder.mustNot(QueryBuilders.term().field(field).value(value).build()._toQuery());
        }
        return builder;
    }

    /**
     * or查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param fields     字段
     * @param values     值eq
     */
    public static <T> BoolQuery.Builder or(boolean expression, BoolQuery.Builder builder, List<String> fields, List<String> values, String minimumShouldMatch) {
        List<Query> phraseQueryList = new ArrayList<>();
        if (expression && Objects.nonNull(builder) && CollectionUtils.isNotEmpty(fields) && CollectionUtils.isNotEmpty(values)) {
            for (String field : fields) {
                for (String value : values) {
                    MatchPhraseQuery.Builder phraseQuery = QueryBuilders.matchPhrase();
                    phraseQuery.field(field).query(value);
                    Query query = phraseQuery.build()._toQuery();
                    phraseQueryList.add(query);
                }
            }
        }
        // 是否至少匹配
        if (StrUtil.isNotBlank(minimumShouldMatch)) {
            builder.minimumShouldMatch(minimumShouldMatch);
        }
        return builder.should(phraseQueryList);
    }

    /**
     * in查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param values     值
     */
    public static <T> BoolQuery.Builder in(boolean expression, BoolQuery.Builder builder, String field, List<T> values) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && CollectionUtils.isNotEmpty(values)) {
            builder.must(TermsQuery.of(fn -> fn.field(field).terms(t -> t.value(values.stream()
                .filter(Objects::nonNull).map(value -> FieldValue.of(value.toString())).collect(Collectors.toList()))))._toQuery());
        }
        return builder;
    }

    /**
     * like查询(文本全文查询)
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param value      值
     */
    public static BoolQuery.Builder like(boolean expression, BoolQuery.Builder builder, String value, List<String> contentFiledList) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(value) && CollectionUtils.isNotEmpty(contentFiledList)) {
            builder.must(QueryBuilders.multiMatch().query(value).slop(0).type(TextQueryType.BestFields).fields(contentFiledList)
                .lenient(true).build()._toQuery());
        }
        return builder;
    }

    /**
     * 等于查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param value      值
     */
    public static BoolQuery.Builder eq(boolean expression, BoolQuery.Builder builder, String field, Object value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && Objects.nonNull(value)) {
            builder.must(TermQuery.of(fn -> fn.field(field).value(FieldValue.of(value.toString())))._toQuery());
        }
        return builder;
    }

    /**
     * 多字段等于查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param fields     字段
     * @param value      值
     */
    public static BoolQuery.Builder eqFields(boolean expression, BoolQuery.Builder builder, String value, List<String> fields) {
        if (expression && Objects.nonNull(builder) && CollectionUtils.isNotEmpty(fields) && Objects.nonNull(value)) {
            builder.must(QueryBuilders.multiMatch().fields(fields).query(value).build()._toQuery());
        }
        return builder;
    }

    /**
     * 排序
     *
     * @param field     字段
     * @param sortOrder 排序
     */
    public static SortOptions sort(String field, SortOrder sortOrder) {
        return SortOptions.of(sortOptions -> sortOptions.field(new FieldSort.Builder().field(field).order(sortOrder).build()));
    }

    /**
     * 聚簇查询最小值
     *
     * @param builder 查询条件
     * @param key     关键字
     * @param field   字段
     */
    public static SearchRequest.Builder agMin(SearchRequest.Builder builder, String key, String field) {
        if (Objects.nonNull(builder) && StrUtil.isNotBlank(field) && StrUtil.isNotBlank(field)) {
            builder.aggregations(key, t -> t.min(m -> m.field(field)));
        }
        return builder;
    }

    /**
     * 聚簇查询最大值
     *
     * @param builder 查询条件
     * @param key     关键字
     * @param field   字段
     */
    public static SearchRequest.Builder agMax(SearchRequest.Builder builder, String key, String field) {
        if (Objects.nonNull(builder) && StrUtil.isNotBlank(field) && StrUtil.isNotBlank(field)) {
            builder.aggregations(key, t -> t.max(m -> m.field(field)));
        }
        return builder;
    }


    /**
     * 字段不为空
     * <p>
     * 查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     */
    public static BoolQuery.Builder exists(boolean expression, BoolQuery.Builder builder, String field) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field)) {
            builder.must(QueryBuilders.exists().field(field).build()._toQuery());
        }
        return builder;
    }

    /**
     * in查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param values     值
     */
    public static <T> BoolQuery.Builder inFilter(boolean expression, BoolQuery.Builder builder, String field, List<T> values) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && CollectionUtils.isNotEmpty(values)) {
            builder.filter(TermsQuery.of(fn -> fn.field(field).terms(t -> t.value(values.stream()
                .filter(Objects::nonNull).map(value -> FieldValue.of(value.toString())).collect(Collectors.toList()))))._toQuery());
        }
        return builder;
    }

    /**
     * ≥
     * gte查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param value      值
     */
    public static <T> BoolQuery.Builder gteFilter(boolean expression, BoolQuery.Builder builder, String field, Double value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && Objects.nonNull(value)) {
            builder.filter(QueryBuilders.range()
                .number(n -> n.gte(value).field(field))
                .build()
                ._toQuery());
        }
        return builder;
    }

    /**
     * >
     * gt查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param value      值
     */
    public static <T> BoolQuery.Builder gtFilter(boolean expression, BoolQuery.Builder builder, String field, Double value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && Objects.nonNull(value)) {
            builder.filter(QueryBuilders.range()
                .number(n -> n.gt(value).field(field))
                .build()
                ._toQuery());
        }
        return builder;
    }

    /**
     * ≤
     * lte查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param value      值
     */
    public static <T> BoolQuery.Builder lteFilter(boolean expression, BoolQuery.Builder builder, String field, Double value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && Objects.nonNull(value)) {
            builder.filter(QueryBuilders.range()
                .number(n -> n.lte(value).field(field))
                .build()
                ._toQuery());
        }
        return builder;
    }

    /**
     * <
     * lt查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param value      值
     */
    public static <T> BoolQuery.Builder ltFilter(boolean expression, BoolQuery.Builder builder, String field, Double value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && Objects.nonNull(value)) {
            builder.filter(QueryBuilders.range()
                .number(n -> n.lt(value).field(field))
                .build()
                ._toQuery());
        }
        return builder;
    }

    /**
     * like filter(文本全文查询)
     * 对于不需考虑得分的查询,优先考虑filter
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param value      值
     */
    public static BoolQuery.Builder filterLike(boolean expression, BoolQuery.Builder builder, String value, List<String> contentFiledList) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(value) && CollectionUtils.isNotEmpty(contentFiledList)) {
            builder.filter(QueryBuilders.multiMatch().query(value).slop(0).type(TextQueryType.BestFields).fields(contentFiledList)
                .lenient(true).build()._toQuery());
        }
        return builder;
    }

    /**
     * 等于查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param value      值
     */
    public static BoolQuery.Builder filter(boolean expression, BoolQuery.Builder builder, String field, Object value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && Objects.nonNull(value)) {
            builder.filter(TermQuery.of(fn -> fn.field(field).value(FieldValue.of(value.toString())))._toQuery());
        }
        return builder;
    }


    /**
     * 多字段等于查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param fields     字段
     * @param value      值
     */
    public static BoolQuery.Builder filterFields(boolean expression, BoolQuery.Builder builder, String value, List<String> fields) {
        if (expression && Objects.nonNull(builder) && CollectionUtils.isNotEmpty(fields) && Objects.nonNull(value)) {
            builder.filter(QueryBuilders.multiMatch().fields(fields).query(value).build()._toQuery());
        }
        return builder;
    }

    /**
     * 字段不为空
     * <p>
     * 查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     */
    public static BoolQuery.Builder existsFilter(boolean expression, BoolQuery.Builder builder, String field) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field)) {
            builder.filter(QueryBuilders.exists().field(field).build()._toQuery());
        }
        return builder;
    }

    /**
     * 等于查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param value      值
     */
    public static BoolQuery.Builder shouldEq(boolean expression, BoolQuery.Builder builder, String field, Object value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && Objects.nonNull(value)) {
            builder.should(TermQuery.of(fn -> fn.field(field).value(FieldValue.of(value.toString())))._toQuery());
        }
        return builder;

    }

    /**
     * in查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param field      字段
     * @param values     值
     */
    public static <T> BoolQuery.Builder shouldIn(boolean expression, BoolQuery.Builder builder, String field, List<T> values) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(field) && CollectionUtils.isNotEmpty(values)) {
            builder.should(TermsQuery.of(fn -> fn.field(field).terms(t -> t.value(values.stream()
                .filter(Objects::nonNull).map(value -> FieldValue.of(value.toString())).collect(Collectors.toList()))))._toQuery());
        }
        return builder;
    }

    /**
     * nested查询
     *
     * @param expression 是否执行
     * @param builder    查询条件
     * @param path       字段路径
     * @param field      字段
     * @param value      值
     */
    public static void nestedEq(boolean expression, BoolQuery.Builder builder, String path, String field, Object value) {
        if (expression && Objects.nonNull(builder) && StrUtil.isNotBlank(path) && StrUtil.isNotBlank(field) && Objects.nonNull(value)) {
            builder.must(NestedQuery.of(n -> n
                .path(path)
                .query(q -> q.term(t -> t.field(field).value(FieldValue.of(value.toString())))))._toQuery());
        }
    }

    /**
     * 向量相似性查询 (k-NN 查询)
     * 该方法会构建一个 knn 查询，并将其添加到 BoolQuery 的 must 子句中。
     *
     * @param expression 是否执行该查询条件
     * @param builder    BoolQuery.Builder 实例，用于组装查询条件
     * @param field      向量字段名 (e.g., "feature_vector")
     * @param vector     查询向量
     * @param k          希望返回的最相似文档数量
     */
    public static BoolQuery.Builder knnQuery(boolean expression, BoolQuery.Builder builder,
                                             String field, Float[] vector, Integer k) {
        if (expression && builder != null && StrUtil.isNotBlank(field) && ArrayUtils.isNotEmpty(vector) && k > 0) {
            KnnQuery query = KnnQuery.of(knn -> knn
                .field(field)
                .queryVector(Arrays.stream(vector).toList())
                .k(k)
            );
            builder.must(Query.of(q -> q.knn(query)));
        }
        return builder;
    }
}
