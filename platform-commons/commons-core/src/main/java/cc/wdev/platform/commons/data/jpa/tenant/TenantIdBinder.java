package cc.wdev.platform.commons.data.jpa.tenant;

import cc.wdev.platform.commons.core.tenant.GlobalTenantManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.MappingException;
import org.hibernate.binder.AttributeBinder;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.mapping.*;
import org.hibernate.type.descriptor.java.JavaType;

import java.util.Collections;
import java.util.Objects;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

@Slf4j
public class TenantIdBinder implements AttributeBinder<TenantId> {

    public static final String FILTER_NAME = "_tenantId";
    public static final String PARAMETER_NAME = "tenantId";

    @Override
    public void bind(
        TenantId tenantId,
        MetadataBuildingContext buildingContext,
        PersistentClass persistentClass,
        Property property) {

        if (!GlobalTenantManager.getConfig().isEnabled()) {
            log.info("TenantIdBinder.bind: tenantId binding is disabled.");
            return;
        }

        final var collector = buildingContext.getMetadataCollector();
        final String returnedClassName = property.getReturnedClassName();
        final var tenantIdType = collector.getTypeConfiguration().getBasicTypeRegistry().getRegisteredType(returnedClassName);
        final FilterDefinition filterDefinition = collector.getFilterDefinition(FILTER_NAME);
        if (filterDefinition == null) {
            collector.addFilterDefinition(
                new FilterDefinition(
                    FILTER_NAME,
                    "",
                    false,
                    true,
                    singletonMap(PARAMETER_NAME, tenantIdType),
                    Collections.emptyMap()
                )
            );
        } else {
            final JavaType<?> tenantIdTypeJtd = tenantIdType.getJavaTypeDescriptor();
            final JavaType<?> parameterJtd = Objects.requireNonNull(filterDefinition
                    .getParameterJdbcMapping(PARAMETER_NAME))
                .getJavaTypeDescriptor();
            if (!parameterJtd.getJavaTypeClass().equals(tenantIdTypeJtd.getJavaTypeClass())) {
                throw new MappingException(
                    "all @TenantId fields must have the same type: "
                        + parameterJtd.getTypeName()
                        + " differs from "
                        + tenantIdTypeJtd.getTypeName()
                );
            }
        }
        persistentClass.addFilter(
            FILTER_NAME,
            columnNameOrFormula(property)
                + " = :"
                + PARAMETER_NAME,
            true,
            emptyMap(),
            emptyMap()
        );
        property.resetUpdateable(false);
        property.resetOptional(false);
    }

    private String columnNameOrFormula(Property property) {
        if (property.getColumnSpan() != 1) {
            throw new MappingException("@CustomTenantId attribute must be mapped to a single column or formula");
        }
        Selectable selectable = property.getSelectables().getFirst();
        return selectable.isFormula() ? ((Formula) selectable).getFormula() : ((Column) selectable).getName();
    }

}
