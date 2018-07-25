package com.csx.dto;

import com.csx.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @author: csx
 * @Date: 2018-07-25
 */
@Getter
@Setter
@ToString
public class AclDto extends SysAcl {
    // 是否要默认选中
    private boolean checked = false;

    // 是否有权限操作
    private boolean hasAcl = false;

    public static AclDto adapt(SysAcl acl) {
        AclDto dto = new AclDto();
        BeanUtils.copyProperties(acl, dto);
        return dto;
    }
}
