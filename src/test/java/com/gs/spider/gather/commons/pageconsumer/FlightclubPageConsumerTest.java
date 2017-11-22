package com.gs.spider.gather.commons.pageconsumer;

import org.junit.Test;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.selector.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/11/7 19:48
 * @version 1.0
 */
public class FlightclubPageConsumerTest {

    String rawHtml=new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/html/flightclub_detail.html").toURI())));

    public FlightclubPageConsumerTest() throws IOException, URISyntaxException {
    }

    @Test
    public void testPage() throws Exception {
        Page page = new Page();
        Request request = new Request("http://test.html");
        page.setRequest(request);
        page.setRawText(rawHtml);
        page.setUrl(new PlainText("http://test.html"));
        String title = page.getHtml().$("title","text").get();
        String category = page.getHtml().$("div.product-name:eq(0) h2", "text").get();
        String name = page.getHtml().$("div.product-name:eq(0) h1", "text").get();
        //货号,颜色以及年份
        List<String> attrs = page.getHtml().$("ul.product-attribute-list li", "text").all();
        //其他颜色
        List<String> otherColorLinks =  page.getHtml().$("div.other-colors-inner a","href").all();

        //图片
        List<Selectable> nodes = page.getHtml().$("ul.bxslider li a").nodes();
        List<String> picUrls = new ArrayList<>();
        List<String> picRetinaUrls = new ArrayList<>();
        for (final Selectable node : nodes) {
//            ((HtmlNode) node).nodes()..attr("data-url-zoom")
            picRetinaUrls.add(node.$("","data-url-zoom").get());
            picUrls.add(node.$("img","src").get());
        }

        //价格
        String price = page.getHtml().$("div.product-shop span.price:eq(0)", "text").get();
        List<String> allSize = page.getHtml().$("ul.list-size:eq(0) button", "text").all();
        System.out.println("completed");
    }
}