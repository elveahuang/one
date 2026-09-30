package cc.wdev.platform.commons.utils.excel.listener;

import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.excel.data.AbstractExcelData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.fesod.sheet.context.AnalysisContext;
import org.apache.fesod.sheet.metadata.CellExtra;
import org.apache.fesod.sheet.metadata.data.ReadCellData;
import org.apache.fesod.sheet.read.listener.ReadListener;
import org.apache.fesod.sheet.read.metadata.holder.ReadRowHolder;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@Slf4j
public class ReadWithExtraListener<T extends AbstractExcelData> implements ReadListener<T> {
    private final Integer headRowNumber;

    public ReadWithExtraListener(Integer headRowNumber) {
        this.headRowNumber = headRowNumber;
    }

    @Getter
    private final List<T> dataList = Lists.newArrayList();

    @Getter
    private final List<CellExtra> mergeList = Lists.newArrayList();

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("Header - {}", GsonUtils.toJson(headMap));
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        ReadRowHolder readRowHolder = context.readRowHolder();
        log.info("Data line - {} - {}", readRowHolder.getRowIndex(), GsonUtils.toJson(data));
        data.setLineNo(readRowHolder.getRowIndex() + 1);
        dataList.add(data);
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("CellExtra - {}", GsonUtils.toJson(extra));
        switch (extra.getType()) {
            case COMMENT -> {
                log.info("CellExtra type - {}", extra.getType());
                log.info("CellExtra getFirstRowIndex - {}", extra.getFirstRowIndex());
                log.info("CellExtra getFirstColumnIndex - {}", extra.getFirstColumnIndex());
                log.info("CellExtra getLastRowIndex - {}", extra.getLastRowIndex());
                log.info("CellExtra getLastColumnIndex - {}", extra.getLastColumnIndex());
            }
            case MERGE -> {
                log.info("CellExtra type - {}", extra.getType());
                log.info("CellExtra getFirstRowIndex - {}", extra.getFirstRowIndex());
                log.info("CellExtra getFirstColumnIndex - {}", extra.getFirstColumnIndex());
                log.info("CellExtra getLastRowIndex - {}", extra.getLastRowIndex());
                log.info("CellExtra getLastColumnIndex - {}", extra.getLastColumnIndex());
                if (extra.getRowIndex() >= headRowNumber) {
                    this.mergeList.add(extra);
                }
            }
            default -> {
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Data analyse finished.");
    }

}
