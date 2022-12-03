package org.caterpillar.rpa.plan;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ObjectUtils;

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
        // 20221203 增加两个参数 threads 并发线程数  param_json 定制参数
        String taskSql = "insert into rpa_plan_task" +
                "(id, name, plan_id, script_id, project_id, threads, param_json,create_by, create_time)" +
                " value(?,?,?,?,?,?, ?,?,now())";
        if(CollectionUtil.isNotEmpty(planItem)){
            // 创建记录
            String id = UUIDGenerator.generate();
            // 默认任务分配 username -> update_by -> create_by
            String username = MapUtil.getStr(planItem, "username");
            String createBy = username;
            if(ObjectUtils.isEmpty(createBy)){
                createBy = MapUtil.getStr(planItem, "update_by");
            }
            if(ObjectUtils.isEmpty(createBy)){
                createBy = MapUtil.getStr(planItem, "create_by");
            }
            jdbcTemplate.update(taskSql, new Object[]{id
                    , MapUtil.getStr(planItem, "script_name")
                    , MapUtil.getStr(planItem, "id")
                    , MapUtil.getStr(planItem, "script_id")
                    , MapUtil.getStr(planItem, "project_id")
                    , MapUtil.getInt(planItem, "threads")
                    , MapUtil.getStr(planItem, "param_json")
                    , createBy});

            // 运行节点
            String runnode = MapUtil.getStr(planItem, "runnode");
            if(ObjectUtils.isEmpty(runnode) && !ObjectUtils.isEmpty(username)){
                // 检查用户最新更新的节点
                List<Map<String, Object>> nodes = jdbcTemplate.queryForList("select * from rpa_runnode where username=? order by update_time desc", username);
                if(CollectionUtil.isNotEmpty(nodes)){
                    runnode = MapUtil.getStr(nodes.get(0),"runnode");
                }
            }
            if(!ObjectUtils.isEmpty(runnode)){
                jdbcTemplate.update("update rpa_plast_task set runnode=? where id=?", new Object[]{runnode, id});
            }
        }

    }
}
