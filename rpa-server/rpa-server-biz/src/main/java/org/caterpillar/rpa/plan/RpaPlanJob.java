package org.caterpillar.rpa.plan;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.common.util.dynamic.db.DynamicDBUtil;
import org.jeecg.modules.online.cgform.service.IOnlineService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.Map;

@Slf4j
public class RpaPlanJob implements Job {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String parameter;  //json，计划参数

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info(" Job Execution key："+context.getJobDetail().getKey());
        log.info( String.format("welcome %s! RpaPlanJob   时间:" + DateUtils.now(), this.parameter));
        // 获取RPA计划配置
        JSONObject rpaPlanParam = JSON.parseObject(this.parameter);
        log.info(rpaPlanParam.toJSONString());

        // 生成执行任务
        String planId = rpaPlanParam.getString("planId");

        String sql = "select a.*, b.name as script_name from rpa_plan_schedule a, rpa_flow_script b" +
                " where a.id=? and a.script_id=b.id";

        List<Map<String,Object>> listPlan = jdbcTemplate.queryForList(sql, new Object[]{planId});
        Map<String,Object> planItem = null;
        if(CollectionUtil.isNotEmpty(listPlan)){
            planItem = listPlan.get(0);
        }
        String taskSql = "insert into rpa_plan_task(id, name, plan_id, script_id, project_id, create_time)" +
                " value(?,?,?,?,?, now())";
        if(CollectionUtil.isNotEmpty(planItem)){
            jdbcTemplate.update(taskSql, new Object[]{UUIDGenerator.generate()
                    , MapUtil.getStr(planItem, "script_name")
                    , MapUtil.getStr(planItem, "id")
                    , MapUtil.getStr(planItem, "script_id")
                    , MapUtil.getStr(planItem, "project_id")});
        }





    }
}
