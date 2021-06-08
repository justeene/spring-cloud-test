package com.xzg.test.dao;

import com.xzg.test.entity.User;
import org.springframework.data.repository.CrudRepository;

/**
 * @author XieZG
 * @Description:
 * @date 21-6-8下午3:55
 */
public interface UserDao extends CrudRepository<User, Long> {
}
