package com.csx.dao;

import com.csx.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    /**
     * 根据注解删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(@Param("id") Integer id);

    /**
     * 插入数据
     * @param record
     * @return
     */
    int insert(SysDept record);

    /**
     * 插入不为空的数据
     * @param record
     * @return
     */
    int insertSelective(SysDept record);

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    SysDept selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 根据主键选择更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(SysDept record);

    /**
     * 根据主键更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(SysDept record);

    /**
     * 获取所有部门信息
     * @return
     */
    List<SysDept> getAllDept();

    /**
     * 根据层级获取子部门
     * @param level
     * @return
     */
    List<SysDept> getChildDeptListByLevel(String level);

    /**
     * 批量更新部门
     * @param deptList
     */
    void batchUpdateLevel(@Param("deptList") List<SysDept> deptList);

    /**
     * 查询同一层级下是否有重复的，返回重复个数
     * @param parentId
     * @param name
     * @param id
     * @return
     */
    int countByNameAndParentId(@Param("parentId") Integer parentId, @Param("name") String name, @Param("id") Integer id);

    /**
     * 查询父类下是否有子类
     * @param deptId
     * @return
     */
    int countByParentId(@Param("deptId") Integer deptId);
}