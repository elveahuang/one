---
name: memory-management
description: Guidelines for safely reading, writing, and updating long-term memory without exposing internal mechanics to the end-user. Trigger this skill whenever managing memories or when the user asks to remember, forget, or verify preferences.
---

# Memory Management and User Interaction Skill

## Core Principles

1. **Invisible Mechanics (静默原则)**
    - Memory operations (`MemoryCreate`, `MemoryInsert`, `MemoryView`, etc.) are internal system processes.
    - **STRICTLY PROHIBITED:** Mentioning internal files (`MEMORY.md`, `*.md`), memory directories, tool names, or internal two-step storage mechanisms in final
      user-facing responses.

2. **Natural & Terse Confirmation (自然简短确认)**
    - When the user explicitly requests to remember, update, or forget a preference:
        - Perform the necessary tool operations quietly.
        - Respond with a brief, friendly, single-sentence confirmation.
        - **Good:** "好的，已为你记下这个偏好，后续讨论算法时会直接提供完整实现和复杂度分析。"
        - **Bad:** "确认：MEMORY.md 索引 ✅，user_algorithm.md 内容 ✅，已成功写入。"

3. **No Psychological Guessing or Meta-Conversations (禁止元对话与动机揣测)**
    - Never analyze or question the user's intent, testing behavior, or repeated prompts.
    - Avoid defensive or conversational meta-statements such as:
        - "我注意到你连着问了多次..."
        - "如果你是在测试我..."
        - "我不会因为重复请求就烦躁..."
    - If the user asks repeatedly whether something is remembered, simply verify and give a polite, affirmative confirmation.

## Response Flow Example

- **User:** "请记住我以后看算法都需要 Java 实现和复杂度分析。"
- **Agent Action:**
    1. Internally check `MEMORY.md` via `MemoryView`.
    2. Create/update memory via `MemoryCreate` / `MemoryStrReplace`.
    3. Update `MEMORY.md` via `MemoryInsert`.
- **Agent Final Output:**
  "好的，已记下你的偏好。后续所有算法解析我都会直接附带完整的 Java 实现与时间/空间复杂度分析。"
