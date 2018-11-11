package com.shuishou.cloudmember.validatecheck.models;

import com.shuishou.cloudmember.models.BaseDataAccessor;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class LicenseValidateHistoryDataAccessor extends BaseDataAccessor implements ILicenseValidateHistoryDataAccessor {
    @Override
    public void save(CustomerLicenseValidateHistory history) {
        sessionFactory.getCurrentSession().save(history);
    }

    @Override
    public List<CustomerLicenseValidateHistory> queryLicenseValidateHistory(String customerName, Date startTime, Date endTime) {
        String listStmt = "select l from CustomerLicenseValidateHistory l";
        List<String> condList = new ArrayList<>();
        if (customerName != null && customerName.length() > 0) {
            condList.add("l.customerName='" + customerName + "'");
        }
        if (startTime != null) {
            condList.add("l.validateDate >= :startTime");
        }
        if (endTime != null) {
            condList.add("l.validateDate <= :endTime");
        }
        for (int i = 0; i < condList.size(); i++) {
            listStmt += (i == 0 ? " where " : " and ") + condList.get(i);
        }

        Query listQuery = getSession().createQuery(listStmt);

        if (startTime != null) {
            listQuery.setTimestamp("startTime", startTime);
        }
        if (endTime != null) {
            listQuery.setTimestamp("endTime", endTime);
        }

        @SuppressWarnings("unchecked")
        List<CustomerLicenseValidateHistory> histories = listQuery.list();
        return histories;
    }
}
