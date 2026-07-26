package com.huarenzaimeng.module.payment.service;

import com.huarenzaimeng.config.WxProperties;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WxPayService {

    private static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private static final String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    private final WxProperties wxProperties;
    private final OkHttpClient httpClient;

    public WxPayService(WxProperties wxProperties) {
        this.wxProperties = wxProperties;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public Map<String, String> unifiedOrder(String orderNo, int totalFeeCents, String openid, String body) {
        Map<String, String> params = new TreeMap<>();
        params.put("appid", wxProperties.getAppId());
        params.put("mch_id", wxProperties.getMchId());
        params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
        params.put("body", body);
        params.put("out_trade_no", orderNo);
        params.put("total_fee", String.valueOf(totalFeeCents));
        params.put("spbill_create_ip", "127.0.0.1");
        params.put("notify_url", "https://huarenzaimeng-api.ap-shanghai.run.tcloudbase.com/api/wx/pay/callback");
        params.put("trade_type", "JSAPI");
        params.put("openid", openid);
        params.put("sign", sign(params));

        String xml = mapToXml(params);
        log.info("WxPay unified order request: orderNo={}, amount={} cents", orderNo, totalFeeCents);

        // In production, this would make the actual HTTP call
        // For P1 skeleton, return mock response structure
        Map<String, String> result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("result_code", "SUCCESS");
        result.put("prepay_id", "mock_prepay_" + orderNo);
        return result;
    }

    public Map<String, String> refund(String orderNo, String refundNo, int totalFeeCents, int refundFeeCents, String reason) {
        Map<String, String> params = new TreeMap<>();
        params.put("appid", wxProperties.getAppId());
        params.put("mch_id", wxProperties.getMchId());
        params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
        params.put("out_trade_no", orderNo);
        params.put("out_refund_no", refundNo);
        params.put("total_fee", String.valueOf(totalFeeCents));
        params.put("refund_fee", String.valueOf(refundFeeCents));
        params.put("refund_desc", reason);
        params.put("sign", sign(params));

        log.info("WxPay refund request: orderNo={}, refundNo={}, amount={} cents", orderNo, refundNo, refundFeeCents);

        Map<String, String> result = new HashMap<>();
        result.put("return_code", "SUCCESS");
        result.put("result_code", "SUCCESS");
        result.put("refund_id", "mock_refund_" + refundNo);
        return result;
    }

    public boolean verifySign(Map<String, String> params) {
        String receivedSign = params.get("sign");
        if (receivedSign == null) {
            return false;
        }
        Map<String, String> filtered = new TreeMap<>(params);
        filtered.remove("sign");
        String expectedSign = sign(filtered);
        return expectedSign.equals(receivedSign);
    }

    private String sign(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        sb.append("key=").append(wxProperties.getMchKey());

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02X", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Sign calculation failed", e);
        }
    }

    private String mapToXml(Map<String, String> params) {
        StringBuilder sb = new StringBuilder("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("<").append(entry.getKey()).append(">")
                    .append(entry.getValue())
                    .append("</").append(entry.getKey()).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }
}
