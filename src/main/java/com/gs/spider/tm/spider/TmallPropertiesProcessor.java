package com.gs.spider.tm.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author holysky.zhao 2017/11/20 20:18
 * @version 1.0
 */
public class TmallPropertiesProcessor implements PageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(TmallPropertiesProcessor.class);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000).setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
    @Override
    public void process(final Page page) {
        System.out.println(page.getUrl().get()+"attr list");
        Selectable el = page.getHtml().$("#J_AttrUL");
        if (el.match()) {
            List<String> attrs =el.$("li", "text").all();
            Map<String, String> attrMap = new HashMap<>();
            for (final String attr : attrs) {
                String[] array = attr.split(":|：");
                if (array.length == 2) {
                    attrMap.put(array[0], array[1]);
                }else{
                    logger.error("错误的属性值，不能放入");
                }
            }
            logger.info("正确获取到页面 {} 的属性值，个数：{}",page.getUrl().get(),attrMap.size());
            page.putField("attrMap",attrMap);
        }else{
            logger.error("未能正确获取到页面属性值：{}",page.getUrl().get());
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
