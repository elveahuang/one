package cc.wdev.platform.system.i18n.service.impl;

import cc.wdev.platform.commons.data.mybatis.service.BaseCachingEntityService;
import cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils;
import cc.wdev.platform.commons.domain.R;
import cc.wdev.platform.commons.enums.ActiveTypeEnum;
import cc.wdev.platform.commons.enums.BooleanTypeEnum;
import cc.wdev.platform.commons.enums.LangTypeEnum;
import cc.wdev.platform.commons.oapis.translator.Translator;
import cc.wdev.platform.commons.oapis.translator.TranslatorFactory;
import cc.wdev.platform.commons.utils.CollectionUtils;
import cc.wdev.platform.commons.utils.JacksonUtils;
import cc.wdev.platform.commons.utils.StringUtils;
import cc.wdev.platform.system.i18n.domain.converter.LabelConverter;
import cc.wdev.platform.system.i18n.domain.entity.LabelEntity;
import cc.wdev.platform.system.i18n.domain.request.LabelEditRequest;
import cc.wdev.platform.system.i18n.domain.request.LabelSearchRequest;
import cc.wdev.platform.system.i18n.domain.vo.LabelVo;
import cc.wdev.platform.system.i18n.enums.LabelGroupTypeEnum;
import cc.wdev.platform.system.i18n.enums.LabelTypeEnum;
import cc.wdev.platform.system.i18n.repository.LabelRepository;
import cc.wdev.platform.system.i18n.service.LabelService;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static cc.wdev.platform.commons.data.mybatis.utils.MyBatisPlusUtils.getMyBatisPlusPage;

/**
 * @author elvea
 * @see LabelService
 * @see BaseCachingEntityService
 */
@Slf4j
@AllArgsConstructor
@Service
public class LabelServiceImpl extends BaseCachingEntityService<LabelEntity, Long, LabelRepository> implements LabelService {

    private final TranslatorFactory translatorFactory;

    /**
     * @see LabelService#translate(List)
     */
    @Override
    public void translate(List<Long> ids) {
        Translator translator = this.translatorFactory.getTranslator();
        List<LabelEntity> labelList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(ids)) {
            labelList = this.lambdaQueryWrapper().eq(LabelEntity::getActive, ActiveTypeEnum.ENABLED.getValue()).list();
        } else {
            List<LabelEntity> list = this.findByIds(ids);
            if (CollectionUtils.isNotEmpty(list)) {
                labelList.addAll(list);
            }
        }
        if (CollectionUtils.isNotEmpty(labelList)) {
            labelList.forEach((label) -> {
                // 获取源语言文本
//                String sourceLangText = label.setSourceLangTypeEnum();
//                LangTypeEnum langTypeEnum = label.getLangTypeEnum();
                LangTypeEnum langTypeEnum = LangTypeEnum.EN;
                String sourceLangText = label.getEnLabel();

                if (label.getZhTwStaticInd() != null && !label.getZhTwStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
                    label.setZhTwLabel(translator.translate(LangTypeEnum.ZH_CN, LangTypeEnum.ZH_TW, label.getZhCnLabel()));
                }
                if (StringUtils.isEmpty(label.getEnLabel())) {
                    label.setEnLabel(translator.translate(LangTypeEnum.ZH_CN, LangTypeEnum.EN, label.getZhCnLabel()));
                }
                if (label.getFrStaticInd() != null && !label.getFrStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
                    label.setFrLabel(translator.translate(langTypeEnum, LangTypeEnum.FR, sourceLangText));
                }
                if (label.getJaStaticInd() != null && !label.getJaStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
                    label.setJaLabel(translator.translate(langTypeEnum, LangTypeEnum.JA, sourceLangText));
                }
                if (label.getKrStaticInd() != null && !label.getKrStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
                    label.setKrLabel(translator.translate(langTypeEnum, LangTypeEnum.KR, sourceLangText));
                }
                if (label.getViStaticInd() != null && !label.getViStaticInd().equals(BooleanTypeEnum.TRUE.getValue())) {
                    label.setViLabel(translator.translate(langTypeEnum, LangTypeEnum.VI, sourceLangText));
                }
            });
            this.saveBatch(labelList);
        }

    }

    /**
     * 生成多语言文件
     *
     * @see LabelService#generate(LabelTypeEnum, File)
     */
    @Override
    public String generate(LabelTypeEnum labelType, File fileDirPath) {
        List<LabelEntity> labelEntityList = this.lambdaQueryWrapper().eq(LabelEntity::getActive, ActiveTypeEnum.ENABLED.getValue()).list();
        Map<LangTypeEnum, Map<String, String>> customMap = Maps.newHashMap();
        Map<LangTypeEnum, Map<String, String>> commonMap = Maps.newHashMap();

        Arrays.stream(LangTypeEnum.values()).toList().forEach(langTypeEnum -> {
            customMap.put(langTypeEnum, Maps.newHashMap());
            commonMap.put(langTypeEnum, Maps.newHashMap());
            labelEntityList.forEach(labelEntity -> {
                if (StringUtils.isNotEmpty(labelEntity.getLabelGroupType()) && LabelGroupTypeEnum.CUSTOM.getValue().equalsIgnoreCase(labelEntity.getLabelGroupType())) {
                    switch (langTypeEnum) {
                        case ZH_CN -> customMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getZhCnLabel());
                        case ZH_TW -> customMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getZhTwLabel());
                        case EN -> customMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getEnLabel());
                        case FR -> customMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getFrLabel());
                        case JA -> customMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getJaLabel());
                        case KR -> customMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getKrLabel());
                        case VI -> customMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getViLabel());
                    }
                }
                if (StringUtils.isNotEmpty(labelEntity.getLabelGroupType()) && LabelGroupTypeEnum.COMMON.getValue().equalsIgnoreCase(labelEntity.getLabelGroupType())) {
                    switch (langTypeEnum) {
                        case ZH_CN -> commonMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getZhCnLabel());
                        case ZH_TW -> commonMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getZhTwLabel());
                        case EN -> commonMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getEnLabel());
                        case FR -> commonMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getFrLabel());
                        case JA -> commonMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getJaLabel());
                        case KR -> commonMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getKrLabel());
                        case VI -> commonMap.get(langTypeEnum).put(labelEntity.getCode(), labelEntity.getViLabel());
                    }
                }
            });
        });
        generateLabelFile(LabelGroupTypeEnum.COMMON.getValue(), labelType, commonMap);
        return generateLabelFile(LabelGroupTypeEnum.CUSTOM.getValue(), labelType, customMap);

    }

    private @NotNull String generateLabelFile(String groupPath, LabelTypeEnum labelType, Map<LangTypeEnum, Map<String, String>> customMap) {
        //定义临时工作目录
        String property = System.getProperty("user.dir").replace("\\", "/");
        String tempDirPath = String.format("%s/.temp/i18n/%s/", property, groupPath);

        if (LabelTypeEnum.PROPERTIES.equals(labelType)) {
            Arrays.stream(LangTypeEnum.values()).toList().forEach(langTypeEnum -> {
                Properties properties = new Properties();
                properties.putAll(customMap.get(langTypeEnum));
                loadPropertiesFile(tempDirPath, properties, langTypeEnum);
            });
        } else if (LabelTypeEnum.JSON.equals(labelType)) {
            Arrays.stream(LangTypeEnum.values()).toList().forEach(langTypeEnum -> {
                String jsonStr = JSONUtil.toJsonStr(customMap.get(langTypeEnum));
                loadJsonFile(tempDirPath, langTypeEnum, jsonStr);
            });
        }

        return tempDirPath;
    }

    @Override
    public R<Page<LabelVo>> getLabelList(LabelSearchRequest request) {
        IPage<LabelEntity> page = this.lambdaQueryWrapper()
            .and(StringUtils.isNotEmpty(request.getQ()),
                wrapper -> wrapper
                    .like(LabelEntity::getCode, request.getQ())
                    .or()
                    .like(LabelEntity::getZhCnLabel, request.getQ())
            )
            .eq(LabelEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .page(getMyBatisPlusPage(request.getPageable()));
        IPage<LabelVo> result = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setCurrent(page.getCurrent());
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            result.setRecords(page.getRecords()
                .stream()
                .map(LabelConverter.INSTANCE::entityToVo)
                .toList());
        }
        return R.success(MyBatisPlusUtils.toSpringDataPage(result));
    }

    @Override
    public void saveLabel(LabelEditRequest request) {
        if (null != request) {
            LabelEntity labelEntity = LabelConverter.INSTANCE.formToEntity(request);
            if (null != request.getId() && request.getId() > 0) {
                labelEntity.setId(request.getId());
            }
            save(labelEntity);
        }
    }

    @Override
    public LabelVo details(LabelSearchRequest request) {
        if (null != request.getId() && request.getId() > 0) {
            LabelEntity labelEntity = this.findById(request.getId());
            if (null != labelEntity) {
                return LabelConverter.INSTANCE.entityToVo(labelEntity);
            }
        }
        return new LabelVo();
    }

    @Override
    public void delete(List<Long> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            softDeleteBatchById(ids);
        }
    }

    @Override
    public void download(String filePath2, HttpServletResponse response) throws IOException {
        String zipFilePath = "";
        // 生成多语言
        File file = new File(filePath2);
        List<LabelTypeEnum> list = Arrays.stream(LabelTypeEnum.values()).toList();
        for (LabelTypeEnum labelTypeEnum : list) {
            zipFilePath = generate(labelTypeEnum, file);
        }

        // 压缩
        Path finalZipFilePath = Paths.get(zipFilePath).getParent();
        File zip = ZipUtil.zip(finalZipFilePath.toString());

        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + zip.getName());
        Files.copy(Paths.get(zip.toURI()), response.getOutputStream());

        // 异步删除临时文件
        CompletableFuture.runAsync(() -> FileUtil.del(finalZipFilePath.getParent()));
    }

    @Override
    public Boolean checkLabelCode(String labelCode) {
        if (StringUtils.isEmpty(labelCode)) {
            return Boolean.FALSE;
        }
        LabelEntity labelEntity =
            this.lambdaQueryWrapper()
                .select(LabelEntity::getId, LabelEntity::getCode)
                .eq(LabelEntity::getCode, labelCode)
                .eq(LabelEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
                .one();
        if (null != labelEntity) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Set<String> getAllLabelCode() {
        List<LabelEntity> labelEntities = this.lambdaQueryWrapper()
            .select(LabelEntity::getCode, LabelEntity::getLabelGroupType)
            .eq(LabelEntity::getActive, ActiveTypeEnum.ENABLED.getValue())
            .list();
        if (CollectionUtils.isEmpty(labelEntities)) {
            return Sets.newHashSet();
        }
        return labelEntities.stream().map(labelEntity -> labelEntity.getLabelGroupType() + '.' + labelEntity.getCode()).collect(Collectors.toSet());
    }

    @Override
    public void loadingLabelJsonData(String localJsonPath, String groupName) throws Exception {
        if (StringUtils.isEmpty(localJsonPath) || StringUtils.isEmpty(groupName)) {
            return;
        }
        Path filePath = Paths.get(localJsonPath);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath.toString()));
        } catch (Exception e) {
            log.info("读取{}文件失败...", filePath.getFileName());
            throw new RuntimeException(e);
        }
        StringBuilder stringBuilder = new StringBuilder();

        char[] charBuilder = new char[128];
        int bytesRead = -1;
        while ((bytesRead = reader.read(charBuilder)) > 0) {
            stringBuilder.append(charBuilder, 0, bytesRead);
        }
        Map map = JacksonUtils.toObject(stringBuilder.toString(), Map.class);
        List<LabelEntity> list = Lists.newArrayList();
        if (!map.isEmpty()) {
            for (Object key : map.keySet()) {
                LabelEntity build = LabelEntity.builder()
                    .labelGroupType(groupName)
                    .code(key.toString())
                    .enStaticInd(BooleanTypeEnum.TRUE.getValue())
                    .zhCnLabel(map.get(key).toString())
                    .build();
                build.setActive(1);
                list.add(build);
            }
        }
        saveBatch(list);
    }

    @Override
    public Long getLabelCount() {
        return this.lambdaQueryWrapper().eq(LabelEntity::getActive, ActiveTypeEnum.ENABLED.getValue()).count();
    }

    /**
     * 加载properties文件
     */
    private void loadPropertiesFile(String parent, Properties properties, LangTypeEnum langType) {
        File file = new File(parent, "messages_" + langType.getValue().toLowerCase() + ".properties");
        if (!FileUtil.exist(file.getPath())) {
            FileUtil.touch(file.getPath());
        }

        if (file.exists()) {
            FileSystemResource fsr = new FileSystemResource(file);
            try (InputStream is = fsr.getInputStream()) {
                properties.load(is);
                properties.store(fsr.getOutputStream(), file.getPath());
            } catch (IOException e) {
                log.info("加载{}文件异常...", file.getName());
                throw new RuntimeException(e);
            }
        }
    }

    private void loadJsonFile(String parent, LangTypeEnum langType, String jsonStr) {
        File file = new File(parent + '/' + langType.getValue().toLowerCase(), "label.json");
        if (!FileUtil.exist(file.getPath())) {
            FileUtil.touch(file.getPath());
        }
        FileUtil.writeUtf8String(jsonStr, file.getPath());
    }

}
