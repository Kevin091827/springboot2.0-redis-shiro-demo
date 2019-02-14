package com.kevin.library.service;

import com.kevin.library.pojo.Manager;
import com.kevin.library.pojo.Student;

import java.util.List;
import java.util.Map;

public interface ManagerService {

    /**
     * 管理员登录
     * @param managerName
     * @return
     */
    Manager login(String managerName);

    /**
     * 注册
     */
    void register(Manager manager);

    /**
     * 根据id查找学生信息
     * @param id
     * @return
     */
    Student selectStuById(int id);

    /**
     * 更新学生信息
     */
    Student updateStuInfo(Student student);

    /**
     * 根据id删除学生信息
     */
    void deleteStuInfo(int id);

    /**
     * 添加学生信息
     * @param student
     */
    void insertStudent(Student student);

    /**
     * 根据id查找管理员
     * @param id
     * @return
     */
    Manager selectManById(int id);

    /**
     * 查找学生信息
     * @param student
     * @return
     */
    List<Student> selectStuInfo(Student student);

    /**
     * 修改密码
     * @param manager
     */
    void updateManPerInfo(Manager manager);

    /**
     * 分页数据
     * @param selectPage
     * @param pageSize
     * @return
     */
    List<Student> selectAllStudent(int selectPage,int pageSize);
}
