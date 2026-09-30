package cc.wdev.platform.system.i18n.service;

import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.system.i18n.domain.request.LabelEditRequest;
import cc.wdev.platform.system.i18n.domain.request.LabelSearchRequest;
import cc.wdev.platform.system.i18n.domain.vo.LabelVo;
import cc.wdev.platform.system.i18n.enums.LabelTypeEnum;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * @author elvea
 */
public interface LabelService {

    /**
     * 多语言翻译
     */
    void translate(List<Long> ids);

    /**
     * 多语言翻译
     */
    String generate(LabelTypeEnum labelType, File fileDirPath);

    /**
     * 获取多语言列表
     */
    R<Page<LabelVo>> getLabelList(LabelSearchRequest labelSearchRequest);

    /**
     * 编辑
     */
    void saveLabel(LabelEditRequest request);

    /**
     * 编辑
     */
    LabelVo details(LabelSearchRequest request);

    /**
     * 删除
     */
    void delete(List<Long> ids);

    /**
     * 生成多语言
     */
    void download(String filePath, HttpServletResponse response) throws Exception;

    /**
     * 检查标识是否存在
     */
    Boolean checkLabelCode(String labelCode);

    /**
     * 查询所有的标识+分组字符串
     */
    Set<String> getAllLabelCode();

    /**
     * 加载json数据
     */
    void loadingLabelJsonData(String localJsonPath, String groupName) throws Exception;

    /**
     * 获取多语言文本数
     */
    Long getLabelCount();
}
