-- =====================================================================================================================
-- 技术基础表
-- =====================================================================================================================

USE webapp;

--
-- 用户表
--

DROP TABLE IF EXISTS sp_user;

CREATE TABLE sp_user
(
    `id`         BIGINT       NOT NULL PRIMARY KEY COMMENT 'ID' AUTO_INCREMENT,
    `tenant_id`  BIGINT       NOT NULL DEFAULT 0 COMMENT '租户ID',
    `username`   VARCHAR(150) NOT NULL DEFAULT '' COMMENT '用户名',
    `name`       VARCHAR(255) NOT NULL DEFAULT '' COMMENT '姓名',
    `status`     SMALLINT     NOT NULL DEFAULT 1 COMMENT '状态',
    `source`     SMALLINT     NOT NULL DEFAULT 1 COMMENT '来源',
    `version`    BIGINT       NOT NULL DEFAULT 0 COMMENT '版本号',
    `active`     SMALLINT     NOT NULL DEFAULT 1 COMMENT '启用状态',
    `created_by` BIGINT       NOT NULL DEFAULT 0 COMMENT '创建人',
    `created_at` DATETIME     NOT NULL DEFAULT NOW() COMMENT '创建时间',
    `updated_by` BIGINT       NOT NULL DEFAULT 0 COMMENT '修改人',
    `updated_at` DATETIME     NOT NULL DEFAULT NOW() COMMENT '修改时间',
    `deleted_by` BIGINT       NOT NULL DEFAULT 0 COMMENT '删除人',
    `deleted_at` DATETIME     NULL COMMENT '删除时间'
) COMMENT '用户表';

CREATE INDEX ix_sp_user__active ON sp_user (active);
CREATE INDEX ix_sp_user__username ON sp_user (username);

--
-- 角色表
--

DROP TABLE IF EXISTS sp_role;

CREATE TABLE sp_role
(
    `id`         BIGINT       NOT NULL PRIMARY KEY COMMENT 'ID' AUTO_INCREMENT,
    `tenant_id`  BIGINT       NOT NULL DEFAULT 0 COMMENT '租户ID',
    `code`       VARCHAR(150) NOT NULL DEFAULT '' COMMENT '编号',
    `title`      VARCHAR(255) NOT NULL DEFAULT '' COMMENT '标题',
    `status`     SMALLINT     NOT NULL DEFAULT 1 COMMENT '状态',
    `source`     SMALLINT     NOT NULL DEFAULT 1 COMMENT '来源',
    `version`    BIGINT       NOT NULL DEFAULT 0 COMMENT '版本号',
    `active`     SMALLINT     NOT NULL DEFAULT 1 COMMENT '启用状态',
    `created_by` BIGINT       NOT NULL DEFAULT 0 COMMENT '创建人',
    `created_at` DATETIME     NOT NULL DEFAULT NOW() COMMENT '创建时间',
    `updated_by` BIGINT       NOT NULL DEFAULT 0 COMMENT '修改人',
    `updated_at` DATETIME     NOT NULL DEFAULT NOW() COMMENT '修改时间',
    `deleted_by` BIGINT       NOT NULL DEFAULT 0 COMMENT '删除人',
    `deleted_at` DATETIME     NULL COMMENT '删除时间'
) COMMENT '角色表';

--
-- 用户-角色关联表
--

DROP TABLE IF EXISTS sp_user_role;

CREATE TABLE sp_user_role
(
    `id`         BIGINT   NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    `tenant_id`  BIGINT   NOT NULL DEFAULT 0 COMMENT '租户ID',
    `role_id`    BIGINT   NOT NULL DEFAULT 0 COMMENT '角色ID',
    `user_id`    BIGINT   NOT NULL DEFAULT 0 COMMENT '用户ID',
    `version`    BIGINT   NOT NULL DEFAULT 0 COMMENT '版本号',
    `active`     SMALLINT NOT NULL DEFAULT 1 COMMENT '启用状态',
    `created_by` BIGINT   NOT NULL DEFAULT 0 COMMENT '创建人',
    `created_at` DATETIME NOT NULL DEFAULT NOW() COMMENT '创建时间'
) COMMENT '用户-角色关联表';

CREATE INDEX ix_sp_user_role__tenant_id ON sp_user_role (tenant_id);
CREATE INDEX ix_sp_user_role__role_id ON sp_user_role (role_id);
CREATE INDEX ix_sp_user_role__user_id ON sp_user_role (user_id);
