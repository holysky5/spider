package com.gs.spider.gather.commons.pageconsumer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

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
 * @author holysky.zhao 2017/10/24 19:36
 * @version 1.0
 */
public class TaobaoPageConsumerTest {

    static class TaobaoProduct{
        public String imageUrl;
        public String name;
        public String url;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("TaobaoProduct{");
            sb.append("imageUrl='").append(imageUrl).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", url='").append(url).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Test
    public void testResoveList() throws Exception {
        String body = new String(Files.readAllBytes(Paths.get(this.getClass().getResource("/html/taobao_list.html").toURI())));
        Document document = Jsoup.parse(body);
        List<TaobaoProduct> items = new ArrayList<>();
        document.select("div.items div.item").forEach((el)->{
            TaobaoProduct product = new TaobaoProduct();
            product.imageUrl = el.select("img").attr("data-src");
            Elements aLink = el.select("div.title>a");
            product.name = aLink.text().trim();
            product.url = aLink.attr("href");
            items.add(product);
        });
        for (final TaobaoProduct item : items) {
            System.out.println(item);
        }
    }
}