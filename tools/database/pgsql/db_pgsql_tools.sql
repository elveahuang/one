-- =====================================================================================================================
-- 常用数据库脚本整理
-- =====================================================================================================================

--
-- 清理所有定时任务表
--

SET session_replication_role = 'replica';
TRUNCATE TABLE qrtz_blob_triggers CASCADE;
TRUNCATE TABLE qrtz_calendars CASCADE;
TRUNCATE TABLE qrtz_cron_triggers CASCADE;
TRUNCATE TABLE qrtz_fired_triggers CASCADE;
TRUNCATE TABLE qrtz_simple_triggers CASCADE;
TRUNCATE TABLE qrtz_simprop_triggers CASCADE;
TRUNCATE TABLE qrtz_scheduler_state CASCADE;
TRUNCATE TABLE qrtz_paused_trigger_grps CASCADE;
TRUNCATE TABLE qrtz_locks CASCADE;
TRUNCATE TABLE qrtz_triggers CASCADE;
TRUNCATE TABLE qrtz_job_details CASCADE;
SET session_replication_role = 'origin';

--
-- 删除所有定时任务表
--

SET session_replication_role = 'replica';
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;
SET session_replication_role = 'origin';

--
-- 清除消息所有记录
--

truncate sys_message_type cascade;
truncate sys_message_channel cascade;
truncate sys_message cascade;
truncate sys_message_template cascade;
truncate sys_message_content cascade;
truncate sys_message_user cascade;
truncate sys_message_history cascade;
truncate sys_notice cascade;

--
-- 清除重置权限
--

truncate sys_role cascade;
truncate sys_entity_authority cascade;
truncate sys_user_role cascade;
truncate sys_authority cascade;
