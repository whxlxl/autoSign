/*
 Navicat Premium Data Transfer

 Source Server         : 阿里云
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : 123.57.222.41:3306
 Source Schema         : autocampus

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 28/07/2020 08:17:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '外键id',
  `day` date NOT NULL COMMENT '当前日期',
  `clock_six` int(255) NULL DEFAULT NULL COMMENT '6点的是否签到，0表示没有，1表示签到',
  `clock_twelve` int(255) NULL DEFAULT NULL COMMENT '12点的是否签到，0表示没有，1表示签到',
  `six_describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '6点签到情况，空表示没签到',
  `twelve_describe` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '12点的签到情况，空表示没签到',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `username_w`(`username`) USING BTREE,
  CONSTRAINT `username_w` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
