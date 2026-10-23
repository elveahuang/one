package cc.wdev.platform.system.core.service.impl;

import cc.wdev.platform.system.core.domain.vo.OrganizationVo;
import cc.wdev.platform.system.core.domain.vo.PositionVo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 部门/岗位树组装逻辑单元测试
 *
 * @author elvea
 */
class OrganizationBuildTreeTests {

    private static OrganizationVo vo(Long id, Long parentId) {
        return OrganizationVo.builder().id(id).parentId(parentId).title("ORG-" + id).build();
    }

    private static PositionVo pvo(Long id, Long parentId) {
        return PositionVo.builder().id(id).parentId(parentId).title("POS-" + id).build();
    }

    @Test
    void testOrganizationTree() {
        // 根 -> 子A -> 孙A1；根 -> 子B
        List<OrganizationVo> tree = OrganizationServiceImpl.buildTree(List.of(vo(1L, 0L), vo(2L, 1L), vo(3L, 2L), vo(4L, 1L)));

        Assertions.assertNotNull(tree);
        Assertions.assertEquals(1, tree.size());
        Assertions.assertEquals(1L, tree.get(0).getId());
        Assertions.assertEquals(2, tree.get(0).getChildren().size());
        Assertions.assertEquals(2L, tree.get(0).getChildren().get(0).getId());
        Assertions.assertEquals(1, tree.get(0).getChildren().get(0).getChildren().size());
        Assertions.assertEquals(3L, tree.get(0).getChildren().get(0).getChildren().get(0).getId());
    }

    @Test
    void testOrganizationTreeOrphan() {
        // 父级不存在的节点作为新的根节点
        List<OrganizationVo> tree = OrganizationServiceImpl.buildTree(List.of(vo(1L, 0L), vo(2L, 99L)));

        Assertions.assertEquals(2, tree.size());
    }

    @Test
    void testOrganizationTreeEmpty() {
        Assertions.assertTrue(OrganizationServiceImpl.buildTree(List.of()).isEmpty());
    }

    @Test
    void testPositionTree() {
        List<PositionVo> tree = PositionServiceImpl.buildTree(List.of(pvo(1L, 0L), pvo(2L, 1L), pvo(3L, 1L)));

        Assertions.assertNotNull(tree);
        Assertions.assertEquals(1, tree.size());
        Assertions.assertEquals(1L, tree.get(0).getId());
        Assertions.assertEquals(2, tree.get(0).getChildren().size());
    }

    @Test
    void testPositionTreeEmpty() {
        Assertions.assertTrue(PositionServiceImpl.buildTree(List.of()).isEmpty());
    }

}
