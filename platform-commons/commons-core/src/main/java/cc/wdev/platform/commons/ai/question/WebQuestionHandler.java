package cc.wdev.platform.commons.ai.question;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springaicommunity.agent.tools.AskUserQuestionTool;

import java.util.List;
import java.util.Map;

/**
 * @author elvea
 */
@RequiredArgsConstructor
@Builder
public class WebQuestionHandler implements AskUserQuestionTool.QuestionHandler {

    private final String conversationId;

    private final Long userId;

    /**
     * 会话所属租户（来自聊天请求），保证挂起提问缓存 key 与提交回答路径一致
     */
    private final Long tenantId;

    private final WebQuestionService webQuestionService;

    @Override
    public Map<String, String> handle(List<AskUserQuestionTool.Question> questions) {
        return this.webQuestionService.handleQuestions(this.conversationId, this.userId, this.tenantId, questions);
    }

}
