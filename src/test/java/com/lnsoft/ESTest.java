package com.lnsoft;

import com.lnsoft.test.es.SearchNearbyService;
import com.lnsoft.test.es.SearchResultVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * Unit test for simple App.
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ESTest {


    @Autowired
    private SearchNearbyService service;

    //我的左边
    private double myLon = 112.1233;
    private double myLat = 34.234;
    private String myName = "Chr";

    public void initData() {
        int total = 100000;

        try {
            //建库，建表，建约束
            service.recreateIndex();
            //随机产生10W条数据
            service.addDataToIndex(myLat, myLon, total);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("\n=============初始化工作完成" + total + "条数据");
    }

//    @Ignore
    @Test
    public void searchNearby() {
        int size = 10, radius = 50;
        System.out.println("开始获取距离" + myName + radius + "米以内人");

        SearchResultVo resultVo = service.search(myLon, myLat, radius,size, null);

        System.out.println("共找到" + resultVo.getTotal() + "个人，优先显示" + size+"人，查询耗时"+resultVo.getUseTime());
        for (Map<String, Object> taxi : resultVo.getData()) {
            String nickName = taxi.get("name").toString();
            String location = taxi.get("location").toString();
            Object geoDistance = taxi.get("geoDistance");
            System.out.println(nickName + "，微信号：" + taxi.get("wxNo") + "，性别："
                    + taxi.get("sex") + "，距离：" + myName + geoDistance + "米" +
                    "(坐标：" + location + ")");

        }

        System.out.println("以上" + size + "个人显示在列表中................");

    }
}
