/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 80025
Source Host           : localhost:3306
Source Database       : srig

Target Server Type    : MYSQL
Target Server Version : 80025
File Encoding         : 65001

Date: 2021-08-24 09:19:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for SRIG_FORWARD_ROUTER
-- ----------------------------
DROP TABLE IF EXISTS `SRIG_FORWARD_ROUTER`;
CREATE TABLE `SRIG_FORWARD_ROUTER` (
  `ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `FROM_SERVER_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `TO_SERVER_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `FROM_TOPIC` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `TO_TOPIC` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '最新数据时间',
  `EXPIRE_TIME` datetime DEFAULT NULL COMMENT '过期时间',
  `STATUS` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '服务状态0禁用,1正常运行,2运行异常',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of SRIG_FORWARD_ROUTER
-- ----------------------------
INSERT INTO `SRIG_FORWARD_ROUTER` VALUES ('093e52a91d454ff681bcbd26cdc8d149', 'e7d411ffd6a343ba81b9ac0cab09deb1', 'c8fe34e1eba1470f90392891c0f319ec', 'test', 'topic3', null, '2021-08-30 00:00:00', '0');
INSERT INTO `SRIG_FORWARD_ROUTER` VALUES ('71b3f62679a44567bed', 'a4abc334d90b4be1b1a975dde16d3dd7', '918a836419f04c4e80be437a54cc03d9', 'dws_hw_gps_devc_tp_gpspoint_rt', 'aly_test', '2021-08-24 09:05:32', '2021-08-25 09:07:36', '0');
INSERT INTO `SRIG_FORWARD_ROUTER` VALUES ('71b3f62679a44567bed31162e', '918a836419f04c4e80be437a54cc03d9', '586a12c1284a466e87492936a10e2930', 'aly_test', 'aly_test1', '2021-08-24 09:05:32', '2021-08-24 09:07:36', '0');
INSERT INTO `SRIG_FORWARD_ROUTER` VALUES ('71b3f62679a44567bed31162ee3df096', '586a12c1284a466e87492936a10e2930', '0d29b50e718e4b16971065747e4099f7', 'aly_test1', 'aly_test', '2021-08-24 09:05:32', '2021-08-24 09:07:36', '0');
INSERT INTO `SRIG_FORWARD_ROUTER` VALUES ('c8c588709f0f4832931c123abb8acc3f', '1297ba0546554d0190f59c465c1092d0', '8a6165d851de410c95d4ab719f3d1175', 'topic2', 'topic', '2021-08-23 18:11:25', '2021-08-27 00:00:00', '0');

-- ----------------------------
-- Table structure for SRIG_MQ_SERVER
-- ----------------------------
DROP TABLE IF EXISTS `SRIG_MQ_SERVER`;
CREATE TABLE `SRIG_MQ_SERVER` (
  `ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `NAME` varchar(100) DEFAULT NULL COMMENT '数据源名称',
  `IP` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `PORT` int DEFAULT NULL,
  `PROTOCOL` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `CLIENT_NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `TYPE` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `USERNAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `PASSWORD` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `RETRY` tinyint DEFAULT '5' COMMENT '重连次数',
  `STATUS` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '0离线,1在线',
  `CREATE_TIME` datetime DEFAULT NULL,
  `UPDATE_TIME` datetime DEFAULT NULL,
  `CLUSTER` varchar(255) DEFAULT NULL COMMENT '集群地址',
  `DEFAULT_PARAM` varchar(500) DEFAULT NULL COMMENT '连接参数:json字符串',
  `GROUP_ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `TOPIC` varchar(100) DEFAULT NULL,
  `TAG` varchar(50) DEFAULT NULL,
  `SECRET_KEY` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `ACCESS_KEY` varchar(255) DEFAULT NULL,
  `NETWORK_TYPE` varchar(1) DEFAULT NULL COMMENT '网络类型',
  `VPN_ADDRESS` varchar(255) DEFAULT NULL COMMENT '代理服务器地址',
  `VPN_ACCONT` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'vpn账号',
  `VPN_PASSWORD` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'vpn密码',
  `DEL_FLAG` tinyint(1) DEFAULT '0' COMMENT '0未删除1删除',
  `SOURCE_TYPE` tinyint(1) DEFAULT '0' COMMENT '0源数据1目标数据',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of SRIG_MQ_SERVER
-- ----------------------------
INSERT INTO `SRIG_MQ_SERVER` VALUES ('0d29b50e718e4b16971065747e4099f7', 'RocketMQ服务', 'localhost', '9876', 'tcp', '/', 'RocketMQ', 'admin', 'public', null, null, '2021-08-20 10:47:42', '2021-08-20 10:47:42', 'localhost:9876', null, 'GID_TRACE_FUNSION_TT', 'dws_hw_gps_devc_tp_gpspoint_rt', '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', '1', null, 'test', 'test', '0', '1');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('1297ba0546554d0190f59c465c1092d0', 'ces2', 'localhost', '1883', 'tcp', '/', 'MQTT', 'admin', '123456', null, null, '2021-08-23 16:43:45', '2021-08-23 16:43:45', '', '', '', 'topic2', '', '', '', '0', null, 'admin', '123456', '0', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('38cb805a906c47279bc7bc03a12bd5e8', 'zhangsan', 'localhost', '9876', null, null, 'RocketMQ', 'admin-1', '12346', null, null, '2021-08-23 16:46:35', '2021-08-23 16:47:15', null, null, null, 'topic1-1', null, null, null, null, null, null, null, '0', '1');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('586a12c1284a466e87492936a10e2930', 'Rabbit服务', 'localhost', '5672', 'tcp', '/', 'AMQP', 'guest', 'guest', null, null, '2021-08-18 17:53:05', '2021-08-20 10:44:18', null, null, 'GID_TRACE_FUNSION_TT', 'dws_hw_gps_devc_tp_gpspoint_rt', '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', '1', null, 'test', 'test', '0', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('6299ff1eb06045789871cca1ebf94d76', 'test3', '', null, '', '', 'RocketMQ', '', '', null, null, '2021-08-20 18:15:14', '2021-08-20 18:15:14', 'localhost:9876', '', 'tes', 'tes', '*', '', '', '0', null, '1', '123456', '1', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('660558f8d50a432390527808498e2acd', '33', 'localhost', '9876', null, null, 'RocketMQ', null, null, null, null, '2021-08-23 14:31:45', '2021-08-23 14:31:45', null, null, null, '11', null, null, null, null, null, null, null, '0', '1');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('6b2e302f4ce04f0aab382b62230dbee1', '测试1', 'localhost', '5672', 'tcp', '/', 'AMQP', 'guest', 'guest', null, null, '2021-08-23 11:45:52', '2021-08-23 11:45:52', '', '', '', 'test', '', '', '', '1', null, null, null, '1', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('72736940fcac474bb0787af194b6c387', '521', 'localhost', '9876', null, null, 'RocketMQ', null, null, null, null, '2021-08-23 14:38:57', '2021-08-23 14:38:57', null, null, null, 'ip', null, null, null, null, null, null, null, '0', '1');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('867e59777c754a2d8f4701fe89430221', 'ces1', 'localhost', '1883', 'tcp', '/', 'MQTT', 'admin', 'pablic', null, null, '2021-08-20 17:30:29', '2021-08-20 17:33:51', '', null, '', 'dws_hw_gps_devc_tp_gpspoint_rt', '', '', '', '1', null, null, null, '1', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('8a6165d851de410c95d4ab719f3d1175', 'lisi', 'localhost', '9876', null, null, 'RocketMQ', 'admin', '123456', null, null, '2021-08-23 17:01:35', '2021-08-23 17:01:35', null, null, null, 'topic', null, null, null, null, null, null, null, '0', '1');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('918a836419f04c4e80be437a54cc03d9', 'MQTT数据源名称', 'localhost', '1883', 'tcp', '/clzy', 'MQTT', 'admin', 'public', null, null, '2021-08-20 10:45:14', '2021-08-20 11:13:27', null, null, 'GID_TRACE_FUNSION_TT', 'dws_hw_gps_devc_tp_gpspoint_rt', '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', '1', null, 'test', 'test', '0', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('a4abc334d90b4be1b1a975dde16d3dd7', '阿里云RocketMQ', 'localhost', '5672', 'tcp', '/', 'Aliyun_RocketMQ', 'guest', 'guest', null, null, '2021-08-18 18:11:22', '2021-08-20 16:43:14', 'http://MQ_INST_1551934212811443_BXdv0RAB.mq-internet-access.mq-internet.aliyuncs.com:80', null, 'GID_TRACE_FUNSION_TT', 'dws_hw_gps_devc_tp_gpspoint_rt', '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', '1', null, 'test', 'test', '0', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('b5dea5ff901c492abc0b49e6239c7cd9', 'ces2', 'localhost', '5672', 'tcp', '/', 'AMQP', 'guest', 'guest', null, null, '2021-08-20 18:13:08', '2021-08-20 18:13:08', '', '', '', '1', '', '', '', '1', null, null, null, '1', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('c8fe34e1eba1470f90392891c0f319ec', 'wang', 'localhost', '9876', null, null, 'RocketMQ', 'admin', '123456', null, null, '2021-08-23 17:02:20', '2021-08-23 17:09:34', null, null, null, 'topic3', null, null, null, null, null, null, null, '0', '1');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('d9cf3bc914da454ca824dc2debd5971f', 'ces1-1', 'localhost', '5672', 'tcp', '/', 'AMQP', 'guest', 'guest', null, null, '2021-08-23 16:36:39', '2021-08-23 16:45:19', '', null, '', 'topic1', '', '', '', '1', null, null, null, '1', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('dc78b2abf8394dd4a7c0e3dc85c9a1f0', 'ce1', 'localhost', '9876', null, null, 'RocketMQ', 'admin', '123456', null, null, '2021-08-23 15:33:38', '2021-08-23 15:43:09', null, null, null, 'ip', null, null, null, null, null, null, null, '0', '1');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('e7d411ffd6a343ba81b9ac0cab09deb1', 'ces3-1', '', null, '', '', 'RocketMQ', '', '', null, null, '2021-08-23 16:44:54', '2021-08-23 17:57:53', 'localhost:9876', null, 'test', 'test', '*', '', '', '1', null, null, null, '1', '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('e9ee26b2d65948e6aeeaf2d1a9071459', '123456', 'localhost', '9876', null, null, 'RocketMQ', 'admin', '123456', null, null, '2021-08-23 14:20:33', '2021-08-23 14:20:33', null, null, null, 'ip', null, null, null, null, null, null, null, '0', '1');

-- ----------------------------
-- Table structure for SRIG_RSU
-- ----------------------------
DROP TABLE IF EXISTS `SRIG_RSU`;
CREATE TABLE `SRIG_RSU` (
  `ID` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IP` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `PORT` int DEFAULT NULL,
  `NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `SN` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of SRIG_RSU
-- ----------------------------
