package com.lnsoft.test.es;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created By Chr on 2019/3/17/0017.
 */
public class SearchResultVo {

    //记录总数
    private long total;
    //搜索花费时间 毫秒
    private float useTime;
    //距离单位 米
    private String    distance;

    //数据集合
    private List<Map<String,Object>> data=new ArrayList<>();

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public float getUseTime() {
        return useTime;
    }

    public void setUseTime(float useTime) {
        this.useTime = useTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
