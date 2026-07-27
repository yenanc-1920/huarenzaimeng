package com.huarenzaimeng.module.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.huarenzaimeng.module.admin.entity.SysConfig;
import com.huarenzaimeng.module.admin.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    private final Cache<String, String> configCache = Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(3, TimeUnit.MINUTES)
            .build();

    public SysConfigService(SysConfigMapper sysConfigMapper) {
        this.sysConfigMapper = sysConfigMapper;
    }

    public String getValue(String key) {
        return configCache.get(key, k -> {
            SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                    .eq(SysConfig::getConfigKey, k));
            return config != null ? config.getConfigValue() : null;
        });
    }

    public String getValue(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    public int getIntValue(String key, int defaultValue) {
        String value = getValue(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public BigDecimal getDecimalValue(String key, BigDecimal defaultValue) {
        String value = getValue(key);
        if (value == null) return defaultValue;
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolValue(String key, boolean defaultValue) {
        String value = getValue(key);
        if (value == null) return defaultValue;
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    public void updateValue(String key, String value) {
        SysConfig config = sysConfigMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, key));
        if (config != null) {
            config.setConfigValue(value);
            sysConfigMapper.updateById(config);
        } else {
            config = new SysConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setValueType("STRING");
            sysConfigMapper.insert(config);
        }
        configCache.invalidate(key);
    }

    public List<SysConfig> listAll() {
        return sysConfigMapper.selectList(new LambdaQueryWrapper<SysConfig>()
                .orderByAsc(SysConfig::getId));
    }

    public void invalidateCache() {
        configCache.invalidateAll();
    }
}
