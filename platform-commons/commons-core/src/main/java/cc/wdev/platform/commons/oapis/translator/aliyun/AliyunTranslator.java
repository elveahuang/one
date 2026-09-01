package cc.wdev.platform.commons.oapis.translator.aliyun;

import cc.wdev.platform.commons.oapis.translator.Translator;
import cc.wdev.platform.commons.oapis.translator.TranslatorConverter;
import cc.wdev.platform.commons.utils.GsonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云机器翻译
 * <a href="https://www.aliyun.com/product/ai/alimt">...</a>
 *
 * @author elvea
 */
@Slf4j
public class AliyunTranslator implements Translator {

    private final Config config;

    private final TranslatorConverter converter = new AliyunTranslatorConverter();

    public AliyunTranslator(Config config) {
        this.config = config;
    }

    /**
     * @see Translator#translate(String, String, String)
     */
    @Override
    public String translate(String sourceLang, String targetLang, String text) {
        String sourceServerLang = this.converter.convert(sourceLang);
        String targetServerLang = this.converter.convert(targetLang);

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(this.config.getAccessKeyId())
            .setAccessKeySecret(this.config.getAccessKeySecret())
            .setEndpoint(this.config.getEndpoint());

        try {
            log.info("AliyunTranslator translate from {} to {} start. text - [{}].", sourceLang, targetLang, text);

            Client client = new Client(config);
            RuntimeOptions runtime = new RuntimeOptions();

            TranslateGeneralRequest request = new TranslateGeneralRequest();
            request.setFormatType("text");
            request.setSourceLanguage(sourceServerLang);
            request.setTargetLanguage(targetServerLang);
            request.setSourceText(text);

            TranslateGeneralResponse response = client.translateGeneralWithOptions(request, runtime);

            log.info("AliyunTranslator translate from {} to {}. response - [{}].", sourceLang, targetLang, GsonUtils.toJson(response));
            if (response.getStatusCode() == 200) {
                String result = response.getBody().getData().getTranslated();
                log.info("AliyunTranslator translate from {} to {} success. text - [{}]  result - [{}]", sourceLang, targetLang, text, result);
                return result;
            }
        } catch (TeaException teaException) {
            com.aliyun.teautil.Common.assertAsString(teaException.message);
            log.error("AliyunTranslator translate from {} to {} fail. error - [{}].", sourceLang, targetLang, teaException.message, teaException);
        } catch (Exception e) {
            TeaException teaException = new TeaException(e.getMessage(), e);
            log.error("AliyunTranslator translate from {} to {} fail. error - [{}].", sourceLang, targetLang, teaException.message, teaException);
        }
        return "";
    }

    /**
     * 阿里云翻译器配置
     */
    @Data
    @NoArgsConstructor
    public static class Config {
        private boolean enabled = false;
        private String endpoint = "mt.aliyuncs.com";
        private String accessKeyId = "";
        private String accessKeySecret = "";
    }

    /**
     * 阿里云翻译器语言转换器
     * <a href="https://help.aliyun.com/document_detail/158269.html">...</a>
     */
    public static class AliyunTranslatorConverter implements TranslatorConverter {

        /**
         * @see TranslatorConverter#convert(String)
         */
        @Override
        public String convert(String language) {
            language = StringUtils.isNotEmpty(language) ? language.trim().toLowerCase() : "";
            return switch (language) {
                case "zh_cn", "zh" -> "zh";
                case "zh_tw" -> "zh-tw";
                case "en_us", "en_uk", "en" -> "en";
                case "ja", "jp" -> "ja";
                case "fr" -> "fr";
                case "kr", "ko" -> "ko";
                case "vi", "vi_vn", "vn" -> "vi";
                default -> language;
            };
        }

    }

}
