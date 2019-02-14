package com.kevin.library.dao;

import com.kevin.library.pojo.Manager;
import com.kevin.library.pojo.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ManagerDao {

    /**
     * 管理员登录
     * @param managerName
     * @return
     */
    Manager login(String managerName);

    /**
     * 注册
     */
    int register(@Param("manager") Manager manager);

    /**
     * 根据id查找学生信息
     * @param id
     * @return
     */
    Student selectStuById(int id);

    /**
     * 更新学生信息
     * @param student
     */
    void updateStuInfo(@Param("student") Student student);

    /**
     * 根据id删除学生信息
     * @param id
     */
    void deleteStuInfo(int id);

    /**
     * 添加学生信息
     * @param student
     */
    void insertStudent(@Param("student") Student student);

    /**
     * 根据id查找管理员
     * @param id
     * @return
     */
    Manager selectManById(int id);

    /**
     * 多条件查询学生信息
     * @param student
     * @return
     */
    List<Student> selectStuInfo(@Param("student") Student student);

    /**
     * 修改密码
     * @param manager
     */
    void updateManPerInfo(@Param("manager") Manager manager);

    /**
     * 分页查询全体student
     * @return
     */
    @Select("select * from student_info")
    List<Student> selectAllStudent();
}
