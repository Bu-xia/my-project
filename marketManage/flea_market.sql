/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80011
 Source Host           : localhost:3306
 Source Schema         : flea_market

 Target Server Type    : MySQL
 Target Server Version : 80011
 File Encoding         : 65001

 Date: 21/10/2025 12:50:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for booth
-- ----------------------------
DROP TABLE IF EXISTS `booth`;
CREATE TABLE `booth`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '摊位ID',
  `booth_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '摊位编号',
  `booth_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '摊位名称',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '位置',
  `area` decimal(10, 2) NULL DEFAULT NULL COMMENT '面积（平方米）',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '租金（元/月）',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-可租，1-已租，2-维修中',
  `images` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '摊位图片URL，多个用逗号分隔',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '摊位描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `booth_no`(`booth_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '摊位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booth
-- ----------------------------
INSERT INTO `booth` VALUES (1, 'A001', 'A区1号摊位', 'A区东侧', 15.50, 1200.00, 1, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '靠近主入口，人流量大，适合经营服装、小商品', '2025-10-13 16:38:06', '2025-10-13 20:45:17');
INSERT INTO `booth` VALUES (2, 'A002', 'A区2号摊位', 'A区东侧', 18.00, 1500.00, 1, 'https://bpic.588ku.com/back_pic/19/04/13/a287a52c1e3f190cd811e87e01876a80.jpg', '紧邻A001，位置优越', '2025-10-13 16:38:06', '2025-10-13 21:43:07');
INSERT INTO `booth` VALUES (3, 'A003', 'A区3号摊位', 'A区中部', 12.00, 1000.00, 1, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '适合经营小吃、饮品', '2025-10-13 16:38:06', '2025-10-13 20:45:21');
INSERT INTO `booth` VALUES (4, 'B001', 'B区1号摊位', 'B区北侧', 20.00, 1800.00, 0, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '面积较大，适合经营家具、家电', '2025-10-13 16:38:06', '2025-10-13 20:45:23');
INSERT INTO `booth` VALUES (5, 'B002', 'B区2号摊位', 'B区北侧', 16.00, 1400.00, 1, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '靠近停车场，方便卸货', '2025-10-13 16:38:06', '2025-10-13 20:45:25');
INSERT INTO `booth` VALUES (6, 'B003', 'B区3号摊位', 'B区中部', 14.00, 1100.00, 0, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '周边摊位繁华，人气旺', '2025-10-13 16:38:06', '2025-10-13 20:45:27');
INSERT INTO `booth` VALUES (7, 'C001', 'C区1号摊位', 'C区南侧', 10.00, 800.00, 0, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '适合新手创业，租金优惠', '2025-10-13 16:38:06', '2025-10-13 20:45:29');
INSERT INTO `booth` VALUES (8, 'C002', 'C区2号摊位', 'C区南侧', 11.00, 850.00, 2, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '维修中，预计一周后开放', '2025-10-13 16:38:06', '2025-10-13 20:45:31');
INSERT INTO `booth` VALUES (9, 'C003', 'C区3号摊位', 'C区东侧', 13.00, 950.00, 0, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '安静位置，适合书籍、文具经营', '2025-10-13 16:38:06', '2025-10-13 20:45:33');
INSERT INTO `booth` VALUES (10, 'D001', 'D区1号摊位', 'D区西侧', 25.00, 2200.00, 1, 'https://img.tukuppt.com/png_preview/02/94/99/6LDCoRLnrK.jpg!/fw/780', '超大面积，适合综合经营', '2025-10-13 16:38:06', '2025-10-13 20:45:36');
INSERT INTO `booth` VALUES (11, 'P001', '动漫摊位', 'P区1号摊位', 15.00, 1800.00, 0, 'https://img-baofun.zhhainiao.com/pcwallpaper_ugc/static/50706fe64cbbdddc2c8c99d27eb64d4d.jpeg?x-oss-process=image%2fresize%2cm_lfit%2cw_3840%2ch_2160', '动漫', '2025-10-21 12:38:39', '2025-10-21 12:38:39');

-- ----------------------------
-- Table structure for booth_apply
-- ----------------------------
DROP TABLE IF EXISTS `booth_apply`;
CREATE TABLE `booth_apply`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` bigint(20) NOT NULL COMMENT '申请用户ID',
  `booth_id` bigint(20) NOT NULL COMMENT '申请摊位ID',
  `apply_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '申请原因',
  `business_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '经营计划',
  `lease_months` int(11) NULL DEFAULT NULL COMMENT '租赁月数',
  `start_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '开始日期',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '经营类型',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `business_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '经营描述',
  `id_card_images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '身份证件图片URLs',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-待审核，1-审核通过，2-审核拒绝，3-已取消',
  `reject_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '拒绝原因',
  `apply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `booth_id`(`booth_id`) USING BTREE,
  CONSTRAINT `booth_apply_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `booth_apply_ibfk_2` FOREIGN KEY (`booth_id`) REFERENCES `booth` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '摊位租赁申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of booth_apply
-- ----------------------------
INSERT INTO `booth_apply` VALUES (1, 1, 2, '想在市场开一家服装店', '主要经营时尚女装，目标客户为25-40岁女性', 12, NULL, NULL, NULL, NULL, 'https://picsum.photos/300/200?random=201,https://picsum.photos/300/200?random=202', 1, NULL, '2025-10-01 09:30:00', '2025-10-01 14:20:00');
INSERT INTO `booth_apply` VALUES (2, 2, 5, '想开一家特色小吃店', '经营地方特色小吃，保证食品安全卫生', 6, NULL, NULL, NULL, NULL, 'https://picsum.photos/300/200?random=203,https://picsum.photos/300/200?random=204', 1, NULL, '2025-10-02 10:15:00', '2025-10-02 16:30:00');
INSERT INTO `booth_apply` VALUES (3, 3, 1, '想尝试摆摊创业', '售卖手工饰品和小商品', 3, NULL, NULL, NULL, NULL, 'https://picsum.photos/300/200?random=205,https://picsum.photos/300/200?random=206', 0, NULL, '2025-10-10 11:00:00', NULL);
INSERT INTO `booth_apply` VALUES (4, 4, 10, '扩大经营规模', '原有摊位太小，需要更大的场地经营百货', 12, NULL, NULL, NULL, NULL, 'https://picsum.photos/300/200?random=207,https://picsum.photos/300/200?random=208', 1, NULL, '2025-09-20 08:45:00', '2025-09-21 10:00:00');
INSERT INTO `booth_apply` VALUES (9, 6, 1, '1111111111111111111', '11111111111111111111', 3, '2025-10-20', 'food', '19509521381', '11111111111111111111', 'http://localhost:8080/uploads/documents/20251013230314_e659dda0d4e64ed7881319b8bb94a74a.jpg', 1, NULL, '2025-10-13 23:03:17', '2025-10-14 13:04:36');
INSERT INTO `booth_apply` VALUES (10, 6, 3, 'ahhaahahahahah', 'fvfdfgvdfvdffgfsgdfg', 6, '2025-10-30', 'daily', '19509521777', 'gdfgdfvdfvfdsv', 'http://localhost:8080/uploads/documents/20251014134607_f15f595363fa47d688b2df1746e93501.jpg', 1, NULL, '2025-10-14 13:46:14', '2025-10-14 13:48:44');

-- ----------------------------
-- Table structure for contract
-- ----------------------------
DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '合同ID',
  `contract_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '合同编号',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `booth_id` bigint(20) NOT NULL COMMENT '摊位ID',
  `start_date` date NOT NULL COMMENT '租赁开始日期',
  `end_date` date NOT NULL COMMENT '租赁结束日期',
  `monthly_rent` decimal(10, 2) NOT NULL COMMENT '月租金',
  `deposit` decimal(10, 2) NOT NULL COMMENT '押金',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已审核，2-已归档，3-已终止',
  `contract_file` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '合同文件URL',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `contract_no`(`contract_no`) USING BTREE,
  INDEX `tenant_id`(`tenant_id`) USING BTREE,
  INDEX `booth_id`(`booth_id`) USING BTREE,
  CONSTRAINT `contract_ibfk_1` FOREIGN KEY (`tenant_id`) REFERENCES `tenant` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `contract_ibfk_2` FOREIGN KEY (`booth_id`) REFERENCES `booth` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租赁合同表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract
-- ----------------------------
INSERT INTO `contract` VALUES (1, 'HT202510001', 1, 2, '2025-10-05', '2026-10-04', 1500.00, 3000.00, 1, 'https://picsum.photos/800/600?random=301', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `contract` VALUES (2, 'HT202510002', 2, 5, '2025-10-10', '2026-04-09', 1400.00, 2800.00, 2, 'https://picsum.photos/800/600?random=302', NULL, '2025-10-13 16:38:06', '2025-10-14 12:02:11');
INSERT INTO `contract` VALUES (3, 'HT202509003', 4, 10, '2025-09-25', '2026-09-24', 2200.00, 4400.00, 3, 'https://picsum.photos/800/600?random=303', NULL, '2025-10-13 16:38:06', '2025-10-14 12:02:58');
INSERT INTO `contract` VALUES (4, 'HT20251014130436', 5, 1, '2025-10-20', '2026-01-20', 1200.00, 2400.00, 1, 'https://picsum.photos/800/600?random=303', NULL, '2025-10-14 13:04:36', '2025-10-14 13:09:23');
INSERT INTO `contract` VALUES (5, 'HT20251014134843', 5, 3, '2025-10-30', '2026-04-30', 1000.00, 2000.00, 1, NULL, NULL, '2025-10-14 13:48:43', '2025-10-14 13:48:43');

-- ----------------------------
-- Table structure for finance_record
-- ----------------------------
DROP TABLE IF EXISTS `finance_record`;
CREATE TABLE `finance_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `record_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '记录编号',
  `record_type` tinyint(4) NOT NULL COMMENT '类型：1-收入，2-支出',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类：租金收入、押金收入、水电费、维修费、工资等',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `payment_id` bigint(20) NULL DEFAULT NULL COMMENT '关联支付ID',
  `related_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '相关人员',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '说明',
  `record_date` date NOT NULL COMMENT '记录日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `record_no`(`record_no`) USING BTREE,
  INDEX `payment_id`(`payment_id`) USING BTREE,
  CONSTRAINT `finance_record_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '费用收支记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of finance_record
-- ----------------------------
INSERT INTO `finance_record` VALUES (1, 'FR202510001', 1, '押金收入', 3000.00, 1, '张三', 'A002摊位押金', '2025-10-05', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (2, 'FR202510002', 1, '租金收入', 1500.00, 2, '张三', 'A002摊位10月租金', '2025-10-05', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (3, 'FR202510003', 1, '押金收入', 2800.00, 3, '李四', 'B002摊位押金', '2025-10-10', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (4, 'FR202510004', 1, '租金收入', 1400.00, 4, '李四', 'B002摊位10月租金', '2025-10-10', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (5, 'FR202509005', 1, '押金收入', 4400.00, 5, '赵六', 'D001摊位押金', '2025-09-25', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (6, 'FR202509006', 1, '租金收入', 2200.00, 6, '赵六', 'D001摊位9月租金', '2025-09-25', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (7, 'FR202510007', 2, '水电费', 850.00, NULL, '供电局', '9月份市场水电费', '2025-10-08', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (8, 'FR202510008', 2, '维修费', 1200.00, NULL, '王师傅', 'C002摊位维修费用', '2025-10-12', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (9, 'FR202510009', 2, '工资', 5000.00, NULL, '保洁人员', '10月保洁工资', '2025-10-01', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (10, 'FR202510010', 2, '物业管理费', 2000.00, NULL, '物业公司', '10月物业管理费', '2025-10-01', '2025-10-13 16:38:06');
INSERT INTO `finance_record` VALUES (11, 'FR20251014133342', 1, '押金收入', 2400.00, 9, '用户6', '押金收入收入', '2025-10-14', '2025-10-14 13:33:42');
INSERT INTO `finance_record` VALUES (12, 'FR20251014135034', 1, '押金收入', 2000.00, 11, '用户6', '押金收入收入', '2025-10-14', '2025-10-14 13:50:34');

-- ----------------------------
-- Table structure for inventory
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物资ID',
  `item_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物资编号',
  `item_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '物资名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类：清洁用品、维修工具、办公用品、其他',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位：个、箱、套等',
  `quantity` int(11) NULL DEFAULT 0 COMMENT '当前库存数量',
  `warning_quantity` int(11) NULL DEFAULT 0 COMMENT '预警数量',
  `item_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '物资图片URL',
  `storage_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存放位置',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `item_no`(`item_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '物资仓储表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory
-- ----------------------------
INSERT INTO `inventory` VALUES (1, 'WZ001', '扫把', '清洁用品', '把', 15, 5, 'https://picsum.photos/300/300?random=601', '仓库A区', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (2, 'WZ002', '拖把', '清洁用品', '把', 19, 5, 'https://picsum.photos/300/300?random=602', '仓库A区', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (3, 'WZ003', '垃圾桶', '清洁用品', '个', 25, 10, 'https://picsum.photos/300/300?random=603', '仓库A区', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (4, 'WZ004', '灭火器', '安全设备', '个', 30, 15, 'https://picsum.photos/300/300?random=604', '仓库B区', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (5, 'WZ005', '电工工具套装', '维修工具', '套', 3, 2, 'https://picsum.photos/300/300?random=605', '仓库B区', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (6, 'WZ006', '螺丝刀套装', '维修工具', '套', 5, 2, 'https://picsum.photos/300/300?random=606', '仓库B区', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (7, 'WZ007', 'A4打印纸', '办公用品', '箱', 8, 3, 'https://picsum.photos/300/300?random=607', '办公室', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (8, 'WZ008', '签字笔', '办公用品', '盒', 20, 5, 'https://picsum.photos/300/300?random=608', '办公室', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (9, 'WZ009', '文件夹', '办公用品', '个', 50, 10, 'https://picsum.photos/300/300?random=609', '办公室', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `inventory` VALUES (10, 'WZ010', '电线卷', '维修材料', '卷', 4, 2, 'https://picsum.photos/300/300?random=610', '仓库B区', NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');

-- ----------------------------
-- Table structure for inventory_record
-- ----------------------------
DROP TABLE IF EXISTS `inventory_record`;
CREATE TABLE `inventory_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `inventory_id` bigint(20) NOT NULL COMMENT '物资ID',
  `record_type` tinyint(4) NOT NULL COMMENT '类型：1-入库，2-出库',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作人',
  `reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原因说明',
  `record_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `inventory_id`(`inventory_id`) USING BTREE,
  CONSTRAINT `inventory_record_ibfk_1` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '物资出入库记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of inventory_record
-- ----------------------------
INSERT INTO `inventory_record` VALUES (1, 1, 1, 20, '李经理', '批量采购', '2025-09-15 10:00:00');
INSERT INTO `inventory_record` VALUES (2, 1, 2, 5, '张保洁', '日常使用', '2025-09-20 09:00:00');
INSERT INTO `inventory_record` VALUES (3, 2, 1, 15, '李经理', '批量采购', '2025-09-15 10:00:00');
INSERT INTO `inventory_record` VALUES (4, 2, 2, 3, '张保洁', '日常使用', '2025-09-25 08:30:00');
INSERT INTO `inventory_record` VALUES (5, 4, 1, 30, '李经理', '消防检查补充', '2025-09-10 14:00:00');
INSERT INTO `inventory_record` VALUES (6, 7, 1, 10, '王秘书', '办公用品采购', '2025-10-01 11:00:00');
INSERT INTO `inventory_record` VALUES (7, 7, 2, 2, '王秘书', '打印使用', '2025-10-05 15:00:00');
INSERT INTO `inventory_record` VALUES (8, 10, 1, 5, '李经理', '维修材料储备', '2025-09-18 16:00:00');
INSERT INTO `inventory_record` VALUES (9, 10, 2, 1, '王师傅', 'C002摊位维修', '2025-10-12 10:00:00');
INSERT INTO `inventory_record` VALUES (10, 2, 1, 5, '888', '999', '2025-10-14 11:33:14');
INSERT INTO `inventory_record` VALUES (11, 2, 1, 3, '777', '123', '2025-10-14 11:33:44');
INSERT INTO `inventory_record` VALUES (12, 2, 2, 1, '888', '123456', '2025-10-14 11:34:20');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `notice_type` tinyint(4) NULL DEFAULT 1 COMMENT '类型：1-系统公告，2-活动通知，3-紧急通知',
  `is_top` tinyint(4) NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `publisher` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发布人',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：1-发布，0-下架',
  `publish_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '市场国庆放假通知', '各位租户：根据国家法定节假日安排，本市场10月1日至7日放假，期间正常营业。请各位租户做好节假日期间的安全防护工作。', 1, 1, 'https://picsum.photos/800/400?random=501', '系统管理员', 156, 1, '2025-09-25 10:00:00', '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `notice` VALUES (2, '摊位租金缴纳提醒', '各位租户请注意：每月5日前请及时缴纳当月租金，逾期将收取滞纳金。可通过系统在线支付或到管理处现场缴纳。', 1, 1, 'https://picsum.photos/800/400?random=502', '系统管理员', 209, 1, '2025-10-01 09:00:00', '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `notice` VALUES (3, '市场消防安全检查通知', '为确保市场安全，将于本周五（10月18日）进行消防安全检查，请各位租户配合检查工作，提前整理好摊位。', 3, 0, 'https://picsum.photos/800/400?random=503', '系统管理员', 90, 1, '2025-10-13 14:30:00', '2025-10-13 16:38:06', '2025-10-13 23:20:22');
INSERT INTO `notice` VALUES (4, '双十一促销活动方案', '市场将于11月11日举办\"双十一购物节\"活动，欢迎各位租户积极参与。活动期间将有流量扶持和宣传推广，详情请联系管理处。', 2, 1, 'https://picsum.photos/800/400?random=504', '系统管理员', 131, 1, '2025-10-08 11:00:00', '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `notice` VALUES (5, '新增停车位使用说明', '市场西侧新增停车位20个，请各位租户和顾客按规定停放车辆，禁止占用消防通道。', 1, 0, 'https://picsum.photos/800/400?random=505', '系统管理员', 95, 1, '2025-10-05 16:00:00', '2025-10-13 16:38:06', '2025-10-13 23:20:25');

-- ----------------------------
-- Table structure for payment
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `contract_id` bigint(20) NULL DEFAULT NULL COMMENT '关联合同ID',
  `payment_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付单号',
  `payment_type` tinyint(4) NOT NULL COMMENT '支付类型：1-租金，2-押金，3-水电费，4-其他',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-已退款',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付方式：alipay-支付宝，wechat-微信，cash-现金',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `receipt_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '支付凭证URL',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `payment_no`(`payment_no`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `contract_id`(`contract_id`) USING BTREE,
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment
-- ----------------------------
INSERT INTO `payment` VALUES (1, 1, 1, 'PAY202510001', 2, 3000.00, 1, 'alipay', '2025-10-05 10:30:00', 'https://i04piccdn.sogoucdn.com/2e47890d93908ebe', NULL, '2025-10-13 16:38:06', '2025-10-14 13:25:49');
INSERT INTO `payment` VALUES (2, 1, 1, 'PAY202510002', 1, 1500.00, 1, 'alipay', '2025-10-05 10:35:00', 'https://i04piccdn.sogoucdn.com/2e47890d93908ebe', NULL, '2025-10-13 16:38:06', '2025-10-14 13:25:51');
INSERT INTO `payment` VALUES (3, 2, 2, 'PAY202510003', 2, 2800.00, 1, 'wechat', '2025-10-10 09:20:00', 'https://i04piccdn.sogoucdn.com/2e47890d93908ebe', NULL, '2025-10-13 16:38:06', '2025-10-14 13:25:53');
INSERT INTO `payment` VALUES (4, 2, 2, 'PAY202510004', 1, 1400.00, 1, 'wechat', '2025-10-10 09:25:00', 'https://i04piccdn.sogoucdn.com/2e47890d93908ebe', NULL, '2025-10-13 16:38:06', '2025-10-14 13:25:55');
INSERT INTO `payment` VALUES (5, 4, 3, 'PAY202509005', 2, 4400.00, 1, 'alipay', '2025-09-25 14:10:00', 'https://i04piccdn.sogoucdn.com/2e47890d93908ebe', NULL, '2025-10-13 16:38:06', '2025-10-14 13:25:57');
INSERT INTO `payment` VALUES (6, 4, 3, 'PAY202509006', 1, 2200.00, 1, 'alipay', '2025-09-25 14:15:00', 'https://i04piccdn.sogoucdn.com/2e47890d93908ebe', NULL, '2025-10-13 16:38:06', '2025-10-14 13:26:01');
INSERT INTO `payment` VALUES (7, 1, 1, 'PAY202511001', 1, 1500.00, 0, NULL, NULL, NULL, NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `payment` VALUES (8, 2, 2, 'PAY202511002', 1, 1400.00, 0, NULL, NULL, NULL, NULL, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `payment` VALUES (9, 6, 4, 'PAYDEPOSIT20251014130436', 2, 2400.00, 1, '支付宝', '2025-10-14 13:33:43', NULL, NULL, '2025-10-14 13:04:36', '2025-10-14 13:33:42');
INSERT INTO `payment` VALUES (10, 6, 4, 'PAYRENT20251014130436', 1, 1200.00, 0, NULL, NULL, NULL, NULL, '2025-10-14 13:04:36', '2025-10-14 13:04:36');
INSERT INTO `payment` VALUES (11, 6, 5, 'PAYDEPOSIT20251014134843', 2, 2000.00, 1, '微信支付', '2025-10-14 13:50:34', NULL, NULL, '2025-10-14 13:48:43', '2025-10-14 13:50:34');
INSERT INTO `payment` VALUES (12, 6, 5, 'PAYRENT20251014134843', 1, 1000.00, 0, NULL, NULL, NULL, NULL, '2025-10-14 13:48:43', '2025-10-14 13:48:43');

-- ----------------------------
-- Table structure for rental
-- ----------------------------
DROP TABLE IF EXISTS `rental`;
CREATE TABLE `rental`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '租赁ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `contract_id` bigint(20) NOT NULL COMMENT '合同ID',
  `booth_id` bigint(20) NOT NULL COMMENT '摊位ID',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：1-租赁中，2-已到期，3-已退租',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `contract_id`(`contract_id`) USING BTREE,
  INDEX `booth_id`(`booth_id`) USING BTREE,
  CONSTRAINT `rental_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `rental_ibfk_2` FOREIGN KEY (`contract_id`) REFERENCES `contract` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `rental_ibfk_3` FOREIGN KEY (`booth_id`) REFERENCES `booth` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租赁信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of rental
-- ----------------------------
INSERT INTO `rental` VALUES (1, 1, 1, 2, '2025-10-05', '2026-10-04', 1, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `rental` VALUES (2, 2, 2, 5, '2025-10-10', '2026-04-09', 1, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `rental` VALUES (3, 4, 3, 10, '2025-09-25', '2026-09-24', 1, '2025-10-13 16:38:06', '2025-10-13 16:38:06');
INSERT INTO `rental` VALUES (4, 6, 4, 1, '2025-10-20', '2026-01-20', 1, '2025-10-14 13:04:36', '2025-10-14 13:04:36');
INSERT INTO `rental` VALUES (5, 6, 5, 3, '2025-10-30', '2026-04-30', 1, '2025-10-14 13:48:43', '2025-10-14 13:48:43');

-- ----------------------------
-- Table structure for sys_admin
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_admin
-- ----------------------------
INSERT INTO `sys_admin` VALUES (1, 'admin', '123456', '系统管理员', '13800138000', 'admin@12345678.com', 'http://localhost:8080/uploads/avatars/20251014110051_d82d7045882c417cbd7aa3148c359f71.jpg', 1, '2025-10-13 16:38:06', '2025-10-14 10:45:27');

-- ----------------------------
-- Table structure for tenant
-- ----------------------------
DROP TABLE IF EXISTS `tenant`;
CREATE TABLE `tenant`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '租户ID',
  `user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `business_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商户名称',
  `business_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '经营类型',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `business_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '营业执照URL',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：1-正常，0-停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `tenant_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tenant
-- ----------------------------
INSERT INTO `tenant` VALUES (1, 1, '三哥服装店', '服装鞋帽', '张三', '13900139888', 'https://ts2.tc.mm.bing.net/th/id/OIP-C.LwP-AFm_RX4-Ifwdasy0qwAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3', 1, '2025-10-13 16:38:06', '2025-10-14 11:46:16');
INSERT INTO `tenant` VALUES (2, 2, '四妹小吃摊', '餐饮美食', '李四', '13900139002', 'https://ts2.tc.mm.bing.net/th/id/OIP-C.LwP-AFm_RX4-Ifwdasy0qwAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3', 1, '2025-10-13 16:38:06', '2025-10-14 11:46:18');
INSERT INTO `tenant` VALUES (3, 4, '老六百货', '日用百货', '赵六', '13900139004', 'https://ts2.tc.mm.bing.net/th/id/OIP-C.LwP-AFm_RX4-Ifwdasy0qwAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3', 1, '2025-10-13 16:38:06', '2025-10-14 11:46:20');
INSERT INTO `tenant` VALUES (4, 5, '孙氏家电', '家用电器', '孙七', '13900139005', 'https://ts2.tc.mm.bing.net/th/id/OIP-C.LwP-AFm_RX4-Ifwdasy0qwAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3', 1, '2025-10-13 16:38:06', '2025-10-14 11:46:23');
INSERT INTO `tenant` VALUES (5, 6, '11111111111111111111', 'food', '一只小松鼠', '19509521381', 'https://ts2.tc.mm.bing.net/th/id/OIP-C.LwP-AFm_RX4-Ifwdasy0qwAAAA?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3', 1, '2025-10-14 13:04:36', '2025-10-14 13:12:41');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号',
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'zhangsan', '123456', '张三', '13900139001', 'zhangsan@qq.com', 'https://www.keaitupian.cn/cjpic/frombd/0/253/1152107840/119779555.jpg', '110101199001011234', 1, '2025-10-13 16:38:06', '2025-10-13 20:07:09');
INSERT INTO `user` VALUES (2, 'lisi', '123456', '李四', '13900139002', 'lisi@qq.com', 'https://www.keaitupian.cn/cjpic/frombd/0/253/1152107840/119779555.jpg', '110101199002021234', 1, '2025-10-13 16:38:06', '2025-10-13 20:07:11');
INSERT INTO `user` VALUES (3, 'wangwu', '123456', '王五', '13900139003', 'wangwu@qq.com', 'https://www.keaitupian.cn/cjpic/frombd/0/253/1152107840/119779555.jpg', '110101199003031234', 1, '2025-10-13 16:38:06', '2025-10-13 20:07:12');
INSERT INTO `user` VALUES (4, 'zhaoliu', '123456', '赵六', '13900139004', 'zhaoliu@qq.com', 'https://www.keaitupian.cn/cjpic/frombd/0/253/1152107840/119779555.jpg', '110101199004041234', 1, '2025-10-13 16:38:06', '2025-10-13 20:07:14');
INSERT INTO `user` VALUES (5, 'sunqi', '123456', '孙七', '13900139005', 'sunqi@qq.com', 'https://www.keaitupian.cn/cjpic/frombd/0/253/1152107840/119779555.jpg', '110101199005051234', 1, '2025-10-13 16:38:06', '2025-10-13 20:07:16');
INSERT INTO `user` VALUES (6, 'zhx', '123456', '一只小松鼠', '19509521999', '123456@qq.com', '/uploads/avatars/20251013203546_313fa707f1e14e6ea1d166e858412b65.png', '110101199005051288', 1, '2025-10-13 20:00:58', '2025-10-13 20:24:06');

SET FOREIGN_KEY_CHECKS = 1;
