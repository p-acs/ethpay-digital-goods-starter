package de.pacs.ethpay.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

public class EthpayURI {
    public static class Builder {
        private final EthpayURI base;

        public Builder(EthpayURI base) {
            this.base = base;
        }

        public Builder setRef(String ref) {
            base.ref = ref;
            return this;
        }

        public Builder setMessage(String message) {
            base.message = message;
            return this;
        }

        public Builder setCurrency(String currency) {
            base.currency = currency;
            return this;
        }

        public Builder setPrice(String price) {
            base.suggestedPrice = price;
            return this;
        }

        public String build() {
            StringBuilder builder = new StringBuilder();
            builder.append(base.getStoreContract()).append("/").append("purchase").append('/').append(base.getProductId()).append("?");
            try {
                builder.append(KEY_MESSAGE).append("=").append(URLEncoder.encode(base.getMessage(), Charset.defaultCharset().name()));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
            builder.append('&');
            builder.append(KEY_CURRENCY).append("=").append(base.getCurrency());
            builder.append('&');
            builder.append(KEY_AMOUNT).append("=").append(base.getSuggestedPrice());
            return builder.toString();
        }
    }

    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DATA = "data";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_CURRENCY = "currency";
    public static final String KEY_REF = "r";

    private long productId;
    private String suggestedPrice;
    private String storeContract;
    private String suggestedData;
    private String command;
    private String message;
    private String currency;
    private String ref;

    private String uri;

    public EthpayURI(String uri) {
        this.uri = uri;
        parse(uri);
    }

    private void parse(String uri) {
        if (uri.contains("?")) {
            String[] addressSplitTokens = uri.split("\\?", 2);
            parseCommand(addressSplitTokens[0]);
            parseParams(addressSplitTokens[1]);
        } else {
            parseCommand(uri);
        }

    }

    private void parseParams(String uri) {
        String[] params = uri.split("&");
        for (String paramPair : params) {
            String[] pair = paramPair.split("=");
            String key = pair[0];
            String value = pair[1];
            if (KEY_AMOUNT.equals(key)) {
                suggestedPrice = value;
            } else if (KEY_DATA.equals(key)) {
                try {
                    suggestedData = URLDecoder.decode(value, Charset.defaultCharset().name());
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException(e);
                }
            } else if (KEY_MESSAGE.equals(key)) {
                try {
                    message = URLDecoder.decode(value, Charset.defaultCharset().name());
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException(e);
                }
            } else if (KEY_CURRENCY.equals(key)) {
                currency = value;
            } else if (KEY_REF.equals(key)) {
                ref = value;
            }
        }

    }

    private void parseCommand(String uri) {
        StringTokenizer tokenizer = new StringTokenizer(uri, "/");
        storeContract = tokenizer.nextToken();
        command = tokenizer.nextToken();
        productId = Long.parseLong(tokenizer.nextToken());
    }

    public long getProductId() {
        return productId;
    }

    public String getSuggestedPrice() {
        return suggestedPrice;
    }

    public String getStoreContract() {
        return storeContract;
    }

    public String getSuggestedData() {
        return suggestedData;
    }

    public String getCommand() {
        return command;
    }

    public String getMessage() {
        return message;
    }

    public String getCurrency() {
        return currency;
    }

    public String getUri() {
        return uri;
    }

    public String getRef() {
        return ref;
    }
}
