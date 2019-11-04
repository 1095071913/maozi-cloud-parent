/*
SQLyog Ultimate v12.08 (32 bit)
MySQL - 5.7.26 : Database - oauth2
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`oauth2` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `oauth2`;

/*Table structure for table `clients` */

DROP TABLE IF EXISTS `clients`;

CREATE TABLE `clients` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `clientId` varchar(100) NOT NULL,
  `resourceIds` varchar(255) DEFAULT NULL,
  `isSecretRequired` bit(1) DEFAULT NULL,
  `clientSecret` varchar(100) DEFAULT NULL,
  `isScoped` bit(1) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `authorizedGrantTypes` varchar(255) NOT NULL,
  `registeredRedirectUri` varchar(255) NOT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `isAutoApprove` bit(1) DEFAULT NULL,
  `accessTokenValiditySeconds` int(11) DEFAULT NULL,
  `refreshTokenValiditySeconds` int(11) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL COMMENT 'createDate 创建时间',
  `updateDate` datetime DEFAULT NULL COMMENT 'updateDate 更新时间',
  `createBy` varchar(32) DEFAULT NULL COMMENT 'createBy 创建者',
  `updateBy` varchar(32) DEFAULT NULL COMMENT 'updateBy 更新者',
  `remarks` varchar(32) DEFAULT NULL COMMENT 'remarks 备注信息',
  `deleted` varchar(1) DEFAULT '0' COMMENT 'deleted 逻辑删除',
  `extendS1` varchar(32) DEFAULT NULL COMMENT 'extendS1 扩展String  1',
  `extendS2` varchar(32) DEFAULT NULL COMMENT 'extendS2 扩展String  2',
  `extendS3` varchar(32) DEFAULT NULL COMMENT 'extendS3 扩展String  3',
  `extendS4` varchar(32) DEFAULT NULL COMMENT 'extendS4 扩展String  4',
  `extendS5` varchar(32) DEFAULT NULL COMMENT 'extendS5 扩展String  5',
  `extendS6` varchar(32) DEFAULT NULL COMMENT 'extendS6 扩展String  6',
  `extendS7` varchar(32) DEFAULT NULL COMMENT 'extendS7 扩展String  7',
  `extendS8` varchar(32) DEFAULT NULL COMMENT 'extendS8 扩展String  8',
  `extendI1` int(11) DEFAULT NULL COMMENT 'extendI1 扩展Integer  1',
  `extendI2` int(11) DEFAULT NULL COMMENT 'extendI2 扩展Integer  2',
  `extendI3` int(11) DEFAULT NULL COMMENT 'extendI3 扩展Integer  3',
  `extendI4` int(11) DEFAULT NULL COMMENT 'extendI4 扩展Integer  4',
  `extendF1` bigint(20) DEFAULT NULL COMMENT 'extendF1 扩展Float  1',
  `extendF2` bigint(20) DEFAULT NULL COMMENT 'extendF2 扩展Float  2',
  `extendF3` bigint(20) DEFAULT NULL COMMENT 'extendF3 扩展Float  3',
  `extendF4` bigint(20) DEFAULT NULL COMMENT 'extendF4 扩展Float  4',
  `extendD1` datetime DEFAULT NULL COMMENT 'extendD1 扩展 Date  1',
  `extendD2` datetime DEFAULT NULL COMMENT 'extendD2 扩展 Date  2',
  `extendD3` datetime DEFAULT NULL COMMENT 'extendD3 扩展 Date  3',
  `extendD4` datetime DEFAULT NULL COMMENT 'extendD4 扩展 Date  4',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `clients` */

insert  into `clients`(`id`,`clientId`,`resourceIds`,`isSecretRequired`,`clientSecret`,`isScoped`,`scope`,`authorizedGrantTypes`,`registeredRedirectUri`,`authorities`,`isAutoApprove`,`accessTokenValiditySeconds`,`refreshTokenValiditySeconds`,`createDate`,`updateDate`,`createBy`,`updateBy`,`remarks`,`deleted`,`extendS1`,`extendS2`,`extendS3`,`extendS4`,`extendS5`,`extendS6`,`extendS7`,`extendS8`,`extendI1`,`extendI2`,`extendI3`,`extendI4`,`extendF1`,`extendF2`,`extendF3`,`extendF4`,`extendD1`,`extendD2`,`extendD3`,`extendD4`) values (1,'client','boot-server','','$2a$10$SJYPYaAeAR9WKM/Fy2dV9eVmpeZ0RGltQ77Rfy8ixld2PI5fRL7XK','','select','refresh_token,authorization_code,password','http://localhost:12001/login','CLIENT,ADMIN','',1800,36000,'2018-10-16 10:02:14','2018-12-14 09:05:03',NULL,NULL,NULL,'0',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

/*Table structure for table `oauth_access_token` */

DROP TABLE IF EXISTS `oauth_access_token`;

CREATE TABLE `oauth_access_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` longblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` longblob,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `oauth_access_token` */

/*Table structure for table `oauth_refresh_token` */

DROP TABLE IF EXISTS `oauth_refresh_token`;

CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` longblob,
  `authentication` longblob
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `oauth_refresh_token` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
