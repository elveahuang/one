package cc.wdev.platform.commons.data.jdbc.dialect;

import org.springframework.data.relational.core.dialect.Dialect;

/**
 * @author elvea
 */
public interface DbDialect extends Dialect {

    TemporaryTableClause temporaryTable();

    DateTimeClause dateTime();

}
