
计划任务新增SQL增强

insert into sys_quartz_job(id, job_class_name, parameter, description, status)
value('#{id}','org.caterpillar.rpa.plan.RpaPlanJob','{"planId":"#{id}"}','#{name}', 0);
update rpa_plan_schedule set quartz_job = id where id='#{id}';