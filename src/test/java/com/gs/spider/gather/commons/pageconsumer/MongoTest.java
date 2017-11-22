package com.gs.spider.gather.commons.pageconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/11/8 16:39
 * @version 1.0
 */
public class MongoTest extends BaseSpringTest{
    @Autowired
    MongoTemplate template;

    @org.junit.Test
    public void test() throws Exception {
        long count = template.count(new BasicQuery("{}"), "cms_bt_product_c001");
        System.out.println(count);
    }
}
