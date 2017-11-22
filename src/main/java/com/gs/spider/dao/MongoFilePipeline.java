package com.gs.spider.dao;

import com.gs.spider.daomongo.FlightclubDao;
import com.gs.spider.model.biz.FlightclubBean;
import com.gs.spider.model.commons.Webpage;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/11/8 19:24
 * @version 1.0
 */
@Component
public class MongoFilePipeline implements Pipeline {

    private static final Logger logger = LoggerFactory.getLogger(MongoFilePipeline.class);
    private String FLIGHT_CLUB_DOMAIN = "www.flightclub.com";

    final FlightclubDao flightclubDao;

    @Autowired
    public MongoFilePipeline(final FlightclubDao flightclubDao) {
        this.flightclubDao = flightclubDao;
    }

    @Override
    public void process(final ResultItems resultItems, final Task task) {
        Webpage webpage = CommonWebpagePipeline.convertResultItems2Webpage(resultItems);
        if (FLIGHT_CLUB_DOMAIN.equals(webpage.getDomain())) {
            Map<String, Object> dynamicFields = webpage.getDynamicFields();
            try {
                if (dynamicFields != null && dynamicFields.keySet().size()>0) {
                    FlightclubBean bean = new FlightclubBean();
                    BeanUtils.populate(bean,dynamicFields);
                    if (bean.getName() != null) {
                        flightclubDao.save(bean);
                    }
                }
            } catch (Exception e) {
                logger.error("保存FlightclubBean出错", e);
            }
        }
    }
}
