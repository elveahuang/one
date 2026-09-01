package cc.wdev.dev.webapp.mybatis.service;

import cc.wdev.dev.webapp.BaseTests;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.webapp.mybatis.domain.entity.MpUserEntity;
import cc.wdev.webapp.mybatis.service.MpUserService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author elvea
 */
public class MpUserServiceTests extends BaseTests {

    @Autowired
    MpUserService mpUserService;

    @Test
    public void baseTest() {
        Assertions.assertNotNull(this.mpUserService);
    }

    @Test
    public void versionTest() {
        MpUserEntity user = new MpUserEntity();
        user.setUsername("admin");
        this.mpUserService.save(user);

        Long id = user.getId();
        Assertions.assertNotNull(id);

        user = this.mpUserService.findById(id);

        this.mpUserService.updateById(user);
        Assertions.assertNotNull(user.getVersion());
    }

    @Test
    public void pageTest() {
        LambdaUpdateWrapper<MpUserEntity> wrapper = new LambdaUpdateWrapper<MpUserEntity>()
            .set(MpUserEntity::getUsername, "admin2")
            .eq(MpUserEntity::getId, 999L);
        List<MpUserEntity> list = this.mpUserService.getMapper().selectList(wrapper);
        Assertions.assertNotNull(list);

        IPage<MpUserEntity> page = this.mpUserService.getMapper().selectPage(MyBatisPlusUtils.getLimitPage(), wrapper);
        Assertions.assertNotNull(list);
    }

}
