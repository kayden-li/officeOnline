/*
Navicat MySQL Data Transfer

Source Server         : lianjie57
Source Server Version : 50717
Source Host           : localhost:3307
Source Database       : doconline

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2020-09-25 18:52:31
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for gd_doc
-- ----------------------------
DROP TABLE IF EXISTS `gd_doc`;
CREATE TABLE `gd_doc` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for gd_update
-- ----------------------------
DROP TABLE IF EXISTS `gd_update`;
CREATE TABLE `gd_update` (
  `doc` int(10) NOT NULL,
  `user` int(8) NOT NULL,
  `sheet` varchar(255) NOT NULL,
  `position` varchar(5) NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  `updateTime` datetime NOT NULL,
  PRIMARY KEY (`doc`,`sheet`,`position`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for gd_user
-- ----------------------------
DROP TABLE IF EXISTS `gd_user`;
CREATE TABLE `gd_user` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `userName` varchar(20) NOT NULL,
  `passWord` varchar(20) NOT NULL,
  `email` varchar(30) NOT NULL,
  `createTime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
