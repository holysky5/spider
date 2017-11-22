package com.gs.spider.model.biz;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document(collection = "flight_club_product")
public  class FlightclubBean implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(FlightclubBean.class);

    public FlightclubBean(
            final String url,
            final String name, final String brand, final String code, final String color, final String startTime, final List<String> picUrls, final List<String> picRetinaUrls, final String price, final List<String> sizes,String priceJson) {
        this.productUrl=url;
        this.name = name;
        this.brand = brand;
        this.code = code;
        this.color = color;
        this.startTime = startTime;
        this.picUrls = picUrls;
        this.picRetinaUrls = picRetinaUrls;
        this.price = price;
        this.sizes = sizes;
        this.priceJson = priceJson;
    }

    @Id
    private String _id;

    private String productUrl;
    /** 产品名称 */
    private String name;
    /** 品牌 */
    private String brand;
    /** code */
    private String code;
    /** 颜色 */
    private String color;
    /** 上市年份 */
    private String startTime;
    /** 小图 */
    private List<String> picUrls;
    /** 大图 */
    private List<String> picRetinaUrls;
    /** 价格*/
    private String price;
    /** 尺码*/
    private List<String> sizes;

    private String priceJson;

    private List<FlightclubSku> skus;

    public Map<String, Object> toMap() {
        try {
            Map<String, Object>  describe = PropertyUtils.describe(this);
            describe.remove("class");
            return describe;
        } catch (Exception e) {
            logger.error("FlightclubBean to map error", e);
        }
        return null;
    }
}