package com.firefighter.aenitto.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;


public class CursorPagination {
    public static HttpHeaders setHeaders(PaginatedResponse paginatedResponse, String endpoint) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.set("X-Count", String.valueOf(paginatedResponse.pageCount()));
        String cursor = paginatedResponse.nextCursor()
                .orElseGet(() -> "");
        String linkHeaderValue = cursor.isEmpty() ? "" : "";
        StringBuilder sb = new StringBuilder();
        sb.append("<" + endpoint + ">");
        sb.append(" rel: \"");
        sb.append(LinkRelation.FIRST.getValue());
        sb.append(" \";");

        if (!cursor.isEmpty()) {
            sb.append(sb.append("<" + endpoint + ">"));
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    public static class RequestInfo {
        private String endpoint;
        private HashMap<String, String> queryParams;

        public String getFullPath() {
            StringBuilder sb = new StringBuilder();
            sb.append(endpoint);
            sb.append("?");
            for (String qp : queryParams.keySet()) {
                sb.append(qp + "=" + queryParams.get(qp) + "&");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        }
    }
}
