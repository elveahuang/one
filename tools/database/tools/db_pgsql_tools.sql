-- =====================================================================================================================
-- PgSQL DB Tools
-- =====================================================================================================================

/**
* 本地设置租户域名
*/
update sys_tenant
set domain = 'wdev.cc'
where id = 1000001;

update sys_tenant
set domain = 'z.wdev.cc'
where id = 1000002;

/**
* 查询套餐权限
*/
select sp.id, sp.code, sa.id, sa.code, sa.title
from sys_authority sa,
     sys_entity_authority sea,
     sys_package sp
where sa.id = sea.authority_id
  and sea.entity_id = sp.id
  and sea.biz_type = 'PACKAGE';

/**
* 查询用户角色
*/
select su.id, su.username, sr.id, sr.code, sr.title
from sys_user su,
     sys_role sr,
     sys_user_role sur
where su.id = sur.user_id
  and sr.id = sur.role_id
  and su.id = 1002001;

/**
* 查询角色权限
*/
select st.id,
       st.code,
       st.title,
       sr.id,
       sr.code,
       sr.title,
       sa.id,
       sa.code,
       sa.title
from sys_role sr,
     sys_tenant st,
     sys_entity_authority sea,
     sys_authority sa
where sr.id = sea.entity_id
  and st.id = sea.tenant_id
  and sa.id = sea.authority_id
  and sea.biz_type = 'ROLE'
  and st.id = 1000002;

/**
* 查询租户权限
*/
select st.id, st.code, st.title, sa.id, sa.code, sa.title
from sys_entity_authority sea,
     sys_tenant st,
     sys_authority sa
where sea.tenant_id = st.id
  and sea.entity_id = st.id
  and sea.authority_id = sa.id
  and sea.biz_type = 'TENANT'
  and sea.tenant_id = 1000002;

/**
* 查询用户权限
*/
select su.id,
       su.username,
       su.display_name,
       sr.id,
       sr.code,
       sr.title,
       sa.id,
       sa.code,
       sa.title
from sys_entity_authority sea,
     sys_authority sa,
     sys_user_role sur,
     sys_role sr,
     sys_user su
where sea.tenant_id = su.tenant_id
  and sea.entity_id = sr.id
  and sea.authority_id = sa.id
  and sea.biz_type = 'ROLE'
  and sur.role_id = sr.id
  and su.id = 1002003;

/**
* 查询用户->角色->权限
*/
select sr.id, sr.code, sr.title, sa.id, sa.code, sa.title
from sys_entity_authority sea,
     sys_authority sa,
     sys_role sr
where sea.authority_id = sa.id
  and sr.id = sea.entity_id
  and sea.biz_type = 'ROLE'
  and sea.tenant_id = 1000002
  and sr.tenant_id = 1000002
  and sea.entity_id in (select r.role_id from sys_user_role r where r.user_id = 1002003)
order by sa.code;
