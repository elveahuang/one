package cc.wdev.platform.system.i18n.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.ResponseCodeEnum;
import cc.wdev.platform.commons.exception.ServiceException;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.system.i18n.domain.converter.LabelConverter;
import cc.wdev.platform.system.i18n.domain.entity.LabelEntity;
import cc.wdev.platform.system.i18n.domain.vo.LabelExcelExportVO;
import cc.wdev.platform.system.i18n.domain.vo.LabelExcelImportVo;
import cc.wdev.platform.system.i18n.repository.LabelRepository;
import cc.wdev.platform.system.i18n.service.LabelExcelService;
import cc.wdev.platform.system.i18n.service.LabelService;
import cc.wdev.platform.system.i18n.utils.LabelExcelImportVoListener;
import cc.wdev.platform.system.i18n.utils.SpinnerWriteHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.compress.utils.Lists;
import org.apache.fesod.sheet.FesodSheet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class LabelExcelServiceImpl extends BaseCachingEntityService<LabelEntity, Long, LabelRepository> implements LabelExcelService {

    @Resource
    private LabelService labelService;

    @Override
    public void exportLabelExcel(HttpServletResponse response) throws Exception {
        // 导出数据
        List<LabelExcelExportVO> labelExcelExportVOS = exportLabelExcelData();
        executeExport(response, labelExcelExportVOS);
    }

    @Override
    public void exportLabelExcelTemplate(HttpServletResponse response) throws Exception {
        // 导出数据
        List<LabelExcelExportVO> labelExcelExportVOS = Lists.newArrayList();
        executeExport(response, labelExcelExportVOS);
    }

    @Override
    public R<?> importLabelExcel(MultipartFile multipartFile) {
        LabelExcelImportVoListener labelExcelImportVoListener = new LabelExcelImportVoListener(this.labelService);
        try {
            FesodSheet.read(multipartFile.getInputStream(), LabelExcelImportVo.class, labelExcelImportVoListener)
                .sheet()
                .doRead();
        } catch (Exception e) {
            throw new ServiceException(ResponseCodeEnum.FILE_UPLOAD_ERROR);
        }
        return R.success(labelExcelImportVoListener.getErrorMessageList());
    }

    private List<LabelExcelExportVO> exportLabelExcelData() {
        List<LabelExcelExportVO> exportVo = Lists.newArrayList();
        List<LabelExcelExportVO> collect =
            this.lambdaQueryWrapper().eq(LabelEntity::getActive, ActiveTypeEnum.ENABLED.getValue()).list()
                .stream()
                .map(labelEntity -> {
                    LabelExcelExportVO labelExcelExportVO = LabelConverter.INSTANCE.entityToExcelVo(labelEntity);
                    labelExcelExportVO.setZhCnStaticInd(labelEntity.getZhCnStaticInd().equals(BooleanTypeEnum.TRUE.getValue()) ? "是" : "否");
                    labelExcelExportVO.setZhTwStaticInd(labelEntity.getZhTwStaticInd().equals(BooleanTypeEnum.TRUE.getValue()) ? "是" : "否");
                    labelExcelExportVO.setEnStaticInd(labelEntity.getEnStaticInd().equals(BooleanTypeEnum.TRUE.getValue()) ? "是" : "否");
                    labelExcelExportVO.setJaStaticInd(labelEntity.getJaStaticInd().equals(BooleanTypeEnum.TRUE.getValue()) ? "是" : "否");
                    labelExcelExportVO.setKrStaticInd(labelEntity.getKrStaticInd().equals(BooleanTypeEnum.TRUE.getValue()) ? "是" : "否");
                    labelExcelExportVO.setFrStaticInd(labelEntity.getFrStaticInd().equals(BooleanTypeEnum.TRUE.getValue()) ? "是" : "否");
                    labelExcelExportVO.setViStaticInd(labelEntity.getViStaticInd().equals(BooleanTypeEnum.TRUE.getValue()) ? "是" : "否");
                    return labelExcelExportVO;
                })
                .toList();
        if (CollectionUtils.isNotEmpty(collect)) {
            exportVo.addAll(collect);
        }
        return exportVo;
    }

    /**
     * 导出Excel执行器
     */
    private static void executeExport(HttpServletResponse response, List<LabelExcelExportVO> labelExcelExportVOS) throws IOException {
        String filename = URLEncoder.encode("label", StandardCharsets.UTF_8);
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + filename + ".xlsx");
        FesodSheet.write(response.getOutputStream(), LabelExcelExportVO.class)
            .registerWriteHandler(new SpinnerWriteHandler())
            .sheet("Data")
            .doWrite(labelExcelExportVOS);
    }

}
