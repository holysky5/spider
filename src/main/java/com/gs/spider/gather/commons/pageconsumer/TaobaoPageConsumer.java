package com.gs.spider.gather.commons.pageconsumer;

import com.gs.spider.gather.commons.PageConsumer;
import com.gs.spider.model.async.Task;
import com.gs.spider.model.biz.TaobaoProduct;
import com.gs.spider.model.commons.SpiderInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Title: 淘宝的页面抓取逻辑 </p>
 * <p>Description:
 * <p>
 * <p>
 * <p>
 * </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/10/24 16:21
 * @version 1.0
 */
public class TaobaoPageConsumer implements PageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TaobaoPageConsumer.class);


    public static class TaobaoProductResolver{
        List<TaobaoProduct> items = new ArrayList<>();
        List<String> links;

        public TaobaoProductResolver(String html) {
            Document document = Jsoup.parse(html);
            document.select("div.items div.item").forEach((el)->{
                TaobaoProduct product = new TaobaoProduct();
                product.setImageUrl(el.select("img").attr("data-src"));
                Elements aLink = el.select("div.title>a");
                product.setName(aLink.text().trim());
                product.setUrl( aLink.attr("href"));
                items.add(product);
            });
        }
        public List<TaobaoProduct> getItems() {
            return items;
        }

        public List<String> getItemLinks() {
            if (links == null) {
                links = items.stream().map((s) -> s.getUrl())
                             .map((s)->{
                                 int indexOfSharp = s.indexOf("#");
                                 return s.substring(0, indexOfSharp == -1 ? s.length() : indexOfSharp);
                             })
                             .collect(Collectors.toList());
            }
            return links;
        }
    }


    @Override
    public void accept(final Page page, final SpiderInfo info, final Task task) {
        try {
            logger.info("开始抓取:{}",page.getUrl().get());
            long start = System.currentTimeMillis();
            //本页是否是startUrls里面的页面
            final boolean startPage = info.getStartURL().contains(page.getUrl().get());
            //加载列表页
            if (page.getRequest().getExtra("detail") == null) {
                TaobaoProductResolver taobaoProductResolver = new TaobaoProductResolver(page.getHtml().get());
                logger.info("{} url列表读取了url链接数:{}", page.getUrl().get(), taobaoProductResolver.getItemLinks().size());
                for (final String link : taobaoProductResolver.getItemLinks()) {
                    Request request = new Request(link);
                    Map<String,Object> extras = new HashMap<>(5);
                    extras.put("detail", true);
                    request.setExtras(extras);
                    page.addTargetRequest(request);
                }
                page.setSkip(true);
                return;
            }else{
                // do Detail


            }
//            //去掉startUrl页面
//            if (startPage) {
//                page.setSkip(true);
//            }
//            page.putField("url", page.getUrl().get());
//            page.putField("domain", info.getDomain());
//            page.putField("spiderInfoId", info.getId());
//            page.putField("gatherTime", new Date());
//            page.putField("spiderInfo", info);
//            page.putField("spiderUUID", task.getTaskId());
//            if (info.isSaveCapture()) {
//                page.putField("rawHTML", page.getHtml().get());
//            }
//            //转换静态字段
//            if (info.getStaticFields() != null && info.getStaticFields().size() > 0) {
//                Map<String, String> staticFieldList = Maps.newHashMap();
//                for (SpiderInfo.StaticField staticField : info.getStaticFields()) {
//                    staticFieldList.put(staticField.getName(), staticField.getValue());
//                }
//                page.putField("staticField", staticFieldList);
//            }
//            page.putField("content", "列表页");
////            if (info.isNeedContent() && StringUtils.isBlank(content)) {//if the content is blank ,skip it!
////                page.setSkip(true);
////                return;
////            }
//            //抽取标题
////            String title;
////            if (!StringUtils.isBlank(info.getTitleXPath())) {//提取网页标题
////                title = page.getHtml().xpath(info.getTitleXPath()).get();
////            } else if (!StringUtils.isBlank(info.getTitleReg())) {
////                title = page.getHtml().regex(info.getTitleReg()).get();
////            } else {//如果不写默认是title
////                title = page.getHtml().getDocument().title();
////            }
//            page.putField("title", "列表页");
//            if (info.isNeedTitle() && StringUtils.isBlank(title)) {//if the title is blank ,skip it!
//                page.setSkip(true);
//                return;
//            }
//
//            //抽取动态字段
//            Map<String, Object> dynamicFields = Maps.newHashMap();
//            for (SpiderInfo.FieldConfig conf : info.getDynamicFields()) {
//                String fieldName = conf.getName();
//                String fieldData = null;
//                if (!StringUtils.isBlank(conf.getXpath())) {//提取
//                    fieldData = page.getHtml().xpath(conf.getXpath()).get();
//                } else if (!StringUtils.isBlank(conf.getRegex())) {
//                    fieldData = page.getHtml().regex(conf.getRegex()).get();
//                }
//                dynamicFields.put(fieldName, fieldData);
//                if (conf.isNeed() && StringUtils.isBlank(fieldData)) {//if the field data is blank ,skip it!
//                    page.setSkip(true);
//                    return;
//                }
//            }
//            page.putField(Constants.DYNAMIC_FIELD, dynamicFields);
//
//            //抽取分类
//            String category = null;
//            if (!StringUtils.isBlank(info.getCategoryXPath())) {//提取网页分类
//                category = page.getHtml().xpath(info.getCategoryXPath()).get();
//            } else if (!StringUtils.isBlank(info.getCategoryReg())) {
//                category = page.getHtml().regex(info.getCategoryReg()).get();
//            }
//            if (StringUtils.isNotBlank(category)) {
//                page.putField("category", category);
//            } else {
//                page.putField("category", info.getDefaultCategory());
//            }
//
//            //抽取发布时间
//            String publishTime = null;
//            if (!StringUtils.isBlank(info.getPublishTimeXPath())) {//文章发布时间规则
//                publishTime = page.getHtml().xpath(info.getPublishTimeXPath()).get();
//            } else if (!StringUtils.isBlank(info.getPublishTimeReg())) {
//                publishTime = page.getHtml().regex(info.getPublishTimeReg()).get();
//            }
//            Date publishDate;
//            SimpleDateFormat simpleDateFormat = null;
//            //获取SimpleDateFormat时间匹配模板,首先检测爬虫模板指定的,如果为空则自动探测
//            if (StringUtils.isNotBlank(info.getPublishTimeFormat())) {
//                //使用爬虫模板指定的时间匹配模板
//                if (StringUtils.isNotBlank(info.getLang())) {
//                    simpleDateFormat = new SimpleDateFormat(info.getPublishTimeFormat(), new Locale(info.getLang(), info.getCountry()));
//                } else {
//                    simpleDateFormat = new SimpleDateFormat(info.getPublishTimeFormat());
//                }
//            }
////            else if (StringUtils.isBlank(publishTime) && info.isAutoDetectPublishDate()) {
////                //如果没有使用爬虫模板抽取到文章发布时间,或者选择了自动抽时间,则进行自动发布时间探测
////                for (Pair<String, SimpleDateFormat> formatEntry : datePattern) {
////                    publishTime = page.getHtml().regex(formatEntry.getKey(), 0).get();
////                    //如果探测到了时间就退出探测
////                    if (StringUtils.isNotBlank(publishTime)) {
////                        simpleDateFormat = formatEntry.getValue();
////                        break;
////                    }
////                }
////            }
//            //解析发布时间成date类型
//            if (simpleDateFormat != null && StringUtils.isNotBlank(publishTime)) {
//                try {
//                    publishDate = simpleDateFormat.parse(publishTime);
//                    //如果时间没有包含年份,则默认使用当前年
//                    if (!simpleDateFormat.toPattern().contains("yyyy")) {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(publishDate);
//                        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
//                        publishDate = calendar.getTime();
//                    }
//                    page.putField("publishTime", publishDate);
//                } catch (ParseException e) {
//                    logger.debug("解析文章发布时间出错,source:" + publishTime + ",format:" + simpleDateFormat.toPattern());
//                    task.setDescription("解析文章发布时间出错,url:%s source:%s ,format:%s", page.getUrl().toString(), publishTime, simpleDateFormat.toPattern());
//                    if (info.isNeedPublishTime()) {//if the publishTime is blank ,skip it!
//                        page.setSkip(true);
//                        return;
//                    }
//                }
//            } else if (info.isNeedPublishTime()) {//if the publishTime is blank ,skip it!
//                page.setSkip(true);
//                return;
//            }
//            ///////////////////////////////////////////////////////
////            if (info.isDoNLP()) {//判断本网站是否需要进行自然语言处理
////                //进行nlp处理之前先去除标签
////                String contentWithoutHtml = content.replaceAll("<br/>", "");
////                try {
////                    //抽取关键词,10个词
////                    page.putField("keywords", keywordsExtractor.extractKeywords(contentWithoutHtml));
////                    //抽取摘要,5句话
////                    page.putField("summary", summaryExtractor.extractSummary(contentWithoutHtml));
////                    //抽取命名实体
////                    page.putField("namedEntity", namedEntitiesExtractor.extractNamedEntity(contentWithoutHtml));
////                } catch (Exception e) {
////                    e.printStackTrace();
////                    LOG.error("对网页进行NLP处理失败,{}", e.getLocalizedMessage());
////                    task.setDescription("对网页进行NLP处理失败,%s", e.getLocalizedMessage());
////                }
////            }
//
//
//            //本页面处理时长
//            page.putField("processTime", System.currentTimeMillis() - start);
        } catch (Exception e) {
            String err = String.format("处理网页出错，%s", e.toString());
            logger.error(err, e);
            task.setDescription(err);
        }
    }
}
