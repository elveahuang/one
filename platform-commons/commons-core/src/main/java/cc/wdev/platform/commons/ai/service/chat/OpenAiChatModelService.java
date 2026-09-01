package cc.wdev.platform.commons.ai.service.chat;

import cc.wdev.platform.commons.ai.config.ModelChatConfig;
import cc.wdev.platform.commons.ai.config.ModelCommonsConfig;
import cc.wdev.platform.commons.ai.domain.request.SimpleChatRequest;
import cc.wdev.platform.commons.ai.domain.response.SimpleChatResponse;
import cc.wdev.platform.commons.ai.enums.AiServiceProvider;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import static cc.wdev.platform.commons.ai.utils.AiUtils.buildChatModelConfig;

/**
 * @author elvea
 */
public class OpenAiChatModelService extends AbstractChatModelService {

    public OpenAiChatModelService(ModelCommonsConfig commonsConfig, ModelChatConfig modelConfig) {
        super(buildChatModelConfig(commonsConfig, modelConfig));
    }

    /**
     * @see ChatModelService#getServiceProvider()
     */
    @Override
    public AiServiceProvider getServiceProvider() {
        return AiServiceProvider.OPENAI_SDK;
    }

    /**
     * @see ChatModelService#call(SimpleChatRequest)
     */
    @Override
    public SimpleChatResponse<?> call(SimpleChatRequest request) {
        SimpleChatResponse<ChatCompletion> response = new SimpleChatResponse<>();
        try {
            OpenAIClient client = this.getOpenAIClient();

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(this.config.getName())
                .addSystemMessage(request.getSystemPrompt())
                .addUserMessage(request.getPrompt())
                .build();

            ChatCompletion chatCompletion = client.chat().completions().create(params);
            String content = chatCompletion.choices().getFirst().message().content().orElse("未返回有效内容");

            response.setResult(chatCompletion);
            response.setContent(content);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return response;
    }

    private OpenAIClient getOpenAIClient() {
        return OpenAIOkHttpClient.builder()
            .apiKey(this.config.getApiKey())
            .baseUrl(this.config.getBaseUrl())
            .build();
    }

}
