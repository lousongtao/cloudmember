package com.shuishou.cloudmember.member.models;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.shuishou.cloudmember.models.BaseDataAccessor;

@Repository
public class MemberDataAccessor extends BaseDataAccessor implements IMemberDataAccessor {

	@Override
	public Member getMemberById(String customerName, int id) {
		String sql = "select * from Member_"+customerName+" where id = "+id;
		return (Member) sessionFactory.getCurrentSession().createQuery(sql).uniqueResult();
	}
	
	@Override
	public Member getMemberByCard(String customerName, String card) {
		String hql = "from Member where memberCard = '"+card+"'";
		return (Member) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<Member> queryMember(String customerName, String name, String memberCard, String address,
			String postCode, String telephone) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Member.class);
		if (name != null && name.length() > 0)
			c.add(Restrictions.ilike("name", "%" + name + "%"));
		if (memberCard != null && memberCard.length() > 0)
			c.add(Restrictions.ilike("memberCard", "%" + memberCard + "%"));
		if (address != null && address.length() > 0)
			c.add(Restrictions.ilike("address", "%" + address + "%"));
		if (postCode != null && postCode.length() > 0)
			c.add(Restrictions.like("postCode", postCode));
		if (telephone != null && telephone.length() > 0)
			c.add(Restrictions.ilike("telephone", "%" + telephone + "%"));
		c.addOrder(Order.asc("id"));
		return (List<Member>)c.list();
	}
	
	@Override
	public List<Member> queryAllMember(String customerName) {
		String hql = "from Member";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public int queryMemberCount(String customerName, String name, String memberCard, String address, String postCode,
			String telephone) {
		String countStmt = "select count(l) from Member l";
		List<String> condList = Lists.newArrayList();
		if (name != null && name.length() > 0)
			condList.add("l.name like '%" + name +"%'");
		if (memberCard != null && memberCard.length() > 0)
			condList.add("l.memberCard like '%" + memberCard +"%'");
		if (address != null && address.length() > 0)
			condList.add("l.address like '%" + address +"%'");
		if (postCode != null && postCode.length() > 0)
			condList.add("l.postCode like '%" + postCode +"%'");
		if (telephone != null && telephone.length() > 0)
			condList.add("l.telephone like '%" + telephone +"%'");
		for (int i = 0; i < condList.size(); i++) {
			if (i == 0)
				countStmt += " where ";
			else countStmt += " and ";
			countStmt += condList.get(i);
		}
		Query query = sessionFactory.getCurrentSession().createQuery(countStmt);
		return (int)(long)query.uniqueResult();
	}

	@Override
	public void save(String customerName, Member m) {
		sessionFactory.getCurrentSession().save(m);
	}

	@Override
	public void delete(String customerName, Member m) {
		sessionFactory.getCurrentSession().delete(m);
	}

	@Override
	public void createMemberTableByCustomer(String customerName){
		String stmt_member = "CREATE TABLE `member_"+customerName+"` (`id` int(11) NOT NULL AUTO_INCREMENT,`address` varchar(255) DEFAULT NULL, "
				+ "`balanceMoney` double DEFAULT '0',`birth` datetime DEFAULT NULL,`createTime` datetime NOT NULL,`discountRate` double DEFAULT '1', "
				+ "`memberCard` varchar(255) NOT NULL,`name` varchar(255) NOT NULL,`postCode` varchar(255) DEFAULT NULL,`score` double DEFAULT '0', "
				+ "`telephone` varchar(255) DEFAULT NULL, "
				+ "PRIMARY KEY (`id`), "
				+ "UNIQUE KEY `UK_membercard_"+ customerName + "` (`memberCard`) "
				+ "KEY `IDX_membername_"+ customerName + "` (`name`) "
				+ ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;";
		String stmt_member_consum = "CREATE TABLE `member_consumption_"+customerName+"` (`id` int(11) NOT NULL AUTO_INCREMENT,`amount` double NOT NULL,"
				+ "`date` datetime NOT NULL,`place` varchar(255) NOT NULL,`type` int(11) NOT NULL,`member_id` int(11) DEFAULT NULL,"
				+ "PRIMARY KEY (`id`),"
				+ "KEY `FK_consum_memberid_" + customerName + "` (`member_id`),"
				+ "CONSTRAINT `FK_consum_memberid_"+customerName+"` FOREIGN KEY (`member_id`) REFERENCES `member_"+customerName+"` (`id`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		String stmt_member_score = "CREATE TABLE `member_score_"+customerName+"` (`id` int(11) NOT NULL AUTO_INCREMENT,`amount` double NOT NULL,"
				+ "`date` datetime NOT NULL,`place` varchar(255) NOT NULL,`type` int(11) NOT NULL,`member_id` int(11) DEFAULT NULL,"
				+ "PRIMARY KEY (`id`),"
				+ "KEY `FK_score_memberid_"+customerName+"` (`member_id`),"
				+ "CONSTRAINT `FK_score_memberid_"+customerName+"` FOREIGN KEY (`member_id`) REFERENCES `member_"+customerName+"` (`id`)"
				+ ") ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;";
		sessionFactory.getCurrentSession().createSQLQuery(stmt_member).executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery(stmt_member_consum).executeUpdate();
		sessionFactory.getCurrentSession().createSQLQuery(stmt_member_score).executeUpdate();
	}

}
