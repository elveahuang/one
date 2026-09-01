package cc.wdev.platform.system.ai.service.impl;

import cc.wdev.platform.commons.data.core.utils.SpringDataUtils;
import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.enums.StatusTypeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.ai.domain.converter.AiToolConverter;
import cc.wdev.platform.system.ai.domain.entity.AiToolEntity;
import cc.wdev.platform.system.ai.domain.request.AiToolGetRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSaveRequest;
import cc.wdev.platform.system.ai.domain.request.AiToolSearchRequest;
import cc.wdev.platform.system.ai.domain.vo.AiToolSimpleVo;
import cc.wdev.platform.system.ai.domain.vo.AiToolVo;
import cc.wdev.platform.system.ai.repository.AiToolRepository;
import cc.wdev.platform.system.ai.service.AiToolService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 */
@Slf4j
@Service
public class AiToolServiceImpl extends BaseCachingEntityService<AiToolEntity, Long, AiToolRepository> implements AiToolService {

    /**
     * @see AiToolService#findByPage(AiToolSearchRequest)
     */
    @Override
    public Page<AiToolVo> findByPage(AiToolSearchRequest request) {
        IPage<AiToolEntity> page = lambdaQueryWrapper()
            .like(StringUtils.isNotBlank(request.getQ()), AiToolEntity::getToolName, request.getQ())
            .eq(AiToolEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(request.getPageable()));
        if (!MyBatisPlusUtils.isNotEmpty(page)) {
            return SpringDataUtils.emptyPage(request.getPageable());
        }
        List<AiToolVo> vos = page.getRecords().stream().map(AiToolConverter.INSTANCE::entityVo).toList();
        return MyBatisPlusUtils.toSpringDataPage(request.getPageable(), vos, page.getTotal());
    }

    /**
     * @see AiToolService#getAiTool(AiToolGetRequest)
     */
    @Override
    public AiToolVo getAiTool(AiToolGetRequest request) {
        AiToolEntity entity = null;
        if (StringUtils.isNotEmpty(request.getCode())) {
            entity = this.findCacheByCode(request.getCode().trim());
        } else if (ObjectUtils.isValidId(request.getId())) {
            entity = this.findCacheById(request.getId());
        }
        if (entity == null) {
            throw new ServiceException(ResponseCodeEnum.AI_INVALID_TOOL);
        }
        return AiToolConverter.INSTANCE.entityVo(entity);
    }

    /**
     * @see AiToolService#saveAiTool(AiToolSaveRequest)
     */
    @Override
    public void saveAiTool(AiToolSaveRequest request) {
        AiToolEntity entity = AiToolConverter.INSTANCE.formEntity(request);
        if (ObjectUtils.isValidId(request.getId())) {
            entity.setId(request.getId());
        }
        this.save(entity);
    }

    /**
     * @see AiToolService#getAiTools()
     */
    @Override
    public List<AiToolSimpleVo> getAiTools() {
        List<AiToolEntity> toolEntities = this.lambdaQueryWrapper()
            .select(AiToolEntity::getId, AiToolEntity::getToolName)
            .eq(AiToolEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .eq(AiToolEntity::getStatus, StatusTypeEnum.ON.getValue())
            .list();

        return toolEntities.stream().map(entity -> AiToolSimpleVo.builder()
            .id(entity.getId())
            .code(entity.getCode())
            .title(entity.getTitle())
            .toolName(entity.getToolName())
            .build()
        ).toList();
    }

}
