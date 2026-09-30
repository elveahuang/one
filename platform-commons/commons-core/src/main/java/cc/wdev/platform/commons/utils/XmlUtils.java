package cc.wdev.platform.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * @author elvea
 */
@Slf4j
public abstract class XmlUtils {

    public static List<Element> getElements(Element element, String elementName) {
        if (element != null) {
            log.info("getElements. Parent Element [{}]. elementName [{}].", element.getName(), elementName);
            return element.elements(elementName);
        }
        return Lists.newArrayList();
    }

    public static Element getElement(Element element, String elementName) {
        if (element != null) {
            log.info("getElement. Parent Element [{}]. elementName [{}].", element.getName(), elementName);
            return element.element(elementName);
        }
        return null;
    }

    public static String getElementValue(@Nullable Element element) {
        if (element != null) {
            log.info("getElementValue. Element [{}].", element.getName());
            return element.getStringValue();
        }
        return "";
    }

    public static List<Attribute> getAttributes(Element element) {
        if (element != null) {
            log.info("getAttributes. Element [{}].", element.getName());
            return element.attributes();
        }
        return Lists.newArrayList();
    }

    public static Attribute getAttribute(Element element, String attributeName) {
        if (element != null) {
            log.info("getAttribute. Element [{}]. attributeName [{}].", element.getName(), attributeName);
            return element.attribute(attributeName);
        }
        return null;
    }

    public static String getAttributeValue(Element element, String attributeName) {
        if (element != null) {
            log.info("getAttributeValue. Element [{}]. attributeName [{}].", element.getName(), attributeName);
            Attribute attribute = getAttribute(element, attributeName);
            return (attribute != null) ? attribute.getValue() : "";
        }
        return "";
    }

}
