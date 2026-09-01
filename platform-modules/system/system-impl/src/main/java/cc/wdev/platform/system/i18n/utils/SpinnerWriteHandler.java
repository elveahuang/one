package cc.wdev.platform.system.i18n.utils;

import cc.wdev.platform.commons.utils.SpringUtils;
import cc.wdev.platform.system.i18n.enums.LabelGroupTypeEnum;
import cc.wdev.platform.system.i18n.service.LabelService;
import lombok.NoArgsConstructor;
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.metadata.holder.WriteSheetHolder;
import org.apache.fesod.sheet.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author elvea
 */
@NoArgsConstructor
public class SpinnerWriteHandler implements SheetWriteHandler {

    private static final LabelService labelService = SpringUtils.getBean(LabelService.class);

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Map<Integer, String[]> mapDropDown = new HashMap<>();
        Long labelCount = labelService.getLabelCount();
        String[] labelStaticInd = {"是", "否"};
        mapDropDown.put(0, Arrays.stream(LabelGroupTypeEnum.values()).map(LabelGroupTypeEnum::getValue).toList().toArray(new String[0]));
        mapDropDown.put(3, labelStaticInd);
        mapDropDown.put(5, labelStaticInd);
        mapDropDown.put(7, labelStaticInd);
        mapDropDown.put(9, labelStaticInd);
        mapDropDown.put(11, labelStaticInd);
        mapDropDown.put(13, labelStaticInd);
        Sheet sheet = writeSheetHolder.getSheet();
        // 开始设置下拉框
        DataValidationHelper helper = sheet.getDataValidationHelper();//设置下拉框
        for (Map.Entry<Integer, String[]> entry : mapDropDown.entrySet()) {
            // 起始行、终止行、起始列、终止列
            CellRangeAddressList addressList = new CellRangeAddressList(2, (int) (labelCount + 50), entry.getKey(), entry.getKey());
            // 设置下拉框数据
            DataValidationConstraint constraint = helper.createExplicitListConstraint(entry.getValue());
            DataValidation dataValidation = helper.createValidation(constraint, addressList);
            // 处理Excel兼容性问题
            if (dataValidation instanceof XSSFDataValidation) {
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.setShowErrorBox(true);
            } else {
                dataValidation.setSuppressDropDownArrow(false);
            }
            sheet.addValidationData(dataValidation);
        }
    }
}
