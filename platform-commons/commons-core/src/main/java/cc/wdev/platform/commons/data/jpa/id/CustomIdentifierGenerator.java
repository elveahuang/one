package cc.wdev.platform.commons.data.jpa.id;

import cc.wdev.platform.commons.core.sequence.SequenceManager;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * 自定义主键生成器
 *
 * @author elvea
 * @see IdentifierGenerator
 */
public class CustomIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return SequenceManager.getSequence().nextId();
    }

}
