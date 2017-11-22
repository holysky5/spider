package com.gs.spider.dao;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.spider.gather.commons.pageconsumer.BaseSpringTest;
import com.gs.spider.model.biz.FlightclubBean;
import com.gs.spider.model.biz.FlightclubSku;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.util.CloseableIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.gs.spider.daomongo.FlightclubDao;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/11/8 17:32
 * @version 1.0
 */
public class FlightclubDaoTest extends BaseSpringTest {
    private static final Logger logger = LoggerFactory.getLogger(FlightclubDaoTest.class);
    @Autowired
    FlightclubDao flightclubDao;

    @Test
    public void testInsert() throws Exception {
        FlightclubBean entity = new FlightclubBean();
        entity.setName("123");
        flightclubDao.save(entity);
    }


    @Test
    public void testStream() throws Exception {
        try( CloseableIterator<FlightclubBean> iter = flightclubDao.stream()){
            while (iter.hasNext()) {
                FlightclubBean bean = iter.next();
                System.out.println(bean.getName());
            }
        }

    }

    @Test
    public void testUpdate() throws Exception {

        List<FlightclubBean> allBeans = flightclubDao.find(new BasicQuery("{}"));
        for (final FlightclubBean bean : allBeans) {
            if (bean.getPriceJson() == null) {
                System.out.println("error price json");
            }else{
                List<FlightclubSku> flightclubSkus = resloveSkus(bean.getPriceJson());
                System.out.println(bean.get_id());
                List<String> sizes = flightclubSkus.stream().map((el) -> el.getSize()).collect(Collectors.toList());
                bean.setSizes(sizes);
                bean.setSkus(flightclubSkus);
                flightclubDao.save(bean);
            }
        }
    }

    private List<FlightclubSku> resloveSkus(final String priceJson) throws IOException {
        JsonNode root = om.readTree(priceJson);
        List<FlightclubSku> skus = new ArrayList<>();
        root.fields().forEachRemaining((mainEntry) -> {
            if (!"-1".equals(mainEntry.getKey())) {
                mainEntry.getValue().fields().forEachRemaining((entry) -> {
                    JsonNode node = entry.getValue();
                    try {
                        Integer qty = node.get("qty").asInt();
                        if (qty > 0) {
                            String sku = node.get("sku").asText();
                            String size = node.get("size").asText();
                            String price = node.get("price").asText();
                            Matcher matcher = PRICE_PATTERN.matcher(price);
                            if (matcher.find()) {
                                skus.add(new FlightclubSku(sku, size, matcher.group(1)));
                            } else {
                                System.out.println("price resolve error");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error node:" + node.toString());
                    }
                });
            }
        });
        return skus;
    }
    Pattern PRICE_PATTERN = Pattern.compile("(\\$[^<]+)");
    static ObjectMapper om = new ObjectMapper();

    static {
        om.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }


}