/*
 * Copyright (c) 2019-2022 WangFeiHu
 *  Radar is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *  http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

package com.pgmmers.radar.controller;

import com.pgmmers.radar.service.common.CommonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * engine 启动首页.
 * @author wangfeihu
 */
@RestController
public class IndexController {

    @GetMapping(value = {"/", ""})
    public CommonResult index(HttpServletRequest request) {
        CommonResult result =  new CommonResult(Boolean.TRUE, "100", "Engine is running");
        result.getData().put("swagger url:", request.getRequestURL() + "swagger-ui.html");
        return result;
    }
}
