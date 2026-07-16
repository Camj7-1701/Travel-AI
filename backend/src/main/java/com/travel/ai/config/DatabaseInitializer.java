package com.travel.ai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        File dataDir = new File("./data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        createUserTable();
        createScenicTable();
        createCommentTable();
        createStrategyTable();
        insertInitialData();
    }

    private void createUserTable() {
        try {
            jdbcTemplate.execute("SELECT COUNT(*) FROM user");
        } catch (Exception e) {
            jdbcTemplate.execute("CREATE TABLE user (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username VARCHAR(50) NOT NULL UNIQUE," +
                    "password VARCHAR(100) NOT NULL," +
                    "nickname VARCHAR(50) DEFAULT ''," +
                    "avatar VARCHAR(255) DEFAULT ''," +
                    "phone VARCHAR(11) DEFAULT ''," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "status TINYINT DEFAULT 1" +
                    ")");
        }
    }

    private void createScenicTable() {
        try {
            jdbcTemplate.execute("SELECT COUNT(*) FROM scenic");
        } catch (Exception e) {
            jdbcTemplate.execute("CREATE TABLE scenic (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "scenic_name VARCHAR(100) NOT NULL," +
                    "address VARCHAR(200) DEFAULT ''," +
                    "longitude DECIMAL(10,7) DEFAULT 0," +
                    "latitude DECIMAL(10,7) DEFAULT 0," +
                    "category VARCHAR(50) DEFAULT ''," +
                    "intro TEXT DEFAULT ''," +
                    "price DECIMAL(10,2) DEFAULT 0," +
                    "open_time VARCHAR(100) DEFAULT ''," +
                    "img_urls TEXT DEFAULT ''," +
                    "hot INTEGER DEFAULT 0," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")");
        }
    }

    private void createCommentTable() {
        try {
            jdbcTemplate.execute("SELECT COUNT(*) FROM scenic_comment");
        } catch (Exception e) {
            jdbcTemplate.execute("CREATE TABLE scenic_comment (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "scenic_id INTEGER NOT NULL," +
                    "content TEXT NOT NULL," +
                    "like_num INTEGER DEFAULT 0," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES user(id)," +
                    "FOREIGN KEY (scenic_id) REFERENCES scenic(id)" +
                    ")");
        }
    }

    private void createStrategyTable() {
        try {
            jdbcTemplate.execute("SELECT COUNT(*) FROM travel_strategy");
        } catch (Exception e) {
            jdbcTemplate.execute("CREATE TABLE travel_strategy (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "start_point VARCHAR(200) DEFAULT ''," +
                    "end_point VARCHAR(200) DEFAULT ''," +
                    "travel_days INTEGER DEFAULT 1," +
                    "budget VARCHAR(50) DEFAULT ''," +
                    "preference VARCHAR(200) DEFAULT ''," +
                    "ai_content LONGTEXT DEFAULT ''," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES user(id)" +
                    ")");
        }
    }

    private void insertInitialData() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM scenic", Integer.class);
            if (count == 0) {
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('故宫博物院', '北京市东城区景山前街4号', 116.3974, 39.9163, '人文', '故宫博物院是在明、清两代皇宫及其收藏基础上建立起来的综合性博物馆，是中国古代宫廷建筑之精华。', 60, '08:30-17:00', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Beijing%20Forbidden%20City%20ancient%20palace%20golden%20roof&image_size=landscape_16_9', 999)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('西湖', '浙江省杭州市西湖区', 120.1535, 30.2874, '山水', '西湖，位于浙江省杭州市西湖区龙井路1号，杭州市区西部，景区总面积49平方千米，汇水面积为21.22平方千米，湖面面积为6.38平方千米。', 0, '全天开放', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Hangzhou%20West%20Lake%20beautiful%20scenery%20pagoda%20willow%20trees&image_size=landscape_16_9', 980)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('张家界', '湖南省张家界市', 110.4727, 29.1118, '山水', '张家界国家森林公园位于湖南省西北部张家界市境内，是中国第一个国家森林公园。', 225, '07:00-18:00', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Zhangjiajie%20National%20Park%20pillar%20mountains%20avatar%20scenery&image_size=landscape_16_9', 950)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('丽江古城', '云南省丽江市古城区', 100.2364, 26.8639, '古镇', '丽江古城位于云南省丽江市古城区，又名大研镇，坐落在丽江坝中部，始建于宋末元初。', 0, '全天开放', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Lijiang%20ancient%20town%20traditional%20architecture%20lanterns&image_size=landscape_16_9', 920)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('迪士尼乐园', '上海市浦东新区', 121.7898, 31.1443, '乐园', '上海迪士尼乐园，是中国内地首座迪士尼主题乐园，位于上海市浦东新区川沙新镇。', 719, '08:30-22:00', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Disneyland%20castle%20amusement%20park%20fireworks%20night&image_size=landscape_16_9', 900)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('九寨沟', '四川省阿坝州九寨沟县', 103.9107, 33.1449, '山水', '九寨沟国家级自然保护区位于四川省阿坝藏族羌族自治州九寨沟县境内，是中国第一个以保护自然风景为主要目的的自然保护区。', 280, '07:30-17:00', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Jiuzhaigou%20Valley%20colorful%20lakes%20waterfalls%20nature&image_size=landscape_16_9', 880)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('兵马俑', '陕西省西安市临潼区', 109.2756, 34.3853, '人文', '秦始皇兵马俑，亦简称秦兵马俑或秦俑，第一批全国重点文物保护单位，第一批中国世界遗产。', 120, '08:30-18:00', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Terracotta%20Army%20ancient%20warriors%20museum%20Xi%27an&image_size=landscape_16_9', 870)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('成都大熊猫基地', '四川省成都市成华区', 104.1377, 30.7578, '乐园', '成都大熊猫繁育研究基地，是中国政府实施大熊猫等濒危野生动物迁地保护工程的主要研究基地之一。', 55, '07:30-18:00', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Chengdu%20Panda%20Base%20cute%20giant%20pandas%20bamboo&image_size=landscape_16_9', 850)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('鼓浪屿', '福建省厦门市', 118.0892, 24.4798, '古镇', '鼓浪屿隶属于厦门市思明区鼓浪屿街道，下辖龙头社区、内厝澳社区，辖区禁止机动车辆通行。', 0, '全天开放', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Gulangyu%20Island%20coastal%20town%20western%20architecture&image_size=landscape_16_9', 830)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('三亚亚龙湾', '海南省三亚市', 109.5305, 18.2586, '山水', '亚龙湾是中华人民共和国海南省三亚市东郊的一处优质热带海滨风景区，距离三亚市中心约10公里。', 0, '全天开放', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Sanya%20Yalong%20Bay%20tropical%20beach%20blue%20sea%20palms&image_size=landscape_16_9', 800)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('夫子庙', '江苏省南京市秦淮区', 118.7719, 32.0215, '人文', '夫子庙是一组规模宏大的古建筑群，历经沧桑，几番兴废，是供奉和祭祀孔子的地方，中国四大文庙之一。', 0, '全天开放', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Nanjing%20Confucius%20Temple%20ancient%20architecture%20night&image_size=landscape_16_9', 780)");
                jdbcTemplate.execute("INSERT INTO scenic (scenic_name, address, longitude, latitude, category, intro, price, open_time, img_urls, hot) VALUES ('宽窄巷子', '四川省成都市青羊区', 104.0536, 30.6799, '古镇', '宽窄巷子位于四川省成都市青羊区长顺街附近，由宽巷子、窄巷子、井巷子平行排列组成，全为青黛砖瓦的仿古四合院落。', 0, '全天开放', 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Chengdu%20Kuanzhai%20Alley%20traditional%20architecture%20tea%20house&image_size=landscape_16_9', 750)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}