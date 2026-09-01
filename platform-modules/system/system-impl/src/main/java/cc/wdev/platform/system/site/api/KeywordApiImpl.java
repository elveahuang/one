package cc.wdev.platform.system.site.api;

import cc.wdev.platform.system.commons.domain.request.DeleteRequest;
import cc.wdev.platform.system.site.domain.converter.KeywordConverter;
import cc.wdev.platform.system.site.domain.form.KeywordForm;
import cc.wdev.platform.system.site.domain.request.KeywordCheckRequest;
import cc.wdev.platform.system.site.domain.request.KeywordRequest;
import cc.wdev.platform.system.site.domain.vo.KeywordVo;
import cc.wdev.platform.system.site.service.KeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordApiImpl implements KeywordApi {

    private final KeywordService keywordService;

    @Override
    public Boolean checkKeyword(KeywordCheckRequest request) {
        return keywordService.checkKeyword(request);
    }

    @Override
    public Object deleteKeyword(DeleteRequest request) {
        return keywordService.deleteKeyword(request);
    }

    @Override
    public KeywordVo findById(Long id) {
        return KeywordConverter.INSTANCE.entityToVo(keywordService.findById(id));
    }

    @Override
    public void saveKeyword(KeywordForm coinInvestorForm) {
        keywordService.saveKeyword(coinInvestorForm);
    }

    @Override
    public Page<KeywordVo> findKeywordList(KeywordRequest request) {
        return keywordService.findKeywordList(request).map(KeywordConverter.INSTANCE::entityToVo);
    }
}
