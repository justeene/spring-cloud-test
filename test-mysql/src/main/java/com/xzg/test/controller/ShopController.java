package com.xzg.test.controller;

import com.xzg.test.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author XieZG
 * @Description:
 * @date 21-6-8下午3:57
 */
@Controller
public class ShopController {
    @Autowired
    private StockService stockService;

    @ResponseBody
    @GetMapping(path = "/buy/{stockId}")
    public String buy(@PathVariable("stockId") Long stockId) {
        boolean success = stockService.buy(stockId);
        return success ? "OK" : "NO";
    }
}
