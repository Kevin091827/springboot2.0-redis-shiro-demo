/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : mybatis

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2019-02-15 03:52:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for student_info
-- ----------------------------
DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info` (
  `username` varchar(255) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sex` varchar(255) DEFAULT NULL,
  `profession` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=160 DEFAULT CHARSET=utf8;
