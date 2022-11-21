package org.caterpillar.rpa.plan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class RpaPlanJob implements Job {

    private String parameter;  //json，计划参数

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info(" Job Execution key："+context.getJobDetail().getKey());
        log.info( String.format("welcome %s! Jeecg-Boot 带参数定时任务 SampleParamJob !   时间:" + DateUtils.now(), this.parameter));
        // 获取RPA计划配置
        JSONObject rpaPlanParam = JSON.parseObject(this.parameter);
        log.info(rpaPlanParam.toJSONString());

        // 生成执行任务

    }
}
