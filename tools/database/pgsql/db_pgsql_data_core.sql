-- =====================================================================================================================
-- 核心基础数据
-- =====================================================================================================================

\c one_platform;

-- =====================================================================================================================
-- 实体数据
-- 1000000 - 租户 Tenant
-- 1001000 - 身份 Identity
-- 1002001 - 用户 User
-- 1003000 - 角色 Role
-- 1004000 - 部门 Organization
-- 1005000 - 岗位 Position
-- 1006000 - 职级 Level
-- 1007000 - 群组 Group
-- =====================================================================================================================

--
-- 内置租户
--

truncate sys_tenant;

insert into sys_tenant (id, code, title, account_count, domain, description, source, root_ind)
values (1000001, 'ROOT', 'Root', 0, 'wdev.cc', '默认租户', 1, 1),
       (1000002, 'DEMO', 'Demo', 0, 'z.wdev.cc', '演示租户', 1, 0);

--
-- 内置账号
--

truncate sys_identity;

insert into sys_identity (id, uuid, source)
values (1001001, '1001001', 1),
       (1001002, '1001002', 1);

truncate sys_user;

insert into sys_user (id, tenant_id, uuid, username, email, mobile_country_code, mobile_number, display_name, password, source)
values (1002001, 1000001, 0, 'admin', 'ee@elvea.cn', '86', '13500000000', 'Administrator',
        '{bcrypt}$2a$10$z/deaEDovvc7n9q36xGOpun2sOPL75kPEVz9meaxlMoAjJhZdqEci', 1),
       (1002002, 1000001, 0, 'test', 'me@elvea.cn', '86', '13500000000', 'User',
        '{bcrypt}$2a$10$0KT8RoEjGUhs9aatKjMniOh6DrkF4nhZRLUd5Sl8tHiXwZqr3CMtW', 1);

insert into sys_user (id, tenant_id, uuid, username, email, mobile_country_code, mobile_number, display_name, password, source)
values (1002003, 1000002, 0, 'admin', 'ee@elvea.cn', '86', '13500000000', 'Administrator',
        '{bcrypt}$2a$10$z/deaEDovvc7n9q36xGOpun2sOPL75kPEVz9meaxlMoAjJhZdqEci', 1),
       (1002004, 1000002, 0, 'test', 'me@elvea.cn', '86', '13500000000', 'User',
        '{bcrypt}$2a$10$0KT8RoEjGUhs9aatKjMniOh6DrkF4nhZRLUd5Sl8tHiXwZqr3CMtW', 1);

--
-- 顶层组织架构
--

truncate sys_organization;

insert into sys_organization (id, tenant_id, parent_id, code, label, title, root_ind, default_ind, source)
values (1004001, 1000001, 0, 'ROOT_ORG', 'label_top_organization', 'All Organization', 1, 1, 1);

insert into sys_organization (id, tenant_id, parent_id, code, label, title, root_ind, default_ind, source)
values (1004002, 1000002, 0, 'ROOT_ORG', 'label_top_organization', 'All Organization', 1, 1, 1);

--
-- 顶层岗位
--

truncate sys_position;

insert into sys_position (id, tenant_id, parent_id, code, label, title, root_ind, default_ind, source)
values (1005001, 1000001, 0, 'ROOT_PST', 'label_top_position', 'All Position', 1, 1, 1);

insert into sys_position (id, tenant_id, parent_id, code, label, title, root_ind, default_ind, source)
values (1005002, 1000002, 0, 'ROOT_PST', 'label_top_position', 'All Position', 1, 1, 1);

--
-- 内置用户关联数据
--

truncate sys_entity_relation;

insert into sys_entity_relation (id, tenant_id, ancestor_id, entity_id, parent_ind, biz_type, relation_index)
values (1002001001, 1000001, 1004001, 1002001, 1, 'USR_CURRENT_ORG', 1),
       (1002001002, 1000001, 1005001, 1002001, 1, 'USR_CURRENT_PST', 1),
       (1002002001, 1000001, 1004001, 1002002, 1, 'USR_CURRENT_ORG', 1),
       (1002002002, 1000001, 1005001, 1002002, 1, 'USR_CURRENT_PST', 1);

insert into sys_entity_relation (id, tenant_id, ancestor_id, entity_id, parent_ind, biz_type, relation_index)
values (1002003001, 1000001, 1004001, 1002003, 1, 'USR_CURRENT_ORG', 1),
       (1002003002, 1000001, 1005001, 1002003, 1, 'USR_CURRENT_PST', 1),
       (1002004001, 1000001, 1004001, 1002004, 1, 'USR_CURRENT_ORG', 1),
       (1002004002, 1000001, 1005001, 1002004, 1, 'USR_CURRENT_PST', 1);

-- =====================================================================================================================
-- 多语言
-- =====================================================================================================================

--
-- 语言类型
--

truncate sys_lang;

insert into sys_lang (id, code, lang, country, label, description, default_ind, active)
values (1000001, 'zh_cn', 'zh', 'cn', 'label_lang_zh_cn', '简体中文', 1, 1),
       (1000002, 'zh_tw', 'zh', 'tw', 'label_lang_zh_tw', '繁体中文', 0, 1),
       (1000003, 'en_us', 'en', 'us', 'label_lang_en_us', '美式英语', 0, 1);

--
-- 多语言文本
--

truncate sys_label;

insert into sys_label (id, code, zh_cn_label, en_label)
values (10010000101, 'label_lang_type__zh_cn', '简体中文', 'Simplified Chinese'),
       (10010000102, 'label_lang_type__zh_tw', '繁体中文', 'Traditional Chinese'),
       (10010000103, 'label_lang_type__en_us', '英文', 'English'),
       (10010000201, 'label_mobile_country_code__0085', '中国', 'China'),
       (10010000202, 'label_mobile_country_code__00852', '中国香港', 'Hong Kong'),
       (10010000203, 'label_mobile_country_code__00886', '中国台湾', 'Taiwan'),
       (10010000204, 'label_mobile_country_code__00853', '中国澳门', 'Macao'),
       (10020000001, 'label__ok', 'OK', 'OK'),
       (10020000002, 'label__delete', '删除', 'Delete'),
       (10020000003, 'label__save', '保存', 'Save'),
       (10020000004, 'label__reset', '重置', 'Reset'),
       (10020000005, 'label__submit', '提交', 'Submit'),
       (10020000006, 'label__add', '添加', 'Add'),
       (10020000007, 'label__edit', '编辑', 'Edit'),
       (10020000008, 'label__remove', '移除', 'Remove'),
       (10020000009, 'label__id', 'ID', 'ID'),
       (10020000010, 'label__code', '编号', 'Code'),
       (10020000011, 'label__title', '标题', 'Title'),
       (10020000012, 'label__name', '名称', 'Name'),
       (10020000013, 'label__description', '描述说明', 'Description'),
       (10020000014, 'label__x', '占位', '占位');

--
-- 会员类型
--

truncate sys_package;

insert into sys_package (id, biz_type, code, title, label, source, active)
values (1001001, 'TENANT', 'Professional', '专业版', 'label_package_professional', 1, 1),
       (1001002, 'TENANT', 'Enterprise', '企业版', 'label_package_enterprise', 1, 1),
       (1002001, 'MEMBER', 'VIP', '会员', 'label_package_vip', 1, 1),
       (1002002, 'MEMBER', 'SVIP', '超级会员', 'label_package_svip', 1, 1);

-- ==============================¬=======================================================================================
-- OAuth
-- =====================================================================================================================

--
-- 客户端
--

truncate table sys_client;

insert into sys_client (id, client_id, client_name, client_secret, authorization_grant_types,
                        client_authentication_methods, redirect_uris, scopes, active)
values (1000001, 'webapp', 'webapp', '{noop}c4e859c68a7e3996a13719161d0b4ef006e9cc007a3b95292fbd4e9c18c5e12c',
        'authorization_code,refresh_token,client_credentials,password,social,otp',
        'client_secret_basic,client_secret_post,client_secret_jwt,private_key_jwt',
        'http://127.0.0.1:8080,http://127.0.0.1:9292,http://127.0.0.1:9292/login/oauth/code/webapp',
        'openid,profile', 1),
       (1000002, 'admin', 'admin', '{noop}c4e859c68a7e3996a13719161d0b4ef006e9cc007a3b95292fbd4e9c18c5e12c',
        'authorization_code,refresh_token,client_credentials,password,social,otp',
        'client_secret_basic,client_secret_post,client_secret_jwt,private_key_jwt',
        'http://127.0.0.1:8080,http://127.0.0.1:9292,http://127.0.0.1:9292/login/oauth/code/webapp',
        'openid,profile', 1),
       (1000003, 'mobile', 'mobile', '{noop}c4e859c68a7e3996a13719161d0b4ef006e9cc007a3b95292fbd4e9c18c5e12c',
        'authorization_code,refresh_token,client_credentials,password,social,otp',
        'client_secret_basic,client_secret_post,client_secret_jwt,private_key_jwt',
        'http://127.0.0.1:8080,http://127.0.0.1:9292,http://127.0.0.1:9191/login/oauth/code/demo',
        'openid,profile', 0);
