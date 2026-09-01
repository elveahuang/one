package cc.wdev.platform.system.i18n.service;

import cc.wdev.platform.commons.domain.R;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author elvea
 */
public interface LabelExcelService {
    /**
     * 多语言导出excel
     */
    void exportLabelExcel(HttpServletResponse response) throws Exception;

    /**
     * 多语言导出excel
     */
    void exportLabelExcelTemplate(HttpServletResponse response) throws Exception;

    /**
     * 多语言excel导入
     */
    R<?> importLabelExcel(MultipartFile multipartFile);
}
