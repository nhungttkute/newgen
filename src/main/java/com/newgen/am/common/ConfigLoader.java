/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import java.io.File;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.stereotype.Component;

/*
 * @author thanhdv
 */
@Component
public class ConfigLoader {

    private final long reloadInterval = 60000;     // 60 seconds = 1 min

    private final PropertiesConfiguration mainConfig = initConfig("main");

    private PropertiesConfiguration initConfig(String configType) {
        PropertiesConfiguration config = new PropertiesConfiguration();
        File configFile = new File(Constant.CONFIG_DIR + configType + ".properties");
        FileChangedReloadingStrategy reload = new FileChangedReloadingStrategy();
        reload.setRefreshDelay(reloadInterval);
        config.setReloadingStrategy(reload);
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    public PropertiesConfiguration getMainConfig() {
        return mainConfig;
    }
}
