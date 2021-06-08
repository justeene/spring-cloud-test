package com.xzg.test.service;

import com.xzg.test.dao.StockDao;
import com.xzg.test.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author XieZG
 * @Description:
 * @date 21-6-8下午4:10
 */
@Service
public class StockService {
    @Autowired
    private StockDao stockDao;

    @Transactional
    public void addStock(Stock stock) {
        stockDao.save(stock);
    }

    @Transactional
    public boolean buy(Long stockId) {
        return stockDao.buy(stockId) > 0;
    }
}
