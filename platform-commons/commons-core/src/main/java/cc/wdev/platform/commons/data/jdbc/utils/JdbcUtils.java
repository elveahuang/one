package cc.wdev.platform.commons.data.jdbc.utils;

import cc.wdev.platform.commons.data.jdbc.dialect.DbDialect;
import cc.wdev.platform.commons.data.jdbc.dialect.DbMySqlDialect;
import cc.wdev.platform.commons.data.jdbc.dialect.DbPostgresDialect;
import cc.wdev.platform.commons.utils.CollectionUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.util.List;
import java.util.Locale;

import static cc.wdev.platform.commons.constants.GlobalConstants.BATCH_SIZE;

/**
 * @author elvea
 */
@Slf4j
public abstract class JdbcUtils {

    private static IdentifierProcessing getIdentifierProcessing(DatabaseMetaData metaData) throws SQLException {
        String quoteString = metaData.getIdentifierQuoteString();
        IdentifierProcessing.Quoting quoting = StringUtils.hasText(quoteString)
            ? new IdentifierProcessing.Quoting(quoteString) : IdentifierProcessing.Quoting.NONE;

        IdentifierProcessing.LetterCasing letterCasing;
        if (metaData.supportsMixedCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.AS_IS;
        } else if (metaData.storesUpperCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.UPPER_CASE;
        } else if (metaData.storesLowerCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.LOWER_CASE;
        } else {
            letterCasing = IdentifierProcessing.LetterCasing.UPPER_CASE;
        }

        return IdentifierProcessing.create(quoting, letterCasing);
    }

    /**
     * 获取当前数据库链接的数据库方言
     */
    public static DbDialect getDialect(Connection con) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();

        String name = metaData.getDatabaseProductName().toLowerCase(Locale.ENGLISH);

        if (name.contains("mysql")) {
            return new DbMySqlDialect(getIdentifierProcessing(metaData));
        }

        if (name.contains("postgresql")) {
            return DbPostgresDialect.INSTANCE;
        }

        log.info("Couldn't determine Dialect for {}. use DbMySqlDialect.", name);
        return new DbMySqlDialect(getIdentifierProcessing(metaData));
    }

    /**
     * 创建简单临时表
     */
    public static String createTemporaryTable(Connection con, List<Long> data) throws SQLException {
        return createTemporaryTable(con, data, BATCH_SIZE);
    }

    /**
     * 创建简单临时表
     */
    public static String createTemporaryTable(Connection con, List<Long> data, int batchSize) throws SQLException {
        // 生成临时表名
        String temporaryTableName = "tmp_" + ((int) (Math.random() * 100000)) + "_" + System.currentTimeMillis();

        Statement statement = null;
        try {
            // 获取数据库类型
            DbDialect dialect = getDialect(con);
            statement = con.createStatement();

            // 创建临时表
            statement.execute(dialect.temporaryTable().createSimpleTemporaryTable(temporaryTableName));

            String sqlTpl = " insert into %s (id) values %s ";
            if (CollectionUtils.isNotEmpty(data)) {
                List<List<Long>> list = Lists.partition(data, batchSize);
                for (List<Long> idList : list) {
                    String sql = String.format(sqlTpl, temporaryTableName, StringUtils.collectionToDelimitedString(idList, ", ", "(", ")"));
                    statement.execute(sql);
                }
            }
            return temporaryTableName;
        } catch (Exception e) {
            log.error("Create temporary table {} failure. ", temporaryTableName, e);
            throw new SQLException(String.format("Create temporary table %s failure.", temporaryTableName));
        } finally {
            close(statement);
        }
    }

    /**
     * 移除临时表
     *
     * @param con                数据库连接
     * @param temporaryTableName 临时表名
     */
    public static void dropTemporaryTable(Connection con, String temporaryTableName) throws SQLException {
        if (!Strings.isNullOrEmpty(temporaryTableName) && temporaryTableName.startsWith("tmp_")) {
            Statement statement = null;
            try {
                // 获取数据库类型
                DbDialect dialect = getDialect(con);
                statement = con.createStatement();

                // 执行删除表语句
                String sql = dialect.temporaryTable().dropSimpleTemporaryTable(temporaryTableName);
                statement.executeUpdate(sql);
            } catch (Exception e) {
                log.error("Drop temporary table {} failure.", temporaryTableName, e);
                throw new SQLException(String.format("Drop temporary table %s failure.", temporaryTableName));
            } finally {
                close(statement);
            }
        }
    }

    /**
     * 转换成模糊搜索
     *
     * @param q 搜索关键字
     * @return String
     */
    public static String generateLike(String q) {
        return "%" + (Strings.isNullOrEmpty(q) ? "" : q.replaceAll("%", "\\\\%")) + "%";
    }

    public static void close(ResultSet rs, Statement stmt) {
        close(rs);
        close(stmt);
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.error("Could not close JDBC Statement", ex);
            } catch (Throwable ex) {
                log.error("Unexpected exception on closing JDBC Statement", ex);
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                log.trace("Could not close JDBC ResultSet", ex);
            } catch (Throwable ex) {
                log.trace("Unexpected exception on closing JDBC ResultSet", ex);
            }
        }
    }

    public static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                log.trace("Could not close JDBC Connection", ex);
            } catch (Throwable ex) {
                log.trace("Unexpected exception on closing JDBC Connection", ex);
            }
        }
    }

}
