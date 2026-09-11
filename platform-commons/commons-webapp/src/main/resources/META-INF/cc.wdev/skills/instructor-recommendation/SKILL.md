---
name: instructor-recommendation
description: 当用户需要查找、了解或推荐讲师/老师时使用。先通过 searchInstructor 工具检索讲师，筛选后按当前响应模式交付前端讲师卡片。
---

# 讲师推荐 Skill

## 目标

根据用户的教学与讲师咨询需求检索真实讲师，并按当前响应模式把讲师卡片交给前端渲染。

## 严格流水线规范（必须按序执行）

1. **意图理解**：提取学科方向、讲师姓名、技术特长等意图。
2. **检索候选集**：调用 `searchInstructor` 工具搜索，关键字取用户诉求中的核心词。
3. **相关性过滤**：仅保留高度匹配的讲师，静默丢弃无关项。
4. **生成正文**：正文只写讲师的教学特色评价及后续选课/咨询建议，不得复述讲师清单。
5. **交付**：按下方「交付形态」中当前响应模式对应的方式交付正文与卡片。

## 交付形态（以系统提示中的「当前响应模式」为准）

- **STRICT**：只输出 blocks JSON。正文放在 `{"type":"text","props":{"content":"<Markdown 正文>"}}` 块里，讲师卡片按「卡片规范」生成，禁止输出 `json-render` 围栏。
- **JSON**：正文用 Markdown，末尾输出 **唯一一个** 语言标识为 `json-render` 的代码块，卡片只能出现在该代码块里。
- **TEXT**：只输出 Markdown 正文，用文字或列表说明推荐结果，禁止输出 `json-render` 代码块或任何卡片标记。

## 卡片规范（STRICT / JSON 模式下使用）

- 卡片由 `type` 与 `props` 组成，条目统一放在 `props.items` 数组里，严禁把条目直接挂在 `props` 上。
- 组件字段与数据来源固定如下，多余字段会被前端 Schema 拒绝：

  | type | item 字段 | 数据来源 |
            | --- | --- | --- |
  | `instructor-list-view` | `instructorId`、`instructorName` | `searchInstructor` 返回的 `id`、`name` |
  | `course-list-view` | `courseId`、`courseTitle` | `searchCourse` 返回的 `id`、`title` |

- 元素结构固定为：

  {"type":"instructor-list-view","props":{"items":[{"instructorId":"<讲师id>","instructorName":"<讲师姓名>"}]}}

- `items` 中的字段必须原样复制工具返回结果，两个字段都必填；严禁补充资历、简介、评分等工具未返回的字段。
- `items` 不超过 5 条，优先保留最匹配的。
- 除卡片块外，正文严禁用 Markdown 列表、粗体或表格复述讲师清单。

## 混合交付

- 默认只交付本轮主组件；仅当用户明确还想了解该讲师的主讲课程时，才补充调用 `searchCourse`。
- 课程卡片必须与本轮主卡片写进 **同一个** 数组（STRICT 为 blocks 数组，JSON 为围栏内的 JSON 数组），严禁再开第二个代码块。

## 示例（JSON 模式）

已为你找到匹配讲师，建议先了解教学风格再决定跟课。

```json-render
[{"type":"instructor-list-view","props":{"items":[{"instructorId":"7","instructorName":"李老师"}]}}]
```

## 空结果分支

若检索整体为空或无合适讲师， **不得输出任何卡片**（JSON 模式下即不输出 `json-render` 代码块），直接在正文中正向说明。

## 🚨 核心禁令（违规将被视为严重故障）

1. 绝对禁止伪造异常免责声明（“卡片渲染异常”“讲师卡片加载失败”“暂以文字形式呈现”等）。
2. 绝对禁止在正文中重复罗列讲师清单——清单只能出现在卡片块内（JSON 模式为 `json-render` 围栏，STRICT 模式为卡片 block）。
3. 正向交付：只要有推荐结果，严禁提及“未找到xx讲师”或罗列未命中分支。
