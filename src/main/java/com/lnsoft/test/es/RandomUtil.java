package com.lnsoft.test.es;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created By Chr on 2019/3/16/0016.
 */
public class RandomUtil {
    private static Random random = new Random();
    private static final char[] sexs = "男女".toCharArray();
    private static final char[] wxNo = "sdfkjhsuiewbkzcibbezbiasdfaad32443566576812".toLowerCase().toCharArray();
    private static char[] firstName = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜阮蓝闵席季麻强贾路娄危江童颜郭梅盛林刁钟徐邱骆高夏蔡田樊胡凌霍虞万支柯咎管卢莫经房裘缪干解应宗宣丁贲邓郁单杭洪包诸左石崔吉钮龚程嵇邢滑裴陆荣翁荀羊於惠甄魏加封芮羿储靳汲邴糜松井段富巫乌焦巴弓牧隗山谷车侯宓蓬全郗班仰秋仲伊宫宁仇栾暴甘钭厉戎祖武符刘姜詹束龙叶幸司韶郜黎蓟薄印宿白怀蒲台从鄂索咸籍赖卓蔺屠蒙池乔阴郁胥能苍双闻莘党翟谭贡劳逄姬申扶堵冉宰郦雍却璩桑桂濮牛寿通边扈燕冀郏浦尚农温别庄晏柴瞿阎充慕连茹习宦艾鱼容向古易慎戈廖庚终暨居衡步都耿满弘匡国文寇广禄阙东殴殳沃利蔚越夔隆师巩厍聂晁勾敖融冷訾辛阚那简饶空曾毋沙乜养鞠须丰巢关蒯相查后江红游竺权逯盖益桓公万俟司马上官欧阳夏侯诸葛闻人东方赫连皇甫尉迟公羊澹台公冶宗政濮阳淳于仲孙太叔申屠公孙乐正轩辕令狐钟离闾丘长孙慕容鲜于宇文司徒司空亓官司寇仉督子车颛孙端木巫马公西漆雕乐正壤驷公良拓拔夹谷宰父谷粱晋楚阎法汝鄢涂钦段干百里东郭南门呼延归海羊舌微生岳帅缑亢况后有琴梁丘左丘东门西门商牟佘佴伯赏南宫墨哈谯笪年爱阳佟第五言福百家姓续".toCharArray();
    //确保微信号不重复，声明缓冲区
    private static Set<String> wxNoCache;

    /**
     * 随机生成性别
     */
    public static String randomSex() {
        int i = random.nextInt(sexs.length);

        return ("" + sexs[i]);
    }

    /**
     * 随机微信号
     *
     * @return
     */
    public static String randomWxNo() {
        //初始化缓冲区
        openCache();
        //微信号自动生成规则，wx_开头加上10位数字组合
        StringBuffer sb = new StringBuffer();
        for (int c = 0; c < 10; c++) {
            int i = random.nextInt(wxNo.length);
            sb.append(wxNo[i]);
        }
        String carName = ("wx_" + sb.toString());
        //防止重复，生成后检查一下
        //如果重复，重新生成，知道不重复
        if (wxNoCache.contains(carName)) {
            return randomWxNo();
        }

        wxNoCache.add(carName);
        return carName;
    }


    /**
     * 随机生成经纬度
     */
    public static double[] randomPoint(double myLat, double myLon) {
        double min = 0.000001;//坐标范围，最小1米
        double max = 0.000002;//坐标范围，最大1000米

        //随机生成一组坐标
        double s = random.nextDouble() % (max - max + 1) + max;
        //格式化保留6小数
        DecimalFormat df = new DecimalFormat("######0.000000");
        String slat = df.format(s + myLat);
        String slon = df.format(s + myLon);
        Double dlat = Double.valueOf(slat);
        Double dlon = Double.valueOf(slon);
        return new double[]{dlat, dlon};
    }

    /**
     * 随机生成行吗
     */
    public static String randomName(String sex) {
        int i = random.nextInt(firstName.length);
        return firstName[i] + ("男".equals(sex) ? "男士" : "女士");
    }


    /**
     * 初始化缓存区
     */
    public static void openCache() {
        if (wxNoCache == null)
            wxNoCache = new HashSet<String>();
    }

    /**
     * 清空缓存区
     */
    public static void cleanCache() {
        wxNoCache = null;
    }
}