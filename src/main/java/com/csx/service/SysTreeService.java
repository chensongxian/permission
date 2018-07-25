package com.csx.service;

import com.csx.dao.SysAclMapper;
import com.csx.dao.SysAclModuleMapper;
import com.csx.dao.SysDeptMapper;
import com.csx.dto.AclDto;
import com.csx.dto.AclModuleLevelDto;
import com.csx.dto.DeptLevelDto;
import com.csx.model.SysAcl;
import com.csx.model.SysAclModule;
import com.csx.model.SysDept;
import com.csx.util.LevelUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @author: csx
 * @Date: 2018-07-23
 */
@Service
public class SysTreeService{
    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysCoreService sysCoreService;
    @Resource
    private SysAclMapper sysAclMapper;

    public List<AclModuleLevelDto> userAclTree(int userId) {
        List<SysAcl> userAclList = sysCoreService.getUserAclList(userId);
        List<AclDto> aclDtoList = Lists.newArrayList();
        for (SysAcl acl : userAclList) {
            AclDto dto = AclDto.adapt(acl);
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> roleTree(int roleId) {
        // 1、当前用户已分配的权限点
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        // 2、当前角色分配的权限点
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        // 3、当前系统所有权限点
        List<AclDto> aclDtoList = Lists.newArrayList();

        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList = sysAclMapper.getAll();
        for (SysAcl acl : allAclList) {
            AclDto dto = AclDto.adapt(acl);
            if (userAclIdSet.contains(acl.getId())) {
                dto.setHasAcl(true);
            }
            if (roleAclIdSet.contains(acl.getId())) {
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList) {
        if (CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelList = aclModuleTree();

        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        for(AclDto acl : aclDtoList) {
            if (acl.getStatus() == 1) {
                moduleIdAclMap.put(acl.getAclModuleId(), acl);
            }
        }
        bindAclsWithOrder(aclModuleLevelList, moduleIdAclMap);
        return aclModuleLevelList;
    }

    public void bindAclsWithOrder(List<AclModuleLevelDto> aclModuleLevelList, Multimap<Integer, AclDto> moduleIdAclMap) {
        if (CollectionUtils.isEmpty(aclModuleLevelList)) {
            return;
        }
        for (AclModuleLevelDto dto : aclModuleLevelList) {
            List<AclDto> aclDtoList = (List<AclDto>)moduleIdAclMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtoList)) {
                Collections.sort(aclDtoList, aclSeqComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclsWithOrder(dto.getAclModuleList(), moduleIdAclMap);
        }
    }



    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        for (SysAclModule aclModule : aclModuleList) {
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclModuleListToTree(List<AclModuleLevelDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Lists.newArrayList();
        }
        // level -> [aclmodule1, aclmodule2, ...] Map<String, List<Object>>
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        for (AclModuleLevelDto dto : dtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        Collections.sort(rootList, aclModuleSeqComparator);
        transformAclModuleTree(rootList, LevelUtil.ROOT, levelAclModuleMap);
        return rootList;
    }

    public void transformAclModuleTree(List<AclModuleLevelDto> dtoList, String level, Multimap<String, AclModuleLevelDto> levelAclModuleMap) {
        for (int i = 0; i < dtoList.size(); i++) {
            AclModuleLevelDto dto = dtoList.get(i);
            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempList)) {
                Collections.sort(tempList, aclModuleSeqComparator);
                dto.setAclModuleList(tempList);
                transformAclModuleTree(tempList, nextLevel, levelAclModuleMap);
            }
        }
    }


    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList = sysDeptMapper.getAllDept();

        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }

    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelDtoList){
        if(CollectionUtils.isEmpty(deptLevelDtoList)){
            return Lists.newArrayList();
        }
        Multimap<String,DeptLevelDto> levelDtoMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();
        for(DeptLevelDto dto : deptLevelDtoList){
            levelDtoMap.put(dto.getLevel(),dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        // 按照seq从小到大排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq()-o2.getSeq();
            }
        });
        transformDeptTree(deptLevelDtoList,LevelUtil.ROOT,levelDtoMap);
        return rootList;
    }

    public void transformDeptTree(List<DeptLevelDto> deptLevelList,String level,Multimap<String,DeptLevelDto> levelDtoMultimap){
        for(int i=0;i<deptLevelList.size();i++){
            // 遍历该层的每个元素
            DeptLevelDto deptLevelDto = deptLevelList.get(i);
            // 处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level,deptLevelDto.getId());
            // 处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDtoMultimap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)){
                // 排序
                Collections.sort(tempDeptList,deptLevelDtoComparator);
                // 设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                // 进入下一层处理
                transformDeptTree(tempDeptList,nextLevel,levelDtoMultimap);
            }
        }
    }

    public Comparator<DeptLevelDto> deptLevelDtoComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    };

    public Comparator<AclModuleLevelDto> aclModuleSeqComparator = new Comparator<AclModuleLevelDto>() {
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    public Comparator<AclDto> aclSeqComparator = new Comparator<AclDto>() {
        public int compare(AclDto o1, AclDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };
}
