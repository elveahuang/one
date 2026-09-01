package cc.wdev.platform.system.core.domain.form;

import cc.wdev.platform.system.storage.domain.vo.AttachmentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "账号修改头像表单")
public class UserChangeAvatarForm implements Serializable {

    @Schema(description = "头像附件")
    private AttachmentVo avatar;
}
