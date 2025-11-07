package com.backend.LasBestias.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponentsBuilder;

public class PaginationUtil {
    private PaginationUtil() {
    }

    public static HttpHeaders generatePaginationHttpHeaders(Page<?> page, String baseUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        String link = "";
        if (page.getNumber() + 1 < page.getTotalPages()) {
            String var10000 = generateUri(baseUrl, page.getNumber() + 1, page.getSize());
            link = "<" + var10000 + ">; rel=\"next\",";
        }

        if (page.getNumber() > 0) {
            link = link + "<" + generateUri(baseUrl, page.getNumber() - 1, page.getSize()) + ">; rel=\"prev\",";
        }

        int lastPage = 0;
        if (page.getTotalPages() > 0) {
            lastPage = page.getTotalPages() - 1;
        }

        link = link + "<" + generateUri(baseUrl, lastPage, page.getSize()) + ">; rel=\"last\",";
        link = link + "<" + generateUri(baseUrl, 0, page.getSize()) + ">; rel=\"first\"";
        headers.add("Link", link);
        return headers;
    }

    public static HttpHeaders setTotalCountPageHttpHeaders(Page<?> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        return headers;
    }

    private static String generateUri(String baseUrl, int page, int size) {
        return UriComponentsBuilder.fromUriString(baseUrl).queryParam("page", new Object[]{page}).queryParam("size", new Object[]{size}).toUriString();
    }
}

