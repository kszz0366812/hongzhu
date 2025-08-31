package com.insightflow.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.insightflow.entity.employee.EmployeeInfo;
import com.insightflow.service.employee.EmployeeInfoService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * 员工Excel导入导出DTO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EmployeeExcelDTO extends ExcelDTO {

    @ExcelProperty(value = "员工姓名", index = 0)
    private String name;

    @ExcelProperty(value = "员工工号", index = 1)
    private String employeeCode;

    @ExcelProperty(value = "所属大区", index = 2)
    private String regionLevel1;

    @ExcelProperty(value = "所属地市", index = 3)
    private String regionLevel2;

    @ExcelProperty(value = "所属区域", index = 4)
    private String regionLevel3;

    @ExcelProperty(value = "负责区域", index = 5)
    private String responsibleRegions;

    @ExcelProperty(value = "直接上级", index = 6)
    private String directLeader;

    @ExcelProperty(value = "岗位", index = 7)
    private String position;

    @ExcelProperty(value = "职级", index = 8)
    private String levels;

    @ExcelProperty(value = "渠道", index = 9)
    private String channel;

    @ExcelProperty(value = "入职时间", index = 10)
    private String joinDate;

    @ExcelProperty(value = "状态", index = 11)
    private String status;

    @Override
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() && 
               employeeCode != null && !employeeCode.trim().isEmpty();
    }
    
    // 添加无参构造函数，EasyExcel实例化对象需要
    public EmployeeExcelDTO() {
    }
    
	public EmployeeExcelDTO(Map<String, Object> map) {
        BeanUtils.copyProperties(map, this);
        if(map.get("status") != null){
            this.status = map.get("status").toString().equals("1")?"离职":"在职";
        }
	}
    @Override
    public Object toEntity() {
        EmployeeInfo employee = new EmployeeInfo();
        
        // 使用BeanUtils复制相同字段
        BeanUtils.copyProperties(this, employee);
        
        // 特殊字段处理
        if ("在职".equals(this.status) || "0".equals(this.status)) {
            employee.setStatus(0);
        } else if ("离职".equals(this.status) || "1".equals(this.status)) {
            employee.setStatus(1);
        } else {
            employee.setStatus(0); // 默认在职
        }
        
        // 处理日期转换
        if (this.joinDate != null && !this.joinDate.trim().isEmpty()) {
            try {
                // 尝试解析常见的日期格式
                if (this.joinDate.contains("-")) {
                    // yyyy-MM-dd 格式
                    employee.setJoinDate(java.time.LocalDate.parse(this.joinDate));
                } else if (this.joinDate.contains("/")) {
                    // yyyy/MM/dd 格式
                    String[] parts = this.joinDate.split("/");
                    if (parts.length == 3) {
                        int year = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        int day = Integer.parseInt(parts[2]);
                        employee.setJoinDate(java.time.LocalDate.of(year, month, day));
                    }
                }
            } catch (Exception e) {
                // 如果日期解析失败，记录警告但不中断导入
                System.out.println("警告：无法解析入职时间: " + this.joinDate + "，将设置为null");
                employee.setJoinDate(null);
            }
        }
        
        return employee;
    }
    
    @Override
    public Class<?> getServiceClass() {
        return EmployeeInfoService.class;
    }
} 