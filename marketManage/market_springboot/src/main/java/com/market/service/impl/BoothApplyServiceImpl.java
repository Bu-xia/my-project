package com.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.market.controller.admin.AdminApplyController.ApplyStatistics;
import com.market.entity.*;
import com.market.entity.vo.BoothApplyVO;
import com.market.mapper.*;
import com.market.service.BoothApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 摊位租赁申请Service实现类
 */
@Service
public class BoothApplyServiceImpl implements BoothApplyService {
    
    @Autowired
    private BoothApplyMapper boothApplyMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private BoothMapper boothMapper;
    
    @Autowired
    private TenantMapper tenantMapper;
    
    @Autowired
    private ContractMapper contractMapper;
    
    @Autowired
    private RentalMapper rentalMapper;
    
    @Autowired
    private PaymentMapper paymentMapper;
    
    @Override
    public boolean submitApply(BoothApply apply) {
        apply.setApplyTime(LocalDateTime.now());
        apply.setStatus(0); // 待审核
        return boothApplyMapper.insert(apply) > 0;
    }
    
    @Override
    public Page<BoothApply> getMyApplyPage(int current, int size, Long userId) {
        Page<BoothApply> page = new Page<>(current, size);
        LambdaQueryWrapper<BoothApply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BoothApply::getUserId, userId)
               .orderByDesc(BoothApply::getApplyTime);
        return boothApplyMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Page<BoothApplyVO> getMyApplyPageWithBooth(int current, int size, Long userId) {
        Page<BoothApplyVO> page = new Page<>(current, size);
        return boothApplyMapper.selectMyApplyPageWithBooth(page, userId);
    }
    
    @Override
    public BoothApply getById(Long id) {
        return boothApplyMapper.selectById(id);
    }
    
    @Override
    public boolean cancelApply(Long id) {
        BoothApply apply = new BoothApply();
        apply.setId(id);
        apply.setStatus(3); // 已取消
        return boothApplyMapper.updateById(apply) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditApply(Long id, Integer status, String rejectReason) {
        // 更新申请状态
        BoothApply apply = boothApplyMapper.selectById(id);
        if (apply == null) {
            throw new RuntimeException("申请不存在");
        }
        
        apply.setStatus(status);
        apply.setRejectReason(rejectReason);
        apply.setAuditTime(LocalDateTime.now());
        
        int updateResult = boothApplyMapper.updateById(apply);
        if (updateResult <= 0) {
            throw new RuntimeException("更新申请状态失败");
        }
        
        // 如果审核通过，执行后续业务逻辑
        if (status == 1) {
            try {
                processApprovedApply(apply);
            } catch (Exception e) {
                System.err.println("处理审核通过后的业务逻辑失败: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("处理审核通过后的业务逻辑失败: " + e.getMessage());
            }
        }
        
        return true;
    }
    
    /**
     * 处理审核通过后的业务逻辑
     */
    private void processApprovedApply(BoothApply apply) {
        // 1. 检查并创建租户记录
        Tenant tenant = createOrGetTenant(apply.getUserId(), apply);
        
        // 2. 获取摊位信息
        Booth booth = boothMapper.selectById(apply.getBoothId());
        if (booth == null) {
            throw new RuntimeException("摊位不存在");
        }
        
        // 3. 创建合同
        Contract contract = createContract(tenant, booth, apply);
        
        // 4. 创建租赁记录
        createRental(apply.getUserId(), contract, booth);
        
        // 5. 生成支付记录
        createPaymentRecords(apply.getUserId(), contract);
        
        // 6. 更新摊位状态
        booth.setStatus(1); // 已租
        boothMapper.updateById(booth);
        
        System.out.println("申请审核通过后的业务处理完成");
    }
    
    /**
     * 创建或获取租户记录
     */
    private Tenant createOrGetTenant(Long userId, BoothApply apply) {
        // 检查是否已存在租户记录
        LambdaQueryWrapper<Tenant> tenantWrapper = new LambdaQueryWrapper<>();
        tenantWrapper.eq(Tenant::getUserId, userId);
        Tenant existingTenant = tenantMapper.selectOne(tenantWrapper);
        
        if (existingTenant != null) {
            return existingTenant;
        }
        
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 创建新的租户记录
        Tenant tenant = new Tenant();
        tenant.setUserId(userId);
        tenant.setBusinessName(apply.getBusinessDescription()); // 使用经营描述作为商户名称
        tenant.setBusinessType(apply.getBusinessType());
        tenant.setContactPerson(user.getRealName());
        tenant.setContactPhone(apply.getContactPhone());
        tenant.setBusinessLicense(apply.getIdCardImages()); // 将申请时上传的身份证件/营业执照传递过来
        tenant.setStatus(1); // 正常状态
        
        int result = tenantMapper.insert(tenant);
        if (result <= 0) {
            throw new RuntimeException("创建租户记录失败");
        }
        
        System.out.println("已为用户创建租户记录，租户ID: " + tenant.getId());
        return tenant;
    }
    
    /**
     * 创建合同
     */
    private Contract createContract(Tenant tenant, Booth booth, BoothApply apply) {
        Contract contract = new Contract();
        
        // 生成合同编号
        String contractNo = generateContractNo();
        contract.setContractNo(contractNo);
        
        contract.setTenantId(tenant.getId());
        contract.setBoothId(booth.getId());
        
        // 计算租赁日期
        LocalDate startDate = LocalDate.parse(apply.getStartDate());
        LocalDate endDate = startDate.plusMonths(apply.getLeaseMonths());
        contract.setStartDate(startDate);
        contract.setEndDate(endDate);
        
        contract.setMonthlyRent(booth.getPrice());
        contract.setDeposit(booth.getPrice().multiply(new BigDecimal("2"))); // 押金为2个月租金
        contract.setStatus(1); // 已审核
        
        int result = contractMapper.insert(contract);
        if (result <= 0) {
            throw new RuntimeException("创建合同失败");
        }
        
        System.out.println("已创建合同，合同编号: " + contractNo);
        return contract;
    }
    
    /**
     * 创建租赁记录
     */
    private void createRental(Long userId, Contract contract, Booth booth) {
        Rental rental = new Rental();
        rental.setUserId(userId);
        rental.setContractId(contract.getId());
        rental.setBoothId(booth.getId());
        rental.setStartDate(contract.getStartDate());
        rental.setEndDate(contract.getEndDate());
        rental.setStatus(1); // 租赁中
        
        int result = rentalMapper.insert(rental);
        if (result <= 0) {
            throw new RuntimeException("创建租赁记录失败");
        }
        
        System.out.println("已创建租赁记录，租赁ID: " + rental.getId());
    }
    
    /**
     * 创建支付记录
     */
    private void createPaymentRecords(Long userId, Contract contract) {
        // 创建押金支付记录
        Payment depositPayment = new Payment();
        depositPayment.setUserId(userId);
        depositPayment.setContractId(contract.getId());
        depositPayment.setPaymentNo(generatePaymentNo("DEPOSIT"));
        depositPayment.setPaymentType(2); // 押金
        depositPayment.setAmount(contract.getDeposit());
        depositPayment.setStatus(0); // 待支付
        paymentMapper.insert(depositPayment);
        
        // 创建首月租金支付记录
        Payment rentPayment = new Payment();
        rentPayment.setUserId(userId);
        rentPayment.setContractId(contract.getId());
        rentPayment.setPaymentNo(generatePaymentNo("RENT"));
        rentPayment.setPaymentType(1); // 租金
        rentPayment.setAmount(contract.getMonthlyRent());
        rentPayment.setStatus(0); // 待支付
        paymentMapper.insert(rentPayment);
        
        System.out.println("已创建押金和首月租金支付记录");
    }
    
    /**
     * 生成合同编号
     */
    private String generateContractNo() {
        return "HT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    /**
     * 生成支付单号
     */
    private String generatePaymentNo(String type) {
        return "PAY" + type + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
    
    @Override
    public Page<BoothApply> getAllApplyPage(int current, int size, Integer status) {
        Page<BoothApply> page = new Page<>(current, size);
        LambdaQueryWrapper<BoothApply> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(BoothApply::getStatus, status);
        }
        wrapper.orderByDesc(BoothApply::getApplyTime);
        return boothApplyMapper.selectPage(page, wrapper);
    }
    
    @Override
    public Page<BoothApplyVO> getAllApplyPageWithBooth(int current, int size, Integer status) {
        Page<BoothApplyVO> page = new Page<>(current, size);
        return boothApplyMapper.selectAllApplyPageWithBooth(page, status);
    }
    
    @Override
    public Page<BoothApplyVO> getApplyPageWithDetails(int current, int size, Integer status, String keyword) {
        Page<BoothApplyVO> page = new Page<>(current, size);
        return boothApplyMapper.selectApplyPageWithDetails(page, status, keyword);
    }
    
    @Override
    public BoothApplyVO getApplyDetailById(Long id) {
        return boothApplyMapper.selectApplyDetailById(id);
    }
    
    @Override
    public ApplyStatistics getApplyStatistics() {
        // 统计各状态的申请数量
        Long totalCount = boothApplyMapper.selectCount(null);
        
        LambdaQueryWrapper<BoothApply> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(BoothApply::getStatus, 0);
        Long pendingCount = boothApplyMapper.selectCount(wrapper);
        
        wrapper.clear();
        wrapper.eq(BoothApply::getStatus, 1);
        Long approvedCount = boothApplyMapper.selectCount(wrapper);
        
        wrapper.clear();
        wrapper.eq(BoothApply::getStatus, 2);
        Long rejectedCount = boothApplyMapper.selectCount(wrapper);
        
        wrapper.clear();
        wrapper.eq(BoothApply::getStatus, 3);
        Long cancelledCount = boothApplyMapper.selectCount(wrapper);
        
        return new ApplyStatistics(totalCount, pendingCount, approvedCount, rejectedCount, cancelledCount);
    }
}

