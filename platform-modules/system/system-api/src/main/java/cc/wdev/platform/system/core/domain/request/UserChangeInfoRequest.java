package cc.wdev.platform.system.core.domain.request;

import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "账号修改基本信息请求")
public class UserChangeInfoRequest {
    private String displayName;
    private String sex;
    private String mobileNumber;
    private String mobileCountryCode;
    private String email;
    private LocalDate birthday;
    private AttachmentVo avatar;
}
