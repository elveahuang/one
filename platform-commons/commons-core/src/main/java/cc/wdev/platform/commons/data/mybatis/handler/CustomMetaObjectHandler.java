package cc.wdev.platform.commons.data.mybatis.handler;

import cc.wdev.platform.commons.data.mybatis.domain.BaseEntity;
import cc.wdev.platform.commons.data.mybatis.domain.SimpleEntity;
import cc.wdev.platform.commons.utils.DateTimeUtils;
import cc.wdev.platform.commons.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.handlers.StrictFill;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author elvea
 * @see MetaObjectHandler
 */
@Slf4j
public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        if (metaObject.getOriginalObject() instanceof BaseEntity entity) {
            entity.setCreatedAt(now);
            entity.setCreatedBy(getCurUserId());
            entity.setUpdatedAt(now);
            entity.setUpdatedBy(getCurUserId());
        } else if (metaObject.getOriginalObject() instanceof SimpleEntity entity) {
            entity.setCreatedAt(now);
            entity.setCreatedBy(getCurUserId());
        } else {
            this.strictInsertFill(metaObject, "createdAt", now);
            this.strictInsertFill(metaObject, "createdBy", Long.class, getCurUserId());
            this.strictInsertFill(metaObject, "updatedAt", now);
            this.strictInsertFill(metaObject, "updatedBy", Long.class, getCurUserId());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        if (metaObject.getOriginalObject() instanceof BaseEntity entity) {
            entity.setUpdatedAt(now);
            entity.setUpdatedBy(getCurUserId());
        } else {
            this.strictInsertFill(metaObject, "updatedAt", now);
            this.strictInsertFill(metaObject, "updatedBy", Long.class, getCurUserId());
        }
    }

    private Long getCurUserId() {
        return SecurityUtils.getUid();
    }

    private void strictInsertFill(MetaObject metaObject, String fieldName, LocalDateTime now) {
        TableInfo tableInfo = findTableInfo(metaObject);
        tableInfo.getFieldList().stream().filter(fieldInfo -> fieldInfo.getProperty().equals(fieldName)).findFirst().ifPresent(fieldInfo -> {
            if (fieldInfo.getPropertyType() == LocalDateTime.class) {
                this.strictFillStrategy(metaObject, fieldName, StrictFill.of(fieldName, LocalDateTime.class, now).getFieldVal());
            } else if (fieldInfo.getPropertyType() == Date.class) {
                this.strictFillStrategy(metaObject, fieldName, StrictFill.of(fieldName, Date.class, DateTimeUtils.toDate(now)).getFieldVal());
            }
        });
    }

}
