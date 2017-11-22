package com.gs.spider.gather.commons.pageconsumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.gs.spider.model.async.Task;
import com.gs.spider.model.biz.FlightclubBean;
import com.gs.spider.model.commons.SpiderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * https://www.flightclub.com 网站抓取器
 * 页面有两种形式
 * 对于形如https://www.flightclub.com/air-jordans?id=34&p=48,包含id参数的,那么为列表页,会解析出商品链接,加入爬虫队列
 * 然后就是详情页,详情页解析商品列表
 *
 * @author holysky.zhao 2017/11/7 17:01
 * @version 1.0
 */
public class FlightclubPageConsumer extends AbstractPageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(FlightclubPageConsumer.class);
    private static final String LINK_KEY = "LINK_LIST";


    private static Set<String> NO_PICTURES = new HashSet<>();

    static {
        try {
            String[] noPicTxt=new String(Files.readAllBytes(Paths.get(FlightclubPageConsumer.class.getResource("/no_picture.txt").toURI())))
                    .split("\n");
            NO_PICTURES.addAll(Arrays.asList(noPicTxt));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    private static boolean isListPageUrl(final Page page) {
        if (page.getUrl().get().contains("p=")) {
            return true;
        }else if(page.getHtml().$("ul.products-grid").match()){
            return true;
        }
        return false;
    }

    @Override
    public void accept(final Page page, final SpiderInfo info, final Task task) {
        try {
            long start = System.currentTimeMillis();
            boolean isListPage = isListPageUrl(page);
            String url = page.getUrl().get();
            List<String> extraLinks = new ArrayList<>();
            if (isListPage) {
                //添加所有商品的链接
//                List<String> detailUrls = page.getHtml().$("ul.products-grid>li>a", "href").all();
//                extraLinks.addAll(detailUrls);
//                logger.info("商品链接数 {}",detailUrls.size());
//
//                if (isFirstPage(page)) {
//                    //添加所有下一页的链接
//                    List<String> nextPageUrls = page.getHtml().$("ul.pages-dropdown>li>a", "href").all();
//                    extraLinks.addAll(nextPageUrls);
//                    logger.info("列表页扫描,总页数 {}",nextPageUrls.size());
//                }
                extraLinks.addAll(NO_PICTURES);
                page.setSkip(true);
            } else {
                // 明细页面, 这里解析字段和值
                page.putField("url", url);
                page.putField("domain", info.getDomain());
                page.putField("spiderInfoId", info.getId());
                page.putField("gatherTime", new Date());
                page.putField("spiderInfo", info);
                page.putField("spiderUUID", task.getTaskId());
                //保存原始的html
                page.putField("rawHTML", page.getHtml().get());

                String title = page.getHtml().$("title", "text").get();
                page.putField("title", title);

                String category = page.getHtml().$("div.product-name:eq(0) h2", "text").get();
                page.putField("category", category);


                String name = page.getHtml().$("div.product-name:eq(0) h1", "text").get();
                page.putField("content", name);
                //货号,颜色以及年份
                List<String> attrs = page.getHtml().$("ul.product-attribute-list li", "text").all();
//                page.putField("publishTime", attrs.size() >2 ? attrs.get(2) : null);
                page.putField("publishTime", new Date());
                //其他颜色
                List<String> otherColorLinks = page.getHtml().$("div.other-colors-inner a", "href").all();
                if (otherColorLinks.size() > 0) {
                    extraLinks.addAll(otherColorLinks);
                }
                //图片
                List<Selectable> nodes = page.getHtml().$("ul.bxslider li").nodes();
                List<String> picUrls = new ArrayList<>();
                List<String> picRetinaUrls = new ArrayList<>();
                for (final Selectable node : nodes) {
//            ((HtmlNode) node).nodes()..attr("data-url-zoom")
                    picRetinaUrls.add(node.$("a", "data-url-zoom").get());
                    picUrls.add(node.$("img", "src").get());
                }

                //可能只有一个图片的情况
                if (picRetinaUrls.size() == 0) {
                    String src = page.getHtml().$("div.desktop-product-image img", "src").get();
                    if (src != null) {
                        picRetinaUrls.add(src);
                    }
                }

                //价格
                String price = page.getHtml().$("div.product-shop span.price:eq(0)", "text").get();
                List<String> allSize = page.getHtml().$("ul.list-size:eq(0) button", "text").all();

                //price json
                String priceJson = page.getHtml().$("div.product-data-mine", "data-lookup").get();


                //转换静态字段
                if (info.getStaticFields() != null && info.getStaticFields().size() > 0) {
                    Map<String, String> staticFieldList = Maps.newHashMap();
                    for (SpiderInfo.StaticField staticField : info.getStaticFields()) {
                        staticFieldList.put(staticField.getName(), staticField.getValue());
                    }
                    page.putField("staticField", staticFieldList);
                }
                //抽取动态字段

                //默认字段为了让页面显示的更好看
                page.putField("summary", ImmutableList.of("-"));
                page.putField("keywords", ImmutableList.of("-"));
                FlightclubBean bean = new FlightclubBean(url,name, category, attrs.size() > 0 ? attrs.get(0) : null,
                        attrs.size() > 1 ? attrs.get(1) : null, attrs.size() > 2 ? attrs.get(2) : null,
                        picUrls, picRetinaUrls, price, allSize,
                        priceJson);
                page.putField(DYNAMIC_FIELD,bean.toMap());
            }
            addExtraLinks(page, task, extraLinks);
            //本页面处理时长
            page.putField("processTime", System.currentTimeMillis() - start);
        } catch (Exception e) {
            task.setDescription("处理网页出错，%s", e.toString());
        }
    }

    /**
     * 判断是否是列表页的第一页,这个函数只在判断当前页是列表页的前提下才有用
     * 当包含参数p=1或者不包含p=这样的页码时认为是列表页第一页
     */
    private boolean isFirstPage(final Page page) {
        String url = page.getUrl().get();
        if (url.contains("p=1") || !url.contains("p=")) {
            return true;
        }
        return false;
    }

    /**
     * 添加额外需要抓取的链接
     */
    private void addExtraLinks(final Page page, final Task task, final List<String> extraLinks) {
        page.addTargetRequests(extraLinks);
        List<String> urls;
        if ((urls = ((List<String>) task.getExtraInfoByKey(LINK_KEY))) != null) {
            urls.addAll(extraLinks);
        } else {
            task.addExtraInfo(LINK_KEY, extraLinks);
        }
    }
}
