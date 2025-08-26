package generator;

import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Codegen {

    private static final String[] TABLE_NAMES = {"chat_history"};

    public static void main(String[] args) {
        //配置数据源
        Dict dict = YamlUtil.loadByPath("application.yaml");
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres");
        dataSource.setUsername("postgres.pollrxffbmhccdjxvpco");
        dataSource.setPassword("dd8857356");

        //创建配置内容，两种风格都可以。
//        GlobalConfig globalConfig = createGlobalConfigUseStyle1();
        GlobalConfig globalConfig = createGlobalConfigUseStyle2();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle2() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage("com.ckrey.ckreycodemother.generesult");

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setGenerateTable(TABLE_NAMES)
                .setGenerateSchema("public")
                .setLogicDeleteColumn("isdelete");

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true)
                .setJdkVersion(21);

        //设置生成 mapper
        globalConfig.enableMapper();

        globalConfig.enableService();
        globalConfig.enableServiceImpl();
        globalConfig.enableController();
        globalConfig.enableMapperXml();
        globalConfig.getJavadocConfig()
                .setAuthor("ckrey");


//        //可以单独配置某个列
//        ColumnConfig columnConfig = new ColumnConfig();
//        columnConfig.setColumnName("tenant_id");
//        columnConfig.setLarge(true);
//        columnConfig.setVersion(true);
//        globalConfig.getStrategyConfig()
//                .setColumnConfig("tb_account", columnConfig);

        return globalConfig;
    }
}