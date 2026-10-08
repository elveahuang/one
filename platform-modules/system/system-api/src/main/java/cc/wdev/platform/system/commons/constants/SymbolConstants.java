package cc.wdev.platform.system.commons.constants;

/**
 * 通用符号常量定义。
 * 适用于字符串拼接、格式处理、模板构建、路径处理等场景。
 */
public interface SymbolConstants {

    // 空类字符
    String EMPTY = "";
    String SPACE = " ";
    String TAB = "\t";

    // 换行符
    String NEW_LINE = "\n";       // Unix/Linux
    String CARRIAGE_RETURN = "\r"; // Mac/Windows
    String CRLF = "\r\n";         // Windows标准

    // 常用标点符号
    String DOT = ".";
    String COMMA = ",";
    String COLON = ":";
    String SEMICOLON = ";";
    String EXCLAMATION = "!";
    String QUESTION = "?";

    // 引号类
    String SINGLE_QUOTE = "'";
    String DOUBLE_QUOTE = "\"";
    String BACKTICK = "`";

    // 括号类
    String LEFT_PAREN = "(";
    String RIGHT_PAREN = ")";
    String LEFT_BRACKET = "[";
    String RIGHT_BRACKET = "]";
    String LEFT_BRACE = "{";
    String RIGHT_BRACE = "}";

    // 运算/分隔类
    String EQUALS = "=";
    String PLUS = "+";
    String MINUS = "-";
    String ASTERISK = "*";
    String SLASH = "/";
    String BACKSLASH = "\\";
    String PERCENT = "%";
    String AMPERSAND = "&";
    String PIPE = "|";

    // 分隔符
    String UNDERSCORE = "_";
    String DASH = "-";
    String AT = "@";
    String HASH = "#";
    String DOLLAR = "$";

    // HTML/XML 相关
    String LESS_THAN = "<";
    String GREATER_THAN = ">";
    String AMP = "&amp;";
    String NBSP = "&nbsp;";

    // URL/路径类
    String QUESTION_MARK = "?";
    String AMPERSAND_HTML = "&";
    String FILE_SEPARATOR = "/"; // 推荐动态用 File.separator

    // 占位符
    String PLACEHOLDER_PREFIX = "${";
    String PLACEHOLDER_SUFFIX = "}";

    // 特殊
    String ELLIPSIS = "...";
}
