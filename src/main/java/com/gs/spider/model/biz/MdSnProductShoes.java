package com.gs.spider.model.biz;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author holysky.zhao 2017/11/20 20:15
 * @version 1.0
 */
@Data
@Document(collection = "md_sn_product_shoes")
public class MdSnProductShoes {

    @Id
    private String id;

    private String brand;
    private String modelNo;
    private List<Product> products;

    private List<String> productUrls;
    /**
     * 是否按关键字抓取过了
     */
    private boolean fetchKeywords;
    /**
     * 是否抓取过属性值了
     */
    private boolean fetchAttrs;
    private String keywordHtml;
    private boolean fetched;
    private int merged;

    @Data
    public static class Product {

        private String code;
        private String originalCode;
        private String brand;
        private String productNameEn;
        private String model;
        private String color;
        private String codeDiff;
        private String shortDesEn;
        private String longDesEn;
        private String lastReceivedOn;
        private int quantity;
        private String clientProductUrl;
        private String productType;
        private String sizeType;
        private int isMasterMain;
        private String translateStatus;
        private String hsCodeStatus;
        private String usageEn;
        private int appSwitch;
        private String categoryStatus;
        private String categorySetter;
        private String categorySetTime;
        private String clientMsrpPrice;
        private String clientNetPrice;
        private double weightLB;
        private int weightG;
        private double weightKG;
        private int skuCnt;
        private String hsCodePrivate;
        private String materialEn;
        private String origProductType;
        private String origSizeType;
        @JsonProperty("abstract")
        private String abstractX;
        private String googleCategory;
        private String googleDepartment;
        private String priceGrabberCategory;
        private String urlkey;
        private String taxable;
        private String accessory;
        private String colorMap;
//    private List<Images1Bean> images1;
//    private List<?> images2;
//    private List<?> images6;
    }
}
