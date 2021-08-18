/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1_3306
Source Server Version : 80025
Source Host           : 127.0.0.1:3306
Source Database       : srig

Target Server Type    : MYSQL
Target Server Version : 80025
File Encoding         : 65001

Date: 2021-08-18 12:01:39
*/

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for SRIG_FORWARD_ROUTER
-- ----------------------------
DROP TABLE IF EXISTS `SRIG_FORWARD_ROUTER`;
CREATE TABLE `SRIG_FORWARD_ROUTER`
(
    `ID`             varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `FROM_SERVER_ID` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci      DEFAULT NULL,
    `TO_SERVER_ID`   varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci      DEFAULT NULL,
    `FROM_TOPIC`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci     DEFAULT NULL,
    `TO_TOPIC`       varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci     DEFAULT NULL,
    `EXPIRE_TIME`    timestamp                                              NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '过期时间',
    `STATUS`         char(1) CHARACTER SET utf8 COLLATE utf8_general_ci          DEFAULT NULL COMMENT '服务状态0禁用,1正常运行,2运行异常',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of SRIG_FORWARD_ROUTER
-- ----------------------------
INSERT INTO `SRIG_FORWARD_ROUTER`
VALUES ('2', '5', '5', 'topic1', 'test1', null, null);
INSERT INTO `SRIG_FORWARD_ROUTER`
VALUES ('3', '6', '5', 'dws_hw_gps_devc_tp_gpspoint_rt', 'test1', '2021-08-29 18:05:03', '');

-- ----------------------------
-- Table structure for SRIG_MQ_SERVER
-- ----------------------------
DROP TABLE IF EXISTS `SRIG_MQ_SERVER`;
CREATE TABLE `SRIG_MQ_SERVER`
(
    `ID`            varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `IP`            varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT NULL,
    `PORT`          int                                                     DEFAULT NULL,
    `PROTOCOL`      varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci   DEFAULT NULL,
    `CLIENT_NAME`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `TYPE`          varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  DEFAULT NULL,
    `USERNAME`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `PASSWORD`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `RETRY`         tinyint                                                 DEFAULT '5' COMMENT '重连次数',
    `STATUS`        char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      DEFAULT NULL COMMENT '0离线,1在线',
    `CREATE_TIME`   datetime                                                DEFAULT NULL,
    `UPDATE_TIME`   datetime                                                DEFAULT NULL,
    `CLUSTER`       varchar(255)                                            DEFAULT NULL COMMENT '集群地址',
    `DEFAULT_PARAM` varchar(500)                                            DEFAULT NULL COMMENT '连接参数:json字符串',
    `GROUP_ID`         varchar(50)                                             DEFAULT NULL,
    `TAG`           varchar(50)                                             DEFAULT NULL,
    `SECRET_KEY`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `ACCESS_KEY`    varchar(255)                                            DEFAULT NULL,
    `NETWORK_TYPE`  varchar(1)                                              DEFAULT NULL COMMENT '网络类型',
    `VPN_ACCONT`    varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'vpn账号',
    `VPN_PASSWORD`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'vpn密码',
    `DEL_FLAG`      tinyint(1)                                              DEFAULT '1' COMMENT '0未删除1删除',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of SRIG_MQ_SERVER
-- ----------------------------
INSERT INTO `SRIG_MQ_SERVER`
VALUES ('1', 'localhost', '1883', 'tcp', 'srig-mq-integration', 'MQTT', null, null, '5', null, '2021-07-21 14:59:10',
        null, null, null, null, null, null, null, null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER`
VALUES ('2', '10.149.10.172', '1883', 'tcp', 'srig-mq-integration', 'MQTT', null, null, '5', null,
        '2021-07-21 14:59:10', null, null, null, null, null, null, null, null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER`
VALUES ('3', 'localhost', '5672', 'tcp', 'foo', 'AMQP', null, null, '5', null, '2021-07-21 14:59:10', null, null, null,
        null, null, null, null, null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER`
VALUES ('4', 'localhost', '1883', 'tcp', 'target', 'MQTT', null, null, '5', null, '2021-07-21 14:59:10', null, null,
        null, null, null, null, null, null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER`
VALUES ('5', 'localhost', '9876', 'tcp', 'target', 'RocketMQ', '', '', '5', '', '2021-07-21 14:59:10', null,
        '127.0.0.1:9876', null, null, null, null, null, null, null, null, '0');
INSERT INTO `SRIG_MQ_SERVER`
VALUES ('6', 'localhost', '9876', 'tcp', 'target', 'Aliyun_RocketMQ', '', '', '5', '', '2021-07-21 14:59:10',
        '2021-08-17 16:00:28',
        'http://MQ_INST_1551934212811443_BXdv0RAB.mq-internet-access.mq-internet.aliyuncs.com:80', '',
        'GID_TRACE_FUNSION_TT', '*', 'xHbzq7ntbOkS7HTPK2HqGBDX93UDkj', 'LTAI4GDmh8jPmnnozLrcNyLY', null, null, null,
        '1');

-- ----------------------------
-- Table structure for SRIG_RSU
-- ----------------------------
DROP TABLE IF EXISTS `SRIG_RSU`;
CREATE TABLE `SRIG_RSU`
(
    `ID`   varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `IP`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `PORT` int                                                     DEFAULT NULL,
    `NAME` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    `SN`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
  ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of SRIG_RSU
-- ----------------------------
