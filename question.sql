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

 Date: 30/07/2020 11:58:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `question` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES ('当前体温', '<37.3℃');
INSERT INTO `question` VALUES ('实测体温', '%temperature%');
INSERT INTO `question` VALUES ('当前身体状况', '正常');
INSERT INTO `question` VALUES ('学生公寓', '%apartment%');
INSERT INTO `question` VALUES ('寝室号', '%dormitory%');
INSERT INTO `question` VALUES ('是否去过疫情中、高风险区', '否');
INSERT INTO `question` VALUES ('是否接触过疑似或确诊病例', '否');
INSERT INTO `question` VALUES ('是否承诺以上信息如实填写', '是');

SET FOREIGN_KEY_CHECKS = 1;
