-- =====================================================================================================================
-- 核心基础表
-- =====================================================================================================================

\c one_platform;

--
-- 开启PostGIS和PgVector
--

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS postgis;

--
-- 业务类型表
--

DROP TABLE IF EXISTS sys_biz_type;

CREATE TABLE sys_biz_type
(
    id             BIGSERIAL     NOT NULL PRIMARY KEY,
    biz_group_type VARCHAR(150)  NOT NULL DEFAULT '',
    biz_scope_type VARCHAR(150)  NOT NULL DEFAULT '',
    biz_type       VARCHAR(150)  NOT NULL DEFAULT '',
    extra          VARCHAR(2000) NOT NULL DEFAULT '',
    default_config VARCHAR(2000) NOT NULL DEFAULT '',
    custom_config  VARCHAR(2000) NOT NULL DEFAULT '',
    label_key      VARCHAR(255)  NOT NULL DEFAULT '',
    label_group    VARCHAR(255)  NOT NULL DEFAULT '',
    title          VARCHAR(255)  NOT NULL DEFAULT '',
    description    VARCHAR(255)  NOT NULL DEFAULT '',
    idx            SMALLINT      NOT NULL DEFAULT 1,
    status         SMALLINT      NOT NULL DEFAULT 1,
    source         SMALLINT      NOT NULL DEFAULT 2,
    version        BIGINT        NOT NULL DEFAULT 0,
    active         SMALLINT      NOT NULL DEFAULT 1,
    created_by     BIGINT        NOT NULL DEFAULT 0,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by     BIGINT        NOT NULL DEFAULT 0,
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by     BIGINT        NOT NULL DEFAULT 0,
    deleted_at     TIMESTAMP     NULL
);

CREATE INDEX ix_sys_biz_type__key ON sys_biz_type (biz_group_type, biz_type);

COMMENT ON TABLE sys_biz_type IS '业务类型表';
COMMENT ON COLUMN sys_biz_type.id IS 'ID';
COMMENT ON COLUMN sys_biz_type.biz_group_type IS '业务分组';
COMMENT ON COLUMN sys_biz_type.biz_scope_type IS '业务范围';
COMMENT ON COLUMN sys_biz_type.biz_type IS '业务编号';
COMMENT ON COLUMN sys_biz_type.extra IS '附加信息';
COMMENT ON COLUMN sys_biz_type.default_config IS '默认配置';
COMMENT ON COLUMN sys_biz_type.custom_config IS '自定义配置';
COMMENT ON COLUMN sys_biz_type.label_key IS '多语言文本';
COMMENT ON COLUMN sys_biz_type.label_group IS '多语言文本分组';
COMMENT ON COLUMN sys_biz_type.title IS '标题';
COMMENT ON COLUMN sys_biz_type.description IS '备注说明';
COMMENT ON COLUMN sys_biz_type.idx IS '序号';
COMMENT ON COLUMN sys_biz_type.source IS '来源';
COMMENT ON COLUMN sys_biz_type.status IS '状态';
COMMENT ON COLUMN sys_biz_type.version IS '版本号';
COMMENT ON COLUMN sys_biz_type.active IS '启用状态';
COMMENT ON COLUMN sys_biz_type.created_by IS '创建人';
COMMENT ON COLUMN sys_biz_type.created_at IS '创建时间';
COMMENT ON COLUMN sys_biz_type.updated_by IS '修改人';
COMMENT ON COLUMN sys_biz_type.updated_at IS '修改时间';
COMMENT ON COLUMN sys_biz_type.deleted_by IS '删除人';
COMMENT ON COLUMN sys_biz_type.deleted_at IS '删除时间';

--
-- 身份主体表
--

DROP TABLE IF EXISTS sys_identity;

CREATE TABLE sys_identity
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    uuid       VARCHAR(150) NOT NULL DEFAULT '',
    status     SMALLINT     NOT NULL DEFAULT 1,
    source     SMALLINT     NOT NULL DEFAULT 2,
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by BIGINT       NOT NULL DEFAULT 0,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by BIGINT       NOT NULL DEFAULT 0,
    deleted_at TIMESTAMP    NULL
);

CREATE INDEX ix_sys_identity__active ON sys_identity (active);
CREATE INDEX ix_sys_identity__uuid ON sys_identity (uuid);

COMMENT ON TABLE sys_identity IS '身份主体表';
COMMENT ON COLUMN sys_identity.id IS 'ID';
COMMENT ON COLUMN sys_identity.uuid IS '用户标识';
COMMENT ON COLUMN sys_identity.status IS '状态';
COMMENT ON COLUMN sys_identity.source IS '来源';
COMMENT ON COLUMN sys_identity.version IS '版本号';
COMMENT ON COLUMN sys_identity.active IS '启用状态';
COMMENT ON COLUMN sys_identity.created_by IS '创建人';
COMMENT ON COLUMN sys_identity.created_at IS '创建时间';
COMMENT ON COLUMN sys_identity.updated_by IS '修改人';
COMMENT ON COLUMN sys_identity.updated_at IS '修改时间';
COMMENT ON COLUMN sys_identity.deleted_by IS '删除人';
COMMENT ON COLUMN sys_identity.deleted_at IS '删除时间';

--
-- 租户表
--

DROP TABLE IF EXISTS sys_tenant;

CREATE TABLE sys_tenant
(
    id                     BIGSERIAL    NOT NULL PRIMARY KEY,
    code                   VARCHAR(150) NOT NULL DEFAULT '',
    title                  VARCHAR(255) NOT NULL DEFAULT '',
    details                TEXT         NULL,
    address                VARCHAR(255) NOT NULL DEFAULT '',
    domain                 VARCHAR(255) NOT NULL DEFAULT '',
    contact_user           VARCHAR(50)  NOT NULL DEFAULT '',
    contact_phone          VARCHAR(50)  NOT NULL DEFAULT '',
    company_name           VARCHAR(50)  NOT NULL DEFAULT '',
    company_license_number VARCHAR(50)  NOT NULL DEFAULT '',
    registration_date      TIMESTAMP    NOT NULL DEFAULT NOW(),
    expiration_date        TIMESTAMP    NOT NULL DEFAULT NOW(),
    account_count          INT          NOT NULL DEFAULT 0,
    root_ind               SMALLINT     NOT NULL DEFAULT 0,
    description            VARCHAR(255) NOT NULL DEFAULT '',
    status                 SMALLINT     NOT NULL DEFAULT 1,
    source                 SMALLINT     NOT NULL DEFAULT 2,
    version                BIGINT       NOT NULL DEFAULT 0,
    active                 SMALLINT     NOT NULL DEFAULT 1,
    created_by             BIGINT       NOT NULL DEFAULT 0,
    created_at             TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by             BIGINT       NOT NULL DEFAULT 0,
    updated_at             TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by             BIGINT       NOT NULL DEFAULT 0,
    deleted_at             TIMESTAMP    NULL
);

CREATE INDEX ix_sys_tenant__active ON sys_tenant (active);
CREATE INDEX ix_sys_tenant__code ON sys_tenant (code);

COMMENT ON TABLE sys_tenant IS '租户表';
COMMENT ON COLUMN sys_tenant.id IS 'ID';
COMMENT ON COLUMN sys_tenant.code IS '租户编号';
COMMENT ON COLUMN sys_tenant.title IS '名称';
COMMENT ON COLUMN sys_tenant.details IS '简介';
COMMENT ON COLUMN sys_tenant.address IS '地址';
COMMENT ON COLUMN sys_tenant.domain IS '域名';
COMMENT ON COLUMN sys_tenant.contact_user IS '联系人';
COMMENT ON COLUMN sys_tenant.contact_phone IS '联系电话';
COMMENT ON COLUMN sys_tenant.company_name IS '企业名称';
COMMENT ON COLUMN sys_tenant.company_license_number IS '统一社会信用代码';
COMMENT ON COLUMN sys_tenant.registration_date IS '注册时间';
COMMENT ON COLUMN sys_tenant.expiration_date IS '到期时间';
COMMENT ON COLUMN sys_tenant.account_count IS '租户用户数';
COMMENT ON COLUMN sys_tenant.root_ind IS '是否顶层租户，顶层租户拥有最大的权限';
COMMENT ON COLUMN sys_tenant.description IS '备注说明';
COMMENT ON COLUMN sys_tenant.source IS '来源';
COMMENT ON COLUMN sys_tenant.status IS '状态';
COMMENT ON COLUMN sys_tenant.version IS '版本号';
COMMENT ON COLUMN sys_tenant.active IS '启用状态';
COMMENT ON COLUMN sys_tenant.created_by IS '创建人';
COMMENT ON COLUMN sys_tenant.created_at IS '创建时间';
COMMENT ON COLUMN sys_tenant.updated_by IS '修改人';
COMMENT ON COLUMN sys_tenant.updated_at IS '修改时间';
COMMENT ON COLUMN sys_tenant.deleted_by IS '删除人';
COMMENT ON COLUMN sys_tenant.deleted_at IS '删除时间';

--
-- 权限表
-- biz_type - TENANT - 租户套餐功能权限
-- biz_type - MEMBER - 会员套餐功能权限
--

DROP TABLE IF EXISTS sys_authority;

CREATE TABLE sys_authority
(
    id                   BIGSERIAL    NOT NULL PRIMARY KEY,
    parent_id            BIGINT       NOT NULL DEFAULT 0,
    code                 VARCHAR(100) NOT NULL DEFAULT '',
    title                VARCHAR(150) NOT NULL DEFAULT '',
    label                VARCHAR(150) NOT NULL DEFAULT '',
    idx                  INT          NOT NULL DEFAULT 999,
    description          VARCHAR(255) NOT NULL DEFAULT '',
    authority_type       VARCHAR(100) NOT NULL DEFAULT '',
    authority_biz_type   VARCHAR(100) NOT NULL DEFAULT '',
    authority_scope_type VARCHAR(100) NOT NULL DEFAULT '',
    authority_role_type  VARCHAR(500) NOT NULL DEFAULT '',
    status               SMALLINT     NOT NULL DEFAULT 1,
    source               SMALLINT     NOT NULL DEFAULT 2,
    version              BIGINT       NOT NULL DEFAULT 0,
    active               SMALLINT     NOT NULL DEFAULT 1,
    created_by           BIGINT       NOT NULL DEFAULT 0,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           BIGINT       NOT NULL DEFAULT 0,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by           BIGINT       NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP    NULL
);

CREATE INDEX ix_sys_authority__parent_id ON sys_authority (parent_id);
CREATE INDEX ix_sys_authority__authority_type ON sys_authority (authority_type);
CREATE INDEX ix_sys_authority__authority_biz_type ON sys_authority (authority_biz_type);
CREATE INDEX ix_sys_authority__authority_scope_type ON sys_authority (authority_scope_type);
CREATE INDEX ix_sys_authority__authority_role_type ON sys_authority (authority_role_type);
CREATE INDEX ix_sys_authority__code ON sys_authority (code);

COMMENT ON TABLE sys_authority IS '权限表';
COMMENT ON COLUMN sys_authority.id IS 'ID';
COMMENT ON COLUMN sys_authority.parent_id IS 'Parent ID';
COMMENT ON COLUMN sys_authority.code IS '编码';
COMMENT ON COLUMN sys_authority.title IS '标题';
COMMENT ON COLUMN sys_authority.label IS '多语言文本';
COMMENT ON COLUMN sys_authority.idx IS '序号';
COMMENT ON COLUMN sys_authority.description IS '备注';
COMMENT ON COLUMN sys_authority.authority_type IS '权限类型';
COMMENT ON COLUMN sys_authority.authority_biz_type IS '权限业务类型';
COMMENT ON COLUMN sys_authority.authority_scope_type IS '权限范围类型';
COMMENT ON COLUMN sys_authority.authority_role_type IS '权限角色类型';
COMMENT ON COLUMN sys_authority.status IS '状态';
COMMENT ON COLUMN sys_authority.source IS '来源';
COMMENT ON COLUMN sys_authority.version IS '版本号';
COMMENT ON COLUMN sys_authority.active IS '启用状态';
COMMENT ON COLUMN sys_authority.created_by IS '创建人';
COMMENT ON COLUMN sys_authority.created_at IS '创建时间';
COMMENT ON COLUMN sys_authority.updated_by IS '修改人';
COMMENT ON COLUMN sys_authority.updated_at IS '修改时间';
COMMENT ON COLUMN sys_authority.deleted_by IS '删除人';
COMMENT ON COLUMN sys_authority.deleted_at IS '删除时间';

--
-- 套餐表
--

DROP TABLE IF EXISTS sys_package;

CREATE TABLE sys_package
(
    id          BIGSERIAL     NOT NULL PRIMARY KEY,
    biz_type    VARCHAR(100)  NOT NULL DEFAULT '',
    code        VARCHAR(150)  NOT NULL DEFAULT '',
    title       VARCHAR(150)  NOT NULL DEFAULT '',
    label       VARCHAR(150)  NOT NULL DEFAULT '',
    privilege   VARCHAR(1000) NOT NULL DEFAULT '',
    default_ind SMALLINT      NOT NULL DEFAULT 0,
    trial_ind   SMALLINT      NOT NULL DEFAULT 0,
    trial_limit INT           NOT NULL DEFAULT 60,
    level       INT           NOT NULL DEFAULT 1,
    idx         INT           NOT NULL DEFAULT 999,
    description VARCHAR(255)  NOT NULL DEFAULT '',
    source      SMALLINT      NOT NULL DEFAULT 2,
    version     BIGINT        NOT NULL DEFAULT 0,
    active      SMALLINT      NOT NULL DEFAULT 1,
    created_by  BIGINT        NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by  BIGINT        NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT        NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP     NULL
);

CREATE INDEX ix_sys_package__biz_type ON sys_package (biz_type);
CREATE INDEX ix_sys_package__code ON sys_package (code);

COMMENT ON TABLE sys_package IS '套餐表';
COMMENT ON COLUMN sys_package.id IS 'ID';
COMMENT ON COLUMN sys_package.biz_type IS '业务类型';
COMMENT ON COLUMN sys_package.code IS '编号';
COMMENT ON COLUMN sys_package.title IS '标题';
COMMENT ON COLUMN sys_package.label IS '内容';
COMMENT ON COLUMN sys_package.privilege IS '特权';
COMMENT ON COLUMN sys_package.default_ind IS '是否默认';
COMMENT ON COLUMN sys_package.trial_ind IS '是否允许试用';
COMMENT ON COLUMN sys_package.trial_limit IS '试用时长，单位是自然天';
COMMENT ON COLUMN sys_package.level IS '会员等级，等级越高显示优先级越高';
COMMENT ON COLUMN sys_package.idx IS '序号';
COMMENT ON COLUMN sys_package.description IS '备注';
COMMENT ON COLUMN sys_package.source IS '来源';
COMMENT ON COLUMN sys_package.active IS '启用状态';
COMMENT ON COLUMN sys_package.version IS '版本号';
COMMENT ON COLUMN sys_package.created_by IS '创建人';
COMMENT ON COLUMN sys_package.created_at IS '创建时间';
COMMENT ON COLUMN sys_package.updated_by IS '修改人';
COMMENT ON COLUMN sys_package.updated_at IS '修改时间';
COMMENT ON COLUMN sys_package.deleted_by IS '删除人';
COMMENT ON COLUMN sys_package.deleted_at IS '删除时间';

--
-- 套餐明细表
--

DROP TABLE IF EXISTS sys_package_item;

CREATE TABLE sys_package_item
(
    id                    BIGSERIAL      NOT NULL PRIMARY KEY,
    biz_type              VARCHAR(100)   NOT NULL DEFAULT '',
    package_id            BIGINT         NOT NULL DEFAULT 0,
    code                  VARCHAR(150)   NOT NULL DEFAULT '',
    title                 VARCHAR(150)   NOT NULL DEFAULT '',
    label                 VARCHAR(150)   NOT NULL DEFAULT '',
    automatic_renewal_ind SMALLINT       NOT NULL DEFAULT 0,
    list_price            NUMERIC(10, 6) NOT NULL DEFAULT 0,
    price                 NUMERIC(10, 6) NOT NULL DEFAULT 0,
    date_unit             INT            NOT NULL DEFAULT 0,
    date_value            INT            NOT NULL DEFAULT 0,
    idx                   INT            NOT NULL DEFAULT 999,
    description           VARCHAR(255)   NOT NULL DEFAULT '',
    status                SMALLINT       NOT NULL DEFAULT 1,
    version               BIGINT         NOT NULL DEFAULT 0,
    active                SMALLINT       NOT NULL DEFAULT 1,
    created_by            BIGINT         NOT NULL DEFAULT 0,
    created_at            TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_by            BIGINT         NOT NULL DEFAULT 0,
    updated_at            TIMESTAMP      NOT NULL DEFAULT NOW(),
    deleted_by            BIGINT         NOT NULL DEFAULT 0,
    deleted_at            TIMESTAMP      NULL
);

CREATE INDEX ix_sys_package_item__biz_type ON sys_package_item (biz_type);
CREATE INDEX ix_sys_package_item__package_id ON sys_package_item (package_id);
CREATE INDEX ix_sys_package_item__code ON sys_package_item (code);

COMMENT ON TABLE sys_package_item IS '套餐明细表';
COMMENT ON COLUMN sys_package_item.id IS 'ID';
COMMENT ON COLUMN sys_package_item.biz_type IS '业务类型';
COMMENT ON COLUMN sys_package_item.package_id IS '套餐ID';
COMMENT ON COLUMN sys_package_item.code IS '编号';
COMMENT ON COLUMN sys_package_item.title IS '标题';
COMMENT ON COLUMN sys_package_item.label IS '文本';
COMMENT ON COLUMN sys_package_item.automatic_renewal_ind IS '是否自动续费';
COMMENT ON COLUMN sys_package_item.list_price IS '划线价格';
COMMENT ON COLUMN sys_package_item.price IS '价格';
COMMENT ON COLUMN sys_package_item.date_unit IS '单位';
COMMENT ON COLUMN sys_package_item.date_value IS '数值';
COMMENT ON COLUMN sys_package_item.idx IS '序号';
COMMENT ON COLUMN sys_package_item.description IS '备注';
COMMENT ON COLUMN sys_package_item.status IS '发布状态';
COMMENT ON COLUMN sys_package_item.active IS '启用状态';
COMMENT ON COLUMN sys_package_item.version IS '版本号';
COMMENT ON COLUMN sys_package_item.created_by IS '创建人';
COMMENT ON COLUMN sys_package_item.created_at IS '创建时间';
COMMENT ON COLUMN sys_package_item.updated_by IS '修改人';
COMMENT ON COLUMN sys_package_item.updated_at IS '修改时间';
COMMENT ON COLUMN sys_package_item.deleted_by IS '删除人';
COMMENT ON COLUMN sys_package_item.deleted_at IS '删除时间';

--
-- 实体-权限关联表
--

DROP TABLE IF EXISTS sys_entity_authority;

CREATE TABLE sys_entity_authority
(
    id           BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 0,
    biz_type     VARCHAR(100) NOT NULL DEFAULT '',
    entity_id    BIGINT       NOT NULL DEFAULT 0,
    authority_id BIGINT       NOT NULL DEFAULT 0,
    version      BIGINT       NOT NULL DEFAULT 0,
    active       SMALLINT     NOT NULL DEFAULT 1,
    created_by   BIGINT       NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_entity_authority__entity ON sys_entity_authority (tenant_id, biz_type, entity_id);

COMMENT ON TABLE sys_entity_authority IS '实体-权限关联表';
COMMENT ON COLUMN sys_entity_authority.id IS 'ID';
COMMENT ON COLUMN sys_entity_authority.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_entity_authority.biz_type IS '业务类型';
COMMENT ON COLUMN sys_entity_authority.entity_id IS '实体ID';
COMMENT ON COLUMN sys_entity_authority.authority_id IS '权限ID';
COMMENT ON COLUMN sys_entity_authority.version IS '版本号';
COMMENT ON COLUMN sys_entity_authority.active IS '启用状态';
COMMENT ON COLUMN sys_entity_authority.created_by IS '创建人';
COMMENT ON COLUMN sys_entity_authority.created_at IS '创建时间';

--
-- 套餐实体关联表
--

DROP TABLE IF EXISTS sys_entity_package;

CREATE TABLE sys_entity_package
(
    id                BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL DEFAULT 0,
    biz_type          VARCHAR(100) NOT NULL DEFAULT '',
    entity_id         BIGINT       NOT NULL DEFAULT 0,
    package_id        BIGINT       NOT NULL DEFAULT 0,
    trial_start_date  TIMESTAMP    NULL,
    trial_end_date    TIMESTAMP    NULL,
    registration_date TIMESTAMP    NULL,
    expiration_date   TIMESTAMP    NULL,
    description       VARCHAR(255) NOT NULL DEFAULT '',
    version           BIGINT       NOT NULL DEFAULT 0,
    active            SMALLINT     NOT NULL DEFAULT 1,
    created_by        BIGINT       NOT NULL DEFAULT 0,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by        BIGINT       NOT NULL DEFAULT 0,
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by        BIGINT       NOT NULL DEFAULT 0,
    deleted_at        TIMESTAMP    NULL
);

CREATE INDEX ix_sys_package_entity__tenant_id ON sys_entity_package (tenant_id);
CREATE INDEX ix_sys_package_entity__package_id ON sys_entity_package (package_id);
CREATE INDEX ix_sys_package_entity__biz_type ON sys_entity_package (biz_type, entity_id);

COMMENT ON TABLE sys_entity_package IS '套餐实体关联表';
COMMENT ON COLUMN sys_entity_package.id IS 'ID';
COMMENT ON COLUMN sys_entity_package.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_entity_package.biz_type IS '业务类型';
COMMENT ON COLUMN sys_entity_package.entity_id IS '实体ID';
COMMENT ON COLUMN sys_entity_package.package_id IS '套餐ID';
COMMENT ON COLUMN sys_entity_package.trial_start_date IS '会员试用开始时间';
COMMENT ON COLUMN sys_entity_package.trial_end_date IS '会员试用结束时间';
COMMENT ON COLUMN sys_entity_package.registration_date IS '会员注册时间';
COMMENT ON COLUMN sys_entity_package.expiration_date IS '会员到期时间';
COMMENT ON COLUMN sys_entity_package.description IS '备注说明';
COMMENT ON COLUMN sys_entity_package.version IS '版本号';
COMMENT ON COLUMN sys_entity_package.active IS '启用状态';
COMMENT ON COLUMN sys_entity_package.created_by IS '创建人';
COMMENT ON COLUMN sys_entity_package.created_at IS '创建时间';
COMMENT ON COLUMN sys_entity_package.updated_by IS '修改人';
COMMENT ON COLUMN sys_entity_package.updated_at IS '修改时间';
COMMENT ON COLUMN sys_entity_package.deleted_by IS '删除人';
COMMENT ON COLUMN sys_entity_package.deleted_at IS '删除时间';

--
-- 实体套餐开通记录表
--

DROP TABLE IF EXISTS sys_package_log;

CREATE TABLE sys_package_log
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    biz_type    VARCHAR(100) NOT NULL DEFAULT '',
    biz_id      BIGINT       NOT NULL DEFAULT 0,
    package_id  BIGINT       NOT NULL DEFAULT 0,
    order_id    BIGINT       NOT NULL DEFAULT 0,
    quota       BIGINT       NOT NULL DEFAULT 0,
    description VARCHAR(255) NOT NULL DEFAULT '',
    log_type    SMALLINT     NOT NULL DEFAULT 0,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_package_log__tenant_id ON sys_package_log (tenant_id);
CREATE INDEX ix_sys_package_log__biz_id ON sys_package_log (biz_type, biz_id);
CREATE INDEX ix_sys_package_log__package_id ON sys_package_log (package_id);

COMMENT ON TABLE sys_package_log IS '实体套餐开通记录表';
COMMENT ON COLUMN sys_package_log.id IS 'ID';
COMMENT ON COLUMN sys_package_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_package_log.biz_type IS '业务类型';
COMMENT ON COLUMN sys_package_log.biz_id IS '业务ID';
COMMENT ON COLUMN sys_package_log.package_id IS '套餐ID';
COMMENT ON COLUMN sys_package_log.order_id IS '关联订单ID';
COMMENT ON COLUMN sys_package_log.quota IS '额度';
COMMENT ON COLUMN sys_package_log.description IS '备注';
COMMENT ON COLUMN sys_package_log.log_type IS '日志类型';
COMMENT ON COLUMN sys_package_log.version IS '版本号';
COMMENT ON COLUMN sys_package_log.active IS '启用状态';
COMMENT ON COLUMN sys_package_log.created_by IS '创建人';
COMMENT ON COLUMN sys_package_log.created_at IS '创建时间';
COMMENT ON COLUMN sys_package_log.updated_by IS '修改人';
COMMENT ON COLUMN sys_package_log.updated_at IS '修改时间';
COMMENT ON COLUMN sys_package_log.deleted_by IS '删除人';
COMMENT ON COLUMN sys_package_log.deleted_at IS '删除时间';

--
-- 社交账号关联表
--

DROP TABLE IF EXISTS sys_entity_open_id;

CREATE TABLE sys_entity_open_id
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL DEFAULT 0,
    biz_type   VARCHAR(100) NOT NULL DEFAULT '',
    biz_id     BIGINT       NOT NULL DEFAULT 0,
    union_id   VARCHAR(255) NOT NULL DEFAULT '',
    open_id    VARCHAR(255) NOT NULL DEFAULT '',
    username   VARCHAR(255) NOT NULL DEFAULT '',
    nickname   VARCHAR(255) NOT NULL DEFAULT '',
    email      VARCHAR(255) NOT NULL DEFAULT '',
    avatar     VARCHAR(500) NOT NULL DEFAULT '',
    status     SMALLINT     NOT NULL DEFAULT 1,
    source     SMALLINT     NOT NULL DEFAULT 2,
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by BIGINT       NOT NULL DEFAULT 0,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by BIGINT       NOT NULL DEFAULT 0,
    deleted_at TIMESTAMP    NULL
);

CREATE INDEX ix_sys_entity_open__tenant_id ON sys_entity_open_id (tenant_id);
CREATE INDEX ix_sys_entity_open__biz_id ON sys_entity_open_id (biz_type, biz_id);
CREATE UNIQUE INDEX ix_sys_entity_open__tenant_id_union_id_open_id ON sys_entity_open_id (tenant_id, union_id, open_id);

COMMENT ON TABLE sys_entity_open_id IS '社交账号关联表';
COMMENT ON COLUMN sys_entity_open_id.id IS 'ID';
COMMENT ON COLUMN sys_entity_open_id.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_entity_open_id.biz_type IS '业务类型';
COMMENT ON COLUMN sys_entity_open_id.biz_id IS '业务ID';
COMMENT ON COLUMN sys_entity_open_id.union_id IS 'Union Id';
COMMENT ON COLUMN sys_entity_open_id.open_id IS 'Open ID';
COMMENT ON COLUMN sys_entity_open_id.username IS '用户名';
COMMENT ON COLUMN sys_entity_open_id.nickname IS '昵称';
COMMENT ON COLUMN sys_entity_open_id.email IS '用户邮箱';
COMMENT ON COLUMN sys_entity_open_id.avatar IS '头像地址';
COMMENT ON COLUMN sys_entity_open_id.status IS '状态';
COMMENT ON COLUMN sys_entity_open_id.source IS '来源';
COMMENT ON COLUMN sys_entity_open_id.version IS '版本号';
COMMENT ON COLUMN sys_entity_open_id.active IS '启用状态';
COMMENT ON COLUMN sys_entity_open_id.created_by IS '创建人';
COMMENT ON COLUMN sys_entity_open_id.created_at IS '创建时间';
COMMENT ON COLUMN sys_entity_open_id.updated_by IS '修改人';
COMMENT ON COLUMN sys_entity_open_id.updated_at IS '修改时间';
COMMENT ON COLUMN sys_entity_open_id.deleted_by IS '删除人';
COMMENT ON COLUMN sys_entity_open_id.deleted_at IS '删除时间';

--
-- 角色表
--

DROP TABLE IF EXISTS sys_role;

CREATE TABLE sys_role
(
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    biz_type        VARCHAR(100) NOT NULL DEFAULT '',
    code            VARCHAR(150) NOT NULL DEFAULT '',
    title           VARCHAR(150) NOT NULL DEFAULT '',
    label           VARCHAR(150) NOT NULL DEFAULT '',
    data_scope_type VARCHAR(100) NOT NULL DEFAULT '',
    group_type      VARCHAR(100) NOT NULL DEFAULT '',
    default_ind     SMALLINT     NOT NULL DEFAULT 0,
    description     VARCHAR(255) NOT NULL DEFAULT '',
    status          SMALLINT     NOT NULL DEFAULT 1,
    source          SMALLINT     NOT NULL DEFAULT 2,
    version         BIGINT       NOT NULL DEFAULT 0,
    active          SMALLINT     NOT NULL DEFAULT 1,
    created_by      BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      BIGINT       NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by      BIGINT       NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP    NULL
);

CREATE INDEX ix_sys_role__tenant_id ON sys_role (tenant_id);
CREATE INDEX ix_sys_role__code ON sys_role (code);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS 'ID';
COMMENT ON COLUMN sys_role.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_role.biz_type IS '业务类型';
COMMENT ON COLUMN sys_role.code IS '编号';
COMMENT ON COLUMN sys_role.title IS '标题';
COMMENT ON COLUMN sys_role.label IS '文本';
COMMENT ON COLUMN sys_role.data_scope_type IS '数据范围';
COMMENT ON COLUMN sys_role.description IS '备注';
COMMENT ON COLUMN sys_role.status IS '状态';
COMMENT ON COLUMN sys_role.source IS '来源';
COMMENT ON COLUMN sys_role.version IS '版本号';
COMMENT ON COLUMN sys_role.active IS '启用状态';
COMMENT ON COLUMN sys_role.created_by IS '创建人';
COMMENT ON COLUMN sys_role.created_at IS '创建时间';
COMMENT ON COLUMN sys_role.updated_by IS '修改人';
COMMENT ON COLUMN sys_role.updated_at IS '修改时间';
COMMENT ON COLUMN sys_role.deleted_by IS '删除人';
COMMENT ON COLUMN sys_role.deleted_at IS '删除时间';

--
-- 组织表
--

DROP TABLE IF EXISTS sys_organization;

CREATE TABLE sys_organization
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    parent_id   BIGINT       NOT NULL DEFAULT 0,
    code        VARCHAR(150) NOT NULL DEFAULT '',
    label       VARCHAR(250) NOT NULL DEFAULT '',
    title       VARCHAR(150) NOT NULL DEFAULT '',
    root_ind    SMALLINT     NOT NULL DEFAULT 0,
    default_ind SMALLINT     NOT NULL DEFAULT 0,
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    source      SMALLINT     NOT NULL DEFAULT 2,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_organization__tenant_id ON sys_organization (tenant_id);
CREATE INDEX ix_sys_organization__parent_id ON sys_organization (parent_id);
CREATE INDEX ix_sys_organization__code ON sys_organization (code);

COMMENT ON TABLE sys_organization IS '组织表';
COMMENT ON COLUMN sys_organization.id IS 'ID';
COMMENT ON COLUMN sys_organization.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_organization.parent_id IS '父级ID';
COMMENT ON COLUMN sys_organization.code IS '编号';
COMMENT ON COLUMN sys_organization.label IS '文本';
COMMENT ON COLUMN sys_organization.title IS '标题';
COMMENT ON COLUMN sys_organization.root_ind IS '是否顶层';
COMMENT ON COLUMN sys_organization.default_ind IS '是否默认';
COMMENT ON COLUMN sys_organization.description IS '备注';
COMMENT ON COLUMN sys_organization.status IS '状态';
COMMENT ON COLUMN sys_organization.source IS '来源';
COMMENT ON COLUMN sys_organization.version IS '版本号';
COMMENT ON COLUMN sys_organization.active IS '启用状态';
COMMENT ON COLUMN sys_organization.created_by IS '创建人';
COMMENT ON COLUMN sys_organization.created_at IS '创建时间';
COMMENT ON COLUMN sys_organization.updated_by IS '修改人';
COMMENT ON COLUMN sys_organization.updated_at IS '修改时间';
COMMENT ON COLUMN sys_organization.deleted_by IS '删除人';
COMMENT ON COLUMN sys_organization.deleted_at IS '删除时间';

--
-- 岗位表
--

DROP TABLE IF EXISTS sys_position;

CREATE TABLE sys_position
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    parent_id   BIGINT       NOT NULL DEFAULT 0,
    code        VARCHAR(150) NOT NULL DEFAULT '',
    label       VARCHAR(250) NOT NULL DEFAULT '',
    title       VARCHAR(150) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    root_ind    SMALLINT     NOT NULL DEFAULT 0,
    default_ind SMALLINT     NOT NULL DEFAULT 0,
    status      SMALLINT     NOT NULL DEFAULT 1,
    source      SMALLINT     NOT NULL DEFAULT 2,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_position__tenant_id ON sys_position (tenant_id);
CREATE INDEX ix_sys_position__parent_id ON sys_position (parent_id);
CREATE INDEX ix_sys_position__code ON sys_position (code);

COMMENT ON TABLE sys_position IS '岗位表';
COMMENT ON COLUMN sys_position.id IS 'ID';
COMMENT ON COLUMN sys_position.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_position.parent_id IS '父级ID';
COMMENT ON COLUMN sys_position.code IS '编号';
COMMENT ON COLUMN sys_position.label IS '文本';
COMMENT ON COLUMN sys_position.title IS '标题';
COMMENT ON COLUMN sys_position.description IS '备注';
COMMENT ON COLUMN sys_position.root_ind IS '是否顶层';
COMMENT ON COLUMN sys_position.default_ind IS '是否默认';
COMMENT ON COLUMN sys_position.status IS '状态';
COMMENT ON COLUMN sys_position.source IS '来源';
COMMENT ON COLUMN sys_position.version IS '版本号';
COMMENT ON COLUMN sys_position.active IS '启用状态';
COMMENT ON COLUMN sys_position.created_by IS '创建人';
COMMENT ON COLUMN sys_position.created_at IS '创建时间';
COMMENT ON COLUMN sys_position.updated_by IS '修改人';
COMMENT ON COLUMN sys_position.updated_at IS '修改时间';
COMMENT ON COLUMN sys_position.deleted_by IS '删除人';
COMMENT ON COLUMN sys_position.deleted_at IS '删除时间';

--
-- 用户表
--

DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user
(
    id                   BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id            BIGINT       NOT NULL DEFAULT 0,
    uuid                 VARCHAR(150) NOT NULL DEFAULT '',
    username             VARCHAR(150) NOT NULL DEFAULT '',
    name                 VARCHAR(255) NOT NULL DEFAULT '',
    display_name         VARCHAR(255) NOT NULL DEFAULT '',
    avatar_url           VARCHAR(255) NOT NULL DEFAULT '',
    email                VARCHAR(150) NOT NULL DEFAULT '',
    mobile_country_code  VARCHAR(10)  NOT NULL DEFAULT '',
    mobile_number        VARCHAR(50)  NOT NULL DEFAULT '',
    password             VARCHAR(255) NOT NULL DEFAULT '',
    id_card_type         VARCHAR(50)  NOT NULL DEFAULT '',
    id_card_no           VARCHAR(50)  NOT NULL DEFAULT '',
    sex                  VARCHAR(20)  NOT NULL DEFAULT 'M',
    birthday             DATE         NULL,
    signature            VARCHAR(255) NOT NULL DEFAULT '',
    description          VARCHAR(255) NOT NULL DEFAULT '',
    last_login_status    VARCHAR(255) NOT NULL DEFAULT '',
    last_login_at        VARCHAR(255) NOT NULL DEFAULT '',
    last_login_ip        VARCHAR(255) NOT NULL DEFAULT '',
    password_expire_at   VARCHAR(255) NOT NULL DEFAULT '',
    password_error_at    VARCHAR(255) NOT NULL DEFAULT '',
    password_error_count INT          NOT NULL DEFAULT 0,
    telegram             VARCHAR(255) NOT NULL DEFAULT '',
    registration         TIMESTAMP    NOT NULL DEFAULT NOW(),
    invite_code          VARCHAR(255) NOT NULL DEFAULT '',
    invite_by            BIGINT       NOT NULL DEFAULT 0,
    status               SMALLINT     NOT NULL DEFAULT 1,
    source               SMALLINT     NOT NULL DEFAULT 2,
    version              BIGINT       NOT NULL DEFAULT 0,
    active               SMALLINT     NOT NULL DEFAULT 1,
    created_by           BIGINT       NOT NULL DEFAULT 0,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           BIGINT       NOT NULL DEFAULT 0,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by           BIGINT       NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP    NULL
);

CREATE INDEX ix_sys_user__tenant_id ON sys_user (tenant_id);
CREATE INDEX ix_sys_user__username ON sys_user (username);
CREATE INDEX ix_sys_user__email ON sys_user (email);
CREATE INDEX ix_sys_user__active ON sys_user (active);
CREATE INDEX ix_sys_user__mobile ON sys_user (mobile_country_code, mobile_number);
CREATE INDEX ix_sys_user__invite_code ON sys_user (invite_code);
CREATE INDEX ix_sys_user__invite_by ON sys_user (invite_by);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS 'ID';
COMMENT ON COLUMN sys_user.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user.uuid IS 'UUID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.name IS '姓名';
COMMENT ON COLUMN sys_user.display_name IS '昵称';
COMMENT ON COLUMN sys_user.avatar_url IS '头像地址';
COMMENT ON COLUMN sys_user.email IS '电子邮箱';
COMMENT ON COLUMN sys_user.mobile_country_code IS '手机国家区号';
COMMENT ON COLUMN sys_user.mobile_number IS '手机号码';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.id_card_type IS '证件类型';
COMMENT ON COLUMN sys_user.id_card_no IS '证件号码';
COMMENT ON COLUMN sys_user.sex IS '性别';
COMMENT ON COLUMN sys_user.birthday IS '生日';
COMMENT ON COLUMN sys_user.signature IS '个性签名';
COMMENT ON COLUMN sys_user.description IS '备注';
COMMENT ON COLUMN sys_user.last_login_status IS '最后登录状态';
COMMENT ON COLUMN sys_user.last_login_at IS '最后登录时间';
COMMENT ON COLUMN sys_user.last_login_ip IS '最后登录IP';
COMMENT ON COLUMN sys_user.password_expire_at IS '密码过期时间';
COMMENT ON COLUMN sys_user.password_error_at IS '最后一次输入错误密码的时间';
COMMENT ON COLUMN sys_user.password_error_count IS '输入错误密码的次数';
COMMENT ON COLUMN sys_user.telegram IS 'Telegram';
COMMENT ON COLUMN sys_user.registration IS '注册时间';
COMMENT ON COLUMN sys_user.invite_code IS '邀请码';
COMMENT ON COLUMN sys_user.invite_by IS '邀请人';
COMMENT ON COLUMN sys_user.status IS '状态';
COMMENT ON COLUMN sys_user.source IS '来源';
COMMENT ON COLUMN sys_user.version IS '版本号';
COMMENT ON COLUMN sys_user.active IS '启用状态';
COMMENT ON COLUMN sys_user.created_by IS '创建人';
COMMENT ON COLUMN sys_user.created_at IS '创建时间';
COMMENT ON COLUMN sys_user.updated_by IS '修改人';
COMMENT ON COLUMN sys_user.updated_at IS '修改时间';
COMMENT ON COLUMN sys_user.deleted_by IS '删除人';
COMMENT ON COLUMN sys_user.deleted_at IS '删除时间';


--
-- 邀请统计表
--

DROP TABLE IF EXISTS sys_invite_statistic;

CREATE TABLE sys_invite_statistic
(
    id           BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id    BIGINT    NOT NULL DEFAULT 0,
    user_id      BIGINT    NOT NULL DEFAULT 0,
    invite_count INT       NOT NULL DEFAULT 0,
    version      BIGINT    NOT NULL DEFAULT 0,
    active       SMALLINT  NOT NULL DEFAULT 1,
    created_by   BIGINT    NOT NULL DEFAULT 0,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by   BIGINT    NOT NULL DEFAULT 0,
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_by   BIGINT    NOT NULL DEFAULT 0,
    deleted_at   TIMESTAMP NULL
);

CREATE INDEX ix_sys_invite_statistic__tenant_id ON sys_invite_statistic (tenant_id);
CREATE UNIQUE INDEX ix_sys_invite_statistic__user_id ON sys_invite_statistic (user_id);

COMMENT ON TABLE sys_invite_statistic IS '对话记录';
COMMENT ON COLUMN sys_invite_statistic.id IS 'ID（用此ID生成邀请码）';
COMMENT ON COLUMN sys_invite_statistic.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_invite_statistic.user_id IS '用户ID';
COMMENT ON COLUMN sys_invite_statistic.invite_count IS '邀请数';
COMMENT ON COLUMN sys_invite_statistic.version IS '版本号';
COMMENT ON COLUMN sys_invite_statistic.active IS '启用状态';
COMMENT ON COLUMN sys_invite_statistic.created_by IS '创建人';
COMMENT ON COLUMN sys_invite_statistic.created_at IS '创建时间';
COMMENT ON COLUMN sys_invite_statistic.updated_by IS '修改人';
COMMENT ON COLUMN sys_invite_statistic.updated_at IS '修改时间';
COMMENT ON COLUMN sys_invite_statistic.deleted_by IS '删除人';
COMMENT ON COLUMN sys_invite_statistic.deleted_at IS '删除时间';

--
-- 用户-业务关联表
--

DROP TABLE IF EXISTS sys_user_biz_relation;

CREATE TABLE sys_user_biz_relation
(
    id            BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    user_id       BIGINT       NOT NULL DEFAULT 0,
    biz_type      VARCHAR(100) NOT NULL DEFAULT '',
    biz_id        BIGINT       NOT NULL DEFAULT 0,
    relation_type VARCHAR(100) NOT NULL DEFAULT '',
    date_time     TIMESTAMP,
    version       BIGINT       NOT NULL DEFAULT 0,
    active        SMALLINT     NOT NULL DEFAULT 1,
    created_by    BIGINT       NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_sys_user_entity_relation ON sys_user_biz_relation (tenant_id, user_id, biz_type, biz_id, relation_type);
CREATE INDEX ix_sys_user_biz_relation__tenant_id ON sys_user_biz_relation (tenant_id);
CREATE INDEX uk_sys_user_biz_relation__user_id ON sys_user_biz_relation (user_id);
CREATE INDEX uk_sys_user_biz_relation__relation_type ON sys_user_biz_relation (relation_type);
CREATE INDEX uk_sys_user_biz_relation__biz_type ON sys_user_biz_relation (biz_id, biz_type);

COMMENT ON TABLE sys_user_biz_relation IS '用户实体关系表';
COMMENT ON COLUMN sys_user_biz_relation.id IS 'ID';
COMMENT ON COLUMN sys_user_biz_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_biz_relation.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_biz_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_user_biz_relation.biz_id IS '业务ID';
COMMENT ON COLUMN sys_user_biz_relation.relation_type IS '关系类型';
COMMENT ON COLUMN sys_user_biz_relation.date_time IS '时间';
COMMENT ON COLUMN sys_user_biz_relation.version IS '版本号';
COMMENT ON COLUMN sys_user_biz_relation.active IS '启用状态';
COMMENT ON COLUMN sys_user_biz_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_user_biz_relation.created_at IS '创建时间';

--
-- 用户-角色关联表
--

DROP TABLE IF EXISTS sys_user_role;

CREATE TABLE sys_user_role
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id  BIGINT    NOT NULL DEFAULT 0,
    role_id    BIGINT    NOT NULL DEFAULT 0,
    user_id    BIGINT    NOT NULL DEFAULT 0,
    version    BIGINT    NOT NULL DEFAULT 0,
    active     SMALLINT  NOT NULL DEFAULT 1,
    created_by BIGINT    NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_user_role__tenant_id ON sys_user_role (tenant_id);
CREATE INDEX ix_sys_user_role__role_id ON sys_user_role (role_id);
CREATE INDEX ix_sys_user_role__user_id ON sys_user_role (user_id);

COMMENT ON TABLE sys_user_role IS '用户-角色关联表';
COMMENT ON COLUMN sys_user_role.id IS 'ID';
COMMENT ON COLUMN sys_user_role.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.version IS '版本号';
COMMENT ON COLUMN sys_user_role.active IS '启用状态';
COMMENT ON COLUMN sys_user_role.created_by IS '创建人';
COMMENT ON COLUMN sys_user_role.created_at IS '创建时间';

--
-- 实体关联表
--

DROP TABLE IF EXISTS sys_entity_relation;

CREATE TABLE sys_entity_relation
(
    id             BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id      BIGINT        NOT NULL DEFAULT 0,
    biz_type       VARCHAR(100)  NOT NULL DEFAULT '',
    biz_id         BIGINT        NOT NULL DEFAULT 0,
    ancestor_id    BIGINT        NOT NULL DEFAULT 0,
    entity_id      BIGINT        NOT NULL DEFAULT 0,
    parent_ind     SMALLINT      NOT NULL DEFAULT 0,
    relation_path  VARCHAR(2000) NOT NULL DEFAULT '',
    relation_index VARCHAR(2000) NOT NULL DEFAULT '',
    version        BIGINT        NOT NULL DEFAULT 0,
    active         SMALLINT      NOT NULL DEFAULT 1,
    created_by     BIGINT        NOT NULL DEFAULT 0,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_entity_relation__tenant_id ON sys_entity_relation (tenant_id);
CREATE INDEX ix_sys_entity_relation__ancestor_id ON sys_entity_relation (ancestor_id);
CREATE INDEX ix_sys_entity_relation__entity_id ON sys_entity_relation (entity_id);
CREATE INDEX ix_sys_entity_relation__biz_id ON sys_entity_relation (biz_type, biz_id);

COMMENT ON TABLE sys_entity_relation IS '实体关联表';
COMMENT ON COLUMN sys_entity_relation.id IS 'ID';
COMMENT ON COLUMN sys_entity_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_entity_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_entity_relation.biz_id IS '业务ID';
COMMENT ON COLUMN sys_entity_relation.ancestor_id IS '祖先ID';
COMMENT ON COLUMN sys_entity_relation.entity_id IS '实体ID';
COMMENT ON COLUMN sys_entity_relation.parent_ind IS '是否直接上级';
COMMENT ON COLUMN sys_entity_relation.relation_path IS '关联路径';
COMMENT ON COLUMN sys_entity_relation.relation_index IS '关联层级';
COMMENT ON COLUMN sys_entity_relation.version IS '版本号';
COMMENT ON COLUMN sys_entity_relation.active IS '启用状态';
COMMENT ON COLUMN sys_entity_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_entity_relation.created_at IS '创建时间';

--
-- 登录会话记录
--

DROP TABLE IF EXISTS sys_login_session;

CREATE TABLE sys_login_session
(
    id                   BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id            BIGINT        NOT NULL DEFAULT 0,
    user_id              BIGINT        NOT NULL DEFAULT 0,
    session_id           VARCHAR(50)   NOT NULL DEFAULT '',
    username             VARCHAR(255)  NOT NULL DEFAULT '',
    host                 VARCHAR(150)  NOT NULL DEFAULT '',
    ua                   VARCHAR(2000) NOT NULL DEFAULT '',
    client_id            VARCHAR(150)  NOT NULL DEFAULT '',
    client_name          VARCHAR(150)  NOT NULL DEFAULT '',
    client_version       VARCHAR(150)  NOT NULL DEFAULT '',
    start_datetime       TIMESTAMP     NOT NULL DEFAULT NOW(),
    start_year           INT           NOT NULL DEFAULT 0,
    start_month          INT           NOT NULL DEFAULT 0,
    start_day            INT           NOT NULL DEFAULT 0,
    start_hour           INT           NOT NULL DEFAULT 0,
    start_minute         INT           NOT NULL DEFAULT 0,
    last_access_datetime TIMESTAMP     NOT NULL DEFAULT NOW(),
    last_access_year     INT           NOT NULL DEFAULT 0,
    last_access_month    INT           NOT NULL DEFAULT 0,
    last_access_day      INT           NOT NULL DEFAULT 0,
    last_access_hour     INT           NOT NULL DEFAULT 0,
    last_access_minute   INT           NOT NULL DEFAULT 0,
    end_datetime         TIMESTAMP     NULL,
    success              SMALLINT      NOT NULL DEFAULT 1,
    version              BIGINT        NOT NULL DEFAULT 0,
    active               SMALLINT      NOT NULL DEFAULT 1,
    created_by           BIGINT        NOT NULL DEFAULT 0,
    created_at           TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by           BIGINT        NOT NULL DEFAULT 0,
    updated_at           TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by           BIGINT        NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP     NULL
);

CREATE INDEX ix_sys_login_session__tenant_id ON sys_login_session (tenant_id);
CREATE INDEX ix_sys_login_session__user_id ON sys_login_session (user_id);
CREATE INDEX ix_sys_login_session__session_id ON sys_login_session (session_id);
CREATE INDEX ix_sys_login_session__username ON sys_login_session (username);

COMMENT ON TABLE sys_login_session IS '登录会话记录';
COMMENT ON COLUMN sys_login_session.id IS 'ID';
COMMENT ON COLUMN sys_login_session.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_login_session.user_id IS '用户ID';
COMMENT ON COLUMN sys_login_session.session_id IS 'Session ID';
COMMENT ON COLUMN sys_login_session.username IS '用户名';
COMMENT ON COLUMN sys_login_session.host IS '用户登录主机';
COMMENT ON COLUMN sys_login_session.ua IS 'User Agent';
COMMENT ON COLUMN sys_login_session.client_id IS '客户端编号';
COMMENT ON COLUMN sys_login_session.client_name IS '客户端名称';
COMMENT ON COLUMN sys_login_session.client_version IS '客户端版本';
COMMENT ON COLUMN sys_login_session.start_datetime IS '会话开始时间';
COMMENT ON COLUMN sys_login_session.start_year IS '会话开始年';
COMMENT ON COLUMN sys_login_session.start_month IS '会话开始月';
COMMENT ON COLUMN sys_login_session.start_day IS '会话开始天';
COMMENT ON COLUMN sys_login_session.start_hour IS '会话开始时';
COMMENT ON COLUMN sys_login_session.start_minute IS '会话开始分';
COMMENT ON COLUMN sys_login_session.last_access_datetime IS '最近访问时间';
COMMENT ON COLUMN sys_login_session.last_access_year IS '最近访问年';
COMMENT ON COLUMN sys_login_session.last_access_month IS '最近访问月';
COMMENT ON COLUMN sys_login_session.last_access_day IS '最近访问天';
COMMENT ON COLUMN sys_login_session.last_access_hour IS '最近访问时';
COMMENT ON COLUMN sys_login_session.last_access_minute IS '最近访问分';
COMMENT ON COLUMN sys_login_session.end_datetime IS '会话结束时间';
COMMENT ON COLUMN sys_login_session.success IS '是否成功登录';
COMMENT ON COLUMN sys_login_session.version IS '版本号';
COMMENT ON COLUMN sys_login_session.active IS '启用状态';
COMMENT ON COLUMN sys_login_session.created_by IS '创建人';
COMMENT ON COLUMN sys_login_session.created_at IS '创建时间';
COMMENT ON COLUMN sys_login_session.updated_by IS '修改人';
COMMENT ON COLUMN sys_login_session.updated_at IS '修改时间';
COMMENT ON COLUMN sys_login_session.deleted_by IS '删除人';
COMMENT ON COLUMN sys_login_session.deleted_at IS '删除时间';

--
-- 系统设置表
--

DROP TABLE IF EXISTS sys_config;

CREATE TABLE sys_config
(
    id                  BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id           BIGINT        NOT NULL DEFAULT 0,
    title               VARCHAR(100)  NOT NULL DEFAULT '',
    label               VARCHAR(150)  NOT NULL DEFAULT '',
    config_content_type VARCHAR(100)  NOT NULL DEFAULT '',
    config_group_type   VARCHAR(100)  NOT NULL DEFAULT '',
    config_key          VARCHAR(100)  NOT NULL DEFAULT '',
    config_value        VARCHAR(2000) NOT NULL DEFAULT '',
    default_value       VARCHAR(2000) NOT NULL DEFAULT '',
    extra               VARCHAR(2000) NOT NULL DEFAULT '',
    help                VARCHAR(250)  NOT NULL DEFAULT '',
    description         VARCHAR(250)  NOT NULL DEFAULT '',
    source              SMALLINT      NOT NULL DEFAULT 2,
    version             BIGINT        NOT NULL DEFAULT 0,
    active              SMALLINT      NOT NULL DEFAULT 1,
    created_by          BIGINT        NOT NULL DEFAULT 0,
    created_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by          BIGINT        NOT NULL DEFAULT 0,
    updated_at          TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by          BIGINT        NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMP     NULL
);

CREATE INDEX ix_sys_config__tenant_id ON sys_config (tenant_id);
CREATE INDEX ix_sys_config__key ON sys_config (config_key);
CREATE INDEX ix_sys_config__group ON sys_config (config_group_type);

COMMENT ON TABLE sys_config IS '系统设置表';
COMMENT ON COLUMN sys_config.id IS 'ID';
COMMENT ON COLUMN sys_config.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_config.title IS '标题';
COMMENT ON COLUMN sys_config.label IS '文本';
COMMENT ON COLUMN sys_config.config_content_type IS '配置内容类型';
COMMENT ON COLUMN sys_config.config_group_type IS '配置分组类型';
COMMENT ON COLUMN sys_config.config_key IS '参数名';
COMMENT ON COLUMN sys_config.config_value IS '参数值';
COMMENT ON COLUMN sys_config.default_value IS '默认值';
COMMENT ON COLUMN sys_config.extra IS '附加信息';
COMMENT ON COLUMN sys_config.help IS '帮助信息';
COMMENT ON COLUMN sys_config.description IS '备注';
COMMENT ON COLUMN sys_config.source IS '数据来源';
COMMENT ON COLUMN sys_config.version IS '版本号';
COMMENT ON COLUMN sys_config.active IS '启用状态';
COMMENT ON COLUMN sys_config.created_by IS '创建人';
COMMENT ON COLUMN sys_config.created_at IS '创建时间';
COMMENT ON COLUMN sys_config.updated_by IS '修改人';
COMMENT ON COLUMN sys_config.updated_at IS '修改时间';
COMMENT ON COLUMN sys_config.deleted_by IS '删除人';
COMMENT ON COLUMN sys_config.deleted_at IS '删除时间';

--
-- 系统关键字表
-- tenant_id = 0 : 系统全局关键字
-- tenant_id > 0 : 租户专用关键字
--

DROP TABLE IF EXISTS sys_keyword;

CREATE TABLE sys_keyword
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL DEFAULT 0,
    content    VARCHAR(100) NOT NULL DEFAULT '',
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by BIGINT       NOT NULL DEFAULT 0,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by BIGINT       NOT NULL DEFAULT 0,
    deleted_at TIMESTAMP    NULL
);

COMMENT ON TABLE sys_keyword IS '系统关键字表';
COMMENT ON COLUMN sys_keyword.id IS 'ID';
COMMENT ON COLUMN sys_keyword.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_keyword.content IS '关键字';
COMMENT ON COLUMN sys_keyword.version IS '版本号';
COMMENT ON COLUMN sys_keyword.active IS '启用状态';
COMMENT ON COLUMN sys_keyword.created_by IS '创建人';
COMMENT ON COLUMN sys_keyword.created_at IS '创建时间';
COMMENT ON COLUMN sys_keyword.updated_by IS '修改人';
COMMENT ON COLUMN sys_keyword.updated_at IS '修改时间';
COMMENT ON COLUMN sys_keyword.deleted_by IS '删除人';
COMMENT ON COLUMN sys_keyword.deleted_at IS '删除时间';

--
-- 语言表
--

DROP TABLE IF EXISTS sys_lang;

CREATE TABLE sys_lang
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL DEFAULT '',
    lang        VARCHAR(50)  NOT NULL DEFAULT '',
    country     VARCHAR(50)  NOT NULL DEFAULT '',
    label       VARCHAR(250) NOT NULL DEFAULT '',
    description VARCHAR(250) NOT NULL DEFAULT '',
    default_ind SMALLINT     NOT NULL DEFAULT 0,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_lang__code ON sys_lang (code);

COMMENT ON TABLE sys_lang IS '语言表';
COMMENT ON COLUMN sys_lang.id IS 'ID';
COMMENT ON COLUMN sys_lang.code IS '编号';
COMMENT ON COLUMN sys_lang.lang IS '语言编码';
COMMENT ON COLUMN sys_lang.country IS '地区编码';
COMMENT ON COLUMN sys_lang.label IS '文本';
COMMENT ON COLUMN sys_lang.description IS '备注';
COMMENT ON COLUMN sys_lang.default_ind IS '默认语言';
COMMENT ON COLUMN sys_lang.version IS '版本号';
COMMENT ON COLUMN sys_lang.active IS '启用状态';
COMMENT ON COLUMN sys_lang.created_by IS '创建人';
COMMENT ON COLUMN sys_lang.created_at IS '创建时间';

--
-- 租户-语言关联表
--

DROP TABLE IF EXISTS sys_tenant_lang;

CREATE TABLE sys_tenant_lang
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id  BIGINT    NOT NULL DEFAULT 0,
    lang_id    BIGINT    NOT NULL DEFAULT 0,
    version    BIGINT    NOT NULL DEFAULT 0,
    active     SMALLINT  NOT NULL DEFAULT 1,
    created_by BIGINT    NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_tenant_lang__tenant_id ON sys_tenant_lang (tenant_id);
CREATE INDEX ix_sys_tenant_lang__lang_id ON sys_tenant_lang (lang_id);

COMMENT ON TABLE sys_tenant_lang IS '租户-语言关联表';
COMMENT ON COLUMN sys_tenant_lang.id IS 'ID';
COMMENT ON COLUMN sys_tenant_lang.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_tenant_lang.lang_id IS '语言ID';
COMMENT ON COLUMN sys_tenant_lang.version IS '版本号';
COMMENT ON COLUMN sys_tenant_lang.active IS '启用状态';
COMMENT ON COLUMN sys_tenant_lang.created_by IS '创建人';
COMMENT ON COLUMN sys_tenant_lang.created_at IS '创建时间';

--
-- 多语言文本表
--

DROP TABLE IF EXISTS sys_label;

CREATE TABLE sys_label
(
    id               BIGSERIAL     NOT NULL PRIMARY KEY,
    label_group_type VARCHAR(100)  NOT NULL DEFAULT '',
    code             VARCHAR(255)  NOT NULL DEFAULT '',
    zh_cn_label      VARCHAR(2000) NOT NULL DEFAULT '',
    zh_cn_static_ind SMALLINT      NOT NULL DEFAULT 0,
    zh_tw_label      VARCHAR(2000) NOT NULL DEFAULT '',
    zh_tw_static_ind SMALLINT      NOT NULL DEFAULT 0,
    en_label         VARCHAR(2000) NOT NULL DEFAULT '',
    en_static_ind    SMALLINT      NOT NULL DEFAULT 0,
    ja_label         VARCHAR(2000) NOT NULL DEFAULT '',
    ja_static_ind    SMALLINT      NOT NULL DEFAULT 0,
    kr_label         VARCHAR(2000) NOT NULL DEFAULT '',
    kr_static_ind    SMALLINT      NOT NULL DEFAULT 0,
    fr_label         VARCHAR(2000) NOT NULL DEFAULT '',
    fr_static_ind    SMALLINT      NOT NULL DEFAULT 0,
    vi_label         VARCHAR(2000) NOT NULL DEFAULT '',
    vi_static_ind    SMALLINT      NOT NULL DEFAULT 0,
    version          BIGINT        NOT NULL DEFAULT 0,
    active           SMALLINT      NOT NULL DEFAULT 1,
    created_by       BIGINT        NOT NULL DEFAULT 0,
    created_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by       BIGINT        NOT NULL DEFAULT 0,
    updated_at       TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by       BIGINT        NOT NULL DEFAULT 0,
    deleted_at       TIMESTAMP     NULL
);

CREATE INDEX ix_sys_label__group ON sys_label (label_group_type);
CREATE INDEX ix_sys_label__code ON sys_label (code);

COMMENT ON TABLE sys_label IS '多语言文本表';
COMMENT ON COLUMN sys_label.id IS 'ID';
COMMENT ON COLUMN sys_label.label_group_type IS '多语言分组';
COMMENT ON COLUMN sys_label.code IS '多语言标识';
COMMENT ON COLUMN sys_label.zh_cn_label IS '简体中文';
COMMENT ON COLUMN sys_label.zh_cn_static_ind IS '是否静态文本';
COMMENT ON COLUMN sys_label.zh_tw_label IS '繁体中文';
COMMENT ON COLUMN sys_label.zh_tw_static_ind IS '是否静态文本';
COMMENT ON COLUMN sys_label.en_label IS '英语';
COMMENT ON COLUMN sys_label.en_static_ind IS '是否静态文本';
COMMENT ON COLUMN sys_label.ja_label IS '日语';
COMMENT ON COLUMN sys_label.ja_static_ind IS '是否静态文本';
COMMENT ON COLUMN sys_label.kr_label IS '韩语';
COMMENT ON COLUMN sys_label.kr_static_ind IS '是否静态文本';
COMMENT ON COLUMN sys_label.fr_label IS '法语';
COMMENT ON COLUMN sys_label.fr_static_ind IS '是否静态文本';
COMMENT ON COLUMN sys_label.vi_label IS '越南语';
COMMENT ON COLUMN sys_label.vi_static_ind IS '是否静态文本';
COMMENT ON COLUMN sys_label.version IS '版本号';
COMMENT ON COLUMN sys_label.active IS '启用状态';
COMMENT ON COLUMN sys_label.created_by IS '创建人';
COMMENT ON COLUMN sys_label.created_at IS '创建时间';
COMMENT ON COLUMN sys_label.updated_by IS '修改人';
COMMENT ON COLUMN sys_label.updated_at IS '修改时间';
COMMENT ON COLUMN sys_label.deleted_by IS '删除人';
COMMENT ON COLUMN sys_label.deleted_at IS '删除时间';

--
-- 实体多语言文本表
--

DROP TABLE IF EXISTS sys_entity_label;

CREATE TABLE sys_entity_label
(
    id              BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id       BIGINT        NOT NULL DEFAULT 0,
    lang_id         BIGINT        NOT NULL DEFAULT 0,
    lang_code       VARCHAR(255)  NOT NULL DEFAULT '',
    entity_class    VARCHAR(255)  NOT NULL DEFAULT '',
    entity_property VARCHAR(255)  NOT NULL DEFAULT '',
    entity_id       BIGINT        NOT NULL DEFAULT 0,
    zh_cn_label     VARCHAR(2000) NOT NULL DEFAULT '',
    zh_tw_label     VARCHAR(2000) NOT NULL DEFAULT '',
    en_label        VARCHAR(2000) NOT NULL DEFAULT '',
    ja_label        VARCHAR(2000) NOT NULL DEFAULT '',
    kr_label        VARCHAR(2000) NOT NULL DEFAULT '',
    fr_label        VARCHAR(2000) NOT NULL DEFAULT '',
    version         BIGINT        NOT NULL DEFAULT 0,
    active          SMALLINT      NOT NULL DEFAULT 1,
    created_by      BIGINT        NOT NULL DEFAULT 0,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by      BIGINT        NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by      BIGINT        NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP     NULL
);

CREATE INDEX ix_sys_entity_label ON sys_entity_label (entity_id, entity_class, entity_property);

COMMENT ON TABLE sys_entity_label IS '实体多语言文本表';
COMMENT ON COLUMN sys_entity_label.id IS 'ID';
COMMENT ON COLUMN sys_entity_label.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_entity_label.lang_id IS '语言ID';
COMMENT ON COLUMN sys_entity_label.lang_code IS '语言编号';
COMMENT ON COLUMN sys_entity_label.entity_class IS '实体类名';
COMMENT ON COLUMN sys_entity_label.entity_property IS '实体属性';
COMMENT ON COLUMN sys_entity_label.entity_id IS '实体ID';
COMMENT ON COLUMN sys_entity_label.zh_cn_label IS '简体中文';
COMMENT ON COLUMN sys_entity_label.zh_tw_label IS '繁体中文';
COMMENT ON COLUMN sys_entity_label.en_label IS '英语';
COMMENT ON COLUMN sys_entity_label.ja_label IS '日语';
COMMENT ON COLUMN sys_entity_label.kr_label IS '韩语';
COMMENT ON COLUMN sys_entity_label.fr_label IS '法语';
COMMENT ON COLUMN sys_entity_label.version IS '版本号';
COMMENT ON COLUMN sys_entity_label.active IS '启用状态';
COMMENT ON COLUMN sys_entity_label.created_by IS '创建人';
COMMENT ON COLUMN sys_entity_label.created_at IS '创建时间';
COMMENT ON COLUMN sys_entity_label.updated_by IS '修改人';
COMMENT ON COLUMN sys_entity_label.updated_at IS '修改时间';
COMMENT ON COLUMN sys_entity_label.deleted_by IS '删除人';
COMMENT ON COLUMN sys_entity_label.deleted_at IS '删除时间';

--
-- 目录表
--

DROP TABLE IF EXISTS sys_catalog;

CREATE TABLE sys_catalog
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    biz_type    VARCHAR(100) NOT NULL DEFAULT '',
    biz_id      BIGINT       NOT NULL DEFAULT 0,
    code        VARCHAR(150) NOT NULL DEFAULT '',
    title       VARCHAR(255) NOT NULL DEFAULT '',
    root_ind    SMALLINT     NOT NULL DEFAULT 0,
    description VARCHAR(255) NOT NULL DEFAULT '',
    source      SMALLINT     NOT NULL DEFAULT 2,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_catalog__biz_type ON sys_catalog (biz_type, biz_id);
CREATE INDEX ix_sys_catalog__code ON sys_catalog (code);

COMMENT ON TABLE sys_catalog IS '目录表';
COMMENT ON COLUMN sys_catalog.id IS 'ID';
COMMENT ON COLUMN sys_catalog.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_catalog.biz_type IS '业务类型';
COMMENT ON COLUMN sys_catalog.biz_id IS '业务ID';
COMMENT ON COLUMN sys_catalog.code IS '编号';
COMMENT ON COLUMN sys_catalog.title IS '标题';
COMMENT ON COLUMN sys_catalog.root_ind IS '是否顶层';
COMMENT ON COLUMN sys_catalog.description IS '简介';
COMMENT ON COLUMN sys_catalog.source IS '数据来源';
COMMENT ON COLUMN sys_catalog.version IS '版本号';
COMMENT ON COLUMN sys_catalog.active IS '启用状态';
COMMENT ON COLUMN sys_catalog.created_by IS '创建人';
COMMENT ON COLUMN sys_catalog.created_at IS '创建时间';
COMMENT ON COLUMN sys_catalog.updated_by IS '修改人';
COMMENT ON COLUMN sys_catalog.updated_at IS '修改时间';
COMMENT ON COLUMN sys_catalog.deleted_by IS '删除人';
COMMENT ON COLUMN sys_catalog.deleted_at IS '删除时间';

--
-- 目录分类关联表
--

DROP TABLE IF EXISTS sys_catalog_relation;

CREATE TABLE sys_catalog_relation
(
    id            BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id     BIGINT        NOT NULL DEFAULT 0,
    biz_type      VARCHAR(100)  NOT NULL DEFAULT '',
    ancestor_id   BIGINT        NOT NULL DEFAULT 0,
    entity_id     BIGINT        NOT NULL DEFAULT 0,
    parent_ind    SMALLINT      NOT NULL DEFAULT 0,
    relation_path VARCHAR(2000) NOT NULL DEFAULT '',
    idx           VARCHAR(2000) NOT NULL DEFAULT '',
    version       BIGINT        NOT NULL DEFAULT 0,
    active        SMALLINT      NOT NULL DEFAULT 1,
    created_by    BIGINT        NOT NULL DEFAULT 0,
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_catalog_relation__tenant_id ON sys_catalog_relation (tenant_id);
CREATE INDEX ix_sys_catalog_relation__biz_type ON sys_catalog_relation (biz_type);
CREATE INDEX ix_sys_catalog_relation__ancestor_id ON sys_catalog_relation (ancestor_id);
CREATE INDEX ix_sys_catalog_relation__entity_id ON sys_catalog_relation (entity_id);

COMMENT ON TABLE sys_catalog_relation IS '目录分类关联表';
COMMENT ON COLUMN sys_catalog_relation.id IS 'ID';
COMMENT ON COLUMN sys_catalog_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_catalog_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_catalog_relation.ancestor_id IS '祖先ID';
COMMENT ON COLUMN sys_catalog_relation.entity_id IS '实体ID';
COMMENT ON COLUMN sys_catalog_relation.parent_ind IS '是否直接上级';
COMMENT ON COLUMN sys_catalog_relation.relation_path IS '关联路径';
COMMENT ON COLUMN sys_catalog_relation.idx IS '关联层级';
COMMENT ON COLUMN sys_catalog_relation.version IS '版本号';
COMMENT ON COLUMN sys_catalog_relation.active IS '启用状态';
COMMENT ON COLUMN sys_catalog_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_catalog_relation.created_at IS '创建时间';

--
-- 字典明细表
--

DROP TABLE IF EXISTS sys_dict;

CREATE TABLE sys_dict
(
    id           BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 0,
    reference_id BIGINT       NOT NULL DEFAULT 0,
    biz_type     VARCHAR(100) NOT NULL DEFAULT '',
    biz_id       BIGINT       NOT NULL DEFAULT 0,
    code         VARCHAR(100) NOT NULL DEFAULT '',
    title        VARCHAR(255) NOT NULL DEFAULT '',
    content      TEXT         NULL,
    extra        TEXT         NULL,
    idx          INT          NOT NULL DEFAULT 999,
    status       SMALLINT     NOT NULL DEFAULT 1,
    scope        SMALLINT     NOT NULL DEFAULT 1,
    source       SMALLINT     NOT NULL DEFAULT 2,
    version      BIGINT       NOT NULL DEFAULT 0,
    active       SMALLINT     NOT NULL DEFAULT 1,
    created_by   BIGINT       NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by   BIGINT       NOT NULL DEFAULT 0,
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by   BIGINT       NOT NULL DEFAULT 0,
    deleted_at   TIMESTAMP    NULL
);

CREATE INDEX ix_sys_dict__biz_type ON sys_dict (biz_type, biz_id);
CREATE INDEX ix_sys_dict__tenant_id ON sys_dict (tenant_id);

COMMENT ON TABLE sys_dict IS '字典明细表';
COMMENT ON COLUMN sys_dict.id IS 'ID';
COMMENT ON COLUMN sys_dict.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_dict.reference_id IS '参考ID';
COMMENT ON COLUMN sys_dict.biz_type IS '业务类型';
COMMENT ON COLUMN sys_dict.biz_id IS '业务ID';
COMMENT ON COLUMN sys_dict.code IS '编号';
COMMENT ON COLUMN sys_dict.title IS '文本';
COMMENT ON COLUMN sys_dict.content IS '内容';
COMMENT ON COLUMN sys_dict.extra IS '附加信息';
COMMENT ON COLUMN sys_dict.idx IS '序号';
COMMENT ON COLUMN sys_dict.status IS '发布状态';
COMMENT ON COLUMN sys_dict.scope IS '数据范围';
COMMENT ON COLUMN sys_dict.source IS '数据来源';
COMMENT ON COLUMN sys_dict.version IS '版本号';
COMMENT ON COLUMN sys_dict.active IS '启用状态';
COMMENT ON COLUMN sys_dict.created_by IS '创建人';
COMMENT ON COLUMN sys_dict.created_at IS '创建时间';
COMMENT ON COLUMN sys_dict.updated_by IS '修改人';
COMMENT ON COLUMN sys_dict.updated_at IS '修改时间';
COMMENT ON COLUMN sys_dict.deleted_by IS '删除人';
COMMENT ON COLUMN sys_dict.deleted_at IS '删除时间';

--
-- 字典关联表
--

DROP TABLE IF EXISTS sys_dict_relation;

CREATE TABLE sys_dict_relation
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    tenant_id  BIGINT      NOT NULL DEFAULT 0,
    biz_type   VARCHAR(50) NOT NULL DEFAULT '',
    biz_id     BIGINT      NOT NULL DEFAULT 0,
    dict_id    BIGINT      NOT NULL DEFAULT 0,
    version    BIGINT      NOT NULL DEFAULT 0,
    active     SMALLINT    NOT NULL DEFAULT 1,
    created_by BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_dict_relation__tenant_id ON sys_dict_relation (tenant_id);
CREATE INDEX ix_sys_dict_relation__dict_id ON sys_dict_relation (dict_id);
CREATE INDEX ix_sys_dict_relation__biz_type ON sys_dict_relation (biz_type, biz_id);

COMMENT ON TABLE sys_dict_relation IS '字典关联表';
COMMENT ON COLUMN sys_dict_relation.id IS 'ID';
COMMENT ON COLUMN sys_dict_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_dict_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_dict_relation.biz_id IS '业务ID';
COMMENT ON COLUMN sys_dict_relation.dict_id IS '字典ID';
COMMENT ON COLUMN sys_dict_relation.version IS '版本号';
COMMENT ON COLUMN sys_dict_relation.active IS '启用状态';
COMMENT ON COLUMN sys_dict_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_dict_relation.created_at IS '创建时间';

--
-- 字典排序表
--

DROP TABLE IF EXISTS sys_dict_sequence;

CREATE TABLE sys_dict_sequence
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    tenant_id  BIGINT      NOT NULL DEFAULT 0,
    biz_type   VARCHAR(50) NOT NULL DEFAULT '',
    biz_id     BIGINT      NOT NULL DEFAULT 0,
    dict_id    BIGINT      NOT NULL DEFAULT 0,
    idx        INT         NOT NULL DEFAULT 999,
    version    BIGINT      NOT NULL DEFAULT 0,
    active     SMALLINT    NOT NULL DEFAULT 1,
    created_by BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_by BIGINT      NOT NULL DEFAULT 0,
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    deleted_by BIGINT      NOT NULL DEFAULT 0,
    deleted_at TIMESTAMP   NULL
);

CREATE INDEX ix_sys_dict_sequence__tenant_id ON sys_dict_sequence (tenant_id);
CREATE INDEX ix_sys_dict_sequence__dict_id ON sys_dict_sequence (dict_id);
CREATE INDEX ix_sys_dict_sequence__biz_type ON sys_dict_sequence (biz_type, biz_id);

COMMENT ON TABLE sys_dict_sequence IS '字典排序表';
COMMENT ON COLUMN sys_dict_sequence.id IS 'ID';
COMMENT ON COLUMN sys_dict_sequence.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_dict_sequence.biz_type IS '业务类型';
COMMENT ON COLUMN sys_dict_sequence.biz_id IS '业务ID';
COMMENT ON COLUMN sys_dict_sequence.dict_id IS '字典ID';
COMMENT ON COLUMN sys_dict_sequence.idx IS '序号';
COMMENT ON COLUMN sys_dict_sequence.version IS '版本号';
COMMENT ON COLUMN sys_dict_sequence.active IS '启用状态';
COMMENT ON COLUMN sys_dict_sequence.created_by IS '创建人';
COMMENT ON COLUMN sys_dict_sequence.created_at IS '创建时间';
COMMENT ON COLUMN sys_dict_sequence.updated_by IS '修改人';
COMMENT ON COLUMN sys_dict_sequence.updated_at IS '修改时间';
COMMENT ON COLUMN sys_dict_sequence.deleted_by IS '删除人';
COMMENT ON COLUMN sys_dict_sequence.deleted_at IS '删除时间';

--
-- 标签表
--

DROP TABLE IF EXISTS sys_tag;

CREATE TABLE sys_tag
(
    id           BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id    BIGINT       NOT NULL DEFAULT 0,
    reference_id BIGINT       NOT NULL DEFAULT 0,
    biz_type     VARCHAR(50)  NOT NULL DEFAULT '',
    biz_id       BIGINT       NOT NULL DEFAULT 0,
    title        VARCHAR(150) NOT NULL DEFAULT '',
    content      TEXT         NULL,
    extra        TEXT         NULL,
    description  VARCHAR(255) NOT NULL DEFAULT '',
    idx          INT          NOT NULL DEFAULT 999,
    scope        SMALLINT     NOT NULL DEFAULT 1,
    status       SMALLINT     NOT NULL DEFAULT 1,
    source       SMALLINT     NOT NULL DEFAULT 2,
    version      BIGINT       NOT NULL DEFAULT 0,
    active       SMALLINT     NOT NULL DEFAULT 1,
    created_by   BIGINT       NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by   BIGINT       NOT NULL DEFAULT 0,
    updated_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by   BIGINT       NOT NULL DEFAULT 0,
    deleted_at   TIMESTAMP    NULL
);

CREATE INDEX ix_sys_tag__tenant_id ON sys_tag (tenant_id);
CREATE INDEX ix_sys_tag__biz_type ON sys_tag (biz_type, biz_id);

COMMENT ON TABLE sys_tag IS '标签表';
COMMENT ON COLUMN sys_tag.id IS 'ID';
COMMENT ON COLUMN sys_tag.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_tag.reference_id IS '参考ID';
COMMENT ON COLUMN sys_tag.biz_type IS '业务类型';
COMMENT ON COLUMN sys_tag.biz_id IS '业务ID';
COMMENT ON COLUMN sys_tag.title IS '标题';
COMMENT ON COLUMN sys_tag.description IS '备注';
COMMENT ON COLUMN sys_tag.content IS '内容';
COMMENT ON COLUMN sys_tag.extra IS '附加信息';
COMMENT ON COLUMN sys_tag.idx IS '序号';
COMMENT ON COLUMN sys_tag.scope IS '数据范围';
COMMENT ON COLUMN sys_tag.status IS '发布状态';
COMMENT ON COLUMN sys_tag.source IS '数据来源';
COMMENT ON COLUMN sys_tag.version IS '版本号';
COMMENT ON COLUMN sys_tag.active IS '启用状态';
COMMENT ON COLUMN sys_tag.created_by IS '创建人';
COMMENT ON COLUMN sys_tag.created_at IS '创建时间';
COMMENT ON COLUMN sys_tag.updated_by IS '修改人';
COMMENT ON COLUMN sys_tag.updated_at IS '修改时间';
COMMENT ON COLUMN sys_tag.deleted_by IS '删除人';
COMMENT ON COLUMN sys_tag.deleted_at IS '删除时间';

--
-- 标签关联表
--

DROP TABLE IF EXISTS sys_tag_relation;

CREATE TABLE sys_tag_relation
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    tenant_id  BIGINT      NOT NULL DEFAULT 0,
    biz_type   VARCHAR(50) NOT NULL DEFAULT '',
    biz_id     BIGINT      NOT NULL DEFAULT 0,
    tag_id     BIGINT      NOT NULL DEFAULT 0,
    version    BIGINT      NOT NULL DEFAULT 0,
    active     SMALLINT    NOT NULL DEFAULT 1,
    created_by BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_tag_relation__tenant_id ON sys_tag_relation (tenant_id);
CREATE INDEX ix_sys_tag_relation__tag_id ON sys_tag_relation (tag_id);
CREATE INDEX ix_sys_tag_relation__biz_type ON sys_tag_relation (biz_type, biz_id);

COMMENT ON TABLE sys_tag_relation IS '标签关联表';
COMMENT ON COLUMN sys_tag_relation.id IS 'ID';
COMMENT ON COLUMN sys_tag_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_tag_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_tag_relation.biz_id IS '业务ID';
COMMENT ON COLUMN sys_tag_relation.tag_id IS '标签ID';
COMMENT ON COLUMN sys_tag_relation.version IS '版本号';
COMMENT ON COLUMN sys_tag_relation.active IS '启用状态';
COMMENT ON COLUMN sys_tag_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_tag_relation.created_at IS '创建时间';

--
-- 标签排序表
--

DROP TABLE IF EXISTS sys_tag_sequence;

CREATE TABLE sys_tag_sequence
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    tenant_id  BIGINT      NOT NULL DEFAULT 0,
    biz_type   VARCHAR(50) NOT NULL DEFAULT '',
    biz_id     BIGINT      NOT NULL DEFAULT 0,
    tag_id     BIGINT      NOT NULL DEFAULT 0,
    idx        INT         NOT NULL DEFAULT 999,
    version    BIGINT      NOT NULL DEFAULT 0,
    active     SMALLINT    NOT NULL DEFAULT 1,
    created_by BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_by BIGINT      NOT NULL DEFAULT 0,
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    deleted_by BIGINT      NOT NULL DEFAULT 0,
    deleted_at TIMESTAMP   NULL
);

CREATE INDEX ix_sys_tag_sequence__tenant_id ON sys_tag_sequence (tenant_id);
CREATE INDEX ix_sys_tag_sequence__tag_id ON sys_tag_sequence (tag_id);
CREATE INDEX ix_sys_tag_sequence__biz_type ON sys_tag_sequence (biz_type, biz_id);

COMMENT ON TABLE sys_tag_sequence IS '标签关联表';
COMMENT ON COLUMN sys_tag_sequence.id IS 'ID';
COMMENT ON COLUMN sys_tag_sequence.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_tag_sequence.biz_type IS '业务类型';
COMMENT ON COLUMN sys_tag_sequence.biz_id IS '业务ID';
COMMENT ON COLUMN sys_tag_sequence.tag_id IS '标签ID';
COMMENT ON COLUMN sys_tag_sequence.idx IS '序号';
COMMENT ON COLUMN sys_tag_sequence.version IS '版本号';
COMMENT ON COLUMN sys_tag_sequence.active IS '启用状态';
COMMENT ON COLUMN sys_tag_sequence.created_by IS '创建人';
COMMENT ON COLUMN sys_tag_sequence.created_at IS '创建时间';
COMMENT ON COLUMN sys_tag_sequence.updated_by IS '修改人';
COMMENT ON COLUMN sys_tag_sequence.updated_at IS '修改时间';
COMMENT ON COLUMN sys_tag_sequence.deleted_by IS '删除人';
COMMENT ON COLUMN sys_tag_sequence.deleted_at IS '删除时间';

--
-- 附件表
--

DROP TABLE IF EXISTS sys_attachment;

CREATE TABLE sys_attachment
(
    id                BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id         BIGINT        NOT NULL DEFAULT 0,
    biz_type          VARCHAR(50)   NOT NULL DEFAULT '',
    content_type      VARCHAR(255)  NOT NULL DEFAULT '',
    storage_type      VARCHAR(100)  NOT NULL DEFAULT '',
    access_type       VARCHAR(100)  NOT NULL DEFAULT '',
    original_filename VARCHAR(255)  NOT NULL DEFAULT '',
    filename          VARCHAR(255)  NOT NULL DEFAULT '',
    file_key          VARCHAR(2000) NOT NULL DEFAULT '',
    size              BIGINT        NOT NULL DEFAULT 0,
    url               VARCHAR(2000) NOT NULL DEFAULT '',
    extra             TEXT          NULL,
    version           BIGINT        NOT NULL DEFAULT 0,
    active            SMALLINT      NOT NULL DEFAULT 1,
    created_by        BIGINT        NOT NULL DEFAULT 0,
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by        BIGINT        NOT NULL DEFAULT 0,
    updated_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by        BIGINT        NOT NULL DEFAULT 0,
    deleted_at        TIMESTAMP     NULL
);

CREATE INDEX ix_sys_attachment__tenant_id ON sys_attachment (tenant_id);
CREATE INDEX ix_sys_attachment__biz_type ON sys_attachment (biz_type);

COMMENT ON TABLE sys_attachment IS '附件表';
COMMENT ON COLUMN sys_attachment.id IS 'ID';
COMMENT ON COLUMN sys_attachment.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_attachment.biz_type IS '业务类型';
COMMENT ON COLUMN sys_attachment.content_type IS '文件类型';
COMMENT ON COLUMN sys_attachment.storage_type IS '存储类型';
COMMENT ON COLUMN sys_attachment.access_type IS '文件访问类型';
COMMENT ON COLUMN sys_attachment.original_filename IS '原始文件名';
COMMENT ON COLUMN sys_attachment.filename IS '文件名';
COMMENT ON COLUMN sys_attachment.file_key IS '文件标识';
COMMENT ON COLUMN sys_attachment.size IS '文件大小';
COMMENT ON COLUMN sys_attachment.url IS '文件链接';
COMMENT ON COLUMN sys_attachment.extra IS '附加信息';
COMMENT ON COLUMN sys_attachment.version IS '版本号';
COMMENT ON COLUMN sys_attachment.active IS '启用状态';
COMMENT ON COLUMN sys_attachment.created_by IS '创建人';
COMMENT ON COLUMN sys_attachment.created_at IS '创建时间';
COMMENT ON COLUMN sys_attachment.updated_by IS '修改人';
COMMENT ON COLUMN sys_attachment.updated_at IS '修改时间';
COMMENT ON COLUMN sys_attachment.deleted_by IS '删除人';
COMMENT ON COLUMN sys_attachment.deleted_at IS '删除时间';

--
-- 附件关联表
--

DROP TABLE IF EXISTS sys_attachment_relation;

CREATE TABLE sys_attachment_relation
(
    id            BIGSERIAL   NOT NULL PRIMARY KEY,
    tenant_id     BIGINT      NOT NULL DEFAULT 0,
    attachment_id BIGINT      NOT NULL DEFAULT 0,
    biz_type      VARCHAR(50) NOT NULL DEFAULT '',
    biz_id        BIGINT      NOT NULL DEFAULT 0,
    version       BIGINT      NOT NULL DEFAULT 0,
    active        SMALLINT    NOT NULL DEFAULT 1,
    created_by    BIGINT      NOT NULL DEFAULT 0,
    created_at    TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_attachment_relation__tenant_id ON sys_attachment_relation (tenant_id);
CREATE INDEX ix_sys_attachment_relation__attachment_id ON sys_attachment_relation (attachment_id);
CREATE INDEX ix_sys_attachment_relation__biz_type ON sys_attachment_relation (biz_type, biz_id);

COMMENT ON TABLE sys_attachment_relation IS '附件关联表';
COMMENT ON COLUMN sys_attachment_relation.id IS 'ID';
COMMENT ON COLUMN sys_attachment_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_attachment_relation.attachment_id IS '附件ID';
COMMENT ON COLUMN sys_attachment_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_attachment_relation.biz_id IS '业务ID';
COMMENT ON COLUMN sys_attachment_relation.version IS '版本号';
COMMENT ON COLUMN sys_attachment_relation.active IS '启用状态';
COMMENT ON COLUMN sys_attachment_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_attachment_relation.created_at IS '创建时间';

--
-- 链接日志
--

DROP TABLE IF EXISTS sys_url_log;

CREATE TABLE sys_url_log
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    path       VARCHAR(250) NULL,
    start_time TIMESTAMP    NULL,
    end_time   TIMESTAMP    NULL,
    exec_time  BIGINT       NULL,
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_url_log__path ON sys_url_log (path);

COMMENT ON TABLE sys_url_log IS '链接日志';
COMMENT ON COLUMN sys_url_log.id IS 'ID';
COMMENT ON COLUMN sys_url_log.path IS '路径';
COMMENT ON COLUMN sys_url_log.start_time IS '开始时间';
COMMENT ON COLUMN sys_url_log.end_time IS '结束时间';
COMMENT ON COLUMN sys_url_log.exec_time IS '执行时长';
COMMENT ON COLUMN sys_url_log.version IS '版本号';
COMMENT ON COLUMN sys_url_log.active IS '启用状态';
COMMENT ON COLUMN sys_url_log.created_by IS '创建人';
COMMENT ON COLUMN sys_url_log.created_at IS '创建时间';

--
-- 登录日志表
--

DROP TABLE IF EXISTS sys_login_log;

CREATE TABLE sys_login_log
(
    id             BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id      BIGINT        NOT NULL DEFAULT 0,
    biz_type       VARCHAR(50)   NOT NULL DEFAULT '',
    biz_id         BIGINT        NOT NULL DEFAULT 0,
    host           VARCHAR(150)  NOT NULL DEFAULT '',
    user_agent     VARCHAR(2000) NOT NULL DEFAULT '',
    client_id      VARCHAR(150)  NOT NULL DEFAULT '',
    client_name    VARCHAR(150)  NOT NULL DEFAULT '',
    client_version VARCHAR(150)  NOT NULL DEFAULT '',
    details        VARCHAR(250)  NULL,
    exception      TEXT          NULL,
    success        SMALLINT      NOT NULL DEFAULT 1,
    version        BIGINT        NOT NULL DEFAULT 0,
    active         SMALLINT      NOT NULL DEFAULT 1,
    created_by     BIGINT        NOT NULL DEFAULT 0,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_login_log__tenant_id ON sys_login_log (tenant_id);
CREATE INDEX ix_sys_login_log__biz_type ON sys_login_log (biz_type, biz_id);

COMMENT ON TABLE sys_login_log IS '登录日志表';
COMMENT ON COLUMN sys_login_log.id IS 'ID';
COMMENT ON COLUMN sys_login_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_login_log.biz_type IS '业务类型';
COMMENT ON COLUMN sys_login_log.biz_id IS '业务ID';
COMMENT ON COLUMN sys_login_log.host IS '用户登录主机';
COMMENT ON COLUMN sys_login_log.user_agent IS 'User Agent';
COMMENT ON COLUMN sys_login_log.client_id IS '客户端编号';
COMMENT ON COLUMN sys_login_log.client_name IS '客户端名称';
COMMENT ON COLUMN sys_login_log.client_version IS '客户端版本';
COMMENT ON COLUMN sys_login_log.details IS '详情';
COMMENT ON COLUMN sys_login_log.exception IS '异常';
COMMENT ON COLUMN sys_login_log.success IS '登录状态';
COMMENT ON COLUMN sys_login_log.version IS '版本号';
COMMENT ON COLUMN sys_login_log.active IS '启用状态';
COMMENT ON COLUMN sys_login_log.created_by IS '创建人';
COMMENT ON COLUMN sys_login_log.created_at IS '创建时间';

--
-- 操作日志表
--

DROP TABLE IF EXISTS sys_operation_log;

CREATE TABLE sys_operation_log
(
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    biz_type        VARCHAR(50)  NOT NULL DEFAULT '',
    biz_id          BIGINT       NOT NULL DEFAULT 0,
    class_name      VARCHAR(250) NOT NULL DEFAULT '',
    method_name     VARCHAR(250) NOT NULL DEFAULT '',
    request_id      VARCHAR(250) NOT NULL DEFAULT '',
    request_ip      VARCHAR(250) NOT NULL DEFAULT '',
    request_method  VARCHAR(250) NOT NULL DEFAULT '',
    request_ua      TEXT         NULL,
    request_uri     TEXT         NULL,
    request_params  TEXT         NULL,
    request_headers TEXT         NULL,
    start_time      TIMESTAMP    NULL,
    end_time        TIMESTAMP    NULL,
    exec_time       BIGINT       NOT NULL DEFAULT 0,
    details         VARCHAR(250) NULL,
    exception       TEXT         NULL,
    version         BIGINT       NOT NULL DEFAULT 0,
    active          SMALLINT     NOT NULL DEFAULT 1,
    created_by      BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX ix_sys_operation_log__tenant_id ON sys_operation_log (tenant_id);
CREATE INDEX ix_sys_operation_log__biz_id ON sys_operation_log (biz_type, biz_id);

COMMENT ON TABLE sys_operation_log IS '操作日志表';
COMMENT ON COLUMN sys_operation_log.id IS 'ID';
COMMENT ON COLUMN sys_operation_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_operation_log.biz_type IS '业务类型';
COMMENT ON COLUMN sys_operation_log.biz_id IS '业务ID';
COMMENT ON COLUMN sys_operation_log.class_name IS '类名';
COMMENT ON COLUMN sys_operation_log.method_name IS '方法名';
COMMENT ON COLUMN sys_operation_log.request_id IS '请求ID';
COMMENT ON COLUMN sys_operation_log.request_ip IS '请求IP';
COMMENT ON COLUMN sys_operation_log.request_method IS '请求类型';
COMMENT ON COLUMN sys_operation_log.request_ua IS '请求UA';
COMMENT ON COLUMN sys_operation_log.request_uri IS '请求地址';
COMMENT ON COLUMN sys_operation_log.request_params IS '请求参数';
COMMENT ON COLUMN sys_operation_log.request_headers IS '请求头';
COMMENT ON COLUMN sys_operation_log.start_time IS '开始时间';
COMMENT ON COLUMN sys_operation_log.end_time IS '结束时间';
COMMENT ON COLUMN sys_operation_log.exec_time IS '执行时长';
COMMENT ON COLUMN sys_operation_log.details IS '详情';
COMMENT ON COLUMN sys_operation_log.exception IS '异常';
COMMENT ON COLUMN sys_operation_log.version IS '版本号';
COMMENT ON COLUMN sys_operation_log.active IS '启用状态';
COMMENT ON COLUMN sys_operation_log.created_by IS '创建人';
COMMENT ON COLUMN sys_operation_log.created_at IS '创建时间';

--
-- 系统日志表
--

DROP TABLE IF EXISTS sys_application_log;

CREATE TABLE sys_application_log
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    class_name  VARCHAR(250) NULL,
    method_name VARCHAR(250) NULL,
    request_id  VARCHAR(250) NULL,
    details     VARCHAR(250) NULL,
    exception   TEXT         NULL,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_application_log__tenant_id ON sys_application_log (tenant_id);

COMMENT ON TABLE sys_application_log IS '系统日志表';
COMMENT ON COLUMN sys_application_log.id IS 'ID';
COMMENT ON COLUMN sys_application_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_application_log.class_name IS '类名';
COMMENT ON COLUMN sys_application_log.method_name IS '方法名';
COMMENT ON COLUMN sys_application_log.request_id IS '请求ID';
COMMENT ON COLUMN sys_application_log.details IS '详情';
COMMENT ON COLUMN sys_application_log.exception IS '异常';
COMMENT ON COLUMN sys_application_log.version IS '版本号';
COMMENT ON COLUMN sys_application_log.active IS '启用状态';
COMMENT ON COLUMN sys_application_log.created_by IS '创建人';
COMMENT ON COLUMN sys_application_log.created_at IS '创建时间';

--
-- 验证码发送日志表
--

DROP TABLE IF EXISTS sys_captcha_log;

CREATE TABLE sys_captcha_log
(
    id                  BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    captcha_type        VARCHAR(50)  NOT NULL DEFAULT '',
    captcha_key         VARCHAR(150) NOT NULL DEFAULT '',
    captcha_value       VARCHAR(150) NOT NULL DEFAULT '',
    email               VARCHAR(150) NOT NULL DEFAULT '',
    mobile_country_code VARCHAR(150) NOT NULL DEFAULT '',
    mobile_number       VARCHAR(150) NOT NULL DEFAULT '',
    version             BIGINT       NOT NULL DEFAULT 0,
    active              SMALLINT     NOT NULL DEFAULT 1,
    created_by          BIGINT       NOT NULL DEFAULT 0,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_captcha_log__tenant_id ON sys_captcha_log (tenant_id);

COMMENT ON TABLE sys_captcha_log IS '验证码发送日志表';
COMMENT ON COLUMN sys_captcha_log.id IS 'ID';
COMMENT ON COLUMN sys_captcha_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_captcha_log.captcha_type IS '验证码类型';
COMMENT ON COLUMN sys_captcha_log.captcha_key IS '验证码标识';
COMMENT ON COLUMN sys_captcha_log.captcha_value IS '验证码';
COMMENT ON COLUMN sys_captcha_log.email IS '电子邮箱';
COMMENT ON COLUMN sys_captcha_log.mobile_country_code IS '手机区位码';
COMMENT ON COLUMN sys_captcha_log.mobile_number IS '手机号码';
COMMENT ON COLUMN sys_captcha_log.version IS '版本号';
COMMENT ON COLUMN sys_captcha_log.active IS '启用状态';
COMMENT ON COLUMN sys_captcha_log.created_by IS '创建人';
COMMENT ON COLUMN sys_captcha_log.created_at IS '创建时间';

--
-- 用户搜索日志表
--

DROP TABLE IF EXISTS sys_search_log;

CREATE TABLE sys_search_log
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL DEFAULT 0,
    user_id    BIGINT       NOT NULL DEFAULT 0,
    search_key VARCHAR(150) NOT NULL DEFAULT '',
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_search_log__tenant_id ON sys_search_log (tenant_id);
CREATE INDEX ix_sys_search_log__user_id ON sys_search_log (user_id);

COMMENT ON TABLE sys_search_log IS '用户搜索日志表';
COMMENT ON COLUMN sys_search_log.id IS 'ID';
COMMENT ON COLUMN sys_search_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_search_log.user_id IS '用户ID';
COMMENT ON COLUMN sys_search_log.search_key IS '搜索关键字';
COMMENT ON COLUMN sys_search_log.version IS '版本号';
COMMENT ON COLUMN sys_search_log.active IS '启用状态';
COMMENT ON COLUMN sys_search_log.created_by IS '创建人';
COMMENT ON COLUMN sys_search_log.created_at IS '创建时间';

--
-- 消息通道表
--

DROP TABLE IF EXISTS sys_message_channel;

CREATE TABLE sys_message_channel
(
    id            BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    code          VARCHAR(150) NOT NULL DEFAULT '',
    label         VARCHAR(150) NOT NULL DEFAULT '',
    title         VARCHAR(255) NOT NULL DEFAULT '',
    template_type VARCHAR(50)  NOT NULL DEFAULT '',
    description   TEXT         NULL,
    status        SMALLINT     NOT NULL DEFAULT 1,
    version       BIGINT       NOT NULL DEFAULT 0,
    active        SMALLINT     NOT NULL DEFAULT 1,
    created_by    BIGINT       NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by    BIGINT       NOT NULL DEFAULT 0,
    updated_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by    BIGINT       NOT NULL DEFAULT 0,
    deleted_at    TIMESTAMP    NULL
);

COMMENT ON TABLE sys_message_channel IS '消息通道表';
COMMENT ON COLUMN sys_message_channel.id IS 'ID';
COMMENT ON COLUMN sys_message_channel.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message_channel.code IS '编号';
COMMENT ON COLUMN sys_message_channel.label IS '多语言文本';
COMMENT ON COLUMN sys_message_channel.title IS '标题';
COMMENT ON COLUMN sys_message_channel.template_type IS '模板类型';
COMMENT ON COLUMN sys_message_channel.description IS '备注';
COMMENT ON COLUMN sys_message_channel.status IS '发布状态';
COMMENT ON COLUMN sys_message_channel.version IS '版本号';
COMMENT ON COLUMN sys_message_channel.active IS '启用状态';
COMMENT ON COLUMN sys_message_channel.created_by IS '创建人';
COMMENT ON COLUMN sys_message_channel.created_at IS '创建时间';
COMMENT ON COLUMN sys_message_channel.updated_by IS '修改人';
COMMENT ON COLUMN sys_message_channel.updated_at IS '修改时间';
COMMENT ON COLUMN sys_message_channel.deleted_by IS '删除人';
COMMENT ON COLUMN sys_message_channel.deleted_at IS '删除时间';

--
-- 消息类型表
--

DROP TABLE IF EXISTS sys_message_type;

CREATE TABLE sys_message_type
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    code        VARCHAR(150) NOT NULL DEFAULT '',
    label       VARCHAR(150) NOT NULL DEFAULT '',
    title       VARCHAR(255) NOT NULL DEFAULT '',
    description TEXT         NULL,
    status      SMALLINT     NOT NULL DEFAULT 1,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

COMMENT ON TABLE sys_message_type IS '消息类型表';
COMMENT ON COLUMN sys_message_type.id IS 'ID';
COMMENT ON COLUMN sys_message_type.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message_type.code IS '编号';
COMMENT ON COLUMN sys_message_type.label IS '多语言文本';
COMMENT ON COLUMN sys_message_type.title IS '标题';
COMMENT ON COLUMN sys_message_type.description IS '备注';
COMMENT ON COLUMN sys_message_type.status IS '发布状态';
COMMENT ON COLUMN sys_message_type.version IS '版本号';
COMMENT ON COLUMN sys_message_type.active IS '启用状态';
COMMENT ON COLUMN sys_message_type.created_by IS '创建人';
COMMENT ON COLUMN sys_message_type.created_at IS '创建时间';
COMMENT ON COLUMN sys_message_type.updated_by IS '修改人';
COMMENT ON COLUMN sys_message_type.updated_at IS '修改时间';
COMMENT ON COLUMN sys_message_type.deleted_by IS '删除人';
COMMENT ON COLUMN sys_message_type.deleted_at IS '删除时间';

--
-- 消息模板表
--

DROP TABLE IF EXISTS sys_message_template;

CREATE TABLE sys_message_template
(
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    message_channel VARCHAR(100) NOT NULL DEFAULT '',
    message_type    VARCHAR(100) NOT NULL DEFAULT '',
    content         TEXT,
    status          SMALLINT     NOT NULL DEFAULT 1,
    version         BIGINT       NOT NULL DEFAULT 0,
    active          SMALLINT     NOT NULL DEFAULT 1,
    created_by      BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      BIGINT       NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by      BIGINT       NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP    NULL
);

COMMENT ON TABLE sys_message_template IS '消息模板表';
COMMENT ON COLUMN sys_message_template.id IS 'ID';
COMMENT ON COLUMN sys_message_template.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message_template.message_channel IS '消息通道';
COMMENT ON COLUMN sys_message_template.message_type IS '消息类型';
COMMENT ON COLUMN sys_message_template.content IS '模板';
COMMENT ON COLUMN sys_message_template.status IS '发布状态';
COMMENT ON COLUMN sys_message_template.version IS '版本号';
COMMENT ON COLUMN sys_message_template.active IS '启用状态';
COMMENT ON COLUMN sys_message_template.created_by IS '创建人';
COMMENT ON COLUMN sys_message_template.created_at IS '创建时间';
COMMENT ON COLUMN sys_message_template.updated_by IS '修改人';
COMMENT ON COLUMN sys_message_template.updated_at IS '修改时间';
COMMENT ON COLUMN sys_message_template.deleted_by IS '删除人';
COMMENT ON COLUMN sys_message_template.deleted_at IS '删除时间';

--
-- 消息表
--

DROP TABLE IF EXISTS sys_message;

CREATE TABLE sys_message
(
    id                   BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id            BIGINT       NOT NULL DEFAULT 0,
    message_type         VARCHAR(100) NOT NULL DEFAULT '',
    subject              VARCHAR(255) NULL,
    content              TEXT         NULL,
    data                 TEXT         NULL,
    url                  VARCHAR(255) NOT NULL DEFAULT '',
    target_sent_datetime TIMESTAMP    NULL,
    sent_datetime        TIMESTAMP    NULL,
    attempt              SMALLINT     NULL,
    status               SMALLINT     NOT NULL DEFAULT 1,
    version              BIGINT       NOT NULL DEFAULT 0,
    active               SMALLINT     NOT NULL DEFAULT 1,
    created_by           BIGINT       NOT NULL DEFAULT 0,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           BIGINT       NOT NULL DEFAULT 0,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by           BIGINT       NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP    NULL
);

CREATE INDEX ix_sys_message__tenant_id ON sys_message (tenant_id);
CREATE INDEX ix_sys_message__message_type ON sys_message (message_type);

COMMENT ON TABLE sys_message IS '消息表';
COMMENT ON COLUMN sys_message.id IS 'ID';
COMMENT ON COLUMN sys_message.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message.message_type IS '消息类型';
COMMENT ON COLUMN sys_message.subject IS '标题';
COMMENT ON COLUMN sys_message.content IS '内容';
COMMENT ON COLUMN sys_message.data IS '数据，一般保存JSON格式的参数';
COMMENT ON COLUMN sys_message.url IS '链接';
COMMENT ON COLUMN sys_message.target_sent_datetime IS '目标发送时间';
COMMENT ON COLUMN sys_message.sent_datetime IS '发送时间';
COMMENT ON COLUMN sys_message.attempt IS '尝试发送次数';
COMMENT ON COLUMN sys_message.status IS '发布状态';
COMMENT ON COLUMN sys_message.version IS '版本号';
COMMENT ON COLUMN sys_message.active IS '启用状态';
COMMENT ON COLUMN sys_message.created_by IS '创建人';
COMMENT ON COLUMN sys_message.created_at IS '创建时间';
COMMENT ON COLUMN sys_message.updated_by IS '修改人';
COMMENT ON COLUMN sys_message.updated_at IS '修改时间';
COMMENT ON COLUMN sys_message.deleted_by IS '删除人';
COMMENT ON COLUMN sys_message.deleted_at IS '删除时间';

--
-- 消息用户表
--

DROP TABLE IF EXISTS sys_message_user;

CREATE TABLE sys_message_user
(
    id                  BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    biz_type            VARCHAR(100) NOT NULL DEFAULT '',
    message_id          BIGINT       NOT NULL DEFAULT 0,
    user_type           SMALLINT     NOT NULL DEFAULT 0,
    user_id             BIGINT       NOT NULL DEFAULT 0,
    display_name        VARCHAR(255),
    username            VARCHAR(255),
    email               VARCHAR(255),
    mobile_country_code VARCHAR(150) NOT NULL DEFAULT '',
    mobile_number       VARCHAR(150) NOT NULL DEFAULT '',
    version             BIGINT       NOT NULL DEFAULT 0,
    active              SMALLINT     NOT NULL DEFAULT 1,
    created_by          BIGINT       NOT NULL DEFAULT 0,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by          BIGINT       NOT NULL DEFAULT 0,
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by          BIGINT       NOT NULL DEFAULT 0,
    deleted_at          TIMESTAMP    NULL
);

CREATE INDEX ix_sys_message_user__tenant_id ON sys_message_user (tenant_id);
CREATE INDEX ix_sys_message_user__message_id ON sys_message_user (message_id);
CREATE INDEX ix_sys_message_user__biz_type ON sys_message_user (biz_type);
CREATE INDEX ix_sys_message_user__user_type ON sys_message_user (user_type, user_id);

COMMENT ON TABLE sys_message_user IS '消息用户表';
COMMENT ON COLUMN sys_message_user.id IS 'ID';
COMMENT ON COLUMN sys_message_user.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message_user.biz_type IS '业务类型';
COMMENT ON COLUMN sys_message_user.message_id IS '消息ID';
COMMENT ON COLUMN sys_message_user.user_type IS '实体类型';
COMMENT ON COLUMN sys_message_user.user_id IS '实体ID';
COMMENT ON COLUMN sys_message_user.display_name IS '姓名';
COMMENT ON COLUMN sys_message_user.username IS '账号';
COMMENT ON COLUMN sys_message_user.email IS '邮箱';
COMMENT ON COLUMN sys_message_user.mobile_country_code IS '手机区位码';
COMMENT ON COLUMN sys_message_user.mobile_number IS '手机号码';
COMMENT ON COLUMN sys_message_user.version IS '版本号';
COMMENT ON COLUMN sys_message_user.active IS '启用状态';
COMMENT ON COLUMN sys_message_user.created_by IS '创建人';
COMMENT ON COLUMN sys_message_user.created_at IS '创建时间';
COMMENT ON COLUMN sys_message_user.updated_by IS '修改人';
COMMENT ON COLUMN sys_message_user.updated_at IS '修改时间';
COMMENT ON COLUMN sys_message_user.deleted_by IS '删除人';
COMMENT ON COLUMN sys_message_user.deleted_at IS '删除时间';

--
-- 消息内容表
--

DROP TABLE IF EXISTS sys_message_content;

CREATE TABLE sys_message_content
(
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    message_channel VARCHAR(100) NOT NULL DEFAULT '',
    message_type    VARCHAR(100) NOT NULL DEFAULT '',
    message_id      BIGINT       NOT NULL DEFAULT 0,
    content         TEXT         NULL,
    resp            TEXT         NULL,
    exception       TEXT         NULL,
    sent_datetime   TIMESTAMP    NULL,
    status          SMALLINT     NOT NULL DEFAULT 1,
    version         BIGINT       NOT NULL DEFAULT 0,
    active          SMALLINT     NOT NULL DEFAULT 1,
    created_by      BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      BIGINT       NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by      BIGINT       NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP    NULL
);

CREATE INDEX ix_sys_message_content__tenant_id ON sys_message_content (tenant_id);
CREATE INDEX ix_sys_message_content__message_id ON sys_message_content (message_id);

COMMENT ON TABLE sys_message_content IS '消息内容表';
COMMENT ON COLUMN sys_message_content.id IS 'ID';
COMMENT ON COLUMN sys_message_content.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message_content.message_channel IS '消息通道';
COMMENT ON COLUMN sys_message_content.message_type IS '消息类型';
COMMENT ON COLUMN sys_message_content.message_id IS '消息ID';
COMMENT ON COLUMN sys_message_content.content IS '消息内容';
COMMENT ON COLUMN sys_message_content.resp IS '响应内容';
COMMENT ON COLUMN sys_message_content.exception IS '异常内容';
COMMENT ON COLUMN sys_message_content.sent_datetime IS '发送时间';
COMMENT ON COLUMN sys_message_content.status IS '发布状态';
COMMENT ON COLUMN sys_message_content.version IS '版本号';
COMMENT ON COLUMN sys_message_content.active IS '启用状态';
COMMENT ON COLUMN sys_message_content.created_by IS '创建人';
COMMENT ON COLUMN sys_message_content.created_at IS '创建时间';
COMMENT ON COLUMN sys_message_content.updated_by IS '修改人';
COMMENT ON COLUMN sys_message_content.updated_at IS '修改时间';
COMMENT ON COLUMN sys_message_content.deleted_by IS '删除人';
COMMENT ON COLUMN sys_message_content.deleted_at IS '删除时间';

--
-- 消息发送历史记录表
--

DROP TABLE IF EXISTS sys_message_history;

CREATE TABLE sys_message_history
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL DEFAULT 0,
    biz_type   VARCHAR(100) NOT NULL DEFAULT '',
    biz_id     BIGINT       NOT NULL DEFAULT 0,
    message_id BIGINT       NOT NULL DEFAULT 0,
    user_id    BIGINT       NOT NULL DEFAULT 0,
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_message_history__tenant_id ON sys_message_history (tenant_id);
CREATE INDEX ix_sys_message_history__biz_type ON sys_message_history (biz_type);
CREATE INDEX ix_sys_message_history__message_id ON sys_message_history (message_id);
CREATE INDEX ix_sys_message_history__user_id ON sys_message_history (user_id);

COMMENT ON TABLE sys_message_history IS '消息发送历史记录表';
COMMENT ON COLUMN sys_message_history.id IS 'ID';
COMMENT ON COLUMN sys_message_history.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_message_history.biz_type IS '业务类型';
COMMENT ON COLUMN sys_message_history.biz_id IS '业务ID';
COMMENT ON COLUMN sys_message_history.message_id IS '消息ID';
COMMENT ON COLUMN sys_message_history.user_id IS '用户ID';
COMMENT ON COLUMN sys_message_history.version IS '版本号';
COMMENT ON COLUMN sys_message_history.active IS '启用状态';
COMMENT ON COLUMN sys_message_history.created_by IS '创建人';
COMMENT ON COLUMN sys_message_history.created_at IS '创建时间';

--
-- 系统通知表
--

DROP TABLE IF EXISTS sys_notice;

CREATE TABLE sys_notice
(
    id             BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id      BIGINT       NOT NULL DEFAULT 0,
    subject        VARCHAR(100) NOT NULL DEFAULT '',
    content        VARCHAR(100) NOT NULL DEFAULT '',
    recipient_type VARCHAR(100) NOT NULL DEFAULT '',
    recipient_id   BIGINT       NOT NULL DEFAULT 0,
    sender_type    VARCHAR(100) NOT NULL DEFAULT '',
    sender_id      BIGINT       NOT NULL DEFAULT 0,
    read_ind       SMALLINT     NOT NULL DEFAULT 0,
    read_datetime  TIMESTAMP    NULL,
    version        BIGINT       NOT NULL DEFAULT 0,
    active         SMALLINT     NOT NULL DEFAULT 1,
    created_by     BIGINT       NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by     BIGINT       NOT NULL DEFAULT 0,
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by     BIGINT       NOT NULL DEFAULT 0,
    deleted_at     TIMESTAMP    NULL
);

CREATE INDEX ix_sys_notice__tenant_id ON sys_notice (tenant_id);
CREATE INDEX ix_sys_notice__sender ON sys_notice (recipient_type, sender_id);
CREATE INDEX ix_sys_notice__recipient ON sys_notice (sender_type, recipient_id);

COMMENT ON TABLE sys_notice IS '系统通知表';
COMMENT ON COLUMN sys_notice.id IS 'ID';
COMMENT ON COLUMN sys_notice.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_notice.subject IS '通知标题';
COMMENT ON COLUMN sys_notice.content IS '通知内容';
COMMENT ON COLUMN sys_notice.recipient_type IS '收件人实体类型';
COMMENT ON COLUMN sys_notice.recipient_id IS '收件人实体ID';
COMMENT ON COLUMN sys_notice.sender_type IS '发件人实型类型';
COMMENT ON COLUMN sys_notice.sender_id IS '发件人实体ID';
COMMENT ON COLUMN sys_notice.read_ind IS '是否已读';
COMMENT ON COLUMN sys_notice.read_datetime IS '阅读时间';
COMMENT ON COLUMN sys_notice.version IS '版本号';
COMMENT ON COLUMN sys_notice.active IS '启用状态';
COMMENT ON COLUMN sys_notice.created_by IS '创建人';
COMMENT ON COLUMN sys_notice.created_at IS '创建时间';
COMMENT ON COLUMN sys_notice.updated_by IS '修改人';
COMMENT ON COLUMN sys_notice.updated_at IS '修改时间';
COMMENT ON COLUMN sys_notice.deleted_by IS '删除人';
COMMENT ON COLUMN sys_notice.deleted_at IS '删除时间';

--
-- 宣传栏
--

DROP TABLE IF EXISTS sys_banner;

CREATE TABLE sys_banner
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    title       VARCHAR(150) NOT NULL DEFAULT '',
    details     TEXT         NULL,
    description TEXT         NULL,
    idx         INT          NOT NULL DEFAULT 999,
    status      SMALLINT     NOT NULL DEFAULT 1,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_banner__tenant_id ON sys_banner (tenant_id);

COMMENT ON TABLE sys_banner IS '宣传栏';
COMMENT ON COLUMN sys_banner.id IS 'ID';
COMMENT ON COLUMN sys_banner.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_banner.title IS '标题';
COMMENT ON COLUMN sys_banner.details IS '详情';
COMMENT ON COLUMN sys_banner.description IS '备注';
COMMENT ON COLUMN sys_banner.idx IS '序号';
COMMENT ON COLUMN sys_banner.version IS '版本号';
COMMENT ON COLUMN sys_banner.active IS '启用状态';
COMMENT ON COLUMN sys_banner.created_by IS '创建人';
COMMENT ON COLUMN sys_banner.created_at IS '创建时间';
COMMENT ON COLUMN sys_banner.updated_by IS '修改人';
COMMENT ON COLUMN sys_banner.updated_at IS '修改时间';
COMMENT ON COLUMN sys_banner.deleted_by IS '删除人';
COMMENT ON COLUMN sys_banner.deleted_at IS '删除时间';

--
-- 资讯表
--

DROP TABLE IF EXISTS sys_announcement;

CREATE TABLE sys_announcement
(
    id                BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL DEFAULT 0,
    title             VARCHAR(150) NOT NULL DEFAULT '',
    content           TEXT         NULL,
    status            SMALLINT     NOT NULL DEFAULT 1,
    start_datetime    TIMESTAMP    NOT NULL DEFAULT NOW(),
    end_datetime      TIMESTAMP    NOT NULL DEFAULT NOW(),
    allow_comment_ind SMALLINT     NOT NULL DEFAULT 1,
    description       VARCHAR(255) NOT NULL DEFAULT '',
    version           BIGINT       NOT NULL DEFAULT 0,
    active            SMALLINT     NOT NULL DEFAULT 1,
    created_by        BIGINT       NOT NULL DEFAULT 0,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by        BIGINT       NOT NULL DEFAULT 0,
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by        BIGINT       NOT NULL DEFAULT 0,
    deleted_at        TIMESTAMP    NULL
);

CREATE INDEX ix_sys_announcement__tenant_id ON sys_announcement (tenant_id);

COMMENT ON TABLE sys_announcement IS '资讯表';
COMMENT ON COLUMN sys_announcement.id IS 'ID';
COMMENT ON COLUMN sys_announcement.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_announcement.title IS '标题';
COMMENT ON COLUMN sys_announcement.content IS '内容';
COMMENT ON COLUMN sys_announcement.status IS '发布状态';
COMMENT ON COLUMN sys_announcement.start_datetime IS '发布期限';
COMMENT ON COLUMN sys_announcement.end_datetime IS '发布期限';
COMMENT ON COLUMN sys_announcement.allow_comment_ind IS '是否允许评论';
COMMENT ON COLUMN sys_announcement.description IS '备注';
COMMENT ON COLUMN sys_announcement.version IS '版本号';
COMMENT ON COLUMN sys_announcement.active IS '启用状态';
COMMENT ON COLUMN sys_announcement.created_by IS '创建人';
COMMENT ON COLUMN sys_announcement.created_at IS '创建时间';
COMMENT ON COLUMN sys_announcement.updated_by IS '修改人';
COMMENT ON COLUMN sys_announcement.updated_at IS '修改时间';
COMMENT ON COLUMN sys_announcement.deleted_by IS '删除人';
COMMENT ON COLUMN sys_announcement.deleted_at IS '删除时间';

--
-- 友情链接表
--

DROP TABLE IF EXISTS sys_link;

CREATE TABLE sys_link
(
    id          BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id   BIGINT        NOT NULL DEFAULT 0,
    title       VARCHAR(150)  NOT NULL DEFAULT '',
    sub_title   VARCHAR(150)  NOT NULL DEFAULT '',
    link        VARCHAR(1000) NULL,
    description VARCHAR(255)  NOT NULL DEFAULT '',
    summary     VARCHAR(255)  NOT NULL DEFAULT '',
    idx         INT           NOT NULL DEFAULT 999,
    version     BIGINT        NOT NULL DEFAULT 0,
    active      SMALLINT      NOT NULL DEFAULT 1,
    created_by  BIGINT        NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by  BIGINT        NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT        NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP     NULL
);

CREATE INDEX ix_sys_link__tenant_id ON sys_link (tenant_id);

COMMENT ON TABLE sys_link IS '友情链接表';
COMMENT ON COLUMN sys_link.id IS 'ID';
COMMENT ON COLUMN sys_link.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_link.title IS '标题';
COMMENT ON COLUMN sys_link.sub_title IS '副标题';
COMMENT ON COLUMN sys_link.link IS '链接';
COMMENT ON COLUMN sys_link.description IS '备注';
COMMENT ON COLUMN sys_link.summary IS '简介';
COMMENT ON COLUMN sys_link.idx IS '序号';
COMMENT ON COLUMN sys_link.version IS '版本号';
COMMENT ON COLUMN sys_link.active IS '启用状态';
COMMENT ON COLUMN sys_link.created_by IS '创建人';
COMMENT ON COLUMN sys_link.created_at IS '创建时间';
COMMENT ON COLUMN sys_link.updated_by IS '修改人';
COMMENT ON COLUMN sys_link.updated_at IS '修改时间';
COMMENT ON COLUMN sys_link.deleted_by IS '删除人';
COMMENT ON COLUMN sys_link.deleted_at IS '删除时间';

--
-- 行政区域表
-- 国内数据来自民政部
-- 中国 - 100000

DROP TABLE IF EXISTS sys_region;

CREATE TABLE sys_region
(
    id                 BIGSERIAL    NOT NULL PRIMARY KEY,
    parent_id          BIGINT       NOT NULL DEFAULT 0,
    type               VARCHAR(150) NOT NULL DEFAULT '',
    code               VARCHAR(150) NOT NULL DEFAULT '',
    title              VARCHAR(150) NOT NULL DEFAULT '',
    title_first_letter VARCHAR(5)   NOT NULL DEFAULT '',
    wkb_geometry       GEOMETRY     NULL,
    version            BIGINT       NOT NULL DEFAULT 0,
    active             SMALLINT     NOT NULL DEFAULT 1,
    created_by         BIGINT       NOT NULL DEFAULT 0,
    created_at         TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by         BIGINT       NOT NULL DEFAULT 0,
    updated_at         TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by         BIGINT       NOT NULL DEFAULT 0,
    deleted_at         TIMESTAMP    NULL
);

CREATE INDEX ix_sys_region__parent_id ON sys_region (parent_id);
CREATE INDEX ix_sys_region__code ON sys_region (code);
CREATE INDEX ix_sys_region__wkb_geometry ON sys_region USING GIST (wkb_geometry);

COMMENT ON TABLE sys_region IS '行政区域表';
COMMENT ON COLUMN sys_region.id IS 'ID';
COMMENT ON COLUMN sys_region.parent_id IS '上级区域ID';
COMMENT ON COLUMN sys_region.type IS '行政区划类型';
COMMENT ON COLUMN sys_region.code IS '行政区划代码';
COMMENT ON COLUMN sys_region.title IS '单位名称';
COMMENT ON COLUMN sys_region.title_first_letter IS '单位名称拼音首字母大写';
COMMENT ON COLUMN sys_region.version IS '版本号';
COMMENT ON COLUMN sys_region.active IS '启用状态';
COMMENT ON COLUMN sys_region.wkb_geometry IS '空间几何边界';
COMMENT ON COLUMN sys_region.created_by IS '创建人';
COMMENT ON COLUMN sys_region.created_at IS '创建时间';
COMMENT ON COLUMN sys_region.updated_by IS '修改人';
COMMENT ON COLUMN sys_region.updated_at IS '修改时间';
COMMENT ON COLUMN sys_region.deleted_by IS '删除人';
COMMENT ON COLUMN sys_region.deleted_at IS '删除时间';

--
-- 地区关联表
--

DROP TABLE IF EXISTS sys_region_relation;

CREATE TABLE sys_region_relation
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    tenant_id  BIGINT      NOT NULL DEFAULT 0,
    region_id  BIGINT      NOT NULL DEFAULT 0,
    biz_type   VARCHAR(50) NOT NULL DEFAULT '',
    biz_id     BIGINT      NOT NULL DEFAULT 0,
    version    BIGINT      NOT NULL DEFAULT 0,
    active     SMALLINT    NOT NULL DEFAULT 1,
    created_by BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_region_relation__tenant_id ON sys_region_relation (tenant_id);
CREATE INDEX ix_sys_region_relation__region_id ON sys_region_relation (region_id);
CREATE INDEX ix_sys_region_relation__biz ON sys_region_relation (biz_type, biz_id);

COMMENT ON TABLE sys_region_relation IS '地区关联表';
COMMENT ON COLUMN sys_region_relation.id IS 'ID';
COMMENT ON COLUMN sys_region_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_region_relation.region_id IS '地址ID';
COMMENT ON COLUMN sys_region_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_region_relation.biz_id IS '业务ID';
COMMENT ON COLUMN sys_region_relation.version IS '版本号';
COMMENT ON COLUMN sys_region_relation.active IS '启用状态';
COMMENT ON COLUMN sys_region_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_region_relation.created_at IS '创建时间';

--
-- 地址表
--

DROP TABLE IF EXISTS sys_address;

CREATE TABLE sys_address
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    biz_type    VARCHAR(50)  NOT NULL DEFAULT '',
    biz_id      BIGINT       NOT NULL DEFAULT 0,
    country_id  BIGINT       NOT NULL DEFAULT 0,
    province_id BIGINT       NOT NULL DEFAULT 0,
    city_id     BIGINT       NOT NULL DEFAULT 0,
    county_id   BIGINT       NOT NULL DEFAULT 0,
    title       VARCHAR(150) NOT NULL DEFAULT '',
    location    VARCHAR(100) NOT NULL DEFAULT '',
    details     TEXT         NULL,
    extra       TEXT         NULL,
    lng         VARCHAR(100) NULL,
    lat         VARCHAR(100) NULL,
    status      SMALLINT     NOT NULL DEFAULT 1,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX sys_address__tenant_id ON sys_address (tenant_id);
CREATE INDEX sys_address__country_id ON sys_address (country_id);
CREATE INDEX sys_address__province_id ON sys_address (province_id);
CREATE INDEX sys_address__city_id ON sys_address (city_id);
CREATE INDEX sys_address__county_id ON sys_address (county_id);

COMMENT ON TABLE sys_address IS '地址表';
COMMENT ON COLUMN sys_address.id IS 'ID';
COMMENT ON COLUMN sys_address.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_address.biz_type IS '业务类型';
COMMENT ON COLUMN sys_address.biz_id IS '业务ID';
COMMENT ON COLUMN sys_address.country_id IS '国家ID';
COMMENT ON COLUMN sys_address.province_id IS '省份ID';
COMMENT ON COLUMN sys_address.city_id IS '城市ID';
COMMENT ON COLUMN sys_address.county_id IS '县区ID';
COMMENT ON COLUMN sys_address.title IS '地点标题';
COMMENT ON COLUMN sys_address.location IS '地点';
COMMENT ON COLUMN sys_address.details IS '详细地址';
COMMENT ON COLUMN sys_address.extra IS '附加信息';
COMMENT ON COLUMN sys_address.lng IS '经度';
COMMENT ON COLUMN sys_address.lat IS '纬度';
COMMENT ON COLUMN sys_address.status IS '状态';
COMMENT ON COLUMN sys_address.version IS '版本号';
COMMENT ON COLUMN sys_address.active IS '启用状态';
COMMENT ON COLUMN sys_address.created_by IS '创建人';
COMMENT ON COLUMN sys_address.created_at IS '创建时间';
COMMENT ON COLUMN sys_address.updated_by IS '最后修改人';
COMMENT ON COLUMN sys_address.updated_at IS '最后修改时间';
COMMENT ON COLUMN sys_address.deleted_by IS '删除人';
COMMENT ON COLUMN sys_address.deleted_at IS '删除时间';

--
-- 地址关联表
--

DROP TABLE IF EXISTS sys_address_relation;

CREATE TABLE sys_address_relation
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    tenant_id  BIGINT      NOT NULL DEFAULT 0,
    address_id BIGINT      NOT NULL DEFAULT 0,
    biz_type   VARCHAR(50) NOT NULL DEFAULT '',
    biz_id     BIGINT      NOT NULL DEFAULT 0,
    version    BIGINT      NOT NULL DEFAULT 0,
    active     SMALLINT    NOT NULL DEFAULT 1,
    created_by BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_address_relation__tenant_id ON sys_address_relation (tenant_id);
CREATE INDEX ix_sys_address_relation__address_id ON sys_address_relation (address_id);
CREATE INDEX ix_sys_address_relation__biz ON sys_address_relation (biz_type, biz_id);

COMMENT ON TABLE sys_address_relation IS '地址关联表';
COMMENT ON COLUMN sys_address_relation.id IS 'ID';
COMMENT ON COLUMN sys_address_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_address_relation.address_id IS '地址ID';
COMMENT ON COLUMN sys_address_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_address_relation.biz_id IS '业务ID';
COMMENT ON COLUMN sys_address_relation.version IS '版本号';
COMMENT ON COLUMN sys_address_relation.active IS '启用状态';
COMMENT ON COLUMN sys_address_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_address_relation.created_at IS '创建时间';


-- =====================================================================================================================
-- Job
-- =====================================================================================================================

--
-- 定时任务表
--

DROP TABLE IF EXISTS sys_job;

CREATE TABLE sys_job
(
    id          BIGSERIAL     NOT NULL PRIMARY KEY,
    code        VARCHAR(150)  NOT NULL DEFAULT '',
    classname   VARCHAR(255)  NOT NULL DEFAULT '',
    description VARCHAR(255)  NOT NULL DEFAULT '',
    type        VARCHAR(255)  NULL,
    unit        VARCHAR(255)  NULL,
    period      INT           NULL,
    hour        INT           NULL,
    minute      INT           NULL,
    cron        VARCHAR(255)  NULL,
    params      VARCHAR(5000) NULL,
    status      SMALLINT      NOT NULL DEFAULT 1,
    version     BIGINT        NOT NULL DEFAULT 0,
    active      SMALLINT      NOT NULL DEFAULT 1,
    created_by  BIGINT        NOT NULL DEFAULT 0,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by  BIGINT        NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT        NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP     NULL
);

COMMENT ON TABLE sys_job IS '定时任务表';
COMMENT ON COLUMN sys_job.id IS 'ID';
COMMENT ON COLUMN sys_job.code IS '编号';
COMMENT ON COLUMN sys_job.classname IS '任务类名';
COMMENT ON COLUMN sys_job.description IS '描述说明';
COMMENT ON COLUMN sys_job.type IS '类型';
COMMENT ON COLUMN sys_job.unit IS '单位';
COMMENT ON COLUMN sys_job.period IS '周期';
COMMENT ON COLUMN sys_job.hour IS '小时';
COMMENT ON COLUMN sys_job.minute IS '分钟';
COMMENT ON COLUMN sys_job.cron IS '表达式';
COMMENT ON COLUMN sys_job.params IS '参数';
COMMENT ON COLUMN sys_job.status IS '状态';
COMMENT ON COLUMN sys_job.version IS '版本号';
COMMENT ON COLUMN sys_job.active IS '启用状态';
COMMENT ON COLUMN sys_job.created_by IS '创建人';
COMMENT ON COLUMN sys_job.created_at IS '创建时间';
COMMENT ON COLUMN sys_job.updated_by IS '修改人';
COMMENT ON COLUMN sys_job.updated_at IS '修改时间';
COMMENT ON COLUMN sys_job.deleted_by IS '删除人';
COMMENT ON COLUMN sys_job.deleted_at IS '删除时间';


-- =====================================================================================================================
-- OAuth
-- =====================================================================================================================

--
-- 客户端
--

DROP TABLE IF EXISTS sys_client;

CREATE TABLE sys_client
(
    id                            BIGSERIAL     NOT NULL PRIMARY KEY,
    client_id                     VARCHAR(150)  NOT NULL DEFAULT '',
    client_id_issued_at           TIMESTAMP,
    client_secret                 VARCHAR(255)  NOT NULL DEFAULT '',
    client_secret_expires_at      TIMESTAMP,
    client_name                   VARCHAR(255)  NOT NULL DEFAULT '',
    client_authentication_methods VARCHAR(1000) NOT NULL DEFAULT '',
    authorization_grant_types     VARCHAR(255)  NOT NULL DEFAULT '',
    redirect_uris                 VARCHAR(255)  NOT NULL DEFAULT '',
    post_logout_redirect_uris     VARCHAR(255)  NOT NULL DEFAULT '',
    scopes                        VARCHAR(2000) NOT NULL DEFAULT '',
    client_settings               VARCHAR(2000) NOT NULL DEFAULT '',
    token_settings                VARCHAR(2000) NOT NULL DEFAULT '',
    description                   VARCHAR(255)  NOT NULL DEFAULT '',
    status                        SMALLINT      NOT NULL DEFAULT 1,
    version                       BIGINT        NOT NULL DEFAULT 0,
    active                        SMALLINT      NOT NULL DEFAULT 1,
    created_by                    BIGINT        NOT NULL DEFAULT 0,
    created_at                    TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by                    BIGINT        NOT NULL DEFAULT 0,
    updated_at                    TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by                    BIGINT        NOT NULL DEFAULT 0,
    deleted_at                    TIMESTAMP     NULL
);

CREATE INDEX ix_sys_client__client_id ON sys_client (client_id);

COMMENT ON TABLE sys_client IS '客户端';
COMMENT ON COLUMN sys_client.id IS 'ID';
COMMENT ON COLUMN sys_client.client_id IS 'Client ID';
COMMENT ON COLUMN sys_client.client_id_issued_at IS '客户端ID签发时间';
COMMENT ON COLUMN sys_client.client_secret IS 'Client Secret';
COMMENT ON COLUMN sys_client.client_secret_expires_at IS '客户端密钥过期时间';
COMMENT ON COLUMN sys_client.client_name IS 'Client Name';
COMMENT ON COLUMN sys_client.client_authentication_methods IS 'Client Authentication Methods';
COMMENT ON COLUMN sys_client.authorization_grant_types IS 'Authorization Grant Types';
COMMENT ON COLUMN sys_client.redirect_uris IS 'Redirect Uris';
COMMENT ON COLUMN sys_client.post_logout_redirect_uris IS 'Post Logout Redirect Uris';
COMMENT ON COLUMN sys_client.scopes IS 'Score';
COMMENT ON COLUMN sys_client.client_settings IS '客户端设置';
COMMENT ON COLUMN sys_client.token_settings IS 'Token设置';
COMMENT ON COLUMN sys_client.description IS '备注';
COMMENT ON COLUMN sys_client.status IS '发布状态';
COMMENT ON COLUMN sys_client.version IS '版本号';
COMMENT ON COLUMN sys_client.active IS '启用状态';
COMMENT ON COLUMN sys_client.created_by IS '创建人';
COMMENT ON COLUMN sys_client.created_at IS '创建时间';
COMMENT ON COLUMN sys_client.updated_by IS '修改人';
COMMENT ON COLUMN sys_client.updated_at IS '修改时间';
COMMENT ON COLUMN sys_client.deleted_by IS '删除人';
COMMENT ON COLUMN sys_client.deleted_at IS '删除时间';

--
-- 认证记录
--

DROP TABLE IF EXISTS sys_authorization;

CREATE TABLE sys_authorization
(
    id                            BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id                     BIGINT       NOT NULL DEFAULT 0,
    uuid                          VARCHAR(200) NOT NULL DEFAULT '',
    client_id                     VARCHAR(100) NOT NULL DEFAULT '',
    principal_name                VARCHAR(200),
    authorization_grant_type      VARCHAR(100),
    attributes                    TEXT,
    state                         VARCHAR(2000),
    authorization_code_value      VARCHAR(2000),
    authorization_code_issued_at  TIMESTAMP,
    authorization_code_expires_at TIMESTAMP,
    authorization_code_metadata   TEXT,
    access_token_value            TEXT,
    access_token_issued_at        TIMESTAMP,
    access_token_expires_at       TIMESTAMP,
    access_token_metadata         TEXT,
    access_token_type             VARCHAR(100),
    access_token_scopes           VARCHAR(2000),
    oidc_id_token_value           TEXT,
    oidc_id_token_issued_at       TIMESTAMP,
    oidc_id_token_expires_at      TIMESTAMP,
    oidc_id_token_metadata        TEXT,
    oidc_id_token_claims          TEXT,
    refresh_token_value           TEXT,
    refresh_token_issued_at       TIMESTAMP,
    refresh_token_expires_at      TIMESTAMP,
    refresh_token_metadata        TEXT,
    version                       BIGINT       NOT NULL DEFAULT 0,
    active                        SMALLINT     NOT NULL DEFAULT 1,
    created_by                    BIGINT       NOT NULL DEFAULT 0,
    created_at                    TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by                    BIGINT       NOT NULL DEFAULT 0,
    updated_at                    TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by                    BIGINT       NOT NULL DEFAULT 0,
    deleted_at                    TIMESTAMP    NULL
);

CREATE INDEX ix_sys_authorization__client_id ON sys_authorization (client_id);

COMMENT ON TABLE sys_authorization IS '认证记录';
COMMENT ON COLUMN sys_authorization.id IS 'ID';
COMMENT ON COLUMN sys_authorization.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_authorization.uuid IS 'UUID';
COMMENT ON COLUMN sys_authorization.client_id IS '客户端ID';
COMMENT ON COLUMN sys_authorization.principal_name IS '主体名称';
COMMENT ON COLUMN sys_authorization.authorization_grant_type IS '授权类型';
COMMENT ON COLUMN sys_authorization.attributes IS '属性';
COMMENT ON COLUMN sys_authorization.state IS '状态';
COMMENT ON COLUMN sys_authorization.authorization_code_value IS '授权码值';
COMMENT ON COLUMN sys_authorization.authorization_code_issued_at IS '授权码签发时间';
COMMENT ON COLUMN sys_authorization.authorization_code_expires_at IS '授权码过期时间';
COMMENT ON COLUMN sys_authorization.authorization_code_metadata IS '授权码元数据';
COMMENT ON COLUMN sys_authorization.access_token_value IS '访问令牌值';
COMMENT ON COLUMN sys_authorization.access_token_issued_at IS '访问令牌签发时间';
COMMENT ON COLUMN sys_authorization.access_token_expires_at IS '访问令牌过期时间';
COMMENT ON COLUMN sys_authorization.access_token_metadata IS '访问令牌元数据';
COMMENT ON COLUMN sys_authorization.access_token_type IS '访问令牌类型';
COMMENT ON COLUMN sys_authorization.access_token_scopes IS '访问令牌范围';
COMMENT ON COLUMN sys_authorization.oidc_id_token_value IS 'OIDC ID令牌值';
COMMENT ON COLUMN sys_authorization.oidc_id_token_issued_at IS 'OIDC ID令牌签发时间';
COMMENT ON COLUMN sys_authorization.oidc_id_token_expires_at IS 'OIDC ID令牌过期时间';
COMMENT ON COLUMN sys_authorization.oidc_id_token_metadata IS 'OIDC ID令牌元数据';
COMMENT ON COLUMN sys_authorization.oidc_id_token_claims IS 'OIDC ID令牌声明';
COMMENT ON COLUMN sys_authorization.refresh_token_value IS '刷新令牌值';
COMMENT ON COLUMN sys_authorization.refresh_token_issued_at IS '刷新令牌签发时间';
COMMENT ON COLUMN sys_authorization.refresh_token_expires_at IS '刷新令牌过期时间';
COMMENT ON COLUMN sys_authorization.refresh_token_metadata IS '刷新令牌元数据';
COMMENT ON COLUMN sys_authorization.version IS '版本号';
COMMENT ON COLUMN sys_authorization.active IS '启用状态';
COMMENT ON COLUMN sys_authorization.created_by IS '创建人';
COMMENT ON COLUMN sys_authorization.created_at IS '创建时间';
COMMENT ON COLUMN sys_authorization.updated_by IS '修改人';
COMMENT ON COLUMN sys_authorization.updated_at IS '修改时间';
COMMENT ON COLUMN sys_authorization.deleted_by IS '删除人';
COMMENT ON COLUMN sys_authorization.deleted_at IS '删除时间';

--
-- 认证同意授权记录
--

DROP TABLE IF EXISTS sys_authorization_consent;

CREATE TABLE sys_authorization_consent
(
    id             BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id      BIGINT        NOT NULL DEFAULT 0,
    uuid           VARCHAR(100)  NOT NULL DEFAULT '',
    client_id      VARCHAR(100)  NOT NULL DEFAULT '',
    principal_name VARCHAR(200)  NOT NULL DEFAULT '',
    authorities    VARCHAR(2000) NOT NULL DEFAULT '',
    version        BIGINT        NOT NULL DEFAULT 0,
    active         SMALLINT      NOT NULL DEFAULT 1,
    created_by     BIGINT        NOT NULL DEFAULT 0,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by     BIGINT        NOT NULL DEFAULT 0,
    updated_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by     BIGINT        NOT NULL DEFAULT 0,
    deleted_at     TIMESTAMP     NULL
);

CREATE INDEX ix_sys_authorization_consent ON sys_authorization_consent (client_id, principal_name);

COMMENT ON TABLE sys_authorization_consent IS '授权记录';
COMMENT ON COLUMN sys_authorization_consent.id IS 'ID';
COMMENT ON COLUMN sys_authorization_consent.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_authorization_consent.uuid IS 'UUID';
COMMENT ON COLUMN sys_authorization_consent.client_id IS '客户端ID';
COMMENT ON COLUMN sys_authorization_consent.principal_name IS '主体名称';
COMMENT ON COLUMN sys_authorization_consent.authorities IS '权限';
COMMENT ON COLUMN sys_authorization_consent.version IS '版本号';
COMMENT ON COLUMN sys_authorization_consent.active IS '启用状态';
COMMENT ON COLUMN sys_authorization_consent.created_by IS '创建人';
COMMENT ON COLUMN sys_authorization_consent.created_at IS '创建时间';
COMMENT ON COLUMN sys_authorization_consent.updated_by IS '修改人';
COMMENT ON COLUMN sys_authorization_consent.updated_at IS '修改时间';
COMMENT ON COLUMN sys_authorization_consent.deleted_by IS '删除人';
COMMENT ON COLUMN sys_authorization_consent.deleted_at IS '删除时间';

-- =====================================================================================================================
-- IM Chat Message
-- =====================================================================================================================

--
-- 互动会话表
--

DROP TABLE IF EXISTS sys_chat_session;

CREATE TABLE sys_chat_session
(
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    biz_type        VARCHAR(50)  NOT NULL DEFAULT '',
    biz_id          BIGINT       NOT NULL DEFAULT 0,
    user_id         BIGINT       NOT NULL DEFAULT 0,
    target_user_id  BIGINT       NOT NULL DEFAULT 0,
    last_message_id BIGINT       NOT NULL DEFAULT 0,
    session_id      VARCHAR(100) NOT NULL DEFAULT '',
    status          SMALLINT     NOT NULL DEFAULT 1,
    version         BIGINT       NOT NULL DEFAULT 0,
    active          SMALLINT     NOT NULL DEFAULT 1,
    created_by      BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      BIGINT       NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by      BIGINT       NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP    NULL
);

CREATE INDEX ix_sys_chat_session__tenant_id ON sys_chat_session (tenant_id);
CREATE INDEX ix_sys_chat_session__biz_type ON sys_chat_session (biz_type, biz_id);
CREATE INDEX ix_sys_chat_session__owner ON sys_chat_session (user_id);
CREATE INDEX ix_sys_chat_session__target ON sys_chat_session (target_user_id);
CREATE INDEX ix_sys_chat_session__session_id ON sys_chat_session (session_id);

COMMENT ON TABLE sys_chat_session IS '互动会话表';
COMMENT ON COLUMN sys_chat_session.id IS 'ID';
COMMENT ON COLUMN sys_chat_session.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_chat_session.biz_type IS '业务类型';
COMMENT ON COLUMN sys_chat_session.biz_id IS '业务ID';
COMMENT ON COLUMN sys_chat_session.user_id IS '用户ID';
COMMENT ON COLUMN sys_chat_session.target_user_id IS '目标用户ID';
COMMENT ON COLUMN sys_chat_session.session_id IS '会话标识';
COMMENT ON COLUMN sys_chat_session.last_message_id IS '最新消息的ID';
COMMENT ON COLUMN sys_chat_session.status IS '状态';
COMMENT ON COLUMN sys_chat_session.version IS '版本号';
COMMENT ON COLUMN sys_chat_session.active IS '启用状态';
COMMENT ON COLUMN sys_chat_session.created_by IS '创建人';
COMMENT ON COLUMN sys_chat_session.created_at IS '创建时间';
COMMENT ON COLUMN sys_chat_session.updated_by IS '最后修改人';
COMMENT ON COLUMN sys_chat_session.updated_at IS '最后修改时间';
COMMENT ON COLUMN sys_chat_session.deleted_by IS '删除人';
COMMENT ON COLUMN sys_chat_session.deleted_at IS '删除时间';

--
-- 互动会话实体表
-- 用于保存互动会话跟实体的状态信息
--

DROP TABLE IF EXISTS sys_chat_entity_session;

CREATE TABLE sys_chat_entity_session
(
    id                   BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id            BIGINT    NOT NULL DEFAULT 0,
    user_id              BIGINT    NOT NULL DEFAULT 0,
    chat_session_id      BIGINT    NOT NULL DEFAULT 0,
    last_read_message_id BIGINT    NOT NULL DEFAULT 0,
    last_read_at         TIMESTAMP NULL,
    top_ind              SMALLINT  NOT NULL DEFAULT 0,
    top_at               TIMESTAMP NULL,
    collect_ind          SMALLINT  NOT NULL DEFAULT 0,
    collect_at           TIMESTAMP NULL,
    clear_ind            SMALLINT  NOT NULL DEFAULT 0,
    clear_at             TIMESTAMP NULL,
    status               SMALLINT  NOT NULL DEFAULT 1,
    version              BIGINT    NOT NULL DEFAULT 0,
    active               SMALLINT  NOT NULL DEFAULT 1,
    created_by           BIGINT    NOT NULL DEFAULT 0,
    created_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by           BIGINT    NOT NULL DEFAULT 0,
    updated_at           TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_by           BIGINT    NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP NULL
);

CREATE INDEX ix_sys_chat_entity_session__tenant_id ON sys_chat_entity_session (tenant_id);
CREATE INDEX ix_sys_chat_entity_session__chat_session_id ON sys_chat_entity_session (chat_session_id);
CREATE INDEX ix_sys_chat_entity_session__user_id ON sys_chat_entity_session (user_id);
CREATE INDEX ix_sys_chat_entity_session__last_read_message_id ON sys_chat_entity_session (user_id, last_read_message_id);
CREATE INDEX ix_sys_chat_entity_session__clear ON sys_chat_entity_session (clear_ind);

COMMENT ON TABLE sys_chat_entity_session IS '互动会话实体表';
COMMENT ON COLUMN sys_chat_entity_session.id IS 'ID';
COMMENT ON COLUMN sys_chat_entity_session.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_chat_entity_session.chat_session_id IS '互动会话ID';
COMMENT ON COLUMN sys_chat_entity_session.user_id IS '用户ID';
COMMENT ON COLUMN sys_chat_entity_session.last_read_message_id IS '最后读取消息ID';
COMMENT ON COLUMN sys_chat_entity_session.last_read_at IS '最后读取消息事件';
COMMENT ON COLUMN sys_chat_entity_session.top_ind IS '是否置顶';
COMMENT ON COLUMN sys_chat_entity_session.top_at IS '置顶时间';
COMMENT ON COLUMN sys_chat_entity_session.collect_ind IS '是否收藏';
COMMENT ON COLUMN sys_chat_entity_session.collect_at IS '收藏时间';
COMMENT ON COLUMN sys_chat_entity_session.clear_ind IS '是否清除';
COMMENT ON COLUMN sys_chat_entity_session.clear_at IS '清除时间';
COMMENT ON COLUMN sys_chat_entity_session.status IS '状态';
COMMENT ON COLUMN sys_chat_entity_session.version IS '版本号';
COMMENT ON COLUMN sys_chat_entity_session.active IS '启用状态';
COMMENT ON COLUMN sys_chat_entity_session.created_by IS '创建人';
COMMENT ON COLUMN sys_chat_entity_session.created_at IS '创建时间';
COMMENT ON COLUMN sys_chat_entity_session.updated_by IS '最后修改人';
COMMENT ON COLUMN sys_chat_entity_session.updated_at IS '最后修改时间';
COMMENT ON COLUMN sys_chat_entity_session.deleted_by IS '删除人';
COMMENT ON COLUMN sys_chat_entity_session.deleted_at IS '删除时间';

--
-- 互动消息表
--

DROP TABLE IF EXISTS sys_chat_message;

CREATE TABLE sys_chat_message
(
    id                   BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id            BIGINT       NOT NULL DEFAULT 0,
    biz_id               BIGINT       NOT NULL DEFAULT 0,
    chat_session_id      BIGINT       NOT NULL DEFAULT 0,
    sender_user_id       BIGINT       NOT NULL DEFAULT 0,
    sender_user_type     VARCHAR(100) NOT NULL DEFAULT '',
    receiver_user_id     BIGINT       NOT NULL DEFAULT 0,
    receiver_user_type   VARCHAR(100) NOT NULL DEFAULT '',
    message_content_type VARCHAR(50)  NOT NULL DEFAULT '',
    sequence             SMALLINT     NOT NULL DEFAULT 1,
    status               SMALLINT     NOT NULL DEFAULT 1,
    version              BIGINT       NOT NULL DEFAULT 0,
    active               SMALLINT     NOT NULL DEFAULT 1,
    created_by           BIGINT       NOT NULL DEFAULT 0,
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by           BIGINT       NOT NULL DEFAULT 0,
    updated_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by           BIGINT       NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP    NULL
);

CREATE INDEX ix_sys_chat_message__tenant_id ON sys_chat_message (tenant_id);
CREATE INDEX ix_sys_chat_message__chat_session_id ON sys_chat_message (chat_session_id);
CREATE INDEX ix_sys_chat_message__sender ON sys_chat_message (sender_user_id);
CREATE INDEX ix_sys_chat_message__recipient ON sys_chat_message (receiver_user_id);
CREATE INDEX ix_sys_chat_message__biz_id ON sys_chat_message (biz_id);

COMMENT ON TABLE sys_chat_message IS '互动消息表';
COMMENT ON COLUMN sys_chat_message.id IS 'ID';
COMMENT ON COLUMN sys_chat_message.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_chat_message.biz_id IS '业务ID';
COMMENT ON COLUMN sys_chat_message.chat_session_id IS '互动会话ID';
COMMENT ON COLUMN sys_chat_message.sender_user_id IS '发送人ID';
COMMENT ON COLUMN sys_chat_message.sender_user_type IS '发送人用户类型';
COMMENT ON COLUMN sys_chat_message.receiver_user_id IS '接收人ID';
COMMENT ON COLUMN sys_chat_message.receiver_user_type IS '接收人用户类型';
COMMENT ON COLUMN sys_chat_message.message_content_type IS '消息内容类型';
COMMENT ON COLUMN sys_chat_message.sequence IS '消息序号';
COMMENT ON COLUMN sys_chat_message.status IS '状态';
COMMENT ON COLUMN sys_chat_message.version IS '版本号';
COMMENT ON COLUMN sys_chat_message.active IS '启用状态';
COMMENT ON COLUMN sys_chat_message.created_by IS '创建人';
COMMENT ON COLUMN sys_chat_message.created_at IS '创建时间';
COMMENT ON COLUMN sys_chat_message.updated_by IS '最后修改人';
COMMENT ON COLUMN sys_chat_message.updated_at IS '最后修改时间';
COMMENT ON COLUMN sys_chat_message.deleted_by IS '删除人';
COMMENT ON COLUMN sys_chat_message.deleted_at IS '删除时间';

--
-- 沟通内容表
--

DROP TABLE IF EXISTS sys_chat_message_content;

CREATE TABLE sys_chat_message_content
(
    id              BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id       BIGINT    NOT NULL DEFAULT 0,
    chat_session_id BIGINT    NOT NULL DEFAULT 0,
    chat_message_id BIGINT    NOT NULL DEFAULT 0,
    content         TEXT      NULL,
    extra           TEXT      NULL,
    status          SMALLINT  NOT NULL DEFAULT 1,
    version         BIGINT    NOT NULL DEFAULT 0,
    active          SMALLINT  NOT NULL DEFAULT 1,
    created_by      BIGINT    NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by      BIGINT    NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_by      BIGINT    NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP NULL
);

CREATE INDEX ix_sys_chat_message_content__tenant_id ON sys_chat_message_content (tenant_id);
CREATE INDEX ix_sys_chat_message_content__chat_session_id ON sys_chat_message_content (chat_session_id);
CREATE INDEX ix_sys_chat_message_content__chat_message_id ON sys_chat_message_content (chat_message_id);

COMMENT ON TABLE sys_chat_message_content IS '沟通内容表';
COMMENT ON COLUMN sys_chat_message_content.id IS 'ID';
COMMENT ON COLUMN sys_chat_message_content.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_chat_message_content.chat_session_id IS '互动会话ID';
COMMENT ON COLUMN sys_chat_message_content.chat_message_id IS '互动消息ID';
COMMENT ON COLUMN sys_chat_message_content.content IS '内容';
COMMENT ON COLUMN sys_chat_message_content.extra IS '附加信息';
COMMENT ON COLUMN sys_chat_message_content.status IS '状态';
COMMENT ON COLUMN sys_chat_message_content.version IS '版本号';
COMMENT ON COLUMN sys_chat_message_content.active IS '启用状态';
COMMENT ON COLUMN sys_chat_message_content.created_by IS '创建人';
COMMENT ON COLUMN sys_chat_message_content.created_at IS '创建时间';
COMMENT ON COLUMN sys_chat_message_content.updated_by IS '最后修改人';
COMMENT ON COLUMN sys_chat_message_content.updated_at IS '最后修改时间';
COMMENT ON COLUMN sys_chat_message_content.deleted_by IS '删除人';
COMMENT ON COLUMN sys_chat_message_content.deleted_at IS '删除时间';

--
-- 互动消息实体表
-- 用于保存互动消息跟实体的状态信息
--

DROP TABLE IF EXISTS sys_chat_entity_message;

CREATE TABLE sys_chat_entity_message
(
    id              BIGSERIAL NOT NULL PRIMARY KEY,
    tenant_id       BIGINT    NOT NULL DEFAULT 0,
    chat_session_id BIGINT    NOT NULL DEFAULT 0,
    chat_message_id BIGINT    NOT NULL DEFAULT 0,
    user_id         BIGINT    NOT NULL DEFAULT 0,
    read_ind        SMALLINT  NOT NULL DEFAULT 0,
    delete_ind      SMALLINT  NOT NULL DEFAULT 0,
    status          SMALLINT  NOT NULL DEFAULT 1,
    version         BIGINT    NOT NULL DEFAULT 0,
    active          SMALLINT  NOT NULL DEFAULT 1,
    created_by      BIGINT    NOT NULL DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_by      BIGINT    NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    deleted_by      BIGINT    NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP NULL
);

CREATE INDEX ix_sys_chat_entity_message__tenant_id ON sys_chat_entity_message (tenant_id);
CREATE INDEX ix_sys_chat_entity_message__chat_session_id ON sys_chat_entity_message (chat_session_id);
CREATE INDEX ix_sys_chat_entity_message__chat_message_id ON sys_chat_entity_message (chat_message_id);
CREATE INDEX ix_sys_chat_entity_message__user_id ON sys_chat_entity_message (user_id);

COMMENT ON TABLE sys_chat_entity_message IS '互动消息实体表';
COMMENT ON COLUMN sys_chat_entity_message.id IS 'ID';
COMMENT ON COLUMN sys_chat_entity_message.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_chat_entity_message.chat_session_id IS '互动会话ID';
COMMENT ON COLUMN sys_chat_entity_message.chat_message_id IS '互动消息ID';
COMMENT ON COLUMN sys_chat_entity_message.user_id IS '用户ID';
COMMENT ON COLUMN sys_chat_entity_message.read_ind IS '是否已读';
COMMENT ON COLUMN sys_chat_entity_message.delete_ind IS '是否已删';
COMMENT ON COLUMN sys_chat_entity_message.status IS '状态';
COMMENT ON COLUMN sys_chat_entity_message.version IS '版本号';
COMMENT ON COLUMN sys_chat_entity_message.active IS '启用状态';
COMMENT ON COLUMN sys_chat_entity_message.created_by IS '创建人';
COMMENT ON COLUMN sys_chat_entity_message.created_at IS '创建时间';
COMMENT ON COLUMN sys_chat_entity_message.updated_by IS '最后修改人';
COMMENT ON COLUMN sys_chat_entity_message.updated_at IS '最后修改时间';
COMMENT ON COLUMN sys_chat_entity_message.deleted_by IS '删除人';
COMMENT ON COLUMN sys_chat_entity_message.deleted_at IS '删除时间';

-- =====================================================================================================================
-- AI
-- =====================================================================================================================

--
-- 模型
--

DROP TABLE IF EXISTS sys_ai_model;

CREATE TABLE sys_ai_model
(
    id               BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id        BIGINT       NOT NULL DEFAULT 0,
    code             VARCHAR(150) NOT NULL DEFAULT '',
    title            VARCHAR(150) NOT NULL DEFAULT '',
    service_provider VARCHAR(150) NOT NULL DEFAULT '',
    model_provider   VARCHAR(150) NOT NULL DEFAULT '',
    model_type       VARCHAR(150) NOT NULL DEFAULT '',
    model_name       VARCHAR(150) NOT NULL DEFAULT '',
    api_key          VARCHAR(500) NOT NULL DEFAULT '',
    base_url         VARCHAR(500) NOT NULL DEFAULT '',
    variables        TEXT         NULL,
    description      VARCHAR(255) NOT NULL DEFAULT '',
    status           SMALLINT     NOT NULL DEFAULT 1,
    version          BIGINT       NOT NULL DEFAULT 0,
    active           SMALLINT     NOT NULL DEFAULT 1,
    created_by       BIGINT       NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by       BIGINT       NOT NULL DEFAULT 0,
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by       BIGINT       NOT NULL DEFAULT 0,
    deleted_at       TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_model__tenant_id ON sys_ai_model (tenant_id);
CREATE INDEX ix_sys_ai_model__biz_type ON sys_ai_model (code);

COMMENT ON TABLE sys_ai_model IS '模型';
COMMENT ON COLUMN sys_ai_model.id IS 'ID';
COMMENT ON COLUMN sys_ai_model.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_model.code IS '编号';
COMMENT ON COLUMN sys_ai_model.title IS '标题';
COMMENT ON COLUMN sys_ai_model.service_provider IS '服务提供商';
COMMENT ON COLUMN sys_ai_model.model_provider IS '模型提供商';
COMMENT ON COLUMN sys_ai_model.model_type IS '模型类型';
COMMENT ON COLUMN sys_ai_model.model_name IS '模型名称';
COMMENT ON COLUMN sys_ai_model.api_key IS 'API密钥';
COMMENT ON COLUMN sys_ai_model.base_url IS '基础URL';
COMMENT ON COLUMN sys_ai_model.variables IS '参数配置';
COMMENT ON COLUMN sys_ai_model.description IS '备注说明';
COMMENT ON COLUMN sys_ai_model.status IS '状态';
COMMENT ON COLUMN sys_ai_model.version IS '版本号';
COMMENT ON COLUMN sys_ai_model.active IS '启用状态';
COMMENT ON COLUMN sys_ai_model.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_model.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_model.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_model.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_model.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_model.deleted_at IS '删除时间';

--
-- MCP Server
--

DROP TABLE IF EXISTS sys_ai_mcp_server;

CREATE TABLE sys_ai_mcp_server
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    code        VARCHAR(150) NOT NULL DEFAULT '',
    title       VARCHAR(150) NOT NULL DEFAULT '',
    protocol    VARCHAR(150) NOT NULL DEFAULT '',
    url         VARCHAR(255) NOT NULL DEFAULT '',
    headers     TEXT         NULL     DEFAULT NULL,
    args        TEXT         NULL     DEFAULT NULL,
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_mcp_server__tenant_id ON sys_ai_mcp_server (tenant_id);
CREATE INDEX ix_sys_ai_mcp_server__biz_type ON sys_ai_mcp_server (code);

COMMENT ON TABLE sys_ai_mcp_server IS 'MCP Server';
COMMENT ON COLUMN sys_ai_mcp_server.id IS 'ID';
COMMENT ON COLUMN sys_ai_mcp_server.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_mcp_server.code IS '编号';
COMMENT ON COLUMN sys_ai_mcp_server.title IS '标题';
COMMENT ON COLUMN sys_ai_mcp_server.protocol IS '协议';
COMMENT ON COLUMN sys_ai_mcp_server.url IS '服务地址';
COMMENT ON COLUMN sys_ai_mcp_server.headers IS '请求头';
COMMENT ON COLUMN sys_ai_mcp_server.args IS '参数';
COMMENT ON COLUMN sys_ai_mcp_server.description IS '备注说明';
COMMENT ON COLUMN sys_ai_mcp_server.status IS '状态';
COMMENT ON COLUMN sys_ai_mcp_server.version IS '版本号';
COMMENT ON COLUMN sys_ai_mcp_server.active IS '启用状态';
COMMENT ON COLUMN sys_ai_mcp_server.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_mcp_server.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_mcp_server.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_mcp_server.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_mcp_server.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_mcp_server.deleted_at IS '删除时间';

--
-- 工具
--

DROP TABLE IF EXISTS sys_ai_tool;

CREATE TABLE sys_ai_tool
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    code        VARCHAR(150) NOT NULL DEFAULT '',
    title       VARCHAR(150) NOT NULL DEFAULT '',
    tool_name   VARCHAR(255) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_tool__tenant_id ON sys_ai_tool (tenant_id);
CREATE INDEX ix_sys_ai_tool__biz_type ON sys_ai_tool (code);

COMMENT ON TABLE sys_ai_tool IS '工具';
COMMENT ON COLUMN sys_ai_tool.id IS 'ID';
COMMENT ON COLUMN sys_ai_tool.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_tool.code IS '编号';
COMMENT ON COLUMN sys_ai_tool.title IS '标题';
COMMENT ON COLUMN sys_ai_tool.tool_name IS '工具名称';
COMMENT ON COLUMN sys_ai_tool.description IS '备注说明';
COMMENT ON COLUMN sys_ai_tool.status IS '状态';
COMMENT ON COLUMN sys_ai_tool.version IS '版本号';
COMMENT ON COLUMN sys_ai_tool.active IS '启用状态';
COMMENT ON COLUMN sys_ai_tool.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_tool.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_tool.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_tool.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_tool.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_tool.deleted_at IS '删除时间';

--
-- 智能体
--

DROP TABLE IF EXISTS sys_ai_agent;

CREATE TABLE sys_ai_agent
(
    id            BIGSERIAL     NOT NULL PRIMARY KEY,
    tenant_id     BIGINT        NOT NULL DEFAULT 0,
    code          VARCHAR(150)  NOT NULL DEFAULT '',
    title         VARCHAR(150)  NOT NULL DEFAULT '',
    details       VARCHAR(255)  NOT NULL DEFAULT '',
    greeting      VARCHAR(255)  NOT NULL DEFAULT '',
    prompt        TEXT          NULL,
    role_prompt   TEXT          NULL,
    system_prompt TEXT          NULL,
    temperature   NUMERIC(3, 2) NOT NULL DEFAULT 0.7,
    description   VARCHAR(255)  NOT NULL DEFAULT '',
    status        SMALLINT      NOT NULL DEFAULT 1,
    version       BIGINT        NOT NULL DEFAULT 0,
    active        SMALLINT      NOT NULL DEFAULT 1,
    created_by    BIGINT        NOT NULL DEFAULT 0,
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by    BIGINT        NOT NULL DEFAULT 0,
    updated_at    TIMESTAMP     NOT NULL DEFAULT NOW(),
    deleted_by    BIGINT        NOT NULL DEFAULT 0,
    deleted_at    TIMESTAMP     NULL
);

CREATE INDEX ix_sys_ai_agent__tenant_id ON sys_ai_agent (tenant_id);
CREATE INDEX ix_sys_ai_agent__biz_type ON sys_ai_agent (code);

COMMENT ON TABLE sys_ai_agent IS '智能体';
COMMENT ON COLUMN sys_ai_agent.id IS 'ID';
COMMENT ON COLUMN sys_ai_agent.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_agent.code IS '编号';
COMMENT ON COLUMN sys_ai_agent.title IS '标题';
COMMENT ON COLUMN sys_ai_agent.prompt IS '提示词';
COMMENT ON COLUMN sys_ai_agent.role_prompt IS '角色提示词';
COMMENT ON COLUMN sys_ai_agent.system_prompt IS '系统提示词';
COMMENT ON COLUMN sys_ai_agent.temperature IS '温度';
COMMENT ON COLUMN sys_ai_agent.description IS '备注说明';
COMMENT ON COLUMN sys_ai_agent.details IS '描述';
COMMENT ON COLUMN sys_ai_agent.status IS '状态';
COMMENT ON COLUMN sys_ai_agent.version IS '版本号';
COMMENT ON COLUMN sys_ai_agent.active IS '启用状态';
COMMENT ON COLUMN sys_ai_agent.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_agent.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_agent.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_agent.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_agent.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_agent.deleted_at IS '删除时间';

--
-- AI关联表
--

DROP TABLE IF EXISTS sys_ai_relation;

CREATE TABLE sys_ai_relation
(
    id         BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL DEFAULT 0,
    entity_id  BIGINT       NOT NULL DEFAULT 0,
    biz_type   VARCHAR(100) NOT NULL DEFAULT '',
    biz_id     BIGINT       NOT NULL DEFAULT 0,
    version    BIGINT       NOT NULL DEFAULT 0,
    active     SMALLINT     NOT NULL DEFAULT 1,
    created_by BIGINT       NOT NULL DEFAULT 0,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by BIGINT       NOT NULL DEFAULT 0,
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by BIGINT       NOT NULL DEFAULT 0,
    deleted_at TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_relation__tenant_id ON sys_ai_relation (tenant_id);
CREATE INDEX ix_sys_ai_relation__agent_id ON sys_ai_relation (entity_id);
CREATE INDEX ix_sys_ai_relation__biz ON sys_ai_relation (biz_type, biz_id);

COMMENT ON TABLE sys_ai_relation IS 'AI关联表';
COMMENT ON COLUMN sys_ai_relation.id IS 'ID';
COMMENT ON COLUMN sys_ai_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_relation.entity_id IS '实体ID';
COMMENT ON COLUMN sys_ai_relation.biz_type IS '业务类型';
COMMENT ON COLUMN sys_ai_relation.biz_id IS '业务ID';
COMMENT ON COLUMN sys_ai_relation.version IS '版本号';
COMMENT ON COLUMN sys_ai_relation.active IS '启用状态';
COMMENT ON COLUMN sys_ai_relation.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_relation.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_relation.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_relation.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_relation.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_relation.deleted_at IS '删除时间';

--
-- 知识库
--

DROP TABLE IF EXISTS sys_ai_kb;

CREATE TABLE sys_ai_kb
(
    id                   BIGSERIAL      NOT NULL PRIMARY KEY,
    tenant_id            BIGINT         NOT NULL DEFAULT 0,
    code                 VARCHAR(150)   NOT NULL DEFAULT '',
    title                VARCHAR(150)   NOT NULL DEFAULT '',
    details              VARCHAR(255)   NOT NULL DEFAULT '',
    description          VARCHAR(255)   NOT NULL DEFAULT '',
    collection_name      VARCHAR(255)   NOT NULL DEFAULT '',
    top_k                INT            NOT NULL DEFAULT 0,
    similarity_threshold DECIMAL(10, 2) NOT NULL DEFAULT 0,
    chunk_size           DECIMAL(10, 2) NOT NULL DEFAULT 1024,
    chunk_overlap        DECIMAL(10, 2) NOT NULL DEFAULT 256,
    metadata             TEXT           NULL,
    status               SMALLINT       NOT NULL DEFAULT 1,
    version              BIGINT         NOT NULL DEFAULT 0,
    active               SMALLINT       NOT NULL DEFAULT 1,
    created_by           BIGINT         NOT NULL DEFAULT 0,
    created_at           TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_by           BIGINT         NOT NULL DEFAULT 0,
    updated_at           TIMESTAMP      NOT NULL DEFAULT NOW(),
    deleted_by           BIGINT         NOT NULL DEFAULT 0,
    deleted_at           TIMESTAMP      NULL
);

CREATE INDEX ix_sys_ai_kb__tenant_id ON sys_ai_kb (tenant_id);
CREATE INDEX ix_sys_ai_kb__biz_type ON sys_ai_kb (code);

COMMENT ON TABLE sys_ai_kb IS '智能体';
COMMENT ON COLUMN sys_ai_kb.id IS 'ID';
COMMENT ON COLUMN sys_ai_kb.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_kb.code IS '编号';
COMMENT ON COLUMN sys_ai_kb.title IS '标题';
COMMENT ON COLUMN sys_ai_kb.description IS '备注说明';
COMMENT ON COLUMN sys_ai_kb.details IS '描述';
COMMENT ON COLUMN sys_ai_kb.collection_name IS '向量集合名称';
COMMENT ON COLUMN sys_ai_kb.top_k IS '单次召回数量';
COMMENT ON COLUMN sys_ai_kb.similarity_threshold IS '相似度阈值';
COMMENT ON COLUMN sys_ai_kb.chunk_size IS '分片大小';
COMMENT ON COLUMN sys_ai_kb.chunk_overlap IS '分片重叠';
COMMENT ON COLUMN sys_ai_kb.metadata IS '元数据';
COMMENT ON COLUMN sys_ai_kb.status IS '状态';
COMMENT ON COLUMN sys_ai_kb.version IS '版本号';
COMMENT ON COLUMN sys_ai_kb.active IS '启用状态';
COMMENT ON COLUMN sys_ai_kb.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_kb.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_kb.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_kb.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_kb.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_kb.deleted_at IS '删除时间';

--
-- 知识库知识条目
--

DROP TABLE IF EXISTS sys_ai_kb_item;

CREATE TABLE sys_ai_kb_item
(
    id             BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id      BIGINT       NOT NULL DEFAULT 0,
    kb_id          BIGINT       NOT NULL DEFAULT 0,
    biz_type       VARCHAR(100) NOT NULL DEFAULT '',
    biz_id         BIGINT       NOT NULL DEFAULT 0,
    type           VARCHAR(100) NOT NULL DEFAULT '',
    title          VARCHAR(100) NOT NULL DEFAULT '',
    question       TEXT         NULL,
    answer         TEXT         NULL,
    content        TEXT         NULL,
    content_type   VARCHAR(100) NOT NULL DEFAULT '',
    content_hash   VARCHAR(255) NOT NULL DEFAULT '',
    content_size   BIGINT       NOT NULL DEFAULT 0,
    chunk_strategy VARCHAR(100) NOT NULL DEFAULT '',
    metadata       TEXT         NULL,
    extra          TEXT         NULL,
    vectorized     SMALLINT     NOT NULL DEFAULT 0,
    status         SMALLINT     NOT NULL DEFAULT 1,
    version        BIGINT       NOT NULL DEFAULT 0,
    active         SMALLINT     NOT NULL DEFAULT 1,
    created_by     BIGINT       NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by     BIGINT       NOT NULL DEFAULT 0,
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by     BIGINT       NOT NULL DEFAULT 0,
    deleted_at     TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_kb_item__tenant_id ON sys_ai_kb_item (tenant_id);
CREATE INDEX ix_sys_ai_kb_item__kb_id ON sys_ai_kb_item (kb_id);
CREATE INDEX ix_sys_ai_kb_item__biz_type ON sys_ai_kb_item (biz_type);

COMMENT ON TABLE sys_ai_kb_item IS '知识库知识条目';
COMMENT ON COLUMN sys_ai_kb_item.id IS 'ID';
COMMENT ON COLUMN sys_ai_kb_item.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_kb_item.kb_id IS '知识库ID';
COMMENT ON COLUMN sys_ai_kb_item.biz_type IS '业务类型';
COMMENT ON COLUMN sys_ai_kb_item.biz_id IS '业务ID';
COMMENT ON COLUMN sys_ai_kb_item.type IS '类型';
COMMENT ON COLUMN sys_ai_kb_item.title IS '标题';
COMMENT ON COLUMN sys_ai_kb_item.question IS '问题';
COMMENT ON COLUMN sys_ai_kb_item.answer IS '答案';
COMMENT ON COLUMN sys_ai_kb_item.content IS '原始内容';
COMMENT ON COLUMN sys_ai_kb_item.content_type IS '内容类型';
COMMENT ON COLUMN sys_ai_kb_item.content_size IS '内容大小';
COMMENT ON COLUMN sys_ai_kb_item.content_hash IS '内容哈希，用于去重或变更检测';
COMMENT ON COLUMN sys_ai_kb_item.chunk_strategy IS '分片策略（TOKEN/RECURSIVE/MARKDOWN）';
COMMENT ON COLUMN sys_ai_kb_item.metadata IS '扩展元数据';
COMMENT ON COLUMN sys_ai_kb_item.extra IS '附加数据';
COMMENT ON COLUMN sys_ai_kb_item.vectorized IS '是否已向量化';
COMMENT ON COLUMN sys_ai_kb_item.status IS '状态';
COMMENT ON COLUMN sys_ai_kb_item.version IS '版本号';
COMMENT ON COLUMN sys_ai_kb_item.active IS '启用状态';
COMMENT ON COLUMN sys_ai_kb_item.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_kb_item.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_kb_item.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_kb_item.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_kb_item.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_kb_item.deleted_at IS '删除时间';

--
-- 知识库知识分片
--

DROP TABLE IF EXISTS sys_ai_kb_chunk;

CREATE TABLE sys_ai_kb_chunk
(
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    kb_id           BIGINT       NOT NULL DEFAULT 0,
    kb_item_id      BIGINT       NOT NULL DEFAULT 0,
    biz_type        VARCHAR(100) NOT NULL DEFAULT '',
    title           VARCHAR(100) NOT NULL DEFAULT '',
    content         TEXT         NULL,
    content_type    VARCHAR(100) NOT NULL DEFAULT '',
    content_size    BIGINT       NOT NULL DEFAULT 0,
    content_hash    VARCHAR(255) NOT NULL DEFAULT '',
    chunk_index     INT          NOT NULL DEFAULT 0,
    chunk_total     INT          NOT NULL DEFAULT 0,
    chunk_strategy  VARCHAR(100) NOT NULL DEFAULT '',
    start_index     INT          NOT NULL DEFAULT 0,
    end_index       INT          NOT NULL DEFAULT 0,
    embedding_model VARCHAR(255) NOT NULL DEFAULT '',
    vector_doc_id   VARCHAR(255) NOT NULL DEFAULT '',
    vectorized      SMALLINT     NOT NULL DEFAULT 0,
    metadata        TEXT         NULL,
    status          SMALLINT     NOT NULL DEFAULT 1,
    version         BIGINT       NOT NULL DEFAULT 0,
    active          SMALLINT     NOT NULL DEFAULT 1,
    created_by      BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      BIGINT       NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by      BIGINT       NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_kb_chunk__tenant_id ON sys_ai_kb_chunk (tenant_id);
CREATE INDEX ix_sys_ai_kb_chunk__kb_id ON sys_ai_kb_chunk (kb_id);
CREATE INDEX ix_sys_ai_kb_chunk__kb_item_id ON sys_ai_kb_chunk (kb_item_id);
CREATE INDEX ix_sys_ai_kb_chunk__biz_type ON sys_ai_kb_chunk (biz_type);
CREATE INDEX ix_sys_ai_kb_chunk__vector_doc ON sys_ai_kb_chunk (vector_doc_id);

COMMENT ON TABLE sys_ai_kb_chunk IS '知识库知识分片';
COMMENT ON COLUMN sys_ai_kb_chunk.id IS 'ID';
COMMENT ON COLUMN sys_ai_kb_chunk.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_kb_chunk.kb_id IS '知识库ID';
COMMENT ON COLUMN sys_ai_kb_chunk.kb_item_id IS '所属知识库条目（文档）ID';
COMMENT ON COLUMN sys_ai_kb_chunk.biz_type IS '业务类型';
COMMENT ON COLUMN sys_ai_kb_chunk.title IS '标题';
COMMENT ON COLUMN sys_ai_kb_chunk.content IS '分片文本内容';
COMMENT ON COLUMN sys_ai_kb_chunk.content_type IS '内容类型';
COMMENT ON COLUMN sys_ai_kb_chunk.content_size IS '分片文本大小（字符/字节）';
COMMENT ON COLUMN sys_ai_kb_chunk.content_hash IS '内容哈希，用于去重或变更检测';
COMMENT ON COLUMN sys_ai_kb_chunk.chunk_index IS '分片序号（同一文档内从0递增）';
COMMENT ON COLUMN sys_ai_kb_chunk.chunk_total IS '所属文档总分片数';
COMMENT ON COLUMN sys_ai_kb_chunk.chunk_strategy IS '切分策略（token/character/recursive/markdown/sentence）';
COMMENT ON COLUMN sys_ai_kb_chunk.start_index IS '分片在原文中的起始位置';
COMMENT ON COLUMN sys_ai_kb_chunk.end_index IS '分片在原文中的结束位置';
COMMENT ON COLUMN sys_ai_kb_chunk.embedding_model IS '生成向量的嵌入模型标识';
COMMENT ON COLUMN sys_ai_kb_chunk.vector_doc_id IS '向量库中对应的向量文档ID（ES 向量集合内）';
COMMENT ON COLUMN sys_ai_kb_chunk.vectorized IS '是否已向量化入库';
COMMENT ON COLUMN sys_ai_kb_chunk.metadata IS '分片元数据';
COMMENT ON COLUMN sys_ai_kb_chunk.status IS '状态';
COMMENT ON COLUMN sys_ai_kb_chunk.version IS '版本号';
COMMENT ON COLUMN sys_ai_kb_chunk.active IS '启用状态';
COMMENT ON COLUMN sys_ai_kb_chunk.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_kb_chunk.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_kb_chunk.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_kb_chunk.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_kb_chunk.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_kb_chunk.deleted_at IS '删除时间';

--
-- 向量化任务
--

DROP TABLE IF EXISTS sys_ai_kb_task;

CREATE TABLE sys_ai_kb_task
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    kb_id       BIGINT       NOT NULL DEFAULT 0,
    kb_item_id  BIGINT       NOT NULL DEFAULT 0,
    task_id     VARCHAR(150) NOT NULL DEFAULT '',
    task_type   VARCHAR(50)  NOT NULL DEFAULT '',
    progress    INT          NOT NULL DEFAULT 0,
    token_usage BIGINT       NOT NULL DEFAULT 0,
    exception   TEXT         NULL,
    status      SMALLINT     NOT NULL DEFAULT 1,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_kb_task__tenant_id ON sys_ai_kb_task (tenant_id);
CREATE INDEX ix_sys_ai_kb_task__kb_id ON sys_ai_kb_task (kb_id);
CREATE INDEX ix_sys_ai_kb_task__kb_item_id ON sys_ai_kb_task (kb_item_id);
CREATE INDEX ix_sys_ai_kb_task__status ON sys_ai_kb_task (status);

COMMENT ON TABLE sys_ai_kb_task IS '知识库向量化任务';
COMMENT ON COLUMN sys_ai_kb_task.id IS 'ID';
COMMENT ON COLUMN sys_ai_kb_task.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_kb_task.kb_id IS '知识库ID';
COMMENT ON COLUMN sys_ai_kb_task.kb_item_id IS '知识条目ID';
COMMENT ON COLUMN sys_ai_kb_task.task_id IS '任务标识';
COMMENT ON COLUMN sys_ai_kb_task.task_type IS '任务类型（ITEM/REBUILD）';
COMMENT ON COLUMN sys_ai_kb_task.progress IS '进度';
COMMENT ON COLUMN sys_ai_kb_task.token_usage IS '消耗词元';
COMMENT ON COLUMN sys_ai_kb_task.exception IS '异常信息';
COMMENT ON COLUMN sys_ai_kb_task.status IS '状态';
COMMENT ON COLUMN sys_ai_kb_task.version IS '版本号';
COMMENT ON COLUMN sys_ai_kb_task.active IS '启用状态';
COMMENT ON COLUMN sys_ai_kb_task.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_kb_task.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_kb_task.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_kb_task.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_kb_task.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_kb_task.deleted_at IS '删除时间';

--
-- AI API Key
--

DROP TABLE IF EXISTS sys_ai_api_key;

CREATE TABLE sys_ai_api_key
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    biz_type    VARCHAR(150) NOT NULL DEFAULT '',
    app_id      VARCHAR(150) NOT NULL DEFAULT '',
    app_name    VARCHAR(150) NOT NULL DEFAULT '',
    app_secret  VARCHAR(500) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_api_key__tenant_id ON sys_ai_api_key (tenant_id);
CREATE INDEX ix_sys_ai_api_key__apikey ON sys_ai_api_key (app_id);

COMMENT ON TABLE sys_ai_api_key IS 'AI API Key';
COMMENT ON COLUMN sys_ai_api_key.id IS 'ID';
COMMENT ON COLUMN sys_ai_api_key.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_api_key.biz_type IS '业务类型';
COMMENT ON COLUMN sys_ai_api_key.app_id IS 'App ID';
COMMENT ON COLUMN sys_ai_api_key.app_name IS 'App Name';
COMMENT ON COLUMN sys_ai_api_key.app_secret IS 'App Secret';
COMMENT ON COLUMN sys_ai_api_key.description IS '描述';
COMMENT ON COLUMN sys_ai_api_key.status IS '状态';
COMMENT ON COLUMN sys_ai_api_key.version IS '版本号';
COMMENT ON COLUMN sys_ai_api_key.active IS '启用状态';
COMMENT ON COLUMN sys_ai_api_key.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_api_key.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_api_key.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_api_key.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_api_key.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_api_key.deleted_at IS '删除时间';

--
-- AI Session
--

DROP TABLE IF EXISTS sys_ai_session;

CREATE TABLE sys_ai_session
(
    id            BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id     BIGINT       NOT NULL DEFAULT 0,
    user_id       VARCHAR(150) NOT NULL DEFAULT '',
    session_id    VARCHAR(150) NOT NULL DEFAULT '',
    metadata      TEXT         NULL,
    event_version BIGINT       NOT NULL DEFAULT 0,
    expires_at    TIMESTAMP    NULL,
    version       BIGINT       NOT NULL DEFAULT 0,
    active        SMALLINT     NOT NULL DEFAULT 1,
    created_by    BIGINT       NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_ai_session__tenant_id ON sys_ai_session (tenant_id);
CREATE INDEX ix_sys_ai_session__user_id ON sys_ai_session (user_id);
CREATE INDEX ix_sys_ai_session__expires_at ON sys_ai_session (expires_at);

COMMENT ON TABLE sys_ai_session IS 'AI Session';
COMMENT ON COLUMN sys_ai_session.id IS 'ID';
COMMENT ON COLUMN sys_ai_session.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_session.user_id IS '用户ID';
COMMENT ON COLUMN sys_ai_session.metadata IS 'Metadata';
COMMENT ON COLUMN sys_ai_session.event_version IS 'Event Version';
COMMENT ON COLUMN sys_ai_session.expires_at IS '过期时间';
COMMENT ON COLUMN sys_ai_session.active IS '启用状态';
COMMENT ON COLUMN sys_ai_session.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_session.created_at IS '创建时间';

--
-- AI Session Event
--

DROP TABLE IF EXISTS sys_ai_session_event;

CREATE TABLE sys_ai_session_event
(
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    session_id      VARCHAR(150) NOT NULL DEFAULT '',
    timestamp       TIMESTAMP    NOT NULL,
    message_type    varchar(20)  NOT NULL,
    message_content TEXT         NULL,
    message_data    TEXT         NULL,
    synthetic       SMALLINT     NOT NULL DEFAULT 0,
    branch          VARCHAR(500) NULL,
    archived        SMALLINT     NOT NULL DEFAULT 0,
    metadata        TEXT         NULL,
    version         BIGINT       NOT NULL DEFAULT 0,
    active          SMALLINT     NOT NULL DEFAULT 1,
    created_by      BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX ix_sys_ai_session_event__tenant_id ON sys_ai_session_event (tenant_id);
CREATE INDEX ix_sys_ai_session_event__session ON sys_ai_session_event (session_id, timestamp);
CREATE INDEX ix_sys_ai_session_event__archived ON sys_ai_session_event (session_id, archived);

COMMENT ON TABLE sys_ai_session_event IS 'AI Session Event';
COMMENT ON COLUMN sys_ai_session_event.id IS 'ID';
COMMENT ON COLUMN sys_ai_session_event.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_session_event.session_id IS '对话ID';
COMMENT ON COLUMN sys_ai_session_event.timestamp IS 'Timestamp';
COMMENT ON COLUMN sys_ai_session_event.message_type IS '消息类型';
COMMENT ON COLUMN sys_ai_session_event.message_content IS '消息内容';
COMMENT ON COLUMN sys_ai_session_event.message_data IS '消息数据';
COMMENT ON COLUMN sys_ai_session_event.synthetic IS 'synthetic';
COMMENT ON COLUMN sys_ai_session_event.archived IS '是否归档';
COMMENT ON COLUMN sys_ai_session_event.metadata IS 'metadata';
COMMENT ON COLUMN sys_ai_session_event.branch IS 'branch';
COMMENT ON COLUMN sys_ai_session_event.active IS '启用状态';
COMMENT ON COLUMN sys_ai_session_event.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_session_event.created_at IS '创建时间';

--
-- 对话记录
--

DROP TABLE IF EXISTS sys_ai_chat_memory;

CREATE TABLE sys_ai_chat_memory
(
    id              BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    user_id         BIGINT       NOT NULL DEFAULT 0,
    conversation_id VARCHAR(100) NOT NULL DEFAULT '',
    content         TEXT         NULL,
    type            VARCHAR(100) NOT NULL DEFAULT '',
    chat_type       VARCHAR(100) NOT NULL DEFAULT '',
    agent_code      VARCHAR(100) NOT NULL DEFAULT '',
    version         BIGINT       NOT NULL DEFAULT 0,
    active          SMALLINT     NOT NULL DEFAULT 1,
    created_by      BIGINT       NOT NULL DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by      BIGINT       NOT NULL DEFAULT 0,
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by      BIGINT       NOT NULL DEFAULT 0,
    deleted_at      TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_chat_memory__tenant_id ON sys_ai_chat_memory (tenant_id);
CREATE INDEX ix_sys_ai_chat_memory__conversation_id ON sys_ai_chat_memory (conversation_id);
CREATE INDEX ix_sys_ai_chat_memory__user_id ON sys_ai_chat_memory (user_id);

COMMENT ON TABLE sys_ai_chat_memory IS '对话记录';
COMMENT ON COLUMN sys_ai_chat_memory.id IS 'ID';
COMMENT ON COLUMN sys_ai_chat_memory.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_chat_memory.user_id IS '用户ID';
COMMENT ON COLUMN sys_ai_chat_memory.conversation_id IS '会话标识';
COMMENT ON COLUMN sys_ai_chat_memory.content IS '对话内容';
COMMENT ON COLUMN sys_ai_chat_memory.type IS '对话类型';
COMMENT ON COLUMN sys_ai_chat_memory.chat_type IS '聊天类型';
COMMENT ON COLUMN sys_ai_chat_memory.agent_code IS '智能体编号';
COMMENT ON COLUMN sys_ai_chat_memory.version IS '版本号';
COMMENT ON COLUMN sys_ai_chat_memory.active IS '启用状态';
COMMENT ON COLUMN sys_ai_chat_memory.created_by IS '创建人';
COMMENT ON COLUMN sys_ai_chat_memory.created_at IS '创建时间';
COMMENT ON COLUMN sys_ai_chat_memory.updated_by IS '修改人';
COMMENT ON COLUMN sys_ai_chat_memory.updated_at IS '修改时间';
COMMENT ON COLUMN sys_ai_chat_memory.deleted_by IS '删除人';
COMMENT ON COLUMN sys_ai_chat_memory.deleted_at IS '删除时间';

--
-- AI 用量统计
--

DROP TABLE IF EXISTS sys_ai_usage;

CREATE TABLE sys_ai_usage
(
    id                BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id         BIGINT       NOT NULL DEFAULT 0,
    user_id           BIGINT       NOT NULL DEFAULT 0,
    usage_type        VARCHAR(50)  NOT NULL DEFAULT '',
    model_name        VARCHAR(255) NOT NULL DEFAULT '',
    kb_id             BIGINT       NOT NULL DEFAULT 0,
    conversation_id   VARCHAR(64)  NOT NULL DEFAULT '',
    prompt_tokens     INT          NOT NULL DEFAULT 0,
    completion_tokens INT          NOT NULL DEFAULT 0,
    total_tokens      INT          NOT NULL DEFAULT 0,
    call_count        INT          NOT NULL DEFAULT 0,
    version           BIGINT       NOT NULL DEFAULT 0,
    active            SMALLINT     NOT NULL DEFAULT 1,
    created_by        BIGINT       NOT NULL DEFAULT 0,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by        BIGINT       NOT NULL DEFAULT 0,
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by        BIGINT       NOT NULL DEFAULT 0,
    deleted_at        TIMESTAMP    NULL
);

CREATE INDEX ix_sys_ai_usage__tenant_id ON sys_ai_usage (tenant_id);
CREATE INDEX ix_sys_ai_usage__usage_type ON sys_ai_usage (usage_type);
CREATE INDEX ix_sys_ai_usage__kb_id ON sys_ai_usage (kb_id);
CREATE INDEX ix_sys_ai_usage__user_id ON sys_ai_usage (user_id);

COMMENT ON TABLE sys_ai_usage IS 'AI用量统计';
COMMENT ON COLUMN sys_ai_usage.id IS 'ID';
COMMENT ON COLUMN sys_ai_usage.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_ai_usage.user_id IS '用户ID';
COMMENT ON COLUMN sys_ai_usage.usage_type IS '用量类型（CHAT/SEARCH/EMBEDDING/RERANK）';
COMMENT ON COLUMN sys_ai_usage.model_name IS '模型名称';
COMMENT ON COLUMN sys_ai_usage.kb_id IS '知识库ID';
COMMENT ON COLUMN sys_ai_usage.conversation_id IS '会话ID';
COMMENT ON COLUMN sys_ai_usage.prompt_tokens IS '输入token';
COMMENT ON COLUMN sys_ai_usage.completion_tokens IS '输出token';
COMMENT ON COLUMN sys_ai_usage.total_tokens IS '总token';
COMMENT ON COLUMN sys_ai_usage.call_count IS '调用次数';

-- =====================================================================================================================
-- 开放平台
-- =====================================================================================================================

--
-- 微信公众号
--

DROP TABLE IF EXISTS sys_open_wxmp_app;

CREATE TABLE sys_open_wxmp_app
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    title       VARCHAR(100) NOT NULL DEFAULT '',
    app_id      VARCHAR(100) NOT NULL DEFAULT '',
    app_secret  VARCHAR(200) NOT NULL DEFAULT '',
    app_token   VARCHAR(200) NOT NULL DEFAULT '',
    app_aes_key VARCHAR(200) NOT NULL DEFAULT '',
    app_mp_id   VARCHAR(200) NOT NULL DEFAULT '',
    app_wx_id   VARCHAR(200) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    source      SMALLINT     NOT NULL DEFAULT 2,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

COMMENT ON TABLE sys_open_wxmp_app IS '微信公众号应用表';
COMMENT ON COLUMN sys_open_wxmp_app.id IS 'ID';
COMMENT ON COLUMN sys_open_wxmp_app.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_open_wxmp_app.title IS '公众号名称';
COMMENT ON COLUMN sys_open_wxmp_app.app_id IS 'AppID';
COMMENT ON COLUMN sys_open_wxmp_app.app_secret IS 'Token';
COMMENT ON COLUMN sys_open_wxmp_app.app_token IS 'Token';
COMMENT ON COLUMN sys_open_wxmp_app.app_aes_key IS 'EncodingAESKey';
COMMENT ON COLUMN sys_open_wxmp_app.app_mp_id IS 'MPID';
COMMENT ON COLUMN sys_open_wxmp_app.app_wx_id IS 'WXID';
COMMENT ON COLUMN sys_open_wxmp_app.description IS '备注';
COMMENT ON COLUMN sys_open_wxmp_app.status IS '状态';
COMMENT ON COLUMN sys_open_wxmp_app.source IS '来源';
COMMENT ON COLUMN sys_open_wxmp_app.version IS '版本号';
COMMENT ON COLUMN sys_open_wxmp_app.active IS '启用状态';
COMMENT ON COLUMN sys_open_wxmp_app.created_by IS '创建人';
COMMENT ON COLUMN sys_open_wxmp_app.created_at IS '创建时间';
COMMENT ON COLUMN sys_open_wxmp_app.updated_by IS '修改人';
COMMENT ON COLUMN sys_open_wxmp_app.updated_at IS '修改时间';
COMMENT ON COLUMN sys_open_wxmp_app.deleted_by IS '删除人';
COMMENT ON COLUMN sys_open_wxmp_app.deleted_at IS '删除时间';

--
-- 微信小程序
--

DROP TABLE IF EXISTS sys_open_wxma_app;

CREATE TABLE sys_open_wxma_app
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    title       VARCHAR(100) NOT NULL DEFAULT '',
    app_id      VARCHAR(100) NOT NULL DEFAULT '',
    app_secret  VARCHAR(200) NOT NULL DEFAULT '',
    app_token   VARCHAR(200) NOT NULL DEFAULT '',
    app_aes_key VARCHAR(200) NOT NULL DEFAULT '',
    app_wx_id   VARCHAR(200) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    source      SMALLINT     NOT NULL DEFAULT 2,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

COMMENT ON TABLE sys_open_wxma_app IS '微信公众号应用表';
COMMENT ON COLUMN sys_open_wxma_app.id IS 'ID';
COMMENT ON COLUMN sys_open_wxma_app.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_open_wxma_app.title IS '公众号名称';
COMMENT ON COLUMN sys_open_wxma_app.app_id IS 'AppID';
COMMENT ON COLUMN sys_open_wxma_app.app_secret IS 'Token';
COMMENT ON COLUMN sys_open_wxma_app.app_token IS 'Token';
COMMENT ON COLUMN sys_open_wxma_app.app_aes_key IS 'EncodingAESKey';
COMMENT ON COLUMN sys_open_wxma_app.app_wx_id IS 'WXID';
COMMENT ON COLUMN sys_open_wxma_app.description IS '备注';
COMMENT ON COLUMN sys_open_wxma_app.status IS '状态';
COMMENT ON COLUMN sys_open_wxma_app.source IS '来源';
COMMENT ON COLUMN sys_open_wxma_app.version IS '版本号';
COMMENT ON COLUMN sys_open_wxma_app.active IS '启用状态';
COMMENT ON COLUMN sys_open_wxma_app.created_by IS '创建人';
COMMENT ON COLUMN sys_open_wxma_app.created_at IS '创建时间';
COMMENT ON COLUMN sys_open_wxma_app.updated_by IS '修改人';
COMMENT ON COLUMN sys_open_wxma_app.updated_at IS '修改时间';
COMMENT ON COLUMN sys_open_wxma_app.deleted_by IS '删除人';
COMMENT ON COLUMN sys_open_wxma_app.deleted_at IS '删除时间';

--
-- 企业微信
--

DROP TABLE IF EXISTS sys_open_wxcp_app;

CREATE TABLE sys_open_wxcp_app
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    title       VARCHAR(100) NOT NULL DEFAULT '',
    app_id      VARCHAR(100) NOT NULL DEFAULT '',
    app_secret  VARCHAR(200) NOT NULL DEFAULT '',
    app_token   VARCHAR(200) NOT NULL DEFAULT '',
    app_aes_key VARCHAR(200) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    source      SMALLINT     NOT NULL DEFAULT 2,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

COMMENT ON TABLE sys_open_wxcp_app IS '企业微信应用表';
COMMENT ON COLUMN sys_open_wxcp_app.id IS 'ID';
COMMENT ON COLUMN sys_open_wxcp_app.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_open_wxcp_app.title IS '公众号名称';
COMMENT ON COLUMN sys_open_wxcp_app.app_id IS 'AppID';
COMMENT ON COLUMN sys_open_wxcp_app.app_secret IS 'Token';
COMMENT ON COLUMN sys_open_wxcp_app.app_token IS 'Token';
COMMENT ON COLUMN sys_open_wxcp_app.app_aes_key IS 'EncodingAESKey';
COMMENT ON COLUMN sys_open_wxcp_app.description IS '备注';
COMMENT ON COLUMN sys_open_wxcp_app.status IS '状态';
COMMENT ON COLUMN sys_open_wxcp_app.source IS '来源';
COMMENT ON COLUMN sys_open_wxcp_app.version IS '版本号';
COMMENT ON COLUMN sys_open_wxcp_app.active IS '启用状态';
COMMENT ON COLUMN sys_open_wxcp_app.created_by IS '创建人';
COMMENT ON COLUMN sys_open_wxcp_app.created_at IS '创建时间';
COMMENT ON COLUMN sys_open_wxcp_app.updated_by IS '修改人';
COMMENT ON COLUMN sys_open_wxcp_app.updated_at IS '修改时间';
COMMENT ON COLUMN sys_open_wxcp_app.deleted_by IS '删除人';
COMMENT ON COLUMN sys_open_wxcp_app.deleted_at IS '删除时间';

--
-- 飞书
--

DROP TABLE IF EXISTS sys_open_lark_app;

CREATE TABLE sys_open_lark_app
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    title       VARCHAR(100) NOT NULL DEFAULT '',
    app_id      VARCHAR(100) NOT NULL DEFAULT '',
    app_secret  VARCHAR(200) NOT NULL DEFAULT '',
    app_token   VARCHAR(200) NOT NULL DEFAULT '',
    app_aes_key VARCHAR(200) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    source      SMALLINT     NOT NULL DEFAULT 2,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

COMMENT ON TABLE sys_open_lark_app IS '飞书应用表';
COMMENT ON COLUMN sys_open_lark_app.id IS 'ID';
COMMENT ON COLUMN sys_open_lark_app.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_open_lark_app.title IS '公众号名称';
COMMENT ON COLUMN sys_open_lark_app.app_id IS 'AppID';
COMMENT ON COLUMN sys_open_lark_app.app_secret IS 'Token';
COMMENT ON COLUMN sys_open_lark_app.app_token IS 'Token';
COMMENT ON COLUMN sys_open_lark_app.app_aes_key IS 'EncodingAESKey';
COMMENT ON COLUMN sys_open_lark_app.description IS '备注';
COMMENT ON COLUMN sys_open_lark_app.status IS '状态';
COMMENT ON COLUMN sys_open_lark_app.source IS '来源';
COMMENT ON COLUMN sys_open_lark_app.version IS '版本号';
COMMENT ON COLUMN sys_open_lark_app.active IS '启用状态';
COMMENT ON COLUMN sys_open_lark_app.created_by IS '创建人';
COMMENT ON COLUMN sys_open_lark_app.created_at IS '创建时间';
COMMENT ON COLUMN sys_open_lark_app.updated_by IS '修改人';
COMMENT ON COLUMN sys_open_lark_app.updated_at IS '修改时间';
COMMENT ON COLUMN sys_open_lark_app.deleted_by IS '删除人';
COMMENT ON COLUMN sys_open_lark_app.deleted_at IS '删除时间';

--
-- 钉钉
--

DROP TABLE IF EXISTS sys_open_dingtalk_app;

CREATE TABLE sys_open_dingtalk_app
(
    id          BIGSERIAL    NOT NULL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL DEFAULT 0,
    title       VARCHAR(100) NOT NULL DEFAULT '',
    app_id      VARCHAR(100) NOT NULL DEFAULT '',
    app_secret  VARCHAR(200) NOT NULL DEFAULT '',
    app_token   VARCHAR(200) NOT NULL DEFAULT '',
    app_aes_key VARCHAR(200) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    status      SMALLINT     NOT NULL DEFAULT 1,
    source      SMALLINT     NOT NULL DEFAULT 2,
    version     BIGINT       NOT NULL DEFAULT 0,
    active      SMALLINT     NOT NULL DEFAULT 1,
    created_by  BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_by  BIGINT       NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    deleted_by  BIGINT       NOT NULL DEFAULT 0,
    deleted_at  TIMESTAMP    NULL
);

COMMENT ON TABLE sys_open_dingtalk_app IS '钉钉应用表';
COMMENT ON COLUMN sys_open_dingtalk_app.id IS 'ID';
COMMENT ON COLUMN sys_open_dingtalk_app.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_open_dingtalk_app.title IS '公众号名称';
COMMENT ON COLUMN sys_open_dingtalk_app.app_id IS 'AppID';
COMMENT ON COLUMN sys_open_dingtalk_app.app_secret IS 'Token';
COMMENT ON COLUMN sys_open_dingtalk_app.app_token IS 'Token';
COMMENT ON COLUMN sys_open_dingtalk_app.app_aes_key IS 'EncodingAESKey';
COMMENT ON COLUMN sys_open_dingtalk_app.description IS '备注';
COMMENT ON COLUMN sys_open_dingtalk_app.status IS '状态';
COMMENT ON COLUMN sys_open_dingtalk_app.source IS '来源';
COMMENT ON COLUMN sys_open_dingtalk_app.version IS '版本号';
COMMENT ON COLUMN sys_open_dingtalk_app.active IS '启用状态';
COMMENT ON COLUMN sys_open_dingtalk_app.created_by IS '创建人';
COMMENT ON COLUMN sys_open_dingtalk_app.created_at IS '创建时间';
COMMENT ON COLUMN sys_open_dingtalk_app.updated_by IS '修改人';
COMMENT ON COLUMN sys_open_dingtalk_app.updated_at IS '修改时间';
COMMENT ON COLUMN sys_open_dingtalk_app.deleted_by IS '删除人';
COMMENT ON COLUMN sys_open_dingtalk_app.deleted_at IS '删除时间';
