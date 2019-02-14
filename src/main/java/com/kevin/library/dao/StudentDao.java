package com.kevin.library.dao;

import com.kevin.library.pojo.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface StudentDao {

    /**
     * 学生登录
     */
    Student login(String username);

    /**
     * 注册
     */
    int register(@Param("student")Student student);

    /**
     * 根据id查询student信息
     * @param id
     * @return
     */
    @Select("select * from student_info where id = #{id}")
    Student personalInfo(int id);

    /**
     * 更新student信息
     * @param student
     */
    void updateInfo(@Param("student") Student student);
}
