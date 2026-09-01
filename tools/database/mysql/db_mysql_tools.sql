-- =====================================================================================================================
-- 常用数据库脚本整理
-- =====================================================================================================================

/**
* 清理所有定时任务表
*/
SET foreign_key_checks = 0;
TRUNCATE TABLE `qrtz_blob_triggers`;
TRUNCATE TABLE `qrtz_calendars`;
TRUNCATE TABLE `qrtz_cron_triggers`;
TRUNCATE TABLE `qrtz_fired_triggers`;
TRUNCATE TABLE `qrtz_job_details`;
TRUNCATE TABLE `qrtz_locks`;
TRUNCATE TABLE `qrtz_paused_trigger_grps`;
TRUNCATE TABLE `qrtz_scheduler_state`;
TRUNCATE TABLE `qrtz_simple_triggers`;
TRUNCATE TABLE `qrtz_simprop_triggers`;
TRUNCATE TABLE `qrtz_triggers`;
SET foreign_key_checks = 1;

/**
* 删除所有定时任务表
*/
SET foreign_key_checks = 0;
DROP TABLE `qrtz_blob_triggers`;
DROP TABLE `qrtz_calendars`;
DROP TABLE `qrtz_cron_triggers`;
DROP TABLE `qrtz_fired_triggers`;
DROP TABLE `qrtz_job_details`;
DROP TABLE `qrtz_locks`;
DROP TABLE `qrtz_paused_trigger_grps`;
DROP TABLE `qrtz_scheduler_state`;
DROP TABLE `qrtz_simple_triggers`;
DROP TABLE `qrtz_simprop_triggers`;
DROP TABLE `qrtz_triggers`;
SET foreign_key_checks = 1;

--
-- 清除消息所有记录
--

truncate sys_message_type;
truncate sys_message_channel;
truncate sys_message;
truncate sys_message_template;
truncate sys_message_content;
truncate sys_message_user;
truncate sys_message_history;
truncate sys_notice;
