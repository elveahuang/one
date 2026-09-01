package cc.wdev.platform.commons.domain;

import cc.wdev.platform.commons.enums.BaseResponseCodeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author elvea
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "响应信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<E> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = HttpStatus.OK.value();

    public static final int ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();

    @Schema(description = "响应编号")
    private int code;

    @Schema(description = "响应信息")
    @Builder.Default
    private String message = "Success";

    @Schema(description = "响应数据")
    private E data;

    /**
     * 成功响应
     */
    public static <T> R<T> success() {
        return R.<T>builder().code(ResponseCodeEnum.SUCCESS.getValue()).build();
    }

    /**
     * 成功响应
     */
    public static <T> R<T> success(T data) {
        return R.<T>builder().code(ResponseCodeEnum.SUCCESS.getValue()).data(data).build();
    }

    /**
     * 系统错误
     */
    public static <T> R<T> error() {
        return R.<T>builder().code(ResponseCodeEnum.ERROR.getValue()).message(ResponseCodeEnum.ERROR.getMessage()).build();
    }

    /**
     * 系统错误
     */
    public static <T> R<T> error(T data) {
        return R.<T>builder().code(ResponseCodeEnum.ERROR.getValue()).message(ResponseCodeEnum.ERROR.getMessage()).data(data).build();
    }

    /**
     * 系统错误
     */
    public static <T> R<T> error(String message, T data) {
        return R.<T>builder().code(ResponseCodeEnum.ERROR.getValue()).message(message).data(data).build();
    }

    /**
     * 业务处理失败
     */
    public static <T> R<T> fail(BaseResponseCodeEnum responseCodeEnum) {
        return R.<T>builder().code(responseCodeEnum.getValue()).message(responseCodeEnum.getMessage()).build();
    }

    /**
     * 业务处理失败
     */
    public static <T> R<T> fail(int code, String message) {
        return R.<T>builder().code(code).message(message).build();
    }

    /**
     * 业务处理失败
     */
    public static <T> R<T> fail(int code, String message, T data) {
        return R.<T>builder().code(code).message(message).data(data).build();
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return Objects.equals(this.code, ResponseCodeEnum.SUCCESS.getValue());
    }

}
