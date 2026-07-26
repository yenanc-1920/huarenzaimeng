package com.huarenzaimeng.module.payment.controller;

import com.huarenzaimeng.module.payment.service.WxPayService;
import com.huarenzaimeng.module.recharge.service.OrderService;
import com.huarenzaimeng.module.recharge.service.RechargeService;
import com.huarenzaimeng.module.recharge.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wx/pay")
public class WxPayCallbackController {

    private final WxPayService wxPayService;
    private final OrderService orderService;
    private final RechargeService rechargeService;

    public WxPayCallbackController(WxPayService wxPayService,
                                   OrderService orderService,
                                   RechargeService rechargeService) {
        this.wxPayService = wxPayService;
        this.orderService = orderService;
        this.rechargeService = rechargeService;
    }

    @PostMapping("/callback")
    public String payCallback(@RequestBody String xmlBody) {
        log.info("WxPay callback received");

        // Parse XML and verify sign (simplified for P1)
        // In production: parse XML -> verify sign -> extract out_trade_no + transaction_id
        Map<String, String> params = parseXml(xmlBody);

        if (!wxPayService.verifySign(params)) {
            log.error("WxPay callback sign verification failed");
            return failXml("SIGN_ERROR");
        }

        String orderNo = params.get("out_trade_no");
        String transactionId = params.get("transaction_id");
        String resultCode = params.get("result_code");

        if (!"SUCCESS".equals(resultCode)) {
            log.warn("WxPay callback result not success: orderNo={}, resultCode={}", orderNo, resultCode);
            return successXml();
        }

        try {
            orderService.markPaid(orderNo, transactionId);
            Order order = orderService.getByOrderNo(orderNo);
            if (order != null) {
                rechargeService.executeTopup(order);
            }
        } catch (Exception e) {
            log.error("WxPay callback processing error: orderNo={}", orderNo, e);
        }

        return successXml();
    }

    private String successXml() {
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    private String failXml(String msg) {
        return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[" + msg + "]]></return_msg></xml>";
    }

    private Map<String, String> parseXml(String xml) {
        // Simplified XML parsing - in production use proper XML parser
        Map<String, String> map = new java.util.HashMap<>();
        if (xml == null) return map;
        String content = xml.replaceAll("<!\\[CDATA\\[", "").replaceAll("]]>", "");
        String[] parts = content.split("<(?=/?)");
        for (String part : parts) {
            if (part.startsWith("/xml") || part.startsWith("xml") || part.trim().isEmpty()) continue;
            int gtIdx = part.indexOf('>');
            if (gtIdx > 0) {
                String key = part.substring(0, gtIdx).trim();
                String value = part.substring(gtIdx + 1).trim();
                if (!key.startsWith("/") && !value.isEmpty()) {
                    map.put(key, value);
                }
            }
        }
        return map;
    }
}
