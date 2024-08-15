package com.pgmmers.radar.service.impl.engine.plugin;


import com.pgmmers.radar.service.data.MobileInfoService;
import com.pgmmers.radar.service.engine.PluginServiceV2;
import com.pgmmers.radar.service.engine.vo.Location;
import com.pgmmers.radar.vo.data.MobileInfoVO;
import com.pgmmers.radar.vo.model.PreItemVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LIBPHONEVALIDATION implements PluginServiceV2 {
    @Autowired
    private MobileInfoService mobileInfoService;


    @Override
    public Integer key() {
        return 7;
    }

    @Override
    public String desc() {
        return "手机号有效性验证";
    }

    @Override
    public String getType() {
        return "STRING";
    }

    @Override
    public Object handle(PreItemVO item, Map<String, Object> jsonInfo, String[] sourceField) {
        String mobile = jsonInfo.get(sourceField[0]).toString();
        Boolean isPhoneValid = mobileInfoService.isPhoneValid(mobile);
        return isPhoneValid;
    }
}
