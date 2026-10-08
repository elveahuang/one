package cc.wdev.platform.system.core.domain.vo;

import lombok.*;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.write.style.ColumnWidth;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserExportVo implements Serializable {
    /**
     * 用户名
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "姓名", index = 0)
    private String displayName;
    /**
     * 手机
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "手机号", index = 1)
    private String mobileNumber;
    /**
     * 性别
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "性别", index = 2)
    private String sex;
    /**
     * 年龄
     */
    @ColumnWidth(20)
    @ExcelProperty(value = "年龄", index = 3)
    private Integer age;
}
