package cc.wdev.platform.system.security.api.impl;

import cc.wdev.platform.commons.core.tenant.TenantContext;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.SourceTypeEnum;
import cc.wdev.platform.commons.utils.ArrayUtils;
import cc.wdev.platform.commons.utils.ClassUtils;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.ObjectUtils;
import cc.wdev.platform.system.commons.domain.AuthorityNode;
import cc.wdev.platform.system.commons.enums.*;
import cc.wdev.platform.system.core.domain.entity.*;
import cc.wdev.platform.system.core.service.*;
import cc.wdev.platform.system.security.api.SecurityApi;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.constants.GlobalConstants.DELIMITER;
import static cc.wdev.platform.commons.constants.SecurityConstants.ROOT_USER;
import static cc.wdev.platform.commons.enums.BooleanTypeEnum.isTrueValue;
import static cc.wdev.platform.commons.enums.SourceTypeEnum.isSystemSource;
import static cc.wdev.platform.commons.utils.StringUtils.nvl;
import static cc.wdev.platform.system.commons.constants.SystemConstants.GLOABL_BASE_PACKAGE;
import static cc.wdev.platform.system.commons.enums.BizScopeTypeEnum.isPlatformScope;
import static cc.wdev.platform.system.commons.enums.BizScopeTypeEnum.isSystemScope;

/**
 * @author elvea
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityApiImpl implements SecurityApi {

    private final TenantService tenantService;

    private final RoleService roleService;

    private final UserRoleService userRoleService;

    private final AuthorityService authorityService;

    private final UserService userService;

    private final PackageService packageService;

    private final EntityAuthorityService entityAuthorityService;

    /**
     * @see SecurityApi#initialize()
     */
    @Override
    public void initialize() {
        // 初始化权限
        this.initializeAuthority();
        // 初始化租户套餐权限
        this.initializePackageAuthority();
        // 初始化租户权限
        this.initializeTenantAuthority();
        // 初始化角色
        this.initializeRole();
        // 初始化角色权限
        this.initializeRoleUser();
        // 初始化角色权限
        this.initializeRoleAuthority();
    }

    /**
     * 刷新全局权限数据
     * 从BaseAuthorityNodeEnum中获取权限定义
     */
    private void initializeAuthority() {
        log.info("Initialize authority start.");

        // 扫描枚举
        List<BaseAuthorityNodeEnum> authorityNodeEnumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseAuthorityNodeEnum.class);

        // 遍历枚举权限定义然后递归刷新权限
        for (BaseAuthorityNodeEnum nodeEnum : authorityNodeEnumList) {
            log.info("Initialize {} authority start.", nodeEnum.getValue());
            for (AuthorityNode node : nodeEnum.getNodes()) {
                this.initializeAuthority(node, 0L);
            }
            log.info("Initialize {} authority done.", nodeEnum.getValue());
        }

        // 清空缓存
        log.info("Initialize authority cache.");
        this.authorityService.clearCache();

        log.info("Initialize authority done.");
    }

    /**
     * 私有方法，递归刷新权限
     */
    private void initializeAuthority(AuthorityNode node, @NonNull Long parentId) {
        AuthorityEntity entity = this.authorityService.findByCode(node.getCode());
        if (entity == null) {
            entity = new AuthorityEntity();
            entity.setParentId(parentId);
        }
        entity.setCode(node.getCode());
        entity.setTitle(node.getTitle());
        entity.setParentId(parentId);
        entity.setAuthorityType(node.getAuthorityType().getCode());
        entity.setAuthorityScopeType(node.getAuthorityScopeType().getCode());
        entity.setAuthorityBizType(node.getAuthorityBizType().getValue());
        entity.setAuthorityRoleType(Arrays.stream(ArrayUtils.nvl(node.getRoleTypes(), new BaseRoleTypeEnum[]{}))
            .map(BaseRoleTypeEnum::getCode)
            .collect(Collectors.joining(DELIMITER, DELIMITER, DELIMITER))
        );
        entity.setDescription(node.getTitle());
        entity.setActive(BooleanTypeEnum.getByValue(node.getActive()).getValue());
        this.authorityService.save(entity);

        // 递归同步子权限
        if (node.getItems() != null) {
            for (AuthorityNode children : node.getItems()) {
                this.initializeAuthority(children, entity.getId());
            }
        }
    }

    /**
     * 初始化租户套餐权限
     */
    private void initializePackageAuthority() {
        log.info("Initialize package authority start.");

        // 获取租户套餐列表并过滤会员套餐
        List<PackageEntity> entityList = this.packageService.findAll().stream()
            .filter((e) -> PackageBizTypeEnum.TENANT.getValue().equalsIgnoreCase(e.getBizType()))
            .toList();

        if (CollectionUtils.isEmpty(entityList)) {
            log.info("Initialize package authority skip. no package.");
            return;
        }

        // 查询并过滤平台范围权限，平台范围权限只允许顶层租户拥有
        List<Long> authorityIdList = this.authorityService.findAll().stream()
            .filter(e -> !isPlatformScope(e.getAuthorityScopeType()))
            .map(AuthorityEntity::getId)
            .toList();

        for (PackageEntity entity : entityList) {
            log.info("Initialize package [{}] authority start.", entity.getCode());
            this.entityAuthorityService.saveAuthority(EntityAuthorityBizTypeEnum.PACKAGE, entity.getId(), authorityIdList);
            log.info("Initialize package [{}] authority done.", entity.getCode());
        }

        log.info("Initialize package authority cache.");
        this.entityAuthorityService.clearCache();

        log.info("Initialize package authority done.");
    }

    /**
     * 初始化租户角色
     */
    private void initializeRole() {
        log.info("Initialize role start.");

        // 扫描枚举
        List<BaseRoleTypeEnum> bizTypeEnumList = ClassUtils.getEnumClass(GLOABL_BASE_PACKAGE, BaseRoleTypeEnum.class);

        // 平台范围角色类型
        List<BaseRoleTypeEnum> platformEnumList = Lists.newArrayList();
        platformEnumList.addAll(bizTypeEnumList.stream().filter((e) -> e.getScope().equals(BizScopeTypeEnum.PLATFORM.getCode())).toList());
        platformEnumList.addAll(bizTypeEnumList.stream().filter((e) -> e.getScope().equals(BizScopeTypeEnum.SYSTEM.getCode())).toList());

        // 系统范围角色类型
        List<BaseRoleTypeEnum> systemEnumList = Lists.newArrayList();
        systemEnumList.addAll(bizTypeEnumList.stream().filter((e) -> e.getScope().equals(BizScopeTypeEnum.SYSTEM.getCode())).toList());

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Initialize role skip. no tenant.");
            return;
        }

        for (TenantEntity entity : tenantEntityList) {
            try {
                log.info("Initialize tenant [{}] role start.", entity.getCode());

                // 设置租户上下文
                TenantContext.setTenantId(entity.getId());
                TenantContext.setTenantRootInd(entity.getRootInd());

                // 待处理角色实体
                List<RoleEntity> updateEntityList = Lists.newArrayList();
                List<RoleEntity> insertEntityList = Lists.newArrayList();

                List<BaseRoleTypeEnum> tenantRuleEnumList = Lists.newArrayList();
                if (BooleanTypeEnum.isTrueValue(entity.getRootInd())) {
                    tenantRuleEnumList.addAll(platformEnumList);
                } else {
                    tenantRuleEnumList.addAll(systemEnumList);
                }

                if (CollectionUtils.isNotEmpty(tenantRuleEnumList)) {
                    for (BaseRoleTypeEnum bizTypeEnum : tenantRuleEnumList) {
                        RoleEntity roleEntity = this.roleService.findByCode(bizTypeEnum.getCode());
                        if (roleEntity != null) {
                            updateEntityList.add(roleEntity);
                        } else {
                            roleEntity = new RoleEntity();
                            insertEntityList.add(roleEntity);
                        }
                        roleEntity.setActive(1);
                        roleEntity.setCode(bizTypeEnum.getCode());
                        roleEntity.setBizType(bizTypeEnum.getBizType());
                        roleEntity.setGroupType(bizTypeEnum.getRoleGroupType());
                        roleEntity.setDataScopeType(bizTypeEnum.getRoleScopeType());
                        roleEntity.setTitle(bizTypeEnum.getDescription());
                        roleEntity.setLabel(bizTypeEnum.getLabelKey());
                        roleEntity.setDescription(bizTypeEnum.getDescription());
                        roleEntity.setSource(SourceTypeEnum.SYSTEM.getValue());
                    }
                }
                this.roleService.insertBatch(insertEntityList);
                this.roleService.updateBatchById(updateEntityList);

                log.info("Initialize tenant [{}] role start.", entity.getCode());
            } finally {
                TenantContext.clear();
            }
        }

        log.info("Initialize role cache.");
        this.roleService.clearCache();

        log.info("Initialize role done.");
    }

    /**
     * 初始化角色用户关联
     * 默认都只关联租户顶层用户，关联所有默认角色
     */
    private void initializeRoleUser() {
        log.info("Initialize role user start.");

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Initialize role user skip. no tenant.");
            return;
        }

        for (TenantEntity tenant : tenantEntityList) {
            try {
                log.info("Initialize role user for tenant [{}] start.", tenant.getCode());

                // 设置租户上下文
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantRootInd(tenant.getRootInd());

                // 查询角色列表
                List<Long> roleIdList = this.roleService.findAll().stream().map(RoleEntity::getId).toList();
                if (CollectionUtils.isEmpty(roleIdList)) {
                    log.info("Initialize role user for tenant [{}] skip.", tenant.getCode());
                    continue;
                }

                // 查询默认用户
                UserEntity user = this.userService.findByUsername(ROOT_USER);
                if (ObjectUtils.isEmpty(user)) {
                    log.info("Initialize role user for tenant [{}] skip.", tenant.getCode());
                    continue;
                }

                // 保存用户角色关联
                this.userRoleService.saveUserRole(user.getId(), roleIdList);

                log.info("Initialize role user for tenant [{}] done.", tenant.getCode());
            } finally {
                TenantContext.clear();
            }
        }
    }

    private void initializeTenantAuthority() {
        log.info("Initialize tenant authority start.");

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Tenant is empty, skip initialize tenant authority");
            return;
        }

        // 获取权限列表
        List<AuthorityEntity> authorityEntityList = this.authorityService.findAll();

        List<Long> platformAuthorityIdList = authorityEntityList.stream()
            .filter(e -> isPlatformScope(e.getAuthorityScopeType()) || isSystemScope(e.getAuthorityScopeType()))
            .map(AuthorityEntity::getId)
            .toList();

        List<Long> tenantAuthorityIdList = authorityEntityList.stream()
            .filter(e -> isSystemScope(e.getAuthorityScopeType()))
            .map(AuthorityEntity::getId)
            .toList();

        for (TenantEntity tenant : tenantEntityList) {
            log.info("Initialize tenant authority for tenant [{}] start.", tenant.getCode());
            try {
                // 设置租户上下文
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantRootInd(tenant.getRootInd());

                if (isTrueValue(tenant.getRootInd())) {
                    // 顶层租户，拥有平台和系统两个范围的权限
                    this.entityAuthorityService.saveAuthority(EntityAuthorityBizTypeEnum.TENANT, tenant.getId(), platformAuthorityIdList);
                } else {
                    // 顶层租户，拥有系统范围的权限
                    this.entityAuthorityService.saveAuthority(EntityAuthorityBizTypeEnum.TENANT, tenant.getId(), tenantAuthorityIdList);
                }

                log.info("Initialize tenant authority for tenant [{}] done.", tenant.getCode());
            } finally {
                TenantContext.clear();
            }
        }
        log.info("Initialize tenant authority node.");
    }

    /**
     * 初始化角色和权限关联
     */
    private void initializeRoleAuthority() {
        log.info("Initialize role authority start.");

        // 获取租户列表
        List<TenantEntity> tenantEntityList = this.tenantService.findAll();
        if (CollectionUtils.isEmpty(tenantEntityList)) {
            log.info("Initialize role authority skip. no tenant.");
            return;
        }

        // 获取权限列表
        List<AuthorityEntity> authorityEntityList = this.authorityService.findAll();
        if (CollectionUtils.isEmpty(authorityEntityList)) {
            log.info("Initialize role authority skip. no authority.");
            return;
        }

        for (TenantEntity tenant : tenantEntityList) {
            log.info("Initialize role authority for tenant [{}] start.", tenant.getCode());
            try {
                // 设置租户上下文
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantRootInd(tenant.getRootInd());

                // 查询角色列表
                List<RoleEntity> roleEntityList = this.roleService.findAll();
                if (CollectionUtils.isEmpty(roleEntityList)) {
                    log.info("Initialize role authority for tenant [{}] skip.", tenant.getCode());
                    continue;
                }

                for (RoleEntity role : roleEntityList) {
                    log.info("Initialize role authority for tenant [{}] role [{}] [{}] start.", tenant.getCode(), role.getCode(), role.getBizType());

                    // 非系统默认角色，跳过
                    if (!isSystemSource(role.getSource())) {
                        log.info("Initialize role authority for tenant [{}] role [{}] skip. not system role.", tenant.getCode(), role.getCode());
                        continue;
                    }

                    // 过滤获取对应角色类型权限列表
                    List<Long> authorityIdList = this.getRoleTypeAuthorityIds(role.getBizType(), authorityEntityList);

                    // 保存角色和权限的关联
                    this.entityAuthorityService.saveAuthority(EntityAuthorityBizTypeEnum.ROLE, role.getId(), authorityIdList);

                    log.info("Initialize role authority for tenant [{}] role [{}] [{}] done.", tenant.getCode(), role.getCode(), role.getBizType());
                }
            } finally {
                TenantContext.clear();
            }
            log.info("Initialize role authority for tenant [{}] done.", tenant.getCode());
        }

        log.info("Initialize role authority cache.");
        this.entityAuthorityService.clearCache();

        log.info("Initialize role authority done.");
    }

    /**
     * 获取角色类型对应的权限列表
     * 1. 平台管理员，包含所有平台和系统范围的权限
     * 2. 系统管理员，包含所有系统范围的权限
     * 3. 其他类型，只包含指定角色类型的权限
     */
    private List<Long> getRoleTypeAuthorityIds(String roleType, @NonNull List<AuthorityEntity> authorityEntityList) {

        if (RoleTypeEnum.PLATFORM_ADMINISTRATOR.getValue().equalsIgnoreCase(roleType)) {
            return authorityEntityList.stream()
                .filter(e -> isPlatformScope(e.getAuthorityScopeType()) || isSystemScope(e.getAuthorityScopeType()))
                .map(AuthorityEntity::getId)
                .toList();
        } else if (RoleTypeEnum.SYSTEM_ADMINISTRATOR.getValue().equalsIgnoreCase(roleType)) {
            return authorityEntityList.stream()
                .filter(e -> isSystemScope(e.getAuthorityScopeType()))
                .map(AuthorityEntity::getId)
                .toList();
        } else {
            return authorityEntityList.stream()
                .filter(e -> nvl(e.getAuthorityRoleType()).contains(DELIMITER + roleType + DELIMITER))
                .map(AuthorityEntity::getId)
                .toList();
        }
    }

}
