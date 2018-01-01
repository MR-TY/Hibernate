package com.ty.hibernat.helloworld;

import static org.junit.Assert.*;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;

public class HibernateTest {

	@Test
	public void test() {
		System.out.println("进来了");
		//1.创建sessionFactory对象
			SessionFactory sessionFactory = null;
			//<1:创建configration对象：对应hibernate的基本配置信息和对象映射的信息
			Configuration configuration = new Configuration().configure();
			//<2:创建一个serviceRegistry对象
			ServiceRegistry serviceRegistry =  new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			//<3:创建一个sessionFactory
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		
		//2.创建session对象
			Session session = sessionFactory.openSession();
		//3.开启事务
			Transaction transaction = session.beginTransaction();
		//4.执行保存的操作
			Date date = new Date();
			News person = new News("帅哥", "唐haha", date);
			session.save(person);
		//5.提交事务
		transaction.commit();
		//6.关闭session
		session.close();
		//7.关闭sessionFactory的对象
		sessionFactory.close();
	}

}
