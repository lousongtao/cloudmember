package com.shuishou.cloudmember.validatecheck.models;

import java.util.Date;
import java.util.List;

public interface ILicenseValidateHistoryDataAccessor {
    void save(CustomerLicenseValidateHistory history);

    List<CustomerLicenseValidateHistory> queryLicenseValidateHistory(String customerName, Date startTime, Date endTime);
}
