package cc.wdev.platform.commons.domain;

import cc.wdev.platform.commons.web.request.Request;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author elvea
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "实体查询请求")
public class GetRequest extends Request {

    /**
     * 实体ID
     */
    @Schema(description = "实体ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 实体编号
     */
    @Schema(description = "实体编号")
    private String code;

    public static GetRequest of(Long id) {
        return GetRequest.builder().id(id).build();
    }

    public static GetRequest of(String code) {
        return GetRequest.builder().code(code).build();
    }

    public static GetRequest of(Long id, String code) {
        return GetRequest.builder().id(id).code(code).build();
    }

}
