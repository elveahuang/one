package cc.wdev.platform.commons.data.jpa.tenant;

import org.hibernate.PropertyValueException;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.GeneratorCreationContext;
import org.hibernate.type.descriptor.java.JavaType;

import java.lang.reflect.Member;
import java.util.EnumSet;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

public class TenantIdGeneration implements BeforeExecutionGenerator {

    private final String entityName;

    private final String propertyName;

    public TenantIdGeneration(TenantId annotation, Member member, GeneratorCreationContext context) {
        entityName = context.getPersistentClass() == null
            ? member.getDeclaringClass().getName()
            : context.getPersistentClass().getEntityName();
        propertyName = context.getProperty().getName();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return INSERT_ONLY;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        final SessionFactoryImplementor sessionFactory = session.getSessionFactory();
        final JavaType<Object> tenantIdentifierJavaType = sessionFactory.getTenantIdentifierJavaType();

        final Object tenantId = session.getTenantIdentifierValue();
        if (currentValue != null) {
            final CurrentTenantIdentifierResolver<Object> resolver = sessionFactory.getCurrentTenantIdentifierResolver();
            if (resolver.isRoot(tenantId)) {
                return currentValue;
            }
            if (!tenantIdentifierJavaType.areEqual(currentValue, tenantId)) {
                throw new PropertyValueException(
                    "assigned tenant id differs from current tenant id: " +
                        tenantIdentifierJavaType.toString(currentValue) +
                        "!=" +
                        tenantIdentifierJavaType.toString(tenantId),
                    entityName,
                    propertyName
                );
            }
        }
        return tenantId;
    }
}
