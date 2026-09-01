package cc.wdev.platform.system.i18n.domain.converter;

import cc.wdev.platform.system.i18n.domain.entity.LabelEntity;
import cc.wdev.platform.system.i18n.domain.request.LabelEditRequest;
import cc.wdev.platform.system.i18n.domain.vo.LabelExcelExportVO;
import cc.wdev.platform.system.i18n.domain.vo.LabelExcelImportVo;
import cc.wdev.platform.system.i18n.domain.vo.LabelVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author elvea
 */
@Mapper
public interface LabelConverter {

    LabelConverter INSTANCE = Mappers.getMapper(LabelConverter.class);

    LabelEntity formToEntity(LabelEditRequest request);

    @Mapping(target = "selectLabel", ignore = true)
    LabelVo entityToVo(LabelEntity entity);

    @Mapping(target = "zhCnStaticInd", ignore = true)
    @Mapping(target = "zhTwStaticInd", ignore = true)
    @Mapping(target = "enStaticInd", ignore = true)
    @Mapping(target = "frStaticInd", ignore = true)
    @Mapping(target = "jaStaticInd", ignore = true)
    @Mapping(target = "krStaticInd", ignore = true)
    @Mapping(target = "viStaticInd", ignore = true)
    LabelExcelExportVO entityToExcelVo(LabelEntity entity);

    @Mapping(target = "zhCnStaticInd", ignore = true)
    @Mapping(target = "zhTwStaticInd", ignore = true)
    @Mapping(target = "enStaticInd", ignore = true)
    @Mapping(target = "frStaticInd", ignore = true)
    @Mapping(target = "jaStaticInd", ignore = true)
    @Mapping(target = "krStaticInd", ignore = true)
    @Mapping(target = "viStaticInd", ignore = true)
    LabelEntity imExcelVoToEntity(LabelExcelImportVo importVO);
}
