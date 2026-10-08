package cc.wdev.platform.commons.utils.excel.data;

import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelIgnore;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;

import java.io.Serializable;

/**
 * @author elvea
 */
@Data
@ExcelIgnoreUnannotated
public abstract class AbstractExcelData implements Serializable {

    @ExcelIgnore
    protected int lineNo;

}
