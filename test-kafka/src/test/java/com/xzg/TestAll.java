package com.xzg;

import com.github.javafaker.Faker;
import com.xzg.test.Application;
import com.xzg.test.entity.User;
import com.xzg.test.service.UserService;
import com.xzg.test.util.JsonUtil;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void testKafka() throws InterruptedException {
        //中国的使用:
        Faker fakerWithCN = new Faker(Locale.CHINA);
        int count = 1000;
        for (int i = 0; i < count; i++) {
            User userInfo = new User();
            userInfo.setRealName(fakerWithCN.name().fullName());
            userInfo.setCellPhone(fakerWithCN.phoneNumber().cellPhone());
            userInfo.setCity(fakerWithCN.address().city());
            userInfo.setStreet(fakerWithCN.address().streetAddress());
            userInfo.setUniversityName(fakerWithCN.university().name());
            String userJson = JsonUtil.writeJson(userInfo);
            userService.sendToDefaultChannel(userJson);
        }
        //等待消息消费完
        Thread.sleep(500000);
    }

    @Test
    public void testKafkaLoop() throws InterruptedException {
        //中国的使用:
        Faker fakerWithCN = new Faker(Locale.CHINA);
        while (true) {
            User userInfo = new User();
            userInfo.setRealName(fakerWithCN.name().fullName());
            userInfo.setCellPhone(fakerWithCN.phoneNumber().cellPhone());
            userInfo.setCity(fakerWithCN.address().city());
            userInfo.setStreet(fakerWithCN.address().streetAddress());
            userInfo.setUniversityName(fakerWithCN.university().name());
            String userJson = JsonUtil.writeJson(userInfo);
            userService.sendToDefaultChannel(userJson);
            Thread.sleep(100);
        }
    }

    @Test
    public void testKafkaThroughput() throws InterruptedException {
        //中国的使用:
        Faker fakerWithCN = new Faker(Locale.CHINA);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("生成10万条测试数据");
        int count = 100000;
        List<String> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User userInfo = new User();
            userInfo.setRealName(fakerWithCN.name().fullName());
            userInfo.setCellPhone(fakerWithCN.phoneNumber().cellPhone());
            userInfo.setCity(fakerWithCN.address().city());
            userInfo.setStreet(fakerWithCN.address().streetAddress());
            userInfo.setUniversityName(fakerWithCN.university().name());
            users.add(JsonUtil.writeJson(userInfo));
        }
        stopWatch.stop();
        log.info("生成数据速度：{}", count * 1000.0 / stopWatch.getLastTaskTimeMillis());
        stopWatch.start("开始发送10万消息");

        int threadSize = 10;
        ExecutorService cachedThreadPool = new ThreadPoolExecutor(threadSize, threadSize, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(threadSize * 10), // 使用有界队列，避免OOM
                new CallerBlocksPolicy(3600 * 1000));//队列满了阻塞队列，超时未入队抛异常
        for (int i = 0; i < count * 10; i++) {
            String userJson = users.get(i % count);
            cachedThreadPool.execute(() -> {
                userService.sendToDefaultChannel(userJson);
            });
        }
        cachedThreadPool.shutdown();
        cachedThreadPool.awaitTermination(1, TimeUnit.HOURS);
        stopWatch.stop();
        log.info("发送100万消息速度：{}", count * 10 * 1000.0 / stopWatch.getLastTaskTimeMillis());
        stopWatch.getLastTaskTimeMillis();
        log.info(stopWatch.prettyPrint());
    }


    @Test
    public void testBudRiskKafkaTps() throws InterruptedException {
        //中国的使用:
        Faker fakerWithCN = new Faker(Locale.CHINA);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("生成10万条测试数据");
        int count = 100000;
        List<String> riskData = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String dateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
            String phone = fakerWithCN.phoneNumber().phoneNumber();
            String longitude = fakerWithCN.address().longitude();
            String latitude = fakerWithCN.address().latitude();
            Map<String, Object> risk = new HashMap<>();
            risk.put("appName", "budspace");
            risk.put("appId", "budspace");
            risk.put("invokeType", "10");
            risk.put("eventOccurTime", dateStr);
            risk.put("eventId", "budspace_ScanCodeLottery");
            risk.put("ipAddress", fakerWithCN.internet().ipV4Address());
            risk.put("appType", "web");
            risk.put("tokenId", "eyJvcyI6Ind4YXBwIiwidCI6IkV1ME1hTGVoWlhxV3ZxSjdVbUl2RDZweHdPTTVON3lNdEk2bzJWeTRvWlN3M1BYM3g0VndjNERrYnNSSGNrMmtwV2tBY013RWZZc2ZJMjNKeENyUHVBPT0iLCJ2IjoiV05URWxILzVZMWF1ekI3MGU1SmhvcD09IiwicGFydG5lciI6ImJhaXdlaSJ9");
            risk.put("accountLogin", phone);
            risk.put("tradeType", "scancode_lottery");
            risk.put("tradeTypeName", "扫码开奖");
            risk.put("accountMobile", phone);
            risk.put("customerName", fakerWithCN.address().fullAddress());
            risk.put("openId", "oJ9wc5EYSzaCgEROr8h40utK1GxY");
            risk.put("gps", longitude + "," + latitude);
            risk.put("SKU", "百威9.7度500听装-54552/54553/62565/71140");
            risk.put("activityName", "百威空间站");
            risk.put("scanCode", "13d06eaab2c29");
            risk.put("shortName", "sum20.z-l.co/a");
            risk.put("registerDate", "2020-02-13 23:47:35");
            risk.put("scanCodeTime", dateStr);
            risk.put("created", dateStr);
            risk.put("proName", "2020BUDSummerUTC-NONE-BUS-佛山工厂-百威500听装-3000000-佛山波尔-20200413");
            risk.put("activeDateTime", "2021-05-12 01:01:01");
            risk.put("transId", UUID.randomUUID().toString());
            riskData.add(JsonUtil.writeJson(risk));
        }
        stopWatch.stop();
        log.info("生成数据速度：{}", count * 1000.0 / stopWatch.getLastTaskTimeMillis());
        stopWatch.start("开始发送10万消息");
        int threadSize = 10;
        ExecutorService cachedThreadPool = new ThreadPoolExecutor(threadSize, threadSize, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(threadSize * 10), // 使用有界队列，避免OOM
                new CallerBlocksPolicy(3600 * 1000));//队列满了阻塞队列，超时未入队抛异常
        for (String userJson : riskData) {
            cachedThreadPool.execute(() -> {
                userService.sendToDefaultChannel(userJson);
            });
        }
        cachedThreadPool.shutdown();
        cachedThreadPool.awaitTermination(1, TimeUnit.HOURS);
        stopWatch.stop();
        log.info("发送10万消息速度：{}", count * 10 * 1000.0 / stopWatch.getLastTaskTimeMillis());
        stopWatch.getLastTaskTimeMillis();
        log.info(stopWatch.prettyPrint());
    }


}
