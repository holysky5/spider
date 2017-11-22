package com.gs.spider.model.biz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/11/10 12:37
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightclubSku {
    private String sku;
    private String size;
    private String price;
}
