package cc.wdev.platform.system.i18n.utils;

import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.i18n.domain.converter.LabelConverter;
import cc.wdev.platform.system.i18n.domain.entity.LabelEntity;
import cc.wdev.platform.system.i18n.domain.vo.LabelExcelImportVo;
import cc.wdev.platform.system.i18n.service.LabelService;
import cc.wdev.platform.system.i18n.service.impl.LabelServiceImpl;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.common.util.ListUtils;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.read.listener.ReadListener;

import java.util.List;
import java.util.Set;

/**
 * @author elvea
 */
@Slf4j
public class LabelExcelImportVoListener implements ReadListener<LabelExcelImportVo> {

    private final LabelService labelService;

    @Getter
    private final List<String> errorMessageList = Lists.newLinkedList();

    /**
     * 储存所有标识
     */
    private final Set<String> storageCode;

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 缓存的数据
     */
    private List<LabelExcelImportVo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    public LabelExcelImportVoListener(LabelService labelService) {
        this.labelService = labelService;
        this.storageCode = labelService.getAllLabelCode();
    }

    /**
     * 这个每一条数据解析都会来调用，检查标识
     */
    @Override
    public void invoke(LabelExcelImportVo data, AnalysisContext context) {
        // 检查分组是否为空
        if (StringUtils.isEmpty(data.getLabelGroupType())) {
            errorMessageList.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行分组为空！");
            log.error("第{}行分组为空！", context.readRowHolder().getRowIndex() + 1);
            return;
        }
        // 检查code是否重复
        if (StringUtils.isEmpty(data.getCode())) {
            errorMessageList.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行标识为空！");
            log.error("第{}行标识为空！", context.readRowHolder().getRowIndex() + 1);
            return;
        }
        if (!storageCode.contains(data.getLabelGroupType() + '.' + data.getCode())) {
            cachedDataList.add(data);
            storageCode.add(data.getCode());
        } else {
            errorMessageList.add("第" + (context.readRowHolder().getRowIndex() + 1) + "行，" + data.getCode() + "标识重复！");
            log.error("第{}行，{}标识重复！", context.readRowHolder().getRowIndex() + 1, data.getCode());
            return;
        }

        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        if (CollectionUtils.isEmpty(cachedDataList)) {
            return;
        }
        LabelServiceImpl service = (LabelServiceImpl) labelService;
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        List<LabelEntity> labelEntities = cachedDataList.stream().map(labelExcelImportVo -> {
            LabelEntity labelEntity = LabelConverter.INSTANCE.imExcelVoToEntity(labelExcelImportVo);
            labelEntity.setZhCnStaticInd("是".equals(labelExcelImportVo.getZhCnStaticInd()) ? BooleanTypeEnum.TRUE.getValue() : BooleanTypeEnum.FALSE.getValue());
            labelEntity.setZhTwStaticInd("是".equals(labelExcelImportVo.getZhTwStaticInd()) ? BooleanTypeEnum.TRUE.getValue() : BooleanTypeEnum.FALSE.getValue());
            labelEntity.setEnStaticInd("是".equals(labelExcelImportVo.getEnStaticInd()) ? BooleanTypeEnum.TRUE.getValue() : BooleanTypeEnum.FALSE.getValue());
            labelEntity.setJaStaticInd("是".equals(labelExcelImportVo.getJaStaticInd()) ? BooleanTypeEnum.TRUE.getValue() : BooleanTypeEnum.FALSE.getValue());
            labelEntity.setKrStaticInd("是".equals(labelExcelImportVo.getKrStaticInd()) ? BooleanTypeEnum.TRUE.getValue() : BooleanTypeEnum.FALSE.getValue());
            labelEntity.setFrStaticInd("是".equals(labelExcelImportVo.getFrStaticInd()) ? BooleanTypeEnum.TRUE.getValue() : BooleanTypeEnum.FALSE.getValue());
            labelEntity.setViStaticInd("是".equals(labelExcelImportVo.getViStaticInd()) ? BooleanTypeEnum.TRUE.getValue() : BooleanTypeEnum.FALSE.getValue());
            return labelEntity;
        }).toList();
        service.saveBatch(labelEntities);
        log.info("存储数据库成功！");
    }

}
