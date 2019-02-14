package com.kevin.library.service;

import com.kevin.library.pojo.Student;

public interface StudentService {

    /**
     * 学生登录
     * @param username
     * @return
     */
    Student login(String username);

    /**
     * 注册
     * @param student
     */
    void register(Student student);

    /**
     * 学生个人信息
     * @param id
     * @return
     */
    Student personalInfo(int id);

    /**
     * 修改信息
     * @param student
     */
    void updateInfo(Student student);
}
