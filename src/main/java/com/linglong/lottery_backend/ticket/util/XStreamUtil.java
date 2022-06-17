package com.linglong.lottery_backend.ticket.util;

import com.thoughtworks.xstream.XStream;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
public class XStreamUtil {

    public static <T> T toBean(String xml,Class<T> clazz) {
        try {
            XStream xstream = new XStream();
            xstream.processAnnotations(clazz);
            xstream.autodetectAnnotations(true);
            xstream.setClassLoader(clazz.getClassLoader());
            return (T) xstream.fromXML(xml);
        } catch (Exception e) {
            log.error("[XStream]XML转对象出错:{}", e);
        }
        return null;
    }

    public static String toXml(Object bean) {
        try {
            XStream xstream = new XStream();
            xstream.autodetectAnnotations(true);
            xstream.ignoreUnknownElements();
            return xstream.toXML(bean);
        } catch (Exception e) {
            log.error("[XStream]对象转XML出错:{}", e);
        }
        return null;
    }
}
