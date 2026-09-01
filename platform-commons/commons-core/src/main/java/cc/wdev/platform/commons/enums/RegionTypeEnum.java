package cc.wdev.platform.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author elvea
 */
@Getter
@AllArgsConstructor
public enum RegionTypeEnum implements BaseEnum<String> {
    COUNTRY("COUNTRY", "国家、地区"),
    PROVINCE("PROVINCE", "省、自治区、直辖市"),
    CITY("CITY", "地级市、地区、自治州、盟"),
    COUNTY("COUNTY", "市辖区、县级市、县");

    private final String value;
    private final String description;
}
