---
name: course-recommendation
description: 当用户需要查找、咨询或推荐课程时使用。先通过 searchCourse 工具检索课程，筛选后按当前响应模式交付前端课程卡片。
---

# 课程推荐 Skill

## 目标

根据用户的学习需求检索真实课程，并按当前响应模式把课程卡片交给前端渲染。

## 严格流水线规范（必须按序执行）

1. **意图理解**：提取学习方向、技术栈、难度级别及约束。
2. **检索候选集**：调用 `searchCourse` 工具搜索，关键字取用户诉求中的核心词。
3. **相关性过滤**：仅保留高度匹配的课程，静默丢弃无关项。
4. **生成正文**：正文只写整体规划理由与下一步学习建议，不得复述课程清单。
5. **交付**：按下方「交付形态」中当前响应模式对应的方式交付正文与卡片。

## 交付形态（以系统提示中的「当前响应模式」为准）

- **STRICT**：只输出 blocks JSON。正文放在 `{"type":"text","props":{"content":"<Markdown 正文>"}}` 块里，课程卡片按「卡片规范」生成，禁止输出 `json-render` 围栏。
- **JSON**：正文用 Markdown，末尾输出 **唯一一个** 语言标识为 `json-render` 的代码块，卡片只能出现在该代码块里。
- **TEXT**：只输出 Markdown 正文，用文字或列表说明推荐结果，禁止输出 `json-render` 代码块或任何卡片标记。

## 卡片规范（STRICT / JSON 模式下使用）

- 卡片由 `type` 与 `props` 组成，条目统一放在 `props.items` 数组里，严禁把条目直接挂在 `props` 上。
- 组件字段与数据来源固定如下，多余字段会被前端 Schema 拒绝：

  | type | item 字段 | 数据来源 |
          | --- | --- | --- |
  | `course-list-view` | `courseId`、`courseTitle` | `searchCourse` 返回的 `id`、`title` |
  | `instructor-list-view` | `instructorId`、`instructorName` | `searchInstructor` 返回的 `id`、`name` |

- 元素结构固定为：

  {"type":"course-list-view","props":{"items":[{"courseId":"<课程id>","courseTitle":"<课程名称>"}]}}

- `items` 中的字段必须原样复制工具返回结果，两个字段都必填；严禁补充价格、讲师、简介、评分等工具未返回的字段。
- `items` 不超过 5 条，优先保留最匹配的。
- 除卡片块外，正文严禁用 Markdown 列表、粗体或表格复述课程清单。

## 混合交付

- 默认只交付本轮主组件；仅当用户明确还想了解讲师时，才补充调用 `searchInstructor`。
- 讲师卡片必须与本轮主卡片写进 **同一个** 数组（STRICT 为 blocks 数组，JSON 为围栏内的 JSON 数组），严禁再开第二个代码块。

## 示例（JSON 模式）

已按你的方向选出匹配课程，建议先补分布式基础再进实战。

```json-render
[{"type":"course-list-view","props":{"items":[{"courseId":"12","courseTitle":"微服务架构实战"}]}}]
```

## 空结果分支

若检索整体为空或无合适课程， **不得输出任何卡片**（JSON 模式下即不输出 `json-render` 代码块），直接在正文中正向说明。

## 🚨 核心禁令（违规将被视为严重故障）

1. 绝对禁止伪造异常免责声明（“卡片渲染异常”“加载失败”“暂以文字形式呈现”等）。
2. 绝对禁止在正文中重复罗列课程清单——清单只能出现在卡片块内（JSON 模式为 `json-render` 围栏，STRICT 模式为卡片 block）。
3. 正向交付：只要有推荐结果，严禁提及“未找到xx课程”或罗列未命中分支。
