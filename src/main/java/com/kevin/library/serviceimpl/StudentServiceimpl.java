package com.kevin.library.serviceimpl;

import com.kevin.library.dao.StudentDao;
import com.kevin.library.pojo.Manager;
import com.kevin.library.pojo.Student;
import com.kevin.library.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StudentServiceimpl implements StudentService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private RedisTemplate redisTemplate;

    private Lock lock = new ReentrantLock();

    /**
     * 学生登录
     */
    @Override
    public Student login(String username) {
        return studentDao.login(username);
    }

    /**
     * 注册
     * @param student
     */
    @Override
    public void register(Student student) {
         studentDao.register(student);
    }

    /**
     * student个人信息
     * @param id
     * @return
     */
    @Override
    public Student personalInfo(int id) {

        Student student = (Student) redisTemplate.opsForValue().get("student:"+id);
        if(student == null){
            if(lock.tryLock()){
                try{
                    student = studentDao.personalInfo(id);
                    redisTemplate.opsForValue().set("student:"+id,student);
                    return student;
                }finally {
                    lock.unlock();
                }
            }else {
                student = (Student) redisTemplate.opsForValue().get("student:"+id);
                if(student == null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return personalInfo(id);
                }
                return student;
            }
        }
        return student;

    }

    /**
     * 更新student信息
     * @param student
     */
    @Transactional
    @Override
    public void updateInfo(Student student) {

        studentDao.updateInfo(student);
    }
}
