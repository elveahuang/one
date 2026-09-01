package cc.wdev.platform.system.site.api;

import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.site.domain.form.KeywordForm;
import cc.wdev.platform.system.site.domain.request.KeywordCheckRequest;
import cc.wdev.platform.system.site.domain.request.KeywordRequest;
import cc.wdev.platform.system.site.domain.vo.KeywordVo;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface KeywordApi {
    Boolean checkKeyword(KeywordCheckRequest request);

    Object deleteKeyword(@Valid DeleteRequest request);

    KeywordVo findById(Long id);

    void saveKeyword(KeywordForm coinInvestorForm);

    Page<KeywordVo> findKeywordList(@Valid KeywordRequest request);
}
