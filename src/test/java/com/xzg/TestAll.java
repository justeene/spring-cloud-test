package com.xzg;

import com.github.javafaker.Faker;
import com.xzg.test.Application;
import com.xzg.test.dao.StockDao;
import com.xzg.test.dao.UserDao;
import com.xzg.test.entity.Stock;
import com.xzg.test.entity.User;
import com.xzg.test.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.util.CallerBlocksPolicy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StopWatch;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

/**
 * @author XieZG
 * @Description:
 * @date 21-6-8下午4:00
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class TestAll {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockDao stockDao;
    @Autowired
    private UserDao userDao;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGenerateData() {
        //中国的使用:
        Faker fakerWithCN = new Faker(Locale.CHINA);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("生成10万条测试数据");
        List<User> users = new ArrayList<>(100000);
        for (int i = 0; i < 100000; i++) {
            User userInfo = new User();
            userInfo.setRealName(fakerWithCN.name().fullName());
            userInfo.setCellPhone(fakerWithCN.phoneNumber().cellPhone());
            userInfo.setCity(fakerWithCN.address().city());
            userInfo.setStreet(fakerWithCN.address().streetAddress());
            userInfo.setUniversityName(fakerWithCN.university().name());
            users.add(userInfo);
        }
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }

    @Test
    public void testMysqlTPS() throws InterruptedException {
        //中国的使用:
        Faker fakerWithCN = new Faker(Locale.CHINA);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("生成10万条测试数据");
        int count = 100000;
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User userInfo = new User();
            userInfo.setRealName(fakerWithCN.name().fullName());
            userInfo.setCellPhone(fakerWithCN.phoneNumber().cellPhone());
            userInfo.setCity(fakerWithCN.address().city());
            userInfo.setStreet(fakerWithCN.address().streetAddress());
            userInfo.setUniversityName(fakerWithCN.university().name());
            users.add(userInfo);
        }
        stopWatch.stop();
        log.info("生成数据速度：{}", count * 1000.0 / stopWatch.getLastTaskTimeMillis());
        stopWatch.start("开始插入数据");
        int threadSize = 100;
        ExecutorService cachedThreadPool = new ThreadPoolExecutor(threadSize, threadSize, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(threadSize * 10), // 使用有界队列，避免OOM
                new CallerBlocksPolicy(3600 * 1000));//队列满了阻塞队列，超时未入队抛异常
        for (User user : users) {
            cachedThreadPool.execute(() -> {
                userDao.save(user);
            });
        }
        cachedThreadPool.shutdown();
        cachedThreadPool.awaitTermination(1, TimeUnit.HOURS);
        stopWatch.stop();
        log.info("插入数据速度：{}", count * 1000.0 / stopWatch.getLastTaskTimeMillis());
        stopWatch.getLastTaskTimeMillis();
        log.info(stopWatch.prettyPrint());
    }


    @Test
    public void testBuy() throws Exception {
        long size = 1000;
        stockService.addStock(Stock.builder()
                .id(1L)
                .name("shoes")
                .count(size)
                .build());
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < size; i++) {
            cachedThreadPool.execute(() -> {
                stockService.buy(1L);
            });
        }
        cachedThreadPool.shutdown();
        cachedThreadPool.awaitTermination(1, TimeUnit.HOURS);
        log.info("stock count:{}", stockDao.findById(1L).get().getCount());
        assert stockDao.findById(1L).get().getCount().intValue() == 0;

//        mockMvc.perform(MockMvcRequestBuilders.get(new URI("/buy")))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andDo(MockMvcResultHandlers.print());
    }
}
