package com.xzg.test.dao;

import com.xzg.test.entity.Stock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author XieZG
 * @Description:
 * @date 21-6-8下午3:55
 */
public interface StockDao extends CrudRepository<Stock, Long> {
    @Modifying(clearAutomatically = true)
    @Query("update Stock stock set stock.count =stock.count-1 where stock.id =:stockId")
    int buy(@Param("stockId") Long stockId);
}
