package cc.wdev.platform.system.commons.domain.request;

import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.commons.web.request.Request;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.util.Collection;
import java.util.List;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "关联业务类型请求参数")
public class RelationRequest extends Request {
    /**
     * 业务类型
     */
    @Schema(description = "业务类型")
    private String bizType;
    /**
     * 关联业务类型
     */
    @Schema(description = "关联业务类型")
    private String relationBizType;
    /**
     * 关联业务类型
     */
    @Schema(description = "关联业务类型")
    private List<String> relationBizTypeList;
    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 业务ID
     */
    @Schema(title = "业务ID列表", description = "业务ID列表")
    private Collection<Long> bizIdList;
    /**
     * 是否包含下级地区
     */
    @Builder.Default
    @Schema(title = "是否包含下级地区", description = "是否包含下级地区")
    private Boolean includeChildren = Boolean.FALSE;
    /**
     * 是否填充地区名称
     */
    @Builder.Default
    @Schema(title = "是否填充地区名称", description = "是否填充地区名称")
    private Boolean fillRegionNames = Boolean.FALSE;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double longitude;

    public List<Long> getBizIds() {
        List<Long> bizIds = Lists.newArrayList();
        if (ObjectUtils.isValidId(this.getBizId())) {
            bizIds.add(this.getBizId());
        }
        if (CollectionUtils.isNotEmpty(this.bizIdList)) {
            bizIds.addAll(this.bizIdList);
        }
        return bizIds;
    }

    public List<String> getRelationBizTypes() {
        List<String> relationBizTypeList = Lists.newArrayList();
        if (StringUtils.isNotEmpty(this.getRelationBizType())) {
            relationBizTypeList.add(this.getRelationBizType());
        }
        if (CollectionUtils.isNotEmpty(this.getRelationBizTypeList())) {
            relationBizTypeList.addAll(this.getRelationBizTypeList());
        }
        return relationBizTypeList;
    }

}
