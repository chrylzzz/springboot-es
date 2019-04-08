package com.lnsoft.test.es;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.GeoDistanceRangeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created By Chr on 2019/3/16/0016.
 */
@Service
public class SearchNearbyService {

    @Value("9300")
    String port;

    @Value("127.0.0.1")
    String host;
    private static Logger logger = Logger.getLogger(SearchNearbyService.class);

    private String indexName = "Chr_search";//相当于数据库名字
    private String indexType = "weChat";//相当于数据库表名字

    //连接
    private TransportClient client;

    //初始化客户端
    //在业务类的构造方法中简历连接，保证之连接一次
    public SearchNearbyService() {
        try {

            Settings settings = Settings.settingsBuilder().build();

//            client = TransportClient.builder().settings(settings).build()
//                    .addTransportAddress(new InetSocketTransportAddress(
                            //服务器IP地址，//服务器端口
//                            InetAddress.getByName(CustomConfig.getString("es.host.ip")), CustomConfig.getInt("es.host.port")));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建索引
     */
    private void createIndex() {
        //表结构（建约束）
        XContentBuilder mapping = createMapping();

        //建库
        //建库建表建约束
        client.admin().indices().prepareCreate(indexName).execute().actionGet();

        //建表
        PutMappingRequest putMappingRequest = Requests.putMappingRequest(indexName).type(indexType).source(mapping);
        PutMappingResponse putMappingResponse = client.admin().indices().putMapping(putMappingRequest).actionGet();

        if (!putMappingResponse.isAcknowledged()) {
            logger.info("无法创建[" + indexName + "] [" + indexType + "] 的Mapping");
        } else {
            logger.info("创建[" + indexName + "] [" + indexType + "] 的Mapping成功");
        }
    }

    /**
     * 查询,拼接的条件，返回数据Vo
     *
     * @param lat
     * @param lon
     * @param radius
     * @param size
     * @param sex
     * @return
     */
    public SearchResultVo search(double lat, double lon, int radius, int size, String sex) {
        SearchResultVo result = new SearchResultVo();

        //同一单位 米
        String unit = DistanceUnit.METERS.toString();


        //获取一个查询规则构造器
        //茶是哪个库的表
        //完成相当于select * from 数据库.表名
        SearchRequestBuilder srb = client.prepareSearch(indexName).setTypes(indexType);


        //实现分页操作
        //相当于MySQL的 limit 0,size
        srb.setFrom(0).setSize(size);//取出优先级最高的size条数据


        //拼接查询条件
        //性别，昵称，坐标
        //构建查询条件
        //地理坐标，方圆多少米以内的都要找出来
        QueryBuilder qb = new GeoDistanceRangeQueryBuilder("location")
                .point(lon, lat)//
                .from("0" + unit).to(radius + unit)//
                .optimizeBbox("memory")//
                .geoDistance(GeoDistance.PLANE);//设置计算规则，是平米还是立方（方圆多少米）
        //相对于 where location > 0 and location < radius
        srb.setPostFilter(qb);


        //继续拼接where条件
        //and sex = ？
        BoolQueryBuilder bq = QueryBuilders.boolQuery();
        if (!(sex == null || "".equals(sex.trim()))) {
            bq.must(QueryBuilders.matchQuery("sex", sex));
        }
        srb.setQuery(bq);


        //设置排序
        GeoDistanceSortBuilder geoSort = SortBuilders.geoDistanceSort("location");

        geoSort.unit(DistanceUnit.METERS);
        geoSort.order(SortOrder.ASC);//按距离升序排列，最近的要拍在最前面
        geoSort.point(lon, lat);


        //order by lication asc 升序排列
        srb.addSort(geoSort);


        //到此为止，相当于sql语句构建完成


        //开始执行查询，查询
        //调用execute()方法
        //Response:包含了所有的需要的结构
        SearchResponse response = srb.execute().actionGet();

        //高亮分词
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();

        //搜索耗时
        float useTime = response.getTookInMillis() / 1000f;

        result.setTotal(hits.getTotalHits());
        result.setUseTime(useTime);
        result.setData(new ArrayList<Map<String, Object>>());
        result.setDistance(DistanceUnit.METERS.toString());
        for (SearchHit hit : searchHits) {
            //获取距离值，保留两位小数
            BigDecimal geoDis = new BigDecimal((Double) hit.getSortValues()[0]);
            Map<String, Object> hitMap = hit.getSource();
            //创建Mapping的时候，属性名不可谓geoDistance
            hitMap.put("geoDistance", geoDis.setScale(0, BigDecimal.ROUND_HALF_DOWN));
            result.getData().add(hitMap);
        }
        return result;
    }

    /**
     * 创建mapping，相当于创建表结构
     * 2.x-5.x：只有keyword和text类型
     *
     * @return
     */
    private XContentBuilder createMapping() {
        XContentBuilder mapping = null;

        try {
            mapping = XContentFactory.jsonBuilder().startObject()
                    //索引库名（类似数据库中的表）
                    .startObject(indexType).startObject("properties")
                    //微信号（唯一索引）
                    .startObject("wxNo").field("type", "string").endObject()
                    //昵称
                    .startObject("name").field("type", "string").endObject()
                    //性别
                    .startObject("sex").field("type", "string").endObject()
                    //位置，包含经纬度,geo_point这个类型专门存储地理坐标的
                    .startObject("location").field("type", "geo_point").endObject()
                    .endObject().endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapping;
    }


    /**
     * 建库建表建约束
     */
    public void recreateIndex() {
        try {
            //后台级的操作，关乎删库跑路的危险
            //先清除原来已有的数据库
            client.admin().indices().prepareDelete(indexName).execute().actionGet();

        } catch (Exception e) {
            e.printStackTrace();
        }
        createIndex();
    }

    /**
     * @param myLat 经度
     * @param myLon 纬度
     * @param count 生成多少个
     * @return
     */
    public Integer addDataToIndex(double myLat, double myLon, int count) {

        List<String> peopleList = new ArrayList<>();

        //开启重复检验的缓冲区
        RandomUtil.openCache();

        for (long i = 0; i < count; i++) {
            People people = randomPeople(myLat, myLon);
            //object->json
            peopleList.add(JSON.toJSONString(people));
        }


        //清空重复检验的缓冲区
        RandomUtil.cleanCache();
        //创建索引库
        List<IndexRequest> requests = new ArrayList<>();
        //把数据写道数据库表中
        for (int i = 0; i < peopleList.size(); i++) {
            //************************************************

//            IndexRequest request = client.prepareIndex(indexName, indexType).setSource(peopleList);

//            requests.add(request);
        }
        //批量创建索引
        //************************************************
        return null;

    }

    /**
     * 构造人
     *
     * @param myLat 所在的经度
     * @param myLon 所在的纬度
     * @return
     */

    public People randomPeople(double myLat, double myLon) {

        //随机生成微信号
        String wxNo = RandomUtil.randomWxNo();

        //随机生成性别

        String sex = RandomUtil.randomSex();

        //随机生成昵称

        String name = RandomUtil.randomName(sex);
        //随机生成坐标
        double[] point = RandomUtil.randomPoint(myLat, myLon);

        return new People(wxNo, sex, name, point[0], point[1]);

    }
}
