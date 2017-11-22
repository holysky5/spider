package com.gs.spider.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gs.spider.daomongo.MdSnProductShoesDao;
import com.gs.spider.gather.commons.pageconsumer.BaseSpringTest;
import com.gs.spider.model.biz.MdSnProductShoes;
import com.gs.spider.tm.spider.TmallPropertiesProcessor;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.CloseableIterator;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/11/20 20:16
 * @version 1.0
 */
public class MdSnProductShoesDaoTest extends BaseSpringTest {

    private static final Logger logger = LoggerFactory.getLogger(MdSnProductShoesDaoTest.class);
    @Autowired
    MdSnProductShoesDao snProductShoesDao;
    @Test
    public void spiderTest() throws InterruptedException {

        List<Request> productUrls = new ArrayList<>();

        CloseableIterator<MdSnProductShoes> iterator = snProductShoesDao.stream(Query.query(where("fetchKeywords").is(true).andOperator(where("fetchAttrs").ne(true))));
        iterator.forEachRemaining((el)->{
            if (el.getProductUrls() == null || el.getProductUrls().size() == 0) {
                logger.info("{}的产品url数量为空,不需要处理",el.getModelNo());
            }else{
                //每个商品最多取前5个产品链接
                for (int i = 0;i<el.getProductUrls().size();i++) {
                    String url = el.getProductUrls().get(i);
                    if (isValidUrl(url)) {
                        Request request = new Request(url);
                        HashMap<String, Object> extra = Maps.newHashMap();
                        extra.put("id", el.getId());
                        request.setExtras(extra);
                        productUrls.add(request);
                    }
                }
            }
        });
        System.out.println("预加载{}"+productUrls.size());
        Spider.create(new TmallPropertiesProcessor())
              //从"https://github.com/code4craft"开始抓
              //开启5个线程抓取
              .addRequest(productUrls.toArray(new Request[productUrls.size()]))
              .setSpiderListeners(Lists.newArrayList(new SpiderListener() {
                  @Override
                  public void onSuccess(final Request request) {
                      System.out.println(request.getUrl() + " ok");
                  }
                  @Override
                  public void onError(final Request request) {
                      System.out.println(request.toString());
                  }
              }))
              .addPipeline((resultItems, task) -> {
                  Map<String,String> attrMap = resultItems.get("attrMap");
                  String id = (String) resultItems.getRequest().getExtra("id");
                 //do update
                  Query byId = Query.query(where("_id").is(new ObjectId(id)));
                  snProductShoesDao.update(byId,new Update().push("attrs",attrMap).set("fetchAttrs",true));
              })
              .thread(5).run();

    }

    private static boolean isValidUrl(String url) {
        try {
            new URL((url));
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

}