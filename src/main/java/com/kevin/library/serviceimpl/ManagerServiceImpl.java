package com.kevin.library.serviceimpl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kevin.library.dao.ManagerDao;
import com.kevin.library.pojo.Manager;
import com.kevin.library.pojo.Student;
import com.kevin.library.service.ManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ManagerServiceImpl implements ManagerService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ManagerDao managerDao;

    @Autowired
    private RedisTemplate redisTemplate;

    private static Lock reenLock = new ReentrantLock();

    /**
     * 管理员登录
     */
    @Override
    public Manager login(String managerName) {

        return managerDao.login(managerName);
    }

    /**
     * 注册
     */
    @Transactional
    @Override
    public void register(Manager manager) {

        managerDao.register(manager);
    }

    /**
     * 分页数据
     * @param selectPage
     * @param pageSize
     * @return
     */
    @Override
    public List<Student> selectAllStudent(int selectPage,int pageSize) {

        Page<Object> page = PageHelper.startPage(selectPage,pageSize);
        return managerDao.selectAllStudent();
    }

    /**
     * 根据id查找学生信息
     * @param id
     * @return
     */

    @Override
    public Student selectStuById(int id) {

        Student student = (Student) redisTemplate.opsForValue().get("student:"+id);
        if (student == null) {
            if(reenLock.tryLock()){
                try{
                    student = managerDao.selectStuById(id);
                    redisTemplate.opsForValue().set("student:"+student.getId(),student);
                    return student;
                }finally {
                    reenLock.unlock();
                }
            }else{
                student = (Student) redisTemplate.opsForValue().get("student:"+id);
                if(student == null){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return selectStuById(id);
                }
                return student;
            }
        }else {
            return student;
        }
    }

    /**
     * 更新学生信息
     * @param student
     * @return
     */
    @Transactional
    @Override
    public Student updateStuInfo(Student student) {

        redisTemplate.delete("student:"+student.getId());
        managerDao.updateStuInfo(student);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        redisTemplate.delete("student:"+student.getId());
        redisTemplate.opsForValue().set("student:"+student.getId(),student);
        return student;
    }

    /**
     * 根据id删除学生信息
     * @param id
     */
    @Transactional
    @Override
    public void deleteStuInfo(int id) {

         if(redisTemplate.hasKey("student:"+id)) {
             redisTemplate.delete("student:" + id);
             managerDao.deleteStuInfo(id);
         }
    }

    /**
     * 添加学生信息
     * @param student
     */
    @Transactional
    @Override
    public void insertStudent(Student student) {

        if (!redisTemplate.hasKey("student:"+student.getId())){
            try {
                managerDao.insertStudent(student);
                redisTemplate.opsForValue().set("student:"+student.getId(),student);
            }catch (Exception ee){
                logger.info(ee.getMessage());
            }
        }
    }

    /**
     * 根据id查找管理员
     * @param id
     * @return
     */
    @Override
    public Manager selectManById(int id) {

        Manager manager = (Manager) redisTemplate.opsForValue().get("manager:"+id);
        if (manager == null) {
            if(reenLock.tryLock()){
                try{
                    manager = managerDao.selectManById(id);
                    redisTemplate.opsForValue().set("manager:"+id,manager);
                    return manager;
                }finally {
                  reenLock.unlock();
                }
            }else {
                manager = (Manager) redisTemplate.opsForValue().get("manager:"+id);
                if( manager == null ){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return selectManById(id);
                }
                return manager;
            }
        }else {
            return manager;
        }
    }

    /**
     * 查询学生信息
     * @param student
     * @return
     */
    @Override
    public List<Student> selectStuInfo(Student student) {
       return managerDao.selectStuInfo(student);
    }

    /**
     * 修改密码
     * @param manager
     */
    @Transactional
    @Override
    public void updateManPerInfo(Manager manager) {

        redisTemplate.delete("manager:"+manager.getId());
        managerDao.updateManPerInfo(manager);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        redisTemplate.delete("manager:"+manager.getId());
        redisTemplate.opsForValue().set("manager:"+manager.getId(),manager);
    }

}
