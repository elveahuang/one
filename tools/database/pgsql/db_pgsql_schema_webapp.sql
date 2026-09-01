-- =====================================================================================================================
-- 技术基础表
-- =====================================================================================================================

\c webapp;

--
-- 用户表
--

DROP TABLE IF EXISTS sp_user;

CREATE TABLE sp_user
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL DEFAULT 0,
    username   VARCHAR(150) NOT NULL DEFAULT '',
    name       VARCHAR(255) NOT NULL DEFAULT '',
    status     SMALLINT     NOT NULL DEFAULT 1,
    source     SMALLINT     NOT NULL DEFAULT 2,
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP(3) NOT NULL DEFAULT NOW(),
    updated_by BIGINT       NOT NULL DEFAULT 0,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT NOW(),
    deleted_by BIGINT       NOT NULL DEFAULT 0,
    deleted_at TIMESTAMP(3) NULL
);

CREATE INDEX ix_sp_user__active ON sp_user (active);
CREATE INDEX ix_sp_user__username ON sp_user (username);

COMMENT ON TABLE sp_user IS '用户表';
COMMENT ON COLUMN sp_user.id IS 'ID';
COMMENT ON COLUMN sp_user.tenant_id IS '租户ID';
COMMENT ON COLUMN sp_user.username IS '用户名';
COMMENT ON COLUMN sp_user.name IS '姓名';
COMMENT ON COLUMN sp_user.status IS '状态';
COMMENT ON COLUMN sp_user.source IS '来源';
COMMENT ON COLUMN sp_user.version IS '版本号';
COMMENT ON COLUMN sp_user.active IS '启用状态';
COMMENT ON COLUMN sp_user.created_by IS '创建人';
COMMENT ON COLUMN sp_user.created_at IS '创建时间';
COMMENT ON COLUMN sp_user.updated_by IS '修改人';
COMMENT ON COLUMN sp_user.updated_at IS '修改时间';
COMMENT ON COLUMN sp_user.deleted_by IS '删除人';
COMMENT ON COLUMN sp_user.deleted_at IS '删除时间';

--
-- 角色表
--

DROP TABLE IF EXISTS sp_role;

CREATE TABLE sp_role
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL DEFAULT 0,
    code       VARCHAR(150) NOT NULL DEFAULT '',
    title      VARCHAR(255) NOT NULL DEFAULT '',
    status     SMALLINT     NOT NULL DEFAULT 1,
    source     SMALLINT     NOT NULL DEFAULT 2,
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP(3) NOT NULL DEFAULT NOW(),
    updated_by BIGINT       NOT NULL DEFAULT 0,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT NOW(),
    deleted_by BIGINT       NOT NULL DEFAULT 0,
    deleted_at TIMESTAMP(3) NULL
);

COMMENT ON TABLE sp_role IS '角色表';
COMMENT ON COLUMN sp_role.id IS 'ID';
COMMENT ON COLUMN sp_role.tenant_id IS '租户ID';
COMMENT ON COLUMN sp_role.code IS '编号';
COMMENT ON COLUMN sp_role.title IS '标题';
COMMENT ON COLUMN sp_role.status IS '状态';
COMMENT ON COLUMN sp_role.source IS '来源';
COMMENT ON COLUMN sp_role.version IS '版本号';
COMMENT ON COLUMN sp_role.active IS '启用状态';
COMMENT ON COLUMN sp_role.created_by IS '创建人';
COMMENT ON COLUMN sp_role.created_at IS '创建时间';
COMMENT ON COLUMN sp_role.updated_by IS '修改人';
COMMENT ON COLUMN sp_role.updated_at IS '修改时间';
COMMENT ON COLUMN sp_role.deleted_by IS '删除人';
COMMENT ON COLUMN sp_role.deleted_at IS '删除时间';

--
-- 用户-角色关联表
--

DROP TABLE IF EXISTS sp_user_role;

CREATE TABLE sp_user_role
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL DEFAULT 0,
    role_id    BIGINT       NOT NULL DEFAULT 0,
    user_id    BIGINT       NOT NULL DEFAULT 0,
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP(3) NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sp_user_role__tenant_id ON sp_user_role (tenant_id);
CREATE INDEX ix_sp_user_role__role_id ON sp_user_role (role_id);
CREATE INDEX ix_sp_user_role__user_id ON sp_user_role (user_id);

COMMENT ON TABLE sp_user_role IS '用户-角色关联表';
COMMENT ON COLUMN sp_user_role.id IS 'ID';
COMMENT ON COLUMN sp_user_role.tenant_id IS '租户ID';
COMMENT ON COLUMN sp_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sp_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sp_user_role.version IS '版本号';
COMMENT ON COLUMN sp_user_role.active IS '启用状态';
COMMENT ON COLUMN sp_user_role.created_by IS '创建人';
COMMENT ON COLUMN sp_user_role.created_at IS '创建时间';
