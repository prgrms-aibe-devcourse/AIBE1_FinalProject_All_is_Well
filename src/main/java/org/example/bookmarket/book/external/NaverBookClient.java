package org.example.bookmarket.book.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookmarket.book.entity.Book;
import org.example.bookmarket.common.handler.exception.CustomException;
import org.example.bookmarket.common.handler.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.StringReader;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverBookClient {

    private static final String API_URL = "https://openapi.naver.com/v1/search/book_adv.xml";

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public Book fetchBook(String isbnInput) {
        String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("d_isbn", isbnInput)
                .build()
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            log.error("Naver API credentials are missing. clientId={}, clientSecretPresent={}", clientId, clientSecret != null && !clientSecret.isBlank());
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return parseXml(response.getBody(), isbnInput);
            } else {
                log.error("Naver API call unsuccessful. status={}, body={}", response.getStatusCode(), response.getBody());
            }
        } catch (RestClientResponseException e) {
            log.error("Naver API request failed: status={}, body={}", e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        } catch (Exception e) {
            log.error("Failed to call Naver Book API", e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }

        return null;
    }

    private Book parseXml(String xml, String isbnInput) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 🛡️ 보안 설정 (XXE 공격 방지)
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            NodeList items = doc.getElementsByTagName("item");
            if (items.getLength() == 0) {
                throw new CustomException(ErrorCode.BOOK_NOT_FOUND);
            }

            Element item = (Element) items.item(0);

            String title = stripHtml(getText(item, "title"));
            String author = getText(item, "author");
            String publisher = getText(item, "publisher");
            String pubdate = getText(item, "pubdate");
            String price = getText(item, "price");
            String discount = getText(item, "discount");
            String image = getText(item, "image");
            String description = getText(item, "description");
            String isbnValue = parseIsbn(getText(item, "isbn"));
            String finalIsbn = isbnValue != null ? isbnValue : isbnInput;

            Item temp = new Item(title, author, publisher, pubdate, price, discount, image, description);

            return Book.builder()
                    .isbn(finalIsbn)
                    .title(title)
                    .author(author)
                    .publisher(publisher)
                    .publicationYear(parseYear(pubdate))
                    .newPrice(resolvePrice(temp))
                    .description(description)
                    .coverImageUrl(image)
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse Naver Book API response", e);
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }
    }

    private String getText(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        if (nodes.getLength() == 0 || nodes.item(0) == null) return null;
        return nodes.item(0).getTextContent();
    }

    private String parseIsbn(String value) {
        if (value == null || value.isBlank()) return null;
        String[] parts = value.split("\\s+");
        for (String part : parts) {
            if (part.length() == 13) return part;
        }
        return parts.length > 0 ? parts[0] : null;
    }

    private String stripHtml(String text) {
        return text == null ? null : text.replaceAll("<[^>]*>", "");
    }

    private Integer parseYear(String pubdate) {
        if (pubdate == null || pubdate.length() < 4) return null;
        try {
            return Integer.parseInt(pubdate.substring(0, 4));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer resolvePrice(Item item) {
        Integer discount = parseInt(item.discount());
        if (discount != null && discount > 0) {
            return discount;
        }
        return parseInt(item.price());
    }

    // JDK 17 기준: 간단한 데이터 전용 record 사용
    private record Item(
            String title,
            String author,
            String publisher,
            String pubdate,
            String price,
            String discount,
            String image,
            String description
    ) {
    }
}

