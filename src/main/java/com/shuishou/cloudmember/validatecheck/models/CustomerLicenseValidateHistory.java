package com.shuishou.cloudmember.validatecheck.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuishou.cloudmember.ConstantValue;

import javax.persistence.*;
import java.util.Date;

/**
 * 校验用户权限到期时间
 * @author Administrator
 *
 */
@Entity
@Table(name="customer_license_validate_history")
public class CustomerLicenseValidateHistory {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(nullable=false)
	private String customerName;

    @Column(nullable=false)
    private String customerKey;

    /**
     * the time of validation
     */
    @Column
	@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone = "GMT+8:00")
	private Date validateDate;

    /**
     * 1 : success
     * 2 : the key does not match the customer name
     * 3 : other error
     */
	@Column
	private int status;

	@Column(nullable = false)
    private String ip;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerKey() {
		return customerKey;
	}

	public void setCustomerKey(String customerKey) {
		this.customerKey = customerKey;
	}

    public Date getValidateDate() {
        return validateDate;
    }

    public void setValidateDate(Date validateDate) {
        this.validateDate = validateDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
	public String toString() {
		return "Customer License Validate History [id=" + id + ", customerName=" + customerName + ", Time=" + ConstantValue.DFYMDHMS.format(validateDate) + ", IP=" + ip +", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerLicenseValidateHistory other = (CustomerLicenseValidateHistory) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
