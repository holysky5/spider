package com.gs.spider.model.biz;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Voyageone Inc.</p>
 *
 * @author holysky.zhao 2017/10/24 20:06
 * @version 1.0
 */
public class TaobaoProduct {

    private String imageUrl;
    private String name;
    private String url;

    public TaobaoProduct() {
    }

    public TaobaoProduct(final String imageUrl, final String name, final String url) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

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
