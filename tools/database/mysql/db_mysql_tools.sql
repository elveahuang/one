-- =====================================================================================================================
-- 常用数据库脚本整理
-- =====================================================================================================================

/*
* 清理所有定时任务表
*/
SET foreign_key_checks = 0;
TRUNCATE TABLE `QRTZ_BLOB_TRIGGERS`;
TRUNCATE TABLE `QRTZ_CALENDARS`;
TRUNCATE TABLE `QRTZ_CRON_TRIGGERS`;
TRUNCATE TABLE `QRTZ_FIRED_TRIGGERS`;
TRUNCATE TABLE `QRTZ_SIMPLE_TRIGGERS`;
TRUNCATE TABLE `QRTZ_SIMPROP_TRIGGERS`;
TRUNCATE TABLE `QRTZ_SCHEDULER_STATE`;
TRUNCATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS`;
TRUNCATE TABLE `QRTZ_LOCKS`;
TRUNCATE TABLE `QRTZ_TRIGGERS`;
TRUNCATE TABLE `QRTZ_JOB_DETAILS`;
SET foreign_key_checks = 1;

/*
* 删除所有定时任务表
*/
SET foreign_key_checks = 0;
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
SET foreign_key_checks = 1;

--
-- 清除消息所有记录
--

SET foreign_key_checks = 0;
TRUNCATE TABLE `sys_message_type`;
TRUNCATE TABLE `sys_message_channel`;
TRUNCATE TABLE `sys_message`;
TRUNCATE TABLE `sys_message_template`;
TRUNCATE TABLE `sys_message_content`;
TRUNCATE TABLE `sys_message_user`;
TRUNCATE TABLE `sys_message_history`;
TRUNCATE TABLE `sys_notice`;
SET foreign_key_checks = 1;

--
-- 清除重置权限
--

SET foreign_key_checks = 0;
TRUNCATE TABLE `sys_role`;
TRUNCATE TABLE `sys_entity_authority`;
TRUNCATE TABLE `sys_user_role`;
TRUNCATE TABLE `sys_authority`;
SET foreign_key_checks = 1;
