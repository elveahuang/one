package cc.wdev.platform.commons.data.jdbc.dialect;

import org.springframework.data.relational.core.dialect.MySqlDialect;
import org.springframework.data.relational.core.sql.IdentifierProcessing;

/**
 * @author elvea
 */
public class DbMySqlDialect extends MySqlDialect implements DbDialect {

    public DbMySqlDialect(IdentifierProcessing identifierProcessing) {
        super(identifierProcessing);
    }

    private static final TemporaryTableClause TEMPORARY_TABLE_CLAUSE = new TemporaryTableClause() {
    };

    private static final DateTimeClause DATE_TIME_CLAUSE = new DateTimeClause() {

        @Override
        public String currentDateTime() {
            return "select now();";
        }

        @Override
        public String currentDate() {
            return "select curdate();";
        }

        @Override
        public String currentTime() {
            return "select curtime();";
        }

    };

    @Override
    public TemporaryTableClause temporaryTable() {
        return TEMPORARY_TABLE_CLAUSE;
    }

    @Override
    public DateTimeClause dateTime() {
        return DATE_TIME_CLAUSE;
    }

}
