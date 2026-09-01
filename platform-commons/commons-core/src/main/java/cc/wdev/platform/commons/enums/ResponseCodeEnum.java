package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseEnum<Integer>, BaseResponseCodeEnum {
    // 核心基础
    SUCCESS(HttpStatus.OK.value(), "Success", "正确执行并成功返回"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "Bad Request", "错误的请求"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", "未授权"),
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "Forbidden", "访问未授权"),
    NOT_FOUNT(HttpStatus.NOT_FOUND.value(), "Not Found", "请求地址不存在"),
    ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "System Error", "系统错误"),
    // 系统基础
    PARAM_ERROR(1000001, "Param Error", "参数检查不通过"),
    PARAM_NOT_PRESENT(1000002, "Param is not present", "参数不能为空"),
    INVALID_CAPTCHA(1000003, "Incorrect Captcha", "验证码错误或者验证码已经过期"),
    RATE_LIMIT_ERROR(1000004, "Rate Limit", "限流"),
    ATTACHMENT_LIMIT_ERROR(1000005, "Attachment Limit", "附件类型错误"),
    NOT_PRESENT(1000006, "Not present", "不存在"),
    ENTITY_NOT_PRESENT(1000006, "Entity not present", "不存在"),
    ALREADY_EXISTS_DELETE_ERROR(1000007, "Relation already exists delete failed.", "关联已存在，不允许删除"),
    FILE_UPLOAD_ERROR(1000008, "File Upload Error", "上传的文件有误"),
    OPERATION_PERMISSION_ERROR(1000009, "The user does not have the current operation permission", "当前用户无当前操作权限"),
    // 系统模块
    USER__USERNAME_NOT_AVAILABLE(1001001, "Username is Not Available", "用户名不可用"),
    USER__EMAIL_NOT_AVAILABLE(1001002, "E-Mail is Not Available", "邮箱不可用"),
    USER__MOBILE_NOT_AVAILABLE(1001003, "Mobile is not present.", "手机号码不可用"),
    USER__INVALID_USERNAME_OR_PASSWORD(1001004, "Invalid Username or Password.", "用户名或者密码不正确"),
    USER__INVITE_CODE_NOT_AVAILABLE(1001005, "Invitation code is Not Available.", "邀请码不可用"),
    USER__INVITE_CODE_LIMIT_REACHED(1001006, "Invitation limit reached.", "该邀请码邀请数上限"),
    ES_DOCUMENT_SAVE_ERROR(1001007, "The es doc save failed", "es文档保存失败"),
    ES_DOCUMENT_DELETE_ERROR(1001008, "The es doc delete failed", "es文档删除失败"),
    ES_DOCUMENT_QUERY_ERROR(1001009, "The es doc query failed", "es文档查询异常"),
    USER_PASSWORD_NOT_AVAILABLE(1001010, "Password is Not Available", "密码不可用"),


    BIZ_TYPE__NOT_EMPTY(1002001, "BizType is not empty", "业务类型不能为空"),
    BIZ_TYPE__NOT_PRESENT(1002002, "BizType is not present", "业务类型不存在"),
    BIZ_TYPE__NOT_MATCH(1002003, "BizType is not match", "业务类型不匹配"),

    TENANT__PACKAGE_NOT_PRESENT(1003001, "Package is not present", "套餐不存在"),
    TENANT__NOT_PRESENT(1003002, "Tenant is not present", "租户不存在"),
    TENANT__NOT_ACTIVE_OR_DELETED(1003003, "Tenant is not active or deleted", "租户已停用或已删除"),
    TENANT__EXPIRED_ERROR(1003004, "Tenant is expired", "租户已过期"),
    TENANT__USER_FULL_ERROR(1003005, "Tenant is full", "租户已满员"),

    ROLE__NOT_PRESENT(1004001, "Role is not present", "角色不存在"),

    USER__NOT_PRESENT(1005001, "The user is not present", "用户不存在"),
    USER__PASSWORD_NOT_MATCH(1005002, "The original password does not match", "原密码不匹配"),
    USER__PASSWORD_SAME_AS_OLD(1005003, "New password cannot be the same as the original password", "新密码不能与原始密码相同"),

    AI_INVALID_CHAT_TYPE(1006001, "Invalid Chat Type", "错误的对话类型"),
    AI_INVALID_MODEL(1006001, "Invalid Model", "错误的模型"),
    AI_INVALID_KB(1006001, "Invalid KB", "错误的知识库"),
    AI_INVALID_KB_ITEM(1006001, "Invalid KB Item", "错误的知识条目"),
    AI_INVALID_AGENT(1006001, "Invalid Agent", "错误的智能体"),
    AI_INVALID_TOOL(1006001, "Invalid Agent", "错误的智能体"),
    AI_INVALID_AGENT_MODEL(1006001, "Invalid Agent Model", "错误的智能体"),
    AI_INVALID_KB_MODEL(1006001, "Invalid KB Model", "错误的智能体"),

    MESSAGE__MESSAGE_TYPE_NOT_PRESENT(1007001, "The message type is not present", "消息类型不存在"),
    MESSAGE__MESSAGE_CHANNEL_NOT_PRESENT(1007002, "The message channel type is not present", "消息通道不存在"),

    CHAT__SESSION_NOT_PRESENT(1008001, "The session is not present", "该会话不存在"),

    // 文件解析
    PARSE_UNSUPPORTED_TYPE(1009001, "Unsupported File Type", "不支持的文件类型"),
    PARSE_FAILED(1009002, "Parse Failed", "文件解析失败"),
    PARSE_TOO_LARGE(1009003, "File Too Large", "文件大小超出限制"),
    PARSE_TIMEOUT(1009004, "Parse Timeout", "文件解析超时"),
    PARSE_NO_AUDIO(1009005, "No Audio Stream", "文件中不包含音轨"),
    ;

    private final Integer value;
    private final String description;
    private final String message;
}
