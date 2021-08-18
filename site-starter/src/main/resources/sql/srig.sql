/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1_3306
Source Server Version : 80025
Source Host           : 127.0.0.1:3306
Source Database       : srig

Target Server Type    : MYSQL
Target Server Version : 80025
File Encoding         : 65001

Date: 2021-08-18 18:24:35
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
  `EXPIRE_TIME` datetime DEFAULT NULL COMMENT '过期时间',
  `STATUS` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '服务状态0禁用,1正常运行,2运行异常',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of SRIG_FORWARD_ROUTER
-- ----------------------------
INSERT INTO `SRIG_FORWARD_ROUTER` VALUES ('71b3f62679a44567bed31162ee3df096', '586a12c1284a466e87492936a10e2930', 'a4abc334d90b4be1b1a975dde16d3dd7', 'dws_hw_gps_devc_tp_gpspoint_rt', 'test', '2021-08-26 10:17:15', '1');

-- ----------------------------
-- Table structure for SRIG_MQ_SERVER
-- ----------------------------
DROP TABLE IF EXISTS `SRIG_MQ_SERVER`;
CREATE TABLE `SRIG_MQ_SERVER` (
  `ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
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
  `VPN_ACCONT` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'vpn账号',
  `VPN_PASSWORD` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'vpn密码',
  `DEL_FLAG` tinyint(1) DEFAULT '0' COMMENT '0未删除1删除',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of SRIG_MQ_SERVER
-- ----------------------------
INSERT INTO `SRIG_MQ_SERVER` VALUES ('356815f42d4c45719c59a34921c9d4fc', 'localhost', '1883', 'tcp', '/', 'MQTT', 'admin', 'public', null, null, '2021-08-18 17:55:23', '2021-08-18 17:55:23', null, null, 'GID_TRACE_FUNSION_TT', null, '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('586a12c1284a466e87492936a10e2930', 'localhost', '9876', 'tcp', 'target', 'Aliyun_RocketMQ', '', '', null, null, '2021-08-18 17:53:05', '2021-08-18 17:53:05', 'http://MQ_INST_1551934212811443_BXdv0RAB.mq-internet-access.mq-internet.aliyuncs.com:80', null, 'GID_TRACE_FUNSION_TT', 'dws_hw_gps_devc_tp_gpspoint_rt', '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('a4abc334d90b4be1b1a975dde16d3dd7', 'localhost', '5672', 'tcp', '/', 'AMQP', 'guest', 'guest', null, null, '2021-08-18 18:11:22', '2021-08-18 18:11:22', null, null, 'GID_TRACE_FUNSION_TT', null, '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('c1902fad2ae54886b9c5c5e4ffc29d47', 'localhost', '9876', 'tcp', 'target', 'RocketMQ', '', '', null, null, '2021-08-18 17:54:19', '2021-08-18 17:54:19', 'localhost:9876', null, 'GID_TRACE_FUNSION_TT', 'test', '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER` VALUES ('c255c7a2aaeb404784f264a028bac90b', 'localhost', '5672', 'tcp', '/', 'AMQP', 'guest', 'guest', null, null, '2021-08-18 17:56:14', '2021-08-18 17:56:14', null, null, 'GID_TRACE_FUNSION_TT', null, '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', null, null, null, '0');

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
